package com.gguproject.jarvis.plugin.google.speech.service;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.google.api.client.util.PemReader;
import com.google.api.client.util.SecurityUtils;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.function.Consumer;

public class GoogleSpeechService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GoogleSpeechService.class);
	
	private SpeechClient speechClient;
	
	private String credentialFilePath;

	private int listeningTime;
	
	private Consumer<String> onSpeech;
	private Runnable onStart;
	private Runnable onComplete;
	
	private String language;
	
	/**
	 * Callback used by the google service to handle response asynchronously
	 */
	private ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
		ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

		public void onStart(StreamController controller) {
		}

		public void onResponse(StreamingRecognizeResponse response) {
			responses.add(response);
		}

		public void onComplete() {
			for (StreamingRecognizeResponse response : responses) {
				StreamingRecognitionResult result = response.getResultsList().get(0);
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				LOGGER.debug("Found speech(confidence: {}): {}", alternative.getConfidence(), alternative.getTranscript());
				onSpeech.accept(alternative.getTranscript());
			}
			responses.clear();
		}

		public void onError(Throwable t) {
			LOGGER.error("Error while receiving google service response", t);
		}
	};
	
	/**
	 * Initialize the google service and microphone
	 * @throws Exception  
	 */
	private void initializeGoogleService() {
		try {
			SpeechSettings settings = this.buildSettings();
			this.speechClient = SpeechClient.create(settings);
		} catch (IOException e) {
			throw new IllegalStateException("Not able to initialize the google speech service", e);
		}
	}
	
	/**
	 * Start listening 
	 * @throws IOException
	 */
	public void listen() {
		LOGGER.debug("Start listening for {} ms", this.listeningTime);
		
		try {
			var clientStream = this.initClientStream();
			
			long startTime = System.currentTimeMillis();
	
			// Audio Input Stream
			TargetDataLineProvider.start();
			AudioInputStream audio = TargetDataLineProvider.getStream();//new AudioInputStream(targetDataLine);
	
			this.onStart.run();
			
			while (true) {
				long estimatedTime = System.currentTimeMillis() - startTime;
				byte[] data = new byte[6400];
				audio.read(data);
	
				if (estimatedTime > this.listeningTime) {
					LOGGER.debug("End listening");
					TargetDataLineProvider.stop();
					clientStream.closeSend();
					break;
				}
	
				var request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
				clientStream.send(request);
			}
			
			this.onComplete.run();
			
			this.responseObserver.onComplete();
		} catch(IOException e) {
			throw new IllegalArgumentException("Error while listening", e);
		}
	}

	static PrivateKey privateKeyFromPkcs8(String privateKeyPkcs8) throws IOException {
		Reader reader = new StringReader(privateKeyPkcs8);
		PemReader.Section section = PemReader.readFirstSectionAndClose(reader, "PRIVATE KEY");
		if (section == null) {
			throw new IOException("Invalid PKCS#8 data.");
		} else {
			byte[] bytes = section.getBase64DecodedBytes();
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);

			try {
				KeyFactory keyFactory = SecurityUtils.getRsaKeyFactory();
				return keyFactory.generatePrivate(keySpec);
			} catch (InvalidKeySpecException | NoSuchAlgorithmException var7) {
				throw new IOException("Unexpected exception reading PKCS#8 data", var7);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private SpeechSettings buildSettings() throws FileNotFoundException, IOException {
		CredentialsProvider credentialsProvider = FixedCredentialsProvider
				.create(ServiceAccountCredentials.fromStream(new FileInputStream(this.credentialFilePath)));
		return SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
	}
	
	/**
	 *
	 * @return
	 */
	private ClientStream<StreamingRecognizeRequest> initClientStream(){
		ClientStream<StreamingRecognizeRequest> clientStream = this.speechClient.streamingRecognizeCallable().splitCall(responseObserver);

		RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
				.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
				.setLanguageCode(language)
				.setSampleRateHertz(16000)
				.build();
		StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
				.setConfig(recognitionConfig).build();

		StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
				.setStreamingConfig(streamingRecognitionConfig).build(); // The first request in a streaming call has to be a config

		clientStream.send(request);
		
		return clientStream;
	}
	
	/**
	 * 
	 * @throws LineUnavailableException
	 */
	private TargetDataLine buildMicrophoneListener() throws LineUnavailableException {
		AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);

		// Set the system information to read from the microphone audio stream
		//DataLine.Info targetInfo = new Info(TargetDataLine.class, audioFormat);

		//if (!AudioSystem.isLineSupported(targetInfo)) {
		//	throw new IllegalStateException("Microphone is not supported");
		//}

		TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
		targetDataLine.open();

		// Target data line captures the audio stream the microphone produces.
		//TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
		//targetDataLine.open(audioFormat);
		targetDataLine.start();
		
		return targetDataLine; 
	}
	
	public static class GoogleSpeechServiceBuilder {
		
		private GoogleSpeechService service;
		
		public static GoogleSpeechServiceBuilder get() {
			return new GoogleSpeechServiceBuilder();
		}
		
		private GoogleSpeechServiceBuilder() {
			this.service = new GoogleSpeechService();
		}
		
		public GoogleSpeechServiceBuilder onSpeech(Consumer<String> onSpeech) {
			this.service.onSpeech = onSpeech;
			return this;
		}
		
		public GoogleSpeechServiceBuilder onStart(Runnable onStart) {
			this.service.onStart = onStart;
			return this;
		}
		
		public GoogleSpeechServiceBuilder onComplete(Runnable onComplete) {
			this.service.onComplete = onComplete;
			return this;
		}
		
		public GoogleSpeechServiceBuilder credentialFilePath(String credentialFilePath) {
			this.service.credentialFilePath = credentialFilePath;
			return this;
		}
		
		public GoogleSpeechServiceBuilder listeningTime(int listeningTime) {
			this.service.listeningTime = listeningTime;
			return this;
		}
		
		public GoogleSpeechServiceBuilder language(String language) {
			this.service.language = language;
			return this;
		}
		
		public GoogleSpeechService build() {
			this.service.initializeGoogleService();
			return this.service;
		}
	}
}

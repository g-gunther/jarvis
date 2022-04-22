package com.gguproject.jarvis.plugin.speaker.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.helper.google.counter.GoogleCounterException;
import com.gguproject.jarvis.helper.sound.SoundPlayerException;
import com.gguproject.jarvis.helper.sound.SoundPlayerService;
import com.gguproject.jarvis.plugin.speaker.SpeakerPluginConfiguration;
import com.gguproject.jarvis.plugin.speaker.SpeakerPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speaker.service.GoogleSpeakerCounterService.CounterName;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import javax.inject.Named;
import java.io.*;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Named
public class SpeakerService implements OnPostConstruct {
	/** Log */
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeakerService.class);

	private final SpeakerPluginConfiguration configuration;

	private final SoundPlayerService soundPlayerService;

	private final GoogleSpeakerCounterService counterService;

	/** list of properties containing sentences depending on the locale */
	private Map<Locale, Properties> sentences = new ConcurrentHashMap<>();

	/** Save the current locale value */
	private volatile Locale currentLocale;

	private String credentialFilePath;

	public SpeakerService(SpeakerPluginConfiguration configuration, SoundPlayerService soundPlayerService, GoogleSpeakerCounterService counterService){
		this.configuration = configuration;
		this.soundPlayerService = soundPlayerService;
		this.counterService = counterService;
	}

	@Override
	public void postConstruct() {
		this.loadProperties(new Locale(this.configuration.getProperty(PropertyKey.propertiesLanguage)));
		this.credentialFilePath = this.configuration
				.getSecretDataFilePath(this.configuration.getProperty(PropertyKey.googleCredentialPath));
	}

	/**
	 * Retrieve the text by its code from property files
	 *
	 * @param code
	 */
	public void speakByCode(String code) {
		String text = this.getSentence(code);
		this.speak(text);
	}

	/**
	 * Speak a given text
	 * 
	 * @param text
	 */
	public void speak(String text) {
		int nbWords = text.split(" ").length;
		try {
			counterService.check(CounterName.SPEAKER_REQUEST, nbWords);
		} catch (GoogleCounterException e) {
			LOGGER.warn("Can't call google-speaker service");
			return;
		}

		File outputFile = null;
		
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(this.buildSettings())) {
			SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
					.setLanguageCode(this.configuration.getProperty(PropertyKey.googleLanguage))
					.setName(this.configuration.getProperty(PropertyKey.googleVoiceName)).build();

			AudioConfig audioConfig = AudioConfig.newBuilder().setPitch(0).setSpeakingRate(1)
					.setAudioEncoding(AudioEncoding.LINEAR16).build();

			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

			ByteString audioContents = response.getAudioContent();

			// create a temporary file which will be deleted after
			outputFile = File.createTempFile("speech", ".mp3");
			try (OutputStream out = new FileOutputStream(outputFile)){
				out.write(audioContents.toByteArray());
			}
			LOGGER.debug("Speaker sound file saved: {}", outputFile);
			this.soundPlayerService.playOnce(outputFile);
			
		} catch (IOException | SoundPlayerException e) {
			LOGGER.error("An error occurs while generating & playing the speech sound file", e);
		} finally {
			if(outputFile != null) {
				outputFile.delete();
			}
			
			try {
		        this.counterService.increment(CounterName.SPEAKER_REQUEST, nbWords);
	        } catch(GoogleCounterException e1) {
	        	LOGGER.error("An error occurs while incrementing SPEAKER_REQUEST counter", e1);
	        }
		}
	}

	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private TextToSpeechSettings buildSettings() throws FileNotFoundException, IOException {
		CredentialsProvider credentialsProvider = FixedCredentialsProvider
				.create(ServiceAccountCredentials.fromStream(new FileInputStream(this.credentialFilePath)));
		return TextToSpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
	}

	/**
	 * Load the speaker file depending on the locale This file contains all
	 * sentences to play
	 * 
	 * @param locale
	 */
	private void loadProperties(Locale locale) {
		if (!this.sentences.containsKey(locale)) {
			LOGGER.debug("Load sentence file for locale {}", locale);
			try {
				File file = new File(getClass().getClassLoader()
						.getResource("speaker/speaker-" + locale.getLanguage() + ".properties").getFile());
				InputStream input = new FileInputStream(file);
				Properties prop = new Properties();
				prop.load(input);
				this.sentences.put(locale, prop);
			} catch (IOException e) {
				LOGGER.error("Not able to load the speacker file for locale {}", locale.getLanguage());
			}
		}
	}

	/**
	 * Return the sentence if it exists in the property files else it returns the
	 * text directly
	 *
	 * @param text Label of text to speech
	 * @return Text to process
	 */
	private String getSentence(String text) {
		String sentence = this.sentences.get(this.currentLocale).getProperty(text);
		if (sentence == null) {
			return text;
		}
		return sentence;
	}
}

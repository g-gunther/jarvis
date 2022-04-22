package com.gguproject.jarvis.plugin.deepspeech.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessHandle.Info;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;

@Named
public class DeepspeechService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(DeepspeechService.class);
	
	private Process deepspeechProcess;
	
	/**
	 * check if a deepspeech process is already running on the server
	 * if found, destroy them
	 */
	public void cleanupExistingProcesses() {
		ProcessHandle.allProcesses().forEach(process -> {
			Info info = process.info();
			if(info.command().isPresent() && info.command().get().endsWith("/Python")) {
				if(info.arguments().isPresent() && info.arguments().get().length > 0) {
					String pythonFileName = process.info().arguments().get()[0];
					if(pythonFileName.endsWith("mic_vad_streaming.py")) {
						LOGGER.debug("Destroy process: {}", Arrays.asList(process.info().arguments().get()).stream().collect(Collectors.joining(" ")));
						process.destroyForcibly();
					}
				}
			}
		});
	}
	
	public void interrupt() {
		if(this.deepspeechProcess != null) {
			this.deepspeechProcess.destroyForcibly();
			this.deepspeechProcess = null;
		}
	}
	
	/**
	 * Start the deepspeech process
	 * @param event Callback processor to handle the found speechs
	 */
	public void startProcess(OnSpeechProcessor event) {
		LOGGER.debug("Start deepspeech process");
		
		List<String> commands = new ArrayList<>();
		
		commands.add("/usr/local/bin/python3");
		commands.add("/Users/guillaumegunther/Documents/development/deepspeech/DeepSpeech-0.6.0-alpha.15/examples/mic_vad_streaming/mic_vad_streaming.py");
		commands.add("--nospinner");
		commands.add("--model");
		commands.add("/USers/guillaumegunther/Documents/development/deepspeech/deepspeech-0.6.0.a15-0.3.2/output_graph.pbmm"); 
//		commands.add("--alphabet");
//		commands.add("/USers/guillaumegunther/Documents/development/deepspeech/deepspeech-0.6.0.a10-models-fr/alphabet.txt");
		commands.add("--lm");
		commands.add("/USers/guillaumegunther/Documents/development/deepspeech/deepspeech-0.6.0.a15-0.3.2/lm.binary");
		commands.add("--trie");
		commands.add("/USers/guillaumegunther/Documents/development/deepspeech/deepspeech-0.6.0.a15-0.3.2/trie");
		
		LOGGER.debug("Run command: {}", commands.stream().collect(Collectors.joining(" ")));
		
		try {
			ProcessBuilder builder = new ProcessBuilder()
				.redirectErrorStream(true)
				.command(commands);
			Process process = builder.start();
			
		      try(BufferedReader br = new BufferedReader (new InputStreamReader (process.getInputStream()))){
			      String line;
			      boolean started = false;
			      while ((line = br.readLine ()) != null) {
			    	  if(started && line.startsWith("Recognized")) {
			    		  String speech = line.substring("Recognized: ".length()).trim();
			    		  if(StringUtils.isNotEmpty(speech)) {
			    			  LOGGER.debug("Found speech: {}", speech);
			    			  event.process(speech);
			    		  }
			    	  } else if(StringUtils.isNotEmpty(line) && line.startsWith("Listening")) {
			        	started = true;
			    	  }
			      }  
		      }
		} catch (IOException e) {
			LOGGER.error("An error occurs while starting the deepspeech process", e);
		}
		
		LOGGER.debug("End deepspeech process");
	}
	
	/**
	 * Interface used to define a speech processor which will handle found speechs
	 */
	@FunctionalInterface
	public interface OnSpeechProcessor {
		void process(String speech);
	}
}

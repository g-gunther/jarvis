package com.gguproject.jarvis.plugin.time.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.utils.StringBuilderUtils;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Time;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeUnit;
import com.gguproject.jarvis.plugin.time.TimePluginConfiguration;
import com.gguproject.jarvis.plugin.time.TimePluginConfiguration.PropertyKey;

import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Properties;

@Named
public class SpeakerService implements OnPostConstruct {

	private final TimePluginConfiguration configuration;
	
	private Properties properties;

	public SpeakerService(TimePluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		File speakerFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.speakerPropertyFile))
				.orElseThrow(() -> TechnicalException.get().message("Speaker configuration file not found").build());

		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(speakerFile));
		} catch (IOException e) {
			throw new IllegalStateException("Not able to load speaker property file", e);
		}
	}
	
	/**
	 * 
	 * @author guillaumegunther
	 *
	 */
	private final class SpeakerProperty {
		private static final String hour = "hours";
		private static final String minutes = "minutes";
		private static final String secondes = "seconds";
		private static final String and = "and";
		private static final String timer = "timer";
		private static final String alarm = "alarm";
		private static final String ringIn = "ring.in";
		private static final String ringAt = "ring.at";
		private static final String itIs = "itis";
		private static final String midday = "midday";
		private static final String midnight = "minuit";
	}
	
	/**
	 * 
	 * @author guillaumegunther
	 *
	 */
	public enum TimerType {
		ALARM(SpeakerProperty.alarm),
		TIMER(SpeakerProperty.timer);
		
		private String labelCode;
		
		private TimerType(String labelCode) {
			this.labelCode = labelCode;
		}
		
		public String getLabelCode() {
			return this.labelCode;
		}
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public String toSpeech(TimerType timerType, Time time) {
		if(time.isExactTime()) {
			return this.fromExactTimeToSpeech(timerType, time);
		} else {
			return this.fromDelayToSpeech(timerType, time);
		}
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	private String fromExactTimeToSpeech(TimerType timerType, Time time) {
		OffsetDateTime date = time.toOffsetDateTime();

		StringBuilder sb = StringBuilderUtils.build(this.properties.getProperty(timerType.labelCode))
			.append(this.properties.getProperty(SpeakerProperty.ringAt))
			.append(" ")
			.append(date.getHour())
			.append(" ")
			.append(this.properties.getProperty(SpeakerProperty.hour));
		
		if(date.getMinute() != 0) {
			sb.append(" ")
				.append(this.properties.getProperty(SpeakerProperty.and))
				.append(" ")
				.append(date.getMinute())
				.append(" ")
				.append(this.properties.getProperty(SpeakerProperty.minutes));
		}
		sb.append(".");

		return sb.toString();
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	private String fromDelayToSpeech(TimerType timerType, Time time) {
		int hour = time.getTimeElements().stream().filter(e -> e.getUnit() == TimeUnit.HOUR).map(e -> e.getValue()).findFirst().orElse(0);
		int minute = time.getTimeElements().stream().filter(e -> e.getUnit() == TimeUnit.MINUTE).map(e -> e.getValue()).findFirst().orElse(0);
		int second = time.getTimeElements().stream().filter(e -> e.getUnit() == TimeUnit.SECOND).map(e -> e.getValue()).findFirst().orElse(0);
		
		StringBuilder sb = StringBuilderUtils.build(this.properties.getProperty(timerType.labelCode))
			.append(this.properties.getProperty(SpeakerProperty.ringIn));
		
		if(hour != 0) {
			sb.append(" ")
				.append(hour)
				.append(" ")
				.append(this.properties.getProperty(SpeakerProperty.hour));
		}
		if(minute != 0) {
			if(hour != 0) {
				sb.append(" ")
					.append(this.properties.getProperty(SpeakerProperty.and))
					.append(" ");
			} 
			sb.append(" ")
				.append(minute)
				.append(" ")
				.append(this.properties.getProperty(SpeakerProperty.minutes));
		}
		if(second != 0) {
			if(hour != 0 || minute != 0) {
				sb.append(" ")
					.append(this.properties.getProperty(SpeakerProperty.and))
					.append(" ");
			}
			sb.append(" ")
				.append(second)
				.append(" ")
				.append(this.properties.getProperty(SpeakerProperty.secondes));
		}
		sb.append(".");

		return sb.toString();
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public String toSpeech(OffsetTime time) {
		int hour = time.getHour();
		int minute = time.getMinute();
		
		StringBuilder sb = StringBuilderUtils.build(this.properties.getProperty(SpeakerProperty.itIs));
		
		if(hour == 12) {
			sb.append(" ").append(this.properties.getProperty(SpeakerProperty.midday));
		} else if(hour == 0) {
			sb.append(" ").append(this.properties.getProperty(SpeakerProperty.midnight));
		} else {
			sb.append(" ").append(hour).append(" ").append(this.properties.getProperty(SpeakerProperty.hour));
		}
		
		if(minute == 0) {
			sb.append(".");
		} else {
			sb.append(" ")
				.append(minute)
				.append(".");
		}
		
		return sb.toString();
	}
}

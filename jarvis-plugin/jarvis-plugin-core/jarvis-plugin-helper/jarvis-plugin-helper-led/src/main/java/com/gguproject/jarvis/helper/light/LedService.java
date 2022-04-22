package com.gguproject.jarvis.helper.light;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.DevUtils;
import com.pi4j.Pi4J;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.library.pigpio.impl.PiGpioNativeImpl;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;

import javax.inject.Named;
import java.util.concurrent.TimeUnit;

@Named
public class LedService implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(LedService.class);
	
	private DigitalOutput led;

	private static final int PIN_LED = 18;

	@Override
	public void postConstruct() {
		if(!DevUtils.isDevEnvironment()) {
			try {
				var pi4j = Pi4J.newContextBuilder()
						.autoDetect()
						.addPlatform(PiGpioDigitalOutputProvider.newInstance(PiGpioNativeImpl.newInstance()))
						.build();

				var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
						.id("led")
						.name("LED Flasher")
						.address(PIN_LED)
						.shutdown(DigitalState.LOW)
						.initial(DigitalState.LOW)
						.provider(PiGpioDigitalOutputProvider.class);
				this.led = pi4j.create(ledConfig);
			} catch (Exception e) {
				LOGGER.error("Not able to initialize PI4J context", e);
			}
		}
	}
	
	private boolean hasBeenInitialized() {
		return this.led != null;
	}
	
	/**
	 * 
	 * @param duration
	 */
	public void flash(int duration) {
		LOGGER.debug("Flash led for {} ms", duration);
		if(this.hasBeenInitialized()) {
			try {
			    this.led.pulse(duration, TimeUnit.SECONDS);
			} catch (IOException e) {
			    LOGGER.error("Error while flashing the light", e);
			}
		}
	}
	
	public void turnOn() {
		LOGGER.debug("Turn on the led");
		if(this.hasBeenInitialized()) {
			try {
				this.led.high();
			} catch (IOException e) {
				LOGGER.error("An error occurs while turning on the led", e);
			}
		}
	}
	
	public void turnOff() {
		LOGGER.debug("Turn off the led");
		if(this.hasBeenInitialized()) {
			try {
				this.led.low();
			} catch (IOException e) {
				LOGGER.error("An error occurs while turning off the led", e);
			}
		}
	}
}

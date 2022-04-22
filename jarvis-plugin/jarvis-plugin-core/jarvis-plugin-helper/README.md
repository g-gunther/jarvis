# jarvis-plugin-help

This module contains several useful libraries to interact with the Raspberry PI IO devices, or provides some useful transversal features.

## Google Counter

Little project that help to store a counter on a file. Since some plugins are using google cloud services (speech to text or text to speech for example)
which are free for X calls per period, we need to track how many calls has been done.

Example: 

```java
@Named
public class GoogleSpeakerCounterService extends GoogleCounterService<CounterName> {
	
    @Inject
	private SpeakerPluginConfiguration configuration;
	
	@Override
	public File getCounterFile() {
        // define the name of the counter file (in the data/ plugin folder)
		return this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.googleCounterFileName));
	}
	
	/**
	 * List of counter
	 * @author guillaumegunther
	 */
	public enum CounterName {
		SPEAKER_REQUEST;
	}
}
```
With the following configuration file:

```json
{
  "SPEAKER_REQUEST": {
    "period": "MONTH",
    "max": 1000000,
    "dateCounters": {
      "2020-05-21": 12,
      "2020-05-20": 120
    },
    "count": 132
  }
}
```

And its usage:

```java
@Named
public class SpeakerService {
    @Inject
    private GoogleSpeakerCounterService counterService;
    
    public void test(){
        try {
            // check the counter
            counterService.check(CounterName.SPEAKER_REQUEST);
        } catch (GoogleCounterException e) {
            LOGGER.warn("Can't call google-speaker service");
            return;
        }
        // do some actions
        // increment the counter
        this.counterService.increment(CounterName.SPEAKER_REQUEST);
    }
}
```

## LED

Provides a service to turn on and off a led on the PIN 18 of the Raspberry.

### Installation

This project requires the installation of [pi4j](https://v2.pi4j.com/getting-started/set-up-a-new-raspberry-pi/).
The pre-requisite of this installation is [pigpio](http://abyz.me.uk/rpi/pigpio/download.html).

Due to the kernel architecture, the pi4j lib tries to automatically load the *libpi4j-pigpio.so* file from an incorrect path.
Therefore we need to provide the path to this lib:  

- extract the *libpi4j-pigpio.so* file from the jar pi4j-library-pigpio-2.0.jar (in folder */lib/armhf*).
- copy it in the /jarvis folder (root of the application on the Raspberry).
- start the application by adding the argument `-Dpi4j.library.path="/home/pi/jarvis/"` (which is the path where the .so file is located). 

At the time this documentation is written, a bug was found and requires some updates of the .so file.
If the following error *PIGPIO ERROR: PI_INIT_FAILED;* occurs, then check [issue](https://github.com/Pi4J/pi4j-v2/issues/60):

- update the file *libpi4j-pigpio.so*
- replace `gpioCfgSetInternals (gpioCfgGetInternals () | PI_CFG_NOSIGHANDLER);` by `gpioCfgSetInternals (PI_CFG_NOSIGHANDLER);`

## Shell

Provides a useful class to perform some shell commands (like sleep).

## Sound

Provides a service to play a sound file (only once or in a loop).

### Installation

To enable a USB speaker, follow this [guide](https://raspberrypi.stackexchange.com/questions/80072/how-can-i-use-an-external-usb-sound-card-and-set-it-as-default)

- Run command `cat /proc/asound/modules` 
- retrieve the card number associated to the USB device
- Do the same with `cat /proc/asound/cards`
- Remove the jack speaker in `sudo nano /boot/config.txt` file and put `dtparam=audio=off`
- open `sudo nano ~/.asoundrc` and specify the card number: 
```
pcm.!default {
        type hw
        card 1
}

ctl.!default {
        type hw
        card 1
}
```

- Do the same in `sudo nano /usr/share/alsa/alsa.conf` file by setting: 
```
defaults.ctl.card 1
defaults.pcm.card 1
```
By executing `alsamixer`, the volume gauge should at the maximum. It has to be decreased a little bit else it might not work (bug).
A test can be done by downloading a sound file: `wget https://www.kozco.com/tech/piano2.wav` and playing it: `aplay piano2.wav` or `speaker-test -c2`
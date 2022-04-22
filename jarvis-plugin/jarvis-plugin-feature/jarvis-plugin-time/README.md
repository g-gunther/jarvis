# jarvis-plugin-time

This plugin allows to configure alarms (which plays a sound at a given time) or timers (which plays a sound after a given duration).

## Events

### Input

list of listened events: 

**TIME_COMMAND** (TimeCommandEventData): to perform an action on an alarm or a timer  
event attributes:
- target: target of the action (ALARM or TIMER)
- command: the action to perform (STOP)

## Speech listeners

List of speech listeners: 

1. Set an alarm: set an alarm at the given time. It also sends a speaker event to inform the user that an alarm has been set and an other event to display the alarm on the front-end.
2. Stop an alarm: stop the currently playing alarm sound. It sends an event to hide the alarm on the front-end.
3. Set a timer: set a timer for a given duration. As for the "set an alarm" listener, it sends a speaker and display events.
4. Stop a timer: stop the currently playing timer sound. It sends an event to hide the alarm on the front-end.
5. Current time: ask to display the the current time

## Commands

Implemented commands:

1. alarm set -hour={hour} -minute={minute} -second={second} -exact={exact}  
{hour}: hour value to set the alam (optional, 0 by default)  
{minute}: minute value to set the alam (optional, 0 by default)  
{second}: second value to set the alam (optional, 0 by default)  
{exact}: if true, the given hour, minute, second values will be used to determine the exact time for the alarm, else they are used to determine in how many time the alarm sounds (optional, false by default). 
2. alarm stop: stop the alarm
3. timer set -hour={hour} -minute={minute} -second={second} -exact={exact}  
it has the same parameters as the command *alarm set*
4. timer stop: stop the timer

Each of these commands triggers the same process as their related speech listeners.

## Implementation

To be able to do some action after a given period of time, the service `DelayService` is used. Its mecanism is based on `java.util.Timer`
which can trigger an action at a given date time.

For now there is no record of the set alarms and triggers to be able to unset them if needed.

There is also the `SpeakerService` which translate a given digital date to a plain text sentence. This sentence
can then be sent as an event to be played on a speaker.

## Configuration

Configuration details:

- sound.timer: path to the wav file that is played at the end of the timer
- sound.alarm: path to the wav file that is played for the alarm
- speaker.file: location of a property file containing the list of words that are used to convert the digital time to a sentence 
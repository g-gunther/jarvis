# jarvis-plugin-speech

This modules contains 2 speech recognition plugins: one using deepspeech and the other one the google speech-to-text service.

# Installation

Check the deepspeech or google sub-modules for more details.

## Microphone

Here is a [link](http://mutsuda.com/2012/09/07/raspberry-pi-into-an-audio-spying-device/) to setup and test a microphone on a raspberry

## Events

### Output

List of sent events: 

**SPEECH_EVENT** (SpeechEventData) contains the found speech to be broadcasted to all other plugins  
event attributes:  
- speech: the recognized speech

## Commands

Implemented commands:

1. speech -text={text} -broadcast: used to simulate a detected speech (useful in dev mode)  
{text}: the text to process  
{broadcast}: optional attribute, if present it will broadcast the speech across the network

## Implementation

This module provides 2 additional sub-modules which are helpers to enable the other plugins to receive and processed easily a sentence:
- jarvis-plugin-speech-interpreter: provides some tools to analyze a sentence and convert it to a command (action, context, data, localization, time)   
- jarvis-plugin-speech-listener: listens to SPEECH_EVENT events, convert it to commands (using the interpreter) and then tries to process them

If a plugin can be controlled by a speech command, it needs to depend of the `jarvis-plugin-speech-listener`module. A speech command is composed of:

- context: defines the equipment targeted by the command (TV, MUSIC, LIGHT...) 
- action: the action to perfom on that equipment (turn on, turn off...)
- data: additional data that might be needed to perform the action
- localization: defines one or several places to target a more specific equipment
- time: defines when a command has to be executed (at a specific time or in a certain time)

A data file `grammar.json` describing the sentences the plugin can handle should also be created. Here is its format:

```json
{
  // all the additional data words that might be needed for some actions
  "data": {
    "numbers": ["one", "two", "three", "four"],
    "tv_channel": ["tf1", "m6", "w9"]
  },
  // all the actions and their words
  "actions": {
    "START": ["turn on"],
    "STOP": ["turn off", "stop"],
    "VOLUME_UP": ["turn up sound"],
    "VOLUME_DOWN": ["turn down sound"],
    "MUTE": ["mute"],
    "UNMUTE": ["unmute"],
    "SET": ["put"]
  },
  // definition of all the contexts handled by the plugin.
  "contexts": [
    // a context is defined by a unique 'type' and the linked words
    // and a list of actions. It can be a single action or an action associated with some data) 
    {
      "type": "TV",
      "words": ["tv", "television"],
      "actions": [
        "START",
        "STOP",
        "VOLUME_UP",
        "VOLUME_DOWN",
        "MUTE",
        "UNMUTE",
        {
          "action": "SET",
          "data": [
            "numbers",
            "tv_channel"
          ]
        }
      ]
    },
    {
      "type": "ALL",
      "words": ["all"],
      "actions": ["STOP"]
    }
  ]
}
```

So a command can be identified by its context and actions. To implement a speech command processor:

```java
@Named
public class ChannelTvListener extends AsbtractSpeechCommandProcessor {
	
	public ChannelTvListener() {
        // 'TV' is the type of the context defined in the grammar.json file
        // 'SET' is the action type that has been found
		super("TV", "SET");
	}
	
	@Override
	public void process(DistributedEvent event, Command data) {
		// process the command
        // retrieve the data 'numbers' or 'tv_channel' associated to the 'SET' action by calling: data.getData()
	}
}
```

## Configuration

check the deepspeech or google sub-module
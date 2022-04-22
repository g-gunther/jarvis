# TV

This plugin acts as an orchestrator for all TV related commands to synchronized actions on speakers, tv screen and multimedia players.

## Speech listeners

List of speech listeners: 

1. Mute or unmute: send an infrared event to mute or unmute the sound bar
2. Turn on: send the power infrared event to both the tv screen and sound bar, and the power on event to the freebox v6 player 
3. Turn off: same as "turn on" but to turn them off
4. turn up the volume: send the infrared event to turn up the volume of the sound bar
5. turn down the volume: same as above to turn the volume down

## Implementation

The implementation is quite basic, every speech context and action matches with a entry in the list of configured commands (see configuration).
Each entry in that configuration is composed of a list of commands which can be of type:

- INFRARED: sends a "INFRARED_COMMAND" event data
- FREEBOXV6: send a "FREEBOXV6_COMMAND" event data

More can be added but it has to be developed in the list of event types in the class `GenericCommandTvProcess.commandTypeEventProcessors`. It contains the way each event type if built.   

## Configuration

Configuration details:

- command.configuration.file: path the commands configuration  
This file contains the list of actions that are supported and the events that should be send across the network to perform the right actions on the other plugins
# Infrared

This plugin allows to send infrared signals to control TV screen, speakers and everything that can generally be controled with a remote control.

## Installation

First of all, it needs to set up the electric circuit to receive and send infrared signals.
The following links describes the circuit:

- [hackster.io](https://www.hackster.io/duculete/ir-remote-with-raspberry-pi-d5cf5f)
- [instructables.com](https://www.instructables.com/id/Creating-a-Raspberry-Pi-Universal-Remote-With-LIRC/)

But this two articles use a deprecated library to interact with the receiver and emitter (lirc).
There is now a `ir-ctl` library which is described:

- [receiver](https://blog.gordonturner.com/2020/05/31/raspberry-pi-ir-receiver/)
- [transmietter](https://blog.gordonturner.com/2020/06/10/raspberry-pi-ir-transmitter/)

To test the receiver: `ir-keytable -t -s rc0` and then press some remote control button. It should print something like:

```
841.570053: lirc protocol(rc6_0): scancode = 0x11
841.570100: event type EV_MSC(0x04): scancode = 0x11
841.570100: event type EV_KEY(0x01) key_down: KEY_VOLUMEDOWN(0x0072)
849.070051: lirc protocol(rc6_0): scancode = 0x10 toggle=1
849.070087: event type EV_MSC(0x04): scancode = 0x10
849.070087: event type EV_KEY(0x01) key_down: KEY_VOLUMEUP(0x0073)
```

And to try the emitter `ir-ctl -S rc6_0:0x11 -d /dev/lirc0` or `ir-ctl -S rc6_0:0x10 -d /dev/lirc0`. 
This will send a command using the rc6_0 protocol which is one of the most used.

If the remote control uses an unknown protocol, you can still choose to record the original infrared signal in a file and play it again:

- to record a signal: `ir-ctl -d /dev/lirc1 -r > VOLUME_DOWN.txt`
  - Note: delete the timeout entries in the file
- to play the file: `ir-ctl -d /dev/lirc0 --send=VOLUME_DOWN.txt`

## Events

### Input

list of listened events: 

**INFRARED_COMMAND** (InfraredCommandEventData): used to send some infrared signals.  
event attributes:
- commands: list of commands to execute. Each command is identified by a context and an action (exemple: TV and START).  
It also contains a map of properties that can be used to customize the commands to execute.

## Commands

Implemented commands:

1. infrared -context={context} -action={action}: execute an infrared command  
{context}: name of the command context  
{action}: name of command action

## Implementation

At plugin startup, the file containing all the commands is loaded. When an event is received, it first looks
in the list of loaded commands if there is one matching the pair of context/action names provided. 

If an entry can be found, then it simply executes it as a shell script. 

There is a command added by default with context GENERAL and action SLEEP that allow to do some pauses between the commands
if a scenario of several commands is needed (first turn on the tv, wait for X seconds, then press on channel 5 for example).

## Configuration

Configuration details:

- configuration.file: name of the file that contains the list of context/action/command that can be executed  
This file contains the list of all infrared command that can be sent. Each command is identified by a unique pair of context and action names.

Some infrared commands requires a file containing the raw infrared data. There are in the folder keycodes/.
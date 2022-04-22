# CEC (deprecated)

This project was created to try controlling a TV screen by a Raspberry Pi connected with an HDMI cable using the CEC protocol.  
It turns out it does not work very well and it was quite hard to decode the CEC output when trying to get some data like the TV status.
Also, it was not working properly when the TV screen was in sleep mode, only in standby mode.

But this was only tested on a cheap TV screen (not modern enough?).

## Installation

It requires the [cec-client](https://pimylifeup.com/raspberrypi-hdmi-cec/) library installed on the Raspberry

### Shell commands

In order to try the installation, here are some commands:

- echo 'tx 1f:82:10:00' | cec-client -s RPI -> select HDMI 1
- echo 'on 0' | cec-client -s RPI
- echo 'standby 0' | cec-client -s RPI
- echo 'as' | cec-client -s RPI
- echo 'pow' | cec-client -s RPI

## Events

### Input

list of listened events: 

**CEC_STATUS_REQUEST** (CecStatusRequestEventData)  
Ask the power status and check if the Raspberry is the current active source of the TV Screen

### Output

List of sent events: 

**CEC_STATUS_RESPONSE** (CecStatusResponseEventData)  
event attributes:
- powerStatus: Power status of the screen (ON, OFF)
- activeSource: True if the Raspberry is the current active source

Event sent in response of the *CEC_STATUS_REQUEST* event.

## Commands

Implemented commands:

1. cec status: ask for the power status and check the active source  
2. cec off: turn off the TV screen
3. cec on: turn on the TV screen

## Implementation

The plugin is based on *cec-client*. The class `CecService` only executes shell commands and wait for there output to analyse it.

There is no easy solution for the output analysis, only executing the shell command and check on the output to find a specific sentence 
that can be used.

Example: when executing `echo 'scan' | cec-client -s RPI` to check the active source, it then looks for the string `active source: yes`.


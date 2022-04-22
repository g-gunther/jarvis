# Android TV (deprecated)

This project was initially started to remotely control a freebox TV player which uses Android TV.
There were no easy to use java library. So the main principles and libraries that are used in this plugin come from 
android phone application which has been developed to control and Android TV.
There can be found in the libs/ folder of the -core module.

## Events

### Input

list of listened events: 

**ANDROIDTV_TVPIN** (AndroidTvPinEventData)  
event attributes:
- pin: the pin code displayed on the screen to finalize the pairing

The listener will set the pin to the previously started pairing session.

### Output

List of sent events: 

**ANDROIDTV_ASK_FOR_PIN** (AndroidTvAskForPinEventData)  
This event is sent when an attempt of connection is done and failed because it needs pairing between the plugin and the TV.  
It has to be catched and the *ANDROIDTV_TVPIN* event has to be sent next with the pin code displayed on the tv screen.

## Speech listeners

There are 2 speech listeners:

1. To start the android TV
2. To set a specific channel

## Commands

Implemented commands:

1. tv channel -channel={channel}: switch the TV channel  
{channel}: channel number - should be a digits

## Implementation

The top level service is `AndroidTvRemoteControlService`. It hides the complexity of interacting with the Android TV.
It is responsible for establishing the connection with the TV and send some key codes to switch channel for example. Check the `Code` enum for a complete list.

This plugin is based on anymote Java libary and google polo. It allows to create a connection between the application 
and the android TV using SSL (see `ConnectionHandler` class for the implementation details).

The connection requires a local certificate which is generated if missing by the `KeyStoreManager`.

If the connection with the Android TV fails, it might be because of a missing pairing between the plugin and the android TV.
In that case, a pairing session is opened, a pin code should appear on the TV screen and has to be provided to the pairing session to complete it.

## Configuration

Configuration details:

- androidtv.channel: path to a json file providing the mapping between a channel name to its digits value
- androidtv.ipAddress: ip address of the Android TV
- androidtv.port: access port of the Android TV (seems to be 6466 by default)
- androidtv.keystore: name of the keystore file in the data folder
- androidtv.keystore.password: password of the keystore
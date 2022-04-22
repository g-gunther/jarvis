# Spotify

This is used to control a spotify application instance (which is typically installed on one of the connected Raspberry).

It is also used to send events to the sound bar using the infrared events in order to increase/decrease the volume, mute or unmute.

## Installation

### Spotify credentials

**Note**: it only works if you have a premium spotify account

This plugin interacts with spotify via the library `se.michaelthelin.spotify.spotify-web-api-java`.
This library requires the creation of a client_id/client_secret from the [developer interface](https://developer.spotify.com/dashboard) by creating a new application on it.

From the settings of the application, we need to specify the option `Redirect URIs`. This URL will be called once the authentication is done in order to retrieve the refresh_token.
A github.io page has been created to simulate this authentication and retrieve the refresh_token. The url to specify is the following: `https://g-gunther.github.io/spotify-code-callback.html`

Then, to retrieve the token, simply browse the page `https://g-gunther.github.io/`, fill the client_id, client_secret and the list of scopes you'd like to give to the token.
Here is a list of all the available [scopes](https://developer.spotify.com/documentation/general/guides/scopes/).
For this plugin, here is the list of required scopes:

- playlist-read-private
- user-modify-playback-state
- streaming

More information on spotify authentication: [documentation](https://developer.spotify.com/documentation/general/guides/authorization-guide/)
 
### Raspberry spotify client

A spotify player has to be installed on the raspberry. The following library can be used: [raspotify](https://github.com/dtcooper/raspotify)

Then 2 options:

- fill the login and password of you spotify account on the Raspotify configuration file. The application should appear as a new device (you can execute the API to list of the devices linked to your spotify account). Its identifier can be retrieve and stored in the file `device.json`.
- or using the spotify application on your smartphone (which has to be connected on the same network as the raspberry). You should be able to see the new device using spotify-connect and use it to play your music. You can then retrieve and store the device identifier using the spotify API. 

If the speakers/sound bar is connected via USB, you will need to find the audio device name:

- `aplay -L` will list the audio devices. Retrieve its name. Example: `sysdefault:CARD=Device_1` gives the name `sysdefault:CARD`.
- add the option `$$device=\"sysdefault:CARD\"` to the list of options

## Speech listeners

List of speech listeners: 

1. Start music
2. Stop music
3. Mute music
4. Unmute music
5. Turn up the volume
6. Turn down the volume
7. Set music: used to simply play the music or set and play a specific user playlist

All the 6th first listeners are used to control the sound bar using infrared commands.

## Commands

Implemented commands:

1. spotify device list: list all the devices of the spotify account, and the stored device of this plugin and display them 
2. spotify device register -id={id}: store a device id in the configuration file. This should be the raspotify device id.  
{id}: device id to register
3. spotify device select: select the stored device as the current spotify playing device (it will switch from the phone for example to the configured device)
4. spotify playlist list: list all the playlist of the connected user
5. spotify playlist start -id={id}: start the given playlist  
{id}: playlist id 
6. spotify playlist pause: pause the music

## Implementation

This plugin is based on the spotify APIs. They can be called using the `SpotifyService`. 
Some features requires a more complex logics and orchestration of API calls. The `SpotifyServiceOrchestrator` allows that.
Especially for the *play* feature. It requires to check what is the current selected device, its state and depending on that it will select the
raspotify device, starts it or not before starting the music.  

## Configuration

Plugin configuration details:

- infrared.command.configuration.file: list of infrared events to send. Used by all the speech listeners that interact with it
- spotify.device.file: path to the file where the device information is stored

Secured property file: spotify_credentials.properties: 

- secret.spotify.api.client_id: spotify client id
- secret.spotify.api.client_secret: spotify client secret
- secret.spotify.api.refresh_token: spotify refresh token, used to retrieve authorization code
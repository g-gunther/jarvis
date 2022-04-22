# Kodi

This plugin is used to control a Kodi media center with netflix installed on it.

## Installation

Here are the steps to follow in order to install Kodi and netflix on it:

- sudo apt-get install kodi
- sudo apt-get install kodi-inputstream-adaptive
- sudo apt-get install kodi-inputstream-rtmp

- sudo apt install python-pip
- sudo apt-get install build-essential python-dev
- sudo su kodi -c 'pip install --user pycryptodomex'

In order to start Kodi when the Raspberry Pi starts: 
- crontab -e
  - > @reboot kodi --standalone
	
Then to install Netflix, follow the [guide](https://github.com/CastagnaIT/plugin.video.netflix)
- download repository.castagnait-1.0.0.zip
- install it manually in kodi (addons > package on top > install zip)
- search video addons > netflix > install
- then run kodi from terminal (not using cron) to init netflix & download all needed stuff

You can then adjust the quality: 
- video stream addons to set quality
- and do the same in netflix settings
  
### External hard drive  

To plug an external hard drive (exfat) to the Raspberry: 
- [mount a disk](https://www.raspberrypi-spy.co.uk/2014/05/how-to-mount-a-usb-flash-disk-on-the-raspberry-pi/)
- [support exfat](https://pimylifeup.com/raspberry-pi-exfat/)

### Activate HTTP services

To activate the HTTP services do to options > services > controls. 

### Test commands

Kodi is using jsonrpc, so it's a single endpoint: POST http://[kodi ip address]:8080/jsonrpc

- To display the home page: {"jsonrpc":"2.0","method":"GUI.ActivateWindow","id":374,"params":{"window":"home","parameters":["home"]}}
- play/pause: {"jsonrpc": "2.0", "method": "Player.PlayPause", "params": { "playerid": 0 }, "id": 1}
- confirm the select on screen: {"jsonrpc":"2.0","method":"Input.ExecuteAction","id":374,"params":{"action":"select"}}
  - can be used to select the default profile on Netflix screen for example  
- To select Netflix: {"jsonrpc":"2.0","method":"GUI.ActivateWindow","id":374,"params":{"window":"videos","parameters":["plugin://plugin.video.netflix/"]}}
- To list all the netflix users: {"jsonrpc":"2.0","method":"Files.GetDirectory","id":374,"params":{"directory": "plugin://plugin.video.netflix/", "media": "files"}} 
  - can be used to list all the folder of netflix plugins and make shortcut to them

There is an addon which can be used to turn on the screen and select the right source: [json-cec](https://github.com/joshjowen/script.json-cec) 
- {"jsonrpc":"2.0","method":"Addons.ExecuteAddon","params":{"addonid":"script.json-cec","params":{"command":"activate"}},"id":1}
  - https://github.com/joshjowen/script.json-cec/issues/8

## Speech listeners

List of speech listeners: 

1. Set netflix: which turn on the TV and start netflix by selecting the configured default profile

## Commands

Implemented commands:

1. kodi netflix: turn on the TV and start netflix by selecting the configured default profile

## Implementation

Kodi exposes a single REST endpoint to interact with the installed services. So is just about calling some REST APIs to perform the wanted actions.

## Configuration

Secured property file: kodi_credentials.properties: 

- secret.kodi.host: http endpoint of the Kodi process
- secret.kodi.auth.user: user name for basic http authentication
- secret.kodi.auth.password: password for the http authentication
- secret.kodi.netflix.profile_id: default netflix profile id (can be found by listing all the netflix profiles using the rest APIs)
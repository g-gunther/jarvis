# Display

This project only contains an event used to send data information to the `magic-mirror` project.
This is a side project to add a screen on the jarvis application to display some information (like weather or time).

## Installation

Here are some useful links to enable a small screen Raspberry Pi 4 7" Touchscreen:

- how to install it: [video](https://www.youtube.com/watch?v=J69-bxOSMC8&ab_channel=ETAPRIME)
- how to execute Google Chrome automatically at startup: [guide](https://blog.r0b.io/post/minimal-rpi-kiosk/) and for [information](https://die-antwort.eu/techblog/2017-12-setup-raspberry-pi-for-kiosk-mode/)

## Events

### Output

List of sent events: 

**DISPLAY_EVENT** (DisplayEventData)
- screenId: name of the screen to display on the `magic-mirror` front-end
- data: object to serialize and send to the front-end

This event can be sent by any other plugins that needs to display to information on the screen.
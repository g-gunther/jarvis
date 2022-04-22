# jarvis-plugin-speech-google

This plugin uses the speech-to-text google service to recognize a speech.
To use this service, credentials has to be specified in the `google_credentials.json` file in the `data/` folder.
This file can be generated from [Google Cloud Platform](https://console.cloud.google.com/iam-admin/serviceaccounts), by
selecting the project, the account and then creating a key.

Since the use of this google service is free for a given amount of request, 
the sphinx library is also used to detect the 'keyword' (here 'jarvis'). When found, it records the following seconds
and send them to the Google service for recognition.

A counter is also set using the `jarvis-plugin-helper-google-counter` to avoid exceeding the limit of use.
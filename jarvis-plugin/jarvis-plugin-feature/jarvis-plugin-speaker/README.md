# Speaker

This module contains 2 speakers plugins:

1. The first one is based on google service text-to-speech
2. The second one uses the standalone java library Marytts

# Google

## Installation

It uses the google service `text-to-speech` which allows us to convert a free text to a wave file and play it.
Because it's a google service, it requires some credentials to call it. 
It can be generated from [GCP](https://console.cloud.google.com/iam-admin/serviceaccounts) by selecting the project you've created, select the account and then create a key.  
The key file can then be store in a `google_credentials.json` file under the *data/* folder.

## Events

### Input

list of listened events: 

**SPEAKER_EVENT** (SpeakerEventData): to convert a free text (or get one by code) to speech and play it
event attributes:
- speech: Free text to convert
- speechCode: Code used to retrieve a pre-configured text and convert it to speech (used only it the *speech* attributes is null)

## Commands

Implemented commands:

1. speak -text={text} -code={code}: convert and play a given text  
{text}: free text to convert
{code}: code of the pre-configured text to play (used only if {text} is not defined)

## Implementation

It uses the `google-cloud-texttospeech` library and the `TextToSpeechClient` it provides to convert a free text 
to a mp3 file. This file is downloaded and stored as a temporary file which is then played once by the `jarvis-plugin-helper-sound` service.

There is also a google counter service put in place in order to count the number of text words that are converted to avoid exceeding the free cost limit. 

## Configuration

Configuration details:

- google.counter.filename: file path of the google counter file
- speaker.properties.language: speech language for the property codes (fr for french)
- speaker.google.credential: file path of the google service credential
- speaker.google.language: text language for the google service
- speaker.google.voice.name: google voice to use

# Marytts

## Installation

Based on this project: [https://github.com/marytts/marytts](https://github.com/marytts/marytts).
It also uses the `voice-upmc-pierre-hsmm` french voice (see maven dependencies).

## Events

### Input

list of listened events: 

**SPEAKER_EVENT** (SpeakerEventData): to convert a free text (or get one by code) to speech and play it
event attributes:
- speech: Free text to convert
- speechCode: Code used to retrieve a pre-configured text and convert it to speech (used only it the *speech* attributes is null)

## Commands

Implemented commands:

1. speak -text={text} -code={code}: convert and play a given text  
{text}: free text to convert
{code}: code of the pre-configured text to play (used only if {text} is not defined)

## Implementation

It uses the marytts interface to generate an `AudioInputStream` and play it directly using the `AudioPlayer` provided by the marytts project.

## Configuration

Configuration details:

- speaker.voice: voice name to use (*upmc-pierre-hsmm* for french voice)
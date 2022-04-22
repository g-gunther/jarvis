# Weather

This plugin tries to get the weather forecast of a specified localization.

## Installation

It uses the APi https://api.weatherbit.io/v2.0/. An account as to be created to generate an API key. 

## Speech listeners

List of speech listeners: 

1. weather forecast on localization: tries to retrieve the forecast 

## Commands

Implemented commands:

1. weather get -localization={localization}: get the forecast for a specific city...  
{localization}: localization to send to the service 

## Implementation

This plugin uses the **weatherbit** online service to get the weather forecast on a specific city.
The call to this service is abstracted in order to easily change to another service if the terms & conditions change.

The response of the service is then sent to the front-end (magic-mirror) using the **DISPLAY_EVENT** event in order to be display. 

## Configuration

Configuration details:

- weatherbit.api.key: API key of the service
- weatherbit.api.endpoint: API endpoint
- weatherbit.api.lang: default language for the API response
- weatherbit.api.country: default country
- weatherbit.api.unit: unit system (M for metric)

There is also the *weather.properties* file which contains all the weather code that can be returned by the weatherbit service.
It is used by the front-end to get the right label describing the weather. 
There is also a folder *icons* containing all the icons matching those weather codes for day & night.
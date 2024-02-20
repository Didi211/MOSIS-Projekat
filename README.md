# Tour Guide

<div align="center">
  <img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/88c6389f-9848-4a88-bb57-5e970ec2ea9d" width="250" height="auto">
</div>

Tour Guide is a mobile application developed as a project for the Mobile Systems and Services course. It is designed to facilitate the creation and organization of tours or trips, whether solo or with friends.

## Overview

The Tour Guide app provides users with a range of features centered around mapping functionalities. Users can create tours, add places to visit, invite friends to join tours, and more. Additionally, the app offers basic map functionalities such as pinpointing the user's location, searching for places, and filtering places based on preferences.

## Features

- Create tours
- Add places to visit
- Invite friends to join tours
- Pinpoint user's location on the map
- Search for places
- Filter places based on preferences

## Platform and Technologies

- **Platform:** Android
- **UI Framework:** Jetpack Compose
- **Backend:** [TourGuide-API](https://github.com/Didi211/TourGuide-Api) (Web Server)
- **Persistence:** Firebase Authentication and Firestore

## Development Details

The Tour Guide app is developed exclusively for the Android platform, utilizing Jetpack Compose for its UI development. The TourGuide-API web server is utilized for performing complex calls required for Google Maps APIs. These calls include functionalities such as route optimization, geocoding, and other advanced mapping features. The backend server is hosted separately and handles these complex operations, allowing the app to focus on providing a smooth user experience. For persistence on the client side, Firebase Authentication and Firestore are used to provide secure user authentication and store tour-related data.

## Getting Started
To run Android app with your own Firebase and Google Map APIs, you need to follow instructions:
1. [Firebase Setup Instructions](https://firebase.google.com/docs/android/setup)
2. [Google APIs integration](https://medium.com/@tarunanchala/step-by-step-integrating-google-apis-into-your-android-applications-bfb9c7e28cec)

After getting required credentials, import **google-services.json** file to the Tour Guide Android app project structure in *app* folder. 
To integrate google credentials add API keys in *local.properties* file:

    API_URL=<tour-guide-api-url>
    MAPS_API_KEY=<YOUR_MAPS_API_KEY> 
    ROUTE_API_KEY=<YOUR_ROUTES_API_KEY>

## Screenshots

Here are some screenshots of the Tour Guide app:

## Screenshots

| Home Screen | Tour Screen | Add new location | Filtered locations |
|:-:|:-:|:-:|:-:|
| <img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/11a70181-3bb2-4a4a-aac6-f38e19b4e19c" alt="Home Screen" width="250" height="auto"> | <img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/6a1215c1-7c67-44ce-b3b6-92a2f57f929d" alt="Tour Screen" width="250" height="auto"> | <img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/dd886a8f-f49a-4f3e-84dd-abe43fbde718" width="250" height="auto"> | <img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/11ef4486-39cd-41f8-9800-ff6e599055ac" width="250" height="auto">





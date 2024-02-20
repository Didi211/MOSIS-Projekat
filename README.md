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

## Screenshots

Here are some screenshots of the Tour Guide app:

**Home Screen**

<img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/11a70181-3bb2-4a4a-aac6-f38e19b4e19c" width="250" height="auto">

**Tour Screen**

<img src="https://github.com/Didi211/MOSIS-Projekat/assets/82868612/6a1215c1-7c67-44ce-b3b6-92a2f57f929d" width="250" height="auto">


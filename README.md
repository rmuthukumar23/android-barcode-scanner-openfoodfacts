# Android Barcode Scanner

![Build](https://github.com/rmuthukumar23/android-barcode-scanner-openfoodfacts/actions/workflows/build.yml/badge.svg)

An Android app written in Java that scans EAN-13 barcodes and looks up product information using the OpenFoodFacts API.

Originally developed in July and August 2025. I uploaded the project to GitHub in March 2026 as a portfolio/archive copy. Later commits are maintenance and presentation fixes rather than the original development history.

## Features

- Scan EAN-13 barcodes with the device camera
- Retrieve product name, brand, barcode and image from OpenFoodFacts
- Choose which product fields are shown
- Switch between rear and front cameras
- Store preferences locally with `SharedPreferences`
- Separate torchlight utility with a simple Morse-code mode

## Tech

- Java
- Android SDK
- ZXing Android Embedded
- OkHttp
- Moshi
- Ion
- Gradle

## Build

Requirements:

- Android Studio with Android SDK 34, or an equivalent command-line Android SDK setup
- JDK 17

Clone and build:

```bash
git clone https://github.com/rmuthukumar23/android-barcode-scanner-openfoodfacts.git
cd android-barcode-scanner-openfoodfacts
./gradlew assembleDebug
```

The generated debug APK is placed under `app/build/outputs/apk/debug/`.

## Project structure

- `MainActivity.java` handles scanning, product lookup and result display
- `SettingsActivity.java` controls saved display and camera preferences
- `Settings.java` wraps `SharedPreferences`
- `OpenFoodFactsResponse.java` and `Product.java` model API responses
- `Torchlight.java` provides the optional flashlight utility

## Notes

The app requests camera and internet access. Product data comes from OpenFoodFacts, so lookup results depend on whether a scanned barcode exists in their database.

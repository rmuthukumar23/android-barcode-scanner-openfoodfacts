# Barcode Scanner Android

An Android application for scanning barcodes and retrieving product information from the OpenFoodFacts API.

## Features

- Scan barcodes (EAN-13) using the device camera.  
- Fetch product details: brand, product name, code, image, and status.  
- Toggle visibility of fields via **Settings**.  
- Torchlight support for scanning in low light.  
- Lightweight and user-friendly interface.

## Project Structure

- **MainActivity.java** – Handles barcode scanning, API requests, and updates UI elements (`brand`, `product`, `code`, `status`, `image`).  
- **SettingsActivity.java** – UI for toggling which fields are shown.  
- **Settings.java** – Manages user preferences using `SharedPreferences`.  
- **OpenFoodFactsResponse.java** – Model class for deserializing API responses.  
- **Torchlight.java** – Handles torchlight functionality.  

## Getting Started

### Prerequisites

- Android Studio Bumblebee or later  
- Android SDK 33+  
- Gradle 8+  

### Installation

1. Clone the repository:

```bash
git clone git@github.com:rmuthukumar23/barcode-scanner-android.git

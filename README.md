# KitaHack2025 - KitaReport 📸

An application that allows users to log in via phone number and OTP to report an incident to Malaysian authorities by submitting an image captured using the application.

## Prerequisites

Before you begin, ensure you have the following installed:

* **Android Studio:** The official IDE for Android development. You can download it from [here](https://developer.android.com/studio). (Latest stable version recommended).
* **Android SDK:** Make sure you have the necessary Android SDK Platforms installed through the Android Studio SDK Manager. This project requires SDK version **[30]** or higher. *(Please update this value to match your project's actual minSdkVersion)*.


## Getting Started

Follow these steps to get a local copy up and running.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sk3007/KitaHack2025.git
    ```
    Alternatively, you can download the project as a ZIP file from GitHub and extract it.

2.  **Open the project in Android Studio:**
    * Launch Android Studio.
    * Select `File` > `Open...` (or `Open an Existing Project` from the welcome screen).
    * Navigate to the directory where you cloned or extracted the project.
    * Android Studio will automatically detect it as a Gradle project and start syncing. This might take a few minutes depending on your internet connection and system specs. Wait for the Gradle sync to complete successfully.
    * 

## Running the Application

Once the project is successfully imported, configured (if needed), and synced in Android Studio:

1.  **Select the `app` configuration:** In the toolbar near the top, ensure the `app` configuration is selected from the run configurations dropdown menu.
2.  **Choose a target device:** Select an available Android Virtual Device (AVD) from the dropdown menu next to the run configuration, or connect a physical Android device via USB with USB debugging enabled. (If you don't have an AVD, you can create one using the AVD Manager in Android Studio: `Tools` > `AVD Manager`).
3.  **Run the app:** Click the 'Run' button (the green triangle icon ▶️) or press `Shift + F10` (or `Control + R` on macOS).
4.  **Launch:** Android Studio will build the project, install the APK onto your selected device/emulator, and automatically launch the application, likely starting at the Login screen.
5.  **Login for Testing/Demonstration:**
    * On the login screen, enter the phone number: **`+60105557777`**
    * Tap the **"Send OTP"** button (or similar wording).
    * When prompted to enter the OTP, type: **`123456`**
    * Tap the **"Verify OTP"** button (or similar wording) to log in.

## Features

* **Phone Number Authentication:** Secure login/registration using a phone number and One-Time Password (OTP) verification. *(Currently uses a test phone number and OTP for demonstration)*.
* **Incident Reporting via Image:** Capture images related to an incident directly within the app for reporting purposes.
* **About Us Page:** Displays information about the application or its developers.

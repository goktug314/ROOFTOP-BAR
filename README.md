# Rooftop Cocktail Master

A native Android bartending game for a rooftop bar at night. The app is written in Kotlin with Jetpack Compose, works offline, and keeps the high score on the device.

## Game features

- A 60-second service round with a randomized customer order.
- Nine rooftop menu recipes: five drinks and four food dishes, each with ingredients and preparation steps.
- Score rules: +100 for a correct order, −20 for an incorrect order, +50 for each three-order streak, +25 for service in five seconds or less, and +100 after completing ten orders.
- A live score, timer, streak, order count, result screen, and replay button.
- A locally saved high score.
- English-only in-game interface and a custom night skyline scene.

## Build the debug APK

The GitHub Actions workflow builds the app on pushes and pull requests to `main`, and can also be started manually from the Actions tab. It uploads `app-debug.apk` as the `rooftop-cocktail-master-debug-apk` artifact.

To build locally with JDK 17 and Android SDK 36 installed:

```bash
gradle --no-daemon assembleDebug
```

The APK is created at `app/build/outputs/apk/debug/app-debug.apk`.

## Install on an Android device

Download the APK artifact from a successful GitHub Actions run, transfer it to an Android device, and open it to install. Android may ask you to allow installation from that source.

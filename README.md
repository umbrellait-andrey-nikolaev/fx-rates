# FX Rates

A small Kotlin Multiplatform app showing ECB currency rates from the
[Frankfurter API](https://frankfurter.dev).

Select a currency to see its latest available dated rate and business-day history for the last
30 UTC calendar days. The detail screen shows the change between the earliest and latest available
rates, supports retry on errors, and returns to the rates list.

## Requirements

- JDK 21
- Android Studio with support for AGP 9.5, Android SDK 37
- An emulator or a device with Android 11+ (API 30)

## Run

```bash
./gradlew :androidApp:installDebug
```

or open the project in Android Studio and run the `androidApp` configuration.

## Project rules

Start with [`AGENTS.md`](AGENTS.md). It links the architecture, screen, convention and command
documents; AI agents read it automatically (`CLAUDE.md` points to it).

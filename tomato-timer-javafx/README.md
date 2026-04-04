# TomatoTimer JavaFX

A Maven-based JavaFX rewrite of the original C# WPF **TomatoTimerWPF** project.

## Features

| Feature | Description |
|---|---|
| **Work / Break / Long Break** | Three timer modes with individually configurable durations |
| **Pause / Resume** | The work timer can be paused |
| **Reset** | Restart the current mode |
| **Compact window** | Borderless, transparent background, draggable |
| **Always on Top** | Keep the window above all others (top-right corner button) |
| **State persistence** | Timer state is saved and restored on close/reopen |
| **Sounds** | Custom sound file assignable to each event (mp3/wav/ogg) |
| **Google Calendar** | Automatically opens the event-creation page after a work session |

## Usage

- **Hover over the window** → control buttons appear
- **Relax button (hold 2 s)** → Long Break mode
- **Top-right ✕** → Save state and close
- **Settings ⚙** → Timer settings page
- **🔊 button** → Sound settings page

## Running the App

### Development mode (Maven)

```bash
cd tomato-timer-javafx
mvn javafx:run
```

### Build a fat JAR

```bash
mvn package
java -jar target/tomato-timer-1.0.0-fat.jar
```

## Requirements

- Java 21+
- Maven 3.8+

## Project structure

```
tomato-timer-javafx/
├── pom.xml
└── src/main/
    ├── java/com/tomatotimer/
    │   ├── App.java                         # JavaFX Application entry point
    │   ├── Launcher.java                    # Standalone launcher (fat-jar helper)
    │   ├── TimerMode.java                   # WORK / RELAX / RELAX_LONG enum
    │   ├── SoundType.java                   # RESUME / PAUSE / WORK_DONE / REST_TIMEOUT enum
    │   ├── AppSettings.java                 # Settings backed by java.util.prefs
    │   └── controller/
    │       ├── MainController.java          # Central controller (timer logic, navigation)
    │       ├── ButtonsController.java       # Main timer view
    │       ├── SettingsController.java      # Settings page
    │       └── SoundSettingsController.java # Sound settings page
    └── resources/com/tomatotimer/
        ├── main.fxml                        # Outer window shell
        ├── buttons.fxml                     # Timer view (progress bar + buttons)
        ├── settings.fxml                    # Settings page
        ├── sound_settings.fxml              # Sound settings page
        ├── style.css                        # Dark theme styles
        └── icons/                           # PNG icons (Android Jelly Bean set)
```

## Settings storage

The application uses the `java.util.prefs.Preferences` API for persistent settings.  
On Windows this is stored in the registry at:  
`HKCU\Software\JavaSoft\Prefs\com\tomatotimer`

## Attribution

Icons are taken from the original WPF project –
[Android 4.1 Jelly Bean Icon Set](http://palhaiz.deviantart.com/art/Android-4-1-Jelly-Bean-Icon-Set-311741892).

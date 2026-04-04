# TomatoTimer JavaFX

A Maven-based JavaFX Pomodoro timer, rewritten from the original C# WPF **TomatoTimerWPF** project.

## Features

| Feature | Description |
|---|---|
| **Work / Break / Long Break** | Three timer modes with configurable durations |
| **Pause / Resume / Reset** | Full timer control during work mode |
| **Resizable UI** | Window can be resized; fonts, icons, and controls scale dynamically |
| **Dynamic time display** | Main time text scales with window size |
| **Neon visual presets** | `Neon Balanced`, `Ultra Neon`, and `Night Runner` selectable in Settings |
| **Animated background** | Progress-aware gradient + glow changes over time and mode |
| **Vector icon set** | Modern, vivid SVG-based icons rendered in JavaFX (scalable) |
| **Always on Top** | Toggle pin button in the top-right controls |
| **State persistence** | Window and timer state are restored on next start |
| **Sounds** | Custom sound file per event (`mp3`, `wav`, `ogg`) |
| **Google Calendar** | Opens event creation after work session timeout |

## Usage

- Hover over the timer to reveal controls.
- Hold the **Relax** button for ~2 seconds to start **Long Break**.
- Open **Settings** to configure durations and switch neon preset.
- Open **Sound Settings** to assign custom notification sounds.
- Use top-right **Close** menu to exit with or without saving timer state.

## Run

### Development

```bash
mvn javafx:run
```

### Build fat JAR

```bash
mvn package
java -jar target/tomato-timer-1.0.0-fat.jar
```

## Requirements

- Java 21+
- Maven 3.8+

## Project Structure

```text
tomato-timer/
├── pom.xml
├── src/main/java/com/tomatotimer/
│   ├── App.java                          # JavaFX application entry point
│   ├── Launcher.java                     # Fat JAR launcher class
│   ├── AppSettings.java                  # Persistent settings (java.util.prefs)
│   ├── TimerMode.java                    # WORK / RELAX / RELAX_LONG
│   ├── SoundType.java                    # Notification sound event types
│   ├── NeonPreset.java                   # Neon theme presets and tuning parameters
│   ├── TimerBackgroundHelper.java        # Gradient / glow / accent color generation
│   ├── UiScaleHelper.java                # Central dynamic UI scaling calculations
│   ├── IconFactory.java                  # Scalable SVG icon factory
│   └── controller/
│       ├── MainController.java           # Main coordinator (timer, navigation, window)
│       ├── ButtonsController.java        # Timer page (controls + progress + dynamic styling)
│       ├── SettingsController.java       # Timer + theme + Google Calendar settings
│       └── SoundSettingsController.java  # Sound assignment UI and preview
└── src/main/resources/com/tomatotimer/
    ├── main.fxml                         # Main shell layout
    ├── buttons.fxml                      # Timer view
    ├── settings.fxml                     # Settings view
    ├── sound_settings.fxml               # Sound settings view
    ├── style.css                         # Base JavaFX styles
    └── icons/                            # Bitmap assets (legacy/app icon resources)
```

## Settings Storage

The app uses `java.util.prefs.Preferences` for persistence.  
On Windows, values are stored under:  
`HKCU\Software\JavaSoft\Prefs\com\tomatotimer`

## Notes

- The active in-app icon system is vector-based (`IconFactory`) and scales with UI size.
- The `archive/` folder contains the original WPF solution and historical assets.

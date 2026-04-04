# TomatoTimer JavaFX

A Maven-based JavaFX Pomodoro timer, rewritten from the original C# WPF **TomatoTimerWPF** project.

## Features

| Feature | Description |
|---|---|
| **Work / Break / Long Break** | Three timer modes with configurable durations |
| **Pause / Resume / Reset** | Full timer control during work mode |
| **Resizable UI** | Window can be resized; fonts, icons, and controls scale dynamically |
| **Dynamic time display** | Main time text scales with window size |
| **Neon visual presets** | 11 selectable themes: `Neon Balanced`, `Ultra Neon`, `Night Runner`, `Solar Flare`, `Arctic Pulse`, `Toxic Lime`, `Synth Sunset`, `Deep Ocean`, `Crimson Reactor`, `Monochrome Plasma`, `Aurora Drift` |
| **Animated background** | Progress-aware gradient + glow changes over time and mode |
| **Vector icon set** | Modern, vivid SVG-based icons rendered in JavaFX (scalable) |
| **Always on Top** | Toggle pin button in the top-right controls |
| **State persistence** | Window and timer state are restored on next start |
| **Sounds** | Custom sound file per event (`mp3`, `wav`, `ogg`) |
| **Google Calendar** | Opens event creation after work overtime or via manual **Finish Work** action |

## Usage

- Hover over the timer to reveal controls.
- Hold the **Relax** button for ~2 seconds to start **Long Break**.
- Use **Finish Work** to create a Google Calendar event from the current work interval (if enabled), then switch directly to short rest.
- Open **Settings** to configure durations and switch neon preset.
- Open **Sound Settings** to assign custom notification sounds.
- Use top-right **Close** menu to exit with or without saving timer state.

## Controls

### Control map (quick view)

```text
Window shell:
  top-right: [Pin: Always on Top] [Close menu]

Timer face (hover state):
  left:   [Settings]
  center: [Reset] [Play/Resume | Pause] [Work] [Finish Work] [Relax]

Notes:
  - Hold Relax for ~2s => Long Break
  - Finish Work => Google Calendar event (if enabled) + Short Rest
```

- **Settings**: opens timer/theme settings.
- **Reset**: restarts the current mode from full duration.
- **Play / Resume**: resumes paused work mode.
- **Pause**: pauses running work mode.
- **Work**: switches to a new work session.
- **Relax**: starts short rest; hold ~2 seconds for long break.
- **Finish Work**: creates a Google Calendar event (if enabled), then starts short rest.
- **Always on Top** (pin): toggles whether the window stays above other windows.
- **Close menu**: close with timer-state save, or close without saving timer state.

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

### Windows double-click launch

Two launcher scripts at the project root let you start the app without opening a terminal.

| File | Purpose |
|---|---|
| `TomatoTimer.vbs` | **Primary launcher** -- double-click this; starts the batch hidden (no console flicker) |
| `TomatoTimer.bat` | Core logic -- locates the newest `target\*-fat.jar`, verifies `javaw`, and launches with `start` |

**Steps**

1. Build the fat JAR once:
   ```bat
   mvn package
   ```
2. Double-click **`TomatoTimer.vbs`** in File Explorer.  
   The app opens; no console window appears.

**Verify the environment without launching** (run in a terminal):

```bat
TomatoTimer.bat --dry-run
```

Sample output when everything is ready:

```text
[DRY-RUN] JAR    : C:\...\target\tomato-timer-1.0.0-fat.jar
[DRY-RUN] JAVAW  : C:\Program Files\Eclipse Adoptium\jdk-21...\bin\javaw.exe
[DRY-RUN] Command: "...\javaw.exe" -jar "...\tomato-timer-1.0.0-fat.jar"
[DRY-RUN] OK -- environment looks good, no process was started.
```

**Error handling**

- If no fat JAR is found, a popup explains to run `mvn package`.
- If `javaw` is not on `PATH`, a popup instructs you to install Java 21+.

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

- Scalable typography and control/icon sizing are handled by `UiScaleHelper`.
- The active in-app icon system is vector-based (`IconFactory`) and scales with UI size.
- The `archive/` folder contains the original WPF solution and historical assets.

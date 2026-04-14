# TomatoTimer JavaFX

A Maven-based JavaFX Pomodoro timer, rewritten from the original C# WPF **TomatoTimerWPF** project.

## Features

| Feature | Description |
|---|---|
| **Work / Break / Long Break** | Three timer modes with configurable durations |
| **Pause / Resume / Reset** | Full timer control during work mode |
| **Resizable UI** | Window can be resized; fonts, icons, and controls scale dynamically |
| **Dynamic time display** | Main time text scales with window size |
| **Neon visual presets** | 8 selectable rainbow-spectrum themes: `Aurora Drift` (default), `Scarlet Surge`, `Citrus Spark`, `Lime Flash`, `Jade Mist`, `Ocean Glow`, `Cosmos Blaze`, `Prism Veil` |
| **Animated background** | Progress-aware gradient + glow changes over time and mode |
| **Glow intensity profiles** | 3 A/B quick-tune profiles applied on top of any preset: `Soft` (reduced glow), `Balanced` (default, no change), `Vivid` (boosted glow) |
| **Theme selection modes** | On each new **Work** phase, theme can stay fixed or rotate via `Static` / `Sequential` / `Random` / `Shuffle` |
| **Vector icon set** | Modern, vivid SVG-based icons rendered in JavaFX (scalable) |
| **Always on Top** | Toggle pin button in the top-right controls |
| **State persistence** | Window position/size and saved **Work** session state can be restored on next start |
| **Sounds** | Custom sound file per event (`mp3`, `wav`, `ogg`, `wma`) with play/stop/mute controls on the Sound Settings page |
| **Google Calendar** | Opens event creation after work overtime or via manual **Finish Work** action; configured on the dedicated Calendar Settings page |
| **Taskbar countdown icon** | Dynamic live-countdown icon in the Windows taskbar; layout (vertical/horizontal) and font size configurable on the dedicated Taskbar Settings page |
| **Taskbar preview buttons (Windows)** | Thumbnail toolbar actions: `Reset`, `Pause`, `Finish Work`, `Take a break`, `Go to Work` |

## Usage

- Hover over the timer to reveal controls.
- Hold the **Relax** button for ~2 seconds to start **Long Break**.
- Use **Finish Work** to create a Google Calendar event from the current work interval (if enabled), then switch directly to short rest.
- Open **Settings** to configure timer durations and navigate to dedicated sub-pages (**Theme**, **Calendar**, **Taskbar**, **Sound**).
- Open **Theme Settings** (from Settings) to choose neon preset, glow intensity profile, and theme selection mode.
- Open **Sound Settings** (from Settings) to assign custom notification sounds.
- Open **Calendar Settings** (from Settings) to configure Google Calendar integration.
- Open **Taskbar Settings** (from Settings) to configure the taskbar countdown icon.
- Use top-right **Close** menu to exit with or without saving timer state.

## Controls

### Control map (quick view)

```text
Window shell:
  top-right: [Pin: Always on Top] [Close menu]

Timer face (hover state):
  left:   [Settings]
  center: [Reset] [Play/Resume | Pause] [Work] [Finish Work] [Relax]

Settings page:
  left:   [Back to timer]
  right:  [Theme Settings] [Calendar Settings] [Taskbar Settings] [Sound Settings]
  center: Work / Rest / Long spinners

Windows taskbar preview (thumbnail toolbar):
  [Reset] [Pause] [Finish Work] [Take a break] [Go to Work]

Notes:
  - Hold Relax for ~2s => Long Break
  - Finish Work => Google Calendar event (if enabled) + Short Rest
```

- **Settings**: opens timer duration settings, with navigation icons to sub-pages.
- **Reset**: restarts the current mode from full duration.
- **Play / Resume**: resumes paused work mode.
- **Pause**: pauses running work mode.
- **Work**: switches to a new work session.
- **Relax**: starts short rest; hold ~2 seconds for long break.
- **Finish Work**: creates a Google Calendar event (if enabled), then starts short rest.
- **Always on Top** (pin): toggles whether the window stays above other windows.
- **Close menu**: close with timer-state save, or close without saving timer state.

## Settings Pages

### Main Settings (`settings.fxml`)

Configures timer durations (Work / Rest / Long Rest).
Navigation icon buttons in the top-right open the dedicated sub-pages in this order:
**Theme**, **Calendar**, **Taskbar**, **Sound**.

### Theme Settings (`theme_settings.fxml`)

| Control | Purpose |
|---|---|
| **Theme** | Selects the active neon preset (`Aurora Drift` ... `Prism Veil`) |
| **Glow** | Selects the global glow profile (`Soft`, `Balanced`, `Vivid`) |
| **Selection** | Defines how theme changes at each new **Work** start (`Static`, `Sequential`, `Random`, `Shuffle`) |

Preset and Glow changes apply immediately and refresh the timer face in the background.
Selection mode is persisted and applied when the next **Work** phase starts.
Back button returns to the main Settings page.

### Glow Intensity Profiles

A global **glow profile** can be combined with any neon preset to adjust the visual intensity
without duplicating preset data.  The profile is selected on the dedicated **Theme Settings** page
via the **Glow** combo and is persisted in `java.util.prefs.Preferences`
under the key `neon_glow_profile` (managed by `AppSettings`).

| Profile | Effect |
|---|---|
| **Soft** | Reduced alpha/opacity (×0.65), spread (×0.70), sheen (×0.65), white-blend (×0.70); slightly heavier darken (×1.15) — calmer, less distracting appearance. |
| **Balanced** | All factors = 1.00 — presets are used verbatim. **This is the application default.** |
| **Vivid** | Boosted alpha/opacity (×1.30), spread (×1.25), sheen (×1.25), white-blend (×1.25); slightly lighter darken (×0.85) — brighter, more saturated neon effect. |

Scale factors are applied by `TimerBackgroundHelper` at render-time (root gradient + bar glow),
so existing `NeonPreset` constants remain unchanged.  All scaled values are clamped to `[0, 1]`.

### Theme Selection Modes

`ThemeSelectionMode` controls how the next theme is picked when a new **Work** phase starts:

| Mode | Behavior |
|---|---|
| **Static** | Keep the currently selected preset |
| **Sequential** | Move to the next preset in enum order, wrapping at the end |
| **Random** | Pick a random preset that differs from the current one |
| **Shuffle** | Walk a randomized non-repeating cycle of all presets, then reshuffle (**default**) |

### Calendar Settings (`calendar_settings.fxml`)

| Control | Purpose |
|---|---|
| **Enable Google Calendar** | Activates event creation on Finish Work / overtime |
| **Copy URL to clipboard** | Also copies the generated GCal URL to the system clipboard |
| **Calendar source** | Calendar ID / source string embedded in the event URL |
| **Event title** | Default title for created calendar events |
| **Test** (calendar icon) | Opens a test event in the browser using the current work duration |

Back button returns to the main Settings page.

### Sound Settings (`sound_settings.fxml`)

For each event slot (**Resume**, **Pause**, **Work Done**, **Rest End**), you can:

- **Play** preview audio
- **Stop** current preview playback
- **Choose file** (`.mp3`, `.wav`, `.ogg`, `.wma`)
- **Mute** that slot

Back button returns to the main Settings page.

### Taskbar Settings (`taskbar_settings.fxml`)

| Control | Purpose |
|---|---|
| **Enable taskbar icon** | Toggles the live-countdown icon in the Windows taskbar (**default: enabled**) |
| **Font size** | Base font size (px at the 64 px reference canvas, range 8–28, default 23) |
| **Layout — Vertical** | Stacked multi-line: 2 lines (MM / SS) when hours = 0; 3 lines (HH / MM / SS) when hours > 0 |
| **Layout — Horizontal** | Single-line: `MM:SS` when hours = 0; `HH:MM:SS` when hours > 0 |

Changes to layout and font size take effect on the next icon redraw (within ~1 second, or immediately on Back).
Back button saves settings, triggers an immediate icon redraw, and returns to the main Settings page.

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
[DRY-RUN] Command: "...\javaw.exe" --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED -jar "...\tomato-timer-1.0.0-fat.jar"
[DRY-RUN] OK -- environment looks good, no process was started.
```

**Error handling**

- If no fat JAR is found, the script prints an error and asks you to run `mvn package`.
- If `javaw` is not on `PATH`, the script prints an error and asks you to install Java 21+.

### Windows EXE wrapper (jpackage)

`BuildTomatoTimerExe.bat` uses `jpackage` (bundled with JDK 14+) to produce a
self-contained **app-image** — a folder with a native `TomatoTimer.exe` and a
bundled JRE.  No Java installation is required on the target machine.

**Prerequisites**

- JDK 21+ (project baseline; includes `jpackage`) — its `bin\` directory must be on `PATH`.
- Maven 3.8+ on `PATH`.

**Steps**

1. Verify the environment without building (dry-run):
   ```bat
   BuildTomatoTimerExe.bat --dry-run
   ```
2. Build the app-image:
   ```bat
   BuildTomatoTimerExe.bat
   ```

`BuildTomatoTimerExe.bat` now runs `mvn -DskipTests package` automatically before
calling `jpackage`, so the generated EXE always packages the latest sources instead
of a stale `target\*-fat.jar`.

The resulting executable is placed at:

```text
target\exe-image\TomatoTimer\TomatoTimer.exe
```

> **Tip — Pin to Taskbar:** `TomatoTimer.exe` is the best candidate to pin to
> the Windows Taskbar.  Right-click the EXE in File Explorer and choose
> **"Pin to taskbar"**.

Sample dry-run output when everything is ready:

```text
[DRY-RUN] JAR        : C:\...\target\tomato-timer-1.0.0-fat.jar
[DRY-RUN] JAR name   : tomato-timer-1.0.0-fat.jar
[DRY-RUN] JPACKAGE   : C:\Program Files\Eclipse Adoptium\jdk-21...\bin\jpackage.exe
[DRY-RUN] STAGING    : C:\...\target\jpackage-input  (fat JAR only)
[DRY-RUN] DEST       : C:\...\target\exe-image
[DRY-RUN] Output EXE : C:\...\target\exe-image\TomatoTimer\TomatoTimer.exe
[DRY-RUN] Command:
          "...\jpackage.exe"
              --type app-image
              --name TomatoTimer
              --input "...\target\jpackage-input"
              --main-jar "tomato-timer-1.0.0-fat.jar"
              --main-class com.tomatotimer.Launcher
              --dest "...\target\exe-image"
              --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED"
              --java-options "--add-opens=java.base/java.io=ALL-UNNAMED"
[DRY-RUN] Note: = (not space) in --add-opens is required -- jpackage splits
[DRY-RUN]       space-separated java-options values into broken cfg lines.
[DRY-RUN] Note: jpackage auto-injects -Djpackage.app-version from --app-version.
[DRY-RUN] OK -- environment looks good, no build was started.
```

### EXE launch troubleshooting

#### EXE opens and immediately closes / nothing appears

The most common cause is a broken `TomatoTimer.cfg` generated by older jpackage
builds that used the **space form** of `--add-opens`.

**Symptom** — `app\TomatoTimer.cfg` contains split entries like this:

```ini
java-options=--add-opens
java-options=java.base/java.lang=ALL-UNNAMED
```

**Why it breaks** — jpackage on JDK ≤ 21.x splits any `--java-options` value on
spaces when writing the `.cfg` file.  The native app launcher passes each
`java-options=` line as a separate, standalone argument to the JVM through the
JNI invocation API.  Receiving `--add-opens` without its `module/package=target`
value is a fatal JVM error:

```text
Error: --add-opens requires a <module>/<package>=<target-module> specification
```

The JVM exits before `main()` is ever reached, producing no visible window.

**Fix A — patch the existing cfg directly** (no rebuild needed):

Open `target\exe-image\TomatoTimer\app\TomatoTimer.cfg` and replace the split
entries with the single-token `=` form:

```ini
[JavaOptions]
java-options=-Djpackage.app-version=1.0
java-options=--add-opens=java.base/java.lang=ALL-UNNAMED
java-options=--add-opens=java.base/java.io=ALL-UNNAMED
```

**Fix B — rebuild** (picks up the corrected `BuildTomatoTimerExe.bat`):

```bat
mvn package
BuildTomatoTimerExe.bat
```

`BuildTomatoTimerExe.bat` now passes `--add-opens=` (equals form) to jpackage,
which produces a correct single-token cfg entry that the JVM accepts.

#### Verifying the cfg after a build

```bat
type target\exe-image\TomatoTimer\app\TomatoTimer.cfg
```

Each `--add-opens` must appear on **one line** with its `module/package=target`
value joined by `=`:

```ini
java-options=--add-opens=java.base/java.lang=ALL-UNNAMED   ← correct
java-options=--add-opens                                    ← broken (split)
```

## Windows Taskbar Icon

`WindowsNativeWindowIconHelper` sets the native taskbar and window icon on Windows by
writing a temporary ICO file and calling `LoadImageW` / `WM_SETICON` through JNA.
A CRC32-based cache prevents redundant SSD writes when the icon data has not changed.

The **live countdown icon** is rendered by `TaskbarIconRenderer` directly onto a JavaFX
`Canvas` (no AWT / BufferedImage) and pushed to `Stage.getIcons()` every second.
Layout and font size are controlled via the **Taskbar Settings** sub-page (see above).

`WindowsTaskbarPreviewButtonsHelper` adds clickable thumbnail-toolbar controls on
Windows taskbar preview popups (`Reset`, `Pause`, `Finish Work`, `Take a break`, `Go to Work`).

### Taskbar Icon Debug Mode

#### Quick Start (IntelliJ)

> Profiles are pre-versioned under `.run/` — IntelliJ IDEA detects them automatically when you open the project.

1. Open the **Run / Debug** dropdown in the toolbar (top-right of the IDE).
2. Select **`TomatoTimer Debug (App)`** *(recommended)* or `TomatoTimer Debug (Maven javafx:run)`.
3. Click **Run ▶** or **Debug 🐛** and look for `FINE` log output from `com.tomatotimer.WindowsNativeWindowIconHelper`.

#### Quick Start (Console)

```bash
# Maven — JAVA_TOOL_OPTIONS propagates to the forked child JVM spawned by javafx:run
JAVA_TOOL_OPTIONS="-Dtomatotimer.icon.debug=true -Djava.util.logging.config.file=logging.properties" mvn javafx:run
```

```bash
# Direct JAR
java -Dtomatotimer.icon.debug=true -Djava.util.logging.config.file=logging.properties -jar target/tomato-timer-1.0.0-fat.jar
```

---

Enable verbose trace logging by setting the JVM property `-Dtomatotimer.icon.debug=true`.

**Direct JAR launch**

```bash
java -Dtomatotimer.icon.debug=true -jar target/tomato-timer-1.0.0-fat.jar
```

**Maven JavaFX run**

```bash
MAVEN_OPTS="-Dtomatotimer.icon.debug=true" mvn javafx:run
```

> **Note:** `MAVEN_OPTS` sets properties on the Maven process itself.
> `javafx:run` (plugin version 0.0.8) forks a separate child JVM, so properties in
> `MAVEN_OPTS` are **not** forwarded to it and the flag will have no effect.
> Use `JAVA_TOOL_OPTIONS` instead — it is an OS-level environment variable that every
> JVM process inherits, including forked ones:

```bash
# Reliable: propagates to the forked child JVM spawned by javafx:run
JAVA_TOOL_OPTIONS="-Dtomatotimer.icon.debug=true" mvn javafx:run
```

**IntelliJ IDEA run configuration**

1. Open **Run › Edit Configurations…**
2. Choose an existing configuration or create a new one:
   - **Application** — set *Main class* to `com.tomatotimer.Launcher`
   - **Maven** — set *Command line* to `javafx:run`
3. In the **VM options** field add:
   ```
   -Dtomatotimer.icon.debug=true
   ```
4. *(Optional)* To surface `FINE` log output, also add:
   ```
   -Djava.util.logging.config.file=logging.properties
   ```
   Make sure `logging.properties` exists at the project root (see template below).
5. Click **OK** and run the configuration.

**Versioned IntelliJ run profiles (`.run/`)**

The project ships two shared run configurations under `.run/`.
IntelliJ IDEA auto-detects them when you open the project — no manual setup required.

| Profile name | Type | What it runs |
|---|---|---|
| `TomatoTimer Debug (App)` | Application | Launches `com.tomatotimer.Launcher` directly via the JVM |
| `TomatoTimer Debug (Maven javafx:run)` | Maven | Runs `mvn javafx:run`; sets `JAVA_TOOL_OPTIONS` so debug flags reach the forked JVM |

Both profiles pre-configure:
- `-Dtomatotimer.icon.debug=true`
- `-Djava.util.logging.config.file=logging.properties`

**Steps**

1. Open the **Run / Debug** dropdown in the toolbar (top-right of the IDE).
2. Select `TomatoTimer Debug (App)` or `TomatoTimer Debug (Maven javafx:run)`.
3. Click **Run ▶** or **Debug 🐛**.

Logs are written at `FINE` level under the logger
`com.tomatotimer.WindowsNativeWindowIconHelper` via `java.util.logging` (JUL).
Depending on your environment's JUL configuration, `FINE` messages may not appear by default.

Key log points: skip reasons · HWND resolution · CRC cache hit/miss ·
temp ICO write · `LoadImageW` result · `WM_SETICON` broadcast · `DestroyIcon` result.

**Minimal `logging.properties` to surface `FINE` output for this logger only**

```properties
handlers=java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.level=FINE
com.tomatotimer.WindowsNativeWindowIconHelper.level=FINE
```

Pass it to the JVM with `-Djava.util.logging.config.file=logging.properties`:

```bash
java -Dtomatotimer.icon.debug=true \
     -Djava.util.logging.config.file=logging.properties \
     -jar target/tomato-timer-1.0.0-fat.jar
```

### If you see no logs

Even with `-Dtomatotimer.icon.debug=true` set, JUL's default configuration only surfaces
`INFO` and above — `FINE` messages are silently dropped when no `logging.properties` is
supplied.

Starting from this version, the code-level bootstrap in `WindowsNativeWindowIconHelper`
handles this automatically: when debug mode is active, the class logger's level is raised
to `FINE` and a `ConsoleHandler` at `FINE` level is attached if one is not already present
in the logger hierarchy.  No external configuration file is required.

If logs still do not appear, check the following:

| Possible cause | Fix |
|---|---|
| `MAVEN_OPTS` used with `mvn javafx:run` | Switch to `JAVA_TOOL_OPTIONS` (see above) |
| Property typo | Verify the exact flag: `-Dtomatotimer.icon.debug=true` |
| Non-Windows platform | The helper is a no-op on non-Windows; check the `apply: skipped` message |
| IDE run config missing VM option | Add `-Dtomatotimer.icon.debug=true` to **VM options** (not program arguments) |

## Requirements

- Java 21+
- Maven 3.8+

## Project Structure

```text
tomato-timer/
├── BuildTomatoTimerExe.bat
├── TomatoTimer.bat
├── TomatoTimer.vbs
├── pom.xml
├── .run/
│   ├── TomatoTimer_Debug_App.run.xml
│   └── TomatoTimer_Debug_Maven_javafx_run.run.xml
├── src/main/java/com/tomatotimer/
│   ├── App.java                          # JavaFX application entry point
│   ├── Launcher.java                     # Fat JAR launcher class
│   ├── AppSettings.java                  # Persistent settings (java.util.prefs)
│   ├── TimerMode.java                    # WORK / RELAX / RELAX_LONG
│   ├── SoundType.java                    # Notification sound event types
│   ├── NeonPreset.java                   # Neon theme presets and tuning parameters
│   ├── NeonGlowProfile.java              # SOFT / BALANCED / VIVID glow intensity multipliers
│   ├── ThemeSelectionMode.java           # STATIC / SEQUENTIAL / RANDOM / SHUFFLE theme rotation mode
│   ├── TaskbarTimeLayout.java            # VERTICAL / HORIZONTAL enum for taskbar icon
│   ├── TaskbarIconRenderer.java          # Canvas-based live countdown icon renderer
│   ├── TimerBackgroundHelper.java        # Gradient / glow / accent color generation
│   ├── UiScaleHelper.java                # Central dynamic UI scaling calculations
│   ├── IconFactory.java                  # Scalable SVG icon factory
│   ├── WindowsNativeWindowIconHelper.java# JNA-based native taskbar/window icon setter
│   ├── WindowsTaskbarPreviewButtonsHelper.java # Windows taskbar preview (thumbnail) action buttons
│   ├── WindowsAppIdHelper.java           # Windows AppUserModelID helper
│   └── controller/
│       ├── MainController.java           # Main coordinator (timer, navigation, window)
│       ├── ButtonsController.java        # Timer page (controls + progress + dynamic styling)
│       ├── SettingsController.java       # Timer durations; nav to dedicated sub-pages
│       ├── ThemeSettingsController.java  # Theme preset + glow profile + selection mode
│       ├── CalendarSettingsController.java # Google Calendar integration settings
│       ├── SoundSettingsController.java  # Sound assignment UI and preview
│       └── TaskbarSettingsController.java  # Taskbar icon layout, font size, enable toggle
└── src/main/resources/com/tomatotimer/
    ├── main.fxml                         # Main shell layout
    ├── buttons.fxml                      # Timer view
    ├── settings.fxml                     # Main settings view (durations + sub-page nav)
    ├── theme_settings.fxml               # Theme settings sub-page (preset + glow + selection mode)
    ├── calendar_settings.fxml            # Google Calendar settings sub-page
    ├── sound_settings.fxml               # Sound settings sub-page
    ├── taskbar_settings.fxml             # Taskbar icon settings sub-page
    ├── style.css                         # Base JavaFX styles
    └── icons/                            # Bitmap assets (legacy/app icon resources)
```

## Settings Storage

The app uses `java.util.prefs.Preferences` for persistence.
On Windows, values are stored under:
`HKCU\Software\JavaSoft\Prefs\com\tomatotimer`

### Important Windows registry keys

The most relevant persisted keys are:

| Registry key | Meaning | Current factory default |
|---|---|---|
| `theme_selection_mode` | Theme rotation mode used when a new **Work** phase starts | `SHUFFLE` |
| `gcal_enable` | Whether Google Calendar integration is enabled | `true` |
| `taskbar_icon_enable` | Whether the live taskbar countdown icon is enabled | `true` |
| `taskbar_font_size` | Base font size for the taskbar icon renderer | `23.0` |
| `settings_version` | Internal settings schema version used for startup migration | `3` |

### Factory defaults / first-run defaults

Complete list of every key persisted by `AppSettings`, the Java type used for storage,
and the factory default that is written on the very first run (or after a migration bump).
Keys marked **†** are **not** overwritten by the schema migration — they are treated as
user-specific / runtime state and are only written when the user changes them explicitly.

| Registry key | Type | Factory default | Description |
|---|---|---|---|
| `work_time` | `int` | `25` | Work timer duration (minutes) |
| `relax_time` | `int` | `5` | Short rest duration (minutes) |
| `relax_time_long` | `int` | `15` | Long rest duration (minutes) |
| `gcal_enable` | `boolean` | `true` | Google Calendar integration enabled |
| `gcal_copy_clipboard` | `boolean` | `false` | Also copy generated GCal URL to clipboard |
| `gcal_src` **†** | `string` | *(empty)* | Calendar source / ID embedded in event URL |
| `gcal_text` **†** | `string` | *(empty)* | Default title for created calendar events |
| `always_on_top` | `boolean` | `false` | Window stays above all other windows |
| `window_x` **†** | `double` | `-1` | Saved window X position; `-1` = let OS decide |
| `window_y` **†** | `double` | `-1` | Saved window Y position; `-1` = let OS decide |
| `window_width` **†** | `double` | `260` | Saved window width (logical px) |
| `window_height` **†** | `double` | `44` | Saved window height (logical px) |
| `timer_restore_datetime` **†** | `string` | *(empty)* | ISO datetime of the saved Work session for restore |
| `timer_restore_mode` **†** | `int` | `0` | Saved timer-mode ordinal (`0` = Work) |
| `sound_resume` **†** | `string` | *(empty)* | Absolute path to sound file for Resume event |
| `sound_pause` **†** | `string` | *(empty)* | Absolute path to sound file for Pause event |
| `sound_work_done` **†** | `string` | *(empty)* | Absolute path to sound file for Work Done event |
| `sound_rest_timeout` **†** | `string` | *(empty)* | Absolute path to sound file for Rest End event |
| `neon_preset` | `string` | `AURORA_DRIFT` | Active neon color preset |
| `neon_glow_profile` | `string` | `BALANCED` | Active glow intensity profile |
| `taskbar_icon_enable` | `boolean` | `true` | Live countdown taskbar icon enabled |
| `taskbar_font_size` | `double` | `23.0` | Taskbar icon font size (at 64 px reference canvas; range 8–28) |
| `taskbar_layout` | `string` | `VERTICAL` | Taskbar countdown layout (`VERTICAL` / `HORIZONTAL`) |
| `theme_selection_mode` | `string` | `SHUFFLE` | Theme rotation strategy on each new Work phase |
| `settings_version` | `int` | `3` | Internal schema version; drives the startup migration |

> **†** User-specific / runtime keys: the startup migration never overwrites these —
> only window geometry, sound file paths, GCal source/text, and timer-restore state
> fall into this category.

### Why changed defaults may not appear immediately on an existing machine

`java.util.prefs.Preferences` on Windows stores values in the current user's registry.
If an older build has already written a value once, changing the default in source code
alone is not enough, because the stored registry value takes precedence.

To handle this, `AppSettings` performs a startup migration using the `settings_version`
key. This lets the built `TomatoTimer.exe` update selected factory defaults for existing
users as well.

### Manual verification

List the whole settings branch:

```bat
reg query "HKCU\Software\JavaSoft\Prefs\com\tomatotimer" /s
```

Typical values to verify after launching the app:

- `work_time` = `25`
- `always_on_top` = `false`
- `theme_selection_mode` = `SHUFFLE`
- `gcal_enable` = `true`
- `taskbar_icon_enable` = `true`
- `taskbar_font_size` = `23.0`
- `settings_version` = `3`

### Simulating a clean first run

To remove the entire per-user settings branch for this app:

```bat
reg delete "HKCU\Software\JavaSoft\Prefs\com\tomatotimer" /f
```

Use this only when you intentionally want to reset all saved TomatoTimer settings
for the current Windows user.

## Notes

- Scalable typography and control/icon sizing are handled by `UiScaleHelper`.
- The active in-app icon system is vector-based (`IconFactory`) and scales with UI size.
- The `archive/` folder contains the original WPF solution and historical assets.

## Manual QA Checklist (Windows Taskbar Icon)

### Goal

Validate that the Windows taskbar icon shows the live countdown (instead of the Java icon)
and that its appearance follows the currently active theme.

### Preconditions

- Windows environment
- App starts via `TomatoTimer.exe` or fat JAR
- Taskbar icon setting is available in `Settings -> Taskbar Settings`

### Test Steps

#### 1) First icon after app start

- [ ] Start the app (`TomatoTimer.exe` or fat JAR).
- [ ] Check the taskbar icon immediately after the window appears.

**Expected**

- The Java icon is not shown.
- The countdown icon appears on startup (immediately or within ~1 second).

---

#### 2) Enable/Disable toggle behavior

- [ ] Open `Settings -> Taskbar Settings`.
- [ ] Disable `Enable taskbar icon`.
- [ ] Check the taskbar icon.
- [ ] Re-enable `Enable taskbar icon`.
- [ ] Check the taskbar icon again.

**Expected**

- On disable, the icon reverts to the default app/process icon.
- On re-enable, the countdown icon returns immediately (or within ~1 second).

---

#### 3) Theme-dependent color update

- [ ] Open `Settings -> Theme Settings`.
- [ ] Change preset (for example `Aurora Drift` -> `Scarlet Surge`).
- [ ] Observe the taskbar icon accent color.
- [ ] Change glow profile (`Soft`, `Balanced`, `Vivid`).

**Expected**

- Taskbar icon accent follows the active theme/preset.
- Visual intensity remains consistent with the current UI style.

---

#### 4) Correct timer phase mapping

- [ ] Run `Work` mode.
- [ ] Switch to `Relax` mode.
- [ ] Start long relax (long-press relax button).

**Expected**

- Taskbar icon always shows the countdown for the currently active phase.

---

#### 5) Layout and font-size validation

- [ ] In `Taskbar Settings`, switch to `Vertical`.
- [ ] Verify icon text layout.
- [ ] Switch to `Horizontal`.
- [ ] Verify icon text layout.
- [ ] Change `Font size` (for example `12` -> `22`).

**Expected**

- `Vertical`: 2 lines (`MM` / `SS`) or 3 lines (`HH` / `MM` / `SS`).
- `Horizontal`: `MM:SS` or `HH:MM:SS`.
- Font-size change is visible on the icon.

---

#### 6) Pause/Resume/Reset consistency

- [ ] Press Pause.
- [ ] Press Resume.
- [ ] Press Reset.

**Expected**

- Taskbar countdown updates remain consistent with each state transition.

---

#### 7) Restart regression check

- [ ] Close the app.
- [ ] Start it again.

**Expected**

- On restart, countdown icon appears again (not the Java icon).

### If a failure occurs, record

- Launch mode: `TomatoTimer.exe` or fat JAR
- Exact failed step number
- Expected vs actual behavior
- Optional screenshot of the taskbar icon

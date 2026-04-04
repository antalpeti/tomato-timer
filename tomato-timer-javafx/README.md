# TomatoTimer JavaFX

Maven-alapú JavaFX átírása az eredeti C# WPF **TomatoTimerWPF** projektnek.

## Funkciók

| Funkció | Leírás |
|---|---|
| **Munka / Pihenő / Hosszú pihenő** | Három időzítő mód, egyénileg beállítható perc-értékekkel |
| **Szünet / Folytatás** | A munka időzítő szüneteltethető |
| **Visszaállítás** | Az aktuális mód újraindítása |
| **Kompakt ablak** | Keret nélküli, átlátszó háttérrel, fogódézható |
| **Mindig felül** | Ablak tetején tartása (jobb felső sarok gombja) |
| **Állapot mentése** | Bezáráskor a timer állapota megmarad |
| **Hangok** | Egyéni hangfájl rendelhetű minden eseményhez (mp3/wav/ogg) |
| **Google Calendar** | Automatikusan megnyitja az eseménykészítő oldalt munka után |

## Kezelés

- **Hover az ablakra** → megjelennek a vezérlő gombok
- **Relax gomb (⏱ 2 mp lenyomva tartva)** → Hosszú pihenő mód
- **Jobb felső ✕** → Mentés és bezárás
- **Beállítások ⚙** → Időzítő beállítások oldal
- **🔊 gomb** → Hang beállítások oldal

## Indítás

### Fejlesztői módban (Maven)

```bash
cd tomato-timer-javafx
mvn javafx:run
```

### Fat-JAR készítése

```bash
mvn package
java -jar target/tomato-timer-1.0.0-fat.jar
```

## Előfeltételek

- Java 21+
- Maven 3.8+

## Projekt struktúra

```
tomato-timer-javafx/
├── pom.xml
└── src/main/
    ├── java/com/tomatotimer/
    │   ├── App.java                    # JavaFX Application belépési pont
    │   ├── Launcher.java               # Standalone launcher
    │   ├── TimerMode.java              # WORK / RELAX / RELAX_LONG enum
    │   ├── SoundType.java              # RESUME / PAUSE / WORK_DONE / REST_TIMEOUT enum
    │   ├── AppSettings.java            # java.util.prefs beállítás-kezelő
    │   └── controller/
    │       ├── MainController.java     # Fő vezérlő (timer logika, navigáció)
    │       ├── ButtonsController.java  # Fő időzítő nézet
    │       ├── SettingsController.java # Beállítások oldal
    │       └── SoundSettingsController.java  # Hang beállítások oldal
    └── resources/com/tomatotimer/
        ├── main.fxml                   # Fő ablak shell
        ├── buttons.fxml                # Timer nézet (progress bar + gombok)
        ├── settings.fxml               # Beállítások oldal
        ├── sound_settings.fxml         # Hang beállítások oldal
        ├── style.css                   # Sötét téma stílusok
        └── icons/                      # PNG ikonok (Android Jelly Bean)
```

## Beállítások tárolása

Az alkalmazás a `java.util.prefs.Preferences` API-t használja a beállítások perzisztálásához.
Windows rendszeren ez a registry-ben tárolódik:
`HKCU\Software\JavaSoft\Prefs\com\tomatotimer`

## Attribúció

Az ikonok az eredeti WPF projektből származnak –
[Android 4.1 Jelly Bean Icon Set](http://palhaiz.deviantart.com/art/Android-4-1-Jelly-Bean-Icon-Set-311741892).


package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.NeonPreset;
import com.tomatotimer.SoundType;
import com.tomatotimer.TaskbarIconRenderer;
import com.tomatotimer.TaskbarTimeLayout;
import com.tomatotimer.TimerBackgroundHelper;
import com.tomatotimer.TimerMode;
import com.tomatotimer.UiScaleHelper;
import com.tomatotimer.WindowsNativeWindowIconHelper;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;
import java.awt.Taskbar;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for main.fxml – central coordinator, equivalent to MainWindow.xaml.cs.
 */
public class MainController {

    @FXML private StackPane    rootPane;
    @FXML private Pane         contentPane;
    @FXML private HBox         windowControlPane;
    @FXML private ToggleButton btnAlwaysOnTop;
    @FXML private MenuButton   btnClose;

    private Stage        stage;
    private AppSettings  settings = AppSettings.getInstance();

    // ---- timer state --------------------------------------------------------
    private TimerMode      mode             = TimerMode.WORK;
    private boolean        isPaused         = false;
    private boolean        isOverTime       = false;
    private LocalDateTime  timerStartTime   = LocalDateTime.now();
    private LocalDateTime  pauseStartTime   = LocalDateTime.now();
    private long           pausedMillisTotal= 0;
    private long           timerElapsedWhenPaused = 0;

    // ---- sub-views ----------------------------------------------------------
    private Node                     buttonsView;
    private Node                     settingsView;
    private Node                     soundSettingsView;
    private Node                     taskbarSettingsView;
    private ButtonsController        buttonsController;
    private SettingsController       settingsController;
    private SoundSettingsController  soundSettingsController;
    private TaskbarSettingsController taskbarSettingsController;

    // ---- drag support -------------------------------------------------------
    private double dragBaseX, dragBaseY;
    private boolean dragging = false;

    // ---- resize support for transparent stage -------------------------------
    private static final double RESIZE_MARGIN = 6.0;
    private ResizeZone activeResizeZone = ResizeZone.NONE;
    private double resizeStartScreenX;
    private double resizeStartScreenY;
    private double resizeStartStageX;
    private double resizeStartStageY;
    private double resizeStartStageWidth;
    private double resizeStartStageHeight;

    // ---- window-controls fade -----------------------------------------------
    private FadeTransition controlsFadeIn;
    private FadeTransition controlsFadeOut;

    // ── SVG icon references for window controls ───────────────────────────────
    private Group iconPin;
    private Group iconClose;

    // ---- one-second clock ---------------------------------------------------
    private Timeline clock;

    // ---- taskbar icon cache (avoids redraw when text + colour + settings unchanged) ----
    private String            lastIconText     = "";
    private Color             lastIconColor    = null;
    private TaskbarTimeLayout lastIconLayout   = null;
    private double            lastIconFontSize = -1.0;

    // =========================================================================
    //  Initialisation
    // =========================================================================

    /** Called by App.start() after the stage is configured. */
    public void init(Stage stage) {
        this.stage = stage;

        // Restore window position / size
        if (settings.getWindowX() >= 0) stage.setX(settings.getWindowX());
        if (settings.getWindowY() >= 0) stage.setY(settings.getWindowY());
        stage.setWidth(settings.getWindowWidth());
        stage.setHeight(settings.getWindowHeight());
        stage.setAlwaysOnTop(settings.isAlwaysOnTop());

        // Load sub-views
        try {
            FXMLLoader bl = new FXMLLoader(getClass().getResource("/com/tomatotimer/buttons.fxml"));
            buttonsView = bl.load();
            buttonsController = bl.getController();
            buttonsController.setMainController(this);

            FXMLLoader sl = new FXMLLoader(getClass().getResource("/com/tomatotimer/settings.fxml"));
            settingsView = sl.load();
            settingsController = sl.getController();
            settingsController.setMainController(this);

            FXMLLoader ss = new FXMLLoader(getClass().getResource("/com/tomatotimer/sound_settings.fxml"));
            soundSettingsView = ss.load();
            soundSettingsController = ss.getController();
            soundSettingsController.setMainController(this);

            FXMLLoader ts = new FXMLLoader(getClass().getResource("/com/tomatotimer/taskbar_settings.fxml"));
            taskbarSettingsView = ts.load();
            taskbarSettingsController = ts.getController();
            taskbarSettingsController.setMainController(this);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load sub-views", e);
        }

        // Restore timer state
        String restoreDt = settings.getTimerRestoreDateTime();
        if (!restoreDt.isEmpty()) {
            try {
                timerStartTime = LocalDateTime.parse(restoreDt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                mode = TimerMode.fromValue(settings.getTimerRestoreMode());
            } catch (Exception ignored) {
                timerStartTime = LocalDateTime.now();
            }
        }
        pauseStartTime = LocalDateTime.now();

        // Window controls fade animations
        windowControlPane.setOpacity(0);
        controlsFadeIn  = createFade(windowControlPane, 0, 1, 150);
        controlsFadeOut = createFade(windowControlPane, 1, 0, 150);

        // ── SVG icons for window controls ─────────────────────────────────────
        iconPin   = IconFactory.create(IconFactory.PATH_PIN,   IconFactory.COLOR_PIN);
        iconClose = IconFactory.create(IconFactory.PATH_CLOSE, IconFactory.COLOR_CLOSE);

        // Sync initial selected state and pin colour
        btnAlwaysOnTop.setSelected(settings.isAlwaysOnTop());
        final String initialPinColor = settings.isAlwaysOnTop()
                ? IconFactory.COLOR_PIN_ON : IconFactory.COLOR_PIN;
        IconFactory.recolor(iconPin, initialPinColor);

        btnAlwaysOnTop.setGraphic(iconPin);
        btnClose.setGraphic(iconClose);

        // Update pin colour whenever toggle state changes
        btnAlwaysOnTop.selectedProperty().addListener((obs, ov, selected) ->
            IconFactory.recolor(iconPin, selected ? IconFactory.COLOR_PIN_ON : IconFactory.COLOR_PIN));

        // Scale window-control icons with window height
        final double initWcSize = clampWCtrl(stage.getHeight());
        IconFactory.resize(iconPin,   initWcSize);
        IconFactory.resize(iconClose, initWcSize);
        rootPane.heightProperty().addListener((obs, ov, nv) -> {
            final double sz = clampWCtrl(nv.doubleValue());
            IconFactory.resize(iconPin,   sz);
            IconFactory.resize(iconClose, sz);
        });

        // Mouse-enter / leave → show / hide window controls
        rootPane.setOnMouseEntered(e -> { controlsFadeOut.stop(); controlsFadeIn.playFromStart(); });
        rootPane.setOnMouseExited (e -> {
            controlsFadeIn.stop();
            controlsFadeOut.playFromStart();
            if (activeResizeZone == ResizeZone.NONE) {
                rootPane.setCursor(Cursor.DEFAULT);
            }
        });

        rootPane.setOnMouseMoved(e -> {
            if (activeResizeZone != ResizeZone.NONE) return;
            rootPane.setCursor(detectResizeZone(e.getX(), e.getY()).cursor);
        });

        // Drag to move window
        rootPane.setOnMousePressed(e -> {
            activeResizeZone = detectResizeZone(e.getX(), e.getY());

            resizeStartScreenX = e.getScreenX();
            resizeStartScreenY = e.getScreenY();
            resizeStartStageX = stage.getX();
            resizeStartStageY = stage.getY();
            resizeStartStageWidth = stage.getWidth();
            resizeStartStageHeight = stage.getHeight();

            dragBaseX = e.getScreenX() - stage.getX();
            dragBaseY = e.getScreenY() - stage.getY();
            dragging  = false;
        });
        rootPane.setOnMouseDragged(e -> {
            if (activeResizeZone != ResizeZone.NONE) {
                resizeWindow(e.getScreenX(), e.getScreenY());
                return;
            }

            double dx = e.getScreenX() - dragBaseX - stage.getX();
            double dy = e.getScreenY() - dragBaseY - stage.getY();
            if (!dragging && (Math.abs(dx) > 5 || Math.abs(dy) > 5)) dragging = true;
            if (dragging) {
                stage.setX(e.getScreenX() - dragBaseX);
                stage.setY(e.getScreenY() - dragBaseY);
            }
        });
        rootPane.setOnMouseReleased(e -> {
            activeResizeZone = ResizeZone.NONE;
            rootPane.setCursor(detectResizeZone(e.getX(), e.getY()).cursor);
        });

        // Close-request: save state
        stage.setOnCloseRequest(e -> { saveStateAndClose(true); e.consume(); });

        // Show buttons view
        showButtons();

        // Start the 1-second clock
        clock = new Timeline(new KeyFrame(Duration.seconds(1), ev -> updateUI()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        updateUI();
    }

    // =========================================================================
    //  UI update (called every second)
    // =========================================================================

    public void updateUI() {
        // accumulated pause time
        long pauseSpanMs = pausedMillisTotal;
        if (isPaused) {
            pauseSpanMs += java.time.Duration.between(pauseStartTime, LocalDateTime.now()).toMillis();
        }

        // mode total duration
        long modeDurationMs = switch (mode) {
            case WORK       -> settings.getWorkTime()      * 60_000L + 800;
            case RELAX      -> settings.getRelaxTime()     * 60_000L + 800;
            case RELAX_LONG -> settings.getRelaxTimeLong() * 60_000L + 800;
        };

        // remaining millis
        long remainingMs;
        if (isPaused) {
            remainingMs = modeDurationMs - timerElapsedWhenPaused;
        } else {
            long elapsed = java.time.Duration.between(timerStartTime, LocalDateTime.now()).toMillis();
            remainingMs = modeDurationMs - elapsed;
        }

        boolean nowOverTime = remainingMs <= 0;
        if (!isOverTime && nowOverTime) {
            // timer just expired
            if (mode == TimerMode.WORK)
                soundSettingsController.playSound(SoundType.WORK_DONE);
            else
                soundSettingsController.playSound(SoundType.REST_TIMEOUT);
        }
        isOverTime = nowOverTime;

        double progressPct = Math.max(0, Math.min(100,
                100.0 - (double) remainingMs * 100.0 / modeDurationMs));

        // Time string
        long absMs   = Math.abs(remainingMs);
        long totalSec= absMs / 1000;
        long hours   = totalSec / 3600;
        long minutes = (totalSec % 3600) / 60;
        long seconds = totalSec % 60;

        String prefix  = (mode == TimerMode.WORK) ? "Work  " : "Rest  ";
        String timeStr = (hours > 0)
                ? prefix + String.format("%d:%02d:%02d", hours, minutes, seconds)
                : prefix + String.format("%d:%02d", minutes, seconds);

        // Info string (start time + total pause)
        String infoStr = "Start @ " + timerStartTime.format(DateTimeFormatter.ofPattern("H:mm"));
        if (pauseSpanMs > 0) {
            long ps  = pauseSpanMs / 1000;
            long ph  = ps / 3600;
            long pm  = (ps % 3600) / 60;
            long pse = ps % 60;
            infoStr += (ph > 0)
                    ? "\nPause: " + String.format("%d:%02d:%02d", ph, pm, pse)
                    : "\nPause: " + String.format("%d:%02d", pm, pse);
        }

        if (buttonsController != null) {
            final var preset = settings.getNeonPreset();
            buttonsController.updateUI(preset,
                    mode, isPaused, isOverTime, progressPct, timeStr, infoStr);
            updateTaskbarIcon(preset, progressPct);
        }
    }

    // =========================================================================
    //  Taskbar icon (Windows dynamic icon showing remaining time)
    // =========================================================================

    /**
     * Updates (or clears) the stage icon shown in the Windows taskbar every second.
     *
     * <p>Re-renders only when the displayed text, accent colour, layout, or font size
     * has actually changed to avoid unnecessary {@link javafx.scene.canvas.Canvas} snapshots.</p>
     *
     * <p>Routing:
     * <ul>
     *   <li>{@link TaskbarTimeLayout#HORIZONTAL} – single-line {@code "MM:SS"} / {@code "HH:MM:SS"}.</li>
     *   <li>{@link TaskbarTimeLayout#VERTICAL} – 2-line (hours == 0) or 3-line (hours &gt; 0) stacked.</li>
     * </ul>
     * Font size is read from {@link AppSettings#getTaskbarFontSize()} (base px at 64 px canvas).</p>
     *
     * @param preset      the currently active {@link NeonPreset}
     * @param progressPct elapsed progress percentage in [0, 100]
     */
    private void updateTaskbarIcon(NeonPreset preset, double progressPct) {
        if (stage == null) return;

        if (!settings.isTaskbarIconEnable()) {
            // Feature disabled – restore the default JVM icon
            if (!stage.getIcons().isEmpty()) {
                stage.getIcons().clear();
            }
            // Reset cache so re-enabling triggers an immediate redraw
            lastIconText     = "";
            lastIconColor    = null;
            lastIconLayout   = null;
            lastIconFontSize = -1.0;
            return;
        }

        // ── Compute time components ───────────────────────────────────────────
        long absMs    = Math.abs(currentRemainingMs());
        long totalSec = absMs / 1000;
        long hours    = totalSec / 3600;
        long minutes  = (totalSec % 3600) / 60;
        long seconds  = totalSec % 60;

        // Zero-padded components for vertical stacked display.
        final String iconHour   = String.format("%02d", hours);
        final String iconMinute = String.format("%02d", minutes);
        final String iconSecond = String.format("%02d", seconds);

        // Cache key text: "MM:SS" (hours == 0) or "HH:MM:SS" (hours > 0).
        // Different lengths guarantee a redraw when hours cross the zero boundary.
        final String iconText = (hours == 0)
                ? iconMinute + ":" + iconSecond
                : iconHour + ":" + iconMinute + ":" + iconSecond;

        // ── Layout and font size from user settings ───────────────────────────
        final TaskbarTimeLayout layout      = settings.getTaskbarLayout();
        final double            baseFontSz  = settings.getTaskbarFontSize();

        // ── Accent colour ─────────────────────────────────────────────────────
        final Color accentColor = TimerBackgroundHelper.computeAccentColor(
                preset, mode, progressPct, isPaused, isOverTime);

        // ── Re-render only when something changed ─────────────────────────────
        if (iconText.equals(lastIconText)
                && accentColor.equals(lastIconColor)
                && layout == lastIconLayout
                && Double.compare(baseFontSz, lastIconFontSize) == 0) {
            return;
        }

        lastIconText     = iconText;
        lastIconColor    = accentColor;
        lastIconLayout   = layout;
        lastIconFontSize = baseFontSz;

        // ── Render at all standard sizes ──────────────────────────────────────
        final List<Image> icons;
        if (layout == TaskbarTimeLayout.HORIZONTAL) {
            // Single-line "MM:SS" or "HH:MM:SS"
            icons = TaskbarIconRenderer.renderAllSizesHorizontal(iconText, accentColor, baseFontSz);
        } else {
            // VERTICAL: 2-line (hours == 0) or 3-line (hours > 0)
            icons = (hours == 0)
                    ? TaskbarIconRenderer.renderAllSizes(iconMinute, iconSecond, accentColor, baseFontSz)
                    : TaskbarIconRenderer.renderAllSizes(iconHour, iconMinute, iconSecond, accentColor, baseFontSz);
        }
        stage.getIcons().setAll(icons);

        // AWT Taskbar push: use the 32 px image (index 2 in [16,24,32,48,64])
        pushAwtTaskbarIcon(icons.get(2));

        // Native Win32 HICON push: sends WM_SETICON directly to the HWND so that
        // the taskbar button icon is updated even when the JavaFX/AWT paths are
        // insufficient to trigger a Windows shell refresh.
        // Uses the 32 px image; no-op on non-Windows or when JNA is unavailable.
        WindowsNativeWindowIconHelper.apply(icons.get(2));
    }

    /** Returns the current remaining milliseconds (positive = time left, negative = overtime). */
    private long currentRemainingMs() {
        long modeDurationMs = switch (mode) {
            case WORK       -> settings.getWorkTime()      * 60_000L + 800;
            case RELAX      -> settings.getRelaxTime()     * 60_000L + 800;
            case RELAX_LONG -> settings.getRelaxTimeLong() * 60_000L + 800;
        };
        if (isPaused) {
            return modeDurationMs - timerElapsedWhenPaused;
        }
        long elapsed = java.time.Duration.between(timerStartTime, java.time.LocalDateTime.now()).toMillis();
        return modeDurationMs - elapsed;
    }

    // =========================================================================
    //  AWT Taskbar helper (robust Windows taskbar refresh)
    // =========================================================================

    /**
     * Pushes {@code fxImage} to the native AWT {@link Taskbar} so that Windows
     * actually refreshes the taskbar-button image on every tick.
     *
     * <p>{@link javafx.stage.Stage#getIcons()} alone is not enough on Windows –
     * the native taskbar only picks up the icon change when it is also set
     * through the AWT {@link Taskbar} API.  This method performs that second
     * push and silently no-ops when the feature is unavailable (macOS app-dock
     * badge path, headless CI, etc.).</p>
     *
     * @param fxImage the {@link Image} already placed in {@code stage.getIcons()}
     */
    private static void pushAwtTaskbarIcon(Image fxImage) {
        if (!Taskbar.isTaskbarSupported()) return;
        final var taskbar = Taskbar.getTaskbar();
        if (!taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) return;
        try {
            taskbar.setIconImage(fxImageToBufferedImage(fxImage));
        } catch (UnsupportedOperationException | SecurityException ignored) {
            // Platform does not support dynamic icon update – silently skip
        }
    }

    /**
     * Converts a JavaFX {@link Image} to an AWT {@link BufferedImage} by copying
     * pixels through a {@link javafx.scene.image.PixelReader}.
     *
     * <p>This is functionally equivalent to {@code SwingFXUtils.fromFXImage} but
     * does not require the {@code javafx-swing} module, keeping the dependency
     * set unchanged.</p>
     *
     * @param fxImage source JavaFX image; must not be {@code null}
     * @return an ARGB {@link BufferedImage} with identical pixel data
     */
    private static BufferedImage fxImageToBufferedImage(Image fxImage) {
        final int w = (int) fxImage.getWidth();
        final int h = (int) fxImage.getHeight();
        final var argbPixels = new int[w * h];
        fxImage.getPixelReader().getPixels(0, 0, w, h,
                PixelFormat.getIntArgbInstance(), argbPixels, 0, w);
        final var bim = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        bim.setRGB(0, 0, w, h, argbPixels, 0, w);
        return bim;
    }

    // =========================================================================
    //  Timer actions (called from ButtonsController / keyboard shortcuts)
    // =========================================================================

    public void startWork() {
        triggerGCalIfNeeded();
        timerStartTime    = LocalDateTime.now();
        pauseStartTime    = LocalDateTime.now();
        pausedMillisTotal = 0;
        timerElapsedWhenPaused = 0;
        mode     = TimerMode.WORK;
        isPaused = false;
        isOverTime = false;
        updateUI();
    }

    public void startRelax(boolean longBreak) {
        triggerGCalIfNeeded();
        applyRelaxState(longBreak);
    }

    /**
     * Finishes the current WORK session early: always creates a Google Calendar
     * event (when GCal is enabled) regardless of whether overtime has been reached,
     * then immediately switches to a short REST.
     * <p>Safe to call at any point during WORK mode – even before the timer expires.</p>
     */
    public void finishWork() {
        if (mode == TimerMode.WORK && settings.isGCalEnable()) {
            openGoogleCalendar(timerStartTime, LocalDateTime.now());
        }
        applyRelaxState(false);
    }

    /** Shared state-reset for all relax transitions (avoids duplicating reset logic). */
    private void applyRelaxState(boolean longBreak) {
        timerStartTime         = LocalDateTime.now();
        pauseStartTime         = LocalDateTime.now();
        pausedMillisTotal      = 0;
        timerElapsedWhenPaused = 0;
        mode                   = longBreak ? TimerMode.RELAX_LONG : TimerMode.RELAX;
        isPaused               = false;
        isOverTime             = false;
        updateUI();
    }

    public void pause() {
        if (isPaused) return;
        if (mode == TimerMode.RELAX || mode == TimerMode.RELAX_LONG) return;
        soundSettingsController.playSound(SoundType.PAUSE);
        isPaused               = true;
        timerElapsedWhenPaused = java.time.Duration.between(timerStartTime, LocalDateTime.now()).toMillis();
        pauseStartTime         = LocalDateTime.now();
        updateUI();
    }

    public void resume() {
        if (!isPaused) return;
        soundSettingsController.playSound(SoundType.RESUME);
        long pauseDurationMs = java.time.Duration.between(pauseStartTime, LocalDateTime.now()).toMillis();
        timerStartTime    = timerStartTime.plusNanos(pauseDurationMs * 1_000_000L);
        pausedMillisTotal += pauseDurationMs;
        isPaused           = false;
        updateUI();
    }

    public void reset() {
        triggerGCalIfNeeded();
        isPaused          = false;
        timerStartTime    = LocalDateTime.now();
        pauseStartTime    = LocalDateTime.now();
        pausedMillisTotal = 0;
        timerElapsedWhenPaused = 0;
        isOverTime        = false;
        updateUI();
    }

    // =========================================================================
    //  Navigation
    // =========================================================================

    public void showButtons() {
        contentPane.getChildren().setAll(buttonsView);
    }

    public void showSettings() {
        settingsController.syncFromSettings();
        contentPane.getChildren().setAll(settingsView);
    }

    public void showSoundSettings() {
        contentPane.getChildren().setAll(soundSettingsView);
    }

    public void showTaskbarSettings() {
        taskbarSettingsController.syncFromSettings();
        contentPane.getChildren().setAll(taskbarSettingsView);
    }

    // =========================================================================
    //  Window helpers
    // =========================================================================

    public void toggleAlwaysOnTop() {
        boolean next = !stage.isAlwaysOnTop();
        stage.setAlwaysOnTop(next);
        settings.setAlwaysOnTop(next);
    }

    public boolean isAlwaysOnTop() { return stage.isAlwaysOnTop(); }

    public void saveStateAndClose(boolean saveTimer) {
        if (saveTimer && mode == TimerMode.WORK) {
            settings.setTimerRestoreDateTime(
                    timerStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            settings.setTimerRestoreMode(mode.getValue());
        } else {
            settings.setTimerRestoreDateTime("");
            settings.setTimerRestoreMode(TimerMode.WORK.getValue());
        }
        settings.setWindowX(stage.getX());
        settings.setWindowY(stage.getY());
        settings.setWindowWidth(stage.getWidth());
        settings.setWindowHeight(stage.getHeight());
        settings.save();

        clock.stop();
        Platform.exit();
    }

    // =========================================================================
    //  Google Calendar
    // =========================================================================

    public void openGoogleCalendar(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // Convert local times to UTC strings
            java.time.ZonedDateTime startUtc = startTime.atZone(java.time.ZoneId.systemDefault())
                    .withZoneSameInstant(java.time.ZoneOffset.UTC);
            java.time.ZonedDateTime endUtc = endTime.atZone(java.time.ZoneId.systemDefault())
                    .withZoneSameInstant(java.time.ZoneOffset.UTC);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
            String strStart = startUtc.format(fmt);
            String strEnd   = endUtc.format(fmt);

            StringBuilder url = new StringBuilder(
                    "https://calendar.google.com/calendar/r/eventedit?action=TEMPLATE");

            String text = settings.getGCalText();
            if (!text.isEmpty())
                url.append("&text=").append(URLEncoder.encode(text, StandardCharsets.UTF_8));

            String src = settings.getGCalSrc();
            if (!src.isEmpty())
                url.append("&src=").append(URLEncoder.encode(src, StandardCharsets.UTF_8));

            url.append("&dates=").append(strStart).append("/").append(strEnd);

            String finalUrl = url.toString();
            if (settings.isGCalCopyToClipboard()) {
                ClipboardContent cc = new ClipboardContent();
                cc.putString(finalUrl);
                Clipboard.getSystemClipboard().setContent(cc);
            }

            Desktop.getDesktop().browse(new URI(finalUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  FXML action handlers (window controls)
    // =========================================================================

    @FXML
    private void handleAlwaysOnTop() { toggleAlwaysOnTop(); }

    @FXML
    private void handleClose() { saveStateAndClose(true); }

    @FXML
    private void handleCloseNoSave() { saveStateAndClose(false); }

    // =========================================================================
    //  Accessors used by sub-controllers
    // =========================================================================

    public AppSettings      getSettings()      { return settings; }
    public TimerMode        getMode()          { return mode; }
    public LocalDateTime    getTimerStartTime(){ return timerStartTime; }
    public Stage            getStage()         { return stage; }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    private void triggerGCalIfNeeded() {
        if (isOverTime && mode == TimerMode.WORK && settings.isGCalEnable())
            openGoogleCalendar(timerStartTime, LocalDateTime.now());
    }

    private static FadeTransition createFade(Node node, double from, double to, double ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), node);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }

    /** Derives window-control icon size (px) from the stage height – delegated to UiScaleHelper. */
    private static double clampWCtrl(double stageHeight) {
        return UiScaleHelper.winCtrlIconPx(stageHeight);
    }

    private ResizeZone detectResizeZone(double x, double y) {
        if (!stage.isResizable()) return ResizeZone.NONE;

        double w = rootPane.getWidth();
        double h = rootPane.getHeight();

        boolean left = x <= RESIZE_MARGIN;
        boolean right = x >= w - RESIZE_MARGIN;
        boolean top = y <= RESIZE_MARGIN;
        boolean bottom = y >= h - RESIZE_MARGIN;

        if (top && left) return ResizeZone.TOP_LEFT;
        if (top && right) return ResizeZone.TOP_RIGHT;
        if (bottom && left) return ResizeZone.BOTTOM_LEFT;
        if (bottom && right) return ResizeZone.BOTTOM_RIGHT;
        if (left) return ResizeZone.LEFT;
        if (right) return ResizeZone.RIGHT;
        if (top) return ResizeZone.TOP;
        if (bottom) return ResizeZone.BOTTOM;
        return ResizeZone.NONE;
    }

    private void resizeWindow(double screenX, double screenY) {
        double dx = screenX - resizeStartScreenX;
        double dy = screenY - resizeStartScreenY;

        double minW = stage.getMinWidth();
        double minH = stage.getMinHeight();

        double newX = resizeStartStageX;
        double newY = resizeStartStageY;
        double newW = resizeStartStageWidth;
        double newH = resizeStartStageHeight;

        if (activeResizeZone.left) {
            newW = Math.max(minW, resizeStartStageWidth - dx);
            newX = resizeStartStageX + (resizeStartStageWidth - newW);
        }
        if (activeResizeZone.right) {
            newW = Math.max(minW, resizeStartStageWidth + dx);
        }
        if (activeResizeZone.top) {
            newH = Math.max(minH, resizeStartStageHeight - dy);
            newY = resizeStartStageY + (resizeStartStageHeight - newH);
        }
        if (activeResizeZone.bottom) {
            newH = Math.max(minH, resizeStartStageHeight + dy);
        }

        stage.setX(newX);
        stage.setY(newY);
        stage.setWidth(newW);
        stage.setHeight(newH);
    }

    private enum ResizeZone {
        NONE(false, false, false, false, Cursor.DEFAULT),
        LEFT(false, false, true, false, Cursor.W_RESIZE),
        RIGHT(false, false, false, true, Cursor.E_RESIZE),
        TOP(true, false, false, false, Cursor.N_RESIZE),
        BOTTOM(false, true, false, false, Cursor.S_RESIZE),
        TOP_LEFT(true, false, true, false, Cursor.NW_RESIZE),
        TOP_RIGHT(true, false, false, true, Cursor.NE_RESIZE),
        BOTTOM_LEFT(false, true, true, false, Cursor.SW_RESIZE),
        BOTTOM_RIGHT(false, true, false, true, Cursor.SE_RESIZE);

        private final boolean top;
        private final boolean bottom;
        private final boolean left;
        private final boolean right;
        private final Cursor cursor;

        ResizeZone(boolean top, boolean bottom, boolean left, boolean right, Cursor cursor) {
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
            this.cursor = cursor;
        }
    }
}


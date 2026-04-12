package com.tomatotimer;

import javafx.scene.paint.Color;

public class TimerBackgroundHelperHelper {

    public static final double DELTA = 1e-3;

    public static final double PCT_ZERO    = 0.0;
    public static final double PCT_HALF    = 50.0;
    public static final double PCT_WARNING = TimerBackgroundHelper.WARNING_THRESHOLD_PCT;
    public static final double PCT_FULL    = 100.0;

    public static Color auroraAccentAtProgress(double pct, boolean paused, boolean overtime) {
        return TimerBackgroundHelper.computeAccentColor(
                NeonPreset.AURORA_DRIFT, TimerMode.WORK, pct, paused, overtime);
    }
}


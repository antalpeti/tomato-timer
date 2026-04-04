package com.tomatotimer;

public enum TimerMode {
    WORK(0), RELAX(1), RELAX_LONG(2);

    private final int value;

    TimerMode(int value) { this.value = value; }

    public int getValue() { return value; }

    public static TimerMode fromValue(int value) {
        for (TimerMode m : values()) {
            if (m.value == value) return m;
        }
        return WORK;
    }
}


package com.tomatotimer;

public enum SoundType {
    RESUME(0), PAUSE(1), WORK_DONE(2), REST_TIMEOUT(3);

    private final int index;

    SoundType(int index) { this.index = index; }

    public int getIndex() { return index; }
}


package com.tomatotimer;

/**
 * Stand-alone launcher – avoids the "JavaFX runtime components are missing"
 * error when running the fat-jar on some JDKs.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}


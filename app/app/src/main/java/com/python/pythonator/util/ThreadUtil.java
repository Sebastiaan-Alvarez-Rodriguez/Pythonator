package com.python.pythonator.util;

import androidx.annotation.WorkerThread;

public class ThreadUtil {

    @WorkerThread
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored){}

    }
}

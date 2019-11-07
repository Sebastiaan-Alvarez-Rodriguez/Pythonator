package com.python.pythonator.util;

import androidx.annotation.WorkerThread;

public class ThreadUtil {
    @WorkerThread
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored){}
    }
}

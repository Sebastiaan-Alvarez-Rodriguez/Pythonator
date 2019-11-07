package com.python.pythonator.backend.bluetooth;

public interface SendListener {
    enum SendState {
        FAILED,
        BUSY,
        SENT
    }

    void onResult(SendState sent);
}

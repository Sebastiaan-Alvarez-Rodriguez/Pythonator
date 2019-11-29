package com.python.pythonator.backend.bluetooth;

public interface SendListener {
    enum SendState {
        FAILED,
        BUSY,
        ALREADY_SENT,
        SENT
    }

    void onResult(SendState sent);
}

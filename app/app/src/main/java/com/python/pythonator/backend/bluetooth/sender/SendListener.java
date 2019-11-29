package com.python.pythonator.backend.bluetooth.sender;

public interface SendListener {
    enum SendState {
        FAILED,
        SENDING,
        SENT
    }

    void onResult(SendState sent);
}

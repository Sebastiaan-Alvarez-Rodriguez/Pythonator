package com.python.pythonator.backend.connection;

/**
 * Values to separate different connection states
 */
public enum ConnectState {
    DISCONNECTED,
    CONNECTING,
    NOT_FOUND,
    ERROR,
    CONNECTED
}

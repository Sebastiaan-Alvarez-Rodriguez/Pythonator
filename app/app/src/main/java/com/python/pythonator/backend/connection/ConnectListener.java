package com.python.pythonator.backend.connection;

/**
 * Listener for UI, to keep track of current connection status
 * @see ConnectState for the possible connection states
 */
public interface ConnectListener {
    /**
     * Is called when a new connectionstate is active
     * @param new_state New state of the bluetooth connection
     */
    void onConnectStateChange(ConnectState new_state);
}

package com.python.pythonator.backend.connection;

/**
 * Listener for UI, to receive updates about bluetooth activation status
 */
public interface BluetoothListener {
    /**
     * Gets called when bluetooth just went from off to on state
     */
    void onBluetoothOn();

    /**
     * Gets called when user pressed deny instead of allow
     */
    void onUserDeniedActivation();
}

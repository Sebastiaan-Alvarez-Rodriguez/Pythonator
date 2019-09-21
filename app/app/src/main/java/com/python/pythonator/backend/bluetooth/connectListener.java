package com.python.pythonator.backend.bluetooth;

public interface connectListener {

    /**
     * Called when bluetooth is off
     */
    void noBluetooth();

    /**
     * Called when bluetooth host is not found
     */
    void notFound();

    /**
     * Called when connection failed, even though bluetooth is on and the device is found
     */
    void notConnected();

    /**
     * Called when established connection with bluetooth host
     */
    void isConnected();

    /**
     * Called when working on connection
     */
    void isPending();
}

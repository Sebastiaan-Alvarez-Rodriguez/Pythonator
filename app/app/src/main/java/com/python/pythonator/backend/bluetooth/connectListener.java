package com.python.pythonator.backend.bluetooth;

public interface connectListener {
    void noBluetooth();
    void isConnected();
    void isPending();
    void notFound();
}

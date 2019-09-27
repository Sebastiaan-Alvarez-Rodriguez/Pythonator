package com.python.pythonator.backend.bluetooth.connector.state;

import android.util.Log;

import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.backend.bluetooth.ConnectListener;
import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BluetoothStateWatcher {
    private BluetoothClient client;
    private ScheduledExecutorService service;
    private boolean initial_success;
    private ConnectListener connect_listener;

    public BluetoothStateWatcher(BluetoothClient client, boolean initial_success, ConnectListener connect_listener) {
        this.client = client;
        this.initial_success = initial_success;
        this.connect_listener = connect_listener;

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::watch, 5, 5, TimeUnit.SECONDS);
    }

    public synchronized void setState(BluetoothConnectState new_state) {
        connect_listener.onChangeState(new_state);
        Log.e("StateWatcher", "New state: "+new_state.name());
    }

    private void watch() {
        if (initial_success) {
            if (!client.isBluetoothEnabled())
                setState(BluetoothConnectState.NO_BLUETOOTH);
            else if (!client.isConnected())
                setState(BluetoothConnectState.NOT_FOUND);
            else
                setState(BluetoothConnectState.CONNECTED);
        }
    }

    public void stop() {
        service.shutdown();
    }
}

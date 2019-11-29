package com.python.pythonator.backend.bluetooth.connector.state;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.ConnectListener;
import com.python.pythonator.backend.bluetooth.broadcast.state.StateBroadcastHandler;
import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BluetoothStateWatcher {
    private StateBroadcastHandler state_handler;

    private BluetoothConnectState state;
    private Context application_context;

    public BluetoothStateWatcher(@NonNull Context application_context) {
        this.state = BluetoothConnectState.NOT_CONNECTED;
        this.state_handler = null;
        this.application_context = application_context;
    }

    /**
     * Watches to see if connection closes from either client-side or server-side.
     * Call this function only when there is a successful connection
     * @param connect_listener The listener to pass the update when the connection dies
     */
    public synchronized void watch(@NonNull BluetoothAdapter bluetooth_adapter, @NonNull ConnectListener connect_listener) {
        if (state_handler != null)
            stop();
        state = BluetoothConnectState.CONNECTED;
        connect_listener.onChangeState(state);

        Log.i("StateWatcher", "Starting watcher!");
        this.state_handler = new StateBroadcastHandler(application_context, new_state -> {
            if (new_state == BluetoothConnectState.NOT_CONNECTED) {
                stop();
                connect_listener.onChangeState(new_state);
            }
        });
        Executors.newSingleThreadExecutor().execute(() -> state_handler.startBroadcast());
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (!bluetooth_adapter.isEnabled()) {
                stop();
                connect_listener.onChangeState(BluetoothConnectState.NO_BLUETOOTH);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public BluetoothConnectState getState() {
        return state;
    }

    public void stop() {
        state_handler.stopBroadcast();
        state = BluetoothConnectState.NOT_CONNECTED;
        Log.i("StateWatcher", "Stopping state watcher");
        state_handler = null;
    }
}

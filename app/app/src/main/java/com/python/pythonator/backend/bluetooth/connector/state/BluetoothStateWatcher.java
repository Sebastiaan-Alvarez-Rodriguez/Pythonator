package com.python.pythonator.backend.bluetooth.connector.state;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.ConnectListener;
import com.python.pythonator.backend.bluetooth.broadcast.state.StateBroadcastHandler;
import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;

import java.util.concurrent.Executors;

public class BluetoothStateWatcher {
    private StateBroadcastHandler state_handler;

    private volatile BluetoothConnectState state;
    private Context application_context;

    public BluetoothStateWatcher(@NonNull Context application_context) {
        this.state = BluetoothConnectState.NOT_CONNECTED;
        this.state_handler = null;
        this.application_context = application_context;
    }

    public void watch(@NonNull ConnectListener connect_listener) {
        if (state_handler != null)
            stop();
        this.state_handler = new StateBroadcastHandler(application_context, new_state -> {
            if (new_state == BluetoothConnectState.NOT_CONNECTED) {
                state = new_state;
                connect_listener.onChangeState(new_state);
                Log.e("StateWatcher", "New state: " + new_state.name());
                stop();
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> state_handler.startBroadcast());
    }

    public void setState(BluetoothConnectState state) {
        this.state = state;
    }
    public BluetoothConnectState getState() {
        return state;
    }

    public void stop() {
        state_handler.stopBroadcast();
        Log.e("StateWatcher", "Stopping state watcher");
    }
}

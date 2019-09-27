package com.python.pythonator.backend.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;
import com.python.pythonator.backend.bluetooth.connector.BluetoothConnector;
import com.python.pythonator.backend.bluetooth.connector.state.BluetoothStateWatcher;
import com.python.pythonator.structures.Image;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;

// https://examples.javacodegeeks.com/android/core/bluetooth/bluetoothadapter/android-bluetooth-example/
// https://stackoverflow.com/questions/20009565/connect-to-android-bluetooth-socket


// https://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3

// https://github.com/janosgyerik/bluetoothviewer

//On UUIDs:
// https://stackoverflow.com/questions/13964342/android-how-do-bluetooth-uuids-work
public class BluetoothClient {
    private static volatile BluetoothClient INSTANCE;

    public static BluetoothClient getClient(Context application_context) {
        if (INSTANCE == null) {
            synchronized (BluetoothClient.class) {
                if (INSTANCE == null)
                    INSTANCE = new BluetoothClient(application_context);
            }
        }
        return INSTANCE;
    }

    public static final int REQUEST_ENABLE_BLUETOOTH = 400;

    //Bluetooth primitives
    private BluetoothAdapter bluetooth_adapter;
    private BluetoothSocket bluetooth_socket;

    private BluetoothConnector bluetooth_connector;
    private BluetoothStateWatcher bluetooth_state_watcher;

    private BluetoothClient(Context application_context) {
        bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_socket = null;
        bluetooth_connector = new BluetoothConnector(bluetooth_adapter, application_context);
        bluetooth_state_watcher = null;
    }

    /**
     * Sends Intent request to enable bluetooth, with request code
     * {@link #REQUEST_ENABLE_BLUETOOTH
     * @param activity The activity to
     */
    @MainThread
    public void activateBluetooth(@NonNull Activity activity) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Asynchronously tries to connect to the bluetooth server with given name
     * @param server_name the name of the server to connect to
     * @param connection_listener the interface which gets called with possible outcomes
     */
    public void connect(@NonNull String server_name, @NonNull ConnectListener connection_listener) {
        if (bluetooth_state_watcher != null)
            bluetooth_state_watcher.stop();
        connection_listener.onChangeState(BluetoothConnectState.PENDING);
        bluetooth_connector.search(server_name, (state, socket) -> {
            bluetooth_state_watcher = new BluetoothStateWatcher(this, state == BluetoothConnectState.CONNECTED, connection_listener);
            bluetooth_state_watcher.setState(state);
            bluetooth_socket = socket;
        });
    }

    /**
     * Disconnects from connected server, if connected. Does nothing otherwise
     */
    public void disconnect() {
        if (bluetooth_socket != null) {
            try {
                bluetooth_socket.close();
                bluetooth_socket = null;
            } catch (Exception ignored){}
        }
    }

    /**
     * @return whether bluetooth is enabled or not
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isBluetoothEnabled() {
        return bluetooth_adapter.isEnabled();
    }

    /**
     * @return whether there is an active connection or not
     */
    public boolean isConnected() {
        return bluetooth_adapter.isEnabled() && bluetooth_socket != null && bluetooth_socket.isConnected();
    }

    public void sendImage(@NonNull Image image, @NonNull SendListener listener) {
        if (isConnected()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                if (bluetooth_socket.isConnected()) {
                    try {
                        DataOutputStream out = new DataOutputStream(bluetooth_socket.getOutputStream());
                        out.writeLong((long)image.getBitmapBytes().length);
                        out.write(image.getBitmapBytes());
                    } catch (Exception ignored){}

                    try {
                        InputStream in = bluetooth_socket.getInputStream();
                        int result = in.read();
                        listener.onResult(result == 0);
                        if (result == -1)
                            disconnect();
                    } catch (Exception ignored){}
                }
            });
        }
    }
}
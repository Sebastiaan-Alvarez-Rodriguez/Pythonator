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
public class BluetoothServer {
    private static volatile BluetoothServer INSTANCE;

    public static BluetoothServer getServer(Context application_context) {
        if (INSTANCE == null) {
            synchronized (BluetoothServer.class) {
                if (INSTANCE == null)
                    INSTANCE = new BluetoothServer(application_context);
            }
        }
        return INSTANCE;
    }

    public static final int REQUEST_ENABLE_BLUETOOTH = 400;

    private BluetoothAdapter bluetooth_adapter;
    private BluetoothConnector bluetooth_connector;
    private BluetoothSocket bluetooth_socket;

    private BluetoothServer(Context application_context) {
        bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_connector = new BluetoothConnector(bluetooth_adapter, application_context);
        bluetooth_socket = null;
    }


    /**
     * Sends Intent request to enable bluetooth, with request code
     * {@link #REQUEST_ENABLE_BLUETOOTH
     * @param activity The activity to
     */
    @MainThread
    public void activate(@NonNull Activity activity) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Asynchronously tries to connect to the bluetooth server with given name
     * @param server_name the name of the server to connect to
     * @param connection_listener the interface which gets called with possible outcomes
     */
    public void connect(@NonNull String server_name, @NonNull ConnectListener connection_listener) {
        connection_listener.onChangeState(BluetoothConnectState.PENDING);
        bluetooth_connector.search(server_name, (state, socket) -> {
            connection_listener.onChangeState(state);
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
     * @return whether there is an active connection or not
     */
    public boolean isConnected() {
        return bluetooth_adapter.isEnabled() && bluetooth_socket != null && bluetooth_socket.isConnected();
    }

    public void sendImage(@NonNull Image image, @NonNull SendListener listener) {
        if (bluetooth_socket != null && bluetooth_socket.isConnected()) {
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

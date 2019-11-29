package com.python.pythonator.backend.bluetooth.receiver;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.util.ThreadUtil;

import java.io.DataInputStream;
import java.util.concurrent.Executors;

public class ReceiverHandler {
    private boolean receiving;

    public ReceiverHandler() {
        receiving = false;
    }

    public synchronized void receive(@NonNull BluetoothClient client, @NonNull BluetoothSocket socket, @NonNull ReceivedListener listener, int retries) {
        if (receiving)
            return; // We are already busy trying to receive something
        receiving = true;
        Executors.newSingleThreadExecutor().execute(()-> {
            if (client.isConnected()) {
                for (int i = 0; i < retries; ++i) {
                    try {
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        if (in.readByte() == 0) { // TODO: I really hope this call is blocking...
                            listener.onResult();
                            receiving = false;
                            return;
                        }
                    } catch (Exception ignored) {}
                    ThreadUtil.sleep(1000);
                }
            }
        });
    }

}

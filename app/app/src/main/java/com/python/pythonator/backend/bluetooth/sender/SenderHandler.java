package com.python.pythonator.backend.bluetooth.sender;

import android.bluetooth.BluetoothSocket;
import androidx.annotation.NonNull;
import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.structures.Image;
import com.python.pythonator.util.ThreadUtil;

import java.io.DataOutputStream;
import java.util.concurrent.Executors;

public class SenderHandler {
    private boolean sending;

    public SenderHandler() {
        sending = false;
    }

    public synchronized void send(@NonNull BluetoothClient client, @NonNull BluetoothSocket socket, @NonNull Image image, @NonNull SendListener sendlistener, int retries) {
        if (sending)
            return; // We are already busy sending something
        sending = true;
        Executors.newSingleThreadExecutor().execute(()-> {
            if (client.isConnected()) {
                for (int i = 0; i < retries; ++i) {
                    try {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeLong((long) image.getBitmapBytes().length);
                        out.write(image.getBitmapBytes());
                        sendlistener.onResult(SendListener.SendState.SENT);
                        sending = false;
                        return;
                    } catch (Exception ignored) {}
                    ThreadUtil.sleep(1000);
                }
            }
            sendlistener.onResult(SendListener.SendState.FAILED);
        });
    }
}

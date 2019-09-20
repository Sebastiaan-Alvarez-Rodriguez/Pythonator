package com.python.pythonator.backend.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.python.pythonator.structures.Image;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// https://examples.javacodegeeks.com/android/core/bluetooth/bluetoothadapter/android-bluetooth-example/
// https://stackoverflow.com/questions/20009565/connect-to-android-bluetooth-socket


// https://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3

// https://github.com/janosgyerik/bluetoothviewer

//On UUIDs:
// https://stackoverflow.com/questions/13964342/android-how-do-bluetooth-uuids-work
public class BluetoothServer {
    private static volatile BluetoothServer INSTANCE;

    public static BluetoothServer getServer() {
        if (INSTANCE == null) {
            synchronized (BluetoothServer.class) {
                if (INSTANCE == null)
                    INSTANCE = new BluetoothServer();
            }
        }
        return INSTANCE;
    }

    //For board, use well known: 00001101-0000-1000-8000-00805F9B34FB?
    private static final UUID uuid = UUID.fromString("E1D75160-DBE1-11E9-AAEF-0800200C9A66");
//    Valid Bluetooth hardware addresses must be as "00:11:22:33:AA:BB"
    private static final String MAC = "00:11:22:33:AA:BB";

    public static final int REQUEST_ENABLE_BLUETOOTH = 400;

    private BluetoothAdapter bluetooth_adapter;
    private BluetoothSocket bluetooth_socket;

    private connectListener resultInterface;

    private BluetoothServer() {
        bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_socket = null;
    }


    @MainThread
    public void activate(@NonNull Activity activity) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Executes task to connect to the server
     * @param resultInterface the interface which gets called with possible outcomes
     */
    public void connect(connectListener resultInterface) {
        ScheduledExecutorService sof = Executors.newSingleThreadScheduledExecutor();
        sof.scheduleWithFixedDelay(() -> {
            resultInterface.isPending();
            BluetoothDevice device = bluetooth_adapter.getRemoteDevice(MAC);
            try {
                bluetooth_socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                resultInterface.noBluetooth();
            }

            if (bluetooth_socket != null && bluetooth_socket.isConnected()) {
                resultInterface.isConnected();
                sof.shutdown();
            } else {
                resultInterface.notFound();
            }
        }, 0, 4, TimeUnit.SECONDS);
    }

    /**
     * Queries all known (previously connected) devices
     */
    @WorkerThread
    private void queryPaired() {
        Set<BluetoothDevice> paired_devices = bluetooth_adapter.getBondedDevices();
        if (paired_devices.size() > 0) {
            Log.e("Found", "There are paired devices");
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : paired_devices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.e("Found", deviceName);
            }
        } else {
            Log.e("Found", "There are NO paired devices");
        }

    }

    @WorkerThread
    protected void sendImage(@NonNull Image image) {

    }
}

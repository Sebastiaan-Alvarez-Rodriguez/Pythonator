package com.python.pythonator.backend.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.broadcast.BroadcastHandler;
import com.python.pythonator.structures.Image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Executors;

// https://examples.javacodegeeks.com/android/core/bluetooth/bluetoothadapter/android-bluetooth-example/
// https://stackoverflow.com/questions/20009565/connect-to-android-bluetooth-socket


// https://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3

// https://github.com/janosgyerik/bluetoothviewer

//On UUIDs:
// https://stackoverflow.com/questions/13964342/android-how-do-bluetooth-uuids-work
public class BluetoothServer {
    private static volatile BluetoothServer INSTANCE;

    public static BluetoothServer getServer(Context c) {
        if (INSTANCE == null) {
            synchronized (BluetoothServer.class) {
                if (INSTANCE == null)
                    INSTANCE = new BluetoothServer(c);
            }
        }
        return INSTANCE;
    }

    public static final int REQUEST_ENABLE_BLUETOOTH = 400;

    //For board, use well known: 00001101-0000-1000-8000-00805F9B34FB?
    private static final UUID uuid = UUID.fromString("E1D75160-DBE1-11E9-AAEF-0800200C9A66");

    private BluetoothAdapter bluetooth_adapter;
    private BluetoothSocket bluetooth_socket;

    private BroadcastHandler broadcast_handler;

    private BluetoothServer(Context application_context) {
        bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_socket = null;
        this.broadcast_handler = new BroadcastHandler(application_context);
    }


    @MainThread
    public void activate(@NonNull Activity activity) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Executes task to connect to the server by MAC address
     * @param connection_listener the interface which gets called with possible outcomes
     */
    //Big ol hacks
    //https://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/41627149
    //simple functions
    //https://www.thedroidsonroids.com/blog/bluetooth-classic-vs-bluetooth-low-energy-on-android-hints-implementation-steps
    public void connect(@NonNull String server_name, @NonNull connectListener connection_listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!bluetooth_adapter.isEnabled()) {
                Log.e("OOF", "Bluetooth is off");
                connection_listener.noBluetooth();
                return;
            }

            connection_listener.isPending();

            broadcast_handler.startBroadcast(device -> {
                if (device == null) {
                    connection_listener.notFound();
                    if (bluetooth_adapter.isDiscovering())
                        bluetooth_adapter.cancelDiscovery();
                    broadcast_handler.stopBroadcast();
                    return;
                }
                if (device.getName() == null)
                    return;
                String name = device.getName();
                String address = device.getAddress();
                Log.e("Found", "Name: "+name+". MAC: "+address);
                if (name.equals(server_name)) {
                    if (bluetooth_adapter.isDiscovering()) {
                        bluetooth_adapter.cancelDiscovery();
                        broadcast_handler.stopBroadcast();
                    }

                    try {
                        bluetooth_socket = device.createRfcommSocketToServiceRecord(uuid);
                        //Reflection hack required
//                        final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
//                        bluetooth_socket = (BluetoothSocket) m.invoke(device, uuid);

                        Class<?> clazz = device.getClass();
                        Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                        Object[] params = new Object[] {1};
                        bluetooth_socket = (BluetoothSocket) m.invoke((device), params);

                        device.createBond();
                        connection_listener.isConnected();
                    } catch (Exception e) {
                        Log.e("OOF", "Bluetooth hack required most likely:\n"+e);
                        connection_listener.notConnected();
                    }
                }
            });
            if (bluetooth_adapter.isDiscovering())
                bluetooth_adapter.cancelDiscovery();
            bluetooth_adapter.startDiscovery();
        });
    }

    public void disconnect() {}

//    public void connect(@NonNull String server_name, @NonNull connectListener connection_listener) {
//        Log.e("OOF", "Connect called");
//        ScheduledExecutorService sof = Executors.newSingleThreadScheduledExecutor();
//        sof.scheduleWithFixedDelay(() -> {
//            connection_listener.isPending();
//
//            if (!bluetooth_adapter.isEnabled()) {
//                Log.e("OOF", "Bluetooth is off");
//                ThreadUtil.sleep(2000);
//                connection_listener.noBluetooth();
//                return;
//            }
//
//            String device_mac = queryPaired(server_name);
//            if (device_mac == null) {
//                Log.e("OOF", "Device mac not found");
//                ThreadUtil.sleep(2000);
//                connection_listener.notFound();
//                return;
//            }
//
//            BluetoothDevice device = bluetooth_adapter.getRemoteDevice(device_mac);
//            try {
//                bluetooth_socket = device.createRfcommSocketToServiceRecord(uuid);
//            } catch (IOException e) {
//                Log.e("OOF", "Bluetooth is off");
//                ThreadUtil.sleep(2000);
//                connection_listener.noBluetooth();
//                return;
//            }
//
//            if (bluetooth_socket != null && bluetooth_socket.isConnected()) {
//                Log.e("OOF", "Device connected!");
//                connection_listener.isConnected();
//                sof.shutdown();
//            }
//
//            Log.e("OOF", "Device was found but not connected");
//            connection_listener.notConnected();
//        }, 0, 4, TimeUnit.SECONDS);

//    }
//    /**
//     * Queries all known (previously connected) devices
//     */
//    @WorkerThread
//    private @Nullable String queryPaired(@NonNull String server_name) {
//        Set<BluetoothDevice> paired_devices = bluetooth_adapter.getBondedDevices();
//        if (paired_devices.size() > 0) {
////            Log.e("Found", "There are paired devices");
//            for (BluetoothDevice device : paired_devices) {
//                String device_name = device.getName();
//                String device_mac = device.getAddress();
////                Log.e("Found", "Name: "+device_name+". MAC: "+device_mac);
//                if (device_name.equals(server_name))
//                    return device_mac;
//            }
//        } else {
////            Log.e("Found", "There are NO paired devices");
//        }
//        return null;
//    }

    private void sendImage(@NonNull Image image) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (bluetooth_socket.isConnected()) {
                try {
                    OutputStream out = bluetooth_socket.getOutputStream();
                    out.write(image.getWidth());
                    out.write(image.getHeight());
                    out.write(image.getBitmapBytes());
                } catch (Exception ignored){}

                try {
                    InputStream in = bluetooth_socket.getInputStream();
                    int result = in.read();
                    if (result == 0) {
                        //TODO: success
                    } else {
                        //TODO: failure
                    }
                    Thread.sleep(500);
                } catch (Exception ignored){}
            }
        });

    }
}

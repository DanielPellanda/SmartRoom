package room.app.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.function.Consumer;

import room.app.Config;

/**
 * Class used for manage the connection between two bluetooth sockets.
 */
@SuppressLint("MissingPermission")
public class BluetoothConnector extends Thread {
    private static final int REQUEST_PERMISSION_CONNECT = 758;
    private static final int LEGACY_REQUEST_PERMISSION_BLUETOOTH = 555;

    public static final BluetoothAdapter BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();
    private final Consumer<BluetoothSocket> handler;
    private Runnable disconnectionHandle = null;
    private final Activity contextActivity;
    private final BluetoothSocket socket;

    public BluetoothConnector(final Activity contextActivity, final BluetoothDevice device, final Consumer<BluetoothSocket> handler) {
        // Use a temporary object that is later assigned to socket
        // because socket is final.
        this.contextActivity = contextActivity;
        this.handler = handler;

        BluetoothSocket tmp = null;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            if (areBluetoothPermissionDenied(contextActivity)) {
                requestBluetoothPermissions(contextActivity);
            }
            tmp = device.createRfcommSocketToServiceRecord(Config.DEFAULT_DEVICE_UUID);
        } catch (IOException e) {
            Log.e(Config.TAG, "Socket's create() method failed", e);
        }
        socket = tmp;
    }

    public BluetoothConnector(final Activity contextActivity, final BluetoothDevice device, final Consumer<BluetoothSocket> handler, final Runnable disconnectionHandle) {
        this(contextActivity, device, handler);
        this.disconnectionHandle = disconnectionHandle;
    }

    public static boolean isBluetoothUnsupported() {
        return BLUETOOTH_ADAPTER == null;
    }

    public static boolean areBluetoothPermissionDenied(final Activity contextActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        }
        return ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestBluetoothPermissions(final Activity contextActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            contextActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION}, LEGACY_REQUEST_PERMISSION_BLUETOOTH);
            return;
        }
        contextActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSION_CONNECT);
    }

    @Override
    public void run() {
        if (areBluetoothPermissionDenied(contextActivity)) {
            requestBluetoothPermissions(contextActivity);
        }
        // Cancel discovery because it otherwise slows down the connection.
        BLUETOOTH_ADAPTER.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            socket.connect();
        } catch (IOException connectException) {
            Log.e(Config.TAG, "unable to connect");
            // Unable to connect; close the socket and return.
            cancel();
            if (disconnectionHandle != null) {
                disconnectionHandle.run();
            }
            return;
        }
        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        handler.accept(socket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(Config.TAG, "Could not close the client socket", e);
        }
    }
}
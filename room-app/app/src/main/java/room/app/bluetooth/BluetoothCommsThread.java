package room.app.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.function.Consumer;

import room.app.Config;

@SuppressLint("ParcelCreator")
@SuppressWarnings("MissingPermission")
public class BluetoothCommsThread extends Thread implements Parcelable{
    private final BluetoothSocket socket;
    private final BluetoothAdapter btAdapter;

    public BluetoothCommsThread(final BluetoothDevice device, final BluetoothAdapter btAdapter) {
        // Use a temporary object that is later assigned to socket
        // because socket is final.
        this.btAdapter = btAdapter;

        BluetoothSocket tmp = null;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(Config.DEFAULT_DEVICE_UUID);
        } catch (IOException e) {
            Log.e(Config.TAG, "Socket's create() method failed", e);
        }
        socket = tmp;
    }

    public void run(final Consumer<BluetoothSocket> handler) {
        // Cancel discovery because it otherwise slows down the connection.
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            socket.connect();
        } catch (IOException connectException) {
            Log.e(Config.TAG, "unable to connect");
            // Unable to connect; close the socket and return.
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(Config.TAG, "Could not close the client socket", closeException);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
    }
}
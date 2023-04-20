package room.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;

import room.app.Config;
import room.app.R;
import room.app.activity.MainActivity;
import room.app.bluetooth.BluetoothConnector;
import room.app.databinding.LoadFragmentBinding;

/**
 * Class used for describing the behaviour of the load fragment.
 */
public class LoadFragment extends Fragment {
    private enum Status {INIT, CONNECT, ERROR, UNSUPPORTED, DISCONNECT, TIMEOUT, FAIL}
    private static final long MILLIS_AFTER_BT_DEV_PICKER = 3 * 1000;
    private static final long MILLIS_TIMEOUT_TIMER = 10 * 1000;
    private static final int CONNECTION_ATTEMPT_LIMIT = 20;

    private boolean connectionSuccessful = false;
    private boolean intentStarted = false;
    private BluetoothDevice devicePicked = null;
    private BluetoothConnector btConnector = null;
    private Intent btDevicePicker = null;
    private Activity parentActivity = null;
    private LoadFragmentBinding binding = null;

    private final BroadcastReceiver eventListener = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if ("android.bluetooth.devicepicker.action.DEVICE_SELECTED".compareTo(action) == 0) {
                devicePicked = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = LoadFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity = requireActivity();

        final IntentFilter devicePickFilter = new IntentFilter("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
        parentActivity.registerReceiver(eventListener, devicePickFilter);

        btDevicePicker = new Intent("android.bluetooth.devicepicker.action.LAUNCH");
        btDevicePicker.putExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE", "room.app.fragment.load");
        btDevicePicker.putExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS", eventListener.getClass().getName());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        if (BluetoothConnector.isBluetoothUnsupported()) {
            updateComponents(Status.UNSUPPORTED);
            return;
        }
        BluetoothConnector.requireBluetoothPermissions(parentActivity);
        if (devicePicked == null) {
            if (!intentStarted) {
                new Handler().postDelayed(() -> startActivity(btDevicePicker), MILLIS_AFTER_BT_DEV_PICKER);
                intentStarted = true;
            }
            return;
        }
        Log.i(Config.TAG, "Device picked: " + devicePicked.getName());
        updateComponents(Status.CONNECT);
        if (btConnector == null) {
            btConnector = new BluetoothConnector(parentActivity, devicePicked, this::testConnection);
            btConnector.start();
            new Thread(this::waitForConnection).start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        updateComponents(Status.INIT);
        if (btConnector != null) {
            btConnector.cancel();
        }
        devicePicked = null;
        intentStarted = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentActivity.unregisterReceiver(eventListener);
        parentActivity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Tests the connection of a bluetooth socket.
     * @param socket the socket to test.
     */
    private void testConnection(final BluetoothSocket socket) {
        try {
            socket.getOutputStream();
            socket.getInputStream();
            Log.i(Config.TAG, "Connection successful!");
            connectionSuccessful = true;
        } catch (IOException e) {
            Log.e(Config.TAG, "Error occurred when creating output stream", e);
        }
    }

    /**
     * Updates the UI components according to the current status of the task.
     * @param status the new status to apply.
     */
    private void updateComponents(final Status status) {
        String text;
        int visibility = View.INVISIBLE;
        switch (status) {
            case INIT:
                text = getString(R.string.label_load_str_init);
                visibility = View.VISIBLE;
                break;
            case CONNECT:
                text = getString(R.string.label_load_str_pair);
                visibility = View.VISIBLE;
                break;
            case UNSUPPORTED:
                text = getString(R.string.label_load_str_unsupp);
                break;
            case FAIL:
                text = getString(R.string.label_load_str_fail_conn);
                break;
            case TIMEOUT:
                text = getString(R.string.label_load_str_fail_conn) + getString(R.string.label_load_str_timeout);
                break;
            case DISCONNECT:
                text = getString(R.string.label_load_str_disconn);
                visibility = View.VISIBLE;
                break;
            case ERROR:
            default:
                text = getString(R.string.label_load_str_fail);
                break;
        }
        binding.labelLoad.setText(text);
        binding.progbarLoad.setVisibility(visibility);
    }

    /**
     * Awaits for a response from socket.connect() and handles it.
     * It's suggested to run this method behind a seperate thread, to avoid leaving the
     * program stuck while waiting for the connection.
     */
    private void waitForConnection() {
        long timePassed = 0;
        while(btConnector.isAlive()) {
            if (timePassed < MILLIS_TIMEOUT_TIMER) {
                final long timeToSleep = MILLIS_TIMEOUT_TIMER / CONNECTION_ATTEMPT_LIMIT;
                timePassed += timeToSleep;
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    Log.e(Config.TAG, "Thread sleep interrupted.", e);
                }
                continue;
            }
            parentActivity.runOnUiThread(() -> updateComponents(Status.TIMEOUT));
            btConnector.cancel();
            btConnector = null;
            return;
        }
        if (!connectionSuccessful) {
            parentActivity.runOnUiThread(() -> updateComponents(Status.FAIL));
            btConnector = null;
            return;
        }
        moveToNextFragment();
        btConnector = null;
    }

    /**
     * After retrieved the BluetoothDevice instance of the device selected by the user,
     * the program proceeds to move the next fragment.
     */
    private void moveToNextFragment() {
        ((MainActivity) parentActivity).setDevice(devicePicked);
        parentActivity.runOnUiThread(() -> NavHostFragment.findNavController(LoadFragment.this).navigate(R.id.action_load_to_form_fragment));
    }
}

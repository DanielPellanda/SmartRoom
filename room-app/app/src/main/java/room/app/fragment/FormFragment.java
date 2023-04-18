package room.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import room.app.Config;
import room.app.R;
import room.app.activity.MainActivity;
import room.app.bluetooth.BluetoothConnector;
import room.app.databinding.FormFragmentBinding;

/**
 * Class used for describing the behaviour of the form fragment.
 */
public class FormFragment extends Fragment {
    private static final long UPDATE_INTERVAL = 1000;

    private ControlStatus status = ControlStatus.AUTO;
    private OutputStream outputWriter = null;
    private BluetoothConnector dataUpdater = null;
    private Activity parentActivity = null;
    private FormFragmentBinding binding = null;

    private enum ControlStatus {UNDEFINED(-1), AUTO(0), DASHBOARD(1), APP(2);
        private final int value;
        ControlStatus(final int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FormFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity = requireActivity();
        binding.buttonApply.setOnClickListener(v -> new Thread(() -> writeMessage(ControlStatus.APP)).start());
        binding.buttonRelease.setOnClickListener(v -> new Thread(() -> writeMessage(ControlStatus.AUTO)).start());
        binding.seekbarRollb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.textRollb.setText(i + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        BluetoothConnector.requireBluetoothPermissions(parentActivity);
        final BluetoothDevice btDevice = ((MainActivity) parentActivity).getDevice();
        if (btDevice == null) {
            Log.e(Config.TAG, "No device connected.");
            return;
        }
        Log.i(Config.TAG, "Device connected: " + btDevice.getName());
        dataUpdater = new BluetoothConnector(parentActivity, btDevice, this::updateData);
        dataUpdater.start();
        new Thread(this::checkConnection).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dataUpdater != null) {
            dataUpdater.cancel();
            dataUpdater = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        parentActivity = null;
    }

    /**
     * Submits a request for the control of the room to Arduino.
     * @param requestStatus the new room status to apply.
     */
    private void writeMessage(final ControlStatus requestStatus) {
        if (outputWriter == null) {
            return;
        }
        try {
            final String[] data = {
                    String.valueOf(requestStatus.getValue()),
                    String.valueOf(binding.switchLight.isChecked() ? 1 : 0),
                    String.valueOf(binding.seekbarRollb.getProgress())
            };
            final String message = data[0] + ";" + data[1] + ";" + data[2] + "\n";
            outputWriter.write(message.getBytes());
            Log.i(Config.TAG, "Sent " + message.getBytes().length + " bytes to Arduino. Content:\n" + message);
        } catch (IOException e) {
            Log.e(Config.TAG, "Error occurred. Write in output stream failed. Details:", e);
        }
    }

    /**
     * Updates the data received from Arduino.
     * @param socket the socket used for the connection.
     */
    private void updateData(final BluetoothSocket socket) {
        Lifecycle.State currLifecycleState = getLifecycle().getCurrentState();
        try {
            final InputStream input = socket.getInputStream();
            outputWriter = socket.getOutputStream();
            String bufferLeftover = "";

            Log.i(Config.TAG, "Initializing data updater. ");
            while (isInRuntimeState(currLifecycleState)) {
                final int bytesAvailable = input.available();
                if (bytesAvailable > 0) {
                    final byte[] buffer = new byte[bytesAvailable];
                    final int numBytes = input.read(buffer);
                    final String message = new String(buffer);
                    Log.i(Config.TAG, "Received " + numBytes + " bytes from Arduino. Content:\n" + message);

                    final String[] messageLines = (bufferLeftover+message).split("\n");
                    if (messageLines.length > 1) {
                        final String[] data = messageLines[messageLines.length-2].split(";");
                        parentActivity.runOnUiThread(() -> {
                            try {
                                updateStatusFromInt(Integer.parseInt(data[0]));
                            } catch (NumberFormatException n) {
                                Log.e(Config.TAG, "Invalid data format received.\n" + n);
                            }
                        });
                    }
                    bufferLeftover = messageLines[messageLines.length-1];
                }
                Thread.sleep(UPDATE_INTERVAL);
                currLifecycleState = getLifecycle().getCurrentState();
            }
            Log.i(Config.TAG, "Closing data updater. ");

        } catch (IOException e) {
            Log.e(Config.TAG, "Input/Output Error occurred. Details: ", e);
        } catch (InterruptedException e) {
            Log.e(Config.TAG, "Thread sleep interrupted. ", e);
        }
    }

    /**
     * Checks periorically if the data updater thread is still alive or not.
     */
    private void checkConnection() {
        while(true) {
            if (dataUpdater == null) {
                parentActivity.runOnUiThread(() -> NavHostFragment.findNavController(FormFragment.this).navigate(R.id.action_form_to_load_fragment));
                return;
            } else if (!dataUpdater.isAlive()) {
                parentActivity.runOnUiThread(() -> NavHostFragment.findNavController(FormFragment.this).navigate(R.id.action_form_to_load_fragment));
                return;
            }
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                Log.e(Config.TAG, "Thread sleep interrupted. ", e);
            }
        }
    }

    /**
     * Updates the current status of the room and the UI components related to it.
     * @param value an integer value representing the new status to apply.
     */
    private void updateStatusFromInt(final int value) {
        switch(value) {
            case 0:
                binding.buttonApply.setEnabled(true);
                binding.buttonRelease.setEnabled(false);
                binding.labelStatus.setText(status.name().toUpperCase());
                status = ControlStatus.AUTO;
                break;
            case 1:
                binding.buttonApply.setEnabled(false);
                binding.buttonRelease.setEnabled(false);
                binding.labelStatus.setText(status.name().toUpperCase());
                status = ControlStatus.DASHBOARD;
            case 2:
                binding.buttonApply.setEnabled(true);
                binding.buttonRelease.setEnabled(true);
                binding.labelStatus.setText(status.name().toUpperCase());
                status = ControlStatus.APP;
                break;
            default:
                binding.buttonApply.setEnabled(false);
                binding.buttonRelease.setEnabled(false);
                binding.labelStatus.setText(status.name().toUpperCase());
                status = ControlStatus.UNDEFINED;
                break;
        }
    }

    /**
     * @param currLifecycleState the current lifecycle state of the fragment.
     * @return true if the fragment is still in a runtime state (when it's not paused, stopped or destroyed), false if not.
     */
    private boolean isInRuntimeState(final Lifecycle.State currLifecycleState) {
        return currLifecycleState != Lifecycle.State.DESTROYED && currLifecycleState != Lifecycle.State.CREATED;
    }
}

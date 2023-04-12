package room.app.fragment;

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
import room.app.bluetooth.BluetoothConnector;
import room.app.databinding.FormFragmentBinding;

/**
 * Class used for describing the behaviour of the form fragment.
 */
public class FormFragment extends Fragment {
    private static final int BUFFER_SIZE = 1024;
    private static final int UPDATE_INTERVAL = 1000;

    private BluetoothConnector backgroundUpdate = null;
    private BluetoothDevice btDevice = null;
    private ControlStatus status = ControlStatus.AUTO;
    private Boolean onPause = false;
    private Activity parentActivity;
    private FormFragmentBinding binding;

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
        getParentFragmentManager().setFragmentResultListener(Config.REQUEST_BT_KEY, this, (requestKey, result) -> btDevice = result.getParcelable(Config.REQUEST_BT_DEVICE_KEY));
        binding.buttonApply.setOnClickListener(v -> writeMessage(ControlStatus.APP));
        binding.buttonRelease.setOnClickListener(v -> writeMessage(ControlStatus.AUTO));
        binding.seekbarRollb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                parentActivity.runOnUiThread(() -> binding.textRollb.setText(String.format(String.valueOf(R.string.text_rollb_string), i)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (btDevice == null) {
            return;
        }
        backgroundUpdate = new BluetoothConnector(parentActivity, btDevice, this::extractData, () -> NavHostFragment.findNavController(FormFragment.this).navigate(R.id.action_form_to_load_fragment));
        backgroundUpdate.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        onPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        onPause = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (backgroundUpdate != null) {
            backgroundUpdate.cancel();
            backgroundUpdate = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void writeMessage(final ControlStatus requestStatus) {
        if (btDevice == null) {
            return;
        }
        final BluetoothConnector btConnection = new BluetoothConnector(parentActivity, btDevice, socket -> {
            try {
                final OutputStream outputStream = socket.getOutputStream();
                final String[] data = {
                        String.valueOf(requestStatus.getValue()),
                        String.valueOf(binding.switchLight.isChecked() ? 1 : 0),
                        String.valueOf(binding.seekbarRollb.getProgress())
                };
                final String message = data[0] + ";" + data[1] + ";" + data[2];
                outputStream.write(message.getBytes());
            } catch (IOException e) {
                Log.e(Config.TAG, "Error occurred when creating output stream", e);
            }
        });
        btConnection.start();
    }

    private void extractData(final BluetoothSocket socket) {
        while(getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED) {
            if (!onPause) {
                try {
                    final InputStream in = socket.getInputStream();
                    final byte[] buffer = new byte[BUFFER_SIZE];
                    final int numBytes = in.read(buffer);
                    Log.i(Config.TAG, "Read " + numBytes + " bytes from Arduino");

                    final String[] data = new String(buffer).split(";");
                    updateStatusFromInt(Integer.parseInt(data[0]));
                } catch (IOException e) {
                    Log.e(Config.TAG, "Error occurred when creating input stream", e);
                }
            }
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateStatusFromInt(final int value) {
        switch(value) {
            case 0:
                parentActivity.runOnUiThread(() -> {
                    binding.buttonApply.setEnabled(true);
                    binding.buttonRelease.setEnabled(false);
                    binding.labelStatus.setText(status.name().toUpperCase());
                });
                status = ControlStatus.AUTO;
                break;
            case 1:
                parentActivity.runOnUiThread(() -> {
                    binding.buttonApply.setEnabled(false);
                    binding.buttonRelease.setEnabled(false);
                    binding.labelStatus.setText(status.name().toUpperCase());
                });
                status = ControlStatus.DASHBOARD;
            case 2:
                parentActivity.runOnUiThread(() -> {
                    binding.buttonApply.setEnabled(true);
                    binding.buttonRelease.setEnabled(true);
                    binding.labelStatus.setText(status.name().toUpperCase());
                });
                status = ControlStatus.APP;
                break;
            default:
                parentActivity.runOnUiThread(() -> {
                    binding.buttonApply.setEnabled(false);
                    binding.buttonRelease.setEnabled(false);
                    binding.labelStatus.setText(status.name().toUpperCase());
                });
                status = ControlStatus.UNDEFINED;
                break;
        }
    }

    private enum ControlStatus {UNDEFINED(-1), AUTO(0), DASHBOARD(1), APP(2);
        private final int value;
        ControlStatus(final int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }
}

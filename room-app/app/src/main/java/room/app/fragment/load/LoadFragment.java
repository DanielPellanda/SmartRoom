package room.app.fragment.load;

import android.bluetooth.BluetoothAdapter;
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
import room.app.bluetooth.BluetoothCommsThread;
import room.app.databinding.LoadFragmentBinding;

public class LoadFragment extends Fragment {
    private enum Status {INIT, PAIR, FAIL, UNSUPP}
    private static final long MILLIS_AFTER_BT_DEV_PICKER = 3000;
    private boolean connectionSuccessful = false;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice devicePicked;
    private Intent btDevicePicker;
    private LoadFragmentBinding binding;

    private final BroadcastReceiver eventListener = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if ("android.bluetooth.devicepicker.action.DEVICE_SELECTED".compareTo(action) == 0) {
                devicePicked = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
        }
    };

    private void updateComponents(final Status new_status) {
        String text;
        int visibility = View.INVISIBLE;
        switch (new_status) {
            case PAIR:
                text = String.valueOf(R.string.label_load_str_pair);
                visibility = View.VISIBLE;
                break;
            case FAIL:
                text = String.valueOf(R.string.label_load_str_fail);
                break;
            case UNSUPP:
                text = String.valueOf(R.string.label_load_str_unsupp);
                break;
            case INIT:
            default:
                text = String.valueOf(R.string.label_load_str_init);
                break;
        }
        binding.labelLoad.setText(text);
        binding.progbarLoad.setVisibility(visibility);
    }

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
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            updateComponents(Status.UNSUPP);
            return;
        }

        final IntentFilter devicePickFilter = new IntentFilter("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
        requireActivity().registerReceiver(eventListener, devicePickFilter);

        btDevicePicker = new Intent("android.bluetooth.devicepicker.action.LAUNCH");
        btDevicePicker.putExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE", "room.app.fragment.load");
        btDevicePicker.putExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS", eventListener.getClass().getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (btAdapter == null){
            return;
        }
        if (devicePicked == null) {
            new Handler().postDelayed(() -> startActivity(btDevicePicker), MILLIS_AFTER_BT_DEV_PICKER);
            return;
        }
        requireActivity().runOnUiThread(() -> updateComponents(Status.PAIR));
        final BluetoothCommsThread btConnector = new BluetoothCommsThread(devicePicked, btAdapter);
        btConnector.run(this::testConnection);
        if (!connectionSuccessful) {
            requireActivity().runOnUiThread(() -> updateComponents(Status.FAIL));
            btConnector.cancel();
            return;
        }
        final Bundle b = new Bundle();
        b.putParcelable("bluetooth", btConnector);
        getParentFragmentManager().setFragmentResult("bluetooth", b);
        NavHostFragment.findNavController(LoadFragment.this).navigate(R.id.action_load_to_form_fragment);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        requireActivity().unregisterReceiver(eventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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
}

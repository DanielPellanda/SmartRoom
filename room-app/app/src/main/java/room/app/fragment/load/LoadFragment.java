package room.app.fragment.load;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import room.app.R;
import room.app.databinding.LoadFragmentBinding;

public class LoadFragment extends Fragment {
    private enum Status {INIT, PAIR, FAIL, UNSUPP}
    private static final long MILLIS_AFTER_BT_DEV_PICKER = 3000;

    private Status current_status = Status.INIT;
    //private BluetoothAdapter btAdapter;
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
        String text = "";
        int visibility = View.INVISIBLE;
        current_status = new_status;
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
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            updateComponents(Status.UNSUPP);
            return;
        }
        /*
        if (!btAdapter.isEnabled()) {
            final Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            final ActivityResultLauncher<Intent> checkResult = registerForActivityResult(new StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != Activity.RESULT_OK) {
                            updateComponents(Status.FAIL);
                        }
                    });
            checkResult.launch(enableBt);
            if (current_status == Status.FAIL) {
                return;
            }
        }*/
        final IntentFilter devicePickFilter = new IntentFilter("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
        requireActivity().registerReceiver(eventListener, devicePickFilter);

        btDevicePicker = new Intent("android.bluetooth.devicepicker.action.LAUNCH");
        btDevicePicker.putExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE", "room.app.fragment.load");
        btDevicePicker.putExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS", eventListener.getClass().getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (devicePicked == null) {
            new Handler().postDelayed(() -> startActivity(btDevicePicker), 3000);
            return;
        }
        updateComponents(Status.PAIR);
        NavHostFragment.findNavController(LoadFragment.this).navigate(R.id.action_load_to_form_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
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
}

package room.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import room.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private BluetoothDevice device = null;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
    }

    public void setDevice(final BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        final BluetoothDevice device = this.device;
        this.device = null;
        return device;
    }
}
package com.tunjid.rcswitchcontrol.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.tunjid.rcswitchcontrol.bluetooth.BluetoothLeService;
import com.tunjid.rcswitchcontrol.R;
import com.tunjid.rcswitchcontrol.abstractclasses.BaseActivity;
import com.tunjid.rcswitchcontrol.fragments.ControlFragment;
import com.tunjid.rcswitchcontrol.fragments.StartFragment;
import com.tunjid.rcswitchcontrol.model.RfSwitch;

import static com.tunjid.rcswitchcontrol.bluetooth.BluetoothLeService.BLUETOOTH_DEVICE;

public class MainActivity extends BaseActivity {

    public static final String GO_TO_SCAN = "GO_TO_SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences(RfSwitch.SWITCH_PREFS, MODE_PRIVATE);
        String lastConnectedDevice = preferences.getString(BluetoothLeService.LAST_PAIRED_DEVICE, "");
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        Intent startIntent = getIntent();

        // Retreive device from notification intent or shared preferences
        BluetoothDevice device = startIntent.hasExtra(BLUETOOTH_DEVICE)
                ? (BluetoothDevice) startIntent.getParcelableExtra(BLUETOOTH_DEVICE)
                : !TextUtils.isEmpty(lastConnectedDevice) && bluetoothAdapter != null && bluetoothAdapter.isEnabled()
                ? bluetoothAdapter.getRemoteDevice(lastConnectedDevice)
                : null;

        boolean isSavedInstance = savedInstanceState != null;
        boolean isNullDevice = device == null;

        if (!isNullDevice) {
            Intent intent = new Intent(this, BluetoothLeService.class);
            intent.putExtra(BLUETOOTH_DEVICE, device);
            startService(intent);
        }

        if (!isSavedInstance) {
            if (isNullDevice) showFragment(StartFragment.newInstance());
            else showFragment(ControlFragment.newInstance(device));
        }
    }
}

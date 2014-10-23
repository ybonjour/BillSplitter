package ch.pantas.billsplitter.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.View.VISIBLE;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class BluetoothDeviceSelector extends RoboActivity {

    private static final int REQUEST_ENABLE_BT = 0;

    @InjectView(R.id.beam_message)
    private TextView messageField;

    @InjectView(R.id.bluetooth_device_list)
    private ListView deviceList;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_device_selector);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            setUpNonBluetooth();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else{
                showPairedDevices();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_ENABLE_BT == requestCode){
            showPairedDevices();
        }
    }

    private void showPairedDevices(){
        checkNotNull(bluetoothAdapter);
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<PrettyDevice> prettyDevices = makePretty(pairedDevices);

        ArrayAdapter<PrettyDevice> adapter = new ArrayAdapter<PrettyDevice>(this, android.R.layout.simple_list_item_1, prettyDevices);

        deviceList.setAdapter(adapter);
        deviceList.setVisibility(VISIBLE);
    }

    private void setUpNonBluetooth() {
        messageField.setText(getString(R.string.bluetooth_not_available));
        messageField.setVisibility(VISIBLE);
    }

    private List<PrettyDevice> makePretty(Set<BluetoothDevice> devices){
        List<PrettyDevice> output = new LinkedList<PrettyDevice>();
        for(BluetoothDevice device : devices){
            output.add(new PrettyDevice(device));
        }

        return output;
    }

    public class PrettyDevice {

        private final BluetoothDevice device;

        public PrettyDevice(BluetoothDevice device) {
            checkNotNull(device);
            this.device = device;
        }

        @Override
        public String toString() {
            return device.getName() + " (" + device.getAddress() + ")";
        }
    }
}

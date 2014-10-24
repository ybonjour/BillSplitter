package ch.pantas.billsplitter.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;

import ch.pantas.billsplitter.remote.SimpleBluetoothServer;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NdefRecord.createMime;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static roboguice.RoboGuice.getInjector;

public class BeamEvent extends RoboActivity implements BluetoothListener {

    private static final int REQUEST_ENABLE_BT = 0;

    @InjectView(R.id.beam_message)
    private TextView beamMessageField;

    private SimpleBluetoothServer bluetoothServer;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_event);

        String message;
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (nfcAdapter != null && bluetoothAdapter != null) {
            message = getString(R.string.beam_event_message);
            nfcAdapter.setNdefPushMessage(createNdefMessage(bluetoothAdapter.getAddress()), this);
        } else {
            message = getString(R.string.beam_event_error);
        }

        beamMessageField.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter == null) return;
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startBluetoothServer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothServer != null) {
            bluetoothServer.cancel();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ENABLE_BT == requestCode) {
            startBluetoothServer();
        }
    }

    private void startBluetoothServer() {
        bluetoothServer = getInjector(this).getInstance(SimpleBluetoothServer.class).init(bluetoothAdapter, this);
        bluetoothServer.start();
    }

    private static NdefMessage createNdefMessage(String text) {
        return new NdefMessage(new NdefRecord[]{
                createMime(
                        "application/vnd.com.example.android.beam",
                        text.getBytes())
        });
    }

    @Override
    public void onMessageReceived(String message) {
        beamMessageField.setText(message);
    }

    @Override
    public void onConnected() {
        bluetoothServer.postMessage("A message");
    }
}
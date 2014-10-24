package ch.pantas.billsplitter.ui;

import android.app.Activity;
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

    private static final int REQUEST_ENABLE_BT = 1;

    @InjectView(R.id.beam_message)
    private TextView beamMessageField;

    private SimpleBluetoothServer bluetoothServer;
    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null ||
                bluetoothAdapter == null ||
                !nfcAdapter.isNdefPushEnabled() ||
                !bluetoothAdapter.isEnabled()) {
            setUpErrorScreen();
            return;
        }

        setUpTapScreen();
        nfcAdapter.setNdefPushMessage(createNdefMessage(bluetoothAdapter.getAddress()), this);
        startBluetoothServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothServer != null) {
            bluetoothServer.cancel();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ENABLE_BT == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                startBluetoothServer();
            } else {
                setUpErrorScreen();
            }
        }
    }

    private void setUpTapScreen() {
        String message = getString(R.string.beam_event_message);
        beamMessageField.setText(message);
    }

    private void setUpErrorScreen() {
        String message = getString(R.string.beam_event_error);
        beamMessageField.setText(message);
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
package ch.pantas.billsplitter.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import ch.pantas.billsplitter.remote.SimpleBluetoothClient;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED;
import static android.widget.Toast.makeText;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static roboguice.RoboGuice.getInjector;

public class BeamEventReceiver extends RoboActivity implements BluetoothListener {
    @InjectView(R.id.beam_receive_message)
    private TextView messageField;

    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    private SimpleBluetoothClient bluetoothClient;

    private String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_event_receiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (nfcAdapter == null || bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            setUpErrorScreen();
            return;
        }

        if (ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            deviceAddress = extractMessage(getIntent());
            startBluetoothClient();
            setUpWaitingScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothClient != null) {
            bluetoothClient.cancel();
            finish();
        }
    }

    private void setUpWaitingScreen() {
        String message = getString(R.string.beam_event_receiving);
        messageField.setText(message);
    }

    private void setUpErrorScreen() {
        String message = getString(R.string.beam_event_error);
        messageField.setText(message);
    }

    private void setUpSuccessScreen(String message){
        // TODO: handle correctly
        messageField.setText("Received message: " + message);
    }

    private void startBluetoothClient() {
        bluetoothClient = getInjector(this).getInstance(SimpleBluetoothClient.class).init(bluetoothAdapter, deviceAddress, this);
        bluetoothClient.start();
    }

    private String extractMessage(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage msg = (NdefMessage) rawMsgs[0];

        // record 0 contains the MIME type, record 1 is the AAR, if present
        return new String(msg.getRecords()[0].getPayload());
    }

    @Override
    public void onMessageReceived(String message) {
        messageField.append(message);
        setUpSuccessScreen(message);
        bluetoothClient.postMessage("An answer");
    }

    @Override
    public void onConnected() {

    }
}

package ch.pantas.billsplitter.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import ch.pantas.billsplitter.remote.SimpleBluetoothClient;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.widget.Toast.makeText;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static roboguice.RoboGuice.getInjector;

public class BeamEventReceiver extends RoboActivity implements BluetoothListener {

    private static final int REQUEST_ENABLE_BT = 0;

    @InjectView(R.id.beam_receive_message)
    private TextView messageField;

    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    private SimpleBluetoothClient bluetoothClient;

    private String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_event_receiver);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (nfcAdapter == null || bluetoothAdapter == null) {
            makeText(this, "NO NFC...", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            deviceAddress = extractMessage(getIntent());
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                startBluetoothClient();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothClient != null) {
            bluetoothClient.cancel();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ENABLE_BT == requestCode) {
            startBluetoothClient();
        }
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
        bluetoothClient.postMessage("An answer");
    }

    @Override
    public void onConnected() {

    }
}

package ch.pantas.billsplitter.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class BeamBaseActivity extends RoboActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    @InjectView(R.id.beam_message)
    private View beamMessage;

    @InjectView(R.id.beam_message_text)
    private TextView beamMessageText;

    @InjectView(R.id.beam_message_image)
    private ImageView beamMessageImage;

    @InjectView(R.id.beam_indicator_view)
    private LinearLayout beamIndicator;

    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        beamIndicator.setVisibility(GONE);
        beamMessage.setVisibility(GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ENABLE_BT == requestCode) {
            setUpForBeam();
        }
    }

    protected void setUpForBeam(){
        if (readyToBeam()) {
            startBeaming(nfcAdapter, bluetoothAdapter);
        } else {
            setUpServicesDisabledScreen(isBluetoothEnabled(), isNfcEnabled(), isNfcBeamEnabled());
        }
    }

    protected void setUpErrorScreen(int messageResource) {
        showMessage(messageResource, R.drawable.ic_beam_touch);
    }

    protected void setUpCommunicationErrorScreen() {
        showMessage(R.string.beam_communication_error, R.drawable.ic_beam_touch);
    }

    protected void setUpWaitingScreen() {
        showMessage(R.string.beam_event_sending, R.drawable.ic_beam_touch);
    }


    protected void setUpSuccessScreen() {
        showMessage(getString(R.string.beam_received), R.drawable.ic_beam_touch);
    }

    protected void showMessage(int messageResId, int imageResource) {
        showMessage(getString(messageResId), imageResource);
    }

    private void showMessage(String text, int imageResource) {
        beamMessage.setVisibility(VISIBLE);
        beamIndicator.setVisibility(GONE);
        beamMessageImage.setImageResource(imageResource);
        beamMessageText.setText(text);
    }

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private boolean isNfcEnabled() {
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    private boolean isNfcBeamEnabled() {
        return nfcAdapter != null && nfcAdapter.isNdefPushEnabled();
    }

    protected void setUpServicesDisabledScreen(boolean bluetoothEnabled, boolean nfcEnabled, boolean beamEnabled) {
        beamIndicator.setVisibility(VISIBLE);
        beamMessage.setVisibility(GONE);

        View bluetoothIndicator = findViewById(R.id.beam_bluetooth_indicator);
        bluetoothIndicator.setVisibility(bluetoothEnabled ? GONE : VISIBLE);

        View nfcIndicator = findViewById(R.id.beam_nfc_indicator);
        nfcIndicator.setVisibility(nfcEnabled ? GONE : VISIBLE);

        View nfcBeamIndicator = findViewById(R.id.beam_nfc_beam_indicator);
        nfcBeamIndicator.setVisibility(beamEnabled ? GONE : VISIBLE);
    }

    protected void hideMessageAndIndicator(){
        beamMessage.setVisibility(GONE);
        beamIndicator.setVisibility(GONE);
    }

    private boolean readyToBeam(){
        return isBluetoothEnabled() && isNfcEnabled() && isNfcBeamEnabled();
    }

    public void enableBluetooth(View v) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    public void enableNfc(View v) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    protected abstract void startBeaming(NfcAdapter nfcAdapter, BluetoothAdapter bluetoothAdapter);

    protected abstract int getContentView();
}

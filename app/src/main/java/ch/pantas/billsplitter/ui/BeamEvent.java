package ch.pantas.billsplitter.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.remote.SimpleBluetoothServer;
import ch.pantas.billsplitter.services.ImportService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder;
import ch.pantas.billsplitter.services.datatransfer.EventDtoOperator;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NdefRecord.createMime;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder.convertToJson;
import static roboguice.RoboGuice.getInjector;

public class BeamEvent extends RoboActivity implements BluetoothListener {

    public static final String ARGUMENT_EVENT_ID = "event_id";
    private static final int REQUEST_ENABLE_BT = 1;

    @InjectView(R.id.beam_message)
    private TextView beamMessageField;

    @Inject
    private EventStore eventStore;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private UserStore userStore;
    @Inject
    private UserService userService;
    @Inject
    private ImportService importService;

    private Event event;

    private SimpleBluetoothServer bluetoothServer;
    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_event);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        View beamIndicator = findViewById(R.id.beam_indicator_view);
        beamIndicator.setVisibility(View.GONE);

        View beamTouchImage = findViewById(R.id.beam_touch_image);
        beamTouchImage.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        event = eventStore.getById(eventId);
        String titleTemplate = getString(R.string.beam_event_title_template);
        setTitle(String.format(titleTemplate, event.getName()));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        boolean bluetoothEnabled = getBluetoothEnabled();
        boolean nfcEnabled = getNfcEnabled();
        boolean nfcBeamEnabled = getNfcBeamEnabled();

        if (!bluetoothEnabled || !nfcEnabled || !nfcBeamEnabled) {
            setUpErrorScreen(bluetoothEnabled, nfcEnabled, nfcBeamEnabled);
            return;
        }

        setUpTapScreen();
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
            if (resultCode == Activity.RESULT_OK && (nfcAdapter != null && nfcAdapter.isNdefPushEnabled())) {
                setUpTapScreen();
                startBluetoothServer();
            } else {
                setUpErrorScreen(getBluetoothEnabled(), getNfcEnabled(), getNfcBeamEnabled());
            }
        }
    }

    private void setUpTapScreen() {
        View indicatorOverview = findViewById(R.id.beam_indicator_view);
        indicatorOverview.setVisibility(View.GONE);

        View beamTouchImage = findViewById(R.id.beam_touch_image);
        beamTouchImage.setVisibility(View.VISIBLE);
        showMessage(R.string.beam_event_message);
    }

    private void setUpErrorScreen(boolean bluetoothEnabled, boolean nfcEnabled, boolean beamEnabled) {

        if (bluetoothEnabled && nfcEnabled && beamEnabled) {
            showMessage("TODO ERROR"); // TODO
            View indicatorOverview = findViewById(R.id.beam_indicator_view);
            indicatorOverview.setVisibility(View.GONE);
        } else {
            View indicatorOverview = findViewById(R.id.beam_indicator_view);
            indicatorOverview.setVisibility(View.VISIBLE);

            View bluetoothIndicator = findViewById(R.id.beam_bluetooth_indicator);
            bluetoothIndicator.setVisibility(bluetoothEnabled ? View.GONE : View.VISIBLE);

            View nfcIndicator = findViewById(R.id.beam_nfc_indicator);
            nfcIndicator.setVisibility(nfcEnabled ? View.GONE : View.VISIBLE);

            View nfcBeamIndicator = findViewById(R.id.beam_nfc_beam_indicator);
            nfcBeamIndicator.setVisibility(beamEnabled ? View.GONE : View.VISIBLE);
        }
    }

    private void setUpCommunicationErrorScreen() {
        showMessage(R.string.beam_communication_error);
    }

    private void setUpWaitingScreen() {
        showMessage(R.string.beam_event_sending);
    }

    private void showMessage(int messageResId) {
        showMessage(getString(messageResId));
    }

    private void showMessage(String text) {
        beamMessageField.setText(text);
    }

    private void startBluetoothServer() {
        nfcAdapter.setNdefPushMessage(createNdefMessage(bluetoothAdapter.getAddress()), this);
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
        Gson gson = new Gson();
        EventDtoOperator eventDto = new EventDtoOperator(gson.fromJson(message, EventDto.class));
        importService.deepImportEvent(eventDto);
        showMessage(getString(R.string.beam_received));
    }

    @Override
    public void onConnected() {
        EventDtoBuilder builder = getInjector(this).getInstance(EventDtoBuilder.class);
        builder.withEventId(event.getId());
        bluetoothServer.postMessage(convertToJson(builder.build()));
        setUpWaitingScreen();
    }

    @Override
    public void onCommunicationError(Exception e) {
        setUpCommunicationErrorScreen();
    }

    public boolean getBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean getNfcEnabled() {
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    public boolean getNfcBeamEnabled() {
        return nfcAdapter != null && nfcAdapter.isNdefPushEnabled();
    }

    public void enableBluetooth(View v) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    public void enableNfc(View v) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
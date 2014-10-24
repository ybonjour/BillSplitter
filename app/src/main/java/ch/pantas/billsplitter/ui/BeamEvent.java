package ch.pantas.billsplitter.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
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
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NdefRecord.createMime;
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

    private Event event;

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
        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        event = eventStore.getById(eventId);
        String titleTemplate = getString(R.string.beam_event_title_template);
        setTitle(String.format(titleTemplate, event.getName()));

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
        showMessage(R.string.beam_event_message);
    }

    private void setUpErrorScreen() {
        showMessage(R.string.beam_event_error);
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

    private boolean participatesInMultipleEvents(User user) {
        return participantStore.getParticipantsForUsers(user.getId()).size() > 1;
    }

    @Override
    public void onMessageReceived(String message) {
        Gson gson = new Gson();
        ParticipantDto participantDto = gson.fromJson(message, ParticipantDto.class);
        User newUser = participantDto.user;
        Participant participant = participantStore.getById(participantDto.participantId);

        User oldUser = userStore.getById(participant.getUserId());
        removeOrRenameUser(oldUser);

        User existingUser = userStore.getById(newUser.getId());
        if (existingUser == null) {
            userStore.createExistingModel(newUser);
        }

        participant.setConfirmed(true);
        participant.setUserId(newUser.getId());
        participantStore.persist(participant);

        String messageTemplate = getString(R.string.beam_received_template);

        showMessage(String.format(messageTemplate, newUser.getName()));
    }

    private void removeOrRenameUser(User user) {
        if (!participatesInMultipleEvents(user)) {
            userStore.removeById(user.getId());
        } else {
            user.setName(userService.findBestFreeNameForUser(user));
            userStore.persist(user);
        }
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
}
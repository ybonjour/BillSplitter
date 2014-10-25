package ch.pantas.billsplitter.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.remote.SimpleBluetoothClient;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.ImportService;
import ch.pantas.billsplitter.services.LoginService;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder;
import ch.pantas.billsplitter.services.datatransfer.EventDtoOperator;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;
import ch.pantas.billsplitter.ui.adapter.BeamParticipantAdapter;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder.convertToJson;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkState;
import static roboguice.RoboGuice.getInjector;

public class BeamEventReceiver extends RoboActivity implements BluetoothListener {
    @InjectView(R.id.beam_receive_message)
    private TextView messageField;

    @InjectView(R.id.participant_list)
    private ListView participantsList;

    @InjectView(R.id.user_selection_container)
    private LinearLayout userSelectionContainer;

    @InjectView(R.id.join_as_new_user)
    private CheckBox joinCheckBox;

    @Inject
    private UserService userService;

    @Inject
    private UserStore userStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private LoginService loginService;

    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private ImportService importService;

    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    private SimpleBluetoothClient bluetoothClient;

    private String deviceAddress;

    private EventDtoOperator eventDto;
    private BeamParticipantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_event_receiver);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.beam_event_receiver, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean visible = getSelectedUser() != null;
        menu.findItem(R.id.action_import).setVisible(visible);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_import == item.getItemId()) {
            User selectedUser = getSelectedUser();
            checkNotNull(selectedUser);
            handleDto(selectedUser);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpWaitingScreen() {
        showMessage(R.string.beam_event_receiving);
        userSelectionContainer.setVisibility(GONE);
    }

    private void setUpErrorScreen() {
        showMessage(R.string.beam_event_error);
        userSelectionContainer.setVisibility(GONE);
    }

    private void setUpCommunicationErrorScreen() {
        showMessage(R.string.beam_communication_error);
        userSelectionContainer.setVisibility(GONE);
    }

    private void showMessage(int messageResId) {
        messageField.setText(getString(messageResId));
        messageField.setVisibility(VISIBLE);
    }

    private void setUpUserSelectionScreen(List<ParticipantDto> unconfirmedParticipants) {
        checkState(canShowList() || canShowCheckbox());

        messageField.setVisibility(GONE);
        userSelectionContainer.setVisibility(VISIBLE);

        if (canShowList()) {
            adapter = getInjector(this).getInstance(BeamParticipantAdapter.class);
            adapter.setParticipants(unconfirmedParticipants);

            // preselect Uer with my name
            User me = userService.getMe();
            if (me != null) {
                boolean success = adapter.selectParticipantByName(me.getName());
                joinCheckBox.setChecked(!success);
            }

            participantsList.setAdapter(adapter);
            participantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ParticipantDto participantDto = (ParticipantDto) adapterView.getItemAtPosition(i);
                    boolean success = adapter.select(participantDto.user);
                    joinCheckBox.setChecked(!success);
                    participantsList.invalidateViews();
                    invalidateOptionsMenu();
                }
            });

        }

        joinCheckBox.setVisibility(GONE);
        if (canShowCheckbox()) {
            joinCheckBox.setVisibility(VISIBLE);
            joinCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (joinCheckBox.isChecked()) {
                        disableList();
                    } else {
                        enableList();
                    }

                    invalidateOptionsMenu();
                }
            });
            joinCheckBox.setChecked(adapter == null || adapter.getSelected() == null);
        }

        invalidateOptionsMenu();
    }

    private void handleDto(User selectedUser) {
        User me = userService.getMe();
        if (me == null) {
            me = selectedUser;
            loginService.login(me);
        } else {
            eventDto.replaceUser(selectedUser, me);
        }

        importService.deepImportEvent(eventDto);
        sharedPreferenceService.storeActiveEventId(eventDto.getEvent().getId());

        EventDtoBuilder builder = getInjector(this).getInstance(EventDtoBuilder.class);
        builder.withEventId(eventDto.getEvent().getId());
        bluetoothClient.postMessage(convertToJson(builder.build()));

        activityStarter.startEventDetails(this, eventDto.getEvent(), true);
        finish();
    }


    private User getSelectedUser() {
        if (canShowCheckbox() && joinCheckBox.isChecked()) {
            return userService.getMe();
        } else if (canShowList() && adapter != null) {
            return adapter.getSelected();
        }

        return null;
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

    private void disableList() {
        if (adapter == null) return;

        adapter.disable();
        participantsList.setEnabled(false);
        participantsList.invalidateViews();
    }

    private void enableList() {
        if (adapter == null) return;

        adapter.enable();
        participantsList.setEnabled(true);
        participantsList.invalidateViews();
    }

    @Override
    public void onMessageReceived(String message) {
        messageField.append(message);

        eventDto = new EventDtoOperator(EventDtoBuilder.createFromJson(message));

        User me = userService.getMe();

        if (eventDto.hasParticipant(me)) {
            handleDto(me);
            return;
        }

        if (!canShowList() && !canShowCheckbox()) {
            showMessage(R.string.can_not_join_group);
            return;
        }

        setUpUserSelectionScreen(eventDto.getUnconfirmedParticipants());
    }

    private boolean canShowList() {
        if (eventDto == null) return false;

        return !eventDto.getUnconfirmedParticipants().isEmpty();
    }

    private boolean canShowCheckbox() {
        User me = userService.getMe();
        return me != null;
    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onCommunicationError(Exception e) {
        setUpCommunicationErrorScreen();
    }
}

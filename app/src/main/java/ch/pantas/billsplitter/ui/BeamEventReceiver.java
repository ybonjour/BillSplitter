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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.remote.SimpleBluetoothClient;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.services.datatransfer.AttendeeDto;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder;
import ch.pantas.billsplitter.services.datatransfer.ExpenseDto;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;
import ch.pantas.billsplitter.ui.adapter.BeamParticipantAdapter;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.util.UUID.randomUUID;
import static roboguice.RoboGuice.getInjector;

public class BeamEventReceiver extends RoboActivity implements BluetoothListener {
    @InjectView(R.id.beam_receive_message)
    private TextView messageField;

    @InjectView(R.id.participant_list)
    private ListView participantsList;

    @InjectView(R.id.participant_list_container)
    private LinearLayout participantListContainer;

    @InjectView(R.id.join_as_new_user)
    private Button joinButon;

    @Inject
    private UserService userService;

    @Inject
    private UserStore userStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private EventStore eventStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private ActivityStarter activityStarter;

    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;

    private SimpleBluetoothClient bluetoothClient;

    private String deviceAddress;

    private EventDto eventDto;
    private BeamParticipantAdapter adapter;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.beam_event_receiver, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void joinAsNewUser(View v){
        if (eventDto == null || adapter == null) return;

        User me = userService.getMe();
        checkNotNull(me);
        ParticipantDto dtoNew = new ParticipantDto();
        dtoNew.confirmed = true;
        dtoNew.participantId = randomUUID().toString();
        dtoNew.user = me;

        eventDto.participants.add(dtoNew);

        handleDto(dtoNew);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean visible = adapter != null && adapter.getCount() > 0;
        menu.findItem(R.id.action_import).setVisible(visible);

        return super.onPrepareOptionsMenu(menu);
    }

    private void handleDto(ParticipantDto selectedParticipant){
        User me = userService.getMe();
        if (me == null) {
            me = selectedParticipant.user;
            userService.storeMe(me);
        } else {
            replaceUserWithMe(eventDto, selectedParticipant.user, me);
            selectedParticipant.user = me;
        }

        store(eventDto, me);

        bluetoothClient.postMessage(new Gson().toJson(selectedParticipant));

        activityStarter.startEventDetails(this, eventDto.event, true);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_import == item.getItemId()) {
            if (eventDto == null || adapter == null) return true;

            ParticipantDto selectedParticipant = adapter.getSelected();
            handleDto(selectedParticipant);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceUserWithMe(EventDto eventDto, User user, User me) {
        for (ParticipantDto participant : eventDto.participants) {
            if (participant.user.equals(user)) {
                participant.user = me;
            }
        }
    }

    private void store(EventDto eventDto, User me) {
        Event existingEvent = eventStore.getById(eventDto.event.getId());
        Event event = eventDto.event;

        if (existingEvent != null) {
            if (event.getOwnerId() != me.getId()) {
                eventStore.persist(event);
            }

            if (eventDto.expenses.size() > 0) {
                String ownerId = eventDto.expenses.get(0).expense.getOwnerId();
                expenseStore.removeAll(event.getId(), ownerId);
                for (ExpenseDto expenseDto : eventDto.expenses) {
                    attendeeStore.removeAll(expenseDto.expense.getId());
                }
            }
        }
        else {
            eventStore.createExistingModel(event);
        }

        for (ParticipantDto participant : eventDto.participants) {

            Participant existingParticipant = participantStore.getById(participant.participantId);
            if (existingParticipant != null) {
                participantStore.persist(existingParticipant);
            }
            else {
                User user = participant.user;

                if (!user.equals(me)) {
                    User existingUser = userStore.getById(user.getId());
                    if (existingUser == null) {
                        user.setName(userService.findBestFreeNameForUser(user));
                        userStore.createExistingModel(user);
                    }
                }

                participantStore.createExistingModel(new Participant(participant.participantId, user.getId(), event.getId(), participant.confirmed));
            }

        }

        for (ExpenseDto expenseDto : eventDto.expenses) {
            Expense expense = expenseDto.expense;
            expenseStore.createExistingModel(expense);

            for (AttendeeDto attendeeDto : expenseDto.attendingParticipants) {
                Attendee attendee = new Attendee(attendeeDto.attendeeId, expense.getId(), attendeeDto.participantId);
                attendeeStore.createExistingModel(attendee);
            }
        }

        sharedPreferenceService.storeActiveEventId(event.getId());
    }

    private void setUpWaitingScreen() {
        showMessage(R.string.beam_event_receiving);
        participantListContainer.setVisibility(GONE);
    }

    private void setUpErrorScreen() {
        showMessage(R.string.beam_event_error);
        participantListContainer.setVisibility(GONE);
    }

    private void setUpCommunicationErrorScreen() {
        showMessage(R.string.beam_communication_error);
        participantListContainer.setVisibility(GONE);
    }

    private void showMessage(int messageResId) {
        messageField.setText(getString(messageResId));
        messageField.setVisibility(VISIBLE);
    }

    public ParticipantDto getParticipantFromDto(User user){
        checkNotNull(eventDto);
        for(ParticipantDto participantDto : eventDto.participants){
            if(participantDto.user.equals(user)) return participantDto;
        }
        return null;
    }

    private void setUpSuccessScreen(List<ParticipantDto> participants) {
        messageField.setVisibility(GONE);

        adapter = getInjector(this).getInstance(BeamParticipantAdapter.class);
        adapter.setParticipants(participants);

        User me = userService.getMe();

        Event existingEvent = eventStore.getById(eventDto.event.getId());
        ParticipantDto participantMe = getParticipantFromDto(me);
        if(existingEvent != null && participantMe != null){
            handleDto(participantMe);
            return;
        }

        if (me == null) {
            adapter.selectFirst();
            joinButon.setVisibility(GONE);
        } else {
            adapter.selectParticipantByName(me.getName());

            int visibility = adapter.hasUser(me) ? GONE : VISIBLE;
            joinButon.setVisibility(visibility);
        }
        participantsList.setAdapter(adapter);
        participantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ParticipantDto participantDto = (ParticipantDto) adapterView.getItemAtPosition(i);
                adapter.selectParticipantByName(participantDto.user.getName());
                participantsList.invalidateViews();
            }
        });
        participantListContainer.setVisibility(VISIBLE);
        invalidateOptionsMenu();
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

        eventDto = EventDtoBuilder.createFromJson(message);
        setUpSuccessScreen(eventDto.participants);
    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onCommunicationError(Exception e) {
        setUpCommunicationErrorScreen();
    }
}

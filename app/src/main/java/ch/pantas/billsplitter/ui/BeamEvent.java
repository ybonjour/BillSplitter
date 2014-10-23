package ch.pantas.billsplitter.ui;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;

import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.nfc.NdefRecord.createMime;

public class BeamEvent extends RoboActivity {

    public static final String ARGUMENT_EVENT_ID = "event_id";

    @InjectView(R.id.beam_message)
    private TextView beamMessageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_event);

        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);

        String message;
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            message = getString(R.string.beam_event_message);
            nfcAdapter.setNdefPushMessage(createNdefMessage(eventId), this);
        } else {
            message = getString(R.string.beam_event_error);
        }

        beamMessageField.setText(message);

    }


    private static NdefMessage createNdefMessage(String eventId) {
        String text = "Event: " + eventId;
        return new NdefMessage(new NdefRecord[]{
                createMime(
                        "application/vnd.com.example.android.beam",
                        text.getBytes())
        });
    }
}

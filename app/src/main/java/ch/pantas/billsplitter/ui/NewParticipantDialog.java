package ch.pantas.billsplitter.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.ui.fragment.ParticipantsFragment;
import ch.pantas.splitty.R;
import roboguice.fragment.RoboDialogFragment;

public class NewParticipantDialog extends RoboDialogFragment {

    public static final String USER = "user";
    public static final String EVENT_ID = "event_id";

    public static NewParticipantDialog newParticipantDialog(User user, UUID eventId) {
        NewParticipantDialog dialog = new NewParticipantDialog();

        Bundle args = new Bundle();
        args.putSerializable(USER, user);
        args.putSerializable(EVENT_ID, eventId);
        dialog.setArguments(args);

        return dialog;
    }

    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private UserStore userStore;

    @Inject
    private ParticipantStore participantStore;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final User user = (User) getArguments().getSerializable(USER);
        final UUID eventId = (UUID) getArguments().getSerializable(EVENT_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] items = new CharSequence[]{
                getActivity().getString(R.string.create_new_user),
                getActivity().getString(R.string.connect_user)
        };

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedIdx) {
                switch (selectedIdx) {
                    case 0:
                        userStore.persist(user);
                        Participant participant = new Participant(user.getId(), eventId, false, 0);
                        participantStore.persist(participant);
                        Fragment targetFragment = getTargetFragment();
                        if (targetFragment != null) {
                            ((ParticipantsFragment)targetFragment).updateWithNewUser(user);
                        }
                        dismiss();
                        break;
                    case 1:
                        dismiss();
                        activityStarter.startConnectUser(getActivity(), eventId);
                        break;
                }

            }
        });

        return builder.create();
    }
}

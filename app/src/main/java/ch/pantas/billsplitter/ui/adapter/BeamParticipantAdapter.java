package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class BeamParticipantAdapter extends BaseAdapter {

    @Inject
    private LayoutInflater inflater;
    @Inject
    private UserService userService;

    private List<ParticipantDto> participants = new LinkedList<ParticipantDto>();

    private User selected = null;

    private boolean isDisabled = false;

    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }

    /**
     * @param user
     * @return TRUE if user was selected, FALSE otherwise
     */
    public boolean select(User user) {
        checkNotNull(user);
        if (user.equals(selected)) {
            selected = null;
            return false;
        }

        for (ParticipantDto participant : participants) {
            if (participant.getUser().equals(user)) {
                selected = user;
                return true;
            }
        }

        return false;
    }

    /**
     * @param name
     * @return TRUE if a user was selected, FALSE otherwise
     */
    public boolean selectParticipantByName(String name) {
        for (ParticipantDto participant : participants) {
            User user = participant.getUser();
            if (user.getName().equals(name)) {
                selected = user;
                return true;
            }
        }

        return false;
    }

    public User getSelected() {
        if (isDisabled) return null;
        return selected;
    }

    public void disable() {
        isDisabled = true;
    }

    public void enable() {
        isDisabled = false;
    }

    @Override
    public int getCount() {
        return participants.size();
    }

    @Override
    public Object getItem(int i) {
        return participants.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.beam_participant_item, null);
        }

        ParticipantDto participantDto = participants.get(i);
        User user = participantDto.getUser();

        View userView = view.findViewById(R.id.user_item);

        UserItemMode mode = user.equals(selected) && !isDisabled ? SELECTED : UNSELECTED;
        UserItemFormatter.setupUserItem(userView, user, mode);

        TextView text = (TextView) view.findViewById(R.id.beam_participant_item_text);
        text.setText(user.getName());

        return view;
    }
}

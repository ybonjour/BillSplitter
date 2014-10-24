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
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;

public class BeamParticipantAdapter extends BaseAdapter {

    @Inject
    private LayoutInflater inflater;

    private List<User> participants = new LinkedList<User>();

    private User selected = null;

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void selectParticipantByName(String name) {
        for (User participant : participants) {
            if (participant.getName().equals(name)) {
                selected = participant;
                return;
            }
        }
    }

    public void selectFirst() {
        if (participants.isEmpty()) return;

        selected = participants.get(0);
    }

    public User getSelected(){
        return selected;
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

        User user = participants.get(i);
        View userView = view.findViewById(R.id.user_item);

        UserItemMode mode = user.equals(selected) ? SELECTED : UNSELECTED;
        UserItemFormatter.setupUserItem(userView, user, mode);

        TextView text = (TextView) view.findViewById(R.id.beam_participant_item_text);
        text.setText(user.getName());

        return view;
    }
}

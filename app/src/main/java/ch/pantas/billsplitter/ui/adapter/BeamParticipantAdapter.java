package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;

public class BeamParticipantAdapter extends BaseAdapter {

    @Inject
    private LayoutInflater inflater;

    private List<ParticipantDto> participants = new LinkedList<ParticipantDto>();

    private User selected = null;

    public void setParticipants(List<ParticipantDto> participants) {
        this.participants.clear();
        for(ParticipantDto participantDto : participants){
            if(!participantDto.confirmed){
                this.participants.add(participantDto);
            }
        }
    }

    public void selectParticipantByName(String name) {
        for (ParticipantDto participant : participants) {
            User user = participant.user;
            if (user.getName().equals(name)) {
                selected = user;
                return;
            }
        }
    }

    public void selectFirst() {
        if (participants.isEmpty()) return;

        selected = participants.get(0).user;
    }

    public ParticipantDto getSelected(){
        for(ParticipantDto participantDto : participants){
            if(participantDto.user.equals(selected)) {
                return participantDto;
            }
        }

        return null;
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

        ParticipantDto particpantDto = participants.get(i);
        User user = particpantDto.user;

        View userView = view.findViewById(R.id.user_item);

        UserItemMode mode = user.equals(selected) ? SELECTED : UNSELECTED;
        UserItemFormatter.setupUserItem(userView, user, mode);

        TextView text = (TextView) view.findViewById(R.id.beam_participant_item_text);
        text.setText(user.getName());

        return view;
    }
}

package ch.pantas.billsplitter.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import roboguice.fragment.RoboFragment;

public class BaseEventDetailsFragment extends RoboFragment {

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private EventStore eventStore;

    protected Event getCurrentEvent(){
        String eventId = sharedPreferenceService.getActiveEventId();
        return eventStore.getById(eventId);
    }
}

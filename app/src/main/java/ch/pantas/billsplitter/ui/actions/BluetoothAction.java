package ch.pantas.billsplitter.ui.actions;

import android.content.Intent;

import ch.pantas.billsplitter.ui.BluetoothDeviceSelector;
import ch.pantas.billsplitter.ui.EventDetails;

public class BluetoothAction implements EventDetailsAction {
    @Override
    public boolean execute(EventDetails activity) {
        activity.startActivity(new Intent(activity, BluetoothDeviceSelector.class));
        return true;
    }
}

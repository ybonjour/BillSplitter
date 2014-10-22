package ch.pantas.billsplitter.ui.actions;

import android.app.Activity;

public interface Action<A extends Activity> {

    boolean execute(A activity);
}

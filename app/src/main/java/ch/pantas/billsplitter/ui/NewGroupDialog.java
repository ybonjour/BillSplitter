package ch.pantas.billsplitter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.splitty.R;
import roboguice.fragment.RoboDialogFragment;

public class NewGroupDialog extends RoboDialogFragment {
    @Inject
    private ActivityStarter activityStarter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        CharSequence[] items = new CharSequence[]{
                getActivity().getString(R.string.create_event),
                getActivity().getString(R.string.join_event)
        };

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedIdx) {
                switch (selectedIdx) {
                    case 0:
                        dismiss();
                        activityStarter.startAddEvent(getActivity());
                        break;
                    case 1:
                        dismiss();
                        activityStarter.startJoinEvent(getActivity());

                }

            }
        });

        return builder.create();
    }
}

package ch.pantas.billsplitter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.splitty.R;

public class NewGroupDialog extends Dialog {
    public final static void show(Context context, ActivityStarter activityStarter) {
        new NewGroupDialog(context, activityStarter).show();
    }

    protected NewGroupDialog(final Context context, final ActivityStarter activityStarter) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_group_dialog);

        Button addGroup = (Button) findViewById(R.id.action_create_event);
        addGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activityStarter.startAddEvent(context);
            }
        });
        Button joinGroup = (Button) findViewById(R.id.action_join_event);
        joinGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activityStarter.startJoinEvent(context);
            }
        });
    }
}

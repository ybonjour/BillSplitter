package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import javax.inject.Inject;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class Login extends RoboActivity {

    @InjectView(R.id.user_name)
    private EditText nameField;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private ActivityStarter activityStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(sharedPreferenceService.getUserName() != null){
            activityStarter.startEventList(this);
            finish();
            return;
        }

        setContentView(R.layout.login);
        setTitle(R.string.set_user_name);
    }

    public void onSave(View view){
        String userName = nameField.getText().toString();
        if(userName == null || userName.isEmpty()) {
            nameField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        sharedPreferenceService.storeUserName(userName);

        activityStarter.startEventList(this);
        finish();
    }
}

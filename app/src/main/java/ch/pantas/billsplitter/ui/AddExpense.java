package ch.pantas.billsplitter.ui;

import android.os.Bundle;

import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;

public class AddExpense extends RoboActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
    }
}

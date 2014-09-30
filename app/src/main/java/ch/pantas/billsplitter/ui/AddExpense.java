package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;

public class AddExpense extends RoboActivity {

    @InjectView(R.id.expense_description)
    private EditText descriptionField;

    @InjectView(R.id.expense_amount)
    private EditText amountField;

    @InjectView(R.id.expense_payer)
    private EditText payerField;

    @InjectView(R.id.expense_payer_me)
    private CheckBox payerMeCheckbox;

    @Inject
    private EventStore eventStore;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        event = eventStore.getById(eventId);
        setTitle(event.getName());
    }

    public void onPayerMeClicked(View v) {
        if (payerMeCheckbox.isChecked()){
            payerField.setText(R.string.me);
            payerField.setEnabled(false);
        } else {
            payerField.setText("");
            payerField.setEnabled(true);
        }
    }

    public void onSave(View v) {
        User payer = persistPayer();

        String description = descriptionField.getText().toString();
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().toString());
        }
        catch(NumberFormatException e) {
            amountField.setBackgroundColor(getResources().getColor(R.color.error_color));
            amount = 0.0;
        }
        if (payer != null && amount > 0.0) {
            Expense expense = new Expense(event.getId(), payer.getId(), description, amount);
            expenseStore.persist(expense);
            finish();
        }
    }

    private User persistPayer(){
        String payerName = payerField.getText().toString();
        User payer;
        if (payerName.isEmpty()) {
            payerField.setBackgroundColor(getResources().getColor(R.color.error_color));
            payer = null;
        }
        else {
            payer = userStore.getUserWithName(payerName);
            if (payer == null) {
                payer = new User(payerName);
            }
            userStore.persist(payer);
        }
        return payer;
    }
}

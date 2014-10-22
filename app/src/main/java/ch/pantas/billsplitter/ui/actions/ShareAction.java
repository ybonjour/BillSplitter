package ch.pantas.billsplitter.ui.actions;

import android.content.Intent;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.EventDetails;
import ch.pantas.splitty.R;

public class ShareAction implements EventDetailsAction {

    @Inject
    private DebtCalculator debtCalculator;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Override
    public boolean execute(EventDetails activity) {

        Event event = activity.getEvent();
        if(event != null){
            String shareTemplate = activity.getString(R.string.share_template);
            Intent shareIntent = getShareIntent(event, shareTemplate);
            activity.startActivity(shareIntent);
        }
        return true;
    }

    private Intent getShareIntent(Event event, String shareTemplate) {
        List<Debt> debts = debtCalculator.calculateDebts(event);
        String text = createText(debts, shareTemplate);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        return intent;
    }

    private String createText(List<Debt> debts, String shareTemplate) {
        StringBuilder debtsText = new StringBuilder();
        boolean first = true;
        for (Debt debt : debts) {
            if (!first) {
                debtsText.append("\n\r");
            }
            debtsText.append(debt.toString());

            first = false;
        }
        String username = sharedPreferenceService.getUserName();
        return String.format(shareTemplate, debtsText.toString(), username);
    }
}

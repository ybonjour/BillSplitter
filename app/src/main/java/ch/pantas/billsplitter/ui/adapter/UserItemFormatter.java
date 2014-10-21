package ch.pantas.billsplitter.ui.adapter;

import android.view.View;
import android.widget.TextView;

import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static java.lang.String.valueOf;

public class UserItemFormatter {

    public static void setupUserItem(View view, User user, int backgroundDrawable){
        String userName = user.getName();
        TextView nameView = (TextView) view.findViewById(R.id.user_item_name);
        nameView.setText(userName);

        View pictogramView = view.findViewById(R.id.user_item_pictogram);
        pictogramView.setBackground(view.getResources().getDrawable(backgroundDrawable));

        String pictogram = "";
        if(userName != null && !userName.isEmpty()) {
            pictogram = valueOf(userName.toUpperCase().charAt(0));
        }

        TextView pictogramTextView = (TextView) view.findViewById(R.id.user_item_pictogram_text);
        pictogramTextView.setTextSize(22);
        pictogramTextView.setText(pictogram);
    }

}

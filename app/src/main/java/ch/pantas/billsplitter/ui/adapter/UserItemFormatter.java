package ch.pantas.billsplitter.ui.adapter;

import android.view.View;
import android.widget.TextView;

import ch.pantas.billsplitter.model.User;
import ch.pantas.splitty.R;

import static java.lang.String.valueOf;

public class UserItemFormatter {

    public enum UserItemMode {
        NORMAL(R.drawable.background_user_item, 1f),
        SELECTED(R.drawable.background_user_item_selected, 1f),
        UNSELECTED(R.drawable.background_user_item_unselected, 0.3f);

        private final int pictogramBackground;
        private final float alpha;

        UserItemMode(int pictogramBackground, float alpha) {
            this.pictogramBackground = pictogramBackground;
            this.alpha = alpha;
        }

        public int getPictogramBackground() {
            return pictogramBackground;
        }

        public float getAlpha() {
            return alpha;
        }
    }

    public static void setupUserItem(View view, User user, UserItemMode mode) {
        String userName = user.getName();
        TextView nameView = (TextView) view.findViewById(R.id.user_item_name);
        nameView.setText(userName);

        View pictogramView = view.findViewById(R.id.user_item_pictogram);
        pictogramView.setBackground(view.getResources().getDrawable(mode.getPictogramBackground()));

        String pictogram = "";
        if (userName != null && !userName.isEmpty()) {
            pictogram = valueOf(userName.toUpperCase().charAt(0));
        }

        TextView pictogramTextView = (TextView) view.findViewById(R.id.user_item_pictogram_text);
        pictogramTextView.setTextSize(22);
        pictogramTextView.setText(pictogram);

        View container = view.findViewById(R.id.user_item_container);
        container.setAlpha(mode.getAlpha());
    }

}

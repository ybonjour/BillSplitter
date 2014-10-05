package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static java.lang.String.valueOf;

public class UserAdapter extends BaseAdapter{
    @Inject
    private LayoutInflater layoutInflater;

    private List<User> users = new LinkedList<User>();

    private Integer resBackgroundDrawable;

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setResBackgroundDrawable(int resColor){
        this.resBackgroundDrawable = resColor;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = layoutInflater.inflate(R.layout.user_item, null);
        }

        String userName = users.get(i).getName();
        TextView nameView = (TextView) view.findViewById(R.id.user_item_name);
        nameView.setText(userName);

        View pictogramView = view.findViewById(R.id.user_item_pictogram);
        if(resBackgroundDrawable != null) {
            pictogramView.setBackground(view.getResources().getDrawable(resBackgroundDrawable));
        }

        String pictogram = "";
        if(userName != null && !userName.isEmpty()) {
            pictogram = valueOf(userName.toUpperCase().charAt(0));
        }

        TextView pictogramTextView = (TextView) view.findViewById(R.id.user_item_pictogram_text);
        pictogramTextView.setTextSize(22);
        pictogramTextView.setText(pictogram);

        return view;
    }
}

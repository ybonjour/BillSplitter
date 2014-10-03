package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

public class UserAdapter extends BaseAdapter {
    @Inject
    private LayoutInflater layoutInflater;

    private List<User> users;

    public UserAdapter init(List<User> users){
        this.users = users;
        return this;
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

        if (view == null) {
            view = layoutInflater.inflate(R.layout.user_item, null);
        }

        TextView userName = (TextView) view.findViewById(R.id.user_name);

        userName.setText(users.get(i).getName());

        return view;
    }
}

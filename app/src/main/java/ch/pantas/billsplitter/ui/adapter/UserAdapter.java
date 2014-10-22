package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.User;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.setupUserItem;

public class UserAdapter extends BaseAdapter {
    @Inject
    private LayoutInflater layoutInflater;

    private List<User> users = new LinkedList<User>();

    private UserItemMode userItemMode;

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setUserItemMode(UserItemMode userItemMode) {
        this.userItemMode = userItemMode;
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

        setupUserItem(view, users.get(i), userItemMode);

        return view;
    }
}

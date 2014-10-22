package ch.pantas.billsplitter.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.String.valueOf;

public class PayerAdapter extends BaseAdapter{

    @Inject
    private LayoutInflater layoutInflater;

    private List<User> users = new LinkedList<User>();
    private User selectedUser;

    public void setUsers(List<User> users) {
        checkNotNull(users);

        this.users = users;
    }

    public void select(User user) {
        checkNotNull(user);
        selectedUser = user;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public List<User> filterOutSelectedUser(List<User> users) {
        List<User> result = new LinkedList<User>();

        if(selectedUser == null) return result;

        for(User user : users) {
            if(!isSelected(user)){
                result.add(user);
            }
        }

        return result;
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

    private boolean isSelected(User user) {
        checkNotNull(user);
        return user.equals(selectedUser);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = layoutInflater.inflate(R.layout.user_item, null);
        }

        User user = users.get(i);

        UserItemMode mode = isSelected(user) ? SELECTED : UNSELECTED;
        UserItemFormatter.setupUserItem(view, user, mode);

        return view;
    }
}

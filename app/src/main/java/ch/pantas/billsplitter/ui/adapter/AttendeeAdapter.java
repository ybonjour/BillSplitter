package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.inject.Inject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.setupUserItem;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class AttendeeAdapter extends BaseAdapter {
    @Inject
    private LayoutInflater layoutInflater;

    private List<User> users = new LinkedList<User>();

    private Set<User> selectedUsers = new HashSet<User>();


    public void setUsers(List<User> users) {
        checkNotNull(users);

        this.users = users;

        removeInvalidSelectedUsers();
    }

    public void selectAll() {
        for (User user : users) {
            select(user);
        }
    }

    public void select(User user) {
        checkNotNull(user);

        selectedUsers.add(user);
    }

    public void deselect(User user) {
        checkNotNull(user);

        selectedUsers.remove(user);
    }

    public void toggle(User user) {
        if (isSelected(user)) {
            deselect(user);
        } else {
            select(user);
        }
    }

    public Set<User> getSelectedUsers() {
        return selectedUsers;
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

        User user = users.get(i);
        UserItemMode mode = isSelected(user) ? SELECTED : UNSELECTED;
        setupUserItem(view, user, mode);

        return view;
    }

    private void removeInvalidSelectedUsers() {
        List<User> invalidSelectedUsers = getInvalidSelectedUsers();
        for (User user : invalidSelectedUsers) {
            selectedUsers.remove(user);
        }
    }

    private List<User> getInvalidSelectedUsers() {
        List<User> invalidUsers = new LinkedList<User>();

        for (User user : selectedUsers) {
            if (!isValidForAdapter(user)) {
                invalidUsers.add(user);
            }
        }

        return invalidUsers;
    }

    private boolean isValidForAdapter(User user) {
        checkNotNull(user);

        return users.contains(user);
    }

    private boolean isSelected(User user) {
        checkNotNull(user);

        return selectedUsers.contains(user);
    }
}

package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.String.valueOf;

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

        TextView nameView = (TextView) view.findViewById(R.id.user_item_name);
        nameView.setText(user.getName());

        int backgroundDrawable = isSelected(user)
                ? R.drawable.background_user_item_selected
                : R.drawable.background_user_item;

        View pictogramView = view.findViewById(R.id.user_item_pictogram);
        pictogramView.setBackground(view.getResources().getDrawable(backgroundDrawable));

        String pictogram = "";
        if (user.getName() != null && !user.getName().isEmpty()) {
            pictogram = valueOf(user.getName().toUpperCase().charAt(0));
        }

        TextView pictogramTextView = (TextView) view.findViewById(R.id.user_item_pictogram_text);
        pictogramTextView.setTextSize(22);
        pictogramTextView.setText(pictogram);

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

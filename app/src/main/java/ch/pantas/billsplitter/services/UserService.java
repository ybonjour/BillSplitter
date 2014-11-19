package ch.pantas.billsplitter.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class UserService {

    @Inject
    private UserStore userStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    public User getMe(){
        String userId = sharedPreferenceService.getUserId();
        if(userId == null) return null;
        return userStore.getById(userId);
    }

    public void changeMyUsername(String userName) {
        checkNotNull(userName);
        checkArgument(!userName.isEmpty());

        User me = getMe();
        checkNotNull(me);
        me.setName(userName);
        userStore.persist(me);
    }

    public String findBestFreeNameForUser(User user) {
        checkNotNull(user);

        String currentName = user.getName();
        int i = 1;
        while (true) {
            User existingUser = userStore.getUserWithName(currentName);
            if (existingUser == null || existingUser.equals(user)) return currentName;

            currentName = user.getName() + " " + i;

            i += 1;
        }
    }
}

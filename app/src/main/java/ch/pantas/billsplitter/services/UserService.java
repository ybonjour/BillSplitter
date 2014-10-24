package ch.pantas.billsplitter.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.User;

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

    public void changeUsername(String userName) {
        String userId = sharedPreferenceService.getUserId();
        User user;
        if(userId == null) user = new User(userName);
        else {
            user = userStore.getById(userId);
            user.setName(userName);
        }
        userStore.persist(user);
    }
}

package sensecloud.auth2;

import lombok.extern.slf4j.Slf4j;
import sensecloud.auth2.model.UserInfo;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserContext {

    private ConcurrentHashMap<String, UserInfo> ctx = new ConcurrentHashMap<String, UserInfo> ();

    private ThreadLocal<UserInfo> currentUser = new ThreadLocal<>();

    UserContext() {}

    public UserInfo lookup (String domain, String username) {
        String key = this.getKey(domain, username);
        UserInfo user = null;
        if (ctx.containsKey(key)) {
            user = ctx.get(key);
        }
        return user;
    }

    public void login (String domain, String username, UserInfo user) {
        log.debug("Login user: {}", user.toString());
        user.setUsername(username);
        user.setDomain(domain);
        String key = this.getKey(domain, username);
        this.ctx.put(key, user);
        this.currentUser.set(user);
    }

    public void logout (String domain, String username) {
        String key = this.getKey(domain, username);
        this.ctx.remove(key);
        log.debug("Logout user: domain={}, username={}", domain, username);
        UserInfo user = this.currentUser.get();
        if(user != null && user.getUsername().equals("username")) {

        }
    }

    private String getKey(String domain, String username) {
        String key = domain + ":::" + username;
        return key;
    }

    public UserInfo getCurrentUser() {
        return this.currentUser.get();
    }

}

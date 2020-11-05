package sensecloud.auth2;

import lombok.extern.slf4j.Slf4j;
import sensecloud.auth2.model.User;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserContext {

    private ConcurrentHashMap<String, User> ctx = new ConcurrentHashMap<String, User> ();

    UserContext() {}

    public User lookup (String domain, String username) {
        String key = this.getKey(domain, username);
        User user = null;
        if (ctx.containsKey(key)) {
            user = ctx.get(key);
        }
        return user;
    }

    public void login (String domain, String username, User user) {
        log.debug("Login user: {}", user.toString());
        String key = this.getKey(domain, username);
        this.ctx.put(key, user);
    }

    public void logout(String domain, String username) {
        String key = this.getKey(domain, username);
        this.ctx.remove(key);
        log.debug("Logout user: domain={}, username={}", domain, username);
    }

    private String getKey(String domain, String username) {
        String key = domain + ":::" + username;
        return key;
    }

}

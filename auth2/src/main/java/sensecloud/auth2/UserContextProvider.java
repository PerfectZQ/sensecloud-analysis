package sensecloud.auth2;

public class UserContextProvider {

    private static UserContext instance = new UserContext();

    private UserContextProvider() {}

    public static UserContext getContext() {
        return instance;
    }

}

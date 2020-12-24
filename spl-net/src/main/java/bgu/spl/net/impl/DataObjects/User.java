package bgu.spl.net.impl.DataObjects;

import java.util.LinkedList;

public abstract class User {

    private String userName;
    private String password;
    private boolean isLogIn;

    public User(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
        this.isLogIn = false;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogIn() {
        return isLogIn;
    }

    public void setLogIn(boolean logIn) {
        isLogIn = logIn;
    }
}

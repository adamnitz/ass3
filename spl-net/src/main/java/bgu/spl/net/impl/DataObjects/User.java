package bgu.spl.net.impl.DataObjects;

import java.util.LinkedList;

public abstract class User {

    private String userName;
    private String password;

    public User(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}

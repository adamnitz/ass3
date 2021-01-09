package bgu.spl.net.impl.Message;

import java.util.LinkedList;

public class AdminReg extends Message {

    private String userName;
    private String password;

    public AdminReg(int opCode,String userName,String password) {
        super(opCode);
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

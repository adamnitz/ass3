package bgu.spl.net.impl.Message;

public class LogIn extends Message {
    private String userName;
    private String password;

    public LogIn(int opCode,String userName,String password) {
        super(opCode);
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

package bgu.spl.net.impl.Message;

public class StudentStat extends Message {
    private String userName;

    public StudentStat(int opCode,String userName) {
        super(opCode);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
package bgu.spl.net.impl.Message;

public abstract class Message {

    private int opCode;

    public Message(int opCode){
        this.opCode = opCode;
    }

    public int getOpCode() {
        return opCode;
    }
}

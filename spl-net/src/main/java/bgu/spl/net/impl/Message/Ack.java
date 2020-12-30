package bgu.spl.net.impl.Message;

import java.util.LinkedList;

public class Ack extends Message {

    String data;
    LinkedList <Integer> myCourses;
    public Ack(int opCode) {
        super(opCode);
        String data = null;
        LinkedList <Integer> myCourses = null;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMyCourses(LinkedList<Integer> myCourses) {
        this.myCourses = myCourses;
    }

    public String getData() {
        return data;
    }

    public LinkedList<Integer> getMyCourses() {
        return myCourses;
    }
}

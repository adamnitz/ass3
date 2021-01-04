package bgu.spl.net.impl.Message;

public class UnRegister extends Message {

    int courseNum;

    public UnRegister(int opCode, int courseNum){
        super(opCode);
        this.courseNum = courseNum;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }
}
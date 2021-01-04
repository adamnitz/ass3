package bgu.spl.net.impl.Message;

public class CourseStat extends Message {

    int courseNum;

    public CourseStat(int opCode, int courseNum){
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


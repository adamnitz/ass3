package bgu.spl.net.impl.Message;

import bgu.spl.net.impl.DataObjects.Course;

import java.util.LinkedList;

public class KdamCheck extends Message {

    int courseNum;
    LinkedList<Course> kdamcourse;

    public KdamCheck(int opCode, int courseNum,LinkedList<Course> kdam){
        super(opCode);
        this.courseNum = courseNum;
        kdamcourse=kdam;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }
}

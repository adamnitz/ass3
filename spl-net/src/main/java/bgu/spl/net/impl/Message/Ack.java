package bgu.spl.net.impl.Message;

import bgu.spl.net.impl.DataObjects.Course;

import java.util.LinkedList;

public class Ack extends Message {

    String data;
    LinkedList <Integer> myCourses;
    LinkedList<Course> kdamCourses;

    public Ack(int opCode) {
        super(opCode);
        this.data = null;
        this.myCourses = new LinkedList<Integer>();
        this.kdamCourses = new LinkedList<Course>();
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

    public String getMyCourses() {
        return myCourses.toString();
    }

    public void setKdamCourses(LinkedList<Course> kdamCourses) {
        this.kdamCourses = kdamCourses;
    }

    public String getKdamCourses() {
        String str ="[";
        for(int i=0; i<kdamCourses.size(); i++){
            str = str+kdamCourses.get(i).getCourseNum()+",";
        }

        if(str.length()>1)
          str = str.substring(0,str.length()-1) + "]";
        else
            str = "]";

        return str;
    }
}

package bgu.spl.net.impl.Message;

import bgu.spl.net.impl.DataObjects.Course;
import bgu.spl.net.impl.Database;

import java.util.LinkedList;

public class Ack extends Message {

    String data;
    LinkedList <Course> myCourses;
    LinkedList<Course> kdamCourses;
    private Database myData = Database.getInstance();


    public Ack(int opCode) {
        super(opCode);
        this.data = null;
        this.myCourses = new LinkedList<Course>();
        this.kdamCourses = new LinkedList<Course>();
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMyCourses(LinkedList<Course> myCourses) {
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

    public String getStrMyCourses(){

        String str ="[";
        for(int i=0; i<myCourses.size(); i++){
            str = str+myCourses.get(i).getCourseNum()+",";
        }

        if(str.length()>1)
            str = str.substring(0,str.length()-1) + "]";
        else
            str = str + "]";

        return str;

    }

    public String getKdamCourses() {
        String str ="[";
        for(int i=0; i<kdamCourses.size(); i++){
            str = str+kdamCourses.get(i).getCourseNum()+",";
        }

        if(str.length()>1)
          str = str.substring(0,str.length()-1) + "]";
        else
            str =str + "]";

        return str;
    }
}

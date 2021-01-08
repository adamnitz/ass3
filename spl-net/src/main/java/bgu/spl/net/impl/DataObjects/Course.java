package bgu.spl.net.impl.DataObjects;

import bgu.spl.net.impl.Message.Ack;
import bgu.spl.net.impl.Message.Error;
import bgu.spl.net.impl.Message.Message;

import java.util.Comparator;
import java.util.LinkedList;

public class Course {
    private int courseNum;
    private String courseName;
    private LinkedList<Course> kdamCourseList;
    private int numOfMaxStudent;
    private int numOfRegisteredStudent;
    private LinkedList<String> registeredStudent;
    private int index;



    public Course(int courseNum, String courseName, LinkedList<Course> kdamCourseList, int numOfMaxStudent, int index){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.kdamCourseList = kdamCourseList;
        this.numOfMaxStudent = numOfMaxStudent;
        this.numOfRegisteredStudent = 0;
        this.registeredStudent = new LinkedList<String>();
        this.index=index;
    }
    public void setKdamCourseList(LinkedList<Course> kdamCourseList) {
        this.kdamCourseList = kdamCourseList;
    }

    public int getIndex() {
        return index;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public LinkedList<Course> getKdamCourseList() {
        return kdamCourseList;
    }

    public int getNumOfMaxStudent() {
        return numOfMaxStudent;
    }

    public int getNumOfRegisteredStudent() {
        return numOfRegisteredStudent;
    }

    public LinkedList<String> getRegisteredStudent() {
        return registeredStudent;
    }

    public String getStringRegisteredStudent() {
        String str ="[";
        for(int i=0; i<registeredStudent.size(); i++){
            str = str+registeredStudent.get(i)+",";
        }

        if(str.length()>1)
            str = str.substring(0,str.length()-1) + "]";
        else
            str = "]";

        return str;
    }

    public Message addNumOfRegisteredStudent(int opCode){
        if(numOfRegisteredStudent < numOfMaxStudent){//check if we need to add =
            numOfRegisteredStudent++;
        }
        else{
            return new Error(opCode);
        }
        return new Ack(opCode);
    }

    public Message removeNumOfRegisteredStudent(int opCode){
        if(numOfRegisteredStudent>0){
            numOfRegisteredStudent--;
        }

        else{
            return new Error(opCode);
        }
        return new Ack(opCode);
    }

    public void addStudentToCourse(String name){
        this.registeredStudent.add(name);
        registeredStudent.sort(Comparator.naturalOrder());
    }


}


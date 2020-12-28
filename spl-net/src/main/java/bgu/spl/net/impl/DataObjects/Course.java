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
    LinkedList<String> registeredStudent;

    public Course(int courseNum,String courseName,LinkedList<Course> kdamCourseList, int numOfMaxStudent){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.kdamCourseList = kdamCourseList;
        this.numOfMaxStudent = numOfMaxStudent;
        this.numOfRegisteredStudent = 0;
        registeredStudent = new LinkedList<String>();
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


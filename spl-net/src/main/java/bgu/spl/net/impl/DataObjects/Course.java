package bgu.spl.net.impl.DataObjects;

import java.util.LinkedList;

public class Course {
    private int courseNum;
    private String courseName;
    private LinkedList<Course> kdamCourseList;
    private int numOfMaxStudent;
    private int numOfRegisteredStudent;

    public Course(int courseNum,String courseName,LinkedList<Course> kdamCourseList, int numOfMaxStudent){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.kdamCourseList = kdamCourseList;
        this.numOfMaxStudent = numOfMaxStudent;
        this.numOfRegisteredStudent = 0;
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

    public void addNumOfRegisteredStudent(){
        if(numOfRegisteredStudent < numOfMaxStudent){
            numOfRegisteredStudent++;
        }
        else{
            //TODO:SEND ERROR MESSAGE
        }
    }
    public void removeNumOfRegisteredStudent(){
        if(numOfRegisteredStudent>0){
            numOfRegisteredStudent--;
        }

        else{
            //TODO:SEND ERROR MESSAGE
        }

    }
}


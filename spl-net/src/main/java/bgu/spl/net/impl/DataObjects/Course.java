package bgu.spl.net.impl.DataObjects;

import java.util.LinkedList;

public class Course {
    private int courseNum;
    private String courseName;
    private LinkedList<Course> kdamCourseList;
    private int numOfMaxStudent;

    public Course(int courseNum,String courseName,LinkedList<Course> kdamCourseList, int numOfMaxStudent){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.kdamCourseList = kdamCourseList;
        this.numOfMaxStudent = numOfMaxStudent;
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
}


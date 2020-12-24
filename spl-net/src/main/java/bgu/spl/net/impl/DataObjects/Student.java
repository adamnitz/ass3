package bgu.spl.net.impl.DataObjects;

import java.util.LinkedList;

public class Student extends User{

    private LinkedList<Course> coursesList;

    public Student(String userName, String password) {
        super(userName, password);
    }

    public LinkedList<Course> getCoursesList() {
        return coursesList;
    }

    public void addCourse(Course course) {
        this.coursesList.add(course);
        //TODO: need to sort the added course bu the courses file
    }

    public void removeCourse(Course course){
        this.coursesList.remove(course);
        //TODO:CHECK IF WORKS
    }
}

package bgu.spl.net.impl.DataObjects;

import java.util.LinkedList;

public class User {

    private String userName;
    private String password;
    private LinkedList<Course> coursesList;

    public User(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
        coursesList = new LinkedList<Course>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
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

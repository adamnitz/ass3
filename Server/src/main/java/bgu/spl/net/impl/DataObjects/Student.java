package bgu.spl.net.impl.DataObjects;

import bgu.spl.net.impl.Database;

import java.util.LinkedList;

public class Student extends User{


    private LinkedList<Course> coursesList;
    private Database myData = Database.getInstance();


    public Student(String userName, String password) {
        super(userName, password);
        coursesList = new LinkedList<Course>();
    }

    public LinkedList<Course> getCoursesList() {
        return coursesList;
    }

    public String getStringCoursesList() {
        coursesList = myData.sortByCoursList(coursesList);

        String str ="[";

        for(int i=0; i<coursesList.size(); i++){
            str = str+coursesList.get(i).getCourseNum()+",";
        }

        if(str.length()>1)
            str = str.substring(0,str.length()-1) + "]";
        else
            str = str + "]";

        return str;
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

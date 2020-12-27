package bgu.spl.net;


import bgu.spl.net.impl.DataObjects.Admin;
import bgu.spl.net.impl.DataObjects.Course;
import bgu.spl.net.impl.DataObjects.Student;
import bgu.spl.net.impl.DataObjects.User;

import javax.xml.crypto.Data;
import java.io.*;
import java.nio.Buffer;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Stream;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private final Vector<Course> allCourses;
    private final Vector<User> allUsers;

    private static class DataHolder {
        private static final Database instance = new Database();
    }

    //to prevent user from creating new Database
    private Database() {
        allCourses = new Vector<>();
        allUsers = new Vector<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DataHolder.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        // TODO: implement

        FileReader file = null;//TODO:in main ill give to the initialize the courses.txt
        try {
            file = new FileReader(coursesFilePath);
        } catch (FileNotFoundException e) {
            System.out.print("The File not found");
        }

        BufferedReader in = new BufferedReader(file);
        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            return false;
        }

        while (line != null) {
            String[] lineArr = line.split("|");
            int courseNum = Integer.parseInt(lineArr[0]);
            String courseName = lineArr[1];
            LinkedList<Course> kdamCourseList = new LinkedList<Course>();
            String[] kdamCourseArr = lineArr[3].split(",");
            for (int i = 0; i < kdamCourseArr.length; i++) {
                int kdamCourseNum = Integer.parseInt(kdamCourseArr[i]);
                Course course = allCourses.get(kdamCourseNum);
                kdamCourseList.add(course);
                //TODO:check if it's possibbole that there is course that wasnt registered yet
            }
            int numOfMaxStudents = Integer.parseInt(lineArr[3]);
            Course course = new Course(courseNum, courseName, kdamCourseList, numOfMaxStudents);
            allCourses.add(course);
            try {
                line = in.readLine();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public void adminReg(String userName, String password) {
        boolean isExist = false;
        //TODO:think if it's work because of the instanceOf
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
            if (allUsers.get(i) instanceof Admin) {
                if (allUsers.get(i).getUserName().equals(userName))
                    isExist = true;
            }
        }
        if (isExist) {
            //TODO:SEND ERROR MESSAGE
        } else {
            Student admin = new Student(userName, password);
            allUsers.add(admin);
        }
    }

    public void studentReg(String userName, String password) {
        boolean isExist = false;
        //TODO:think if it's work because of the instanceOf
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
            if (allUsers.get(i) instanceof Student) {
                if (allUsers.get(i).getUserName().equals(userName))
                    isExist = true;
            }
        }
        if (isExist) {
            //TODO:SEND ERROR MESSAGE
        } else {
            Student admin = new Student(userName, password);
            allUsers.add(admin);
        }
    }


    //todo:i think we should do it again .said nitzan.
    public void logIn(String userName, String password) {
        boolean isExist = false;
        //TODO:think if it's work because of the instanceOf
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
            User user = allUsers.get(i);
            if (user.getUserName().equals(userName)) {
                isExist = true;
                if (user.isLogIn()) {
                    //TODO:SEND ERROR MESSAGE
                }
                if (!user.getPassword().equals(password)) {
                    //TODO:SEND ERROR MESSAGE
                }

            }
        }
        if (!isExist) {
            //TODO:SEND ERROR MESSAGE
        }
    }

    public void logOut(String userName) {
        for (int i = 0; i < allUsers.size(); i++) { //why we put here (!notthepass)
            User user = allUsers.get(i);
            if (user.getUserName().equals(userName)) {
                if (user.isLogIn()) {
                    //TODO:SEND ERROR MESSAGE
                } else {
                    user.setLogIn(false);
                }
            }
        }
    }

    public void courseReg(String userName, int courseNum) {

        boolean courseIsExist = false;
        Course course = null;
        Student student = null;
        for (int i = 0; i < allCourses.size() && !courseIsExist; i++) {
            course = allCourses.get(i);
            if (course.equals(courseNum)) {
                courseIsExist = true;
                if (course.getNumOfRegisteredStudent() >= course.getNumOfMaxStudent()) {
                    //TODO:SEND ERROR MESSAGE
                }
            }

        }

        if (!courseIsExist) {
            //TODO:SEND ERROR MESSAGE
        }


        for (int i = 0; i < allUsers.size(); i++) { //why we put here (!notthepass)
            if (allUsers.get(i).getUserName().equals(userName)) {
                if (allUsers.get(i) instanceof Admin) {
                    //TODO:SEND ERROR MESSAGE
                }
                if(allUsers.get(i) instanceof Student){
                    student = (Student) allUsers.get(i);
                }
                if (!allUsers.get(i).isLogIn()) {
                    //TODO:SEND ERROR MESSAGE
                }
            }
        }

      kdamCheck(student,course);


        student.addCourse(course);
        course.addNumOfRegisteredStudent();
        //TODO::SEND ACK MESSAGE

    }

    public void kdamCheck(Student student, Course course)
    {
        LinkedList<Course> courseList = student.getCoursesList();
        LinkedList<Course> kdamCourseList = course.getKdamCourseList();
        boolean found = false;
        boolean allKdamDone=true;
        int i=0;
        int j=0;
        while( i<courseList.size()){
            while( j<kdamCourseList.size() && !found && allKdamDone) {
                boolean isEquals=courseList.get(i).equals(kdamCourseList.get(j));
                if (isEquals) {
                    found = true;
                    j++;
                }
                if(isEquals && (i==courseList.size()-1))
                    allKdamDone=false;

                i++;
            }
            found = false;
        }
        if (!allKdamDone) {
            //TODO:SEND ERROR MESSAGE
        }
    }


}
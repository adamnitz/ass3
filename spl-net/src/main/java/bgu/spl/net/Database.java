package bgu.spl.net;


import bgu.spl.net.impl.DataObjects.Admin;
import bgu.spl.net.impl.DataObjects.Course;
import bgu.spl.net.impl.DataObjects.Student;
import bgu.spl.net.impl.DataObjects.User;
import bgu.spl.net.impl.Message.Error;
import bgu.spl.net.impl.Message.Ack;
import bgu.spl.net.impl.Message.Message;


import java.io.*;
import java.util.LinkedList;
import java.util.Vector;

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

    public Message adminReg(String userName, String password) {
        int opCode = 1;
        boolean isExist = false;
        //TODO:think if it's work because of the instanceOf
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
            if (allUsers.get(i) instanceof Admin) {
                if (allUsers.get(i).getUserName().equals(userName))
                    isExist = true;
            }
        }
        if (isExist) {
            return new Error(opCode);
        } else {
            Student admin = new Student(userName, password);
            allUsers.add(admin);
            return new Ack(opCode);
        }
    }


    public User findUser(String userName) {
        boolean isExist = false;
        User user = null;
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
            user = allUsers.get(i);
            if (user.getUserName().equals(userName))
                isExist = true;
        }
        return user;
    }


    public Student findStudent(String userName) {
        boolean isExist = false;
        Student student = null;
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
            if (allUsers.get(i) instanceof Student) {
                student = (Student) allUsers.get(i);
                if (student.getUserName().equals(userName))
                    isExist = true;
            }
        }
        return student;
    }

    public Course findCourse(int courseNum) {
        boolean courseIsExist = false;
        Course course = null;
        for (int i = 0; i < allCourses.size() && !courseIsExist; i++) {
            course = allCourses.get(i);
            if (course.equals(courseNum)) {
                courseIsExist = true;
            }
        }
        return course;
    }

    public Message studentReg(String userName, String password) {
        int opCode = 2;
        if (findStudent(userName) != null)
            return new Error(opCode);

        else {
            Student stu = new Student(userName, password);
            allUsers.add(stu);
            return new Ack(opCode);
        }
    }


    public Message logIn(String userName, String password) {
        int opCode = 3;
        User user = findUser(userName);
        if (user == null | user.isLogIn() | (!user.getPassword().equals(password)))
            return new Error(opCode);

        user.setLogIn(true);
        return new Ack(opCode);
    }


    public Message logOut(String userName) {
        int opCode = 4;
        User user = findUser(userName);
        if (user != null) {
            if (!user.isLogIn())
                return new Error(opCode);
            else {
                user.setLogIn(false);
                return new Ack(opCode);
            }
        }
        return new Error(opCode);
    }

    public Message courseReg(String userName, int courseNum) {
        int opCode = 5;
        Course course = findCourse(courseNum);
        Student student = null;

        if (course == null)
            return new Error(opCode);

        else if (course.getNumOfRegisteredStudent() >= course.getNumOfMaxStudent())
            return new Error(opCode);

        for (int i = 0; i < allUsers.size(); i++) {

            if (allUsers.get(i).getUserName().equals(userName)) {
                if (allUsers.get(i) instanceof Admin) {
                    return new Error(opCode);
                }

                student = (Student) allUsers.get(i);

                if (!allUsers.get(i).isLogIn()) {
                    return new Error(opCode);
                }
            }
        }

        if (kdamCheck(student, courseNum) instanceof Error)
            return new Error(opCode);

        student.addCourse(course);
        course.addStudentToCourse(student.getUserName());
        return course.addNumOfRegisteredStudent(opCode);

    }

    public Message kdamCheck(Student student, int courseNumber) {
        int opCode = 6;
        Course course = findCourse(courseNumber);
        LinkedList<Course> courseList = student.getCoursesList();
        LinkedList<Course> kdamCourseList = course.getKdamCourseList();
        boolean found = false;
        boolean allKdamDone = true;
        int i = 0;
        int j = 0;
        while (i < courseList.size()) {
            while (j < kdamCourseList.size() && !found && allKdamDone) {
                boolean isEquals = courseList.get(i).equals(kdamCourseList.get(j));
                if (isEquals) {
                    found = true;
                    j++;
                }
                if (isEquals && (i == courseList.size() - 1))
                    allKdamDone = false;

                i++;
            }
            found = false;
        }
        if (!allKdamDone)
            return new Error(opCode);

        return new Ack(opCode);
    }

    public String courseStat(int courseNumber) {
        Course course = findCourse(courseNumber);
        int courseNum = course.getCourseNum();
        String courseName = course.getCourseName();
        int availableSeats = course.getNumOfMaxStudent() - course.getNumOfRegisteredStudent();
        LinkedList<String> listOfStudent = course.getRegisteredStudent();

        String string = courseNum + "|" + courseName + "|" + availableSeats + "/" + course.getNumOfMaxStudent() +
                "|";
        if (listOfStudent.isEmpty()) {
            string = string + "[]";
            return string;
        }

        String[] arr = new String[listOfStudent.size()];
        for (int i = 0; i < listOfStudent.size(); i++) {
            arr[i] = listOfStudent.get(i);
        }
        string = string + arr.toString();
        return string;
    }

    public String studentStat(String userName) {
        String string = userName + "|";
        Student student = findStudent(userName);
        LinkedList<Course> courses = student.getCoursesList();
        if (courses.isEmpty()) {
            string = string + "[]";
            return string;
        }

        for (int i = 0; i < courses.size(); i++) {
            string = string + courses.get(i).getCourseNum() + ",";
        }
        return string;
    }

    public String isRegistered(String userName, int courseNum) {
        Course course = findCourse(courseNum);
        if (course != null) {
            LinkedList<String> registeredStudents = course.getRegisteredStudent();
            for (int i = 0; i < registeredStudents.size(); i++) {
                if (registeredStudents.get(i).equals(userName)) {
                    return "REGISTERD";
                }
            }
        }
        return "NOT REGISTERED";
    }

    public Message unRegister(String userName, int courseNum) {
        int opCode = 10;
        Course course = findCourse(courseNum);
        Student student = findStudent(userName);

        if (course == null | student == null)
            return new Error(10);


        LinkedList<String> regStudent = course.getRegisteredStudent();//the student who registered to course
        LinkedList<Course> coursesList = student.getCoursesList(); //the courses of the students

        //delete the student from the course
        for (int i = 0; i < regStudent.size(); i++) {
            if (regStudent.get(i).equals(userName)) {
                regStudent.remove(i);
            }
        }

        for (int i = 0; i < coursesList.size(); i++) {
            if (coursesList.get(i).equals(course)) {
                coursesList.remove(i);
            }
        }

        return course.removeNumOfRegisteredStudent(opCode);

    }


    public LinkedList<Integer> myCourses(String userName) {
        Student student = findStudent(userName);
        if (student != null) {
            LinkedList<Course> courses = student.getCoursesList();
            LinkedList<Integer> myCourses = null;

            for (int i = 0; i < courses.size(); i++) {
                myCourses.add(courses.get(i).getCourseNum());
            }
            return myCourses;
        }
        return null;

    }
}
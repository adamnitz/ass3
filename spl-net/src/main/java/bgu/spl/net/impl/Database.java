package bgu.spl.net.impl;


import bgu.spl.net.impl.DataObjects.Admin;
import bgu.spl.net.impl.DataObjects.Course;
import bgu.spl.net.impl.DataObjects.Student;
import bgu.spl.net.impl.DataObjects.User;
import bgu.spl.net.impl.Message.Error;
import bgu.spl.net.impl.Message.Ack;
import bgu.spl.net.impl.Message.KdamCheck;
import bgu.spl.net.impl.Message.Message;


import javax.swing.text.DefaultStyledDocument;
import java.io.*;
import java.util.Arrays;
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

    private Vector<Course> allCourses;
    private final Vector<User> allUsers;

    private static class DataHolder {
        private static final Database instance = new Database();
    }

    //to prevent user from creating new Database
    private Database() {
        allCourses = new Vector<>();
        allUsers = new Vector<>();
    }

    public Vector<Course> getAllCourses() {
        return allCourses;
    }

    public Vector<User> getAllUsers() {
        return allUsers;
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
    public boolean initialize(String coursesFilePath) {
        try {
            FileReader file = new FileReader(coursesFilePath);
            System.out.println(file+" file");
            BufferedReader in = new BufferedReader(file);
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                return false;
            }
            int index=0;
            while (line != null) {
                String[] lineArr = line.split("\\|");
                int courseNum = Integer.parseInt(lineArr[0]);
                String courseName = lineArr[1];
                LinkedList<Course> kdamCourseList = new LinkedList<Course>();
                String[] kdamCourseArr = lineArr[2].substring(1,lineArr[2].length()-1).split(",");
                if(kdamCourseArr[0]=="")
                    kdamCourseArr = new String[0];
                for (int i = 0; i < kdamCourseArr.length; i++) {
                    int kdamCourseNum = Integer.parseInt(kdamCourseArr[i]);
                    Course course = findCourse(kdamCourseNum);
                    kdamCourseList.add(course);
                //TODO:check if it's possibbole that there is course that wasnt registered yet
                }

            int numOfMaxStudents = Integer.parseInt(lineArr[3]);
            Course course = new Course(courseNum, courseName, kdamCourseList, numOfMaxStudents,index);
            course.setKdamCourseList(sortByCoursList(course.getKdamCourseList()));
            allCourses.add(course);

            try {
                line = in.readLine();
            } catch (IOException e) {
                return false;
            }
         index++;
        }
    }
        catch (FileNotFoundException e) {
                System.out.print("The File not found");
        }

        return true;
    }


    public Message adminReg(String userName, String password) {
        int opCode = 1;
        boolean isExist = false;
        for (int i = 0; i < allUsers.size() && !isExist; i++) {
       //     if (allUsers.get(i) instanceof Admin) {
                if (allUsers.get(i).getUserName().equals(userName))
                    isExist = true;
          //  }
        }
        if (isExist) {
            return new Error(opCode);
        } else {
            User admin = new Admin(userName, password);
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

        if (isExist == false)
            user = null;
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
        if (isExist == false)
            student = null;

        return student;
    }

    public Course findCourse(int courseNum) {
        boolean courseIsExist = false;
        Course course = null;
        for (int i = 0; i < allCourses.size() && !courseIsExist; i++) {
            course = allCourses.get(i);
            if (course.getCourseNum()==courseNum) {
                courseIsExist = true;
            }
        }
        if(courseIsExist == false)
            course = null;
        return course;
    }

    public Message studentReg(String userName, String password) {
        int opCode = 2;
        if (findUser(userName) != null)
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
        if (user == null || user.isLogIn() || (!user.getPassword().equals(password)))
            return new Error(opCode);

        user.setLogIn(true);
        return new Ack(opCode);
    }


    public Message logOut(String userName) {
        int opCode = 4;
        User user = findUser(userName);
        if (user != null) {
            System.out.println("check in function: " + user.isLogIn());
            if (!user.isLogIn()){
                System.out.println("enter186");
                return new Error(opCode);
            }
            else {
                user.setLogIn(false);
                return new Ack(opCode);
            }
        }
        System.out.println("dataBase ok");
        return new Error(opCode);
    }

    public Message courseReg(String userName, int courseNum, String myUser) {
        int opCode = 5;
        if(myUser == null)
            return new Error(opCode);
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
        if (student == null)
            return new Error(opCode);

        if (kdamCheck(userName, courseNum, myUser) instanceof Error)
            return new Error(opCode);

        student.addCourse(course);
        course.addStudentToCourse(student.getUserName());
        return course.addNumOfRegisteredStudent(opCode);

    }

    public Message kdamCheck(String userName, int courseNumber,String myUser) {
        int opCode = 6;
        if(myUser == null)
            return new Error(opCode);
        User user = findUser(myUser);
        if(user instanceof Admin){
            return new Error(opCode);
        }
        Student student = findStudent(userName);
        Course course = findCourse(courseNumber);
        if(course ==null)
            return new Error(opCode);
        LinkedList<Course> courseList = student.getCoursesList();
        LinkedList<Course> kdamCourseList = course.getKdamCourseList();
        boolean studentDoneThisCourse = false;
        boolean allKdamDone = true;
        boolean notfinish=false;
        for (int i=0;i< courseList.size()&& !notfinish;i++){
            for(int j = 0;j<kdamCourseList.size() && !studentDoneThisCourse && allKdamDone;j++){
                boolean isEquals = courseList.get(i).equals(kdamCourseList.get(j));
                if(!isEquals)
                    studentDoneThisCourse=false;
                else {
                    if (j == kdamCourseList.size() - 1) {
                        allKdamDone = true;
                        notfinish = true;
                    }
                    studentDoneThisCourse = true;
                }
                if((i == courseList.size() - 1)) {
                    if (isEquals) {
                        allKdamDone = true;
                        notfinish = true;
                    }
                    else
                        allKdamDone = false;
                }
            }
            studentDoneThisCourse=false;

        }

        if (!allKdamDone ) {
            return new Error(opCode);
        }
        if(notfinish)
            return new Error(opCode);

        Ack ack = new Ack(opCode);
        ack.setKdamCourses(kdamCourseList);
        return ack;
    }


    public Message courseStat(int courseNumber, String myUser) {
        int opCode=7;
        if(myUser == null)
            return new Error(opCode);
        Course course = findCourse(courseNumber);
        if(course == null){
            return new Error(opCode);
        }
        User user = findUser(myUser);
        if(user instanceof Student){
            return new Error(opCode);
        }
        int courseNum = course.getCourseNum();
        String courseName = course.getCourseName();
        int availableSeats = course.getNumOfMaxStudent() - course.getNumOfRegisteredStudent();
        String listOfStudent = course.getStringRegisteredStudent();
        Ack ack = new Ack(opCode);

        String string = "Course:" + "("+courseNum+")" + courseName + '\n' + "seatsAvailable: " +
                availableSeats + "/" + course.getNumOfMaxStudent()+ '\n' + "Students Registered: " + listOfStudent;
        //String string = courseNum + "|" + courseName + "|" + availableSeats + "/" + course.getNumOfMaxStudent() +
          //      "|" + listOfStudent;

        ack.setData(string);

        return ack;
    }

    public Message studentStat(String userName, String myUser) {
        int opCode = 8;
        if(myUser == null)
            return new Error(opCode);
        //String string = userName + "|";
        User user = findUser(myUser);
        if(user instanceof Student){
            return new Error(opCode);
        }

        if( findUser(userName) instanceof Admin)
            return new Error(opCode);

        Student student = findStudent(userName);

        if(student == null){
            return new Error(opCode);
        }

        String courses = student.getStringCoursesList();

        Ack ack = new Ack(opCode);

        String string = "Student: " + userName + '\n' +  "Courses: " + courses;
        System.out.println("ackData" + string);

        ack.setData(string);
        System.out.println("ackData" + ack.getData());
        return ack;
    }

    public Message isRegistered(String userName, int courseNum,String myUser) {
        int opCode = 9;
        User user = findUser(myUser);
        if(user instanceof Admin){
            return new Error(opCode);
        }
        Ack ack = new Ack(opCode);
        Course course = findCourse(courseNum);
        boolean found = false;
        if (course != null) {
            LinkedList<String> registeredStudents = course.getRegisteredStudent();
            for (int i = 0; i < registeredStudents.size() && !found; i++) {
                if (registeredStudents.get(i).equals(userName)) {
                    ack.setData("REGISTERD");
                    found = true;
                }
            }

        }
        if(!found)
            ack.setData("NOT REGISTERED");

        return ack;
    }

    public Message unRegister(String userName, int courseNum,String myUser) {
        int opCode = 10;
        if(myUser == null)
            return new Error(opCode);
        User user = findUser(myUser);
        if(user instanceof Admin){
            return new Error(opCode);
        }
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

    public Message myCourses(String userName,String myUser) {
        int opCode=11;
        if(myUser == null)
            return new Error(opCode);
        User user = findUser(myUser);
        if(user instanceof Admin){
            return new Error(opCode);
        }
        Ack ack = new Ack(11);
        Student student = findStudent(userName);
        if (student != null) {
            LinkedList<Course> courses = student.getCoursesList();
            courses = sortByCoursList(courses);
          //  LinkedList<Integer> myCourses = new LinkedList<Integer>();

           /* for (int i = 0; i < courses.size(); i++) {
                myCourses.add(courses.get(i).getCourseNum());
            }*/

            ack.setMyCourses(courses);
            return ack;
        }
        return new Error(opCode);
    }

    public  LinkedList<Course> sortByCoursList( LinkedList<Course> kdamCourses){

        int len=kdamCourses.size();
        int []tmp=new int[len];
        int[][]array=new int [2][len];
        int i=0;
        for(int k=0;k<len;k++) {
            int kdamcurr = kdamCourses.get(k).getCourseNum();
            array[i][k] = kdamcurr;
            int ind = findCourse(kdamcurr).getIndex();
            array[i + 1][k] = ind;
            tmp[k] = ind;

        }
        boolean found=false;

        Arrays.sort(tmp);
        int [] sortcourse=new int[len];
        for(int b=0;b<len;b++){
            found=false;
            for (int j=0;j<len&& !found;j++){

                if(tmp[b]==array[1][j]) {
                    sortcourse[b] = array[0][j];
                    found = true;
                }
            }
        }
        LinkedList<Course> ans=new LinkedList<Course>();
        for(int x=0;x<len;x++)
            ans.add(findCourse(sortcourse[x]));

        return ans;
    }
}
package bgu.spl.net.impl;


import bgu.spl.net.impl.DataObjects.Admin;
import bgu.spl.net.impl.DataObjects.Course;
import bgu.spl.net.impl.DataObjects.Student;
import bgu.spl.net.impl.DataObjects.User;
import bgu.spl.net.impl.Message.Error;
import bgu.spl.net.impl.Message.Ack;
import bgu.spl.net.impl.Message.KdamCheck;
import bgu.spl.net.impl.Message.Message;


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
            if (course.getCourseNum()==courseNum) {
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

    public Message courseReg(String userName, int courseNum) {
        System.out.println("im in coursereg in dataBse");
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

        if (kdamCheck(userName, courseNum) instanceof Error)
            return new Error(opCode);

        student.addCourse(course);
        course.addStudentToCourse(student.getUserName());
        return course.addNumOfRegisteredStudent(opCode);

    }

    public Message kdamCheck(String userName, int courseNumber) {
        int opCode = 6;
        Student student = findStudent(userName);
        Course course = findCourse(courseNumber);
        LinkedList<Course> courseList = student.getCoursesList();
        LinkedList<Course> kdamCourseList = course.getKdamCourseList();
        boolean studentDoneThisCourse = false;
        boolean allKdamDone = true;
        boolean finish=false;
        for (int i=0;i< courseList.size()&& !finish;i++){
            for(int j = 0;j<kdamCourseList.size() && !studentDoneThisCourse && allKdamDone;j++){
                boolean isEquals = courseList.get(i).equals(kdamCourseList.get(j));
                if(!isEquals)
                    studentDoneThisCourse=false;
                else {
                    if (j == kdamCourseList.size() - 1) {
                        allKdamDone = true;
                        finish = true;
                    }
                    studentDoneThisCourse = true;
                }
                if((i == courseList.size() - 1)) {
                    if (isEquals)
                        allKdamDone = true;
                    else
                        allKdamDone = false;
                }
            }
            studentDoneThisCourse=false;

        }

        if (!allKdamDone) {
            return new Error(opCode);
        }

        Ack ack = new Ack(opCode);
        ack.setKdamCourses(kdamCourseList);
        return ack;
    }

   /* public Message kdamCheck(int courseNumber){
        int opCode=6;
        Course currCurse=findCourse(courseNumber);
        LinkedList<Course> kdam = currCurse.getKdamCourseList();

        return new KdamCheck(opCode, courseNumber,kdam);
    }*/

    public Message courseStat(int courseNumber) {
        int opCode=7;
        Course course = findCourse(courseNumber);
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

    public Message studentStat(String userName) {
        int opCode = 8;
        String string = userName + "|";
        Student student = findStudent(userName);
        LinkedList<Course> courses = student.getCoursesList();
        Ack ack = new Ack(opCode);

        if (courses.isEmpty()) {
            string = string + "[]";
            ack.setData(string);
        }

        for (int i = 0; i < courses.size(); i++) {
            string = string + courses.get(i).getCourseNum() + ",";
        }
        ack.setData(string);
        return ack;
    }

    public Ack isRegistered(String userName, int courseNum) {
        int opCode = 9;
        Ack ack = new Ack(opCode);
        Course course = findCourse(courseNum);
        if (course != null) {
            LinkedList<String> registeredStudents = course.getRegisteredStudent();
            for (int i = 0; i < registeredStudents.size(); i++) {
                if (registeredStudents.get(i).equals(userName)) {
                    ack.setData("REGISTERD");
                }
            }
        }
        ack.setData("NOT REGISTERED");
        return ack;
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

    public Message myCourses(String userName) {
        int opCode=11;
        Ack ack = new Ack(11);
        Student student = findStudent(userName);
        if (student != null) {
            LinkedList<Course> courses = student.getCoursesList();
            LinkedList<Integer> myCourses = null;

            for (int i = 0; i < courses.size(); i++) {
                myCourses.add(courses.get(i).getCourseNum());
            }
            ack.setMyCourses(myCourses);
            return ack;
        }
        return new Error(opCode);
    }

    public  LinkedList<Course> sortByCoursList( LinkedList<Course> kdamCourses){

        int len=kdamCourses.size();
        int[][]array=new int [2][len];
        int i=0;
        for(int k=0;k<len;k++){

            int kdamcurr=kdamCourses.get(k).getCourseNum();
            array[i][k]=kdamcurr;
            int ind=findCourse(kdamcurr).getIndex();
            array[i+1][k]=ind;
        }
        boolean found=false;
        int[] tmp=array[1];
        Arrays.sort(tmp);
        int [] sortcourse=new int[len];
        for(int b=0;b<len;b++){
            found=false;
            for (int j=0;j<len&& !found;j++){

                if(tmp[b]==array[1][j]) {
                    sortcourse[j] = array[0][j];
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
package bgu.spl.net;


import bgu.spl.net.impl.DataObjects.Course;
import bgu.spl.net.impl.DataObjects.User;

import javax.xml.crypto.Data;
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

    Vector<Course> allCourses = new Vector<Course>();
    Vector<User> allUsers = new Vector<User>();

    private static class DataHolder {
        private static Database instance = new Database();
    }

    //to prevent user from creating new Database
    private Database() {
        // TODO: implement
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
        return false;
    }


}

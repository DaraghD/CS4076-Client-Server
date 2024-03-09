package dddq.client;

import java.util.ArrayList;

public class Course {
    private String courseName;
    private ArrayList<String> modules;


    public Course(String courseName){
        this.courseName = courseName;
        modules = new ArrayList<String>();
    }

    public void addModule(String moduleName) throws IncorrectActionException {
        if(modules.size() == 5 ) {
            throw new IncorrectActionException("Cannot add " + moduleName + "\nCourse already has 5 modules");
        }
        modules.add(moduleName);
    }
}

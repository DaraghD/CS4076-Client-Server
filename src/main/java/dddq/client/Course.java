package dddq.client;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private String courseName;
    private ArrayList<String> Programmes;


    public Course(String courseName){
        this.courseName = courseName;
        Programmes = new ArrayList<String>();
    }

    public void addProgramme(String ProgrammeName) throws IncorrectActionException {
        if(Programmes.size() == 5 ) {
            throw new IncorrectActionException("Cannot add " + ProgrammeName + "\nCourse already has 5 Programmes");
        }
        Programmes.add(ProgrammeName);
    }
}

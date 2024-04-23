package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.ScheduleDay;
import dddq.client.TimeSlot;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class Module implements Serializable {
    private ConcurrentHashMap<String, ScheduleDay> classes; // DAY : Schedule for that day ?
    private String programme;
    private String name;

    Module(String programme, String name){
        this.programme = programme;
        this.name = name;
        classes = new ConcurrentHashMap<String, ScheduleDay>();
        //initalise days of the week in classes
        classes.put("Monday", new ScheduleDay(programme,false));
        classes.put("Tuesday", new ScheduleDay(programme,false));
        classes.put("Wednesday", new ScheduleDay(programme,false));
        classes.put("Thursday", new ScheduleDay(programme,false));
        classes.put("Friday", new ScheduleDay(programme,false));
    }

    public void addClass(String className, String day, String time, String room) throws IncorrectActionException {
        TimeSlot timeSlot = classes.get(day).getTimeTable().get(time);
        if(timeSlot.isTaken()){
            throw new IncorrectActionException("Time slot occupied : " + day + ": " + time);
        }
        timeSlot.takeSlot();
        timeSlot.setRoom(room);
        timeSlot.setModule(className);
    }

    public String getName(){
        return name;
    }

    public ScheduleDay getDay(String day){
        return classes.get(day);
    }

    public ConcurrentHashMap<String, ScheduleDay> getClasses(){
        return classes;
    }

    public String getProgramme(){
        return programme;
    }
}

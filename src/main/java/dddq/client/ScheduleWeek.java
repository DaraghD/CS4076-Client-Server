package dddq.client;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleWeek {
    private String className; // programme / course name - called class in the spec
    private String room;
    private HashMap<String,ScheduleDay> scheduleDays = new HashMap<String,ScheduleDay>();

     public ScheduleWeek() {
        scheduleDays = new HashMap<>();
        scheduleDays.put("Monday", new ScheduleDay());
        scheduleDays.put("Tuesday", new ScheduleDay());
        scheduleDays.put("Wednesday", new ScheduleDay());
        scheduleDays.put("Thursday", new ScheduleDay());
        scheduleDays.put("Friday", new ScheduleDay());
    }

    public ScheduleDay getDay(String day) throws IncorrectActionException {
        ScheduleDay scheduleDay = scheduleDays.get(day);
        if (scheduleDay == null) {
            throw new IncorrectActionException("Invalid day");
        }
        return scheduleDay;
    }
}

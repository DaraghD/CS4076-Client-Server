package dddq.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ScheduleDay implements Serializable {
    private LocalDate date;
    private HashMap<String, TimeSlot> timeTable = new HashMap<>();

    public ScheduleDay() {
        // make it so when they try to click to opne schedule, it displays error unless date is already selected !
        // When date is selected and they click to view schedule we ask the server to send us the schedule for the day. and display that
        // event handler will need to take in a timetable or schedule object and then do some forloop over to create buttons for each tiem .

        // displaySchedule() <-  popup new stage with the buttons for schedule .
        // user can click multiple times - making them red when selected, they then click confirm.
        date = LocalDate.now();

        timeTable.put("09:00", new TimeSlot());
        timeTable.put("09:30", new TimeSlot());
        timeTable.put("10:00", new TimeSlot());
        timeTable.put("10:30", new TimeSlot());
        timeTable.put("11:00", new TimeSlot());
        timeTable.put("11:30", new TimeSlot());
        timeTable.put("12:00", new TimeSlot());
        timeTable.put("12:30", new TimeSlot());
        timeTable.put("13:00", new TimeSlot());
        timeTable.put("13:30", new TimeSlot());
        timeTable.put("14:00", new TimeSlot());
        timeTable.put("14:30", new TimeSlot());
        timeTable.put("15:00", new TimeSlot());
        timeTable.put("15:30", new TimeSlot());
        timeTable.put("16:00", new TimeSlot());
        timeTable.put("16:30", new TimeSlot());
        timeTable.put("17:00", new TimeSlot());
        timeTable.put("17:30", new TimeSlot());
        timeTable.put("18:00", new TimeSlot());
    }

    public HashMap<String, TimeSlot> getTimeTable() {
        return timeTable;
    }

    public boolean checkTime(String time) {
        System.out.println("CHECKING TIEM "+ time);
        System.out.println("TIME IS : " + timeTable.get(time).isTaken());
        return timeTable.get(time).isTaken();
    }

    public boolean bookTime(String time) throws IncorrectActionException {
        if (timeTable.get(time).isTaken()) {
            return false;
        }
        timeTable.get(time).takeSlot();
        return true;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (Map.Entry<String, TimeSlot> entry : timeTable.entrySet()) {
            string.append(entry.getKey()).append(":").append(entry.getValue().isTaken()).append("\n");
        }
        return string.toString();
    }
}

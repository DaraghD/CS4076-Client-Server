package dddq.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Schedule implements Serializable {
    private LocalDate date;
    private HashMap<String, TimeSlot> timeTable = new HashMap<>();

    public Schedule() {
        // make it so when they try to click to opne schedule, it displays error unless date is already selected !
        // When date is selected and they click to view schedule we ask the server to send us the schedule for the day. and display that
        // event handler will need to take in a timetable or schedule object and then do some forloop over to create buttons for each tiem .

        // displaySchedule() <-  popup new stage with the buttons for schedule .
        // user can click multiple times - making them red when selected, they then click confirm.
        date = LocalDate.now();
        timeTable.put("08:00", new TimeSlot());
        timeTable.put("08:30", new TimeSlot());
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

    public HashMap<String, Boolean> getTimeTable() {
        return timeTable;
    }

    public boolean checkTime(String time) {
        return timeTable.get(time);
    }

    public boolean bookTime(String time) {
        if (timeTable.get(time) != false) {
            timeTable.put(time, true);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : timeTable.entrySet()) {
            string.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        return string.toString();
    }

    // INITIALIZE SERVER, WHEN START SERVER, CHECK ALL SCHEDULES, IF SCHEDULE.DAY < CURRENT DAY - DELETE SCHEDULE


}

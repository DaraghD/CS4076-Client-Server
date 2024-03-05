package dddq.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class Schedule  {
    private LocalDate date;
    private HashMap<String,Boolean> timeTable = new HashMap<>();
    public Schedule() {
        // make it so when they try to click to opne schedule, it displays error unless date is already selected !
        // When date is selected and they click to view schedule we ask the server to send us the schedule for the day. and display that
        // event handler will need to take in a timetable or schedule object and then do some forloop over to create buttons for each tiem .

        // displaySchedule() <-  popup new stage with the buttons for schedule .
        // user can click multiple times - making them red when selected, they then click confirm.
        date = LocalDate.now();
        timeTable.put("08:00", false);
        timeTable.put("08:30", false);
        timeTable.put("09:00", false);
        timeTable.put("09:30", false);
        timeTable.put("10:00", false);
        timeTable.put("10:30", false);
        timeTable.put("11:00", false);
        timeTable.put("11:30", false);
        timeTable.put("12:00", false);
        timeTable.put("12:30", false);
        timeTable.put("13:00", false);
        timeTable.put("13:30", false);
        timeTable.put("14:00", false);
        timeTable.put("14:30", false);
        timeTable.put("15:00", false);
        timeTable.put("15:30", false);
        timeTable.put("16:00", false);
        timeTable.put("16:30", false);
        timeTable.put("17:00", false);
        timeTable.put("17:30", false);
        timeTable.put("18:00", false);
    }

    public HashMap<String, Boolean> getTimeTable(){
        return timeTable;
    }

    public boolean bookTime(String time){
        if(timeTable.get(time) != false){
            timeTable.put(time,true);
            return true;
        }
        return false;
    }

    // INITIALIZE SERVER, WHEN START SERVER, CHECK ALL SCHEDULES, IF SCHEDULE.DAY < CURRENT DAY - DELETE SCHEDULE

    
}

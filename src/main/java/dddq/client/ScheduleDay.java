package dddq.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleDay implements Serializable {
    private String ProgrammeName; // may be redundant since we are using hashmap now ?
    private String room; // same as above,  possibly redundant
    private HashMap<String, TimeSlot> timeTable = new HashMap<>();

    public ScheduleDay(String ProgrammeName) {
        this.ProgrammeName = ProgrammeName;
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
        return timeTable.get(time).isTaken();
    }
    public String getProgrammeName() {
        return ProgrammeName;
    }

    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    public void setProgrammeName(String ProgrammeName) {
        this.ProgrammeName = ProgrammeName;
    }

    public ArrayList<String> getTakenTimes() {
        ArrayList<String> listOfTakenTimes = new ArrayList<>();
        for (Map.Entry<String, TimeSlot> entry : timeTable.entrySet()) {
            TimeSlot a = entry.getValue();
            String time = entry.getKey();
            if (a.isTaken()) {
                listOfTakenTimes.add(time);
            }
        }
        return listOfTakenTimes;

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

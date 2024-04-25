package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.ScheduleDay;
import dddq.client.TimeSlot;

public class EarlyLectureThread implements Runnable {
    private String[] timeArray = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30", "18:00"
    };
    ScheduleDay day;
    String sDay;

    EarlyLectureThread(ScheduleDay day, String dayString) {
        this.day = day;
        this.sDay = dayString;
    }

    @Override
    public void run() {
        for (String time : timeArray) {
            if(day.getTimeSlot(time).getRoom() == null){
                continue;
            }
            TimeSlot current = day.getTimeSlot(time);
            String currentRoom = current.getRoom();
            ScheduleDay roomTimetable = Server.roomTimetable.get(sDay).get(currentRoom);

            if (current.isTaken()) { // timeslot free for module and ROOM
                String earliestTime = findEarliestFreeTime(time, roomTimetable);
                System.out.println("Earliest :   " +earliestTime );
                if(earliestTime == null){
                    continue;
                }

                int indexEarliest = getIndex(earliestTime);
                int indexCurrent = getIndex(time);
                if (indexCurrent > indexEarliest) {
                    try {
                        shiftTimeSlot(time, earliestTime, roomTimetable);
                    } catch (IncorrectActionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    String findEarliestFreeTime(String time, ScheduleDay roomTimetable) {
        // check room timetable aswell?
        for (String t : timeArray) {
            if (!day.getTimeSlot(t).isTaken() && !roomTimetable.getTimeSlot(t).isTaken()) {
                return t;
            }
        }
        return null; // they are all taken
    }

    int getIndex(String time) {
        for (int i = 0; i < timeArray.length; i++) {
            if (timeArray[i].equals(time)) {
                return i;
            }
        }
        return -1; // should never happen
    }

    void shiftTimeSlot(String current, String newTime, ScheduleDay roomTimetable) throws IncorrectActionException {
        day.getTimeSlot(newTime).setModule(day.getTimeSlot(current).getModule());
        day.getTimeSlot(newTime).setRoom(day.getTimeSlot(current).getRoom());
        day.getTimeSlot(newTime).takeSlot();
        day.getTimeSlot(current).freeSlot();

        roomTimetable.getTimeSlot(newTime).setModule(day.getTimeSlot(current).getModule());
        roomTimetable.getTimeSlot(newTime).setRoom(day.getTimeSlot(current).getRoom());
        roomTimetable.getTimeSlot(newTime).takeSlot();
        roomTimetable.getTimeSlot(current).freeSlot();
    }
}

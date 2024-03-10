package dddq.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {
    private final String OPTION;
    // ADD , VIEW , DISPLAY , REMOVE ETC.
    private String CONTENTS = null;
    private final String MODULE_NAME = null;
    private String Day= null;
    private final String ROOM_NUMBER = null;
    private ArrayList<String> listOfTimes = new ArrayList<String>();
    // list of times,

//To add or remove a class, the client selects a date, time, and room number
//and then provides the name of the class to schedule.

    public Message(String option) {
        OPTION = option;
    }

    public void setDay(String date){
        Day = date;
    }

    public String getDay() {
        return Day;
    }
    public String getOPTION() {
        return OPTION;
    }
    public ArrayList<String> getListOfTimes() {
        return listOfTimes;
    }
    public void addTime(String time){
        listOfTimes.add(time);
    }
    public void setCONTENTS(String CONTENTS) {
        this.CONTENTS = CONTENTS;
    }

    public String getCONTENTS() {
        return CONTENTS;
    }
}

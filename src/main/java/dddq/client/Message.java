package dddq.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {
    private String OPTION;
    // ADD , VIEW , DISPLAY , REMOVE ETC.
    private String CONTENTS;
    private String MODULE_NAME;
    private String Day;
    private String ROOM_NUMBER;
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
    public void setListOfTimes(ArrayList<String> listOfTimes) {
        this.listOfTimes = listOfTimes;
    }
    public void setCONTENTS(String CONTENTS) {
        this.CONTENTS = CONTENTS;
    }
    public void setROOM_NUMBER(String ROOM_NUMBER) {
        this.ROOM_NUMBER = ROOM_NUMBER;
    }

    public String getCONTENTS() {
        return CONTENTS;
    }

    public String getROOM_NUMBER() {
        return ROOM_NUMBER;
    }

    public String getMODULE_NAME() {
        return MODULE_NAME;
    }

    public void setMODULE_NAME(String MODULE_NAME) {
        this.MODULE_NAME = MODULE_NAME;
    }


    @Override
    public String toString() {
        return "Message{" +
                "OPTION='" + OPTION + '\'' +
                ", CONTENTS='" + CONTENTS + '\'' +
                ", MODULE_NAME='" + MODULE_NAME + '\'' +
                ", Day='" + Day + '\'' +
                ", ROOM_NUMBER='" + ROOM_NUMBER + '\'' +
                ", listOfTimes=" + listOfTimes +
                '}';
    }
}

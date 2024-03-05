package dddq.client;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class Message implements Serializable {
    private final String OPTION;
    private final String CONTENTS = null;
    private final String CLASS_NAME = null;
    private LocalDate DATE = null;
    private final String ROOM_NUMBER = null;
    private final String START_TIME = null;
    private final String END_TIME = null;

//To add or remove a class, the client selects a date, time, and room number
//and then provides the name of the class to schedule.

    public Message(String option) {
        OPTION = option;
    }

    public void setDate(LocalDate date){
        DATE = date;
    }

    public LocalDate getDate(){
        return this.DATE;
    }
    public String getOPTION() {
        return OPTION;
    }

    public String getCONTENTS() {
        return CONTENTS;
    }
}

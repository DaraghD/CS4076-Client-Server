package dddq.client;

import java.time.LocalDate;

public class Message {

    private final String OPTION;
    private final String CONTENTS;
    private final String CLASS_NAME = null;
    private final LocalDate DATE = null;
    private final String ROOM_NUMBER = null;
    private final String START_TIME = null;
    private final String END_TIME = null;

//To add or remove a class, the client selects a date, time, and room number
//and then provides the name of the class to schedule.

    public Message(String option, String contents) {
        OPTION = option;
        CONTENTS = contents;
    }
}

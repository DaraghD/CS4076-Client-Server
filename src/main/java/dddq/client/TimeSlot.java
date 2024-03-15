package dddq.client;

import java.io.Serializable;

public class TimeSlot implements Serializable {
    private boolean isTaken;
    private String module;
    private String room;

    public TimeSlot() {
        //Time slots by default are not Taken, and thus have no Programme name
        isTaken = false;
    }

    public boolean isTaken(){
        return isTaken;
    }
    public void setModule(String module){
        this.module = module;
    }

    public String getModule(){
        return module;
    }

    public void freeSlot() throws IncorrectActionException {
        if (!isTaken) {
            throw new IncorrectActionException("Time slot already free");
        } else {
            isTaken = false;
        }
    }

    public void takeSlot() throws IncorrectActionException {
        if (isTaken) {
            throw new IncorrectActionException("Time slot already taken");
        } else {
            isTaken = true;
        }
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

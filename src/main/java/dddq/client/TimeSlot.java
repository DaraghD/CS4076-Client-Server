package dddq.client;

import java.io.Serializable;

public class TimeSlot implements Serializable {
    private boolean isTaken;


    public TimeSlot() {
        //Time slots by default are not Taken, and thus have no Programme name
        isTaken = false;
    }

    public boolean isTaken(){
        return isTaken;
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
}

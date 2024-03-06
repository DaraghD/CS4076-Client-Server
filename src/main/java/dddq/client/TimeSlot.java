package dddq.client;

import java.io.Serializable;

public class TimeSlot implements Serializable {


    private boolean isTaken;

    private String moduleName;


    public TimeSlot() {
        //Time slots by default are not Taken, and thus have no module name
        isTaken = false;
        moduleName = null;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean isTaken(){
        return isTaken;
    }

    public void takeSlot() throws IncorrectActionException {
        if (isTaken) {
            throw new IncorrectActionException("Time slot already taken");
        } else {
            isTaken = true;
        }
    }
}

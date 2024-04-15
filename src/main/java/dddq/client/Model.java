package dddq.client;

import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Model {
    //contains actual data
    //view is just a graphical representation of this data
    //controller is the one that manipulates this data

    private View view;
    private Controller controller;

    // These variables contain the state of the programme.
    private StringProperty room_name;
    private StringProperty programme_name;
    private StringProperty module_name;
    private StringProperty day;
    private ArrayList<String> times; // get set by controller in schedule selector
    private StringProperty action; // also referred to as option in some places

    // Getters and setters for the variables
    //TODO: bind these values to the view gui controls ?
    public String getRoom_name() {
        return room_name.getValue();
    }

    public void setRoom_name(String room_name) {
        this.room_name.setValue(room_name);
    }

    public StringProperty roomNameProperty() {
        return room_name;
    }

    public String getProgramme_name() {
        return programme_name.getValue();
    }

    public void setProgramme_name(String programme_name) {
        this.programme_name.setValue(programme_name);
    }

    public StringProperty programmeNameProperty() {
        return programme_name;
    }

    public String getModule_name() {
        return module_name.get();
    }

    public void setModule_name(String module_name) {
        this.module_name.set(module_name);
    }

    public StringProperty moduleNameProperty() {
        return module_name;
    }

    public String getDay() {
        return day.getValue();
    }

    public void setDay(String day) {
        this.day.setValue(day);
    }

    public StringProperty dayProperty() {
        return day;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    public String getAction() {
        return action.get();
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public StringProperty actionProperty() {
        return action;
    }


}

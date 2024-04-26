package dddq.client;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;


public class Model {
    //contains actual data
    //view is just a graphical representation of this data
    //controller is the one that manipulates this data

    private View view;
    private boolean debug;
    private Controller controller;


    Model(boolean debug) {
        this.debug = debug;
        if(debug){
            room_name.set("a");
            programme_name.set("Computer Science");
            day.set("Monday");
            action.set("ADD");
            module_name.set("m");
        }
    }

    public void setView(View v){
        view=v;
    }
    // These variables contain the state of the programme.
    private StringProperty room_name = new SimpleStringProperty();
    private StringProperty programme_name = new SimpleStringProperty();
    private StringProperty module_name = new SimpleStringProperty();
    private StringProperty day = new SimpleStringProperty();
    private StringProperty action = new SimpleStringProperty();
    private ArrayList<String> times = new ArrayList<>(); // get set by controller in schedule selector

    // Getters and setters for the variables
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
        view.getChosenTimesLabel().setText("Chosen Times : " + times.toString());
    }

    public void addTime(String time){
        times.add(time);
        view.getChosenTimesLabel().setText("Chosen Times : " + times.toString());
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

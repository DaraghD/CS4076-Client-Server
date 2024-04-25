package dddq.client;

import dddq.server.Module;
import dddq.server.Programme;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;


public class TimetableGUI  {

    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    static String[] times_string = {"09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00"};
    public static GridPane makeTimetable(Programme programme){

        GridPane timetable = new GridPane();
        timetable.setPadding(new Insets(10));
        timetable.setHgap(5);
        timetable.setVgap(5);
        timetable.setMaxHeight(800);
        timetable.setMaxWidth(800);


        // Add column constraints to make columns expand horizontally
        for (int i = 0; i < 6; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            timetable.getColumnConstraints().add(column);
        }

        // Add row constraints to make rows expand vertically
        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            timetable.getRowConstraints().add(row);
        }

        // Days labels
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold");
            GridPane.setConstraints(dayLabel, i + 1, 0);
            timetable.getChildren().add(dayLabel);
        }

        // Time labels
        for (int i = 0; i <= 8; i++) {
            Label timeLabel = new Label((i + 9) + ":00");
            timeLabel.setStyle("-fx-font-weight: bold");
            GridPane.setConstraints(timeLabel, 0, i + 1);
            GridPane.setValignment(timeLabel, VPos.CENTER);
            GridPane.setHalignment(timeLabel, HPos.CENTER);
            timetable.getChildren().add(timeLabel);
        }


        // Time slots
        for (int day = 1; day <= 5; day++) {
            //TODO: remove 18:00 from everywhere i think?
            for (int time = 1; time <= 9; time++) {
                TextArea textField = new TextArea();
                for (Module m : programme.getModules()) {
                    ScheduleDay currentDay = m.getDay(days[day - 1]);
                    TimeSlot currentTime = currentDay.getTimeSlot(times_string[time - 1]);
                    if (currentTime.isTaken()) {
                        textField.setText(currentTime.getModule() + "\n" + currentTime.getRoom());
                    }
                }
                //Could make the taken slots a different colour text from available ?
                textField.setPrefWidth(100);
                textField.setPrefHeight(200);
                textField.setEditable(false);
                textField.mouseTransparentProperty().set(false);
                GridPane.setConstraints(textField, day, time);
                GridPane.setHgrow(textField, Priority.ALWAYS);
                timetable.getChildren().add(textField);
            }
        }

        // Make the days labels resizeable
        for (int i = 1; i <= 5; i++) {
            GridPane.setHgrow(timetable.getChildren().get(i), Priority.ALWAYS);
            GridPane.setHalignment(timetable.getChildren().get(i), HPos.CENTER);
        }

        return timetable;
    }

}

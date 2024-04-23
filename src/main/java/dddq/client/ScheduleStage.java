package dddq.client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class ScheduleStage {

    public static class buttonData {
        private boolean isTaken = false;

        public buttonData(boolean taken) {
            isTaken = taken;
        }

        public void click() {
            isTaken = !isTaken;
        }

        public boolean isTaken() {
            return isTaken;
        }
    }

    public static GridPane createButtonGrid(ArrayList<String> times, boolean remove, String displayLabel) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(7);
        gridPane.setVgap(7);
        gridPane.setPadding(new Insets(10));

        // Define column constraints for 4 columns
        for (int i = 0; i < 3; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        // Define row constraints for an additional row for the label + 5 existing rows
        for (int i = 0; i < 4; i++) { // Now 6 instead of 5 to account for the new label row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setMinHeight(50);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Add a label spanning the entire top row
        Label topLabel = new Label(displayLabel);
        topLabel.setFont(Font.font("Arial", 24));
        topLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        topLabel.setStyle("-fx-background-color: #4063e5; -fx-text-fill: white; -fx-alignment: center;");
        GridPane.setConstraints(topLabel, 0, 0, 3, 1); // Column index, Row index, Column span, Row span
        gridPane.getChildren().add(topLabel);

        int currentHour = 9;
        for (int row = 1; row < 5; row++) { // start at one because of label
            for (int col = 0; col < 3; col++) {
                if (row == 4 && col == 2) {
                    Button button = new Button("Submit");
                    button.setStyle("-fx-background-color: #0022ff; -fx-text-fill:white");
                    button.setFont(Font.font("Arial", 20));
                    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    Client.submitScheduleHandler handler = new Client.submitScheduleHandler();
                    //TODO:  swap this out for the controller one ?
                    button.setOnAction(handler);
                    //replace with controller handler for submit
                    gridPane.add(button, col, row);
                    continue;
                }
                // The logic for buttons remains the same, just adjust the row index by +1
                String fTime = currentHour + " : 00";
                Button button = new Button(fTime);
                button.setFont(Font.font("Arial", 20));
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                if (remove) {
                    button.setUserData(new buttonData(!times.contains(fTime)));
                } else {
                    button.setUserData(new buttonData(times.contains(fTime)));
                }

                if (times.contains(fTime) != remove) {
                    button.setStyle("-fx-background-color: red; -fx-text-fill: white");
                } else {
                    button.setStyle("-fx-background-color: green; -fx-text-fill: white");
                }

                // addtimehandelr
//                button.setOnAction(new Client.buttonScheduleHandler());
                button.setOnAction(new Controller.addTimeHandler());
                GridPane.setConstraints(button, col, row); // Adjust the row by +1

                currentHour++;

                gridPane.getChildren().add(button);
            }
        }
        return gridPane;
    }
}

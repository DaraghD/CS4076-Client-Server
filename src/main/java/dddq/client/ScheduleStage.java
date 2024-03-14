package dddq.client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class ScheduleStage {

    public static class buttonData {
        private boolean isTaken = false;
        private boolean selected = false;

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

    // Method to create a 4x5 grid pane with buttons
    public static GridPane createButtonGrid(ArrayList<String> times, boolean remove) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        boolean flagMinute = true;
        boolean flagHour = false;
        int currentHour = 9;
        int currentMinute = 0;
        // Add buttons to each cell using a for loop
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                // Calculate the time
                if (flagMinute) {
                    currentMinute = 0;
                    flagMinute = false;
                } else {
                    currentMinute = 30;
                    flagMinute = true;
                }
                String formattedTime = String.format("%02d:%02d", currentHour, currentMinute);
                if (formattedTime.equals("18:30")) {
                    Button button = new Button("Submit");
                    button.setStyle("-fx-background-color: #0022ff; -fx-text-fill:white");
                    Client.submitScheduleHandler handler = new Client.submitScheduleHandler();
                    button.setOnAction(handler);
                    gridPane.add(button, col, row);
                    continue;
                }
                if (flagHour) {
                    currentHour++;
                    flagHour = false;
                } else {
                    flagHour = true;
                }
                // Create a button with the time as text

                Button button = new Button(formattedTime);

                if (remove) {
                    button.setUserData(new buttonData(!times.contains(formattedTime)));
                } else {
                    button.setUserData(new buttonData(times.contains(formattedTime)));
                }
                //flase means its NOT TAKEN e.g availabl.e, true means its taken

                boolean isTaken = times.contains(formattedTime);
                if (remove) {
                    isTaken = !isTaken;
                }
                if (isTaken) {
                    button.setStyle("-fx-background-color: red; -fx-text-fill: white");
                } else {
                    button.setStyle("-fx-background-color: green; -fx-text-fill: white");
                }
                Client.buttonScheduleHandler handler1 = new Client.buttonScheduleHandler();
                button.setOnAction(handler1);
                gridPane.add(button, col, row);
            }
        }
        return gridPane;
    }
}



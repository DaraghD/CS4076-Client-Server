package dddq.client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import dddq.client.Client.buttonScheduleHandler;

public class ScheduleStage {
    public void createSchedule(){


    }


    public void start(Stage primaryStage) {
        primaryStage.setTitle("Button Grid Example");

        // Create a 4x5 grid pane with buttons
        //GridPane gridPane = createButtonGrid();

        // Display the grid pane in the primary stage
        //Scene scene = new Scene(gridPane, 400, 300);
        //primaryStage.setScene(scene);
        //primaryStage.show();
    }


    public void buildSchedule(Schedule schedule){
        System.out.println("Building shcedule");


    }

    public static class buttonData{
        private boolean isTaken = false;

        public buttonData(boolean taken){
            isTaken = taken;
        }
        public void click(){
            isTaken = !isTaken;
        }
        public boolean isTaken(){
            return isTaken;
        }


    }
    // Method to create a 4x5 grid pane with buttons
    public static GridPane createButtonGrid(Schedule schedule) {
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
                if(flagMinute){
                    currentMinute = 0;
                    flagMinute = false;}
                else{
                    currentMinute=30;
                    flagMinute = true;
                }
                String formattedTime = String.format("%02d:%02d", currentHour, currentMinute);
                if(flagHour){
                    currentHour++;
                    flagHour=false;
                }
                else{
                    flagHour = true;
                }
                // Create a button with the time as text
                Button button = new Button(formattedTime);


                button.setUserData(new buttonData(schedule.checkTime(formattedTime)));

                //flase means its NOT TAKEN e.g availabl.e, true means its taken

                boolean isTaken = schedule.checkTime(formattedTime);
                if (isTaken){
                    button.setStyle("-fx-background-color: red; -fx-text-fill: white");
                }

                Client.buttonScheduleHandler handler1 = new Client.buttonScheduleHandler();
                button.setOnAction(handler1);
                gridPane.add(button, col, row);
            }


        }
        return gridPane;
    }

}



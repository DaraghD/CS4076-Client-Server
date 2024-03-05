package dddq.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class test extends Application {

    public void createSchedule(){


    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Button Grid Example");

        // Create a 4x5 grid pane with buttons
        GridPane gridPane = createButtonGrid();

        // Display the grid pane in the primary stage
        Scene scene = new Scene(gridPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create a 4x5 grid pane with buttons
    private GridPane createButtonGrid() {
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
                // Add the button to the grid
                gridPane.add(button, col, row);
            }


        }
        return gridPane;
    }

}

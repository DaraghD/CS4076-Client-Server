package dddq.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Button Experiment 1");
        DatePicker datePicker = new DatePicker();
        HBox hbox = new HBox(datePicker);
        Scene scene = new Scene(hbox, 200, 100);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

//On event handling --> LocalDate value = datePicker.getValue();

    public static void main(String[] args) {
        launch();
    }

}
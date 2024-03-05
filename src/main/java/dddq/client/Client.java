package dddq.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.collections.*;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class Client extends Application {
    static InetAddress host;


    private Label choiceBox;
    static final int PORT = 1234;
    Label label = new Label("Pick a date from the Calendar");
    TextField textField = new TextField("");
    Button sendButton = new Button("Send");
    DatePicker datePicker = new DatePicker();
    Button stopButton = new Button("Stop");
    Pane optionPane = new Pane();
    Pane datePane = new Pane();
    String options[] = {"DISPLAY", "ADD", "REMOVE"};
    // JAVAFX ALERT DIALOG FOR EXCCEPTION HANDLIN
    // (ADD) LM051-2022 [2022-03-04] 10:00 Room1 Test
    //DISPLAY - ADD - REMOVE - STOP - DEBUG(PRINTALL)
    // Ask abdul do we make a preset list of classes to choose from or type freestyle - clash testing
    String OPTION;
    LocalDate DATE; // maybe change this to dateTime class object ?
    String CLASS;

    @Override
    public void start(Stage stage) {

        ChoiceBox optionBox = new ChoiceBox(FXCollections.observableArrayList(options));
        // OptionBoxHandler handler = new OptionBoxHandler();
        //handler.optionBox = optionBox;

        optionBox.setPrefWidth(180);
        optionBox.setPrefHeight(30);

        // if the item of the list is changed
        optionBox.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
            OPTION = options[new_value.intValue()];
            System.out.println(OPTION);
        });
        optionPane.getChildren().add(optionBox);

        //send response - OPTION , DATE , CLASS -
        datePane.getChildren().add(datePicker);
        datePicker.setOnAction(e -> {
            DATE = datePicker.getValue();
            System.out.println(DATE);

        });


        stopButton.setOnAction(actionEvent -> {
            Socket link;
            try {
                link = new Socket(host, PORT);
                PrintWriter out = new PrintWriter(link.getOutputStream(), true);
                out.println("STOP");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        sendButton.setOnAction(t -> {
            try {
                host = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                System.out.println("Host ID not found!");
                System.exit(1);
            }
            Socket link = null;
            try {
                link = new Socket(host, PORT);
                //link = new Socket( "192.168.0.59", PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                String message = null;
                String response = null;

                System.out.println("Enter message to be sent to server: ");
                message = textField.getText().toString();
                out.println(message);
                response = in.readLine();
                label.setText(response);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("\n* Closing connection... *");
                    link.close();                //Step 4.
                } catch (IOException e) {
                    System.out.println("Unable to disconnect/close!");
                    System.exit(1);
                }
            }
        });
        HBox buttonBox = new HBox(sendButton, stopButton);
        HBox.setHgrow(sendButton, Priority.ALWAYS);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(50);




        gridPane.add(optionPane, 0, 1);
        gridPane.add(textField, 1, 1);
        gridPane.add(datePane, 0, 3);
        gridPane.add(label, 1, 3, 2, 1);
        gridPane.add(buttonBox, 0, 4, 2, 1);


        var scene = new Scene(gridPane, 1000, 1000);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
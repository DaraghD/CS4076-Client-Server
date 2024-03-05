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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

    @Override
    public void init() {
        System.out.println("9999");
    }
    static InetAddress host;

    static {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    private Label choiceBox;
    static final int PORT = 1234;
    Label label = new Label("Pick a date from the Calendar");
    TextField textField = new TextField("");
    Button sendButton = new Button("Send");

    DatePicker datePicker = new DatePicker();

    Pane optionPane = new Pane();
    Pane datePane = new Pane();
    String options[] = {"DISPLAY", "ADD", "REMOVE"};

    Button stopButton = new Button("STOP");
    // JAVAFX ALERT DIALOG FOR EXCCEPTION HANDLIN
    // (ADD) LM051-2022 [2022-03-04] 10:00 Room1 Test
    //DISPLAY - ADD - REMOVE - STOP - DEBUG(PRINTALL)
    // Ask abdul do we make a preset list of classes to choose from or type freestyle - clash testing
    String OPTION;
    LocalDate DATE; // maybe change this to dateTime class object ?
    String CLASS;

    @Override
    public void start(Stage stage) throws IOException {

        stopButton.setStyle("-fx-background-color: #ea2727");
        stopButton.setPrefWidth(119);
        stopButton.setPrefHeight(47);
        stopButton.setLayoutX(453);
        stopButton.setLayoutY(325);

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
            System.out.println("1111");
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
        AnchorPane anchorPane = new AnchorPane();


        // DatePicker
        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutX(386);
        datePicker.setLayoutY(31);

        // ChoiceBox
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setLayoutX(31);
        choiceBox.setLayoutY(31);


        Button sendButton = new Button("Send");
        sendButton.setLayoutX(240);
        sendButton.setLayoutY(152);

        // TextField
        TextField textField = new TextField();
        textField.setLayoutX(194);
        textField.setLayoutY(31);

        anchorPane.getChildren().addAll(stopButton, datePicker, choiceBox, sendButton, textField);


        Scene scene = new Scene(anchorPane, 600, 400);


        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
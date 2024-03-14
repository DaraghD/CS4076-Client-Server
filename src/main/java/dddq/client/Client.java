package dddq.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Application {
    static InetAddress host;

    static {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
    }

    static final int PORT = 1234;
    TextField moduleField = new TextField();
    TextField roomField = new TextField();
    Label roomLabel = new Label("Choose Room");
    String[] options = {"DISPLAY", "ADD", "REMOVE"};
    ChoiceBox optionBox = new ChoiceBox(FXCollections.observableArrayList(options));
    Label actionLabel = new Label("Select Action");
    Button stopButton = new Button("STOP");
    Label moduleLabel = new Label("Choose Module");
    Label dayLabel = new Label("Choose Day");
    Button sendButton = new Button("Send");
    AnchorPane anchorPane = new AnchorPane();
    Button gridButton = new Button("Choose Slots");
    GridPane schedulePane = new GridPane();
    Socket link;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    static ArrayList<String> chosenTimes = new ArrayList<>();
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    ChoiceBox dayBox = new ChoiceBox(FXCollections.observableArrayList(days));
    static Label chosenTimesLabel = new Label("Chosen Times : ");

    @Override
    public void init() {
        //initalising in  / out streams
        try {
            link = new Socket(host, PORT);
            objectOutputStream = new ObjectOutputStream(link.getOutputStream());
            objectInputStream = new ObjectInputStream(link.getInputStream());

        } catch (IOException e) {
            System.out.println(e + "\n\n\n");
            e.printStackTrace();
        }

        //Putting things into { } lets us close it to make the file smaller when viewing.
        //components
        {
            roomLabel.setLayoutX(50);
            roomLabel.setLayoutY(158);
            roomLabel.setPrefHeight(38);
            roomLabel.setPrefWidth(202);
            roomLabel.setVisible(true);
            roomLabel.setMouseTransparent(true);


            roomField.setPrefWidth(119);
            roomField.setPrefHeight(47);
            roomField.setLayoutX(31);
            roomField.setLayoutY(150);



            chosenTimesLabel.setLayoutY(200);
            chosenTimesLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));

            dayBox.setLayoutX(386);
            dayBox.setLayoutY(31);
            dayBox.setPrefHeight(38);
            dayBox.setPrefWidth(202);
            dayBox.setVisible(true);

            optionBox.setLayoutX(31);
            optionBox.setLayoutY(31);
            optionBox.setPrefHeight(38);
            optionBox.setPrefWidth(148);

            sendButton.setLayoutX(240);
            sendButton.setLayoutY(325);
            sendButton.setPrefWidth(119);
            sendButton.setPrefHeight(47);

            gridButton.setLayoutX(31);
            gridButton.setLayoutY(325);
            gridButton.setPrefWidth(119);
            gridButton.setPrefHeight(47);

            moduleField.setLayoutX(194);
            moduleField.setLayoutY(31);
            moduleField.setPrefHeight(38);
            moduleField.setPrefWidth(177);

            stopButton.setStyle("-fx-background-color: #ea2727");
            stopButton.setPrefWidth(119);
            stopButton.setPrefHeight(47);
            stopButton.setLayoutX(453);
            stopButton.setLayoutY(325);

            actionLabel.setLayoutX(37.0);
            actionLabel.setLayoutY(31.0);
            actionLabel.setPrefHeight(38.0);
            actionLabel.setPrefWidth(134.0);
            actionLabel.setMouseTransparent(true);

            moduleLabel.setLayoutX(207.0);
            moduleLabel.setLayoutY(31.0);
            moduleLabel.setPrefHeight(38.0);
            moduleLabel.setPrefWidth(134.0);
            moduleLabel.setMouseTransparent(true);

            dayBox.setLayoutX(404.0);
            dayBox.setLayoutY(31.0);
            dayBox.setPrefHeight(38.0);
            dayBox.setPrefWidth(134.0);

            dayLabel.setLayoutX(404.0);
            dayLabel.setLayoutY(31.0);
            dayLabel.setPrefHeight(38.0);
            dayLabel.setPrefWidth(134.0);
            dayLabel.setMouseTransparent(true);
        }

        //Listeners / event handlers scope
        {

            roomField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    roomLabel.setVisible(false); // Hide the label
                } else {
                    roomLabel.setVisible(true); // Show the label
                }
            });

            moduleField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    moduleLabel.setVisible(false); // Hide the label
                } else {
                    moduleLabel.setVisible(true); // Show the label
                }
            });

            optionBox.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
                actionLabel.setVisible(false);
                // methods here to show different buttons depending on new_value e.g if display -> dont need to show room field or choose time etc.

            });

            //viewing schedules button
            gridButton.setOnAction(actionEvent -> {
                if (dayBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Viewing Schedule Error");
                    alert.setHeaderText(null);
                    alert.setContentText("You must select a day before viewing schedule");
                    alert.showAndWait();
                } else {
                    try {
                        String module = moduleField.getText();
                        chosenTimes = new ArrayList<>(); // Clearing arraylist, if they choose times then reopen schedule- it resets chosen times.
                        Message message = new Message("VIEW");
                        message.setDay(dayBox.getValue().toString());
                        objectOutputStream.writeObject(message);
                        objectOutputStream.flush();
                        System.out.println(dayBox.getValue().toString());

                        Message timesMessage = (Message) objectInputStream.readObject();
                        //schedule should show red for rooms booked at that time, aswell as classes of the same module
                        Stage scheduleStage = new Stage();
                        GridPane scheduleGrid = ScheduleStage.createButtonGrid(timesMessage.getListOfTimes());
                        Scene scheduleScene = new Scene(scheduleGrid, 400, 300);
                        scheduleStage.setScene(scheduleScene);
                        scheduleStage.show();

                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Couldnt send object");
                        e.printStackTrace();
                    }
                }
            });


            stopButton.setOnAction(actionEvent -> {
                try {
                    objectOutputStream.writeObject(new Message("STOP"));

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Stop Alert");
                    alert.setHeaderText(null);
                    Message response = (Message) objectInputStream.readObject();

                    alert.setContentText(response.getOPTION() + " " + response.getCONTENTS());
                    alert.showAndWait();
                    System.exit(1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            sendButton.setOnAction(t -> {
                // check all fields / buttons make sure they are all filled out. Trigger popup and break/ return if not
                try {
                    Message message = new Message(optionBox.getValue().toString());
                    for (String time : chosenTimes) {
                        message.addTime(time);
                    }
                    message.setDay(dayBox.getValue().toString());
                    message.setROOM_NUMBER(roomField.getText());
                    message.setMODULE_NAME(moduleField.getText());

                    objectOutputStream.writeObject(message);
                    Message response = (Message) objectInputStream.readObject();
                    //label.setText(response.getOPTION() + " " + response.getCONTENTS());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(response.getOPTION());
                    alert.setHeaderText(null);
                    alert.setContentText(response.getCONTENTS());
                    alert.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            dayBox.setOnAction(e -> {
                dayLabel.setVisible(false);
            });

            //eventhandler for textbox for module code , how to make it so when they are finishehd typing it updates the variable,
            // maybe just pull the variable e.g textbox.gettext(), when they click send

        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        anchorPane.getChildren().addAll(stopButton, dayBox, optionBox, sendButton, moduleField, moduleLabel, actionLabel, gridButton, chosenTimesLabel, dayLabel, roomField,roomLabel);
        Scene scene = new Scene(anchorPane, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static class submitScheduleHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button) actionEvent.getSource();
            Scene scene = button.getScene();
            Stage stage = (Stage) scene.getWindow();
            chosenTimesLabel.setText("Chosen Times : " + chosenTimes.toString());
            //might need some submit logic here ?>

            stage.close();

        }
    }

    // this needs to be here as it needs to access the chosenTimes arraylist
    public static class buttonScheduleHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button) actionEvent.getSource();
            ScheduleStage.buttonData data = (ScheduleStage.buttonData) button.getUserData();

            if (!data.isTaken()) {
                // change colour of it
                System.out.println("Selected  time : " + button.getText());
                if (chosenTimes.contains(button.getText())) {
                    // popup here
                    System.out.println("Already added time slot ");
                } else {
                    chosenTimes.add(button.getText());
                    button.setStyle("-fx-background-color: orange; fx-text-fill:white;");
                    //orange = selected, red = taken by soemone else
                }

                data.click();
            } else {
                System.out.println("Time slot in use "); // alert popup instead ?
                return;
            }

        }
    }

    // functions to toggle visibility of fields / buttons depending on action selected
    private static void addActionFields() {

    }

    private static void removeActionFields() {

    }

    private static void displayActionFields() {

    }

}
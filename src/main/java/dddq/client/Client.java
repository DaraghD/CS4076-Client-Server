package dddq.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
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
    static TextField ProgrammeField = new TextField();
    static TextField roomField = new TextField();
    static Label roomLabel = new Label("Choose Room");
    static String[] options = {"DISPLAY", "ADD", "REMOVE"};
    static ChoiceBox optionBox = new ChoiceBox(FXCollections.observableArrayList(options));
    static Label actionLabel = new Label("Select Action");
    Button stopButton = new Button("STOP");
    static Label ProgrammeLabel = new Label("Choose Programme");
    static Label dayLabel = new Label("Choose Day");
    Button sendButton = new Button("Send");

    static Button gridButton = new Button("Choose Slots");
    Socket link;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    static ArrayList<String> chosenTimes = new ArrayList<>();
    static String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    static ChoiceBox dayBox = new ChoiceBox(FXCollections.observableArrayList(days));
    static Label chosenTimesLabel = new Label("Chosen Times : ");
    static TextField moduleField = new TextField();
    static Label moduleLabel = new Label("Choose module");


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
            roomLabel.setMouseTransparent(true);

            roomField.setPrefWidth(119);
            roomField.setPrefHeight(47);
            roomField.setLayoutX(31);
            roomField.setLayoutY(150);

            chosenTimesLabel.setLayoutY(200);
            chosenTimesLabel.setFont(Font.font("Arial", 10));

            dayBox.setLayoutX(386);
            dayBox.setLayoutY(31);
            dayBox.setPrefHeight(38);
            dayBox.setPrefWidth(202);
            dayBox.setVisible(true);

            optionBox.setLayoutX(31);
            optionBox.setLayoutY(31);
            optionBox.setPrefHeight(38);
            optionBox.setPrefWidth(148);

            dayBox.setPrefHeight(38);
            dayBox.setPrefWidth(148);

            sendButton.setLayoutX(240);
            sendButton.setLayoutY(325);
            sendButton.setPrefWidth(119);
            sendButton.setPrefHeight(47);

            gridButton.setLayoutX(31);
            gridButton.setLayoutY(325);
            roomLabel.setVisible(true);
            gridButton.setPrefWidth(119);
            gridButton.setPrefHeight(47);

            ProgrammeField.setLayoutX(194);
            ProgrammeField.setLayoutY(31);
            ProgrammeField.setPrefHeight(38);
            ProgrammeField.setPrefWidth(177);

            moduleField.setLayoutX(194);
            moduleField.setLayoutY(75);
            moduleField.setPrefHeight(38);
            moduleField.setPrefWidth(177);

            moduleLabel.setMouseTransparent(true);
            sendButton.setStyle("-fx-background-color: #4abd22");
            stopButton.setStyle("-fx-background-color: #ea2727");
            stopButton.setPrefWidth(119);
            stopButton.setPrefHeight(47);
            stopButton.setLayoutX(453);
            stopButton.setLayoutY(325);

            actionLabel.setMouseTransparent(true);

            ProgrammeLabel.setMouseTransparent(true);

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

            ProgrammeField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    ProgrammeLabel.setVisible(false); // Hide the label
                } else {
                    ProgrammeLabel.setVisible(true); // Show the label
                }
            });

            optionBox.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
                actionLabel.setVisible(false);
                String selectedOption = (String) optionBox.getItems().get(new_value.intValue());
                switch (selectedOption) {
                    case "ADD":
                        addActionFields();
                        break;
                    case "REMOVE":
                        removeActionFields();
                        break;
                    case "DISPLAY":
                        displayActionFields();
                        break;
                    default:
                }
            });

            //viewing schedules button
            gridButton.setOnAction(actionEvent -> {
                if (dayBox.getValue() == null || ProgrammeField.getText().isEmpty() || roomField.getText().isEmpty() || dayBox.getValue().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Viewing Schedule Error");
                    alert.setHeaderText(null);
                    alert.setContentText("You must fill out required fields before viewing schedule");
                    alert.showAndWait();
                } else {
                    try {
                        String Programme = ProgrammeField.getText();
                        chosenTimes = new ArrayList<>(); // Clearing arraylist, if they choose times then reopen schedule- it resets chosen times.
                        Message message = new Message("VIEW");
                        message.setDay(dayBox.getValue().toString());
                        message.setROOM_NUMBER(roomField.getText());
                        System.out.println("ROOM : "+roomField.getText());
                        message.setProgramme_NAME(ProgrammeField.getText());
                        if (optionBox.getValue().equals("REMOVE")) {
                            message.setCONTENTS("r");
                        }else{
                            message.setCONTENTS("v");
                        }
                        objectOutputStream.writeObject(message);
                        objectOutputStream.flush();
                        System.out.println(dayBox.getValue().toString());


                        Message timesMessage = (Message) objectInputStream.readObject();
                        //schedule should show red for rooms booked at that time, aswell as classes of the same Programme
                        Stage scheduleStage = new Stage();

                        boolean remove = false;
                        if (optionBox.getValue().toString().equals("REMOVE")) {
                            remove = true;
                        }
                        String displayText = "Booking schedule for "+Programme + "\nAt room "+roomField.getText() + "\nOn "+dayBox.getValue().toString();
                        GridPane scheduleGrid = ScheduleStage.createButtonGrid(timesMessage.getListOfTimes(), remove,displayText);
                        Scene scheduleScene = new Scene(scheduleGrid, 600, 700);
                        scheduleStage.setScene(scheduleScene);
                        scheduleStage.show();

                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Couldnt send object");
                        e.printStackTrace();
                    }
                }
            });

            stopButton.setOnAction(actionEvent -> {
                System.out.println("Closing");
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
            moduleField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    moduleLabel.setVisible(false); // Hide the label
                } else {
                    moduleLabel.setVisible(true); // Show the label
                }
            });


            sendButton.setOnAction(t -> {

                switch (optionBox.getValue().toString()) {

                    case "ADD":
                        if (ProgrammeField.getText().isEmpty() || roomField.getText().isEmpty() || dayBox.getValue() == null || chosenTimes.isEmpty() || moduleField.getText().isEmpty()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Add Schedule Error");
                            alert.setHeaderText(null);
                            alert.setContentText("You must fill out all fields before adding a schedule");
                            alert.showAndWait();
                            return;
                        }
                        break;
                    case "REMOVE":
                        if (ProgrammeField.getText().isEmpty() || dayBox.getValue() == null || chosenTimes.isEmpty()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Remove Schedule Error");
                            alert.setHeaderText(null);
                            alert.setContentText("You must fill out all fields before removing a schedule");
                            alert.showAndWait();
                            return;
                        }
                        break;
                    case "DISPLAY":
                        if (ProgrammeField.getText().isEmpty()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Display Schedule Error");
                            alert.setHeaderText(null);
                            alert.setContentText("You must fill out all fields before displaying a schedule");
                            alert.showAndWait();
                            return;
                        }
                        break;
                }
                // check all fields / buttons make sure they are all filled out. Trigger popup and break/ return if not
                try {
                    Message message = new Message(optionBox.getValue().toString());
                    for (String time : chosenTimes) {
                        message.addTime(time);
                    }
                    message.setModule(moduleField.getText());
                    if(optionBox.getValue().toString() != "DISPLAY"){message.setDay(dayBox.getValue().toString());}
                    message.setROOM_NUMBER(roomField.getText());
                    message.setProgramme_NAME(ProgrammeField.getText());

                    objectOutputStream.writeObject(message);
                    Message response = (Message) objectInputStream.readObject();

                    if (response.getOPTION().equals("SUCCESS")) {
                        chosenTimes = new ArrayList<>();
                    }
                    chosenTimesLabel.setText("Chosen Times : " + chosenTimes.toString());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(response.getOPTION());

                    alert.setHeaderText(null);
                    alert.setContentText(response.getCONTENTS());
                    alert.showAndWait();
                    refreshLabels();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            dayBox.setOnAction(e -> {
                dayLabel.setVisible(false);
            });
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setMaxSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        gridPane.setMinSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        gridPane.setPrefSize(600.0, 400.0);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        primaryStage.setOnCloseRequest(e-> {
            System.out.println("Closing");
            try {
                objectOutputStream.writeObject(new Message("STOP"));

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Stop Alert");
                alert.setHeaderText(null);
                Message response = (Message) objectInputStream.readObject();

                alert.setContentText(response.getOPTION() + " " + response.getCONTENTS());
                alert.showAndWait();
                System.exit(1);

            } catch (Exception x) {
                x.printStackTrace();
            }
        });

        // Setting up the column constraints
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints(100, 200, Double.MAX_VALUE);
            column.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            gridPane.getColumnConstraints().add(column);
        }

        // Setting up the row constraints
        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints(30, 60, Double.MAX_VALUE);
            row.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            gridPane.getRowConstraints().add(row);
        }

        // Adding components to the grid, COL , ROW
        GridPane.setConstraints(optionBox, 0, 0);
        GridPane.setConstraints(ProgrammeField, 1, 0);
        GridPane.setConstraints(dayBox, 2, 0);
        GridPane.setConstraints(dayLabel, 2, 0);
        GridPane.setConstraints(actionLabel, 0, 0);
        GridPane.setConstraints(moduleField, 1, 1);
        GridPane.setConstraints(gridButton, 0, 2);
        GridPane.setConstraints(sendButton, 2, 1);
        GridPane.setConstraints(stopButton, 2, 2);
        GridPane.setConstraints(roomField, 1, 2);
        GridPane.setConstraints(ProgrammeLabel,1,0);
        GridPane.setConstraints(moduleLabel,1,1);
        GridPane.setConstraints(roomLabel,1,2);
        GridPane.setConstraints(chosenTimesLabel,0,1);
        gridPane.getChildren().addAll(optionBox, ProgrammeField, dayBox, moduleField, gridButton, sendButton, stopButton, roomField, actionLabel,dayLabel,ProgrammeLabel,roomLabel,moduleLabel,chosenTimesLabel);

        GridPane.setHalignment(optionBox, HPos.CENTER);
        GridPane.setValignment(optionBox, VPos.CENTER);
        GridPane.setValignment(actionLabel, VPos.CENTER);

        GridPane.setHalignment(ProgrammeField, HPos.CENTER);
        GridPane.setValignment(ProgrammeField, VPos.CENTER);

        GridPane.setHalignment(dayBox, HPos.CENTER);
        GridPane.setValignment(dayBox, VPos.CENTER);

        GridPane.setHalignment(actionLabel, HPos.CENTER);
        GridPane.setValignment(actionLabel, VPos.CENTER);

        GridPane.setHalignment(dayLabel, HPos.CENTER);
        GridPane.setValignment(dayLabel, VPos.CENTER);

        GridPane.setHalignment(roomLabel, HPos.CENTER);
        GridPane.setValignment(roomLabel, VPos.CENTER);

        GridPane.setHalignment(ProgrammeLabel, HPos.CENTER);
        GridPane.setValignment(ProgrammeLabel, VPos.CENTER);

        GridPane.setHalignment(chosenTimesLabel, HPos.LEFT);
        GridPane.setValignment(chosenTimesLabel, VPos.BOTTOM);

        GridPane.setHalignment(moduleLabel, HPos.CENTER);
        GridPane.setValignment(moduleLabel, VPos.CENTER);

        GridPane.setHalignment(moduleField, HPos.CENTER);
        GridPane.setValignment(moduleField, VPos.CENTER);

        GridPane.setHalignment(gridButton, HPos.CENTER);
        GridPane.setValignment(gridButton, VPos.CENTER);

        GridPane.setHalignment(sendButton, HPos.CENTER);
        GridPane.setValignment(sendButton, VPos.CENTER);

        GridPane.setHalignment(stopButton, HPos.CENTER);
        GridPane.setValignment(stopButton, VPos.CENTER);

        GridPane.setHalignment(roomField, HPos.CENTER);
        GridPane.setValignment(roomField, VPos.CENTER);

        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Application Using Components");
        primaryStage.show();
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

    // this needs to be here as it needs to access the chosenTimes arraylist from this scope
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
        roomField.setVisible(true);
        ProgrammeField.setVisible(true);
        roomLabel.setVisible(true);
        ProgrammeLabel.setVisible(true);
        dayLabel.setVisible(true);
        gridButton.setVisible(true);
        dayBox.setVisible(true);
        chosenTimesLabel.setVisible(true);
        moduleField.setVisible(true);
        moduleLabel.setVisible(true);
    }

    private static void removeActionFields() {
        ProgrammeField.setVisible(true);
        roomField.setVisible(true);
        roomLabel.setVisible(true);
        ProgrammeLabel.setVisible(true);
        dayLabel.setVisible(true);
        gridButton.setVisible(true);
        dayBox.setVisible(true);
        chosenTimesLabel.setVisible(true);
        moduleField.setVisible(true);
        moduleLabel.setVisible(true);
    }

    private static void refreshLabels() {
        ProgrammeField.setText("");
        roomField.setText("");
        dayBox.setValue("");
    }

    private static void displayActionFields() {
        ProgrammeField.setVisible(true);
        roomField.setVisible(false);
        roomLabel.setVisible(false);
        ProgrammeLabel.setVisible(true);
        dayLabel.setVisible(false);
        gridButton.setVisible(false);
        dayBox.setVisible(false);
        chosenTimesLabel.setVisible(false);
        moduleField.setVisible(false);
        moduleLabel.setVisible(false);
    }


}
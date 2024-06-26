package dddq.client;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.concurrent.WorkerStateEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Controller {
    static InetAddress host;

    static {
        try {
            host = InetAddress.getLocalHost();
        } catch (Exception e) {
            System.out.println("host id not found");
            System.exit(1);
        }
    }

    private final int PORT = 4444;
    private static Model model;
    private static View view;
    Socket link;
    private Stage stage;
    ObjectOutputStream out;
    ObjectInputStream in;

    //use model to hide certain elements ?
    // hide labels / buttons bottom of Client.java


    public Controller(Model model, View view, Stage stage) {
        this.model = model;
        this.view = view;
        this.stage = stage;
        init();

    }

    public void init() {
        try {
            link = new Socket(host, PORT);
            out = new ObjectOutputStream(link.getOutputStream());
            in = new ObjectInputStream(link.getInputStream());
        } catch (Exception e) {
            System.out.println("Couldn't initialise in/out/connection to server, closing");
            System.exit(1);
        }
        view.getSendButton().setOnAction(new sendHandler());
        view.getChooseTimesButton().setOnAction(new selectTimesHandler());
        view.getStopButton().setOnAction(new closeHandle());
        stage.setOnCloseRequest(event -> { // identical to closeHandle but cant use because incompatible type
            Message closing = new Message("STOP");
            try {
                out.writeObject(closing);
            } catch (IOException e) {
                System.out.println("Couldn't send message to exit, server probably already closed");
            }
            System.exit(1);
        });

    }

    class closeHandle implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Message closing = new Message("STOP");
            MessageTask closingMessage = new MessageTask(closing, in, out);
            new Thread(closingMessage).start();
            closingMessage.setOnSucceeded(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Closing");
                alert.setHeaderText("Closing client");
                alert.setContentText("Disconnected successfully");
                alert.showAndWait();
                System.exit(1);
            });
        }
    }


    class selectTimesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (model.getDay() == null || model.getProgramme_name() == null || model.getRoom_name() == null || model.getDay().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Viewing Schedule Error");
                alert.setHeaderText(null);
                alert.setContentText("You must fill out required fields before viewing schedule");
                alert.showAndWait();
            } else {
                String Programme = model.getProgramme_name();
                model.setTimes(new ArrayList<>()); // Clearing arraylist, if they choose times then reopen schedule- it resets chosen times.
                Message message = new Message("VIEW");
                message.setDay(model.getDay());
                message.setROOM_NUMBER(model.getRoom_name());
                System.out.println("ROOM : " + model.getRoom_name());
                message.setProgramme_NAME(model.getProgramme_name());
                if (model.getAction().equals("REMOVE")) {
                    message.setCONTENTS("r");
                } else {
                    message.setCONTENTS("v");
                }

                MessageTask viewMessage = new MessageTask(message, in, out);
                new Thread(viewMessage).start();

                viewMessage.setOnSucceeded(event1 -> {
                    Message timesMessage = viewMessage.getValue();

                    //schedule should show red for rooms booked at that time, aswell as classes of the same Programme
                    Stage scheduleStage = new Stage();
                    scheduleStage.setMinHeight(700);
                    scheduleStage.setMinWidth(700);

                    boolean remove = false;
                    if (model.getAction().equals("REMOVE")) {
                        remove = true;
                    }
                    String displayText = "Booking schedule for " + Programme + "\nAt room " + model.getRoom_name() + "\nOn " + model.getDay();
                    GridPane scheduleGrid = ScheduleStage.createButtonGrid(timesMessage.getListOfTimes(), remove, displayText);
                    Scene scheduleScene = new Scene(scheduleGrid, 600, 700);
                    scheduleStage.setScene(scheduleScene);
                    scheduleStage.show();
                });
            }
        }

    }

    static class addTimeHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button) actionEvent.getSource();
            ScheduleStage.buttonData data = (ScheduleStage.buttonData) button.getUserData();

            if (!data.isTaken()) {
                // change colour of it
                System.out.println("Selected  time : " + button.getText());
                if (model.getTimes().contains(button.getText())) {
                    // popup here
                    System.out.println("Already added time slot ");
                } else {
                    model.addTime(button.getText());
                    button.setStyle("-fx-background-color: orange; fx-text-fill:white;");
                    //orange = selected, red = taken by soemone else
                }

                data.click();
            } else {
                System.out.println("Time slot in use "); // alert popup instead ?
            }
        }
    }

    public static class submitScheduleHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Label chosenTimesLabel = view.getChosenTimesLabel();
            Button button = (Button) actionEvent.getSource();
            Scene scene = button.getScene();
            Stage stage = (Stage) scene.getWindow();
            chosenTimesLabel.setText("Chosen Times : " + model.getTimes().toString());
            stage.close();
        }
    }

    class sendHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            String action = model.getAction();
            if (action == null) {
                alert.setTitle("No action chosen");
                alert.setContentText("Please choose an action");
                alert.showAndWait();
                return;
            }
            switch (action) {
                case "ADD":
                    if (model.getProgramme_name().isEmpty() || model.getRoom_name().isEmpty() || model.getDay() == null || model.getTimes().isEmpty() || model.getModule_name().isEmpty()) {
                        alert.setTitle("Add Schedule Error");
                        alert.setContentText("You must fill out all fields before adding a schedule");
                        alert.showAndWait();
                        return;
                    }
                    break;
                case "REMOVE":
                    if (model.getProgramme_name().isEmpty() || model.getDay() == null || model.getTimes().isEmpty()) {
                        alert.setTitle("Remove Schedule Error");
                        alert.setContentText("You must fill out all fields before removing a schedule");
                        alert.showAndWait();
                        return;
                    }
                    break;
                case "DISPLAY":
                    if (model.getProgramme_name().isEmpty()) {
                        alert.setTitle("Display Schedule Error");
                        alert.setContentText("You must fill out all fields before displaying a schedule");
                        alert.showAndWait();
                        return;
                    }
                    break;
                case "EARLY":
                    if (model.getProgramme_name().isEmpty()) {
                        alert.setTitle("Early Lecture Schedule Error");
                        alert.setContentText("You must fill out all fields before shifting lectures to earliest time");
                        alert.showAndWait();
                        return;
                    }
                    break;
            }

            Message request = new Message(action);
            for (String time : model.getTimes()) {
                request.addTime(time);
            }
            request.setModule(model.getModule_name());
            if (!action.equals("DISPLAY")) {
                request.setDay(model.getDay());
            }
            request.setROOM_NUMBER(model.getRoom_name());
            request.setProgramme_NAME(model.getProgramme_name());

            // this is where javafx.concurrent is used for spec, I/O bound operations are taken into a separate thread.
            MessageTask messageTask = new MessageTask(request, in, out);
            new Thread(messageTask).start();
            messageTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    Message response = messageTask.getValue();

                    if (response != null) {
                        if (response.getOPTION().equals("SUCCESS")) {
                            model.setTimes(new ArrayList<>());
                        }

                        alert.setTitle(response.getOPTION());
                        alert.setHeaderText(null);
                        alert.setContentText(response.getCONTENTS());
                    } else {
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to receive response from server.");
                    }
                    alert.showAndWait();
                    if (response.getProgrammeObject() != null) {
                        Stage timetableStage = new Stage();
                        GridPane grid = TimetableGUI.makeTimetable(response.getProgrammeObject());
                        Scene timetableScene = new Scene(grid, 1000, 1000);
                        timetableStage.setMinHeight(1000);
                        timetableStage.setMinWidth(1000);
                        timetableStage.setScene(timetableScene);
                        timetableStage.show();

                    }
                }
            });

        }
    }
}

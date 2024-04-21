package dddq.client;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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

    private final int PORT = 1234;
    private static Model model; //this might be a problem later - static, only one controller class gets made so probably not
    private View view;
    Socket link;

    ObjectOutputStream out;
    ObjectInputStream in;

    //use model to hide certain elements ?
    // hide labels / buttons bottom of Client.java


    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        init();

    }

    public void init() {
        try {
            link = new Socket(host, PORT);
            out = new ObjectOutputStream(link.getOutputStream());
            in = new ObjectInputStream(link.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.getSendButton().setOnAction(new SendHandler());
        view.getChooseTimesButton().setOnAction(new selectTimesHandler());
        //fix select times handler, times getting reset?
        // selected times not showing or something ? 

    }

    class selectTimesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (model.getDay() == null || model.getProgramme_name().isEmpty() || model.getRoom_name().isEmpty() || model.getDay().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Viewing Schedule Error");
                alert.setHeaderText(null);
                alert.setContentText("You must fill out required fields before viewing schedule");
                alert.showAndWait();
            } else {
                try {
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
                    System.out.println("writing message");
                    out.writeObject(message);
                    System.out.println("wrote message");
                    out.flush();


                    Message timesMessage = (Message) in.readObject();
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

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Couldnt send object");
                    e.printStackTrace();
                }
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

    class SendHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String action = model.getAction();
            switch (action) {
                case "ADD":
                    if (model.getProgramme_name().isEmpty() || model.getRoom_name().isEmpty() || model.getDay() == null || model.getTimes().isEmpty() || model.getModule_name().isEmpty()) {
                        alert.setTitle("Add Schedule Error");
                        alert.setHeaderText(null);
                        alert.setContentText("You must fill out all fields before adding a schedule");
                        alert.showAndWait();
                        return;
                    }
                    break;
                case "REMOVE":
                    if (model.getProgramme_name().isEmpty() || model.getDay() == null || model.getTimes().isEmpty()) {
                        alert.setTitle("Remove Schedule Error");
                        alert.setHeaderText(null);
                        alert.setContentText("You must fill out all fields before removing a schedule");
                        alert.showAndWait();
                        return;
                    }
                    break;
                case "DISPLAY":
                    if (model.getProgramme_name().isEmpty()) {
                        alert.setTitle("Display Schedule Error");
                        alert.setHeaderText(null);
                        alert.setContentText("You must fill out all fields before displaying a schedule");
                        alert.showAndWait();
                        return;
                    }
                    break;
                case "EARLY":
                    //do
                    break;
            }

            try {
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

                out.writeObject(request);
                Message response = (Message) in.readObject();

                if (response.getOPTION().equals("SUCCESS")) {
                    model.setTimes(new ArrayList<>());
                }

                alert.setTitle(response.getOPTION());

                alert.setHeaderText(null);
                alert.setContentText(response.getCONTENTS());
                alert.showAndWait();
                //refreshLabels(); bring this over later ?

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

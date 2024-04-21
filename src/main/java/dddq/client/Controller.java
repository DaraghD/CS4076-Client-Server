package dddq.client;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;
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
    private Model model;
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
    }


    public class SendHandler implements EventHandler<ActionEvent> {
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

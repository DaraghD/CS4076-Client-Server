package dddq.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
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
    Label label = new Label("Pick a date from the Calendar");
    TextField textField = new TextField();
    String options[] = {"DISPLAY", "ADD", "REMOVE"};
    ChoiceBox optionBox = new ChoiceBox(FXCollections.observableArrayList(options));
    Label actionLabel = new Label("Select Action");
    Button stopButton = new Button("STOP");
    Label moduleLabel = new Label("Choose Module");
    Label dateLabel = new Label("Choose Date");
    DatePicker datePicker = new DatePicker();
    Button sendButton = new Button("Send");
    AnchorPane anchorPane = new AnchorPane();
    Button gridButton = new Button("Choose Slots");
    GridPane schedulePane = new GridPane();
    Socket link;
    PrintWriter out;
    BufferedReader in;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    String OPTION;
    LocalDate DATE = null;
    static ArrayList<String> listOfTimes = new ArrayList<>();
    String CLASS;
    ArrayList<String> TimeSlots = new ArrayList<>();

    // JAVAFX ALERT DIALOG FOR EXCCEPTION HANDLIN
    // (ADD) LM051-2022 [2022-03-04] 10:00 Room1 Test
    //DISPLAY - ADD - REMOVE - STOP - DEBUG(PRINTALL)


    @Override
    public void init() {
        //initalising in  / out streams
        try {
            link = new Socket(host, PORT);
            out = new PrintWriter(link.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            objectOutputStream = new ObjectOutputStream(link.getOutputStream());
            objectInputStream = new ObjectInputStream(link.getInputStream());

        } catch (IOException e) {
            System.out.println(e + "\n\n\n");
            e.printStackTrace();
        }

        //Putting things into { } lets us close it to make the file smaller when viewing.
        //components
        {
            datePicker.setLayoutX(386);
            datePicker.setLayoutY(31);
            datePicker.setPrefHeight(38);
            datePicker.setPrefWidth(202);

            optionBox.setLayoutX(31);
            optionBox.setLayoutY(31);
            optionBox.setPrefHeight(38);
            optionBox.setPrefWidth(148);

            sendButton.setLayoutX(240);
            sendButton.setLayoutY(152);
            sendButton.setPrefWidth(177);
            sendButton.setPrefHeight(38);

            textField.setLayoutX(194);
            textField.setLayoutY(31);
            textField.setPrefHeight(38);
            textField.setPrefWidth(177);

            stopButton.setStyle("-fx-background-color: #ea2727");
            stopButton.setPrefWidth(119);
            stopButton.setPrefHeight(47);
            stopButton.setLayoutX(453);
            stopButton.setLayoutY(325);

            optionBox.setPrefWidth(180);
            optionBox.setPrefHeight(30);
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

            dateLabel.setLayoutX(404.0);
            dateLabel.setLayoutY(31.0);
            dateLabel.setPrefHeight(38.0);
            dateLabel.setPrefWidth(134.0);
            dateLabel.setMouseTransparent(true);

        }

        //Listenrs / event handlers scope
        {
            optionBox.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
                OPTION = options[new_value.intValue()];
                System.out.println(OPTION);
                actionLabel.setVisible(false);
            });

            //viewing schedules button
            gridButton.setOnAction(actionEvent -> {
                if (DATE == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert");
                    //throw exception with text -> move text into alert - exception incorrection action !! ? ?
                    alert.setHeaderText(null);
                    alert.setContentText("You must select a date before viewing schedule");
                    alert.showAndWait();
                    //pop up stage.
                    // show schedule with date
                    // return time slots that they choose.
                }
                //make handler in this class so it can access these variables, but give the handler to the button in other class
                else { //
                    try {
                        Message message = new Message("VIEW");
                        message.setDate(DATE);
                        //maybe do a builder for messages ?
                        //send view date - > server replies with schedule object, create grid view wit hthis

                        objectOutputStream.writeObject(message);
                        objectOutputStream.flush();
                        Schedule schedule = (Schedule) objectInputStream.readObject();




                        System.out.println(schedule);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Couldnt send object");
                        e.printStackTrace();
                    }


                }});


            stopButton.setOnAction(actionEvent -> {
                out.println("STOP");
                System.exit(1);
            });

            sendButton.setOnAction(t -> {
                System.out.println("1111");

                Socket link = null;
                try {
                    link = new Socket(host, PORT);
                    //link = new Socket( "192.168.0.59", PORT);

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

            datePicker.setOnAction(e -> {
                DATE = datePicker.getValue();
                System.out.println(DATE.getClass());
                //LocalDate
                dateLabel.setVisible(false);
            });
            
            //eventhandler for textbox for module code , how to make it so when they are finishehd typing it updates the variable,
            // maybe just pull the variable e.g textbox.gettext(), when they click send .


        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        anchorPane.getChildren().addAll(stopButton, datePicker, optionBox, sendButton, textField, dateLabel, moduleLabel, actionLabel, gridButton);

        Scene scene = new Scene(anchorPane, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    public static class buttonScheduleHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button) actionEvent.getSource();
            ScheduleStage.buttonData data = (ScheduleStage.buttonData) button.getUserData();
            if (data.isAVAILABLE()) {
                // change colour of it
                System.out.println("Selected  time : " + button.getText());
                if(listOfTimes.contains(button.getText())){
                    // popup here
                    System.out.println("Already added time slot ");
                }
                else{
                    listOfTimes.add(button.getText());
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

}
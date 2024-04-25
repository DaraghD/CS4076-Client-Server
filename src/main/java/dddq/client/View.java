package dddq.client;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.control.Button;

public class View {
    // contains only graphical contents ?
    // model contains actual data
    private Model model;
    private Stage stage;
    private GridPane gridPane = new GridPane();

    //TODO: Seperate view for schedule selector / maybe some others ?
    private TextField ProgrammeField = new TextField();
    String[] options = {"DISPLAY","ADD","REMOVE","EARLY LECTURE"};
    private ChoiceBox<String> optionBox = new ChoiceBox<>(FXCollections.observableArrayList(options));
    static String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private ChoiceBox<String> dayBox = new ChoiceBox<String>(FXCollections.observableArrayList(days));
    private Label dayLabel = new Label("Choose day");
    private Label roomLabel = new Label("Choose Room");
    private Label ProgrammeLabel = new Label("Choose Programme");
    private Label moduleLabel = new Label("Choose Module");
    private Label actionLabel = new Label("Choose Action");
    private Label chosenTimesLabel = new Label("Chosen Times");
    private TextField roomField = new TextField();
    private TextField moduleField = new TextField();
    private Button chooseTimesButton = new Button("Choose times");
    private Button sendButton = new Button("Send");
    private Button stopButton = new Button("Stop");


    public View(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
        bind_model();
        init();
    }

    public Scene getScene() {
        return new Scene(gridPane);
    }

    static private void log(String x) {
        System.out.println(x);
    }

    public Label getChosenTimesLabel() {
        return chosenTimesLabel;
    }

    public Button getStopButton() {
        return stopButton;
    }

    private void bind_model() {
        //bind variables in model to gui controls
        roomField.textProperty().bindBidirectional(model.roomNameProperty());
        ProgrammeField.textProperty().bindBidirectional(model.programmeNameProperty());
        moduleField.textProperty().bindBidirectional(model.moduleNameProperty());
        dayBox.valueProperty().bindBidirectional(model.dayProperty());
        optionBox.valueProperty().bindBidirectional(model.actionProperty());
    }

    public Button getSendButton() {
        return sendButton;
    }

    public Button getChooseTimesButton() {
        return chooseTimesButton;
    }


    public void init() {
        //FXML was being weird, so we are doing ui in code.
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

        chooseTimesButton.setLayoutX(31);
        chooseTimesButton.setLayoutY(325);
        roomLabel.setVisible(true);
        chooseTimesButton.setPrefWidth(119);
        chooseTimesButton.setPrefHeight(47);

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

        gridPane.getChildren().addAll(optionBox, ProgrammeField, dayBox, moduleField, chooseTimesButton, sendButton, stopButton, roomField, actionLabel, dayLabel, ProgrammeLabel, roomLabel, moduleLabel, chosenTimesLabel);


        gridPane.setMaxSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        gridPane.setMinSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        gridPane.setPrefSize(600.0, 400.0);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        // Setting up the column constraints
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints(100, 200, Double.MAX_VALUE);
            column.setHgrow(Priority.SOMETIMES);
            gridPane.getColumnConstraints().add(column);
        }

        // Setting up the row constraints
        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints(30, 60, Double.MAX_VALUE);
            row.setVgrow(Priority.SOMETIMES);
            gridPane.getRowConstraints().add(row);
        }

        // Adding components to the grid, COL , ROW
        GridPane.setConstraints(optionBox, 0, 0);
        GridPane.setConstraints(ProgrammeField, 1, 0);
        GridPane.setConstraints(dayBox, 2, 0);
        GridPane.setConstraints(dayLabel, 2, 0);
        GridPane.setConstraints(actionLabel, 0, 0);
        GridPane.setConstraints(moduleField, 1, 1);
        GridPane.setConstraints(chooseTimesButton, 0, 2);
        GridPane.setConstraints(sendButton, 2, 1);
        GridPane.setConstraints(stopButton, 2, 2);
        GridPane.setConstraints(roomField, 1, 2);
        GridPane.setConstraints(ProgrammeLabel, 1, 0);
        GridPane.setConstraints(moduleLabel, 1, 1);
        GridPane.setConstraints(roomLabel, 1, 2);
        GridPane.setConstraints(chosenTimesLabel, 0, 1);

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

        GridPane.setHalignment(chooseTimesButton, HPos.CENTER);
        GridPane.setValignment(chooseTimesButton, VPos.CENTER);

        GridPane.setHalignment(sendButton, HPos.CENTER);
        GridPane.setValignment(sendButton, VPos.CENTER);

        GridPane.setHalignment(stopButton, HPos.CENTER);
        GridPane.setValignment(stopButton, VPos.CENTER);

        GridPane.setHalignment(roomField, HPos.CENTER);
        GridPane.setValignment(roomField, VPos.CENTER);


        ProgrammeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                ProgrammeLabel.setVisible(false); // Hide the label
            } else {
                ProgrammeLabel.setVisible(true); // Show the label
            }
        });

        roomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                roomLabel.setVisible(false); // Hide the label
            } else {
                roomLabel.setVisible(true); // Show the label
            }
        });



        ProgrammeField.textProperty().addListener((observable ,oldValue, newValue)->{
        ProgrammeLabel.setVisible(false);
        });



        moduleField.textProperty().addListener((observable ,oldValue, newValue)->{
            moduleLabel.setVisible(false);
        });


        optionBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                actionLabel.setVisible(true);
            } else {
                actionLabel.setVisible(false);
            }
            if(dayBox.getValue()!=null) {
                if(dayBox.getValue().isEmpty()){
                    dayLabel.setVisible(true);
                }

            else {dayLabel.setVisible(false);
            }}

            if(newValue.equals("DISPLAY")){
                roomField.setVisible(false);
                roomLabel.setVisible(false);

            }
            if(newValue.equals("ADD")){
                roomField.setVisible(true);
                chooseTimesButton.setVisible(true);
                chosenTimesLabel.setVisible(true);
                dayBox.setVisible(true);

            }
            if(newValue.equals("REMOVE")){
                roomField.setVisible(true);
                chooseTimesButton.setVisible(true);
                chosenTimesLabel.setVisible(true);
                dayBox.setVisible(true);

            }
            if(newValue.equals("EARLY LECTURE")){
                roomField.setVisible(false);
                roomLabel.setVisible(false);
                chooseTimesButton.setVisible(false);
                chosenTimesLabel.setVisible(false);
                dayBox.setVisible(false);
                dayLabel.setVisible(false);

            }
        });

        dayBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                dayLabel.setVisible(true);
            } else {
                dayLabel.setVisible(false);
            }
        });


    }

}

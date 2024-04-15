package dddq.client;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.*;


public class View {
    //contains only graphical contents ?
    // model contains actual data
    private Model model;
    private Stage stage;

    //

    //TODO: Seperate view for schedule selector / maybe some others ?
    private TextField room_field = new TextField();
    private TextField programme_field = new TextField();
    private TextField module_field = new TextField();
    private TextField day_field = new TextField();
    private TextField time_field = new TextField();
    private TextField action_field = new TextField();


    private Button sendButton = new Button("Send");


    public View(Stage stage,Model model){
        this.stage = stage;
        this.model = model;
        bind_model();
    }



    private void bind_model(){
        //bind variables in model to gui controls
        room_field.textProperty().bindBidirectional(model.roomNameProperty());
        programme_field.textProperty().bindBidirectional(model.programmeNameProperty());
        module_field.textProperty().bindBidirectional(model.moduleNameProperty());
        day_field.textProperty().bindBidirectional(model.dayProperty());
        //time_field.textProperty().bindBidirectional(model.timeProperty());, not sure what to do here? Bind text field to values of the array
        action_field.textProperty().bindBidirectional(model.actionProperty());



    }


    public Button getSendButton() {
        return sendButton;
    }
}

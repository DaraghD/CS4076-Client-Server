package dddq.client;


public class Controller {
    private Model model;
    private View view;


    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        init();

    }

    public void init() {
        view.getSendButton().setOnAction(new SendHandler());
    }





}

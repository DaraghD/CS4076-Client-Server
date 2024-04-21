package dddq.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Client2 extends Application{
    private final int PORT = 1234;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Model model = new Model();
        View view = new View(primaryStage, model);
        //i think model or view will need controller later?
        // if not, then idk where it goes or just storage for buttons method
        Controller controller = new Controller(model, view);

        primaryStage.setScene(view.getScene());
        primaryStage.setTitle("Client MVC");
        primaryStage.show();
    }
}
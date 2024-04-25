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
        Model model = new Model(true);
        View view = new View(primaryStage, model);
        model.setView(view);
        Controller controller = new Controller(model, view,primaryStage);

        primaryStage.setScene(view.getScene());
        primaryStage.setTitle("CS4076 Client");
        primaryStage.show();
    }
}

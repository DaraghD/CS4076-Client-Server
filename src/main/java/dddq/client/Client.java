package dddq.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Client extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        Model model = new Model(false);
        View view = new View(primaryStage, model);
        model.setView(view);
        Controller controller = new Controller(model, view,primaryStage);
        controller.init();

        primaryStage.setScene(view.getScene());
        primaryStage.setTitle("CS4076 Client");
        primaryStage.show();
    }
}

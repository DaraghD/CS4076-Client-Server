module dddq.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens dddq.client to javafx.fxml;
    exports dddq.client;
    exports dddq.server;
    opens dddq.server to javafx.fxml;
}
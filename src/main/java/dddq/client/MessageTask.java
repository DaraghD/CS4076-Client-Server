package dddq.client;

import javafx.concurrent.Task;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageTask extends Task<Message> {
    // makes main gui not freeze when doing other stuff, like communicating with server
    // can make artificial delay with thread.sleep on server to show this, since server is hosted on same pc as client its pretty much instant.
    Message message;
    ObjectInputStream in;
    ObjectOutputStream out;

    MessageTask(Message message, ObjectInputStream in, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
        this.in = in;
    }

    @Override
    protected Message call() throws Exception {
        out.writeObject(message);
        Message response = (Message) in.readObject();
        out.flush();
        return response;
    }

}

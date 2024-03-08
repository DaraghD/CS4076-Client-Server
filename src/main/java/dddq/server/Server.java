package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.Schedule;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.HashMap;

public class Server {
    private static final int PORT = 1234;
    static Socket link;
    static HashMap<LocalDate, Schedule> timeTables = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server is running and waiting for connections...");
                // Wait for a client to connect
                link = serverSocket.accept();
            }
            catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println("Client connected: " + link.getInetAddress());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(link.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(link.getInputStream());

            while (true) {
                // Process messages from the client
                processClientMessage(objectInputStream, objectOutputStream);
            }

        } catch (IOException | ClassNotFoundException | IncorrectActionException e) {
            e.printStackTrace();
            // send message back to client ? incorrect action >
        } finally {
            // Close the connection with the client
            System.out.println("Closing connection with client: " + link.getInetAddress());
            link.close();
        }
    }

    private static void processClientMessage(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException, IncorrectActionException, ClassNotFoundException {
        Message message1 = (Message) objectInputStream.readObject();
        System.out.println("Received message: " + message1);

        switch (message1.getOPTION()) {
            case "ADD":

                // take a list of times, Module , date
                break;
            case "VIEW":
                Schedule schedule = timeTables.get(message1.getDate());
            {
                //if schedule not there, create new empty schedule and add it to timetable
                schedule = new Schedule();
            }
            System.out.println("VIEWING SCHEDULE FOR : " + message1.getDate());

            timeTables.put(message1.getDate(), schedule);
            // if()
            objectOutputStream.writeObject(schedule);
            // objectOutputStream.writeObject(classSchedules.get(message.getCONTENTS()));
            break;
            case "REMOVE":
                break;
            case "DEBUG":
                break;
            case "DISPLAY":
                break;
            default:
                throw new IncorrectActionException("NOT A VALID COMMAND");
        }
    }
}


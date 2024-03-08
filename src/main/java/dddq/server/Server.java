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
    static HashMap<LocalDate, Schedule> timeTables = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                Socket link = serverSocket.accept();
                System.out.println("Client connected: " + link.getInetAddress());

                //Create new thread to handle the client
                Thread clientHandler = new Thread(() -> {
                    try (
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(link.getOutputStream());
                            ObjectInputStream objectInputStream = new ObjectInputStream(link.getInputStream())
                    ) {
                        processClientMessages(objectInputStream, objectOutputStream);
                    } catch (IOException | ClassNotFoundException | IncorrectActionException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Closing connection with client: " + link.getInetAddress());
                        try {
                            link.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //Start  thread to handle the client
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processClientMessages(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream)
            throws IOException, ClassNotFoundException, IncorrectActionException {
        while (true) {
            Message message = (Message) objectInputStream.readObject();
            System.out.println("Received message: " + message);
            processClientMessage(message, objectOutputStream);
        }
    }

    private static void processClientMessage(Message message, ObjectOutputStream objectOutputStream) throws IOException, IncorrectActionException {
        switch (message.getOPTION()) {
            case "ADD":

                // take a list of times, Module , date
                break;
            case "VIEW":
                Schedule schedule = timeTables.get(message.getDate());
                {
                    //if schedule not there, create new empty schedule and add it to timetable
                    schedule = new Schedule();
                }
                System.out.println("VIEWING SCHEDULE FOR : " + message.getDate());

                timeTables.put(message.getDate(),schedule);
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


package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.ScheduleDay;
import dddq.client.ScheduleWeek;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static final int PORT = 1234;
    static Socket link;
    static HashMap<String, ScheduleDay> timeTables = new HashMap<>(); // class : schedule 5 days
    static HashMap<String, ScheduleWeek> roomTimetables = new HashMap<>(); // room : schedule 5 days
    //load from disk, persistant data

    public static void main(String[] args) throws IOException {
        while (true) {
            try {
                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                    System.out.println("Server is running and waiting for connections...");
                    // Wait for a client to connect
                    link = serverSocket.accept();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                System.out.println("Client connected: " + link.getInetAddress());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(link.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(link.getInputStream());

                // Process messages from the client
                while(!link.isClosed()){
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
    }

    private static void processClientMessage(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException, IncorrectActionException, ClassNotFoundException {
        Message message1 = (Message) objectInputStream.readObject();
        System.out.println("Received message: " + message1 + "\n \n" + message1.getListOfTimes().toString() + "\n" + message1.getOPTION() + "\n" + message1.getCONTENTS() + "\n" + message1.getDay());

        //might want to build response here.


        switch (message1.getOPTION()) {
            // add a class to a room at a time  // take a list of times, Module ,
            case "ADD":
                ScheduleDay scheduleDay1 = timeTables.get(message1.getDay());
                if (scheduleDay1 == null) {
                    scheduleDay1 = new ScheduleDay();
                }
                for (String time : message1.getListOfTimes()) {
                    if (!scheduleDay1.checkTime(time)) {
                        Message RESPONSE = new Message("ERROR");
                        RESPONSE.setCONTENTS("TIME ALREADY TAKEN : " + time);
                        objectOutputStream.writeObject(RESPONSE);
                        break;
                    }
                }
                //we know they are all free now, since we made it here.
                for (String time : message1.getListOfTimes()) {
                    scheduleDay1.bookTime(time);
                }

                Message RESPONSE = new Message("SUCCESS");
                RESPONSE.setCONTENTS("BOOKED TIMES +  " + message1.getListOfTimes().toString());

                break;
            case "VIEW": // viewing schedule for a day
                ScheduleDay scheduleDay = timeTables.get(message1.getDay());
                if (scheduleDay == null) {
                    //if schedule not there, create new empty schedule and add it to timetable
                    scheduleDay = new ScheduleDay();
                }
                System.out.println("VIEWING SCHEDULE FOR : " + message1.getDay());
                scheduleDay.bookTime("11:00");

                timeTables.put(message1.getDay(), scheduleDay);
                objectOutputStream.writeObject(scheduleDay);
                break;
            case "REMOVE":
                break;
            case "DEBUG":
                break;
            case "DISPLAY":
                break;
            case "STOP":
                System.out.println("Stopping server as requested by client");
                Message stopResponse = new Message("TERMINATE");
                stopResponse.setCONTENTS("Closing connection to client as requested");
                objectOutputStream.writeObject(stopResponse);
                link.close();
                break;
            default:
                throw new IncorrectActionException("NOT A VALID COMMAND");
        }
    }

    public boolean checkRoom(String time, String room, String day) {
        return true;
        // make a check room function -> need to go through every schedule ? -> use roomTimetables
    }
}


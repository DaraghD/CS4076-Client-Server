package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.Schedule;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;
    //static Map<String, String> classSchedules = new HashMap<>();
    //change this to schedule object

    //hashmap of dates - schedules for that date
    static HashMap<LocalDate, Schedule> timeTables = new HashMap<>();
    static ObjectInputStream objectInputStream;
    static ObjectOutputStream objectOutputStream;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for connections...");

            // To add or remove a class, the client selects a date, time, and room number
            //and then provides the name of the class to schedule.
            while (true) {

                Socket link = serverSocket.accept();
                //BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                //PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                objectInputStream = new ObjectInputStream(link.getInputStream());
                objectOutputStream = new ObjectOutputStream(link.getOutputStream());

                //String message = in.readLine();
                //String x = processClientMessage(message);
                Message message = (Message) objectInputStream.readObject();
                System.out.println(message);
                System.out.println(message.getClass());
                processClientMessage(message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException | IncorrectActionException e) {
           System.out.println(e);
        } finally {
            System.out.println("\n Closing Connection... ");
        }
    }

    private static void processClientMessage(Message message) throws IOException, IncorrectActionException {
        switch (message.getOPTION()) {
            case "ADD":

                // take a list of times, Module , date
                break;
            case "VIEW":
                Schedule schedule = timeTables.get(message.getDate());
                if(schedule == null){
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


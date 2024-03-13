package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.ScheduleDay;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private static final int PORT = 1234;
    static Socket link;
    static HashMap<String, ScheduleDay> timeTables = new HashMap<>(); // remove this
    static HashMap<String, ArrayList<ScheduleDay>> moduleTimetable = new HashMap<>(); // DAY : list of  MODULE SCHEDULEs , maybe hashmap of room name to day instaad of list
    static HashMap<String, ArrayList<ScheduleDay>> roomTimetable = new HashMap<>(); // DAY : list of room schedules on that day
    static String[] dayOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};


    public static void init() throws IncorrectActionException {
        for(String day: dayOfTheWeek){
            moduleTimetable.put(day, new ArrayList<ScheduleDay>());
            roomTimetable.put(day, new ArrayList<ScheduleDay>());
        }

        // load data from disk in this function


        //testing
        {
            ScheduleDay test = new ScheduleDay("test");
            test.bookTime("09:00");
            roomTimetable.get("Monday").add(test);
        }
    }
    public static void main(String[] args) throws IOException, IncorrectActionException {
        init();
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
        System.out.println(message1);

        switch (message1.getOPTION()) {
            case "ADD":
                //room,module, day, list of times, class
                ScheduleDay scheduleDay1 = timeTables.get(message1.getDay());
                if (scheduleDay1 == null) {
                    //scheduleDay1 = new ScheduleDay();
                    // need to redo logic to add scheduleday if it isnt there for specific room / module .
                }
                for (String time : message1.getListOfTimes()) {
                    if (scheduleDay1.checkTime(time)) {
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
                //make it so it displays module + room etc for booked
                objectOutputStream.writeObject(RESPONSE);

                break;
            case "VIEW": // viewing schedule for a day
                String room = message1.getROOM_NUMBER();
                String module = message1.getMODULE_NAME();

                ArrayList<ScheduleDay> moduleSchedules = moduleTimetable.get(message1.getDay());
                System.out.println(moduleSchedules);


                ArrayList<ScheduleDay> roomSchedules = roomTimetable.get(message1.getDay());


                ArrayList<String> listOfTakenTimes = new ArrayList<>();

                for(ScheduleDay day : moduleSchedules){
                    if(day.getModuleName().equals(module)){
                        listOfTakenTimes.addAll(day.getTakenTimes());
                    }
                }
                for(ScheduleDay day : roomSchedules){
                    boolean added = false;
                    if(day.getRoom().equals(room)){
                        //there should only be one schedule day per room ,
                        listOfTakenTimes.addAll(day.getTakenTimes());
                        break;
                    }
                    //if we havent break then its not there,
                    ScheduleDay roomDay = new ScheduleDay(null);
                    day.setRoom(room);
                    roomTimetable.get(message1.getDay()).add(roomDay);
                }
                System.out.println("VIEWING SCHEDULE FOR : " + message1.getDay() + " ROOM : " + room + " MODULE : " + module);

                Message responseV = new Message("VIEW");
                responseV.setListOfTimes(listOfTakenTimes);
                objectOutputStream.writeObject(responseV);
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


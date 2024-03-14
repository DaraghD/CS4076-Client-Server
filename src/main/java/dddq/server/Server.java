package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.ScheduleDay;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

public class Server {
    private static final int PORT = 1234;
    static String filePath = "database.ser";
    static Socket link;
    static HashMap<String, HashMap<String, ScheduleDay>> ProgrammeTimetable = new HashMap<>(); // DAY : list of  Programme SCHEDULEs , maybe hashmap of room name to day instaad of list
    static HashMap<String, HashMap<String, ScheduleDay>> roomTimetable = new HashMap<>(); // DAY : list of room schedules on that day
    static HashMap<String, ArrayList<String>> programmeModuleList = new HashMap<>(); // Programme name : class list , class cant go over 5 (Unique)
    static String[] dayOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public static void init() throws IncorrectActionException, IOException, ClassNotFoundException {
        File data = new File(filePath);
        if (data.length() == 0) {
            for (String day : dayOfTheWeek) {
                ProgrammeTimetable.put(day, new HashMap<String, ScheduleDay>());
                roomTimetable.put(day, new HashMap<String, ScheduleDay>());
            }
        } else {
            readData();
        }
    }

    public static void main(String[] args) throws IOException, IncorrectActionException, ClassNotFoundException {
        try {
            init();
        } catch (FileNotFoundException e) {
            System.out.println("No data found, creating new file");
        } catch (IOException e) {
            System.out.println("Error reading data from file");
        }
        while (true) {
            ObjectOutputStream objectOutputStream = null;
            try {
                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                    System.out.println("Server is running and waiting for connections...");
                    // Wait for a client to connect
                    link = serverSocket.accept();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                System.out.println("Client connected: " + link.getInetAddress());
                objectOutputStream = new ObjectOutputStream(link.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(link.getInputStream());

                while (!link.isClosed()) {
                    processClientMessage(objectInputStream, objectOutputStream);
                    saveData();
                }

            } catch (IOException | ClassNotFoundException | IncorrectActionException e) {
                var x = new Message("ERROR");
                x.setCONTENTS(e.getMessage());
                objectOutputStream.writeObject(x);
                e.printStackTrace();
            } finally {
                // Close the connection with the client
                System.out.println("Closing connection with client: " + link.getInetAddress());
                link.close();
            }
        }
    }

    private static void processClientMessage(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException, IncorrectActionException, ClassNotFoundException {
        Message message = (Message) objectInputStream.readObject();
        System.out.println(message); // debug
        String room = message.getROOM_NUMBER();
        String day = message.getDay();
        String Programme = message.getProgramme_NAME();
        String module = message.getModule();

        switch (message.getOPTION()) {
            case "ADD":
                //room,Programme, day, list of times, class

                // Class doesnt have to be shown anywhere so we are only storing it to make sure that only 5 classes per Programme
                programmeModuleList.computeIfAbsent(Programme, k -> {
                    ArrayList<String> x = new ArrayList<String>();
                    x.add(module);
                    return x;
                });

                //if its a new module and modules are > 5 then throw error
                if (programmeModuleList.get(Programme).size() == 5 && !programmeModuleList.get(Programme).contains(module)) {
                    throw new IncorrectActionException("Incorrect Action : Programme already has 5 classes");
                }

                ScheduleDay ProgrammeDay = ProgrammeTimetable.get(day).computeIfAbsent(Programme, k -> new ScheduleDay(Programme));
                ScheduleDay roomDay = roomTimetable.get(day).computeIfAbsent(room, k -> {
                    var x = new ScheduleDay(Programme);
                    x.setRoom(room);
                    return x;
                });

                for (String time : message.getListOfTimes()) {
                    // we don't have to check time is taken because we only send a list of free times
                    ProgrammeDay.bookTime(time);
                    roomDay.bookTime(time);
                }


                Message RESPONSE = new Message("SUCCESS");
                RESPONSE.setCONTENTS("BOOKED TIMES +  " + message.getListOfTimes().toString());
                //make it so it displays Programme + room etc for booked
                objectOutputStream.writeObject(RESPONSE);
                break;


            case "VIEW": // viewing schedule for a day
                ArrayList<String> listOfTakenTimes = new ArrayList<>();

                // Basically tries to get scheduleDay form hashmap, if its not there adds it. Concise null check
                ScheduleDay ProgrammeDay1 = ProgrammeTimetable.get(day).computeIfAbsent(Programme, k -> new ScheduleDay(Programme));

                ScheduleDay roomDay1 = roomTimetable.get(day).computeIfAbsent(room, k -> {
                    var x = new ScheduleDay(room);
                    x.setRoom(room);
                    return x;
                });

                listOfTakenTimes.addAll(ProgrammeDay1.getTakenTimes());
                listOfTakenTimes.addAll(roomDay1.getTakenTimes());
                for (String time : listOfTakenTimes) {
                    System.out.println("TIME TAKEN : " + time);
                }

                System.out.println("VIEWING SCHEDULE FOR : " + day + " ROOM : " + room + " Programme : " + Programme);

                Message responseV = new Message("VIEW");
                responseV.setListOfTimes(listOfTakenTimes);
                objectOutputStream.writeObject(responseV);
                break;
            case "REMOVE":
                //remove - need room , time(multiple?), module, Programme
                ArrayList<String> times = message.getListOfTimes();

                ScheduleDay programmeDay = ProgrammeTimetable.get(day).get(Programme);
                ScheduleDay roomD = roomTimetable.get(day).get(room);

                for (String time : times) {
                    programmeDay.getTimeTable().get(time).freeSlot();
                    roomD.getTimeTable().get(time).freeSlot();
                }


                Message responseR = new Message("SUCCESS");
                responseR.setCONTENTS("REMOVED TIMES +  " + times.toString());
                objectOutputStream.writeObject(responseR);
                break;
            case "DISPLAY": // only needs to be displayed to terminal on SERVER side as per abdul's email
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


    private static void saveData() throws IOException {
        // save data to disk
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(ProgrammeTimetable);
        out.writeObject(roomTimetable);
        out.writeObject(programmeModuleList);
        out.close();
        fileOut.close();
        System.out.println("Serialized data is saved in " + filePath);

    }

    private static void readData() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        ProgrammeTimetable = (HashMap<String, HashMap<String, ScheduleDay>>) in.readObject();
        roomTimetable = (HashMap<String, HashMap<String, ScheduleDay>>) in.readObject();
        programmeModuleList = (HashMap<String, ArrayList<String>>) in.readObject();
        in.close();
        fileIn.close();

    }


}



package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.ScheduleDay;
import dddq.client.TimeSlot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private static final int PORT = 1234;
    static String filePath = "database.ser";
    static Socket link;
    static HashMap<String, HashMap<String, ScheduleDay>> ProgrammeTimetable = new HashMap<>(); // DAY : (PROGRAMME : SCHEDULE FOR THAT PROGRAMME DAY)
    static HashMap<String, HashMap<String, ScheduleDay>> roomTimetable = new HashMap<>(); // DAY : ( ROOM : SCHEDULE FOR THAT ROOM)
    static HashMap<String, ArrayList<String>> programmeModuleList = new HashMap<>(); // PROGRAMME: class list , class cant go over 5 (Unique)
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
                    try {
                        processClientMessage(objectInputStream, objectOutputStream);
                        saveData();
                    } catch (IncorrectActionException e) {
                        var x = new Message("ERROR");
                        x.setCONTENTS(e.getMessage());
                        objectOutputStream.writeObject(x);
                        e.printStackTrace();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                var x = new Message("ERROR");
                x.setCONTENTS(e.getMessage());
                objectOutputStream.writeObject(x);
                e.printStackTrace();
            }
        }
    }
    private static int uniqueModules(ArrayList<String> modules){
        ArrayList<String> unique = new ArrayList<>();
        for(String x : modules){
           if(!unique.contains(x)){
               unique.add(x);
           }
        }
        return unique.size();
    }

    private static void processClientMessage(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException, IncorrectActionException, ClassNotFoundException {
        Message message = (Message) objectInputStream.readObject();
        System.out.println(message); // debug
        switch (message.getOPTION()) {
            case "ADD":
                //room,Programme, day, list of times, class
                String room = message.getROOM_NUMBER();
                String Programme = message.getProgramme_NAME();
                String day = message.getDay();
                String module = message.getModule();

                // Class doesnt have to be shown anywhere so we are only storing it to make sure that only 5 classes per Programme
                programmeModuleList.computeIfAbsent(Programme, k -> {
                    ArrayList<String> x = new ArrayList<String>();
                    x.add(module);
                    return x;
                });

                if(uniqueModules(programmeModuleList.get(Programme)) < 5){
                    programmeModuleList.get(Programme).add(module);
                }
                else{
                    throw new IncorrectActionException(("Incorrect Action : Programme already has 5 classes"));
                }
                //if its a new module and modules are > 5 then throw error

                ScheduleDay ProgrammeDay = ProgrammeTimetable.get(day).computeIfAbsent(Programme, k -> new ScheduleDay(Programme));
                ScheduleDay roomDay = roomTimetable.get(day).computeIfAbsent(room, k -> {
                    var x = new ScheduleDay(Programme);
                    x.setRoom(room);
                    return x;
                });

                for (String time : message.getListOfTimes()) {
                    // we don't have to check time is taken because we only send a list of free times
                    ProgrammeDay.getTimeSlot(time).setModule(module);
                    ProgrammeDay.bookTime(time);
                    roomDay.bookTime(time);
                    roomDay.getTimeSlot(time).setRoom(room);
                    ProgrammeDay.getTimeSlot(time).setRoom(room);
                    System.out.println(roomDay.getTimeSlot(time).getRoom());
                }

                Message RESPONSE = new Message("SUCCESS");
                RESPONSE.setCONTENTS("BOOKED TIMES +  " + message.getListOfTimes().toString());
                //make it so it displays Programme + room etc for booked
                objectOutputStream.writeObject(RESPONSE);
                break;
            case "VIEW": // viewing schedule for a day
                String viewDay = message.getDay();
                String viewRoom = message.getROOM_NUMBER();
                String viewProgramme = message.getProgramme_NAME();

                ArrayList<String> listOfTakenTimes = new ArrayList<>();

                // Basically tries to get scheduleDay form hashmap, if its not there adds it. Concise null check
                ScheduleDay ProgrammeDay1 = ProgrammeTimetable.get(viewDay).computeIfAbsent(viewProgramme, k -> new ScheduleDay(viewProgramme));

                ScheduleDay roomDay1 = roomTimetable.get(viewDay).computeIfAbsent(viewRoom, k -> {
                    var x = new ScheduleDay(viewRoom);
                    x.setRoom(viewRoom);
                    return x;
                });

                listOfTakenTimes.addAll(ProgrammeDay1.getTakenTimes());

                // dont show rooms if removing
                if (!message.getCONTENTS().equals("r")) {
                    listOfTakenTimes.addAll(roomDay1.getTakenTimes());
                }
                for (String time : listOfTakenTimes) {
                    System.out.println("TIME TAKEN : " + time);
                }

                System.out.println("VIEWING SCHEDULE FOR : " + viewDay+ " ROOM : " + viewRoom+ " Programme : " + viewProgramme);

                Message responseV = new Message("VIEW");
                responseV.setListOfTimes(listOfTakenTimes);
                objectOutputStream.writeObject(responseV);
                break;
            case "REMOVE":
                //takes list of times, programme and room, can identify module from these.
                String removeDay = message.getDay();
                String removeRoom = message.getROOM_NUMBER();
                String removeProgramme = message.getProgramme_NAME();
                //remove - need room , time(multiple?), module, Programme
                ArrayList<String> times = message.getListOfTimes();
                ScheduleDay programmeDay = ProgrammeTimetable.get(removeDay).get(removeProgramme);
                ScheduleDay roomD = roomTimetable.get(removeDay).get(removeRoom);

                String mod = null;
                for (String time : times) {
                    mod = programmeDay.getTimeTable().get(time).getModule();
                    programmeDay.getTimeTable().get(time).freeSlot();
                    roomD.getTimeTable().get(time).freeSlot();
                }
                programmeModuleList.get(removeProgramme).remove(mod);
                Message responseR = new Message("SUCCESS");
                responseR.setCONTENTS("Removed times : " + times.toString() + "\n Rooms free : " + message.getROOM_NUMBER() + "\n Removed module : "+mod);
                objectOutputStream.writeObject(responseR);
                break;
            case "DISPLAY":
                String displayProgramme = message.getProgramme_NAME();
                System.out.println("DISPLAYING WEEK SCHEDULE FOR :" +displayProgramme); // only needs to be displayed to terminal on SERVER side as per abdul's email
                for (String d : dayOfTheWeek){
                    System.out.println("DAY : " + d + "\n"+ "-----------------");
                    ScheduleDay s = ProgrammeTimetable.get(d).get(displayProgramme);
                    if(s == null){
                        System.out.println("NO CLASSES SCHEDULED FOR : " + d  + "\n -----------------");
                        continue;
                    }
                    for(String time: s.getTakenTimes()){
                        TimeSlot t = s.getTimeSlot(time);
                        System.out.println(t);
                        System.out.println("TIME : " + time);
                        System.out.println("ROOM : " + s.getTimeSlot(time).getRoom());
                        System.out.println("MODULE : " + s.getTimeSlot(time).getModule());
                    }
                }
                Message responseD = new Message("SUCCESS");
                responseD.setCONTENTS("DISPLAYED WEEK SCHEDULE FOR : " +displayProgramme);
                objectOutputStream.writeObject(responseD);
                break;
            case "STOP":
                System.out.println("Stopping server as requested by client");
                Message stopResponse = new Message("TERMINATE");
                stopResponse.setCONTENTS("Closing connection to client as requested");
                objectOutputStream.writeObject(stopResponse);
                saveData();
                link.close();
                break;
            default:
                throw new IncorrectActionException("NOT A VALID COMMAND");
        }
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
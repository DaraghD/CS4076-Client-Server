package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.ScheduleDay;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server { // TODO: make thread safe / syncrhonized, probably have to use wait & notify
    // maybe split datafields into separate class
    private static final int PORT = 1234;
    static String filePath = "database.ser"; // .ser needed (windows)
    static Socket link;
    static HashMap<String, HashMap<String, ScheduleDay>> ProgrammeTimetable = new HashMap<>(); // DAY : (PROGRAMME : SCHEDULE FOR THAT PROGRAMME DAY)
    static HashMap<String, HashMap<String, ScheduleDay>> roomTimetable = new HashMap<>(); // DAY : ( ROOM : SCHEDULE FOR THAT ROOM)
    static HashMap<String, ArrayList<String>> programmeModuleList = new HashMap<>(); // PROGRAMME: class list , class cant go over 5 (Unique)
    static String[] dayOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public HashMap<String, HashMap<String, ScheduleDay>> getRoomTimetable() {
        return roomTimetable;
    }

    public HashMap<String, ArrayList<String>> getProgrammeModuleList() {
        return programmeModuleList;
    }

    public HashMap<String, HashMap<String, ScheduleDay>> getProgrammeTimetable() {
        return ProgrammeTimetable;
    }

    public void init() throws IOException, ClassNotFoundException {
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
        System.out.println("Server is running");
        Server server = new Server();
        try {
            server.init();
        } catch (FileNotFoundException e) {
            System.out.println("No data found, creating new file");
        } catch (IOException e) {
            System.out.println("Error reading data from file");
        }
        ServerSocket serverSocket = new ServerSocket(PORT);

        while(true){
            try {
                Socket link = serverSocket.accept();
                //i dont think new Server() is correct ? Does it update correctly
                clientHandler clientHandler = new clientHandler(server, link);
                Thread t = new Thread(clientHandler);
                t.start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

     int uniqueModules(ArrayList<String> modules) {
        ArrayList<String> unique = new ArrayList<>();
        for (String x : modules) {
            if (!unique.contains(x)) {
                unique.add(x);
            }
        }
        return unique.size();
    }

    void saveData() throws IOException {
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

    void readData() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        ProgrammeTimetable = (HashMap<String, HashMap<String, ScheduleDay>>) in.readObject();
        roomTimetable = (HashMap<String, HashMap<String, ScheduleDay>>) in.readObject();
        programmeModuleList = (HashMap<String, ArrayList<String>>) in.readObject();
        in.close();
        fileIn.close();
    }

}
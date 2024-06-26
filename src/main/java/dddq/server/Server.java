package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.ScheduleDay;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 4444;
    static String filePath = "database.ser"; // .ser needed (windows)
    static Socket link;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>> roomTimetable = new ConcurrentHashMap<>(); // DAY : ( ROOM : SCHEDULE FOR THAT ROOM)
    static String[] dayOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private CopyOnWriteArrayList<Programme> programmes = new CopyOnWriteArrayList<Programme>(); // Stores all programmes, which have their own modules that have timetable for that module

    public synchronized CopyOnWriteArrayList<Programme> getProgrammes() {
        return programmes;
    }

    public synchronized ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>> getRoomTimetable() {
        return roomTimetable;
    }

    public synchronized boolean programmeExists(String name){
        for(Programme p : programmes){
            if(p.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public synchronized Programme getProgramme(String name){
        for(Programme p: programmes){
            if(p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }


    public void init() throws IOException, ClassNotFoundException {
        File data = new File(filePath);
        if (data.length() == 0) {
            for (String day : dayOfTheWeek) {
                roomTimetable.put(day, new ConcurrentHashMap<String, ScheduleDay>());
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

        while (true) {
            try {
                Socket link = serverSocket.accept();
                clientHandler clientHandler = new clientHandler(server, link);
                Thread t = new Thread(clientHandler);
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void saveData() throws IOException {
        //save data to disk
        //order matters
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(programmes);
        out.writeObject(roomTimetable);
        out.close();
        fileOut.close();
        System.out.println("Serialized data is saved in " + filePath);
    }

    void readData() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        programmes = (CopyOnWriteArrayList<Programme>) in.readObject();
        roomTimetable = (ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>>) in.readObject();
        in.close();
        fileIn.close();
    }
}

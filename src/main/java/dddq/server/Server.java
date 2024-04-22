package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.ScheduleDay;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // TODO: make thread safe / synchronized, probably have to use wait & notify
    private static final int PORT = 1234;
    static String filePath = "database.ser"; // .ser needed (windows)
    static Socket link;
    static ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>> ProgrammeTimetable = new ConcurrentHashMap<>(); // DAY : (PROGRAMME : SCHEDULE FOR THAT PROGRAMME DAY)
    static ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>> roomTimetable = new ConcurrentHashMap<>(); // DAY : ( ROOM : SCHEDULE FOR THAT ROOM)
    static ConcurrentHashMap<String, ArrayList<String>> programmeModuleList = new ConcurrentHashMap<>(); // PROGRAMME: class list , modules cant go over 5 (Unique)
    static String[] dayOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public void init() throws IOException, ClassNotFoundException {
        File data = new File(filePath);
        if (data.length() == 0) {
            for (String day : dayOfTheWeek) {
                ProgrammeTimetable.put(day, new ConcurrentHashMap<String, ScheduleDay>());
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
        ProgrammeTimetable = (ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>>) in.readObject();
        roomTimetable = (ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduleDay>>) in.readObject();
        programmeModuleList = (ConcurrentHashMap<String, ArrayList<String>>) in.readObject();
        in.close();
        fileIn.close();
    }
}

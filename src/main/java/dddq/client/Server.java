package dddq.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;
    static Map<String, String> classSchedules = new HashMap<>();

    //hashmap of dates - schedules for that date

    static HashMap<Date,Schedule> timeTables = new HashMap<>();


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for connections...");

            // To add or remove a class, the client selects a date, time, and room number
            //and then provides the name of the class to schedule.
            while (true) {

                Socket link = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                String message = in.readLine();
                String x = processClientMessage(message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                System.out.println("\n Closing Connection... ");
        }
    }


    private static String processClientMessage(String clientMessage) {
        // Action format: ADD LM051-2022 2022-03-04 9:00 - 10:00 Room1
        //                                          START_TIME - END_TIME
        switch(clientMessage.split("\\s+")[0]) {
            case "ADD":
                // Extract the information after "ADD" and add it to the schedule
                String classInfo = clientMessage.substring("ADD".length()).trim();
                String className = classInfo.split("\\s+")[0];

                if (!classSchedules.containsKey(className)) {
                    classSchedules.put(className, classInfo);
                    return "Class added successfully";
                } else {
                    return "Class schedule already exists";
                }
            case "DISPLAY":
                //return displaySchedule(clientMessage);
                break;
            case "VIEW":
                //VIEW 12/07/06
                if timeTables.containsKey(clientMessage); // send full objects TODO: aaaa
            case "REMOVE":
                // ... (existing REMOVE logic)
                break;
            case "PRINTALL":
                return "1";
            default:
                return "Invalid action";
        }





    private static String displaySchedule(String clientMessage) {
        // Extract class name from DISPLAY command
        String className = clientMessage.split("\\s+")[1];

        if (classSchedules.containsKey(className)) {
            return "Schedule for " + className + ":\n" + classSchedules.get(className);
        } else {
            return "No schedule found for " + className;
        }
    }




    private String getAllSchedules() {
        if (true) {
            // If no filter is provided, return all schedules
            StringBuilder allSchedules = new StringBuilder();
            for (Map.Entry<String, String> entry : classSchedules.entrySet()) {
                allSchedules.append("Class: ").append(entry.getKey()).append("\n")
                        .append("Schedule: ").append(entry.getValue()).append("\n");
            }
            return allSchedules.toString();
        } else {
            // Filter schedules based on the provided criteria
            StringBuilder filteredSchedules = new StringBuilder();
            for (Map.Entry<String, String> entry : classSchedules.entrySet()) {
                if (entry.getKey().contains("1")) {
                    filteredSchedules.append("Class: ").append(entry.getKey()).append("\n")
                            .append("Schedule: ").append(entry.getValue()).append("\n");
                }
            }

            if (filteredSchedules.length() > 0) {
                return filteredSchedules.toString();
            } else {
                return "No schedules found for the specified filter: " ;
            }
        }
    }
}


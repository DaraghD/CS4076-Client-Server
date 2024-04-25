package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.Message;
import dddq.client.ScheduleDay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class clientHandler implements Runnable {

    private Socket link;
    private Server server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean running = true;

    public clientHandler(Server server, Socket socket) {
        this.server = server;
        this.link = socket;
    }


    @Override
    public void run() {
        try {
            input = new ObjectInputStream(link.getInputStream());
            output = new ObjectOutputStream(link.getOutputStream());
            while (running) {
                try {
                    processClientMessage(input, output);
                    server.saveData();
                } catch (IOException | InterruptedException | ClassNotFoundException | IncorrectActionException e) {
                    var x = new Message("ERROR");
                    x.setCONTENTS(e.getMessage());
                    try {
                        output.writeObject(x);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void processClientMessage(ObjectInputStream input, ObjectOutputStream output) throws ClassNotFoundException, IncorrectActionException, IOException, InterruptedException {
        Message message = (Message) input.readObject();
        System.out.println(message); // debug
//        Thread.sleep(2000);  for testing delay on server for javafx.concurrent, keep here in case need to show it during interview

        switch (message.getOPTION()) {
            case "ADD":
                //room,Programme, day, list of times, class
                String room = message.getROOM_NUMBER();
                String Programme_name = message.getProgramme_NAME();
                String day = message.getDay();
                String module_name = message.getModule();
                ArrayList<String> times = message.getListOfTimes();

                if (!server.programmeExists(Programme_name)) {
                    server.getProgrammes().add(new Programme(Programme_name));
                }
                Programme programme = server.getProgramme(Programme_name);
                assert programme != null;
                if (programme.getModule(module_name) == null) {
                    programme.addModule(module_name);
                }
                for (String time : times) {
                    programme.bookClass(module_name, day, time, room);
                }


                // updating room tiemtable
                ScheduleDay roomDay = Server.roomTimetable.get(day).computeIfAbsent(room, k -> {
                    // gets timetable for day, puts new _room_ key with value as this new schedule day for that room
                    // day : ( room : scheduleDay )
                    return new ScheduleDay(room, true);
                });

                for (String time : times) {
                    roomDay.bookTime(time);
                }

                //we know its a succses since exception would of been thrown otherwise
                Message RESPONSE = new Message("SUCCESS");
                RESPONSE.setCONTENTS("Programme : " + Programme_name + "\nBOOKED TIMES +  " + message.getListOfTimes().toString());
                //format this ^
                output.writeObject(RESPONSE);
                output.flush();
                break;
            case "VIEW":
                String viewDay = message.getDay();
                String viewRoom = message.getROOM_NUMBER();
                String viewProgramme = message.getProgramme_NAME();
                ArrayList<String> listOfTakenTimes = new ArrayList<>();

                // checks for classes on at that programme day , and that room, cant have two classes on at same day for programme

                ScheduleDay room_times = Server.roomTimetable.get(viewDay).computeIfAbsent(viewRoom, k -> {
                    return new ScheduleDay(viewRoom, true);
                });
                if (!message.getCONTENTS().equals("r")) {
                    // if it's not the remove option we show times from room
                    listOfTakenTimes.addAll(room_times.getTakenTimes());
                }
                if (server.getProgramme(viewProgramme) == null) {
                    server.programmes.add(new Programme(viewProgramme));
                }
                listOfTakenTimes.addAll(server.getProgramme(viewProgramme).getTakenTimes(viewDay));
                Message responseView = new Message("VIEW");
                responseView.setListOfTimes(listOfTakenTimes);
                output.writeObject(responseView);
                output.flush();
                break;

            case "REMOVE":
                String removeDay = message.getDay();
                String removeRoom = message.getROOM_NUMBER();
                String removeProgramme = message.getProgramme_NAME();
                String removeModule = message.getModule();
                assert removeModule != null; //TODO: make sure client can pass in module
                //remove - need room , time(multiple?), module, Programme
                ArrayList<String> removeTimes = message.getListOfTimes();

                ScheduleDay removeRoomDay = Server.roomTimetable.get(removeDay).get(removeRoom);
                if (removeRoomDay == null) {
                    throw new IncorrectActionException("No bookings for this room");
                }
                ScheduleDay removeModuleDay = server.getProgramme(removeProgramme).getModule(removeModule).getDay(removeDay);

                for (String t : removeTimes) {
                    removeRoomDay.getTimeSlot(t).freeSlot();
                    removeModuleDay.getTimeSlot(t).freeSlot();
                }

                Message removeResponse = new Message("SUCCESS");
                output.writeObject(removeResponse);
                break;
            case "DISPLAY":
                if (!server.programmeExists(message.getProgramme_NAME())) {
                    Message displayResponse = new Message("ERROR");
                    displayResponse.setCONTENTS("Programme does not exist");
                    output.writeObject(displayResponse);
                    break;
                }
                Programme displayProgramme = server.getProgramme(message.getProgramme_NAME());
                var x = displayProgramme;
                Message displayProgrammeResponse = new Message("SUCCESS");
                displayProgrammeResponse.setCONTENTS("Programme exists, timetable will be displayed");
                displayProgrammeResponse.setProgrammeObject(displayProgramme);
                output.reset(); // java is so bad
                output.writeObject(displayProgrammeResponse);
                break;
            case "EARLY LECTURE":
                String earlyProgrammeName = message.getProgramme_NAME();
                if (!server.programmeExists(earlyProgrammeName)) {
                    throw new IncorrectActionException("Programme does not exist");
                }
                System.out.println(7777);
                Programme earlyProgramme = server.getProgramme(earlyProgrammeName);
                assert earlyProgramme != null;

                ForkJoinPool.commonPool().invoke(new EarlyLectures(earlyProgramme, 0, earlyProgramme.getModules().size()));
                Message earlyResponse = new Message("SUCCESS");
                earlyResponse.setCONTENTS("All lectures that in programme : " + earlyProgrammeName + " have been moved to earliest possible time.");
                output.writeObject(earlyResponse);
                output.flush();
                break;
            case "STOP":
                System.out.println("Stopping server as requested by client");
                Message stopResponse = new Message("TERMINATE");
                stopResponse.setCONTENTS("Closing connection to client as requested");
                output.writeObject(stopResponse);
                server.saveData();
                link.close();
                running = false;
                break;
        }


    }


}

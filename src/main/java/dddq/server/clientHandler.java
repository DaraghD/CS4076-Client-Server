package dddq.server;

import dddq.client.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

//call client handelr?
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
                processClientMessage(input, output);
                server.saveData();
            }
        } catch (IOException | ClassNotFoundException | IncorrectActionException e) {
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


    private void processClientMessage(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws
            IOException, IncorrectActionException, ClassNotFoundException {
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
                server.getProgrammeModuleList().computeIfAbsent(Programme, k -> {
                    ArrayList<String> x = new ArrayList<String>();
                    x.add(module);
                    return x;
                });

                if (server.uniqueModules(server.getProgrammeModuleList().get(Programme)) < 5) { // think this is not correct ?
                    server.getProgrammeModuleList().get(Programme).add(module);
                } else {
                    throw new IncorrectActionException(("Incorrect Action : Programme already has 5 classes"));
                }
                //if its a new module and modules are > 5 then throw error

                ScheduleDay ProgrammeDay = server.getProgrammeTimetable().get(day).computeIfAbsent(Programme, k -> new ScheduleDay(Programme));
                ScheduleDay roomDay = server.getRoomTimetable().get(day).computeIfAbsent(room, k -> {
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
                ScheduleDay ProgrammeDay1 = server.getProgrammeTimetable().get(viewDay).computeIfAbsent(viewProgramme, k -> new ScheduleDay(viewProgramme));

                ScheduleDay roomDay1 = server.getRoomTimetable().get(viewDay).computeIfAbsent(viewRoom, k -> {
                    var x = new ScheduleDay(viewRoom);
                    x.setRoom(viewRoom);
                    return x;
                });

                listOfTakenTimes.addAll(ProgrammeDay1.getTakenTimes());

                // dont show rooms if removing, because then they could  remove another class
                if (!message.getCONTENTS().equals("r")) {
                    listOfTakenTimes.addAll(roomDay1.getTakenTimes());
                }
                for (String time : listOfTakenTimes) {
                    System.out.println("TIME TAKEN : " + time);
                }

                System.out.println("VIEWING SCHEDULE FOR : " + viewDay + " ROOM : " + viewRoom + " Programme : " + viewProgramme);

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
                ScheduleDay programmeDay = server.getProgrammeTimetable().get(removeDay).get(removeProgramme);
                ScheduleDay roomD = server.getRoomTimetable().get(removeDay).get(removeRoom);

                String mod = null;
                for (String time : times) {
                    mod = programmeDay.getTimeTable().get(time).getModule();
                    programmeDay.getTimeTable().get(time).freeSlot();
                    roomD.getTimeTable().get(time).freeSlot();
                }
                server.getProgrammeModuleList().get(removeProgramme).remove(mod);
                Message responseR = new Message("SUCCESS");
                responseR.setCONTENTS("Removed times : " + times.toString() + "\n Rooms free : " + message.getROOM_NUMBER() + "\n Removed module : " + mod);
                objectOutputStream.writeObject(responseR);
                break;
            case "DISPLAY":
                String displayProgramme = message.getProgramme_NAME();
                System.out.println("DISPLAYING WEEK SCHEDULE FOR :" + displayProgramme); // only needs to be displayed to terminal on SERVER side as per abdul's email
                for (String d : server.dayOfTheWeek) {
                    System.out.println("DAY : " + d + "\n" + "-----------------");
                    ScheduleDay s = server.getProgrammeTimetable().get(d).get(displayProgramme);
                    if (s == null) {
                        System.out.println("NO CLASSES SCHEDULED FOR : " + d + "\n -----------------");
                        continue;
                    }
                    for (String time : s.getTakenTimes()) {
                        TimeSlot t = s.getTimeSlot(time);
                        System.out.println(t);
                        System.out.println("TIME : " + time);
                        System.out.println("ROOM : " + s.getTimeSlot(time).getRoom());
                        System.out.println("MODULE : " + s.getTimeSlot(time).getModule());
                    }
                }
                Message responseD = new Message("SUCCESS");
                responseD.setCONTENTS("DISPLAYED WEEK SCHEDULE FOR : " + displayProgramme);
                objectOutputStream.writeObject(responseD);
                break;
            case "STOP":
                System.out.println("Stopping server as requested by client");
                Message stopResponse = new Message("TERMINATE");
                stopResponse.setCONTENTS("Closing connection to client as requested");
                objectOutputStream.writeObject(stopResponse);
                server.saveData();
                link.close();
                running = false;
                break;
            default:
                throw new IncorrectActionException("NOT A VALID COMMAND");
        }
    }


}
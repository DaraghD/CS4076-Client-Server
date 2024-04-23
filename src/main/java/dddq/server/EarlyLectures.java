package dddq.server;

import dddq.client.ScheduleDay;

import java.util.concurrent.*;

public class EarlyLectures extends RecursiveTask<Boolean> {

        Module module;

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        ConcurrentHashMap<String, ScheduleDay> earlyLectures;

        EarlyLectures(Module m){
            this.module = m;
            earlyLectures = module.getClasses();
        }

        protected Boolean compute(){
            for(String day : days){

            }


            return false;
        }


    public boolean earlyLectureModule(ScheduleDay day){
        //

        return false;

    }


}

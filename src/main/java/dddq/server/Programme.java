package dddq.server;

import dddq.client.IncorrectActionException;
import dddq.client.ScheduleDay;
import dddq.server.Module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Programme implements Serializable {
    private CopyOnWriteArrayList<Module> modules;
    private String name;
    Programme(String name){
        this.name = name;
        modules = new CopyOnWriteArrayList<Module>();
    }
    public synchronized void addModule(String moduleName) throws IncorrectActionException {
        for(Module module : modules) {
            if(module.getName().equals(moduleName)) {
                throw new IncorrectActionException("Module already exists");
            }
        }
        if(modules.size() == 5) {
            throw new IncorrectActionException("Programme already has 5 modules");
        }

        modules.add(new Module(name, moduleName));
    }

    public synchronized Module getModule(String name){
        for(Module m : modules){
            if(m.getName().equals(name)){
                return m;
            }
        }
        return null;
    }

    public synchronized void bookClass(String module, String day, String time, String room) throws IncorrectActionException {
        Module m = getModule(module);
        m.addClass(module,day,time, room );
    }

    public synchronized ArrayList<String> getTakenTimes(String day){
        ArrayList<String> temp = new ArrayList<String>();
        for(Module m : modules){
            temp.addAll(m.getDay(day).getTakenTimes());
        }
        return temp;
    }
    public String getName(){
        return name;
    }
    public CopyOnWriteArrayList<Module> getModules() {
        return modules;
    }

}

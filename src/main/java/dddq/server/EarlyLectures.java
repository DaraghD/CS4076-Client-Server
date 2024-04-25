package dddq.server;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;

public class EarlyLectures extends RecursiveAction {
    Programme programme;
    int start;
    int end;
    CopyOnWriteArrayList<Module> modules;
    static final int SEQUENTIAL_CUTOFF = 1000; // Threshold for sequential processing

    EarlyLectures(Programme p, int start, int end) {
        this.programme = p;
        this.start = start;
        this.end = end;
        this.modules = p.getModules();
    }

    protected void compute() {
        // Base case: If the range of modules is below the sequential cutoff, process them sequentially
        if (end - start < SEQUENTIAL_CUTOFF) {
            for (int i = start; i < end; i++) {
                Module module = modules.get(i);
                earlyModule(module);
            }
        } else {
            int mid = (start + end) / 2;
            EarlyLectures left = new EarlyLectures(programme, start, mid);
            EarlyLectures right = new EarlyLectures(programme, mid, end);
            left.fork();
            right.compute();
            left.join();
        }
    }

    public void earlyModule(Module m) {
        System.out.println(77777);
        new Thread(new EarlyLectureThread(m.getDay("Monday"), "Monday")).start();
        new Thread(new EarlyLectureThread(m.getDay("Tuesday"), "Tuesday")).start();
        new Thread(new EarlyLectureThread(m.getDay("Wednesday"), "Wednesday")).start();
        new Thread(new EarlyLectureThread(m.getDay("Thursday"), "Thursday")).start();
        new Thread(new EarlyLectureThread(m.getDay("Friday"), "Friday")).start();

    }
}


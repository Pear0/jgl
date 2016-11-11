package jgl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by william on 11/1/16.
 */
public class MassLogger {

    private static final ConcurrentHashMap<String, MassLogger> loggers = new ConcurrentHashMap<>();

    public static void trigger(String tag) {
        MassLogger logger = loggers.computeIfAbsent(tag, MassLogger::new);
        logger.trigger();
    }

    private String tag;

    private volatile long lastLog;
    private volatile long triggerCount;

    private MassLogger(String tag) {
        this.tag = tag;
    }

    public synchronized void trigger() {
        triggerCount++;

        long nano = System.nanoTime();
        if (nano - lastLog > 1_000_000_000) {
            double elapsed = (nano - lastLog) / 1e9;
            System.out.println("# " + tag + ": " + triggerCount + " in " + elapsed + " seconds");
            lastLog = nano;
            triggerCount = 0;
        }

    }

}

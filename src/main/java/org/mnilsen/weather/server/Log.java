package org.mnilsen.weather.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author michaeln
 */
public class Log {

    private static final Logger log = Logger.getLogger("org.mnilsen.weather.server");
    private static final Logger dataLog = Logger.getLogger("org.mnilsen.weather.data");

    private Log() {
    }

    static {
        configure();
    }

    private static void configure() {
        log.setLevel(Level.FINEST);
        try {
            String path = Utils.getAppProperties().get(AppProperty.LOG_DIRECTORY);
            File dir = new File(path);
            if (!dir.exists()) {
                log.info(String.format("Creating Log directory at %s", dir.getCanonicalPath()));
                dir.mkdirs();
            }
            FileHandler fh = new FileHandler(path + "/weather_server_%g.log", 200000, 6, false);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);
            log.addHandler(fh);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to add logging FileHandler", e);
        }

        dataLog.setLevel(Level.ALL);
        try {
            String path = Utils.getAppProperties().get(AppProperty.LOG_DIRECTORY);

            FileHandler fh = new FileHandler(path + "/weather_data_%g.log", 200000, 6, true);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);
            dataLog.addHandler(fh);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to add data logging FileHandler", e);
        }
    }

    public static Logger getLog() {
        return log;
    }

    public static Logger getDataLog() {
        return dataLog;
    }

}

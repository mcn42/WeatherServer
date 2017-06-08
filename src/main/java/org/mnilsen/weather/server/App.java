package org.mnilsen.weather.server;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        App app = new App();
        String configFilePath =  args.length > 0?args[0]:null;
        app.initialize(configFilePath);
        
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                if(app != null) app.shutdown();
//            }
//            
//        });
    }
    
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private static final Logger logger = Logger.getLogger("org.mnilsen.weather.server");

    public void initialize(String configFilePath) {
        if(initialized.get()) return;
        try {
            initialized.set(true);
            logger.info("Initializing system...");            
            if(configFilePath == null) configFilePath = "/home/pi/weather/wserver.properties";
            //  load properties
            Utils.config(configFilePath);
            
            boolean saveToMongo = Utils.getAppProperties().getBoolean(AppProperty.USE_DATABASE);
            int historyLengthDays = Utils.getAppProperties().getInt(AppProperty.HISTORY_LENGTH_DAYS);
            long diplayRefreshMillis = Utils.getAppProperties().getLong(AppProperty.DISPLAY_UPDATE_PERIOD);
            long sensorUpdateMillis = Utils.getAppProperties().getLong(AppProperty.SENSOR_POLLING_PERIOD_MILLIS);
            String mongoHost = Utils.getAppProperties().get(AppProperty.MONGO_HOST);
            int mongoPort = Utils.getAppProperties().getInt(AppProperty.MONGO_PORT);
            boolean showHistory = Utils.getAppProperties().getBoolean(AppProperty.DISPLAY_HISTORY);
            Coordinator.intialize(showHistory, saveToMongo, historyLengthDays, diplayRefreshMillis, sensorUpdateMillis, mongoHost, mongoPort);
            
            Coordinator.getInstance().start();
            Log.getLog().info("Initialization completed");
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"An initialization error occurred",ex);
        }
    }

    
    public void shutdown() {
        if (Coordinator.getInstance() != null)
          Coordinator.getInstance().stop();
    }
}

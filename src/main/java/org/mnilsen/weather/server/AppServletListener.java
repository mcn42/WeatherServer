/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/**
 *
 * @author michaeln
 *//**
 *
 * @author michaeln
 */
import javax.servlet.annotation.WebListener;
@WebListener
public class AppServletListener implements ServletContextListener{
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private static Logger logger = Logger.getLogger("org.mnilsen.weather.server");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(initialized.get()) return;
        try {
            initialized.set(true);
            logger.info("Initializing system...");
            //  Read home dir
            InitialContext ctx = new InitialContext();
            String configPath = (String) ctx.lookup("java:comp/env/APPLICATION_HOME");
            String configFilename = (String) ctx.lookup("java:comp/env/CONFIG_FILENAME");
            if(!configPath.endsWith("/")) configPath = configPath + "/";
            //  load properties
            Utils.config(configPath + configFilename);
            
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
            sce.getServletContext().log("A WeatherServer initialization error occurred", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (Coordinator.getInstance() != null)
          Coordinator.getInstance().stop();
    }
    
}

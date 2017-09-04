/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.sensor;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import org.iot.raspberry.grovepi.GroveDigitalIn;
import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;
import org.mnilsen.weather.server.AppProperty;
import org.mnilsen.weather.server.Coordinator;
import org.mnilsen.weather.server.Log;
import org.mnilsen.weather.server.Utils;

/**
 *
 * @author michaeln
 */
public class WaterSensor {

    private final int sensorPin = Utils.getAppProperties().getInt(AppProperty.WATER_SENSOR_PIN);
    private final long monitorPeriod = Utils.getAppProperties().getLong(AppProperty.WATER_MONITOR_PERIOD);
    private GrovePi pi = null;
    private GroveDigitalIn sensor = null;
    private MonitorTask task = null;
    private boolean lastReading;
    private long lastUpdate = -1;
    
    private Long lastAlert = null;
    private Long firstAlert = null;
    
    public WaterSensor() {
        
    }
    
    public void startMonitor() {
        try {
            Log.getLog().info("Starting Water Sensor monitoring...");
            this.pi = new GrovePi4J();
            sensor = pi.getDigitalIn(sensorPin);
            this.task = new MonitorTask();
            Utils.getAppTimer().schedule(task, 0L, monitorPeriod);
            Log.getLog().log(Level.INFO, "Water Sensor monitoring started successfully with period {0}", monitorPeriod);
        } catch (IOException ex) {
            Log.getLog().log(Level.SEVERE, String.format("Water Sensor monitoring startup error"), ex);
        }
    }
    
    public void stopMonitor() {
        Log.getLog().info("Stopping Water Sensor monitoring...");
        if (this.pi != null) {
            this.pi.close();
        }
    }
    
    private void readSensor() {
        try {
            this.lastReading = this.sensor.get();
            Log.getLog().info(String.format("Water Sensor reading: '%s'", this.lastReading));
            this.lastUpdate = System.currentTimeMillis();
            //  HIGH is dry!
            if (!this.lastReading) {
                //  TODO send alert
            } else { 
                this.lastAlert = null;
                this.firstAlert = null;
            }
            
        } catch (IOException | InterruptedException ex) {
            Log.getLog().log(Level.SEVERE, String.format("Water Sensor read error"), ex);
        }
    }

    public boolean getLastReading() {
        return lastReading;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
    
    private static DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private void sendAlert()
    {   
        if(this.firstAlert == null) this.firstAlert =  System.currentTimeMillis();  
        String since = df.format(new Date(this.firstAlert));
        if(this.lastAlert == null) {
            this.lastAlert = System.currentTimeMillis();
        } else {
            long period = System.currentTimeMillis() - this.lastAlert;
            if(period < Utils.getAppProperties().getLong(AppProperty.ALERT_SUPRESS_PERIOD))
            {
                Log.getLog().info(String.format("Water present message supressed, last sent %s",df.format(new Date(this.lastAlert))));
                return;
            } else this.lastAlert = System.currentTimeMillis();
        }
        String msg = String.format("WATER PRESENT! Since %s", since);
        Coordinator.getInstance().getAwsMgr().sendSMSMessage(msg, "+19086564206");
        
        Log.getLog().info(String.format("Water present message sent, first sent %s",since));
    }
    
    class MonitorTask extends TimerTask {
        
        @Override
        public void run() {
            readSensor();
        }
        
    }
    
    
}

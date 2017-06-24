/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.sensor;

import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import org.iot.raspberry.grovepi.GroveDigitalIn;
import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;
import org.mnilsen.weather.server.AppProperty;
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
    
    class MonitorTask extends TimerTask {
        
        @Override
        public void run() {
            readSensor();
        }
        
    }
}

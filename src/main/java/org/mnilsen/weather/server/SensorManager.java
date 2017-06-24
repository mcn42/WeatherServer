/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import org.mnilsen.weather.sensor.Bme280Device;
import org.mnilsen.weather.server.model.Reading;

/**
 *
 * @author michaeln
 */
public class SensorManager {
    private Bme280Device sensor = new Bme280Device();
    private long updatePeriod;
    private UpdateTask task = null;
    private Timer t = Utils.getAppTimer();
    
    public SensorManager(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }
    
    public void start()
    {
        this.task = new UpdateTask();
        this.t.schedule(task, 0L, updatePeriod);
    }
    
    public void stop()
    {
        if(this.task != null) this.task.cancel();
    }
    
    private void takeReading()
    {
        try {
            Reading r = this.sensor.takeReading();
            Log.getDataLog().info(r.toString());
            Coordinator.getInstance().acceptReading(r);
        } catch (I2CFactory.UnsupportedBusNumberException | IOException ex) {
            Log.getLog().log(Level.SEVERE,"Sensor read error:",ex);
        }
    }

    class UpdateTask extends TimerTask {

        public UpdateTask() {
        }

        @Override
        public void run() {
            takeReading();
        }
    }
    
    
}

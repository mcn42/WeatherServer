/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import java.util.concurrent.atomic.AtomicReference;
import org.mnilsen.weather.aws.AwsIotClient;
import org.mnilsen.weather.sensor.WaterSensor;
import org.mnilsen.weather.server.model.Reading;
import org.mnilsen.weather.server.model.ReadingHistory;

/**
 *
 * @author michaeln
 */
public class Coordinator {
    
    private static Coordinator instance = null;
    
    public static void intialize(boolean showHistory, boolean saveToMongo, int historyLengthDays, long diplayRefreshMillis, long sensorUpdateMillis, String mongoHost, int mongoPort)
    {
        if(saveToMongo)
        {
            instance = new Coordinator(showHistory,historyLengthDays, diplayRefreshMillis, sensorUpdateMillis, mongoHost, mongoPort);
        } else {
            instance = new Coordinator(showHistory, historyLengthDays, diplayRefreshMillis, sensorUpdateMillis);
        }
    }
    
    private SensorManager sensorMgr = null;
    private DisplayManager displayMgr = null;
    private MongoDS database = null;
    private HistoryManager historyMgr = null;
    private WebServer webServer = new WebServer();
    private ProcessManager display = new ProcessManager();
    private AwsIotClient awsMgr = new AwsIotClient();
    private WaterSensor water = new WaterSensor();
    private final AtomicReference<Reading> currentReading;
    private final AtomicReference<ReadingHistory> currentHistory;
    private boolean saveToMongo = false;

    public Coordinator(boolean showHistory,int historyLengthDays, long diplayRefreshMillis, long sensorUpdateMillis, String mongoHost, int mongoPort) {
        this.currentReading = new AtomicReference<>();
        this.currentHistory = new AtomicReference<>();
        this.saveToMongo = true;
        this.database = new MongoDS();
        this.sensorMgr = new SensorManager(diplayRefreshMillis);
        this.historyMgr = new HistoryManager(historyLengthDays);
        Log.getLog().severe("Coordinator created");
        //this.displayMgr = new DisplayManager(0, showHistory);
    }
    
    public Coordinator(boolean showHistory, int historyLengthDays, long diplayRefreshMillis, long sensorUpdateMillis) {
        this.currentReading = new AtomicReference<>();
        this.currentHistory = new AtomicReference<>();
        this.saveToMongo = false;
        //this.database = new MongoDS();
        this.sensorMgr = new SensorManager(diplayRefreshMillis);
        this.historyMgr = new HistoryManager(historyLengthDays);
        Log.getLog().severe("Coordinator created");
        //this.displayMgr = new DisplayManager(diplayRefreshMillis, showHistory);
    }
    
    public void start()
    {
        this.sensorMgr.start();
        //this.displayMgr.start();
        this.water.startMonitor();
        this.display.start();
        this.awsMgr.start();
        this.webServer.start();
        
        
    }
    
    public void stop()
    {
        //this.displayMgr.stop();
        this.awsMgr.stop();
        this.display.stop();
        this.water.stopMonitor();
        this.sensorMgr.stop();
        this.webServer.stop();
    }
    
    public void acceptReading(Reading r)
    {
        r.setWaterPresent(!this.water.getLastReading());
        this.currentReading.lazySet(r);
        this.historyMgr.addReading(r);
        if(this.saveToMongo)
          this.database.saveReading(r);
    }
    
    public void acceptHistory(ReadingHistory h)
    {
        this.currentHistory.lazySet(h);
    }

    public static Coordinator getInstance() {
        return instance;
    }

    public Reading getCurrentReading() {
        return currentReading.get();
    }

    public ReadingHistory getCurrentHistory() {
        return currentHistory.get();
    }

    public boolean isSaveToMongo() {
        return saveToMongo;
    }
    
    
}

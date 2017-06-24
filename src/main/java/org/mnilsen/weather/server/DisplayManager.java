/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import java.text.DateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author michaeln
 */
public class DisplayManager {

    private final long refreshPeriodMillis;
    private final Timer t = Utils.getAppTimer();
    private State currentState = State.DATE_TIME;
    private RefreshTask task = null;

    //private OLEDDisplay display = null;
 //   private Display display = null;
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private I2CBus bus = null;
    private I2CDevice device = null;

    private boolean showHistory = true;

    public enum State {
        DATE_TIME,
        CURRENT,
        AVERAGE,
        HIGH,
        LOW;
    }

    public DisplayManager(long refreshPeriodMillis, boolean showHistory) {
//        String msg = String.format("Starting Display Manager with refreshPeriod=%s and showHistory=%s", refreshPeriodMillis, showHistory);
//        Log.getLog().info(msg);
        this.refreshPeriodMillis = refreshPeriodMillis;
        this.showHistory = showHistory;
//        try {
//            bus =  I2CFactory.getInstance(I2CBus.BUS_1);
//            this.display = new Display(128, 64, GpioFactory.getInstance(),
//                    bus, 0x3c);
//        } catch (I2CFactory.UnsupportedBusNumberException | IOException | ReflectiveOperationException ex) {
//            Log.getLog().log(Level.SEVERE, "Display Startup error", ex);
//        }
        
    }

    public long getRefreshPeriodMillis() {
        return refreshPeriodMillis;
    }

    public void start() {
        this.task = new RefreshTask();
        this.t.schedule(task, 0L, refreshPeriodMillis);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
        }
    }

    private State getNextState() {
        State next = State.DATE_TIME;

        switch (this.currentState) {
            case DATE_TIME:
                next = State.CURRENT;
                break;
            case CURRENT:
                next = this.showHistory ? State.AVERAGE : State.DATE_TIME;
                break;
            case AVERAGE:
                next = State.HIGH;
                break;
            case HIGH:
                next = State.LOW;
                break;
            case LOW:
                next = State.DATE_TIME;
                break;
        }
        return next;
    }

    private void refreshDisplay() {
//        if (display != null) {
//            Log.getLog().log(Level.INFO, "Display mode {0}", this.currentState);
//            display.clear();
//
//            display.getGraphics().setColor(Color.WHITE);
//
//            display.getGraphics().setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
//            display.getGraphics().drawString(df.format(new Date()), 10, 10);
//            //display.getGraphics().drawRect(0, 0, display.getWidth() - 1, display.getHeight() - 1);
//            // Deal with the image using AWT
//
//            display.displayImage();
////           display.drawStringCentered(df.format(new Date()), Font.FONT_5X8, 10, true);
////           try {
////               display.update();
////           } catch (IOException ex) {
////               Log.getLog().log(Level.SEVERE, "Display update failure", ex);
////           }
//        } else {
//            Log.getLog().warning("Display ref was NULL");
//        }
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            currentState = getNextState();
            refreshDisplay();
        }

    }
}

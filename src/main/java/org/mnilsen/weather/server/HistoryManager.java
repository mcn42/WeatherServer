/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.mnilsen.weather.server.model.Reading;
import org.mnilsen.weather.server.model.ReadingHistory;

/**
 *
 * @author michaeln
 */
public class HistoryManager {

    private long startTime;
    private long lastUpdate;
    private final int refreshPeriod = 5;
    private final int cleanupPeriod = 10;
    
    
    private final int historyLengthDays; 
    private final long historyLengthMillis;

    private final AtomicReference<ReadingHistory> currentHistory = new AtomicReference<>(new ReadingHistory());

    private LinkedList<Reading> readings = new LinkedList<>();

    public HistoryManager(int historyLengthDays) {
        this.historyLengthDays = historyLengthDays;
        historyLengthMillis = Utils.daysToMillis(historyLengthDays);
        this.startTime = System.currentTimeMillis();
    }

    public void addReading(Reading r) {
        this.readings.add(r);
        if (this.readings.size() % this.refreshPeriod == 0) {
            this.refreshValues();
        }
        if (this.readings.size() % this.cleanupPeriod == 0) {
            this.cleanUpList();
        }
    }

    private void refreshValues() {

        ReadingHistory rh = new ReadingHistory();

        rh.setAvgTempF(this.readings.stream().collect(Collectors.averagingDouble((s) -> s.getTempF())));
        rh.setAvgTempC(this.readings.stream().collect(Collectors.averagingDouble((s) -> s.getTempC())));
        rh.setAvgPressure(this.readings.stream().collect(Collectors.averagingDouble((s) -> s.getPressure())));
        rh.setAvgHumidity(this.readings.stream().collect(Collectors.averagingDouble((s) -> s.getHumidity())));

        rh.setHighTempF(this.readings.stream().mapToDouble((s) -> s.getTempF()).max().getAsDouble());
        rh.setHighTempC(this.readings.stream().mapToDouble((s) -> s.getTempC()).max().getAsDouble());
        rh.setHighPressure(this.readings.stream().mapToDouble((s) -> s.getPressure()).max().getAsDouble());
        rh.setHighHumidity(this.readings.stream().mapToDouble((s) -> s.getHumidity()).max().getAsDouble());

        rh.setLowTempF(this.readings.stream().mapToDouble((s) -> s.getTempF()).min().getAsDouble());
        rh.setLowTempC(this.readings.stream().mapToDouble((s) -> s.getTempC()).min().getAsDouble());
        rh.setLowPressure(this.readings.stream().mapToDouble((s) -> s.getPressure()).min().getAsDouble());
        rh.setLowHumidity(this.readings.stream().mapToDouble((s) -> s.getHumidity()).min().getAsDouble());

        this.lastUpdate = System.currentTimeMillis();
        rh.setTimestamp(this.lastUpdate);
        this.currentHistory.lazySet(rh);
        Coordinator.getInstance().acceptHistory(rh);
    }

    private void cleanUpList() {
        long cutoff  = System.currentTimeMillis() - this.historyLengthMillis;
        List<Reading> lst = this.readings.stream().filter((r) -> r.getTimestamp() > cutoff).collect(Collectors.toList());
        this.readings = new LinkedList(lst);
    }

    public ReadingHistory getCurrentHistory() {
        return this.currentHistory.get();
    }

    public int getReadingCount() {
        return this.readings.size();
    }

    public long getStartTime() {
        return startTime;
    }

    public LinkedList<Reading> getReadings() {
        return readings;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

}

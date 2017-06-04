/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.mnilsen.weather.server.Utils;

/**
 *
 * @author michaeln
 */
@XmlRootElement
public class ReadingHistory {

    private long timestamp;

    private double lowTempC;
    private double highTempC;
    private double avgTempC;
    private double lowTempF;
    private double highTempF;
    private double avgTempF;
    private double lowHumidity;
    private double highHumidity;
    private double avgHumidity;
    private double lowPressure;
    private double highPressure;
    private double avgPressure;

    public ReadingHistory() {
    }

    public ReadingHistory(long timestamp, double lowTempC, double highTempC, double avgTempC, double lowTempF, double highTempF, double avgTempF, double lowHumidity, double highHumidity, double avgHumidity, double lowPressure, double highPressure, double avgPressure) {
        this.timestamp = timestamp;
        this.lowTempC = lowTempC;
        this.highTempC = highTempC;
        this.avgTempC = avgTempC;
        this.lowTempF = lowTempF;
        this.highTempF = highTempF;
        this.avgTempF = avgTempF;
        this.lowHumidity = lowHumidity;
        this.highHumidity = highHumidity;
        this.avgHumidity = avgHumidity;
        this.lowPressure = lowPressure;
        this.highPressure = highPressure;
        this.avgPressure = avgPressure;
    }

    public double getLowTempC() {
        return lowTempC;
    }

    public double getHighTempC() {
        return highTempC;
    }

    public double getAvgTempC() {
        return avgTempC;
    }

    public double getLowTempF() {
        return lowTempF;
    }

    public double getHighTempF() {
        return highTempF;
    }

    public double getAvgTempF() {
        return avgTempF;
    }

    public double getLowHumidity() {
        return lowHumidity;
    }

    public double getHighHumidity() {
        return highHumidity;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public double getLowPressure() {
        return lowPressure;
    }

    public double getHighPressure() {
        return highPressure;
    }

    public double getAvgPressure() {
        return avgPressure;
    }

    public void setLowTempC(double lowTempC) {
        this.lowTempC = lowTempC;
    }

    public void setHighTempC(double highTempC) {
        this.highTempC = highTempC;
    }

    public void setAvgTempC(double avgTempC) {
        this.avgTempC = avgTempC;
    }

    public void setLowTempF(double lowTempF) {
        this.lowTempF = lowTempF;
    }

    public void setHighTempF(double highTempF) {
        this.highTempF = highTempF;
    }

    public void setAvgTempF(double avgTempF) {
        this.avgTempF = avgTempF;
    }

    public void setLowHumidity(double lowHumidity) {
        this.lowHumidity = lowHumidity;
    }

    public void setHighHumidity(double highHumidity) {
        this.highHumidity = highHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public void setLowPressure(double lowPressure) {
        this.lowPressure = lowPressure;
    }

    public void setHighPressure(double highPressure) {
        this.highPressure = highPressure;
    }

    public void setAvgPressure(double avgPressure) {
        this.avgPressure = avgPressure;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReadingHistory other = (ReadingHistory) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReadingHistory{" + "tstamp=" + Utils.getTimestampString(timestamp) + ", lowTempC=" + lowTempC + ", highTempC=" + highTempC + ", avgTempC=" + avgTempC + ", lowTempF=" + lowTempF + ", highTempF=" + highTempF + ", avgTempF=" + avgTempF + ", lowHumidity=" + lowHumidity + ", highHumidity=" + highHumidity + ", avgHumidity=" + avgHumidity + ", lowPressure=" + lowPressure + ", highPressure=" + highPressure + ", avgPressure=" + avgPressure + '}';
    }

}

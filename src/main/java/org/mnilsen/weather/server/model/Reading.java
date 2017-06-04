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
public class Reading {
    private long timestamp;
    private double humidity;
    private double tempC;
    private double tempF;
    private double pressure;

    public Reading() {
    }

    public Reading(long timestamp,double humidity, double tempC, double tempF, double pressure) {
        this.timestamp = timestamp;
        this.humidity = humidity;
        this.tempC = tempC;
        this.tempF = tempF;
        this.pressure = pressure;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestsamp) {
        this.timestamp = timestsamp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTempC() {
        return tempC;
    }

    public void setTempC(double tempC) {
        this.tempC = tempC;
    }

    public double getTempF() {
        return tempF;
    }

    public void setTempF(double tempF) {
        this.tempF = tempF;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
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
        final Reading other = (Reading) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Reading{" + "tstamp=" + Utils.getTimestampString(timestamp) + ", humidity=" + humidity + ", tempC=" + tempC + ", tempF=" + tempF + ", pressure=" + pressure + '}';
    }
    
}

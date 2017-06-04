/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

/**
 *
 * @author michaeln
 */
public enum AppProperty {
    USE_DATABASE("false"),
    MONGO_HOST("localhost"),
    MONGO_PORT("27017"),
    SENSOR_POLLING_PERIOD_MILLIS("60000"),
    DISPLAY_UPDATE_PERIOD("30000"),
    DISPLAY_ENABLED("true"),
    DISPLAY_HISTORY("true"),
    LOG_DIRECTORY("/home/pi/weather/logs"),
    HISTORY_LENGTH_DAYS("30");
    
    private final String defaultValue;
    
    private AppProperty(String defaultVal)
    {
        this.defaultValue = defaultVal;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    
    
}

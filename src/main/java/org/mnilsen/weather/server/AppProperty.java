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
    HISTORY_LENGTH_DAYS("30"),
    DISPLAY_SCRIPT_NAME("display.py"),
    APPLICATION_HOME_DIR("/home/pi/weather"),
    AWS_CLIENT_ID("weather-station-1"),
    AWS_CLIENT_ENDPOINT("a2qgyw4trswo49.iot.us-east-1.amazonaws.com"),
    AWS_CERT_FILE("/home/pi/aws/WeatherStation1.cert.pem"),
    AWS_PK_FILE("/home/pi/aws/WeatherStation1.private.key"),
    AWS_UPDATE_TOPIC("$aws/things/WeatherStation1/shadow/update"),
    AWS_UPDATE_ACCEPTED_TOPIC("$aws/things/WeatherStation1/shadow/update/accepted"),
    AWS_UPDATE_REJECTED_TOPIC("$aws/things/WeatherStation1/shadow/update/rejected"),
    AWS_UPDATE_PERIOD("300000"),
    AWS_ACCESS_KEY("057YCWB3WW96S7YK15R2"),
    AWS_SECRET_KEY("bhoP0POveY3Z3E5EPq4OXXA2wmPIgFIs0a5iFLFx"),
    WATER_SENSOR_PIN("4"), 
    WATER_MONITOR_PERIOD("300000"),
    ALERT_SUPRESS_PERIOD(Long.toString(60 * 60 * 1000));
    
    private final String defaultValue;
    
    private AppProperty(String defaultVal)
    {
        this.defaultValue = defaultVal;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    
    
}

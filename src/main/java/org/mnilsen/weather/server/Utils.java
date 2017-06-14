/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.mnilsen.weather.server.model.Reading;
import org.mnilsen.weather.server.model.ReadingHistory;

/**
 *
 * @author mnilsen
 */
public class Utils {

    private static AppPropertyManager appProperties = null;
    private static final Timer appTimer = new Timer("AppTimer");
    private static String appDirectory = ".";
    private static JAXBContext jaxb;
    private static Marshaller marshaller;
    
    public static void config(String configPath)
    {
        appProperties = new AppPropertyManager(configPath);
        appDirectory = configPath;
        try {
            jaxb = JAXBContext.newInstance("org.mnilsen.weather.server.model",Utils.class.getClassLoader());
            marshaller = jaxb.createMarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "A JAXB init error occurred", ex);
        }
    }

    public static String getAppDirectory() {
        return appDirectory;
    }

    public static AppPropertyManager getAppProperties() {
        return appProperties;
    }
    
    public static int millisToMinutes(long millis)
    {
        return (int)TimeUnit.MILLISECONDS.toMinutes(millis);
    }
    
    public static long daysToMillis(int days)
    {
        return (long)TimeUnit.DAYS.toMillis(days);
    }

    public static Timer getAppTimer() {
        return appTimer;
    }  
    
    private static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    public static String getTimestampString(long timestamp) {
        return df.format(timestamp);
    }
    
    public static String getReadingJson(Reading r)
    {
        String res= "";
        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(r, writer);
            res = writer.toString();
        } catch (JAXBException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public static String getHistoryJson(ReadingHistory r)
    {
        String res= "";
        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(r, writer);
            res = writer.toString();
        } catch (JAXBException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }

    private Utils() {
        
    }
    
    
}

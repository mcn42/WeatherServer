/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server.model;

/**
 *
 * @author michaeln
 */
public class ObjectFactory {
    
    public Reading createReading()
    {
        Reading r = new Reading();
        
        return r;
    }
    
    public ReadingHistory createReadingHistory()
    {
        ReadingHistory r = new ReadingHistory();
        
        return r;
    }
}

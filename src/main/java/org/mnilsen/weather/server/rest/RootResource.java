/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server.rest;

/**
 *
 * @author michaeln
 */
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RootResource {

    public RootResource() {
    }
    
    @GET
    @Path("status")
    @Produces(MediaType.TEXT_HTML)
    public String test() {
        return "<html><body><h1>The Server is running...</h1></body></html>";
    }
}

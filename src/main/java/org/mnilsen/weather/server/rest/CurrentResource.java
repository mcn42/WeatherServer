/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.mnilsen.weather.server.Coordinator;
import org.mnilsen.weather.server.model.Reading;

/**
 * REST Web Service
 *
 * @author michaeln
 */
@Path("current")
public class CurrentResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CurrentResource
     */
    public CurrentResource() {
    }

    /**
     * Retrieves representation of an instance of org.mnilsen.weather.server.CurrentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Reading getJson() {
        Reading r = Coordinator.getInstance().getCurrentReading();
        return r;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import java.util.logging.Level;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author michaeln
 */
public class WebServer {

    Server jettyServer = new Server(8080);

    public WebServer() {
    }

    public void start() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "org.mnilsen.weather.server.rest");

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception ex) {
            Log.getLog().log(Level.SEVERE, "Jetty startup error", ex);
        } finally {
            jettyServer.destroy();
        }
    }

    public void stop() {
        try {
            if (this.jettyServer != null) {
                this.jettyServer.destroy();
            }
        } catch (Exception ex) {
            Log.getLog().log(Level.SEVERE, "Jetty shutdown error", ex);
        }

    }
}

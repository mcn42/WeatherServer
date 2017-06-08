/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michaeln
 */
public class ProcessManager {
    private final List<String> command = new ArrayList<>();

    private ProcessBuilder executor;
    private Process process = null;
    private int result = -1;

    public ProcessManager() {
        String scriptName = Utils.getAppProperties().get(AppProperty.DISPLAY_SCRIPT_NAME);
        String appDir = Utils.getAppProperties().get(AppProperty.APPLICATION_HOME_DIR);
        if (!appDir.endsWith("/")) {
            appDir = appDir + "/";
        }
        command.add("sudo");
        command.add(appDir + scriptName);

        executor = new ProcessBuilder(command);
        
        Log.getLog().log(Level.INFO, "Process Manager has been set up: {0}", appDir + scriptName);

    }
    
    public boolean start() {
        boolean res = false;

        Log.getLog().log(Level.INFO, "Running Display script {0}", this.executor.toString());
        Executors.newSingleThreadExecutor().execute(new RunTask());

        res = this.result == 0;

        return res;
    }

    class RunTask implements Runnable {

        @Override
        public void run() {
            try {
                process = executor.start();
                result = process.waitFor();
                Log.getLog().log(Level.INFO, "Display script return value: {0}", result);
            } catch (IOException ex) {
                Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE, "Display startup failed.", ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void stop() {
        if(this.process != null) this.process.destroy();
    }

}

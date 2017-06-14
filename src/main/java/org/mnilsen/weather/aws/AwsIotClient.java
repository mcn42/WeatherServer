/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.aws;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mnilsen.weather.server.AppProperty;
import org.mnilsen.weather.server.Coordinator;
import org.mnilsen.weather.server.Log;
import org.mnilsen.weather.server.Utils;
import org.mnilsen.weather.server.model.Reading;
import org.mnilsen.weather.server.model.ReadingHistory;

/**
 *
 * @author michaeln
 */
public class AwsIotClient {

    private final String clientEndpoint = Utils.getAppProperties().get(AppProperty.AWS_CLIENT_ENDPOINT);
    private final String clientId = Utils.getAppProperties().get(AppProperty.AWS_CLIENT_ID);
    private final String certificateFile = Utils.getAppProperties().get(AppProperty.AWS_CERT_FILE);
    private final String privateKeyFile = Utils.getAppProperties().get(AppProperty.AWS_PK_FILE);
    private final String updateTopic = Utils.getAppProperties().get(AppProperty.AWS_UPDATE_TOPIC);
    
    private AWSIotMqttClient client = null;
    private UpdateTask updateTask = null;

    public AwsIotClient() {

    }

    public void start() {
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        Long period = Utils.getAppProperties().getLong(AppProperty.AWS_UPDATE_PERIOD);
        try {
            // optional parameters can be set before connect()
            client.connect();
            this.updateTask = new UpdateTask();
            Utils.getAppTimer().schedule(updateTask, 31000L, period);

        } catch (AWSIotException ex) {
            Log.getLog().log(Level.SEVERE, "AWS Client connect error", ex);
        }
    }

    public void stop() {
        if (client != null) {
            try {
                client.disconnect();
            } catch (AWSIotException ex) {
                Log.getLog().log(Level.SEVERE, "AWS Client disconnect error", ex);
            }
        }
    }

    private void sendUpdate() {
        Reading current = Coordinator.getInstance().getCurrentReading();
        ReadingHistory history = Coordinator.getInstance().getCurrentHistory();
        String currStr = Utils.getReadingJson(current);
        String histStr = Utils.getHistoryJson(history);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("{clientId: %s, current: %s, histroy: %s}",clientId,currStr,histStr));
        
        WeatherMessage wm = new WeatherMessage(this.updateTopic,AWSIotQos.QOS0,sb.toString());
        try {
            this.client.publish(wm);
        } catch (AWSIotException ex) {
            Log.getLog().log(Level.SEVERE, "AWS Client publish update error", ex);
        }
    }

    public class WeatherMessage extends AWSIotMessage {

        public WeatherMessage(String topic, AWSIotQos qos, String payload) {
            super(topic, qos, payload);
        }

        @Override
        public void onSuccess() {
            Log.getLog().log(Level.INFO, "Publish to AWS succeeded");
        }

        @Override
        public void onFailure() {
            Log.getLog().log(Level.SEVERE, "Publish to AWS failed");
        }

        @Override
        public void onTimeout() {
            Log.getLog().log(Level.SEVERE, "Publish to AWS timed out");
        }
    }

    class UpdateTask extends TimerTask {

        @Override
        public void run() {
            sendUpdate();
        }

    }
}

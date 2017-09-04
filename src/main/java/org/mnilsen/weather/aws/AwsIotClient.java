/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mnilsen.weather.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
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
    private final String updateAccTopic = Utils.getAppProperties().get(AppProperty.AWS_UPDATE_ACCEPTED_TOPIC);
    private final String updateRejTopic = Utils.getAppProperties().get(AppProperty.AWS_UPDATE_REJECTED_TOPIC);

    private AWSIotMqttClient client = null;
    private UpdateTask updateTask = null;
    
    private Map<String, MessageAttributeValue> smsAttributes = null;
    private AmazonSNS snsClient = null;

    public AwsIotClient() {
        
    }

    public void start() {
        this.startSNS();
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        Long period = Utils.getAppProperties().getLong(AppProperty.AWS_UPDATE_PERIOD);
        try {
            Log.getLog().info(String.format("Connecting to AWS endpoint %s", this.clientEndpoint));
            client.connect();
            Log.getLog().info(String.format("Connected to AWS endpoint: %s", this.client.getConnectionStatus()));

            MyTopic mt = new MyTopic(this.updateAccTopic, AWSIotQos.QOS0);
            client.subscribe(mt);
            Log.getLog().info(String.format("Subscribed to AWS topic: %s", this.updateAccTopic));
            mt = new MyTopic(this.updateRejTopic, AWSIotQos.QOS0);
            client.subscribe(mt);
            Log.getLog().info(String.format("Subscribed to AWS topic: %s", this.updateRejTopic));

            this.updateTask = new UpdateTask();
            Utils.getAppTimer().schedule(updateTask, 31000L, period);
            Log.getLog().info(String.format("Started Update Task with period %s", period));
        } catch (AWSIotException ex) {
            Log.getLog().log(Level.SEVERE, "AWS Client connect error", ex);
        }
    }
    
    public void startSNS()
    {
        this.prepSMSAlerts();
    }

    public void stop() {
        Log.getLog().info("Stopping AWS connection");
        if (client != null) {
            try {
                client.disconnect();
            } catch (AWSIotException ex) {
                Log.getLog().log(Level.SEVERE, "AWS Client disconnect error", ex);
            }
        }
    }

    private void sendUpdate() {
        
        Log.getLog().info(String.format("Sending update to AWS topic '%s'", this.updateTopic));
        Reading current = Coordinator.getInstance().getCurrentReading();
        ReadingHistory history = Coordinator.getInstance().getCurrentHistory();
        String currStr = current == null ? "{}" : Utils.getReadingJson(current);
        String histStr = history == null ? "{}" : Utils.getHistoryJson(history);

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("{\"state\": {\"reported\": {\"current\": %s, \"history\": %s}}}", currStr, histStr));
        //Log.getLog().log(Level.INFO, "Sending JSON: {0}", sb.toString());
        WeatherMessage wm = new WeatherMessage(this.updateTopic, AWSIotQos.QOS0, sb.toString());
        try {
            this.client.publish(wm);
            Log.getLog().info("Update sent to AWS");
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

    private void prepSMSAlerts() {
        smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("Weather") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.10") //Sets the max price to 0.10 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional")
                .withDataType("String"));
        
        BasicAWSCredentials cred = new BasicAWSCredentials(Utils
                .getAppProperties().get(AppProperty.AWS_ACCESS_KEY)
                ,Utils.getAppProperties().get(AppProperty.AWS_SECRET_KEY));
        snsClient = AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred)).build();
        
//        SetSMSAttributesRequest setRequest = new SetSMSAttributesRequest()
//			.addAttributesEntry("DefaultSenderID", "WeatherSensor")
//			.addAttributesEntry("MonthlySpendLimit", "1")
//			.addAttributesEntry("DeliveryStatusIAMRole", 
//					"arn:aws:iam::234513162694:role/SNSSuccessFeedback")
//			.addAttributesEntry("DeliveryStatusSuccessSamplingRate", "10")
//			.addAttributesEntry("DefaultSMSType", "Promotional");
//	snsClient.setSMSAttributes(setRequest);
//	Map<String, String> myAttributes = snsClient.getSMSAttributes(new GetSMSAttributesRequest())
//		.getAttributes();
//	Log.getLog().info("My SMS attributes:");
//	for (String key : myAttributes.keySet()) {
//		System.out.println(key + " = " + myAttributes.get(key));
//	}
    }
    
    public void sendSMSMessage(String message,String phoneNumber) {
        PublishResult result = snsClient.publish(new PublishRequest()
                        .withMessage(message)
                        .withPhoneNumber(phoneNumber)
                        .withMessageAttributes(smsAttributes));
        Log.getLog().info(result.toString());
}

    class UpdateTask extends TimerTask {

        @Override
        public void run() {
            try {
            sendUpdate();
            } catch(Exception e) {
                Log.getLog().log(Level.SEVERE, String.format("AWS update error: %s",e.getMessage()));
            }
        }

    }

    public class MyTopic extends AWSIotTopic {

        public MyTopic(String topic, AWSIotQos qos) {
            super(topic, qos);
        }

        @Override
        public void onMessage(AWSIotMessage message) {
            Log.getLog().log(Level.INFO, String.format("AWS Message received- topic: %s, payload: %s", message.getTopic(), message.getStringPayload()));
        }
    }
    
    public static void main(String[] args)
    {
        Utils.config("test.properties");
        AwsIotClient client = new AwsIotClient();
        client.startSNS();
        client.sendSMSMessage("This is a test...", "+9086564206");
    }
}

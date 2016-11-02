/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappclient;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author vanyadeasy
 */
public class MessagingApp {
    private static final String SERVER_QUEUE_NAME = "server_queue";
    private Connection connection = null;
    private Channel channel;
    private String queueName;
    private QueueingConsumer consumer;

    public MessagingApp() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            consumer = new QueueingConsumer(channel);
        } catch (IOException ex) {
            Logger.getLogger(MessagingApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(MessagingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String call(String message) throws Exception {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                                    .Builder()
                                    .correlationId(corrId)
                                    .replyTo(queueName)
                                    .build();

        channel.basicPublish("", SERVER_QUEUE_NAME, props, message.getBytes("UTF-8"));

        while (true) {
          QueueingConsumer.Delivery delivery = consumer.nextDelivery();
          if (delivery.getProperties().getCorrelationId().equals(corrId)) {
            response = new String(delivery.getBody(),"UTF-8");
            break;
          }
        }
        return response;
    }

    public void close() throws Exception {
        connection.close();
    }
    
    public void createQueue(String queueName) {
        this.queueName = queueName;
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException ex) {
            Logger.getLogger(MessagingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

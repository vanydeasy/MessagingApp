/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappserver;

import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class MessagingAppServer {
    private static final String QUEUE_NAME = "server_queue";
    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private final String SIGNUP = "signup";
    private final String LOGIN = "login";
    private final String CREATE_GROUP = "create_group";
    private final String LEAVE_GROUP = "leave_group";
    private final String ADD_FRIEND = "add_friend";
    private final String ADD_GROUP_MEMBER = "add_member";
    
    public static void main(String[] argv) {
        MessagingAppServer server = new MessagingAppServer();
        Connection connection = null;
        Channel channel = null;
        JSONParser parser = new JSONParser();
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.basicQos(1);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(QUEUE_NAME, false, consumer);

            System.out.println(" [x] Awaiting requests");

            while (true) {
                String response = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();
                BasicProperties replyProps = new BasicProperties
                                                .Builder()
                                                .correlationId(props.getCorrelationId())
                                                .build();

                try {
                    JSONObject message = (JSONObject)parser.parse(new String(delivery.getBody(),"UTF-8"));
                    response = server.doCommand(message).toJSONString();
                }
                catch (UnsupportedEncodingException | ParseException e){
                    System.out.println(" [.] " + e.toString());
                    response = "";
                }
                finally {
                    channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }
        } catch(IOException | TimeoutException | InterruptedException | ShutdownSignalException | ConsumerCancelledException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignore) {}
            }
        }
    }
    
    public JSONObject doCommand(JSONObject request) {
        JSONObject response = new JSONObject();
        response.put("command", request.get("command"));
        String command = request.get("command").toString();        
        System.out.println(">> Command: "+command);
        String username = null;
        switch(command) {
            case SIGNUP:
                username = request.get("username").toString();
                String password = request.get("password").toString();
                if(dbHelper.insertUser(username,password)) {
                    response.put("status", true);
                    response.put("message", "Signup has been successful.");
                }
                else {
                    response.put("status", false);
                    response.put("message", "Username already exists.");
                }
                break;
            case LOGIN:
                username = request.get("username").toString();
                String pw = request.get("password").toString();
                JSONObject user = dbHelper.selectUser(username);
                if (!user.isEmpty()) {
                    if (user.get("username").equals(username) && user.get("password").equals(pw)) {
                        response.put("status", true);
                        response.put("message", "Login has been successful!");
                    } else {
                        response.put("status", false);
                        response.put("message", "Your username and password does not match!");
                    }
                } else {
                    response.put("status", false);
                    response.put("message", "You are not registered!");
                }
                break;
            case LEAVE_GROUP:
                int groupId = Integer.parseInt(request.get("group_id").toString());
                username = request.get("username").toString();
                if(dbHelper.removeGroupMember(groupId, username)) {
                    response.put("status", true);
                    response.put("message", " You left the group.");
                }
                else {
                    response.put("status", false);
                    response.put("message", "Failed.");
                }
                break;
            case CREATE_GROUP:
                username = request.get("username").toString();
                String groupName = request.get("group_name").toString();
                JSONParser parser = new JSONParser();
                JSONArray members = null;
                {
                    try {
                        members = (JSONArray)parser.parse(request.get("members").toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(dbHelper.createNewGroup(groupName, username, members)) {
                    response.put("status", true);
                    response.put("message", "The group has been created.");
                }
                else {
                    response.put("status", false);
                    response.put("message", "Failed.");
                }
                break;
            case ADD_FRIEND:
                username = request.get("username").toString();
                String friendName = request.get("friend_name").toString();
            case ADD_GROUP_MEMBER:
                break;
            default:
                response.put("status", false);
                response.put("message", "Command is unrecognizable.");
                break;
        }
        
        return response;
    }
}

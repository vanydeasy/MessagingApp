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
    private final String GET_GROUP = "get_group";
    
    public static void main(String[] argv) {
        MessagingAppServer server = new MessagingAppServer();
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = null;
        factory.setHost("localhost");
        JSONParser parser = new JSONParser();
        try {
            connection = factory.newConnection();
        } catch (IOException ex) {
            Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) 
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    JSONParser parser = new JSONParser();
                    JSONObject jsonMessage = null;
                    try {
                        jsonMessage = (JSONObject) parser.parse(message);
                    } catch (ParseException ex) {
                        Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("[x] Received '" + message + "'");
                    try {
                        channel.basicPublish("", jsonMessage.get("username").toString(), null, server.doCommand(jsonMessage).toJSONString().getBytes("UTF-8"));
                    } catch (IOException ex) {
                        Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch(IOException | ShutdownSignalException | ConsumerCancelledException e) {
            e.printStackTrace();
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
                if(dbHelper.addFriend(username, friendName)) {
                    response.put("status", true);
                    response.put("message", "A friend has been added.");
                }
                else {
                    response.put("status", false);
                    response.put("message", "A friend cannot be added");
                }
                break;
            case GET_GROUP:
                username = request.get("username").toString();
                JSONArray groups = dbHelper.selectGroupByUser(username);
                response.put("status", true);
                response.put("groups", groups);
                if(groups.size()==0)
                    response.put("message", "User does not join on any groups.");
                else
                    response.put("message", "Groups have been received.");
                break;
            default:
                response.put("status", false);
                response.put("message", "Command is unrecognizable.");
                break;
        }
        
        return response;
    }
}

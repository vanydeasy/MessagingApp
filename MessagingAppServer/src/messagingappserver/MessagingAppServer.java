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
import java.io.IOException;
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
    private final String GET_FRIEND = "get_friend";
    private final String ADD_GROUP_MEMBER = "add_member";
    private final String CHAT_FRIEND = "chat_friend";
    private final String CHAT_GROUP = "chat_group";
    
    private static Channel channel;
    public static void main(String[] argv) {
        MessagingAppServer server = new MessagingAppServer();
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = null;
        
        factory.setHost("localhost");
        JSONParser parser = new JSONParser();
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("[*] Waiting for messages. To exit press CTRL+C");

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
        System.out.println("[!] Command: "+command);
        String username;
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
                String group_name = request.get("group_name").toString();
                username = request.get("username").toString();
                JSONArray groupList = dbHelper.selectGroupByUser(username);
                if(groupList.size()==0) {
                    response.put("status", false);
                    response.put("message", "User does not join on any groups.");
                } else {
                    int group_id = 0;
                    for (int i=0; i<groupList.size(); i++) {
                        JSONParser parse = new JSONParser();
                        JSONObject temp = new JSONObject();
                        try {
                            temp = (JSONObject)parse.parse(groupList.get(i).toString());
                            System.out.println(temp);
                            if (temp.get("name").toString().equals(group_name)) {
                                group_id = Integer.parseInt(temp.get("group_id").toString());
                                System.out.println(group_id);
                                if(dbHelper.removeGroupMember(group_id, username)) {
                                    response.put("status", true);
                                    response.put("message", " You left the group.");
                                }
                                else {
                                    response.put("status", false);
                                    response.put("message", "Failed.");
                                }
                                break;
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
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
                    response.put("message", username+" added you as a friend.");
                    try {
                        channel.basicPublish("", friendName, null, response.toJSONString().getBytes("UTF-8"));
                    } catch (IOException ex) {
                        Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
                if(groups.isEmpty())
                    response.put("message", "User does not join on any groups.");
                else
                    response.put("message", "Groups have been received.");
                break;
            case CHAT_FRIEND:
                username = request.get("username").toString();
                String friend = request.get("friend_name").toString();
                String message = request.get("message").toString();
                if (!dbHelper.isFriend(username, friend)) {
                    response.put("status", false);
                    response.put("message", "You are not friends yet.");
                } else {
                    if (dbHelper.insertPersonalMessage(username, friend, message)) {
                        try {
                            channel.basicPublish("", friend, null, request.toJSONString().getBytes("UTF-8"));
                            channel.basicPublish("", username, null, request.toJSONString().getBytes("UTF-8"));
                        } catch (IOException ex) {
                            Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        response.put("status", true);
                        response.put("message", "Your message has been sent.");
                    }
                }
                break;
            case CHAT_GROUP:
                username = request.get("username").toString();
                String groupname = request.get("group_name").toString();
                String content = request.get("message").toString();
                JSONArray user_groups = dbHelper.selectGroupByUser(username);
                if (user_groups.size()>0) {
                    int id = 0;
                    for (int i=0; i<user_groups.size(); i++) {
                        JSONParser parse = new JSONParser();
                        JSONObject temp = new JSONObject();
                        try {
                            temp = (JSONObject)parse.parse(user_groups.get(i).toString());
                        } catch (ParseException ex) {
                            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (temp.get("name").equals(groupname)) {
                            id = Integer.parseInt(temp.get("group_id").toString());
                            break;
                        }
                    }
                    JSONArray users = dbHelper.selectUserOnGroup(id);
                    for (int j=0; j<users.size(); j++) {
                        try {
                            channel.basicPublish("", users.get(j).toString(), null, request.toJSONString().getBytes("UTF-8"));
                        } catch (Exception ex) {
                            Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    response.put("status", true);
                    response.put("message", "Your message has been sent.");
                } else {
                    response.put("status", false);
                    response.put("message", "Your message cannot be sent.");
                }
                break;
            case ADD_GROUP_MEMBER:
                username = request.get("username").toString();
                String group = request.get("group_name").toString();
                JSONParser parse = new JSONParser();
                JSONArray member = null;
                {
                    try {
                        member = (JSONArray)parse.parse(request.get("members").toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(MessagingAppServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(dbHelper.addGroupMember(group, username, member)) {
                    response.put("status", true);
                    response.put("message", "Successfully added new member(s).");
                }
                else {
                    response.put("status", false);
                    response.put("message", "You are not admin of this group.");
                }
                break;
            case GET_FRIEND:
                username = request.get("username").toString();
                JSONArray friends = dbHelper.getFriendsByUser(username);
                response.put("status", true);
                response.put("friends", friends);
                if(friends.isEmpty()) {
                    response.put("message", "User does not have any friend.");
                } else {
                    response.put("message", "Friends List have been received.");
                }
                break;
            default:
                response.put("status", false);
                response.put("message", "Command is unrecognizable.");
                break;
        }
        
        return response;
    }
}

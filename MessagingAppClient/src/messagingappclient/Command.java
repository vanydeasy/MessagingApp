/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappclient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author vanyadeasy
 */
public class Command {
    public static JSONObject signup(String username, String password) {
        JSONObject result = new JSONObject();
        result.put("command", "signup");
        result.put("username", username);
        result.put("password", password);
        
        return result;
    }
    
    public static JSONObject addFriend(String username, String friend) {
        JSONObject result = new JSONObject();
        result.put("command", "add_friend");
        result.put("username", username);
        result.put("friend_name", friend);
        
        return result;
    }
    
    public static JSONObject createGroup(String username, String groupName, JSONArray members) {
        JSONObject result = new JSONObject();
        result.put("command", "create_group");
        result.put("username", username);
        result.put("group_name", groupName);
        result.put("members", members);
        
        return result;
    }
    
    public static JSONObject leaveGroup(String groupName, String username) {
        JSONObject result = new JSONObject();
        result.put("command", "leave_group");
        result.put("group_name", groupName);
        result.put("username", username);
        
        return result;
    }
   
   public static JSONObject login(String username, String password) {
        JSONObject result = new JSONObject();
        result.put("command", "login");
        result.put("username", username);
        result.put("password", password);
        
        return result;
    }
   
    public static JSONObject getGroup(String username) {
        JSONObject result = new JSONObject();
        result.put("command", "get_group");
        result.put("username", username);

        return result;
    }
    
    public static JSONObject getFriend(String username) {
        JSONObject result = new JSONObject();
        result.put("command", "get_friend");
        result.put("username", username);
        
        return result;
    }

    public static JSONObject addGroupMember(String username, String groupName, JSONArray members) {
        JSONObject result = new JSONObject();
        result.put("command", "add_member");
        result.put("username", username);
        result.put("group_name", groupName);
        result.put("members", members);
        
        return result;
    }
    
    public static JSONObject chatFriend(String username, String friendName, String content) {
        JSONObject result = new JSONObject();
        result.put("command", "chat_friend");
        result.put("username", username);
        result.put("friend_name", friendName);
        result.put("message", content);
        
        return result;
    }
    
    public static JSONObject chatGroup(String username, String groupName, String content) {
        JSONObject result = new JSONObject();
        result.put("command", "chat_group");
        result.put("username", username);
        result.put("group_name", groupName);
        result.put("message", content);
        
        return result;
    }
}

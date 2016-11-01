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
        result.put("add", friend);
        
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
    
    public static JSONObject leaveGroup(int groupId, String username) {
        JSONObject result = new JSONObject();
        result.put("command", "leave_group");
        result.put("group_id", groupId);
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
}

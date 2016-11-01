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
    
    public static JSONObject createNewGroup(String username, String groupName) {
        JSONObject result = new JSONObject();
        result.put("command", "create_group");
        result.put("username", username);
        result.put("group_name", groupName);
        
        return result;
    }
    
    public static JSONObject addGroupMember(String username, JSONArray member) {
        JSONObject result = new JSONObject();
        result.put("command", "add_member");
        result.put("username", username);
        result.put("member", member);
        
        return result;
    }
    
    public static JSONObject removeMember(int groupId, String username, String removedMember) {
        JSONObject result = new JSONObject();
        result.put("command", "remove_member");
        result.put("group_id", groupId);
        result.put("username", username);
        result.put("removed_member", removedMember);
        
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

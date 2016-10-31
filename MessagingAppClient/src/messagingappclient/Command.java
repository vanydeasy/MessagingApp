/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappclient;

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
}

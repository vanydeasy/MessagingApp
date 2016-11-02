/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author vanyadeasy
 */
public class MessagingAppClient {
    public static void main(String[] argv) {
        MessagingApp client = new MessagingApp();
        JSONObject response = null;
        // Login
        do {
            response = client.login();
            System.out.println(response.get("message")+"\n");
        } while(!(Boolean)response.get("status"));
        
        System.out.println("hai");
        // Command
        try {
            JSONArray members = new JSONArray();
            members.add("pipink");
            String res = client.call(Command.createGroup("vanydeasy", "haiho", members).toJSONString());
            System.out.println(res);
        } catch (Exception ex) {
            Logger.getLogger(MessagingAppClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

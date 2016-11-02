/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappclient;

import org.json.simple.JSONObject;
import java.util.Scanner;


public class MessagingAppClient {
    public static void main(String[] argv) {
        MessagingApp client = new MessagingApp();
        JSONObject response = null;
        // Login
        do {
            response = client.login();
            System.out.println(response.get("message")+"\n");
        } while(!(Boolean)response.get("status"));
        
        Scanner scanner = new Scanner(System.in);
        // Command
        while(true) {
            System.out.println("1. Send message to friend");
            System.out.println("2. Send message to group");
            System.out.println("3. Create new group");
            System.out.println("4. Leave group");
            System.out.println("5. Add new friend");
            
            String option = scanner.next();
            switch(option) {
         
            }
        }
        
    }
}

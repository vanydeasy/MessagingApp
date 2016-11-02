/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappclient;

import org.json.simple.JSONObject;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class MessagingAppClient {
    private static final MessagingApp QUEUE_HANDLER = new MessagingApp();
    private static String username;
    
    public static void main(String[] argv) {
        JSONObject response = null;
        // Login
        do {
            response = login();
            System.out.println(response.get("message")+"\n");
        } while(!(Boolean)response.get("status"));
        
        Scanner scanner = new Scanner(System.in);
        
        // Command
        Integer option;
        do {
            System.out.println("1. Send message to friend");
            System.out.println("2. Send message to group");
            System.out.println("3. Create new group");
            System.out.println("4. Leave group");
            System.out.println("5. Add new friend");
            System.out.println("6. Exit");
            
            option = scanner.nextInt();
            switch(option) {
                case 1: // Send message to friend
                    
                    break;
                case 2: // Send message to group
                    
                    break;
                case 3: // Create new group
                    response = createNewGroup();
                    break;
                case 4: // Leave group
                    
                    break;
                case 5: // Add new friend
                    response = addFriend();
                    break;
                case 6:
                    response = null;
                    break;
                default:
                    response = null;
                    System.out.println("Command is unrecognizable. Try again.");
                    break;
            }
            if(response != null) System.out.println(">> "+response.get("message"));
        } while(option != 6);
        try {
            QUEUE_HANDLER.close();
        } catch (Exception ex) {
            Logger.getLogger(MessagingAppClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static JSONObject stringToJson(String input) {
        JSONObject message = null;
        try {
            JSONParser parser = new JSONParser();
            message = (JSONObject)parser.parse(input);
        } catch (ParseException ex) {
            Logger.getLogger(MessagingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }
    
    public static JSONObject login() {
        Scanner reader = new Scanner(System.in);
        
        System.out.print("Do you want to login with an existing user?(Y/N) ");
        String option = reader.next();
        System.out.print("Username: ");
        username = reader.next();
        System.out.print("Password: ");
        String password = reader.next();
        
        QUEUE_HANDLER.createQueue(username);
        
        String response = null;
        try {
            if(option.equals("Y")) {
                response = QUEUE_HANDLER.call(Command.login(username, password).toJSONString());
            }
            else {
                response = QUEUE_HANDLER.call(Command.signup(username, password).toJSONString());
            }
        } catch (Exception ex) {
            Logger.getLogger(MessagingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringToJson(response);
    }
    
    public static JSONObject createNewGroup() {
        Scanner reader = new Scanner(System.in);
        
        System.out.print("Insert group name: ");
        String groupName = reader.next();
        System.out.print("Do you want to add other members to the group?(Y/N) ");
        JSONArray members = new JSONArray();
        if(reader.next().equals("Y")) {
            while(true) {
                // TODO: tampilkan list friend
                String input = reader.next();
                if(input.equals("quit")) break;
                members.add(input);
            }
        }
        String response = null;
        try {
            response = QUEUE_HANDLER.call(Command.createGroup(username, groupName, members).toJSONString());
        } catch (Exception ex) {
            Logger.getLogger(MessagingAppClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringToJson(response);
    }
    
    public static JSONObject addFriend() {
        Scanner reader = new Scanner(System.in);
        
        System.out.print("Add by username: ");
        String friend = reader.next();
        String response = null;
        try {
            response = QUEUE_HANDLER.call(Command.addFriend(username, friend).toJSONString());
        } catch (Exception ex) {
            Logger.getLogger(MessagingAppClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringToJson(response);
    }
}

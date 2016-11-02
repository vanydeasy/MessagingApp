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

public class MessagingAppClient {
    private static final MessagingApp QUEUE_HANDLER = new MessagingApp();
    private static String username;
    private static boolean isLoggedIn = false;
    private static boolean flag = false;
    
    public static void main(String[] argv) {
        JSONObject response = null;
        Scanner scanner = new Scanner(System.in);
        
        // Command
        Integer option;
        Scanner reader = new Scanner(System.in);
        System.out.println("0. Login/Signup");
        System.out.println("1. Send message to friend");
        System.out.println("2. Send message to group");
        System.out.println("3. Create new group");
        System.out.println("4. Leave group");
        System.out.println("5. Add new friend");
        System.out.println("6. Get groups");
        System.out.println("7. Get friends");
        System.out.println("8. Exit");
        do {
            System.out.print("[] "); option = scanner.nextInt();
            if(option == 0) {
                login();
            }
            else {
                if(!QUEUE_HANDLER.isLoggedIn) {
                    System.out.println("You need to login first!");
                }
                else {
                    switch(option) {
                        case 1: // Send message to friend
                            
                            break;
                        case 2: // Send message to group

                            break;
                        case 3: // Create new group
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
                            QUEUE_HANDLER.send(Command.createGroup(username, groupName, members).toJSONString());
                            break;
                        case 4: // Leave group
                            System.out.print("No. group: ");
                            Integer groupId = reader.nextInt();
                            QUEUE_HANDLER.send(Command.leaveGroup(Integer.valueOf(groupId), username).toJSONString());
                            break;
                        case 5: // Add new friend
                            System.out.print("Add by username: ");
                            String friend = reader.next();
                            QUEUE_HANDLER.send(Command.addFriend(username, friend).toJSONString());
                            break;
                        case 6: // Get groups
                            QUEUE_HANDLER.send(Command.getGroup(username).toJSONString());
                            break;
                        case 7: // Get friends
                            QUEUE_HANDLER.send(Command.getFriend(username).toJSONString());
                            break;
                        default:
                            System.out.println("Command is unrecognizable. Try again.");
                            break;
                    }
                    while(!QUEUE_HANDLER.isAnswered) {System.out.print("");}
                }
            }
        } while(option < 8);
        try {
            QUEUE_HANDLER.close();
        } catch (Exception ex) {
            Logger.getLogger(MessagingAppClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void login() {
        Scanner reader = new Scanner(System.in);
        
        System.out.print("Do you want to login with an existing user?(Y/N) ");
        String option = reader.next();
        System.out.print("Username: ");
        username = reader.next();
        System.out.print("Password: ");
        String password = reader.next();
        
        QUEUE_HANDLER.listen(username);
        
        if(option.equals("Y")) {
            QUEUE_HANDLER.send(Command.login(username, password).toJSONString());
        }
        else {
            QUEUE_HANDLER.send(Command.signup(username, password).toJSONString());
        }
    }
    
    public static void addGroupMember() {
        Scanner reader = new Scanner(System.in);
        JSONArray friends = new JSONArray();
        String name = "";
        
        System.out.print("Friend's name (type \"end\" to stop): ");
        name = reader.next();
        while (!name.equals("end")) {
            friends.add(name);
            System.out.print("Friend's name (type \"end\" to stop): ");
            name = reader.next();
        }
        
        System.out.print("Enter group name: ");
        String groupName = reader.next();
        QUEUE_HANDLER.send(Command.addGroupMember(username, groupName, friends).toJSONString());
    }
}

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
    
    public static void main(String[] argv) {
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
        System.out.println("6. Add new group member");
        System.out.println("7. Get groups");
        System.out.println("8. Get friends");
        System.out.println("9. Exit");
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
                            System.out.print("Insert your friend's username: ");
                            String friend = reader.nextLine();
                            System.out.print("Type the message: ");
                            String message = reader.nextLine();
                            QUEUE_HANDLER.send(Command.chatFriend(username, friend, message).toJSONString());
                            break;
                        case 2: // Send message to group
                            System.out.print("Insert group name: ");
                            String groupName = reader.nextLine();
                            System.out.print("Type the message: ");
                            String content = reader.nextLine();
                            QUEUE_HANDLER.send(Command.chatGroup(username, groupName, content).toJSONString());
                            break;
                        case 3: // Create new group
                            System.out.print("Insert group name: ");
                            String groupname = reader.next();
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
                            QUEUE_HANDLER.send(Command.createGroup(username, groupname, members).toJSONString());
                            break;
                        case 4: // Leave group
                            System.out.print("No. group: ");
                            Integer groupId = reader.nextInt();
                            QUEUE_HANDLER.send(Command.leaveGroup(groupId, username).toJSONString());
                            break;
                        case 5: // Add new friend
                            System.out.print("Add by username: ");
                            String friendName = reader.next();
                            QUEUE_HANDLER.send(Command.addFriend(username, friendName).toJSONString());
                            break;
                        case 6:
                            addGroupMember();
                        case 7: // Get groups
                            QUEUE_HANDLER.send(Command.getGroup(username).toJSONString());
                            break;
                        case 8: // Get friends
                            QUEUE_HANDLER.send(Command.getFriend(username).toJSONString());
                            break;
                        default:
                            System.out.println("Command is unrecognizable. Try again.");
                            break;
                    }
                    while(!QUEUE_HANDLER.isAnswered) {System.out.print("");}
                }
            }
            while(!QUEUE_HANDLER.isAnswered) {System.out.print("");}
        } while(option < 9);
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
        String name;
        System.out.print("Enter group name: ");
        String groupName = reader.next();
        System.out.print("Friend's name (type \"quit\" to stop): ");
        name = reader.next();
        while (!name.equals("quit")) {
            friends.add(name);
            name = reader.next();
        }
        QUEUE_HANDLER.send(Command.addGroupMember(username, groupName, friends).toJSONString());
    }
}

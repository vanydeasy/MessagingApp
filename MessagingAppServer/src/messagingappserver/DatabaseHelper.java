/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingappserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author vanyadeasy
 */
public class DatabaseHelper {
    private Connection conn;
    // JDBC driver name and database URL
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/messaging_app";
    // Database credentials
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    public DatabaseHelper() {
        try {
            Class.forName(DRIVER);
            // Establish connection to db
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean insertUser(String username, String password) {
        try {
            if(!selectUser(username).isEmpty()) {
                System.out.println("Username already exists.");
                return false;
            }
            String query = "INSERT INTO `user`(username, password) VALUES(?,?)";
            try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
                dbStatement.setString(1, username);
                dbStatement.setString(2, password);
                dbStatement.executeUpdate();
                dbStatement.close();
                
                System.out.println("Signup has been successful.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public JSONObject selectUser(String username) {
        JSONObject user = new JSONObject();
        try {
            String query = "SELECT * FROM `user` WHERE username = ?";
            try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
                dbStatement.setString(1, username);
                ResultSet rs = dbStatement.executeQuery();
                if(rs.next()) {
                    user.put("username", rs.getString("username"));
                    user.put("password", rs.getString("password"));
                }
                dbStatement.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    public JSONArray selectGroupByUser(String username) {
        JSONArray groups = new JSONArray();
        try {
            String query = "SELECT group_id,name FROM `group` NATURAL JOIN `group_member` WHERE username = ?";
            try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
                dbStatement.setString(1, username);
                ResultSet rs = dbStatement.executeQuery();
                while(rs.next()) {
                    JSONObject group = new JSONObject();
                    group.put("group_id", rs.getInt("group_id"));
                    group.put("name", rs.getString("name"));
                    group.put("users", selectUserOnGroup(rs.getInt("group_id")));
                    groups.add(group);
                }
                dbStatement.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return groups;
    }
    
    public JSONArray selectUserOnGroup(int groupId) {
        JSONArray users = new JSONArray();
        try {
            String query = "SELECT username FROM `group` NATURAL JOIN `group_member` WHERE group_id = ?";
            try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
                dbStatement.setInt(1, groupId);
                ResultSet rs = dbStatement.executeQuery();
                while(rs.next()) {
                    users.add(rs.getString("username"));
                }
                dbStatement.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }
    
    public boolean removeGroupMember(int groupId, String username) {
        JSONArray members = selectUserOnGroup(groupId);
        try {
            if(!members.toString().contains(username)) {
                //System.out.println("haha");
                return false;
            }
            String query = "DELETE FROM `group_member` WHERE username = ?";
            try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
                dbStatement.setString(1, username);
                dbStatement.executeUpdate();
                dbStatement.close();
                
                System.out.println(username + " has been removed from the group");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
     
    public int insertGroup(String groupName) {
        int id = 0;
        String query1 = "INSERT INTO `group` (name) VALUES(?)";
        try (PreparedStatement dbStatement = conn.prepareStatement(query1,Statement.RETURN_GENERATED_KEYS)) {
            dbStatement.setString(1, groupName);
            dbStatement.executeUpdate();
            
            ResultSet rs = dbStatement.getGeneratedKeys();
                if(rs.next())
                {
                    id = rs.getInt(1);
                }
            
            dbStatement.close();

            System.out.println("Successfully create a new group");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    public boolean createNewGroup(String groupName, String username, JSONArray members) {
        int groupId = insertGroup(groupName);
        if (groupId == 0) {
            return false;
        }
        String query = "INSERT INTO `group_member` (group_id, username, isAdmin) VALUES(?, ?, ?)";
        try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
            dbStatement.setInt(1, groupId);
            dbStatement.setString(2, username);
            dbStatement.setInt(3, 1);
            dbStatement.executeUpdate();
            
            for (int i=0; i<members.size(); i++) {
                dbStatement.setInt(1, groupId);
                dbStatement.setString(2, members.get(i).toString());
                dbStatement.setInt(3, 0);
                dbStatement.executeUpdate();
            }
            dbStatement.close();

            System.out.println("Successfully add group members");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public boolean isFriend(String username, String friendName) { 
        boolean found = false;
        if(!selectUser(friendName).isEmpty()) {
            System.out.println("Your friend are not registered.");
            return found;
        }
        String query = "SELECT friend_username FROM `friend` WHERE username = ?";
        try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
            dbStatement.setString(1, username);
            ResultSet rs = dbStatement.executeQuery();
            if(rs.next()) {
                if (rs.equals(friendName)) {
                    found = true;
                }
            }
            dbStatement.close();   
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return found;
    }
    
    public boolean addFriend(String username, String friendName) {
        try {
            if(isFriend(username, friendName)) {
                System.out.println(friendName + "is already your friend.");
                return false;
            }
            String query = "INSERT INTO `friend`(username, friend_username) VALUES(?,?)";
            try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
                dbStatement.setString(1, username);
                dbStatement.setString(2, friendName);
                dbStatement.executeUpdate();
                dbStatement.close();
                
                System.out.println("Successfully added a friend");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public boolean addNewGroup(String groupName, String username, JSONArray members) {
        int groupId = 0;
        JSONArray groups = new JSONArray();
        groups = selectGroupByUser(username);
        JSONParser parser = new JSONParser();
        JSONObject temp = new JSONObject();
        for (int i=0; i<groups.size(); i++) {
            try {
                temp = (JSONObject)parser.parse(groups.get(i).toString());
            } catch (ParseException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (temp.get("name").equals(groupName)) {
                groupId = Integer.parseInt(temp.get("group_id").toString());
                break;
            }
        }
        String query = "INSERT INTO `group_member` (group_id, username, isAdmin) VALUES(?, ?, ?)";
        try (PreparedStatement dbStatement = conn.prepareStatement(query)) {
            for (int i=0; i<members.size(); i++) {
                dbStatement.setInt(1, groupId);
                dbStatement.setString(2, members.get(i).toString());
                dbStatement.setInt(3, 0);
                dbStatement.executeUpdate();
            }
            dbStatement.close();

            System.out.println("Successfully add group members");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}

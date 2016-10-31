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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
}

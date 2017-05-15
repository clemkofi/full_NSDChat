/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finalyear.networkservicediscovery.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author Vanessa
 */
public class SqliteConnection {
    static Connection conn = null;
    public static Connection dbConnector()
{
        try{
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:G:\\project files\\servcie discovery desktop\\NSDChat_today\\intranet_messenger_db.db");
            JOptionPane.showMessageDialog(null, "Connection successful");
            return conn;
        }catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    
}
}

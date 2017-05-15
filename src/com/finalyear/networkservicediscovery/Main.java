/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finalyear.networkservicediscovery;

import com.finalyear.networkservicediscovery.services.SocketService;
import com.finalyear.networkservicediscovery.ui.DiscoveryScreen;
import com.finalyear.networkservicediscovery.ui.RegisterScreen;
import com.finalyear.networkservicediscovery.utils.SqliteConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 *
 * @author KayO
 */
public class Main {

    public static SocketService SOCKET_SERVICE = new SocketService();

    static Connection connection = SqliteConnection.dbConnector();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //start server thread
        SOCKET_SERVICE.start();


        //check if user is logged in
        boolean isFirstRun = false;
        String identity;
        try {
            String sql = "SELECT * FROM app_info_tbl LIMIT 1";
            PreparedStatement pst;
            pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {//there is data in the result set
                System.out.print("Table contains identity: ");
                //this is NOT the first run
                identity = rs.getString("t_identity");
                System.out.println(identity);


                //ChatUIScreen will be called from the discovery screen , not the main function
                //ChatUIScreen uiScreen = new ChatUIScreen();
                //SOCKET_SERVICE.setServerUIActivity(uiScreen);
                //uiScreen.setService(SOCKET_SERVICE);
                //pass this identity to the remaining windows
                DiscoveryScreen discoveryScreen = new DiscoveryScreen(identity);
                //discoveryScreen.setSocketService(SOCKET_SERVICE);
                //discoveryScreen.main();

            } else {
                isFirstRun = true;
                //open registration screen
                RegisterScreen regScreen = new RegisterScreen();
                regScreen.main();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }



    }

}

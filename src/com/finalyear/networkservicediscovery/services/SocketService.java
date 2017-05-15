/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finalyear.networkservicediscovery.services;

import com.finalyear.networkservicediscovery.pojos.Contact;
import com.finalyear.networkservicediscovery.ui.ChatUIScreen;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author KayO
 */
public class SocketService extends Thread {

    Socket socket;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    ServerSocket serverSocket, tempServerSocket;

    int port = -1;

    public int getPort() {
        return port;
    }


    TempServerThread tempServerThread;

    //input streams
    InputStream inputStream;
    DataInputStream din;
    // TODO: 28/03/2017 for buffering image bytes
    BufferedInputStream bis;//from Image Sharer server
    //output streams
    OutputStream outputStream;
    DataOutputStream dout;
    // TODO: 28/03/2017 for streaming the bytes through the socket
    ObjectOutputStream oos;//from Image Sharer server


    private HashSet<InetAddress> ipSet = new HashSet<InetAddress>();
    String incoming = "";
    String msgIn = "";
    private HashMap<InetAddress, String> hashMap = null;

    ChatUIScreen serverUIActivity = null;

    private boolean complete = false;

    //MessageManager messageManager;
    @Override
    public void run() {
        super.run(); //To change body of generated methods, choose Tools | Templates.
        System.out.println("ServerSocket: run() method called");

        //messageManager = new MessageManager(getApplicationContext());
        connectServer();
    }

    private void connectServer() {
        try {
            System.out.println("ServerSocket: connectServer() method called");
            serverSocket = new ServerSocket(0);//server starts at random port

            port = serverSocket.getLocalPort();
            System.out.println("doInBackground: server port = " + port);
            socket = serverSocket.accept();//server will accept connections
            System.out.println("doInBackground: client IP: " + socket.getInetAddress());
            ipSet.add(socket.getInetAddress());
            System.out.println("IP address: "+socket.getInetAddress()+" added to IP Set");

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            din = new DataInputStream(inputStream);
            System.out.println("new Input stream");
            dout = new DataOutputStream(outputStream);

            while (!msgIn.equals("##exit")) {
                msgIn = din.readUTF();//get new incoming message
                if (msgIn.startsWith("##identity")) {
                    //match this name to ip in hashmap
                    if (hashMap == null) {
                        hashMap = new HashMap<InetAddress, String>();
                    }
                    hashMap.put(socket.getInetAddress(), msgIn.substring(msgIn.indexOf(':') + 1));
                }
                if (!msgIn.equals("")) {
                    System.out.println("Incoming message: " + msgIn);
                    if (msgIn.contains("##transfer_complete")) {
                        setComplete(true);
                    } else if (msgIn.contains("##port:")) {//request to share file with you
                        //extract new port number and connect to new socket
                        // TODO: 10/04/2017 test to see if ip is not giving issues
                        serverUIActivity.connectToFilePort(
                                socket.getInetAddress().toString().substring(1),// remove leading backslash
                                Integer.valueOf(msgIn.substring(msgIn.indexOf(':') + 1, msgIn.indexOf('/'))),
                                msgIn.substring(msgIn.lastIndexOf('/') + 1));
                    }
                }

                //display messages from client
                publishProgress();//update UI
            }

            //loop is broken, exit message was sent... remove this user from IPset
            ipSet.remove(socket.getInetAddress());
        } catch (IOException ex) {
            //Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setServerUIActivity(ChatUIScreen serverUIActivity) {
        this.serverUIActivity = serverUIActivity;
    }

    private void publishProgress() {
        if (serverUIActivity != null) {//activity has sent itself
            //serverUIActivity.showChatMessage("Client:\t" + msgIn, true);
            serverUIActivity.showChatMessage("Client:\t" + msgIn, true);
            //serverUIActivity.storeMessage("Client:\t" + msgIn, serverUIActivity.getContact(), true);//params: message, other party, reception state
        } else {
            //find the contact currently associated with this IP and use that to store the message
            Contact contact = new Contact();
            contact.setName(hashMap.get(socket.getInetAddress()));
            //storeForLater("Client:\t" + msgIn, contact, true);//params: message, other party, reception state
        }
    }

    public boolean sendMessage(String message) {
        try {
            if (dout != null) {
                dout.writeUTF(message);//send message
                return true;
            } else {
                System.out.println("SocketService: dout is null, no socket connection");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("sendMessage: message not sent, e = " + e.toString());
        }
        return false;
    }

    public void sendFile(String path) {
        //start temporary server
        tempServerThread = new TempServerThread(path);
        tempServerThread.start();
    }

    private class TempServerThread extends Thread {

        String filePath;

        public TempServerThread(String path) {
            this.filePath = path;
        }

        @Override
        public void run() {
            Socket tempSocket = null;//temporary file transfer socket

            try {
                tempServerSocket = new ServerSocket(0);//temporary server socket on random available port

                //send message of format: ##port:12345/file_name.extension
                String fileName = filePath.substring(filePath.lastIndexOf('\\') + 1);
                serverUIActivity.sendMessage("##port:" + tempServerSocket.getLocalPort() + "/" + fileName);

                //send this port alert to the recipient to authorize transfer by connecting to this server
                int loopCount = 0;
                while (!complete) {
                    System.out.println("loopCount: " + ++loopCount);
                    tempSocket = tempServerSocket.accept();//blocks loop till a socket connection is accepted
                    //when a connection is established, send the file
                    FileTxThread fileTxThread = new FileTxThread(tempSocket, filePath);
                    fileTxThread.start();
                    //break;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("run: " + e.toString());
            } finally {
                if (tempSocket != null) {
                    try {
                        tempSocket.close();
                        System.out.println("closeTempSocket: tempSocket closed");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("closeTempSocket: tempSocket NOT closed");
                    }
                }
            }
        }
    }

    private class FileTxThread extends Thread {

        String path;
        Socket tempSocket;

        FileTxThread(Socket tempSocket, String path) {
            this.path = path;
            this.tempSocket = tempSocket;
        }

        @Override
        public void run() {

            String queriedPath = path;
            File file = new File(queriedPath);
            System.out.println("queriedPath: " + queriedPath);

            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);//store bytes in byte[] bytes

                ObjectOutputStream oos = new ObjectOutputStream(tempSocket.getOutputStream());
                oos.writeObject(bytes);//write bytes to the socket
                oos.flush();

                tempSocket.close();

                final String sentMsg = "File sent to: " + tempSocket.getInetAddress();
                complete = true;//file transfer is complete

                System.out.println("Sent Message: " + sentMsg);

                //set complete to true here
                setComplete(true);
                msgIn = "##transfer_complete";//force code to abort infinite loop

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // TODO Auto-generated catch block
            finally {
                try {
                    tempSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    public HashSet<InetAddress> getIpSet() {
        return ipSet;
    }
}

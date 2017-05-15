/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finalyear.networkservicediscovery.pojos;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

/**
 *
 * @author KayO
 */
public class ListServiceDescription {
    private String instanceName;
    private int port;
    private InetAddress address;

    public ListServiceDescription(){
    }

    public ListServiceDescription(String instanceName, int port, InetAddress address) {
        this.instanceName = instanceName;
        this.address = address;
        this.port = port;
    }


    //setting and getting the name of instance or service discovered

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }


    protected String getEncodedInstanceName() {
        try {
            return URLEncoder.encode(getInstanceName(),"UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            return null;
        }
    }


    //setting and getting the address of the service discovered
    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }


    protected String getAddressAsString(){
        return getAddress().getHostAddress();
    }


    //setting and getting the port of the service discovered
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    protected String getPortAsString(){
        return String.valueOf(getPort());
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("name: ");
        buf.append(getEncodedInstanceName());
        buf.append(" IP: ");
        buf.append(getAddressAsString());
        buf.append(" Port: ");
        buf.append(getPortAsString());
        return buf.toString();
    }
}

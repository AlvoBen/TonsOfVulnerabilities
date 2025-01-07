﻿/**
 * ServerComponentAccessor.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.server;

//import com.sap.engine.frame.core.thread.ThreadSystem;

/**
 * ServerComponentAccessor is used to allow the clients running on the server
 * to find out about their location. The static variable container is set to point
 * to the JMSServerContainer on the server if the container is started. Clients
 * running outside the VM of the server will always get null when accessing this pointer.
 * @author Margarit Kirov
 * @version 1.0
 */
public class ServerComponentAccessor {
/*	
  public static String hardwareID = null;
  public static String systemID = null;
  public static Object container = null;
  public static ThreadSystem threadSystem = null;
*/  
    protected static ServerComponentInterface serverComponentInterface = null;
    
    public static ServerComponentInterface getServerComponentInterface() {    	
  	    return serverComponentInterface;  	
    }
    
    public static void setServerComponentInterface(ServerComponentInterface serverComponentInterfaceImplementation) {    	
  	    serverComponentInterface = serverComponentInterfaceImplementation;  	
    }    
}

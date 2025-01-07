/*
 * Created on 2006-1-18
 *
 */
package com.sap.jms.server;

import com.sap.jms.client.connection.ConnectionFactoryInterface;
import com.sap.jms.interfaces.JMSClientPassportManager;
import com.sap.jms.util.TaskManager;

/**
 * @author desislav-b
 *
 */
public interface ServerComponentInterface {
	public String getHardwareID();
	public String getSystemID();
	public TaskManager getTaskManager();	
	public JMSClientPassportManager getJMSClientPassportManager();	
	public ConnectionFactoryInterface getLocalConnectionFactoryInterface(String vpName); 
}

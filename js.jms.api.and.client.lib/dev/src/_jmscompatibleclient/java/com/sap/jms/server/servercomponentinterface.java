/*
 * Created on 2006-1-18
 *
 */
package com.sap.jms.server;

import java.util.Map;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.interfaces.JMSClientPassportManager;

/**
 * @author desislav-b
 *
 */
public interface ServerComponentInterface {
	public String getHardwareID();
	public String getSystemID();
  	public Object getContainer();
	public ThreadSystem getThreadSystem();
	public JMSClientPassportManager getJMSClientPassportManager();	
//  remove method as it is not called
//	public long getDestinationMaxDeliveryAttempts(String virtualProviderName, String destinationName);
	public Map getCurrentVirtualProviderProperties(String virtualProviderName);
}

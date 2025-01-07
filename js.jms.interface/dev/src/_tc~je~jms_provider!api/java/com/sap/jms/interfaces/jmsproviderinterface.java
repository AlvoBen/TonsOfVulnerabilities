/**
 * JMSProviderInterface.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2004.
 * All rights reserved.
 */
package com.sap.jms.interfaces;


import java.io.InputStream;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.resource.spi.ResourceAdapter;


/**
 * The JMS Provider interface
 *
 * @author Stoyan Vellev
 * @version 7.0
 */
public interface JMSProviderInterface {

    /**
     * Method getResourceAdapter.
     *
     */
    ResourceAdapter getResourceAdapter();

    /**
     * Method getRADescriptor.
     *
     */
    InputStream getRADescriptor();

    /**
     * Method getClassLoader.
     *
     */
    ClassLoader getClassLoader();
    
	/**
	 * Method registerClientPassportManager.
	 *
	 */        
	void registerClientPassportManager(JMSClientPassportManager manager);

	/**
	 * Method unregisterClientPassportManager.
	 *
	 */	
	void unregisterClientPassportManager();
  
    /**
     * Method getDeployHandler.
     *
     */
    JMSDeployHandlerInterface getDeployHandler();
  	
    
    boolean supportsSynchronizationOptimization(ConnectionFactory session);
    
    JtaSynchronization getJtaSynchronization(Session session); 
}

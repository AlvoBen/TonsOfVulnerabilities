package com.sap.jms.interfaces;

import java.util.Hashtable;
import java.util.Map;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.lib.descriptors.jmsconnector.JmsResources;

/**
 * @author rositza.andreeva@sap.com
 * 
 * The interface for the JMSDeployHandler
 * a wrapper for this interface is provided in order
 * to be accessible from the JMSConnector
 */
public interface JMSDeployHandlerInterface {
       /**
        * The constant will be used from the JMS connector and the jms_provider
        * as a key of a property for the way a saf-agent will be removed
        */
       public static final String JMS_SAF_PROPERTY = "[[[saf-agent]]]";
       
       // declare this string here as it is needed for the JMSConnector, the resource bundles will be removed at some point of time
       // that's why we declare it here. As the connector cannot access com.sap.jms.util.Enum we have to make a wrapper here
       public static final String CURRENT_NODE_IS_NOT_LOCK_OWNER = DeployExceptionEnum.CURRENT_NODE_IS_NOT_LOCK_OWNER.getName();

 
	/**
	 * Creates JMSResources in the JMS provider configuration.
	 * No commit over the creation of the resources is performed.
	 * @param appName the application to/in which the resources will
	 * 				  created in case of local/global application.
	 * @param resources the resources parsed from the jms-resources.xml
	 * @param handler the handler from the deploy action
	 * @return
	 * @throws JMSDeploymentException
	 */
	 public Map create(String appName, JmsResources resources, ConfigurationHandler handler) throws JMSDeploymentException;
  
    /**
	 * @param appName
	 * @param map
	 * @param handler
	 * @throws JMSDeploymentException
	 */
     public void remove(String appName, Map map, ConfigurationHandler handler) throws JMSDeploymentException;
  
    /**
     * @param appName
     * @param mapping
     * @throws JMSDeploymentException
     */
     public void start(String appName, Map mapping) throws JMSDeploymentException;
  
	/**
	 * @param appName
	 * @param jmsResources
	 * @param oldJmsResources
	 * @param configHandler
	 * @return
	 * @throws JMSDeploymentException
	 */
	public Map update(String appName, JmsResources jmsResources, Map oldJmsResources, ConfigurationHandler configHandler) throws JMSDeploymentException;
  
    /**
     * @param appName
     * @param mapping
     * @throws JMSDeploymentException
     */
    public void stop(String appName, Map mapping) throws JMSDeploymentException;


	/**
     * Deploys set of jms resources together with a library or a service which SDA 
     * will be updated (deployed)
     * @param libName the name of the library service which will be updated
     * @param resources the array of jms resources
     * @return  map cointaining name and lookupname for a value
     * @throws JMSDeploymentException
     */
 
	 public Map updateSDAResources(Hashtable<String, JmsResources> libNameResources)throws JMSDeploymentException;

	// ---------------------------------------------------------
	//  Methods for convertion of old jms resources xml
	// ---------------------------------------------------------
	
	/** 
	 * Checks if the factory is belonging to SAP JMS Provider
	 * if so creates queue
	 * @param appName
	 * @param factoryName
	 * @return false if the factory is not a jms factory
	 * @throws JMSDeploymentException
	 */
	public boolean createQueue(String appName, String queueName, Object connectionFactory) throws JMSDeploymentException;
	
	/**
	 * Checks if the factory is belonging to SAP JMS Provider
	 * if so creates the topic 
	 * @param appName
	 * @param topicName
	 * @param factory
	 * @return false if the factory is not belonging
	 * @throws JMSDeploymentException
	 */
	public boolean createTopic(String appName, String topicName, Object factory) throws JMSDeploymentException;
	
	/**
	 * 
	 * @param appName
	 * @return
	 * @throws JMSDeploymentException
	 */
	public boolean remove(String appName, String resourceName) throws JMSDeploymentException;
	
	public void startSAFAgent(String appName, String vpName, String safAgentName) throws JMSDeploymentException;

	/**
	 * @param vpName
	 * @throws JMSDeploymentException
	 */
	public void startVirtualProvider(String vpName) throws JMSDeploymentException;
	
	public Object lookupWithTimeout(String objectLookupName) throws JMSDeploymentException;
}

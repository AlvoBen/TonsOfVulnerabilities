package com.sap.jms.interfaces;

import com.sap.jms.interfaces.Enum;

/**
 * @author D035640
 * 
 * Enum class which typesafely enumerates JMSDeployHandler exception keys 
 * 
 */
public class DeployExceptionEnum extends Enum {
	/**
     * Constructor for JMSExceptionEnum.
     * @param name
     */
    protected DeployExceptionEnum(String name) {
        super(name);
    } //JMSExceptionEnum
    
    //---------------------------------------------------------------
    // Deploy
    // JMS0700 - JMS0707
    //---------------------------------------------------------------
	public static final DeployExceptionEnum REMOVE_FACTORY_DPL = new DeployExceptionEnum("JMS0700");
	public static final DeployExceptionEnum REMOVE_DESTINATION_DPL = new DeployExceptionEnum("JMS0701");

	public static final DeployExceptionEnum CREATE_DESTINATION_DPL = new DeployExceptionEnum("JMS0702");
	public static final DeployExceptionEnum CREATE_FACTORY_DPL = new DeployExceptionEnum("JMS0703");
	public static final DeployExceptionEnum UPDATE_DESTINATION_DPL = new DeployExceptionEnum("JMS0704");
	public static final DeployExceptionEnum UPDATE_FACTORY_DPL = new DeployExceptionEnum("JMS0705");
	public static final DeployExceptionEnum UPDATE_VP_PROPS_DPL = new DeployExceptionEnum("JMS0805");
	public static final DeployExceptionEnum AQUIRE_DEPLOY_LOCK =  new DeployExceptionEnum("JMS0706");
	public static final DeployExceptionEnum RELEASE_DEPLOY_LOCK = new DeployExceptionEnum("JMS0707");

	//---------------------------------------------------------------
    // Move JMS Resources 
    // JMS0707 - JMS0730
    //---------------------------------------------------------------
	public static final DeployExceptionEnum NO_FREE_NODE_AVAILABLE =  new DeployExceptionEnum("JMS708");
	public static final DeployExceptionEnum NO_FREE_VP_AVAILABLE = new DeployExceptionEnum("JMS709");
	
	public static final DeployExceptionEnum CONFIGURATION_PROBLEM  = new DeployExceptionEnum("JMS720");
	public static final DeployExceptionEnum CONFIGURATION_FACTORY_NOT_INIT  = new DeployExceptionEnum("JMS721");
	public static final DeployExceptionEnum CFG_NAME_NOT_FOUND_EXCEPTION = new DeployExceptionEnum("JMS0722"); 
	public static final DeployExceptionEnum CFG_INCONSISTENT_READ_EXCEPTION = new DeployExceptionEnum("JMS0723"); 
	public static final DeployExceptionEnum REMOVE_INSTANCE_DPL = new DeployExceptionEnum("JMS0724");
	public static final DeployExceptionEnum REMOVE_AGENT_DPL = new DeployExceptionEnum("JMS0725");
	
	public static final DeployExceptionEnum CURRENT_NODE_IS_NOT_LOCK_OWNER = new DeployExceptionEnum("JMS0726");
	public static final DeployExceptionEnum JMS_VP_RUNNING_MODE_CHANGED = new DeployExceptionEnum("JMS0727");
	public static final DeployExceptionEnum JMS_VP_DATA_SOURCE_CHANGED = new DeployExceptionEnum("JMS0728");
	public static final DeployExceptionEnum JMS_VP_PROPERTIES_UPDATE_ERROR = new DeployExceptionEnum("JMS0729");
	public static final DeployExceptionEnum JMS_UPDATE_ERROR = new DeployExceptionEnum("JMS0730");
	public static final DeployExceptionEnum JMS_VP_DATA_SOURCE_NOT_ACTIVE = new DeployExceptionEnum("JMS0731");
	public static final DeployExceptionEnum JMS_FACTORY_OPTIMIZED_VP_GLOBAL = new DeployExceptionEnum("JMS0732");
	public static final DeployExceptionEnum JMS_VP_CACHE_SIZE_LIMIT_CHANGED = new DeployExceptionEnum("JMS0733");
	public static final DeployExceptionEnum JMS_VP_CUSTOM_SECURITY_CONFIGURATION_CHANGED = new DeployExceptionEnum("JMS0734");
}

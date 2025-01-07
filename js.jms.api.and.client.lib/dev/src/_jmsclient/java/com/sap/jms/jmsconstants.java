/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.jms;
 
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
/**
 *
 */
public interface JMSConstants {

	String DEFAULT_SERVER_INSTANCE = "default";
    
    String JMS_SERVICE_NAME = "jms_provider";

    /** The name of the location object used for tracing and logging */
    String LOCATION_NAME = "com.sap.jms";

    /** Location object used for tracing */
    Location TRACER = Location.getLocation(LOCATION_NAME);

    /** Category variable used for logging */
    Category LOGGER = Category.SYS_SERVER;


    String JMS_FACTORY_SUBCONTEXT = "jmsfactory/";
    String[] FACTORY_NAMES = new String[] {"ConnectionFactory",
                                                           "QueueConnectionFactory",
                                                           "TopicConnectionFactory",
                                                           "XAConnectionFactory",
                                                           "XAQueueConnectionFactory",
                                                           "XATopicConnectionFactory"};
  
    byte[] FACTORY_TYPES = new byte[] {1, 3, 2, 6, 5, 4};

    String JMS_QUEUES_SUBCONTEXT = "jmsqueues/";
    String JMS_TOPICS_SUBCONTEXT = "jmstopics/";
    
    static final String DEFAULT_QUEUE_NAME_1 = "sapDemoQueue";
    static final String DEFAULT_QUEUE_NAME_2 = "sapDemoQueue2";
    static final String DEFAULT_TOPIC_NAME_1 = "sapDemoTopic";
    static final String DEFAULT_TOPIC_NAME_2 = "sapDemoTopic2";
    static final String DEFAULT_ERROR_QUEUE_NAME = "sapDefaultErrorQueue";
    
    static final String[] QUEUE_NAMES = { DEFAULT_QUEUE_NAME_1, DEFAULT_QUEUE_NAME_2 };
    static final String[] TOPIC_NAMES = { DEFAULT_TOPIC_NAME_1, DEFAULT_TOPIC_NAME_2 };
    
    String USER_HOME_DIR_PROPERTY_NAME = "user.home";
    
    /** Deploy Interface constant name */
    String DEPLOY_SERVICE_NAME = "deploy";  

    /** JMX Interface constant name */
    String JMX_SERVICE_NAME = "jmx";
    
    /** Shell Interface constant name */
    String SHELL_INTERFACE = "shell";

    /** Application Context interface constant name */
    String APPCONTEXT_INTERFACE = "appcontext";
    
    /** Security Interface constant name */
    String SECURITY_SERVICE_NAME = "security";  

    /** The Timeout service interface **/
    String TIMEOUT_SERVICE_NAME = "timeout";
    
    String BASIC_ADMIN_NAME = "basicadmin";
    String JMS_LOCK_NAME = "$" + JMS_SERVICE_NAME;

    String JMS_BOOT_LOCK_NAME = JMS_LOCK_NAME + "_boot";

    String JMS_DB_DELETE_LOCK_NAME = JMS_LOCK_NAME + "_db_lock_";

    char SLASH_REPLACE_CHAR = 5;  
	char STAR_REPLACE_CHAR = 6;
    char SEMICOLON_REPLACE_CHAR = 7;
	char COMMA_REPLACE_CHAR = 8;
	char EQUAL_REPLACHE_CHAR = 9;

    
    /** The sleep interval in ms in case the configuration was locked */
    int OPEN_CONFIGURATION_SLEEP_INTERVAL = 500;

    /** The max attempts for opening the configuration */
    int OPEN_CONFIGURATION_MAX_ATTEMPTS = 1200;  // 10 min 
    
    int DB_LOCK_SLEEP_INTERVAL = 100;
    int DB_LOCK_MAX_ATTEMPTS = 6000;  // 10 min
    
    int LOCK_OWNER_SLEEP_INTERVAL = 100;
    int LOCK_OWNER_MAX_ATTEMPTS = 1200; // 2 min
    
    byte CONNECTIONFACTORY = 1;
    byte TOPICCONNECTIONFACTORY = 2;
    byte QUEUECONNECTIONFACTORY = 3;
    byte XACONNECTIONFACTORY = 6;
    byte XATOPICCONNECTIONFACTORY = 4;
    byte XAQUEUECONNECTIONFACTORY = 5;

    byte TYPE_QUEUE = 0;
    byte TYPE_TOPIC = 1;
   
    public final static String TMP_QUEUE_PREFIX = "$$$SAPTMPQUEUE";
    public final static String TMP_TOPIC_PREFIX = "$$$SAPTMPTOPIC";
    public final static String TMP_DESTINATION_POSTFIX = "$$$";
    
   
    // JMS defined property names
    /** Name of the message property specifying the number of message delivery
    * attempts; the first is 1, the second 2,...*/
    public static final String MSG_PROPERTY_DELIVERY_COUNT = "JMSXDeliveryCount";
    
     public static final int DEFAULT_MAX_DELIVERY_ATTEMPTS = 5;

    /**
     *  The default value for the QueueMBean property maxDelivery Attempts
     */
    public static final long LISTENER_REDELIVERY_ATTEMPTS = 10;
    
    /**
     *  The constant used for the property key, which is set to the message 
     *  It represents the unique identifier of SAF sending agent.
     */
    public static final String JMSX_SAP_SAFSendingAgentUID = "JMSX_SAP_SAFSendingAgentUID";
    
    /**
     *  The constant used for the property key, which is set to the message 
     *  It represents the unique identifier of JMS message.
     */    
    public static final String JMSX_SAP_SAFMessageUID = "JMSX_SAP_SAFMessageUID";
    
    /**
     *  The constant used for the property key, which is set to the message 
     *  It represents some information about the sending agent, applications, etc.
     */        
    public static final String JMSX_SAP_SAFMessageInfo = "JMSX_SAP_SAFMessageInfo";
    
    /**
     *  The constant used for the property key, which is set to the message 
     *  It represents the destination ID of the local SAF destination.
     */        
    public static final String JMSX_SAP_SAFOriginatorDestinationId = "JMSX_SAP_SAFOriginatorDestinationId";

    /**
     * The pCounter
     */        
    public static final String JMSX_SAP_PCOUNTER = "JMSX_SAP_PCounter";
    
    /**
     * The constant used for the property key of messageId, which is set to the message
     * when it becomes dead and transferred to the error Destination 
     */
    public static final String JMSX_SAP_DEAD_MSG_ID = "JMSX_SAP_DeadMessageId";
    
    /**
     * The constant usef for the property key of the destinaton, which message has become
     * dead.
     */
    public static final String JMSX_SAP_DEAD_DST_ID = "JMSX_SAP_DeadMsgDstId";
    
    public static final String JMSX_SAP_DEAD_MSG_TIMESTAMP = "JMSX_SAP_DeadMsgTimestamp"; 
    
    public static final String JMSX_SAP_DEAD_MSG_EXPIRATION = "JMSX_SAP_DeadMsgExpiration";
    
    public static final String JMSX_SAP_DEAD_MSG_CORRELATIONID = "JMSX_SAP_DeadMsgCorrelationId";
    
    public static final String JMSX_SAP_DEAD_MSG_CONNECTIONID = "JMSX_SAP_DeadMsgConnectionId";
    
    public static final long DEFAULT_DELIVERY_INTERVAL = 2000;//ms
    public static final String DELIVERY_DELAY_INTERVAL_PREFIX = "DLVRY_DELAY_INTERVAL-";

    public static final int DEFAULT_CACHE_SIZE_LIMIT = 50*1024*1024;
}

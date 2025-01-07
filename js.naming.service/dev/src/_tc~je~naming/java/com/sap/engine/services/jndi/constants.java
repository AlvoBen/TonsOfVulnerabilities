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
package com.sap.engine.services.jndi;

/**
 * Constants
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public final class Constants {

  /**
   * Constant used in JNDIFrame to register CrossObjectFactory implementation in cross interface
   */
  public static final String OBJECT_FACTORY_REGISTRATION_NAME = "RedirectableServerContext";

  /**
   * Constant used in ClusterObject to specify that the type of the operation that a certain ClusterObject participate in is replicated
   */
  public final static short REPLICATED_OPERATION = 2;

  /**
   * Constant used in ClusterObject to specify that the type of the operation that a certain ClusterObject participate in is not replicated
   */
  public final static short NOT_REPLICATED_OPERATION = 3;

  /**
   * Constant used in ClusterObject to specify that the type of the operation that a certain ClusterObject participate in is replicated
   */
  public final static short REMOTE_REPLICATED_OPERATION = 4;

  /**
   * This constant enables or disables default root lookup from applications.
   */
  public static boolean APPLICATION_ROOT_LOOKUP_ENABLED = true;

  /**
   * p4 object broker to use in case the same environment is used for creation of InitialContext.
   */
  public static final String P4_OBJECT_BROKER = "P4_OBJECT_BROKER";

  /**
   * This constant enables or disables the old behaviour of rebind operation over non serializable objects, i.e. the rebind passes even if exception from the server side is thrown.
   * Purpose: Backward Compatibility.
   * Depricated property - will be removed in the fututre releases.
   */
  public static boolean NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR = false;

  /**
   * This constant enables or disables the old behaviour of destroy context operation over context that contains non serializable objects, i.e. the destroy context operation passes even if the context is not empty.
   * Purpose: Backward Compatibility.
   * Depricated property - will be removed in the fututre releases.
   */
  public static boolean DESTROY_CONTEXT_OLD_BEHAVIOUR = false;

  /**
   * Constant used in ClusterObject to specify that the type of the object that a certain ClusterObject represents is unknown
   */
  public final static short NOT_DEFINED_OBJECT = -1;

  /**
   * True if ClientContext must keep Absolute Name of the context (always true as specification requires getNameInNamespace)
   */
  public final static boolean KEEP_ABSOLUTE_NAME = true;

  public static int lockTrials = 1;
  /**
   * This constant specifies the size of the messages sent during the replication process, if there is a property with name "ReplicationMessageSize" set as naming service property, its value will be used
   * otherwise the default value is 2Mb (the purpose is to sent the message not via message server but via direct connection)
   */
  public static int REPLICATION_MESSAGE_SIZE = 2097152;


}


/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.core.service630;

import java.util.*;
import java.text.*;

import com.sap.engine.core.Names;
import com.sap.localization.ResourceAccessor;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizationException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.frame.ServiceResourceAccessor;

/**
 * Provides methods for convinient work with
 * the messages in the resource bundle
 */
public class ResourceUtils {

  /**
   * Default part of the key name in the resource bundle
   */
  public static final String DEFAULT_KEY = "kernel_";

  /**
   * ID offset in the resource bundle for the manager
   */
  public static final int offset = 7000;

  /**
   * Location for logging
   */
  private static final Location location = Location.getLocation(ResourceUtils.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  /**
   * IDs of the keys in the resource bundle
   */
  public static final int ERROR_LOCALIZATION_ID                                     = 200; //Error localizing text with id
  public static final int ERROR_LOCALIZATION_KEY                                    = 201; //Error localizing text with key
  //balance
  public static final int NAME_OR_DESTINATION_NULL                                  = 122;
  public static final int DESTINATION_NOT_FOUND                                     = 123;
  public static final int DESTINATION_IS_EMPTY                                      = 124;
  public static final int ALREADY_REGISTERED_ESTIMATOR                              = 125;
  //PersistentContainer
  public static final int ERROR_CREATING_COMPONENT                                  = 300;
  public static final int ERROR_INITIALIZE_CONFIGURATION_LEVELS                     = 301;
  public static final int PROPERTIES_READ_ERROR                                     = 302;
  public static final int SECURED_KEYS_READ_ERROR                                   = 303;
  public static final int CANT_DISCARD_CHANGES                                      = 304;
  public static final int PROPERTIES_STORE_ERROR                                    = 305;
  public static final int PROPERTIES_DELETE_ERROR                                   = 306;
  public static final int PROPERTIES_RESTORE_ERROR                                  = 307;
  public static final int DEPLOY_FAILED                                             = 308;
  public static final int UNDEPLOY_FAILED                                           = 309;
  public static final int ERROR_SYNCHRONIZING_BINARIES                              = 310;
  public static final int CONFIGURATION_LOCKED                                      = 311;
  public static final int ERROR_CALLING                                             = 312;
  //ServiceManagementImpl
  public static final int TYPE_IS_NOT_VALID                                         = 390;
  public static final int NAME_IS_NOT_VALID                                         = 391;
  //ReferenceResolver
  public static final int COMPONENT_NOT_RESOLVED_MISSING_REFERENCE                  = 400;
  public static final int COMPONENT_NOT_RESOLVED_NOT_RESOLVED_REFERENCE             = 401;
  public static final int CORE_COMPONENT_MISSING                                    = 402;
  public static final int CORE_COMPONENT_NOT_LOADED                                 = 403;
  public static final int CORE_COMPONENTS_MISSING_OR_NOT_LOADED                     = 404;
  public static final int INTERFACE_WITHOUT_PROVIDER                                = 405;
  public static final int CAN_NOT_DISABLE_CORE_SERVICE                              = 406;
  public static final int ERROR_READING_FILTERS                                     = 407;
  public static final int FILTERS_EMPTY                                             = 408;
  public static final int CYCLE_FOUND                                               = 409;
  public static final int CANT_ADD_TO_STARTUP_SET_NOT_DEPLOYED                      = 410;
  public static final int CANT_ADD_TO_STARTUP_SET_NOT_RESOLVED                      = 411;
  //ServiceContainerImpl
  public static final int CANT_READ_INSTANCE_TYPE                                   =   0;
  public static final int SERVICE_MANAGER_STARTED                                   =   1;
  public static final int SERVICE_MANAGER_STOPPED                                   =   2;
  public static final int CANT_GET_MANAGER                                          =   3;
  public static final int CANT_GET_MANAGERS                                         =   4;
  //ComponentWrapper & InterfaceWrapper & ServiceWrapper
  public static final int ERROR_CALCULATING_HASH                                    =  10;
  public static final int PROVIDER_ALREADY_SET                                      =  11;
  public static final int MANAGEMENT_INTERFACE_IS_ALREADY_REGISTERED                =  12;
  public static final int MANAGEMENT_INTERFACE_IS_NOT_REGISTERED                    =  13;
  public static final int FORBIDDEN_REFERENCE                                       =  14;
  //PersistentHelperImpl & DescriptorsHelperImpl
  public static final int ERROR_READING_FROM_PERSISTENT_CONTAINER                   = 136;
  public static final int ERROR_STORING_IN_PERSISTENT_CONTAINER                     = 137;
  public static final int ERROR_REMOVING_FROM_PERSISTENT_CONTAINER                  = 138;
  public static final int ERROR_LISTING_PERSISTENT_CONTAINER                        = 139;
  public static final int ERROR_GETING_MIGRATION_VERSION                            = 140;
  public static final int GET_PERSISTENT_FILE_DEPRECATED                            = 141;
  //RuntimeConfigurationWrapper
  public static final int RUNTIME_CONFIGURATION_ALREADY_REGISTERED                  = 150;
  public static final int TRACE_PROPERTIES_UPDATE                                   = 151;
  //all
  public static final int WAIT_INTERRUPTED                                          = 100;
  public static final int ERROR_PARSING_PROPERTY                                    = 101;
  //ContainerObjectRegistry
  public static final int INTERFACE_NOT_REGISTERED_AS_COMPONENT                     = 220;
  public static final int INTERFACE_NOT_REGISTERED_IN_REGISTRY                      = 221;
  public static final int INTERFACE_IS_ALREADY_REGISTERED_IN_REGISTRY               = 222;
  public static final int INTERFACE_NOT_DECLARED                                    = 223;
  public static final int SERVICE_INTERFACE_NOT_REGISTERED                          = 224;
  public static final int SERVICE_INTERFACE_IS_ALREADY_REGISTERED                   = 225;
  //CoreMonitorImpl
  public static final int SERVICE_INITIATE_SHUTDOWN                                 = 260;
  public static final int NO_SUCH_MANAGER                                           = 261;
  public static final int REBOOT_NOT_SUPPORTED                                      = 262;
  //ContainerEventRegistry
  public static final int THREAD_INTERRUPTED_REGISTERING_CONTAINEREVENTLISTENER     =  50;
  public static final int CONTAINER_LISTENER_ALREADY_REGISTERED                     =  51;
  public static final int LISTENER_DOESNT_EXIST                                     =  52;
  //ContainerEvent
  public static final int EVENT_TIMEOUT_EXPIRED                                     =  55;
  public static final int EVENT_TIMEOUT_EXPIRED2                                    =  56;
  //OperationDistributor
  public static final int LISTENER_ALREADY_REGISTERED                               =  80;
  public static final int CANT_SEND_MESSAGE                                         =  81;
  public static final int CANT_RECEIVE_MESSAGE                                      =  82;
  public static final int ACTION_FAILED_AT_CLUSTER_ELEMENT_CORRELATOR_ID            =  83;
  public static final int PARTIAL_RESPONSE                                          =  84;
  public static final int ERROR                                                     =  85;
  public static final int ERROR_DURING_DEPLOY                                       =  86;
  public static final int ACTION_FAILED_AT_CLUSTER_ELEMENT                          =  87;
  //LoadContainer
  public static final int ERROR_REGISTERING_LOADER                                  =  70;
  public static final int JAR_FILE_DOESNT_EXIST                                     =  72;
  public static final int JAR_FILE_IS_NULL_INCORRECT_DTD                            =  73;
  //MemoryContainer
  public static final int CANT_INITIALIZE_SMREPOSITORY                              = 350;
  public static final int CANT_CREATE_LOCK_OWNER                                    = 351;
  public static final int SERVICE_NOT_LOADED                                        = 352;
  public static final int LOADING_SERVICES                                          = 353;
  public static final int STOPPING_SERVICES                                         = 354;
  public static final int SERVICES_TIMED_OUT                                        = 355;
  public static final int TIMED_OUT_SERVICES                                        = 356;
  public static final int MSG_SERVICE_LIST                                          = 357;
  public static final int CANT_LOCK_SERVICEMANAGER_LOCK_AREA                        = 358;
  public static final int CANT_UNLOCK_SERVICEMANAGER_LOCK_AREA                      = 359;
  public static final int ATTEMPT_TO_STOP_SERVICE                                   = 360;
  public static final int SERVICE_DISABLED                                          = 361;
  public static final int CANNOT_START_DISABLED_SERVICE                             = 362;
  public static final int CLASSLOAD_CYCLES_DETECTED                                 = 363;
  public static final int CANT_STOP_CORE_SERVICE                                    = 364;
  public static final int ATTEMPT_TO_START_ALREADY_STARTED_SERVICE                  = 365;
  public static final int BINARY_SYNCHRONIZATION_ERROR                              = 366;
  public static final int CANT_UNREGISTER_COMPONENT_LOADER                          = 367;
  public static final int COMPONENT_NOT_EXISTS_IN_REGISTRY                          = 368;
  public static final int CANT_START_SERVICE_PREDECESOR_START_FAILED                = 369;
  public static final int CANT_START_SERVICE_INTERFACE_PROVIDER_START_FAILED        = 370;
  public static final int TIMEOUT_REASON_STARTING                                   = 371;
  public static final int TIMEOUT_REASON_WAIT_COMPONENTS_TO_START                   = 372;
  public static final int THREAD_DUMP_NUMBER                                        = 373;
  public static final int TIMEOUT_REASON_STOPPING                                   = 374;
  public static final int TIMEOUT_REASON_WAIT_COMPONENTS_TO_STOP                    = 375;
  public static final int CANT_START_SERVICE_IPROVIDER_NOT_DEPLOYED                 = 376;
  public static final int CANT_START_SERVICE_IPROVIDER_NOT_LOADED                   = 377;
  //ServiceRunner & ServiceStopper
  public static final int SERVICE_ERROR                                             = 290;
  public static final int CORE_SERVICE_FAILED                                       = 291;
  public static final int ADDITIONAL_SERVICE_FAILED                                 = 292;
  public static final int SERVICE_ERROR_DURING_STOP                                 = 293;
  public static final int SERVICE_DOES_NOT_PROVIDE_INTERFACE                        = 294;
  public static final int CORE_SERVICE_CONFIGURATION_NOT_VALID                      = 295;  
  //PropertiesEventHandler
  public static final int UNABLE_TO_REGISTER_CONFIG_CHANGE_LISTENER                 = 250;
  public static final int UNABLE_TO_GET_SERVICE                                     = 251;
  public static final int UNABLE_TO_APPLY_UPDATED_PROPERTIES_TO_SERVICE             = 252;
  public static final int NO_RUNTIME_CONFIGURATION_FOR_SERVICE                      = 253;

  /**
   * Resource Accessor used to obtain strings and localize text
   */
  public static ResourceAccessor resourceAccessor = ServiceResourceAccessor.getInstance();

  /**
   * Nubmer formatter for the key IDs
   */
  private static NumberFormat nf = NumberFormat.getInstance();
  static {
    nf.setMinimumIntegerDigits(4);
    nf.setGroupingUsed(false);
  }

  /**
   * Obtains a string from the resource bundle corresponding to the key given
   *
   * @param key Key in the resource bundle
   * @return String in the bundle
   */
  public static String getString(String key) {
    return resourceAccessor.getMessageText(Locale.getDefault(), key);
  }

  /**
   * Obtains a string from the resource bundle corresponding to the given message number
   *
   * @param id The ID of the key in the bundle
   * @return String in the bundle
   */
  public static String getString(int id) {
    return resourceAccessor.getMessageText(Locale.getDefault(), DEFAULT_KEY + nf.format(offset + id));
  }

  /**
   * Formats a string from the resource bundle corresponding to the given key
   *
   * @param key Key in the bundle
   * @param params Objects to format the string with
   * @return The formatted string
   */
  public static String formatString(String key, Object[] params) {
    try {
      return LocalizableTextFormatter.formatString(resourceAccessor, key, params);
    } catch (LocalizationException e) {
      location.traceThrowableT(Severity.ERROR, getString(ERROR_LOCALIZATION_KEY) + " " + key, e);
      return null;
    }
  }

  /**
   * Formats a string from the resource bundle corresponding to the given message number
   *
   * @param id The ID of the key in the bundle
   * @param params Objects to format the string with
   * @return The formatted string
   */
  public static String formatString(int id, Object[] params) {
    try {
      return LocalizableTextFormatter.formatString(resourceAccessor, DEFAULT_KEY + nf.format(offset + id), params);
    } catch (LocalizationException e) {
      location.traceThrowableT(Severity.ERROR, getString(ERROR_LOCALIZATION_ID) + " " + id, e);
      return null;
    }
  }

  /**
   * Get a key from resource buldle
   *
   * @param id - <int> key constant
   * @return key in resource bundle coresponding to <id>
   */
  public static String getKey(int id) {
    return DEFAULT_KEY + nf.format(offset + id);
  }

}
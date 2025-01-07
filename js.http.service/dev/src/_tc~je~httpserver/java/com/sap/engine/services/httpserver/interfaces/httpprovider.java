/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.interfaces;

import com.sap.engine.services.httpserver.CacheManagementInterface;
import com.sap.engine.services.httpserver.interfaces.exceptions.HostStoreException;
import com.sap.engine.services.httpserver.interfaces.exceptions.HttpShmException;
import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;
import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.LRUMap;
import com.sap.engine.services.httpserver.server.errorreport.ErrorReportInfoBean;

import java.util.Vector;

/**
 * Runtime interface for configuration of the http provider service  
 *   
 * @author Violeta Uzunova (I024174)
 */
public interface HttpProvider {
  
  /**
   * Registers the webContainer service into the http service  
   * 
   * @param servletAndJsp webContainer service
   */
  public void registerHttpHandler(HttpHandler servletAndJsp);

  /**
   * Returns a vector with all aliases stored in all virtual hosts
   * 
   * @return all application alises
   */
  public Vector getAllApplicationAliases();

  /**
   * Add the alias to an application. Update the configuration too.
   *
   * @param alias value of the alias
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   * @deprecated use HttpProvider.addApplicationAlias(String alias, boolean persistent)
   *             where you can specify to update or not the configuration.
   */
  public void addApplicationAlias(String alias) throws HostStoreException, IllegalHostArgumentException;

  /**
   * Add the alias to an application. Update the configuration depending on persistent property.
   *
   * @param alias value of the alias
   * @param persistent if true updates the configuration, else - only runtime structures.
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   */
  public void addApplicationAlias(String alias, boolean persistent) throws HostStoreException, IllegalHostArgumentException;
  
  /**
   * Adds all the aliases of the given application. 
   * Updates the configuration depending on persistent property.
   *
   * @param applicationName value of the application name
   * @param aliasesCanonicalized all the web aliases of the application
   * @param persistent if true updates the configuration, else - only runtime structures.
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   */
  public void addAllApplicationAliases(String applicationName, String[] aliasesCanonicalized, boolean persistent) throws HostStoreException, IllegalHostArgumentException;

  /**
   * Checks the alias to an application.
   *
   * @param alias value of the alias.
   * @throws IllegalHostArgumentException if alias or its substring already exists.
   */
  public void checkApplicationAlias(String alias) throws IllegalHostArgumentException;

  /**
   * Adds the path to the alias to an application to the specified host only.
   *
   * @param alias value of the alias
   * @param dir   canonical pathname string of the application directory
   */
  public void startApplicationAlias(String alias, String dir);

  /**
   * Removes some alias to an application. Update the configuration too.
   *
   * @param alias alias an application
   * @throws HostStoreException if writing this property configuration throws an ConfigurationException
   * @deprecated use HttpProvider.removeApplicationAlias(String alias, boolean persistent)
   * where you can specify to update or not the configuration.
   */
  public void removeApplicationAlias(String alias) throws HostStoreException;

  /**
   * Removes some alias to an application. Update the configuration depending on persistent property.
   *
   * @param alias alias an application
   * @param persistent if true updates the configuration, else - only runtime structures.
   * @throws HostStoreException if writing this property configuration throws an ConfigurationException
   */
  public void removeApplicationAlias(String alias, boolean persistent) throws HostStoreException;

  /**
   * Add the alias to an application
   *
   * @param alias value of the alias
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   */
  public void addHttpAlias(String alias, String value) throws HostStoreException, IllegalHostArgumentException;

  /**
   * Removes some alias to an application.
   *
   * @param alias alias an application
   * @throws HostStoreException if writing this property configuration throws an ConfigurationException
   */
  public void removeHttpAlias(String alias) throws HostStoreException;

  /**
   * Checks weather HTTP alias with the given name exists
   * 
   * @param alias
   * the alias to check for
   * 
   * @return
   * <code>true</code> if HTTP alias with the given name exists, otherwise 
   * <code>false</code>
   * 
   * @deprecated
   * with no replacement, because Web Container doesn't use it anymore
   */
  @Deprecated
  public boolean containsHttpAlias(String alias);
  
  public boolean containsApplicationAlias(String applicationAlias);

  /**
   * Notification that the application <CODE>application</CODE> containg web modules 
   * with <CODE>alises</CODE> alises is stopped/started, depending on the 
   * <CODE>start</CODE> flag on the current server node. This information is used to
   * update the local information in the http service (as host info for example) but 
   * it is propagated to the SHM as well. 
   * 
   * @param application the application name
   * @param aliases     list with alises of the web modules that are contained in this application 
   * @param start       flag showing the state in which the application is moved; if <CODE>true</CODE> - the application is starting
   * 
   * @throws HttpShmException 
   */
  public void changeLoadBalance(String application, String[] aliases, boolean start) throws HttpShmException;

  /**
   * Marks that the given application should use URL session tracking on the
   * dispatcher node
   * 
   * @param webApplication
   * the name of the web application to mark
   * 
   * @param isURLSessionTracking
   * if URL session tracking is enabled or disabled
   * 
   * @deprecated the dispatcher node is removed and replaced with ICM. Use
   * {@link #urlSessionTracking(String, String, boolean)}
   */
  @Deprecated
  public void urlSessionTracking(String webApplication, boolean isURLSessionTracking);

  /**
   * Marks that the given web application in the given application should use
   * URL session tracking on the ICM
   * 
   * @param applicationName
   * the name of the application
   * 
   * @param webApplication
   * the name of the web application to mark
   * 
   * @param isURLSessionTracking
   * if URL session tracking is enabled or disabled
   * 
   * @throws HttpShmException
   * if there are some problems with shared memory
   * 
   * @throws DuplicatedAliasException
   * if the <CODE>webApplication</CODE> alais is already registered in SHM and
   * the method is invoked for SAP default application (<CODE>applicationName</CODE> 
   * is sap.com/com.sap.engine.docs.examples)
   */
  public boolean urlSessionTracking(String applicationName, String webApplication, boolean isURLSessionTracking) throws HttpShmException;

  /**
   * Clear the whole ICM server cache 
   *
   */
  public void clearCache();

  /**
   * Clear the ICM server cache which corrensponds to <CODE>alias</CODE> 
   *  
   * @param alias the name of the alias
   */
  public void clearCacheByAlias(String alias);

  /**
   * Returns the object which represnets http service properties 
   * 
   * @return the http service properties 
   */
  public HttpProperties getHttpProperties();

  /**
   * Returns the host properties of the specifies host  
   * 
   * @param hostName    host name
   * @return            host properties 
   */
  public HostProperties getHostProperties(String hostName);
  
  /**
   * Provides access to the cache management interface, which allows 
   * cleaning of the ICM server cache
   * 
   * @return cache management interface
   */
  public CacheManagementInterface getCacheManagementInterface();
  
  /**
   * Returns the map of the correspondance between errorId 
   * and detailed error info   
   * 
   * @return map of correspondance between errorId and detailed error info 
   */
  public LRUMap<String, ErrorReportInfoBean> getErrorReportInfos();
}

/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;

import java.rmi.Remote;

/**
 * This interface represents an http virtual host.
 * It provides methods for viewing and modifying the virtual host settings on some server node.
 *
 * @author    Maria Jurova
 * @version   6.30
 */
public interface HostPropertiesRuntimeInterface extends Remote {
  // GETTERS

  /**
   * Returns the name of this virtual host. The name is equal to its domain name.
   *
   * @return    the name of this virtual host
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public String getHostName() throws java.rmi.RemoteException;

  /**
   * Returns whether this virtual host supports persistent connections or not.
   * If not the client socket will be closed after each response.
   *
   * @return    True, if this virtual host supports persistent connections and false otherwise
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public boolean isKeepAliveEnabled() throws java.rmi.RemoteException;

  /**
   * Returns whether directory listing is allowed on this virtual host or not.
   *
   * @return    True, if directory listing is allowed on this virtual host and false otherwise
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public boolean isList() throws java.rmi.RemoteException;

  /**
   * Returns whether logging of information about each http reguest is enabled on this virtual host or not.
   *
   * @return    True, if logging of each http request is enabled on this virtual host or false otherwise
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public boolean isLogEnabled() throws java.rmi.RemoteException;

  /**
   * Returns whether this virtual host uses cache for static http responses or not.
   *
   * @return    True, if this virtual host uses cache and false otherwise
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public boolean isUseCache() throws java.rmi.RemoteException;

  /**
   * Returns the start page of this virtual host.
   *
   * @return    The start page of this virtual host
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public String getStartPage() throws java.rmi.RemoteException;

  /**
   * Returns the root directory of this virtual host.
   *
   * @return    The root directory of this virtual host
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public String getRootDir() throws java.rmi.RemoteException;

  /**
   * Return all aliases (http or application) available on this virtual host.
   *
   * @return    All aliases available on this virtual host
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public String[] getAliasNames() throws java.rmi.RemoteException;

  /**
   * Returns the directory of a given alias.
   *
   * @param key   Alias name
   * @return    The directory of the given alias
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public String getAliasValue(String key) throws java.rmi.RemoteException;

  /**
   * Checks if a specified alias is http or application alias.
   *
   * @param key   Alias name
   * @return    True, if the specified alias is application alias and false otherwise or if the alias does not exist
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public boolean isApplicationAlias(String key) throws java.rmi.RemoteException;

  /**
   * Checks if a specified alias is application alias and if this application alias is activated on this host.
   *
   * @param key   Alias name
   * @return    True, if the specified alias is application alias and is activated on this host, false otherwise
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public boolean isApplicationAliasEnabled(String key) throws java.rmi.RemoteException;

  // SETTERS

  /**
   * Sets whether persistent connections will be available on this virtual host.
   *
   * @param keepAliveEnabled    If true, persistent connections will be available, otherwise not
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void setKeepAliveEnabled(boolean keepAliveEnabled) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Sets whether directory listing will be allowed on this virtual host or not.
   *
   * @param list    If true, directory listing will be allowed, otherwise not
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void setList(boolean list) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Sets whether information about each http request will be loged for this virtual host or not.
   *
   * @param enableLog   If true each http request will be logged, otherwise not
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void setLogEnabled(boolean enableLog) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Sets whether http cache will be used for this virtual host or not.
   *
   * @param useCache    If true http cache will be used, otherwise not
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void setUseCache(boolean useCache) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Changes the start page of this virtual host.
   *
   * @param startPage   The new start page
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void setStartPage(String startPage) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Changes the root directory of this virtual host.
   *
   * @param vDir    Directory name
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void setRootDir(String vDir) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Creates a new http alias with specified name and directory.
   *
   * @param alias   Alias name
   * @param valueal   Alias directory
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws IllegalHostArgumentException    If such alias already exists
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void addHttpAlias(String alias, String valueal) throws ConfigurationException, IllegalHostArgumentException, java.rmi.RemoteException;

  /**
   * Removes an http alias.
   *
   * @param alias   Alias name
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void removeHttpAlias(String alias) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Changes the directory of an http alias. If such alias does not exist it will be created.
   *
   * @param alias   Alias name
   * @param value   Alias directory
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void changeHttpAlias(String alias, String value) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Activates some application alias on this host. If deactivated the alias will be no more considered in http requests.
   *
   * @param alias   Alias name
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void enableApplicationAlias(String alias) throws ConfigurationException, java.rmi.RemoteException;

  /**
   * Deactivates some application alias on this host. If deactivated the alias will be no more considered in http requests.
   *
   * @param alias   Alias name
   * @throws ConfigurationException   If some error occurs in storing the new settings into data base
   * @throws java.rmi.RemoteException   is some exception occurs in remote communication with the server
   */
  public void disableApplicationAlias(String alias) throws ConfigurationException, java.rmi.RemoteException;
}

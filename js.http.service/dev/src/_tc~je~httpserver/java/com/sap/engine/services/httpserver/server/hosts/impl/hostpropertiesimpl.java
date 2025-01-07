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
package com.sap.engine.services.httpserver.server.hosts.impl;

import java.io.File;
import java.io.IOException;

import com.sap.engine.lib.util.ConcurrentReadHashMap;
import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.server.HttpHosts;
import com.sap.engine.services.httpserver.server.Log;

public class HostPropertiesImpl implements HostProperties {
  public static final String dir_list_key = "DirList";
  public static final String use_cache_key = "UseCache";
  public static final String enable_log_key = "EnableLoging";
  public static final String keep_alive_enabled_key = "KeepAliveEnabled";
  public static final String root_key = "Root";
  public static final String start_page_key = "StartPage";
  public static final String aliases_key = "Aliases";
  public static final String web_applications_key = "WebApplications";

  private String host = "default";
  private byte[] hostBytes = host.getBytes();
  /**
   * Root directory for this host
   */
  private String rootDirectory = "../../docs";
  private String rootDirectoryCanonical = "../../docs";
  protected String startPage = "";
  protected boolean keepAliveEnabled = true;
  protected boolean list = false;
  protected boolean useCache = false;
  protected boolean enableLog = true;
  /**
   * Alias name to alias path
   */
  protected ConcurrentReadHashMap aliases = new ConcurrentReadHashMap();
  /**
   * Alias name to "true" or "false" for visible or not visible application aliases
   */
  protected ConcurrentReadHashMap applications = new ConcurrentReadHashMap();


  private HttpHosts globalHostsInfo = null;

  public HostPropertiesImpl(String name, HttpHosts globalHostsInfo) {
    host = name;
    hostBytes = name.getBytes();
    this.globalHostsInfo = globalHostsInfo;
    try {
      initRootDir(rootDirectory);
    } catch (IOException io) {
      Log.logError("ASJ.http.000106", 
    		       "Cannot set the root directory of http virtual host [{0}] to [{1}]." 
    		       + " Http error responses [404 Not Found] may be returned.", 
    		       new Object[]{name, rootDirectory}, io, null, null, null);
      
    }
  }

  public String getHostName() {
    return host;
  }

  public byte[] getHostNameBytes() {
    return hostBytes;
  }

  public boolean isKeepAliveEnabled() {
    return this.keepAliveEnabled;
  }

  public boolean isList() {
    return this.list;
  }

  public boolean isLogEnabled() {
    return enableLog;
  }

  public boolean isUseCache() {
    return useCache;
  }

  public String getStartPage() {
    return startPage;
  }

  public String getRootDir() {
    return rootDirectoryCanonical;
  }

  public String getRootDirNoCanonical() {
    return rootDirectory;
  }

  public boolean rootExists() {
    return (new File(rootDirectoryCanonical)).exists();
  }

  public String[] getAliasNames() {
    Object[] httpAliases = aliases.getAllKeys();
    Object[] appAliases = applications.getAllKeys();
    if (httpAliases == null && appAliases == null) {
      return null;
    }
    int length = 0;
    if (httpAliases == null) {
      length = appAliases.length;
    } else if (appAliases == null) {
      length = httpAliases.length;
    } else {
      length = httpAliases.length + appAliases.length;
    }
    String[] result = new String[length];
    int off = 0;
    if (httpAliases != null) {
      for (int i = 0; i < httpAliases.length; i++) {
        result[off++] = (String)httpAliases[i];
      }
    }
    if (appAliases != null) {
      for (int i = 0; i < appAliases.length; i++) {
        result[off++] = (String)appAliases[i];
      }
    }
    return result;
  }

  /**
   * Gets the root directory for the given application or HTTP alias if it 
   * exists and if it is enabled in case of application alias
   * 
   * <p>Searches first in available application aliases and then in available 
   * HTTP aliases. If an HTTP alias is found returns its root directory only if
   * it doesn't overlap any existing application alias, otherwise returns 
   * <code>null</code></p>
   * 
   * <p>The change of the bihaviour of the method is an incompatible change, so
   * it is documented in the Web Container's list of incompatible changes</p>
   * 
   * @param key
   * the application or HTTP alias name
   * 
   * @return 
   * Returns an <code>String</code> with the given alias root directory
   */
  public String getAliasValue(String key) {
    String value = getApplicationAliasValue(key);
    if (value != null) { return value; } 
    value = (String) aliases.get(key);
    if (value == null) { return null; }
    // HTTP alias is found but its value will be returned
    // only if it doesn't overlap any application alias
    int i; 
    while ((i = key.lastIndexOf(ParseUtils.separator)) > -1) {
      key = key.substring(0, i);
      String tvalue = getApplicationAliasValue(key);
      if (tvalue != null) { return null; }
    }
    return value;
  }
  
  /**
   * Gets the root directory of the given application alias if exists and if it
   * is enabled
   * 
   * @param key
   * the application alias name
   * 
   * @return
   * Returns an <code>String</code> with the given alias root directory
   */
  public String getApplicationAliasValue(String key) {
    if (isApplicationAliasEnabled(key)) {
      String value = globalHostsInfo.getApplicationAliasValue(key);
      return (value == null) ? "" : value;
    }
    return null;
  }

  public boolean isApplicationAlias(String key) {
    return applications.containsKey(key);
  }

  public boolean isApplicationAliasEnabled(String key) {
    return "true".equals(applications.get(key));
  }

  public ConcurrentReadHashMap getHttpAliases() {
    return aliases;
  }

  public ConcurrentReadHashMap getApplications() {
    return applications;
  }

  public void dump() {
    System.out.println("host = " + host);
    System.out.println("aliases = " + aliases);
    System.out.println("applications = " + applications);
    System.out.println("enableLog = " + enableLog);
    System.out.println("keepAliveEnabled = " + keepAliveEnabled);
    System.out.println("list = " + list);
    System.out.println("startPage = " + startPage);
    System.out.println("useCache = " + useCache);
    System.out.println("vDir = " + rootDirectory);
  }

  protected void initRootDir(String vDir) throws IOException {
    File root = null;
    if (vDir == null || vDir.trim().equals("")) {
      root = new File(".");
    } else {
      root = new File(vDir);
    }
    this.rootDirectoryCanonical = root.getCanonicalPath().replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
  }
}

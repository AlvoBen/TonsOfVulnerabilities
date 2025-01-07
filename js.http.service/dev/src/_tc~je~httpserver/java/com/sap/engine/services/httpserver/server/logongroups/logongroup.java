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
package com.sap.engine.services.httpserver.server.logongroups;

import java.rmi.RemoteException;
import java.util.Vector;


//import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
//import com.sap.engine.services.httpserver.LogonGroupsManager;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.server.HttpLock;
import com.sap.engine.services.httpserver.server.Log;
//import com.sap.engine.services.httpserver.server.logongroups.LogonGroup;

public class LogonGroup {
  private String logonGroupName = null;  
  private Vector<String> instances = null;
  private Vector<String> aliases = null;
  private Vector<String> exactAliases = null;
  private Vector<String> newAliases = new Vector<String>();
  private Vector<String> newExactAliases = new Vector<String>();
   
  private LogonGroupsManager logonGroupsManager;
  private ConfigurationWriter configurationWriter;

  public LogonGroup(LogonGroupsManager logonGroupsManager) throws RemoteException {
    instances = new Vector<String>();
    aliases = new Vector<String>();
    exactAliases = new Vector<String>();
    
    this.logonGroupsManager = logonGroupsManager;
    this.configurationWriter = null;
  }

  public LogonGroup(String logonGroupName, LogonGroupsManager logonGroupsManager) throws RemoteException {    
    this.logonGroupName = logonGroupName;
    instances = new Vector<String>();
    aliases = new Vector<String>();
    exactAliases = new Vector<String>();
    
    this.logonGroupsManager = logonGroupsManager;
    this.configurationWriter = null;
  }

  public LogonGroup(String logonGroupName, ConfigurationWriter configurationWriter, LogonGroupsManager logonGroupsManager) throws RemoteException {
    this.logonGroupName = logonGroupName;
    instances = new Vector<String>();
    aliases = new Vector<String>();
    exactAliases = new Vector<String>();
    
    this.logonGroupsManager = logonGroupsManager;
    this.configurationWriter = configurationWriter;
  }

  /**
   * return the name of this logon group
   * @return
   */
  public String getLogonGroupName() {
    return logonGroupName;
  }

  // ======================================= INSTANCES ================================================
  
  /**
   * returns all instances of this logon group
   * @return
   */
  public Vector<String> getInstances() {
    return new Vector(instances);
  }

  /**
   * adds an instance to this logon group
   * @param instance
   */
  public synchronized void addInstance(String instanceID) throws ConfigurationException {
    if (!instances.contains(instanceID)) {
      instances.add(instanceID);
      try {
        for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
          try {
            configurationWriter.updateInstances(logonGroupName, getInstances());
            break;
          } catch (InconsistentReadException e) {
            try {
              Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
            } catch (InterruptedException ie) {
              Log.logError("ASJ.http.000116", 
                "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
            }
            if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
              throw e;
            }
          }
        }
      } catch (ConfigurationException e) {
        instances.removeElement(instanceID);
        Log.logError("ASJ.http.000117", 
          "Cannot add an instance [{0}] to http zone [{1}]. " +
        	"Probably a configuration access problem has occurred.", 
        	new Object[]{instanceID, logonGroupName}, e, null, null, null);
        throw e;
      }
    }
  }
  
  protected synchronized void instanceAdded(String instanceID) {
    if (!instances.contains(instanceID)) {
      instances.add(instanceID);
    }
  }
  
  public synchronized void addInstances(Vector<String> newInstanceIDs) throws ConfigurationException {
    for (int i = 0; newInstanceIDs != null && i < newInstanceIDs.size(); i++) {
      addInstance(newInstanceIDs.elementAt(i));
    }
  }
  
  public synchronized void instancesAdded(Vector<String> newInstanceIDs) throws ConfigurationException {
    for (int i = 0; newInstanceIDs != null && i < newInstanceIDs.size(); i++) {      
      instanceAdded(newInstanceIDs.elementAt(i));
    }
  }
  
  /**
   * removes an instance for this logon group
   * @param instance
   * @return
   */
  public void removeInstance(String instanceID) throws ConfigurationException {
    instances.remove(instanceID);
    try {
      for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.updateInstances(logonGroupName, getInstances());
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logError("ASJ.http.000118", 
              "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
          }
          if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    } catch (ConfigurationException e) {
      instances.addElement(instanceID);
      Log.logError("ASJ.http.000119", 
    		"Cannot remove an instance [{0}] from http zone [{1}] . " +
    		"Probably a configuration access problem has occurred.", 
    		new Object[]{instanceID, logonGroupName}, e, null, null, null);
      throw e;
    }    
  }
  
  public synchronized void instanceRemoved(int instanceID) {
    instances.remove(instanceID);
  }


  public synchronized void clearAllInstances() throws ConfigurationException {
    instances.clear();
    try {
      for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.updateInstances(logonGroupName, getInstances());
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logError("ASJ.http.000120", 
              "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
          }
          if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000121", 
    		"Cannot remove all instances from http zone [{0}]. " +
    		"Probably a configuration access problem has occurred.", 
    		new Object[]{logonGroupName}, e, null, null, null);
      throw e;
    }
  }

  public synchronized void instancesCleared() throws ConfigurationException {
    instances.clear();
  }
  
  /**
   * checks if this instance is added to this logon group
   */
  public boolean containsInstance(String instance) {
    return instances.contains(instance);
  }
  
  
  // ======================================= ALIASES ================================================
  

  public Vector<String> getAliases() {
    return new Vector(aliases);
  }  
  
  /**
   * add an alias to this logon group
   * @param aliasName   the name of the alias
   */
  public synchronized void addAlias(String alias) throws ConfigurationException {
    if (!aliases.contains(alias)) {
      aliases.add(alias);
      try {
        configurationWriter.updateAliases(logonGroupName, getAliases());
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000122", 
		      "Cannot add HTTP alias [{0}] to HTTP logon group [{1}]. " +
		      "Probably a configuration access problem has occurred.", 
		      new Object[]{alias, logonGroupName}, e, null, null, null);
        throw e;
      }
    }
  }
  
  /**
   * Adds an array of aliases to this logon group.
   * @param aliasesForAdd - the names of the aliases to be added
   * @throws ConfigurationException
   */
  public synchronized void addAliases(String[] aliasesForAdd) throws ConfigurationException {	  		
	      try {
	    	initNewAliases(aliasesForAdd);
	        configurationWriter.updateAliases(logonGroupName, getAliases());
	      } catch(ConfigurationException e) {
	        Log.logError("ASJ.http.000396", 
			      "Cannot add the HTTP aliases [{0}] to the HTTP logon group [{1}]. " +
			      "Probably a configuration access problem has occurred.", 
			      new Object[]{newAliases, logonGroupName}, e, null, null, null);
	        newAliases = new Vector<String>();	
	        throw e;
	      }
	    }
  
  /**
   * Adds an array of exact aliases to this logon group.
   * @param aliasesForAdd - the names of the exact aliases to be added
   * @throws ConfigurationException
   */
  public synchronized void addExactAliases(String[] exAliasesForAdd) throws ConfigurationException {
		
      try {
    	initNewExactAliases(exAliasesForAdd);
        configurationWriter.updateExactAliases(logonGroupName, getExactAliases());
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000397", 
		      "Cannot add the HTTP exact aliases [{0}] to the HTTP logon group [{1}]. " +
		      "Probably a configuration access problem has occurred.", 
		      new Object[]{newExactAliases, logonGroupName}, e, null, null, null);
        newExactAliases = new Vector<String>();
        throw e;
      }
  }


/**
 * From the given array of aliases extracts only the new ones (that are not already added to the current logon group).
 * Stores the new ones in newAliases field;
 * @param aliasesForUpdate - all the aliases to be updated
 * 
 *  */  
private synchronized void initNewAliases(String[] aliasesForUpdate){
	//first clear the cached newAliases
	newAliases = new Vector<String>();
	for(String alias:aliasesForUpdate){
		if (!aliases.contains(alias)) {
			newAliases.add(alias);
			aliases.add(alias);
		}
	}
}

/**
 * From the given array of exact aliases extracts only the new ones (that are not already added to the current logon group).
 * Stores the new ones in newExactAliases field;
 * @param aliasesForUpdate - all the aliases to be updated
 * 
 *  */  
private synchronized void initNewExactAliases(String[] exactAliasesForUpdate) throws ConfigurationException {
	//first clear the cached newExactAliases
	newExactAliases =  new Vector<String>();
	for(String alias:exactAliasesForUpdate){
		if (!exactAliases.contains(alias)) {
			newExactAliases.add(alias);
			exactAliases.add(alias);
		}
	}
}
  
  protected synchronized void aliasAdded(String alias) {
    if (!aliases.contains(alias)) {
      aliases.add(alias);
    }
  }
 

  
  public synchronized void aliasesAdded(Vector<String> newAliases) throws ConfigurationException {
    for (int i = 0; newAliases != null && i < newAliases.size(); i++) {
      aliasAdded(newAliases.elementAt(i));
    }
  }
  
  /**
   * removes alias from this logon group
   * @param aliasName   the name of the alias
   */
  public void removeAlias(String aliasName) throws ConfigurationException {
    aliases.remove(aliasName);
    try {
      configurationWriter.updateAliases(logonGroupName, getAliases());
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000123", 
    		"Cannot remove HTTP alias [{0}] from HTTP logon group [{1}] " +
    		"Probably a configuration access problem has occurred.", 
    		new Object[]{aliasName, logonGroupName}, e, null, null, null);
      throw e;
    }
  }
  
  protected synchronized void aliasRemoved(String alias) {
    aliases.remove(alias);
  }
  
  public synchronized void clearAllAliases() throws ConfigurationException {
    aliases.clear();    
    try {
      configurationWriter.updateAliases(logonGroupName, getAliases());
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000124", 
        "Cannot remove all HTTP aliases from HTTP logon group [{0}]. " +
        "Probably a configuration access problem has occurred.", new Object[] {logonGroupName}, e, null, null, null);
      throw e;
    }
  }
  
  public synchronized void aliasesCleared() throws ConfigurationException {
    aliases.clear();
  }
  
  /**
   * checks if this alias is added to logon group
   * @param aliasName
   * @return
   */
  public boolean containAlias(String aliasName) {
    return aliases.contains(aliasName);
  }
  
  
  // ======================================= EXACT ALIASES ==============================================
  
  public Vector<String> getExactAliases() {
    return new Vector(exactAliases);
  }
  
  /**
   * add exact alias to this logon group - update the runtime structures and DB
   * @param aliasName   the name of the alias
   */
  public synchronized void addExactAlias(String aliasName) throws ConfigurationException {
    if (!exactAliases.contains(aliasName)) {
      exactAliases.add(aliasName);
      try {
        configurationWriter.updateExactAliases(logonGroupName, getExactAliases());
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000125", 
        	"Cannot add exact alias [{0}] to HTTP logon group [{1}]. " +
        	"Probably a configuration access problem has occurred.", 
        	new Object[]{aliasName, logonGroupName}, e, null, null, null);
        throw e;
      }
    }
  }

  /**
   * Notification that an exact alias is added to this logon group on the remote server
   * This method is called to be updated the runtime structures only
   * 
   * @param alias the name of the new alias
   */
  protected synchronized void exactAliasAdded(String alias) {
    if (!exactAliases.contains(alias)) {
      exactAliases.add(alias);
    }
  }
  

  public synchronized void exactAliasesAdded(Vector<String> newAliases) throws ConfigurationException {
    for (int i = 0; newAliases != null && i < newAliases.size(); i++) {
      exactAliasAdded(newAliases.elementAt(i));
    }
  }
  
  
  /**
   * removes exact alias from this logon group
   * @param aliasName   the name of the alias
   */
  public void removeExactAlias(String aliasName) throws ConfigurationException {
    exactAliases.remove(aliasName);
    try {
      configurationWriter.updateExactAliases(logonGroupName, getExactAliases());
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000356", 
        "Cannot remove exact alias [{0}] from HTTP logon group [{1}] " +
        "Probably a configuration access problem has occurred.", 
        new Object[]{aliasName, logonGroupName}, e, null, null, null);
      throw e;
    }
  }
  
  
  public synchronized void clearAllExactAliases() throws ConfigurationException {
    exactAliases.clear();
    try {
      configurationWriter.updateExactAliases(logonGroupName, getExactAliases());
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000126", 
    		"Cannot remove all HTTP aliases from HTTP logon group [{0}]. " +
    		"Probably a configuration access problem has occurred.", 
    		new Object[]{logonGroupName}, e, null, null, null);
      throw e;
    }
  }

  public synchronized void exactAliasesCleared() throws ConfigurationException {
    exactAliases.clear();
  }
  
  /**
   * checks if this alias is added to this logon group as a prefix (exact alias)
   * 
   * @param aliasName
   * @return
   */
  public boolean containExactAlias(String aliasName) {
    if (aliasName == null || aliasName.length() == 0) {      
      return false;
    } else if (!aliasName.equals(ParseUtils.separator) && aliasName.charAt(0) == ParseUtils.separatorChar) {
      // skip the leading // TODO is that correct
      aliasName = aliasName.substring(1);
    }
   
    if (exactAliases.contains(ParseUtils.separator)) {
      // if the / is added as exact alias to this logon group all request has to be processed 
      return true;
    }
    
    if (aliasName.equals(ParseUtils.separator) || aliasName.indexOf(ParseUtils.separator) == -1) {
      // the alias is not composite => check if it is added as exact alias in this logon group
      return exactAliases.contains(aliasName);
    } else {        
      // the alias is composite (ex: a/b/c) => check if each prefix (eg a/b, a in this order) 
      // is added as exact alias in this logon group   
      String prefix = aliasName;  // prefix contains the current prefix of the requested alias 
      int j = prefix.length();
      do {
        prefix = prefix.substring(0, j);
        if (exactAliases.contains(prefix)) {            
          return true;
        } 
      } while ((j = prefix.lastIndexOf(ParseUtils.separatorByte)) > -1);
      return false;
    } 
  }
  
  
  // ======================================= READ/WRITE METHODS ==============================================
  
 
  /**
   * generates line of type 
   * PREFIX=/irj~<zonealias_1>/&GROUP=<zonealias_1>&CASE=&VHOST=*.*;&STACK=J2EE
   * PREFIX=/irj~<zonealias_2>/&GROUP=<zonealias_2>&CASE=&VHOST=*.*;&STACK=J2EE
   * 
   * For the default application it should be 
   * 
   * (exact alias) -> PREFIX=/&GROUP=testLG&CASE=&VHOST=*.*;&STACK=J2EE
   * (alias) -> PREFIX=/~testLG/&GROUP=testLG&CASE=&VHOST=*.*;&STACK=J2EE
   */
  public String toAliasInfoLines() {
    String res = "";
     
    // add lines for aliases
    // PREFIX=/<aliasName>~<zonealias_1>/&GROUP=<zonealias_1>&CASE=&VHOST=*.*;&STACK=J2EE
    if (aliases != null) {
      for (int j = 0; j < aliases.size(); j++) {            
        String line = "PREFIX=/";            
        if (!aliases.elementAt(j).equals("/")) {
          line += aliases.elementAt(j);
        }
        line += ((LogonGroupsManager)logonGroupsManager).getZoneSeparator() + logonGroupName + "/&";
        line += "GROUP=" + logonGroupName + "&";            
        line += "CASE=&VHOST=*.*;&STACK=J2EE" + "\r\n";  
        res += line;
      }
    }
    
    // add lines for exact aliases  
    // the type is PREFIX=/<aliasName>/&GROUP=<zonealias_1>&CASE=&VHOST=*.*;&STACK=J2EE
    if (exactAliases != null) {
      for (int j = 0; j < exactAliases.size(); j++) {            
        String line = "PREFIX=/";            
        if (!exactAliases.elementAt(j).equals("/")) {
          line += exactAliases.elementAt(j) + "/";
        }
        line += "&";
        line += "GROUP=" + logonGroupName + "&";            
        line += "CASE=&VHOST=*.*;&STACK=J2EE" + "\r\n";
        res += line;           
      }
    }
    
    return res;
  }
  
  public String traceValuesMsg() {
    return "Logon group <" + logonGroupName + ">" +
      ", instances=" + instances + ", aliases=" + aliases + 
      ", exact aliases=" + exactAliases + ".";
  }
}

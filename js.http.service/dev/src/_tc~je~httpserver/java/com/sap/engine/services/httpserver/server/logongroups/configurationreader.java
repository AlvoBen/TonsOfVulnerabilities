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

import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.tc.logging.Location;

import java.util.StringTokenizer;
import java.util.Vector;

public class ConfigurationReader {
  private static Location traceLocation = Location.getLocation(ConfigurationReader.class);
  private LogonGroupsManager logonGroupsManager;
  private ConfigurationHandlerFactory factory;

  public ConfigurationReader(LogonGroupsManager logonGroupsManager, ConfigurationHandlerFactory factory) {
    this.logonGroupsManager = logonGroupsManager;   
    this.factory = factory;
  }

  public synchronized void readAll() throws InconsistentReadException {
    ConfigurationHandler handler = null;
    try {
      handler = factory.getConfigurationHandler();
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000108", 
        "Cannot access configuration. HTTP zones will not be initialized.", e, null, null, null);
    }
    Configuration logonGroupsConfig = null;
    try {
      logonGroupsConfig = handler.openConfiguration("HttpZones", 0);
    } catch (NameNotFoundException e) {
      return;
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000109", 
        "Cannot open configuration [HttpZones]. HTTP zones will not be initialized.", e, null, null, null);
    }
    try {
      setLogonGroups(logonGroupsConfig);
    } finally {
      try {
        handler.closeAllConfigurations();
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000110", 
          "Cannot close configurations used for reading http zones.", e, null, null, null);
      }
    }
  }

  public void logonGroupUpdated(String logonGroupName) {
    try {
      initLogonGroup(logonGroupName);
    } catch(ConfigurationException e) {
      Log.logError("ASJ.http.000111", 
    		"Cannot initialize or update settings for http logon group [{0}] . " +
    		"Probably a configuration access problem has occurred.", 
    		new Object[]{logonGroupName}, e, null, null, null);
    }
  }

  private void setLogonGroups(Configuration configuration) throws InconsistentReadException {
    String zoneNames[] = null;
    try {
      zoneNames = configuration.getAllSubConfigurationNames();
    } catch (InconsistentReadException e) {
      throw e;
    } catch(ConfigurationException e) {
      Log.logError("ASJ.http.000112", 
        "Cannot read and initialize HTTP zones. All zones settings will be ignored. " +
        "Probably a configuration access problem has occurred.", e, null, null, null);
      return;
    } finally {
      try {
        configuration.close();
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000113", 
          "Cannot close configurations used for reading HTTP zones.", e, null, null, null);
      }
    }
    for (int i = 0; zoneNames != null && i < zoneNames.length; i++) {
      try {
        initLogonGroup(zoneNames[i]);
      } catch (InconsistentReadException e) {
        throw e;
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000114", 
          "Cannot initialize HTTP zone [{0}]. " +
        	"All settings for this zone will be ignored. Probably a configuration access problem has occurred.", 
        	new Object[]{zoneNames[i]}, e, null, null, null);
      }
    }
  }

  public boolean isDBConfigurationExist() throws InconsistentReadException {
    ConfigurationHandler handler = null;
  
    try {
      handler = factory.getConfigurationHandler();
      Configuration logonGroupsConfig = handler.openConfiguration("HttpZones", 0);
      String logonGroupsNames[] = logonGroupsConfig.getAllSubConfigurationNames();
      if (logonGroupsNames == null || logonGroupsNames.length == 0) {
        return false;
      }
    } catch (NameNotFoundException e) {
      return false;
    } catch (Exception ex) {
      // the operation of reading will be repeated 
      throw new InconsistentReadException(ex);
    } finally {
      try {
        if (handler != null) {
          handler.closeAllConfigurations();
        }
      } catch(ConfigurationException e) {
        Log.logError("ASJ.http.000115", 
          "Cannot close configurations used for reading HTTP zones.", e, null, null, null);
      }
    }    
    return true;
  }
  
  private void initLogonGroup(String logonGroupName) throws ConfigurationException {
	  //Filter the real logon groups from their helper property sheets that we use to store aliases and axact aliases. 
	  //Otherwise these property sheets would be shown in the NWA as well as the actual logon groups. 
	if( logonGroupName.indexOf("/"+ConfigurationWriter.ALIASES_CONFIG) < 0 
	    		 &&  logonGroupName.indexOf("/"+ConfigurationWriter.EXACT_ALIASES_CONFIG) < 0 ){
		ConfigurationHandler handler = factory.getConfigurationHandler();
		Configuration logonGroupConfig = null;
		try {
			logonGroupConfig = handler.openConfiguration("HttpZones/" + logonGroupName, 0);
		} catch(NameNotFoundException e) {
			logonGroupsManager.logonGroupUnregistered(logonGroupName);
			//zoneManagement.zoneUnregistered(zoneName);
			return;
		}
		try {
			LogonGroup logonGroup = (LogonGroup)logonGroupsManager.getLogonGroup(logonGroupName);
			boolean isNew = false;//for the trace message
			if (logonGroup == null) {
				logonGroup = logonGroupsManager.newLogonGroupRegistered(logonGroupName);
				isNew = true;
			}      
			//The next is synchronized because instances, aliases and exact aliases could
			//be accessed simultaneously from the configuration change listener and from the
			//NWA by the add/remove methods.
			synchronized (logonGroup) {
				if (traceLocation.beDebug()) {
					traceLocation.debugT("initLogonGroup("+ logonGroupName+ "): " +
							(isNew ? " newly created; " : " existing; ") +
							"Update runtime structures from DB. Previous values: " + logonGroup.traceValuesMsg());
				}
				logonGroup.instancesCleared();
				logonGroup.aliasesCleared();
				logonGroup.exactAliasesCleared();
				try {
					String instancesEntry = (String) logonGroupConfig.getConfigEntry("instances");
					logonGroup.instancesAdded(fromStringVector(instancesEntry));
				} catch (NameNotFoundException e) {
					//$JL-EXC$ - ok, no instances to read
				}
				try { 
					String aliases = readAliasesFromDB(logonGroupConfig, ConfigurationWriter.ALIASES_CONFIG, ConfigurationWriter.ALIASES_CFG_ENTRY);
					logonGroup.aliasesAdded(fromStringVector(aliases));
					if (traceLocation.beDebug()) {
						traceLocation.debugT("The aliases found in the configuration are:\n"+aliases);
					}
				}catch (NameNotFoundException e){
					//$JL-EXC$ - ok, no aliases to read
				}
				try{
					String exactAliasesStr = readAliasesFromDB(logonGroupConfig, ConfigurationWriter.EXACT_ALIASES_CONFIG, ConfigurationWriter.EXACT_ALIASES_CFG_ENTRY);
					logonGroup.exactAliasesAdded(fromStringVector(exactAliasesStr));
					if (traceLocation.beDebug()) {
						traceLocation.debugT("The exact aliases found in the configuration are:\n"+exactAliasesStr);
					}
				}catch (NameNotFoundException e){
					//$JL-EXC$ - ok, no exact aliases to read
				}
				if (traceLocation.beDebug()) {
					traceLocation.debugT("initLogonGroup("+ logonGroupName+ "): " +
							(isNew ? " newly created; " : " existing; ") +
							"Update of RT structures from DB done. New values: " + logonGroup.traceValuesMsg());
				}
			}           
		} finally {
			handler.closeAllConfigurations();
		}
	}
  }
  /**
   * Reads the aliases/exact aliases from the configuration. First tries to read them from a property sheet and
   * if there is no such sub configuration, tries to find them through the old configuration entry name.
   * @param logonGroupConfig - the logon group configuration
   * @param propSheetName - the name of the property sheet used to store aliases/exact aliases
   * @param oldCfgEntryName - the former name of the aliases/exact aliases configuration entries
   * @return
   * @throws InconsistentReadException
   * @throws NameNotFoundException
   * @throws ConfigurationException
   */
  private synchronized String readAliasesFromDB(Configuration logonGroupConfig, String propSheetName, String oldCfgEntryName) throws InconsistentReadException, NameNotFoundException, ConfigurationException{
      String aliasesStr = "";
      // first check for new sub configuration (with propertySheet), if it does not exist, then try the old one
	  if( logonGroupConfig.existsSubConfiguration(propSheetName)){ 
          Configuration cfg = logonGroupConfig.getSubConfiguration(propSheetName);
          //The given property sheet is currently used only for storing aliases or exact aliases => all its entries should be read
          PropertyEntry[] entries = cfg.getPropertySheetInterface().getAllPropertyEntries();
          if (entries != null){
        	  StringBuilder allAliases = new StringBuilder(aliasesStr);
        	  for (PropertyEntry entry:entries){
        		  if (entry != null){
            		  allAliases.append(entry.getValue()).append(", ");        			  
        		  }
        	  }
        	  if (allAliases.length()>=2){ 
        		  aliasesStr = allAliases.substring(0, allAliases.length()- 2);
        	  }
          }
	  } else { // read from the old configuration - without the propertySheet 
		  	  Object oldCfgEntry = logonGroupConfig.getConfigEntry(oldCfgEntryName);
        	  aliasesStr = (oldCfgEntry != null)? (String)oldCfgEntry : "";
      }	  
	  return aliasesStr;
  }

  private Vector<String> fromStringVector(String str) {
    if (str == null) {
      return null;
    }
    Vector<String> res = new Vector<String>();
    StringTokenizer tokenizer = new StringTokenizer(str, ",");
    while (tokenizer.hasMoreTokens()) {
      String next = tokenizer.nextToken().trim();      
      res.add(next);
    }
    return res;
  }
  
//  private String[] fromStringString(String res) {
//    if (res == null) {
//      return null;
//    }
//    String aliases[] = new String[0];
//    for (StringTokenizer tokenizer = new StringTokenizer(res, ","); tokenizer.hasMoreTokens();) {
//      String next = tokenizer.nextToken().trim();
//      String tmp[] = new String[aliases.length + 1];
//      System.arraycopy(aliases, 0, tmp, 0, aliases.length);
//      tmp[aliases.length] = next;
//      aliases = tmp;
//    }
//    return aliases;
//  }
}
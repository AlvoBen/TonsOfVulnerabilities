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

import java.util.Vector;
import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.httpserver.server.*;
import com.sap.tc.logging.Location;

public class ConfigurationWriter {
  private static Location traceLocation = Location.getLocation(ConfigurationWriter.class);
  private ConfigurationHandlerFactory factory;
  
  public static final String ALIASES_CFG_ENTRY = "aliases";
  public static final String EXACT_ALIASES_CFG_ENTRY = "exact-aliases";
  public static final String ALIASES_CONFIG = "aliases_config";
  public static final String EXACT_ALIASES_CONFIG = "exact-aliases_config";
  private static final int maxStringLength = FrameUtils.MAX_STRING;
 
  public ConfigurationWriter(ConfigurationHandlerFactory factory) {
    this.factory = null;
    this.factory = factory;
  }

  public synchronized void createLogonGroup(String logonGroupName) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.LOGON_GROUPS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000051",
        "Cannot get cluster lock for storing logon groups settings.", e, null, null, null);
    }
    try {
      Configuration hostConfiguration = null;
      try {
        hostConfiguration = handler.openConfiguration("HttpZones", 1);
      } catch(NameNotFoundException e) {
        hostConfiguration = handler.createSubConfiguration("HttpZones");
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("createLogonGroup(" + logonGroupName + ") - config HttpZones created", new Object[0]);
        }
      }
      try {
        hostConfiguration = hostConfiguration.getSubConfiguration(logonGroupName);
      } catch (NameNotFoundException ex) {
        hostConfiguration = hostConfiguration.createSubConfiguration(logonGroupName);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("createLogonGroup(" + logonGroupName + ") - subconfig created", new Object[0]);
        }
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.LOGON_GROUPS_LOCK);
        } catch(Exception e) {
          Log.logWarning("ASJ.http.000052", 
            "Cannot close cluster lock for logon groups settings.", e, null, null, null);
        }
      }
    }
  }

  public synchronized void deleteLogonGroup(String logonGroupName) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.LOGON_GROUPS_LOCK);
    } catch (Exception e) {
      Log.logWarning( "ASJ.http.000053",  
        "Cannot get cluster lock for storing logon groups settings.", e, null, null, null);
    }
    try {
      Configuration hostConfiguration = null;
      try {
        hostConfiguration = handler.openConfiguration("HttpZones", 1);
      } catch(NameNotFoundException e) {
        return;
      }
      try {
        hostConfiguration = hostConfiguration.getSubConfiguration(logonGroupName);
      } catch (NameNotFoundException ex) {
        return;
      }
      hostConfiguration.deleteConfiguration();
      handler.commit();
      if (traceLocation.beDebug()) {
        traceLocation.debugT("deleteLogonGroup(" + logonGroupName + ") - subconfig deleted", new Object[0]);
      }
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.LOGON_GROUPS_LOCK);
        } catch(Exception e) {
          Log.logWarning("ASJ.http.000104", 
            "Cannot close cluster lock for logon groups settings.", e, null, null, null);
        }
      }
    }
  }

  // TODO not groupsId but instances
  public synchronized void updateInstances(String logonGroupName, Vector<String> instanceIds) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.LOGON_GROUPS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000054", 
        "Cannot get cluster lock for storing zones settings.", e, null, null, null);
    }
    try {
      Configuration hostConfiguration = null;
      try {
        hostConfiguration = handler.openConfiguration("HttpZones", 1);
      } catch (NameNotFoundException e) {
        hostConfiguration = handler.createSubConfiguration("HttpZones");
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateInstances(" + logonGroupName + ") - config HttpZones created", new Object[0]);
        }
      }
      try {
        hostConfiguration = hostConfiguration.getSubConfiguration(logonGroupName);
      } catch (NameNotFoundException ex) {
        hostConfiguration = hostConfiguration.createSubConfiguration(logonGroupName);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateInstances(" + logonGroupName + ") instances: " + toString(instanceIds) + " - subconfig for logong group created", new Object[0]);
        }
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      if (instanceIds != null) {
        hostConfiguration.modifyConfigEntry("instances", toString(instanceIds), true);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateInstances(" + logonGroupName + ") instances: " +
              toString(instanceIds) + " - instances added", new Object[0]);
        }
      } else {
				hostConfiguration.deleteConfigEntry("instances");
				if (traceLocation.beDebug()) {
          traceLocation.debugT("updateInstances(" + logonGroupName + ") instances: " +
              toString(instanceIds) + " - instances deleted", new Object[0]);
        }
      }
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.LOGON_GROUPS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000055", 
            "Cannot close cluster lock for logon groups settings.", e, null, null, null);
        }
      }
    }
  }

  public synchronized void updateAliases(String logonGroupName, Vector<String> aliases) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.LOGON_GROUPS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000056", 
        "Cannot get cluster lock for storing logon groups settings.", e, null, null, null);
    }
    try {
      Configuration hostConfiguration = null;
      Configuration aliasConfiguration = null;
      try {
        hostConfiguration = handler.openConfiguration("HttpZones", 1);
      } catch (NameNotFoundException e) {
        hostConfiguration = handler.createSubConfiguration("HttpZones");
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateAliases(" + logonGroupName + ") - config HttpZones created");
        }
      }
      try {
        hostConfiguration = hostConfiguration.getSubConfiguration(logonGroupName);
      } catch (NameNotFoundException ex) {
        hostConfiguration = hostConfiguration.createSubConfiguration(logonGroupName);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateAliases(" + logonGroupName + ") aliases: " + aliases + " - subconfig for logong group created");
        }
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);          
      }
      if(! hostConfiguration.existsSubConfiguration(ALIASES_CONFIG)){
          aliasConfiguration = hostConfiguration.createSubConfiguration(ALIASES_CONFIG, Configuration.CONFIG_TYPE_PROPERTYSHEET);
              // if old format exists - delete it and leave only the new one
          if( hostConfiguration.existsConfigEntry(ALIASES_CFG_ENTRY) ){
            hostConfiguration.deleteConfigEntry(ALIASES_CFG_ENTRY);
          }
        } // the configuration should exist now
        aliasConfiguration = hostConfiguration.getSubConfiguration(ALIASES_CONFIG); 
        PropertySheet prSheet = aliasConfiguration.getPropertySheetInterface();
        try{ 
           //delete all aliases groups previously stored in the property sheet	
           prSheet.deleteAllPropertyEntries();
          if (traceLocation.beDebug()) {
              traceLocation.debugT("updateAliases(" + logonGroupName + ") aliases: " + aliases + " - The previously stored aliases are now deleted form the DB.");
          }
        } catch (NameNotFoundException ex) {
          // the method throws NNFE, although java doc says it should not
          if (traceLocation.beDebug()) {
                traceLocation.debugT("There are no aliases stored in the configuration for logon group with name ["+logonGroupName+"].");
          }
        }
        if (aliases != null) { // now update with the new values OR just leave it blank
        	//check the length of aliases. Divide it in several groups if needed. Each entry should not exceed 25 000 characters.
           	Vector<String> groupedAliases = splitToStrings(aliases, maxStringLength);
           	if (groupedAliases == null){
             	  Log.logError("ASJ.http.000394", "Cannot update the aliases settings of logongroup [{0}]. " +
             	  		"Some of the new aliases exceeds the maximum allowed by the configuration characters length [{1}].",
      	        		new Object[]{logonGroupName, maxStringLength}, null, null, null);
             }
           	else if (!groupedAliases.isEmpty()){
        		if (groupedAliases.size()== 1){
        			prSheet.createPropertyEntry(ALIASES_CFG_ENTRY, groupedAliases.elementAt(0), "Aliases for logon groups.");
        		}else{
        			if (traceLocation.beDebug()) {
                		traceLocation.debugT("The size of the aliases exceeds the maximum allowed size for a single entry in the data base. " +
                				"Therefore the aliases are split and stored in separate configuration entries. The number of the entries is :" + groupedAliases.size());
                	}
        			for (int i=0; i<groupedAliases.size(); i++){
        				int index = i+1;
        				prSheet.createPropertyEntry(ALIASES_CFG_ENTRY+"_part_"+ index, groupedAliases.elementAt(i), "Aliases for logon groups.");
        			}
        		}
        	  	if (traceLocation.beDebug()) {
        	  		traceLocation.debugT("updateAliases(" + logonGroupName + ") aliases: " + aliases + " - aliases added");
        	  	}
           	}
        }
      handler.commit();
   }catch(ConfigurationException cfe){
            Log.logError("ASJ.http.000392","Cannot update the aliases settings of logongroup [{0}] due to problems with the data base.",
            		new Object[]{logonGroupName}, cfe, null, null, null);
            throw cfe;
   } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.LOGON_GROUPS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000057", 
            "Cannot close cluster lock for logon groups settings.", e, null, null, null);
        }
      }
    }
  }

  public synchronized void updateExactAliases(String logonGroupName, Vector<String> aliases) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.LOGON_GROUPS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000058", 
        "Cannot get cluster lock for storing logon groups settings.", e, null, null, null);
    }
    try {
      Configuration hostConfiguration = null;
      Configuration exAliasConfig = null;
      try {
        hostConfiguration = handler.openConfiguration("HttpZones", 1);
      } catch (NameNotFoundException e) {
        hostConfiguration = handler.createSubConfiguration("HttpZones");
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateExactAliases(" + logonGroupName + ") - config HttpZones created");
        }
      }
      try {
        hostConfiguration = hostConfiguration.getSubConfiguration(logonGroupName);
      } catch (NameNotFoundException ex) {
        hostConfiguration = hostConfiguration.createSubConfiguration(logonGroupName);
        if (traceLocation.beDebug()) {
          traceLocation.debugT("updateExactAliases(" + logonGroupName + ") exact aliases: " + aliases + " - subconfig for logong group created");
        }
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      if(! hostConfiguration.existsSubConfiguration(EXACT_ALIASES_CONFIG)){
    	  exAliasConfig = hostConfiguration.createSubConfiguration(EXACT_ALIASES_CONFIG, Configuration.CONFIG_TYPE_PROPERTYSHEET);
              // if old format exists - delete it and leave only the new one
          if( hostConfiguration.existsConfigEntry(EXACT_ALIASES_CFG_ENTRY) ){
            hostConfiguration.deleteConfigEntry(EXACT_ALIASES_CFG_ENTRY);
          }
        } // the configuration should exist now
      	exAliasConfig = hostConfiguration.getSubConfiguration(EXACT_ALIASES_CONFIG); 
        PropertySheet prSheet = exAliasConfig.getPropertySheetInterface();
        try{ 
           //delete all aliases groups previously stored in the property sheet	
           prSheet.deleteAllPropertyEntries();
          if (traceLocation.beDebug()) {
              traceLocation.debugT("updateExactAliases(" + logonGroupName + ") exact aliases: " + aliases + ": The previously stored exact aliases are now deleted form the DB.");
          }
        } catch (NameNotFoundException ex) {
          // the method throws NNFE, although java doc says it should not
          if (traceLocation.beDebug()) {
                traceLocation.debugT("There are no exact aliases stored in the configuration for logon group with name ["+logonGroupName+"].");
          }
        }
        if (aliases != null) { // now update with the new values OR just leave it blank
        	//check the length of aliases. Divide it in several groups if needed. Each entry should not exceed 25 000 characters.
           	Vector<String> groupedAliases = splitToStrings(aliases, maxStringLength);
           	if (groupedAliases == null){
           	  Log.logError("ASJ.http.000395", "Cannot update the exact aliases settings of logongroup [{0}]. " +
           	  		"Some of the new exact aliases exceeds the maximum allowed by the configuration characters length [{1}].",
    	        		new Object[]{logonGroupName, maxStringLength}, null, null, null);
           	}
           	else if (!groupedAliases.isEmpty()){
        		if (groupedAliases.size()== 1){
        			prSheet.createPropertyEntry(EXACT_ALIASES_CFG_ENTRY, groupedAliases.elementAt(0), "Exact aliases for logon groups.");
        		}else{
        			if (traceLocation.beDebug()) {
                		traceLocation.debugT("The size of the exact aliases exceeds the maximum allowed size for a single entry in the data base. " +
                				"Therefore the exact aliases are split and stored in separate configuration entries. The number of the entries is :" + groupedAliases.size());
                	}
        			for (int i=0; i<groupedAliases.size(); i++){
        				int index = i+1; 
        				prSheet.createPropertyEntry(EXACT_ALIASES_CFG_ENTRY+"_part_"+ index, groupedAliases.elementAt(i), "Exact aliases for logon groups.");
        			}
        		}
            	if (traceLocation.beDebug()) {
        		traceLocation.debugT("updateExactAliases(" + logonGroupName + ") aliases: " + aliases + " - exact aliases added");
            	}
           	}
        }
      handler.commit();
    }catch(ConfigurationException cfe){
        Log.logError("ASJ.http.000393", "Cannot update the exact aliases settings of logongroup [{0}] due to problems with the data base.",
        		new Object[]{logonGroupName}, cfe, null, null, null);
        throw cfe;
	}finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.LOGON_GROUPS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000059", 
            "Cannot close cluster lock for logon groups settings.", e, null, null, null);
        }
      }
    }
  }

  /**
   * Groups the given aliases in several strings, so that each string does not exceeds the given max length of characters.
   * The aliases in each of the resulted strings are separated with commas and intervals.
   * Returns null 
   * @param aliases - the aliases to be converted
   * @param maxLength - the maximum number of characters that each of the resulted strings should not exceed
   * @return vector with all the generated strings
   * 		 returns null if some of the given aliases is longer than the maximum allowed length
   */
  
  private Vector<String> splitToStrings (Vector<String> elements, int maxLength){
	  Vector<String> result = new Vector<String>();
	  //First check the element's size and format them
	  StringBuilder allElementsStr = toString(elements, maxLength);
	  if (allElementsStr == null) {
		  return null; 
	  }
	  StringBuilder remainder = allElementsStr;
	  int lastCommaInd;
	  while (remainder.length() > maxLength){
  			  String piece = remainder.substring(0, maxLength + 1);
  			  //as the piece is longer than maxLength and each of the elements are shorter than maxLength, then at least one comma exists 
  			  lastCommaInd = piece.lastIndexOf(",");
  			  result.add(remainder.substring(0, lastCommaInd));
  			  remainder = remainder.delete(0, lastCommaInd + 2);
	  }
	  if (remainder.length()>0 && remainder.length() <= maxLength){
		  result.add(new String (remainder));
	  }
	  return result;
  }
  
  /**
   * Checks if all the given strings do not exceed the given maxLenth and returns them concatenated in a StringBuilder.
   * In the returned StringBuilder all the elements are separated with commas and intervals.
   * Returns null if some of the given elements is longer than the specified maxLength.
   * @param elements
   * @param maxLength
   * @return
   */
  private  StringBuilder toString(Vector<String> elements, int maxLength) {
	    StringBuilder res = new StringBuilder("");
	    for (int i = 0; elements != null && i < elements.size(); i++) {
	      if (elements.elementAt(i).length() > maxLength){
	    	  return null;
	      }
	      res = res.append(elements.elementAt(i)).append(", ");
	    }
	    if (res.length() != 0) {
	      res = res.delete(res.length() - 2, res.length());
	    }
	    return res;
	  }
  
  private String toString(Vector<String> elements) {
    String res = "";
    for (int i = 0; elements != null && i < elements.size(); i++) {
      res = res + elements.elementAt(i) + ", ";
    }
    if (res.length() != 0) {
      res = res.substring(0, res.length() - 2);
    }
    return res;
  }

//  private String toString(String aliases[]) {
//    String res = "";
//    for (int i = 0; aliases != null && i < aliases.length; i++) {
//      res = res + aliases[i] + ", ";
//    }
//    if (res.length() != 0) {
//      res = res.substring(0, res.length() - 2);
//    }
//    return res;
//  }
}
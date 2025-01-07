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

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationValueUtility;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;


public class LogonGroupsRequestGenerator {
  public static final String INSTANCE_NAME_KEY = "SAPMYNAME";
  public static final String SAPSTART = "SAPSTART";
  
  private LogonGroupsManager logonGroupsManager = null;
  // local cache of the mapping between groupId and instnceName in double stack system
  // to obtain this mapping
  private Hashtable<String, String> instanceIdsCache; 
  
  public LogonGroupsRequestGenerator(LogonGroupsManager logonGroupsManager) {
    this.logonGroupsManager = logonGroupsManager;
  }
  
  /**
   * Generates the response (according to the syntax of the icrgroups.txt file):
   *  
   * version 1.0
   * J2EE<ClusterID_J2EEDispatcher_1>
   * J2EE<ClusterID_J2EEDispatcher_2>
   * ...
   * J2EE<ClusterID_J2EEDispatcher_n>
   * (empty row)
   * <zonealias_1>:J2EE<J2EEDispatcherClusterID_1>
   * <zonealias_2>:J2EE<J2EEDispatcherClusterID_2>, J2EE<J2EEDispatcherClusterID_3>,...
   * ...
   * <zonealias_2>:J2EE<J2EEDispatcherClusterID_1>, J2EE<J2EEDispatcherClusterID_n>
   *
   * See http://aiokeh.wdf.sap.corp:1080/SAPIKS2/contentShow.sap?_CLASS=IWB_EXTHLP&_LOIO=87252C4142AEF623E10000000A155106&&TMP_IWB_TASK=PREVIEW2&RELEASE=700&LANGUAGE=EN
   * It is returned on /sap/public/J2EE/icf_info/icr_groups request
   */
  public String answerGroupInfoRequest() {
    // string for responce
    String responseBody = LogonGroupsManager.VERSION_LINE + "\r\n";

    //structures to collect the data   
    Vector<String> instances = new Vector<String>();    
    // for the section with lines <zonealias_2>:J2EE<J2EEDispatcherClusterID_2>, J2EE<J2EEDispatcherClusterID_3>,...
    String secondSection = "";
    
    LogonGroup allLGroups[] = logonGroupsManager.getAllLogonGroups();
    if (allLGroups != null && allLGroups.length != 0) {
      for (int i = 0; i < allLGroups.length; i++) {
        if (allLGroups[i] == null) {
          continue;
        }
                
        String logonGroup = allLGroups[i].getLogonGroupName();
        Vector<String> currentInstances = allLGroups[i].getInstances();        
        if (currentInstances != null) {
          for (int j = 0; j < currentInstances.size(); j++) {            
            String currentInstance = getInstanceNameForWebDispatcher(currentInstances.elementAt(j));            
            if (currentInstance == null) {
              Log.logWarning("ASJ.http.000063", 
                "Error in the configuration of logon group [{0}].", new Object[]{logonGroup}, null, null, null);
              continue;
            }
            // add the instance to the vector with all instances
            if (!instances.contains(currentInstance)) {
              instances.add(currentInstance);
            }
          }
        }
        // add a line for this logon group
        secondSection += logonGroupToInstancesLine(allLGroups[i]) + "\r\n";        
      } 
      
      // starts constructing the responce      
      // add instances
      for (int i = 0; i < instances.size(); i++) {
        responseBody += instances.elementAt(i) + "\r\n";
      }
      
      // add an empty row
      responseBody += "\r\n";    
      
      // add mappings group:instances
      responseBody += secondSection;
      
      // add an empty row
      responseBody += "\r\n"; 
    }
    
    return responseBody;    
  }
  
  
  /**
   * generates line of type 
   * <logonGroup_N>:J2EE<J2EEDispatcherClusterID_1>, J2EE<J2EEDispatcherClusterID_2>, ..., J2EE<J2EEDispatcherClusterID_n>
   */
  public String logonGroupToInstancesLine(LogonGroup logonGroup) {
    String res = "";
    res += logonGroup.getLogonGroupName() + ":";
    Vector<String> logonGroupInstances = logonGroup.getInstances();
    for (String instance: logonGroupInstances) {  
      String instanceName = getInstanceNameForWebDispatcher(instance);
      if (instanceName != null) { 
        res += instanceName + ",";
      }
    }
    if (res.endsWith(",")) {
      return res.substring(0, res.length() - 1); // remove the last ',' 
    } else {
      //should never happens because this method is called when there are instnces to this logon group
      return res;
    }
  }
  
  
  
  /**
   * Generates the response (according to the syntax of the urlinfo.txt file)
   *  
   * version 1.0
   * PREFIX=/irj~<zonealias_1>/&GROUP=<zonealias_1>&CASE=&VHOST=*.*;&STACK=J2EE
   * PREFIX=/irj~<zonealias_2>/&GROUP=<zonealias_2>&CASE=&VHOST=*.*;&STACK=J2EE
   * PREFIX=/~<zonealias_3>/&GROUP=<zonealias_2>&CASE=&VHOST=*.*;&STACK=J2EE  -> for default alias
   * ...
   * PREFIX=/irj~<zonealias_n>/&GROUP=<zonealias_n>&CASE=&VHOST=*.*;&STACK=J2EE
   *
   * See http://aiokeh.wdf.sap.corp:1080/SAPIKS2/contentShow.sap?_CLASS=IWB_EXTHLP&_LOIO=87252C4142AEF623E10000000A155106&&TMP_IWB_TASK=PREVIEW2&RELEASE=700&LANGUAGE=EN
   * It is returned on /sap/public/J2EE/icf_info/icr_urlprefix request
   */
  public String answerUrlMapRequest() {
    String responseBody = LogonGroupsManager.VERSION_LINE + "\r\n";
    
    LogonGroup allLGroups[] = logonGroupsManager.getAllLogonGroups();
    if (allLGroups != null && allLGroups.length != 0) {
      for (int i = 0; i < allLGroups.length; i++) {
        if (allLGroups[i] == null) {
          continue;
        }       
        
        responseBody += allLGroups[i].toAliasInfoLines();
      }
      // add an empty row
      responseBody += "\r\n"; 
    }    
    return responseBody;
  }
   
  
  /**
   * Returns the instance name for the web dispatcher config response by the given groupid.     
   * In case of 
   * 
   * J2EE stack - the name should be J2EE<groupId>20
   * double stack - the value of the SAPMYNAME SystemParamter for the corresponding instance
   * 
   * @param groupId - this is JCnumber + groupId (kernal) ie 
   * @return instance name as the web dispatcher knows it
   */
  private String getInstanceNameForWebDispatcher(String groupId) {  
    String instanceName = null;
    
    String sapstart = SystemProperties.getProperty(SAPSTART);
    Log.logError("ASJ.http.000360", "sapstart = {0}", new Object[]{sapstart}, null, null, null);    
    if ((sapstart != null) && sapstart.equals("1")) {   
      // double stack system
      // read the instance name from the system parameter as the value of the SAPMYNAME for the given instance
      // this value is written in DB cluster_config/system/instances/idXXXXX/instance_profile property
      try {
        // this hashtable is done for optimization - not to make a call to configuration for each call
        if (instanceIdsCache == null) {
          instanceIdsCache = new Hashtable<String, String>();
        }
        
        if (instanceIdsCache.containsKey(groupId)) {
          return instanceIdsCache.get(groupId);
        }
        
        // read it from configuration 
        // obtain the system properties of the other instance
        ConfigurationHandlerFactory cfgHdlFctry = logonGroupsManager.getFactory();    
        ConfigurationValueUtility utility = cfgHdlFctry.getConfigurationValueUtility();
        if (utility != null) {        
          Properties profile = utility.getSystemProfile("ID" + groupId);
          if (profile == null) {
            Log.logError("ASJ.http.000352", 
                "Cannot obtain system profile properties for instance [{0}] in double stack system.",
                new Object[]{groupId}, null, null, null);
            return null;
          } else {            // profile == null
            instanceName = (String)profile.get("SAPMYNAME");        
            if (instanceName != null && !instanceName.equalsIgnoreCase("null")) {
              instanceIdsCache.put(groupId, instanceName);
              return instanceName;              
            } else {              
              Log.logError("ASJ.http.000353", 
                  "Cannot obtain SAPMYNAME profile parameter for instance [{0}] in double stack system.", null, null, null);
              return null;    // is this correct; there is such instance but there is no value for SAPMYNAME instance param
            }
          }                   
        } else {
          Log.logWarning("ASJ.http.000354", 
              "Cannot obtain ConfigurationValueUtility from configuration handler.", null, null, null);
          return null;
        }                     // utility != null
      } catch (ConfigurationException e) {
        Log.logWarning("ASJ.http.000355", 
            "Cannot obtain SAPMYNAME profile parameter for instance [{0}] in double stack system.",
            new Object[]{groupId}, e, null, null, null);
        return null;
      }    
    }
    
    // in case of j2ee stack only return the j2ee style instance names i.e. J2EE<groupid>20
    
    // the normal way to make this check is 
    // allNodes[i].getGroupId() == groupId && allNodes[i].getType() == ClusterElement.DISPATCHER
    // the reason to change this is explained in CSN 2418603 2007 
    
    // The web dispatcher collagues insist on static list of instances within icr_group request
    // for more info see CSN 2418603 2007 and JIRA task WEB-205. But the core API does not provide 
    // info for stopped instances. To retrieve it JStartup Framework web service has to be used.
    // Unfortunatly it does not provide the mapping between instance-dispatcher. A CSN 3560834 2007
    // is logged for the issue 
    return new StringBuilder("J2EE").append(groupId).append("20").toString();
  }  
}

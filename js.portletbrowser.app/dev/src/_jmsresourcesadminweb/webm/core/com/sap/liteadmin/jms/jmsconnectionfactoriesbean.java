package com.sap.liteadmin.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.itsam.jsr77.jms.SAP_ITSAMJ2eeJMSConnectionFactorySettings;
import com.sap.engine.admin.model.itsam.jsr77.jms.SAP_ITSAMJ2eeJMSConnectionFactoryWrapper;

public class JMSConnectionFactoriesBean {
  private static String CONNECTION_FACTORIES_QUERY = "*:cimclass=SAP_ITSAMJ2eeJMSConnectionFactory,*";
      
  private Logger log = Logger.getLogger(JMSConnectionFactoriesBean.class.getName());
     
  private HashMap<String, JMSConnectionFactoryInfo> connectionFactoryInfoMap = null;
  private HashMap<String, ObjectName> connectionFactoryObjectNameMap = null;
  
  private boolean needsUpdate = false;
   
  private MBeanServerConnection mbs;
  
  
  public JMSConnectionFactoriesBean() { 
    connectionFactoryInfoMap = new HashMap<String, JMSConnectionFactoryInfo>();
    connectionFactoryObjectNameMap = new HashMap<String, ObjectName>();
    needsUpdate = false;
    
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (NamingException ex) {
      ex.printStackTrace();
    }
  }
  
  protected List getFactories() {
    if (connectionFactoryInfoMap.size() == 0 || needsUpdate) {      
      connectionFactoryInfoMap = new HashMap<String, JMSConnectionFactoryInfo>();
      connectionFactoryObjectNameMap = new HashMap<String, ObjectName>();
      updateConnectionFactoriesList();
    }    
    return getCFList();
  }
  
  protected JMSConnectionFactoryInfo getSelectedConnectionFactory(String name) {    
    return connectionFactoryInfoMap.get(name);
  }
  
  protected String updateConnectionFactoryProperties(String name) {
    try {
      ObjectName on = (ObjectName)connectionFactoryObjectNameMap.get(name);
      if (on == null) {
        return "Cannot find MBean for jms connection factory " + name + "!";
      }
      
      CompositeData  cd = (CompositeData) mbs.getAttribute(on, "Settings");      
      SAP_ITSAMJ2eeJMSConnectionFactorySettings tc  = (SAP_ITSAMJ2eeJMSConnectionFactorySettings ) SAP_ITSAMJ2eeJMSConnectionFactoryWrapper.getSAP_ITSAMJ2eeJMSConnectionFactorySettingsForCData(cd);
      
      JMSConnectionFactoryInfo cfInfo = getSelectedConnectionFactory(name);
      
      log.info("Change properties of " + name + " connection factory with new values:");
      log.info("                ClientID = " + cfInfo.getClientID());      
      tc.setClientID(cfInfo.getClientID());      
      cd = SAP_ITSAMJ2eeJMSConnectionFactoryWrapper.getCDataForSAP_ITSAMJ2eeJMSConnectionFactorySettings(tc); 
      Object _result =  mbs.invoke(on, "ApplyChanges", new Object[] { cd }, new String[] {"javax.management.openmbean.CompositeData"}); 
      needsUpdate = true;
      return (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
    } catch (Exception e) {
      e.printStackTrace();
      return e.toString();
    }    
  } 

  protected String createNewCF(String newResouceName, byte type) {
    String result = null;

    try {
      Set set = mbs.queryNames(
              new ObjectName("*:cimclass=SAP_ITSAMJ2eeJMSVirtualProvider,SAP_ITSAMJ2eeJMSVirtualProvider.Name=default,*"),
              null); // obtain the default Virtual Provider
      if (set.size() != 1) {
        return "Cannot find the MBean for default virual provider";
      }
      Iterator it = set.iterator();
      ObjectName on = (ObjectName) it.next();
      
      Object _result = mbs.invoke(on, "CreateConnectionFactory", 
          new Object[] {new String(newResouceName), new Byte(type)},
          new String[] {String.class.getName(), Byte.TYPE.getName() });
      result = (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
      
      needsUpdate = true;
    } catch (Exception e) {
      e.printStackTrace();
      result = e.toString() + ". See default trace for more info";
    }

    return result;
  }

  protected String removeConnectionFactories() {
    String result = "";

    try {      
      Set set = mbs.queryNames(
              new ObjectName("*:cimclass=SAP_ITSAMJ2eeJMSVirtualProvider,SAP_ITSAMJ2eeJMSVirtualProvider.Name=default,*"),
              null); // obtain the default Virtual Provider
      if (set.size() != 1) {
        return "Cannot find the MBean for default virual provider";
      }
      Iterator it = set.iterator();
      ObjectName on = (ObjectName) it.next();

      Iterator allConnectionFactories = connectionFactoryInfoMap.keySet().iterator();
      while (allConnectionFactories.hasNext()) {
        JMSConnectionFactoryInfo connectionFactoryInfo = connectionFactoryInfoMap.get(allConnectionFactories.next());
        if (connectionFactoryInfo.isSelected()) {
          try {
            log.info(">> remove connection factory " + connectionFactoryInfo.getLookupName());
            mbs.invoke(on, "RemoveDestination", new Object[]{new String(connectionFactoryInfo.getLookupName())}, new String[]{String.class.getName()});
          } catch (Exception e) {
            result = result + "\r\n" + e.toString() + ". See default trace for more info";
          }
        }
      }      
      result = result + "\r\n OK";
      needsUpdate = true;
    } catch (Exception e) {
      e.printStackTrace();
      result = e.toString() + ". See default trace for more info";
    }
    return result;  
  } 
  
  // --------------------- PRIVATE METHODS ---------  
  private void updateConnectionFactoriesList() {
    try {
      log.info(" Lsit jms resources with query " + CONNECTION_FACTORIES_QUERY);      
      ObjectName pattern = new ObjectName(CONNECTION_FACTORIES_QUERY);
      Set result = mbs.queryNames(pattern, null);    
      Object connectionFactories[] = result.toArray();

      for (int i = 0; i < connectionFactories.length; i++) { // 2    
        ObjectName on = (ObjectName)connectionFactories[i];
        String lookupName = (String) mbs.getAttribute(on, "Name"); // THIS IS IMPORTANT - from JASEN
        CompositeData factorySettings = (CompositeData) mbs.getAttribute(on, "Settings"); // THIS IS IMPORTANT - from JASEN
        
        String name = lookupName.substring(lookupName.lastIndexOf('/') + 1);
        String provider = lookupName.substring(lookupName.indexOf('/') + 1, lookupName.lastIndexOf('/'));
        if (provider != null && !provider.equals("default")) { 
          // only connection factories in default virtual provider
          continue;
        }
        JMSConnectionFactoryInfo info = new JMSConnectionFactoryInfo(name, lookupName);
        
        info.setCaption((String)factorySettings.get("Caption"));
        info.setClientID((String)factorySettings.get("ClientID"));
        info.setConnectionType((String)factorySettings.get("ConnectionType"));
        info.setDescription((String)factorySettings.get("Description"));
        info.setElementName((String)factorySettings.get("ElementName"));
        info.setLookupNameSettings((String)factorySettings.get("LookupName"));       
        
        connectionFactoryInfoMap.put(name, info);
        connectionFactoryObjectNameMap.put(name, on);
        log.info("     Info collected for jms resources >>>> " + name);
      }
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
    }    
  }

  private List getCFList() {
    List<JMSConnectionFactoryInfo> list = new ArrayList<JMSConnectionFactoryInfo>();
  
    Iterator allFactories = connectionFactoryInfoMap.keySet().iterator();
    while(allFactories.hasNext()) {
      list.add(connectionFactoryInfoMap.get(allFactories.next()));
    }
    return list;
  }
}
  
  


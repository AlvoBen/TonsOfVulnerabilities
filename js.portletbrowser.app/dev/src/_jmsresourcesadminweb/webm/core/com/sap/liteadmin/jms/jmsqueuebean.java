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

import com.sap.engine.admin.model.itsam.jsr77.jms.SAP_ITSAMJ2eeJMSQueueSettings;
import com.sap.engine.admin.model.itsam.jsr77.jms.SAP_ITSAMJ2eeJMSQueueWrapper;

public class JMSQueueBean {
  private static String QUEUES_QUERY = "*:cimclass=SAP_ITSAMJ2eeJMSQueue,*"; 
     
  private Logger log = Logger.getLogger(JMSQueueBean.class.getName()); 
  
  private HashMap<String, JMSQueueInfo> queueInfoMap = null;
  private HashMap<String, ObjectName> queueObjectNameMap = null;
  private boolean needsUpdate = false;  
  private MBeanServerConnection mbs;  
   
  
  public JMSQueueBean() {      
    queueInfoMap = new HashMap<String, JMSQueueInfo>();
    queueObjectNameMap = new HashMap<String, ObjectName>();
    needsUpdate = false;
    
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (NamingException ex) {
      ex.printStackTrace();
    }
  }

  protected List getQueues() {
    if (queueInfoMap.size() == 0 || needsUpdate) {        
      queueInfoMap = new HashMap<String, JMSQueueInfo>();
      queueObjectNameMap = new HashMap<String, ObjectName>();      
      updateQueuesList();
    }   
    
    return getQueueList();
  } 

  private List getQueueList() {
    List<JMSQueueInfo> queueList = new ArrayList<JMSQueueInfo>();
  
    Iterator allQueues = queueInfoMap.keySet().iterator();
    while(allQueues.hasNext()) {
      queueList.add(queueInfoMap.get(allQueues.next()));
    }
    return queueList;
  }
  
  protected JMSQueueInfo getSelectedQueue(String name) {
    return queueInfoMap.get(name);
  }
  
  protected String updateQueueProperties(String name) {
    try {
      ObjectName on = (ObjectName)queueObjectNameMap.get(name);
      if (on == null) {
        return "Cannot find MBean for jms queue " + name + "!";
      }
      
      CompositeData  cd = (CompositeData) mbs.getAttribute(on, "Settings");
      SAP_ITSAMJ2eeJMSQueueSettings tc  = (SAP_ITSAMJ2eeJMSQueueSettings ) SAP_ITSAMJ2eeJMSQueueWrapper.getSAP_ITSAMJ2eeJMSQueueSettingsForCData(cd);

      JMSQueueInfo queueInfo = getSelectedQueue(name);
      log.info("Change properties of " + name + " queue with new values:");
      log.info("                AgentKeepAliveTimeSeconds = " + queueInfo.getAgentKeepAliveTimeSeconds());
      log.info("                AverageMessageSize = " + queueInfo.getAverageMessageSize());
      log.info("                DeliveryAttemptsLimited = " + queueInfo.getDeliveryAttemptsLimited());
      log.info("                JMSDeliveryCountEnabled = " + queueInfo.getJmsDeliveryCountEnabled());
      log.info("                LoadBalanceBehavior = " + queueInfo.getLoadBalanceBehavior());
      log.info("                MaxDeliveryAttempts = " + queueInfo.getMaxDeliveryAttempts());
      
      tc.setAgentKeepAliveTimeSeconds(queueInfo.getAgentKeepAliveTimeSeconds());
      tc.setAverageMessageSize(queueInfo.getAverageMessageSize());
      tc.setDeliveryAttemptsLimited(queueInfo.getDeliveryAttemptsLimited());
      tc.setJMSDeliveryCountEnabled(queueInfo.getJmsDeliveryCountEnabled());
      tc.setLoadBalanceBehavior(queueInfo.getLoadBalanceBehavior());
      tc.setMaxDeliveryAttempts(queueInfo.getMaxDeliveryAttempts());
      
      cd = SAP_ITSAMJ2eeJMSQueueWrapper.getCDataForSAP_ITSAMJ2eeJMSQueueSettings(tc); 
      Object _result = mbs.invoke(on, "ApplyChanges", new Object[] { cd }, new String[] { "javax.management.openmbean.CompositeData"}); 
      needsUpdate = true;
      
      return (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
    } catch (Exception e) {      
      e.printStackTrace();
      return e.toString();
    }
  }
    
  private void updateQueuesList() {
    try {
      ObjectName pattern = new ObjectName(QUEUES_QUERY);
      Set result = mbs.queryNames(pattern, null);    
      Object topics[] = result.toArray();

      for (int i = 0; i < topics.length; i++) { // 2    
        ObjectName on = (ObjectName)topics[i];
        String lookupName = (String) mbs.getAttribute(on, "Name"); // THIS IS IMPORTANT - from JASEN
        
        String name = lookupName.substring(lookupName.lastIndexOf('/') + 1);
        String provider = lookupName.substring(lookupName.indexOf('/') + 1, lookupName.lastIndexOf('/'));
        if (provider != null && !provider.equals("default")) { 
          // only queues in default virtual provider
          continue;
        }
        JMSQueueInfo info = new JMSQueueInfo(name, lookupName);
        
        CompositeData queueSettings = (CompositeData) mbs.getAttribute(on, "Settings"); // THIS IS IMPORTANT - from JASEN        
        info.setAgentKeepAliveTimeSeconds((Integer)queueSettings.get("AgentKeepAliveTimeSeconds"));
        info.setAverageMessageSize((Integer)queueSettings.get("AverageMessageSize"));
        info.setCaption((String)queueSettings.get("Caption"));
        info.setConnectionId((Long)queueSettings.get("ConnectionId"));
        info.setDeliveryAttemptsLimited((Boolean)queueSettings.get("DeliveryAttemptsLimited"));
        info.setDescription((String)queueSettings.get("Description"));
        info.setDestinationName((String)queueSettings.get("DestinationName"));
        info.setDestinationType((Byte)queueSettings.get("DestinationType"));
        info.setElementName((String)queueSettings.get("ElementName"));
        info.setId((Integer)queueSettings.get("Id"));
        info.setIsTemprorary((Boolean)queueSettings.get("IsTemprorary"));
        info.setJmsDeliveryCountEnabled((Boolean)queueSettings.get("JMSDeliveryCountEnabled"));        
        info.setLoadBalanceBehavior((Byte)queueSettings.get("LoadBalanceBehavior"));
        info.setMaxDeliveryAttempts((Integer)queueSettings.get("MaxDeliveryAttempts"));
        info.setMemoryQueueMaxRowsToSelect((Integer)queueSettings.get("MemoryQueueMaxRowsToSelect"));
        info.setMemoryQueueSize((Integer)queueSettings.get("MemoryQueueSize"));
        info.setWorkListBufferSize((Integer)queueSettings.get("WorkListBufferSize"));
        info.setWorkListMaxRowsToSelect((Integer)queueSettings.get("WorkListMaxRowsToSelect"));        
        
        queueInfoMap.put(name, info);
        queueObjectNameMap.put(name, on);        
      }
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
    }   
  }

  protected String createNewQueue(String newResouceName) {
    boolean is = false;
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

      Object _result = mbs.invoke(on, "CreateQueue", 
          new Object[] {new String(newResouceName), new Boolean(is) },
          new String[] {String.class.getName(), Boolean.TYPE.getName() });
      result = (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
      
      needsUpdate = true;
    } catch (Exception e) {
      e.printStackTrace();
      result = e.toString() + ". See default trace for more info";
    }

    return result;
  }

  protected String removeQueues() {
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

      Iterator allQueues = queueInfoMap.keySet().iterator();
      while(allQueues.hasNext()) {
        JMSQueueInfo queueInfo = queueInfoMap.get(allQueues.next());
        if (queueInfo.isSelected()) {
          try {
            mbs.invoke(on, "RemoveDestination", new Object[]{new String(queueInfo.getLookupName())}, new String[]{String.class.getName()});
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
}


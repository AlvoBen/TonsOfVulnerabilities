package com.sap.liteadmin.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.itsam.jsr77.jms.SAP_ITSAMJ2eeJMSTopicSettings;
import com.sap.engine.admin.model.itsam.jsr77.jms.SAP_ITSAMJ2eeJMSTopicWrapper;

public class JMSTopicBean {
  private static String TOPICS_QUERY = "*:cimclass=SAP_ITSAMJ2eeJMSTopic,*";
  
  private Logger log = Logger.getLogger(JMSTopicBean.class.getName());
  
  private HashMap<String,JMSTopicInfo> topicInfoMap = null;
  private HashMap<String, ObjectName> topicObjectNameMap = null;
  private boolean needsUpdate = false; 
  
  private MBeanServerConnection mbs;
  
  public JMSTopicBean() {
    topicInfoMap = new HashMap<String,JMSTopicInfo>();
    topicObjectNameMap = new HashMap<String,ObjectName>();
    needsUpdate = false;
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (NamingException ex) {
      ex.printStackTrace();
    }
  }

  //public methods from UI  
  public List getTopics() {
    if (topicInfoMap.size() == 0 || needsUpdate) {      
      topicInfoMap = new HashMap<String,JMSTopicInfo>();
      topicObjectNameMap = new HashMap<String,ObjectName>();
      updateJMSTopicsList();
    }    
    return getTopicsList();
  }   
  
  protected JMSTopicInfo getSelectedTopic(String name) {    
    return topicInfoMap.get(name);
  }
  
  protected String updateTopicProperties(String name) {
    try {
      ObjectName on = (ObjectName)topicObjectNameMap.get(name);
      if (on == null) {
        return "Cannot find MBean for jms topic " + name + "!";
      }
      
      CompositeData  cd = (CompositeData) mbs.getAttribute(on, "Settings");
      SAP_ITSAMJ2eeJMSTopicSettings tc  = (SAP_ITSAMJ2eeJMSTopicSettings) SAP_ITSAMJ2eeJMSTopicWrapper.getSAP_ITSAMJ2eeJMSTopicSettingsForCData(cd);
  
      JMSTopicInfo topicInfo = getSelectedTopic(name);      
      log.info("Change properties of " + name + " topic with new values:");
      log.info("                AgentKeepAliveTimeSeconds = " + topicInfo.getAgentKeepAliveTimeSeconds());
      log.info("                AverageMessageSize = " + topicInfo.getAverageMessageSize());
      log.info("                JMSDeliveryCountEnabled = " + topicInfo.getJmsDeliveryCountEnabled());
      log.info("                MemoryQueueMaxRowsOnStartup = " + topicInfo.getMemoryQueueMaxRowsOnStartup());
      
      tc.setAgentKeepAliveTimeSeconds(topicInfo.getAgentKeepAliveTimeSeconds());
      tc.setAverageMessageSize(topicInfo.getAverageMessageSize());
      tc.setJMSDeliveryCountEnabled(topicInfo.getJmsDeliveryCountEnabled());
      tc.setMemoryQueueMaxRowsOnStartup(topicInfo.getMemoryQueueMaxRowsOnStartup());
  
      cd = SAP_ITSAMJ2eeJMSTopicWrapper.getCDataForSAP_ITSAMJ2eeJMSTopicSettings(tc); 
      Object _result = mbs.invoke(on, "ApplyChanges", new Object[] { cd }, new String[] { "javax.management.openmbean.CompositeData"}); 
      needsUpdate = true;    
      return (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
    } catch (Exception e) {      
      e.printStackTrace();
      return e.toString();
    }    
  }

  protected String createNewTopic(String newResouceName) {
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

      Object _result = mbs.invoke(on, "CreateTopic", 
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

  protected String removeTopics() {
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

      Iterator allTopics = topicInfoMap.keySet().iterator();
      while(allTopics.hasNext()) {
        JMSTopicInfo topicInfo = topicInfoMap.get(allTopics.next());
        if (topicInfo.isSelected()) {
          try {
            mbs.invoke(on, "RemoveDestination", new Object[]{new String(topicInfo.getLookupName())}, new String[]{String.class.getName()});
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
  
  private void updateJMSTopicsList() {
    try {            
      ObjectName pattern = new ObjectName(JMSTopicBean.TOPICS_QUERY);
      Set result = mbs.queryNames(pattern, null);    
      Object topics[] = result.toArray();

      for (int i = 0; i < topics.length; i++) { // 2    
        ObjectName on = (ObjectName)topics[i];
        String lookupName = (String) mbs.getAttribute(on, "Name"); // THIS IS IMPORTANT - from JASEN
        
        String name = lookupName.substring(lookupName.lastIndexOf('/') + 1);
        String provider = lookupName.substring(lookupName.indexOf('/') + 1, lookupName.lastIndexOf('/'));
        if (provider != null && !provider.equals("default")) { 
          // only topics in default virtual provider
          continue;
        }
        
        JMSTopicInfo info = new JMSTopicInfo(name, lookupName);
        CompositeData topicSettings = (CompositeData) mbs.getAttribute(on, "Settings"); // THIS IS IMPORTANT - from JASEN
        info.setAgentKeepAliveTimeSeconds((Integer)topicSettings.get("AgentKeepAliveTimeSeconds"));
        info.setAverageMessageSize((Integer)topicSettings.get("AverageMessageSize"));
        info.setCaption((String)topicSettings.get("Caption"));
        info.setConnectionId((Long)topicSettings.get("ConnectionId"));
        info.setDescription((String)topicSettings.get("Description"));
        info.setDestinationName((String)topicSettings.get("DestinationName"));
        info.setDestinationType((Byte)topicSettings.get("DestinationType"));
        info.setElementName((String)topicSettings.get("ElementName"));
        info.setId((Integer)topicSettings.get("Id"));
        info.setIsTemprorary((Boolean)topicSettings.get("IsTemprorary"));
        info.setJmsDeliveryCountEnabled((Boolean)topicSettings.get("JMSDeliveryCountEnabled"));
        info.setMemoryQueueMaxRowsOnStartup((Integer)topicSettings.get("MemoryQueueMaxRowsOnStartup"));
        info.setMemoryQueueSize((Integer)topicSettings.get("MemoryQueueSize"));
        info.setWorkListBufferSize((Integer)topicSettings.get("WorkListBufferSize"));
        info.setWorkListMaxRowsToSelect((Integer)topicSettings.get("WorkListMaxRowsToSelect"));     
       
        topicInfoMap.put(name, info);
        topicObjectNameMap.put(name, on);
      }
    } catch (Exception e) {      
      e.printStackTrace();
      FacesContext context = FacesContext.getCurrentInstance();
      FacesMessage msg = new FacesMessage("Error! " + e.toString() + "!");
      context.addMessage("" , msg);
    }    
  }
  
  private List getTopicsList() {
    List<JMSTopicInfo> list = new ArrayList<JMSTopicInfo>();
  
    Iterator allQueues = topicInfoMap.keySet().iterator();
    while(allQueues.hasNext()) {
      list.add(topicInfoMap.get(allQueues.next()));
    }
    return list;
  }

}



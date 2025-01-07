package com.sap.liteadmin.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 * 
 * @author Violeta Uzunova
 */
public class JMSResourcesBean {
  private static int IN_TOPICS_MODE = 1;
  private static int IN_QUEUES_MODE = 2;
  private static int IN_CONNECTION_FACTORIES_MODE = 3;
   
  private Logger log = Logger.getLogger(JMSResourcesBean.class.getName());
  private int mode = JMSResourcesBean.IN_TOPICS_MODE;
  
  private JMSTopicBean jmsTopicBean = null;
  private JMSQueueBean jmsQueueBean = null;  
  private JMSConnectionFactoriesBean jmsConnectionFacotriesBean = null;
  
  private JMSTopicInfo selectedJMSTopic = null; 
  private JMSQueueInfo selectedJMSQueue = null;
  private JMSConnectionFactoryInfo selectedJMSConnectionFactory = null;
  
  private String selectedResouceName = null;
  private boolean createSelected = false;
  private String newResouceName = null;
  private String newResouceType = null;
  private boolean messages = false;
  
  public JMSResourcesBean() {
    jmsTopicBean = new JMSTopicBean();
    jmsQueueBean = new JMSQueueBean();
    jmsConnectionFacotriesBean = new JMSConnectionFactoriesBean();
    
    selectedResouceName = null;
    mode = JMSResourcesBean.IN_TOPICS_MODE;
    createSelected = false;
    messages = false;
  }

  //public methods from UI  
  public List getTopics() {        
    return jmsTopicBean.getTopics();    
  }
  
  public List getQueues() {  
    return jmsQueueBean.getQueues();
  }  

  public List getConnectionFactories() {
    return jmsConnectionFacotriesBean.getFactories(); 
  }
  
  public void topicPropertiesListener(ActionEvent event) {
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    String jmsResoucesNameDetailed = obj.toString();      
   
    selectedJMSQueue = null;
    selectedJMSTopic = jmsTopicBean.getSelectedTopic(jmsResoucesNameDetailed);
    selectedJMSConnectionFactory = null;
    
    mode = JMSResourcesBean.IN_TOPICS_MODE; 
    if (selectedJMSTopic != null) {
      selectedResouceName = jmsResoucesNameDetailed;
    }  
  }

  public void queuePropertiesListener(ActionEvent event) {
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    String jmsResoucesNameDetailed = obj.toString();
    selectedJMSTopic = null;
    selectedJMSQueue = jmsQueueBean.getSelectedQueue(jmsResoucesNameDetailed);
    selectedJMSConnectionFactory = null;
    
    mode = JMSResourcesBean.IN_QUEUES_MODE; 
    if (selectedJMSQueue != null) {
      selectedResouceName = jmsResoucesNameDetailed;
    }
  }
  
  public void cfPropertiesListener(ActionEvent event) {
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    String jmsResoucesNameDetailed = obj.toString();    
        
    selectedJMSTopic = null;
    selectedJMSQueue = null;
    selectedJMSConnectionFactory = jmsConnectionFacotriesBean.getSelectedConnectionFactory(jmsResoucesNameDetailed);      
  
    mode = JMSResourcesBean.IN_CONNECTION_FACTORIES_MODE;
    if (selectedJMSConnectionFactory != null) {
      selectedResouceName = jmsResoucesNameDetailed;
    }
  }

  public JMSTopicInfo getSelectedJMSTopic() {    
    return selectedJMSTopic;
  }
  
  public JMSQueueInfo getSelectedJMSQueue(){
    return selectedJMSQueue;
  }
  
  public JMSConnectionFactoryInfo getSelectedJMSConnectionFacotry(){
    return selectedJMSConnectionFactory;
  }
  
  public String getSelectedResouceName() {
    log.info("get selectedResouceName");
    return selectedResouceName;
  }
  
  public String getSelectedResouceType() {
    if (mode == JMSResourcesBean.IN_TOPICS_MODE) {
      return "Topics";
    } else if (mode == JMSResourcesBean.IN_QUEUES_MODE) {
      return "Queues";
    } else if (mode == JMSResourcesBean.IN_CONNECTION_FACTORIES_MODE) {
      return "Connection Factories";
    }
    return "";
  }
  
  public String clearAll() {    
    selectedJMSTopic = null; 
    selectedJMSQueue = null;
    selectedJMSConnectionFactory = null;
    selectedResouceName = null;    
    createSelected = false;
    newResouceName = null;
    newResouceType = null;
    messages = false;
    return "";
  }
  
  public String updateTopicProperties() {
    String result = jmsTopicBean.updateTopicProperties(selectedJMSTopic.getName());
    result = "Update of the properties of the topic " + selectedJMSTopic.getName() + " finished with status: " + result;
    mode = JMSResourcesBean.IN_TOPICS_MODE;
    
    messages = true;
    FacesContext context = FacesContext.getCurrentInstance();
    FacesMessage msg = new FacesMessage(result);
    context.addMessage("" , msg); 
    
    return "";
  }
  
  public String updateQueueProperties() {    
    String result = jmsQueueBean.updateQueueProperties(selectedJMSQueue.getName());
    result = "Update of the properties of the queue " + selectedJMSQueue.getName() + " finished with status: " + result;
    mode = JMSResourcesBean.IN_QUEUES_MODE;
    
    messages = true;
    FacesContext context = FacesContext.getCurrentInstance();
    FacesMessage msg = new FacesMessage(result);
    context.addMessage("" , msg); 
    return "";
  }
  
  public String updateConnectionFactoryProperties() {    
    String result = jmsConnectionFacotriesBean.updateConnectionFactoryProperties(selectedJMSConnectionFactory.getName());
    result = "Update of the properties of the connection Factory " + selectedJMSConnectionFactory.getName() + " finished with status: " + result;
    mode = JMSResourcesBean.IN_CONNECTION_FACTORIES_MODE;
    
    messages = true;
    FacesContext context = FacesContext.getCurrentInstance();
    FacesMessage msg = new FacesMessage(result);
    context.addMessage("" , msg); 
    return "";
  }
  
  public int getMode() {
    return mode;
  }

  public void setMode(int t) {
	  //mode = t;
  }

  public boolean isCreateSelected() {
    return createSelected;
  }
  
  public String create() {
    createSelected = true;    
    return "";
  }  
  
  public String remove() {
    createSelected = false;
    messages = true;    
          
    String result1 = jmsTopicBean.removeTopics();
    result1 = "Operation for removing selected jms topics finished with status: " + result1;      
        
    String result2 = jmsQueueBean.removeQueues();
    result2 = "Operation for removing selected jms queues finished with status: " + result2;      
         
    String result3 = jmsConnectionFacotriesBean.removeConnectionFactories();
    result3 = "Operation for removing selected jms queues finished with status: " + result3;
        
    selectedJMSTopic = null;
    selectedJMSQueue = null;
    selectedJMSConnectionFactory = null;
    
    FacesContext context = FacesContext.getCurrentInstance();
    context.addMessage("" , new FacesMessage(result1));
    context.addMessage("" , new FacesMessage(result2));
    context.addMessage("" , new FacesMessage(result3));

    return "";
  }
  
  public String createResouce() {
    if (newResouceName == null || newResouceType == null) {
      return "";
    }
    String result = null;
    
    messages = true;
    byte resourceType = new Byte(newResouceType).byteValue();    
    if (resourceType == 7) {      // topic
      result = jmsTopicBean.createNewTopic(newResouceName);
      result = "Operation for creating new jms topic " + newResouceName + " finished with ststaus: " + result;
      
      selectedJMSTopic = jmsTopicBean.getSelectedTopic(newResouceName);     
      if (selectedJMSTopic != null) {
        selectedResouceName = newResouceName;
      } 
    } else if (resourceType == 8) { // queue
      result = jmsQueueBean.createNewQueue(newResouceName);
      result = "Operation for creating new jms queue " + newResouceName + " finished with status: " + result;
      
      selectedJMSQueue = jmsQueueBean.getSelectedQueue(newResouceName);     
      if (selectedJMSQueue != null) {
        selectedResouceName = newResouceName;
      }            
    } else { //connection factories 
      result = jmsConnectionFacotriesBean.createNewCF(newResouceName, resourceType);
      result = "Operation for creating new jms connection factory " + newResouceName + " finished with statsus: " + result;
      selectedJMSConnectionFactory = jmsConnectionFacotriesBean.getSelectedConnectionFactory(newResouceName);     
      if (selectedJMSConnectionFactory != null) {
        selectedResouceName = newResouceName;
      } 
    }   
    createSelected = false;
    newResouceName = null;
    newResouceType = null;
    
    FacesContext context = FacesContext.getCurrentInstance();
    FacesMessage msg = new FacesMessage(result);
    context.addMessage("" , msg);  
    return "";
  }
  
  public String getNewResouceName() {
    return newResouceName;
  }
  
  public void setNewResouceName(String newResouceName) {
    this.newResouceName = newResouceName;
  }
  
  public String getNewResouceType() {
    return newResouceType;
  }
  
  public void setNewResouceType(String newResouceType) {    
    this.newResouceType = newResouceType;
  }  
  
  public List getResouceTypesList() {    
    ArrayList list = new ArrayList();
    list.add(new SelectItem("1", "Connection Factory"));    //1
    list.add(new SelectItem("2", "TopicConnectionFactory"));  //2
    list.add(new SelectItem("3", "QueueConnectionFactory"));   //3 
    list.add(new SelectItem("4", "XATopicConnectionFactory"));  //4
    list.add(new SelectItem("5", "XAQueueConnectionFactory"));  //5
    list.add(new SelectItem("6", "XAConnectionFactory"));  //6
    list.add(new SelectItem("7", "Topic"));  //7
    list.add(new SelectItem("8", "Queue"));  //8
    return list;    
  }
  
  public boolean isMessages() {
    return messages;
  }  
}

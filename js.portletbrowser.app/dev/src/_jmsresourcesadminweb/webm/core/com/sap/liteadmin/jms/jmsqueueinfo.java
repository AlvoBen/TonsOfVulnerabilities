package com.sap.liteadmin.jms;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public class JMSQueueInfo {
  
  private String name;
  private String lookupName;
  private Integer agentKeepAliveTimeSeconds;
  private Integer averageMessageSize;
  private String caption;
  private Long connectionId;
  private Boolean deliveryAttemptsLimited;
  private String description;
  private String destinationName;
  private Byte destinationType;
  private String elementName;
  private Integer id;
  private Boolean isTemprorary;
  private Boolean jmsDeliveryCountEnabled;
  private Byte loadBalanceBehavior;
  private Integer maxDeliveryAttempts;
  private Integer memoryQueueMaxRowsToSelect;
  private Integer memoryQueueSize;
  private Integer workListBufferSize;
  private Integer workListMaxRowsToSelect;
  private boolean selected;

  public JMSQueueInfo(String name, String lookupName) {
    this.name = name;
    this.lookupName = lookupName;
    this.selected = false;
  }

  public String getLookupName() {
    return lookupName;
  }

  public String getName() {
    return name;
  }

  public Integer getAgentKeepAliveTimeSeconds() {
    return agentKeepAliveTimeSeconds;
  }

  public void setAgentKeepAliveTimeSeconds(Integer agentKeepAliveTimeSeconds) {
    this.agentKeepAliveTimeSeconds = agentKeepAliveTimeSeconds;
  }

  public Integer getAverageMessageSize() {
    return averageMessageSize;
  }

  public void setAverageMessageSize(Integer averageMessageSize) {
    this.averageMessageSize = averageMessageSize;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public Long getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(Long connectionId) {
    this.connectionId = connectionId;
  }

  public Boolean getDeliveryAttemptsLimited() {
    return deliveryAttemptsLimited;
  }

  public void setDeliveryAttemptsLimited(Boolean deliveryAttemptsLimited) {
    this.deliveryAttemptsLimited = deliveryAttemptsLimited;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDestinationName() {
    return destinationName;
  }

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  public Byte getDestinationType() {
    return destinationType;
  }

  public void setDestinationType(Byte destinationType) {
    this.destinationType = destinationType;
  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Boolean getIsTemprorary() {
    return isTemprorary;
  }

  public void setIsTemprorary(Boolean isTemprorary) {
    this.isTemprorary = isTemprorary;
  }

  public Boolean getJmsDeliveryCountEnabled() {
    return jmsDeliveryCountEnabled;
  }

  public void setJmsDeliveryCountEnabled(Boolean jmsDeliveryCountEnabled) {
    this.jmsDeliveryCountEnabled = jmsDeliveryCountEnabled;
  }

  public Byte getLoadBalanceBehavior() {
    return loadBalanceBehavior;
  }

  public List getLoadBalanceBehaviorList() {    
    ArrayList list = new ArrayList();
    list.add(new SelectItem("1", "Exclusive"));    
    list.add(new SelectItem("3", "Round-robin"));  
    return list;    
  }

  public void setLoadBalanceBehavior(String loadBalanceBehavior) {
    this.loadBalanceBehavior = new Byte(loadBalanceBehavior);
  }
  
  public void setLoadBalanceBehavior(Byte loadBalanceBehavior) {
    this.loadBalanceBehavior = loadBalanceBehavior;
  }

  public Integer getMaxDeliveryAttempts() {
    return maxDeliveryAttempts;
  }

  public void setMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
    this.maxDeliveryAttempts = maxDeliveryAttempts;
  }

  public Integer getMemoryQueueMaxRowsToSelect() {
    return memoryQueueMaxRowsToSelect;
  }

  public void setMemoryQueueMaxRowsToSelect(Integer memoryQueueMaxRowsToSelect) {
    this.memoryQueueMaxRowsToSelect = memoryQueueMaxRowsToSelect;
  }

  public Integer getMemoryQueueSize() {
    return memoryQueueSize;
  }

  public void setMemoryQueueSize(Integer memoryQueueSize) {
    this.memoryQueueSize = memoryQueueSize;
  }

  public Integer getWorkListBufferSize() {
    return workListBufferSize;
  }

  public void setWorkListBufferSize(Integer workListBufferSize) {
    this.workListBufferSize = workListBufferSize;
  }

  public Integer getWorkListMaxRowsToSelect() {
    return workListMaxRowsToSelect;
  }

  public void setWorkListMaxRowsToSelect(Integer workListMaxRowsToSelect) {
    this.workListMaxRowsToSelect = workListMaxRowsToSelect;
  }
  
  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}

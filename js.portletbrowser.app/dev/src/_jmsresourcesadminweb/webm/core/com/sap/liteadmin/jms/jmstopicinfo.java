package com.sap.liteadmin.jms;

import java.util.logging.Logger;

public class JMSTopicInfo {
  
  private String name;
  private String lookupName;
  private Integer agentKeepAliveTimeSeconds;
  private Integer averageMessageSize;
  private String caption;
  private Long connectionId;
  private String description;
  private String destinationName;
  private Byte destinationType;
  private String elementName;
  private Integer id;
  private Boolean isTemprorary;
  private Boolean jmsDeliveryCountEnabled;
  private Integer memoryQueueMaxRowsOnStartup;
  private String memoryQueueMaxRowsToSelect;
  private Integer memoryQueueSize;
  private Integer workListBufferSize;
  private Integer workListMaxRowsToSelect;
  private boolean selected = false;
  
  private Logger log = Logger.getLogger(JMSTopicBean.class.getName());

  public JMSTopicInfo(String name, String lookupName) {
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

  public Integer getMemoryQueueMaxRowsOnStartup() {
    return memoryQueueMaxRowsOnStartup;
  }

  public void setMemoryQueueMaxRowsOnStartup(Integer memoryQueueMaxRowsOnStartup) {
    this.memoryQueueMaxRowsOnStartup = memoryQueueMaxRowsOnStartup;
  }

  public String getMemoryQueueMaxRowsToSelect() {
    return memoryQueueMaxRowsToSelect;
  }

  public void setMemoryQueueMaxRowsToSelect(String memoryQueueMaxRowsToSelect) {
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

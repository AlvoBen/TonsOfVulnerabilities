package com.sap.jms.client.connection;

public class Subscription implements java.io.Serializable {
  
    public static enum ConsumerType { QueueReceiver, QueueBrowser, TopicSubscriber, DurableSubscriber }
    
    /* Note - it seems it's much better to have descendant of Subscription - DurableSubscriberInfo, QueueBrowserInfo,
     * etc., or something like this instead of using such enumeration
     * */
    
    ConsumerType type;
    String selector;
    boolean started;    
    //the buffer size in the client 
    int clientLimit;
    long connectionId;
    long clientConsumerId;
    int destinationId;
    boolean connectionConsumer;
    
    public void setConsumerType(ConsumerType type) {
        this.type = type;        
    }
    
    public ConsumerType getConsumerType() {
        return type;        
    }
    
    public void setSelector(String selector) {
        this.selector = selector;
    }
    
    public String getSelector() {
        return selector;
    }    
    
    public void setStarted(boolean started) {
        this.started = started;
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public void setClientLimit(int clientLimit) {
        this.clientLimit = clientLimit;
    }
    
    public int getClientLimit() {
        return clientLimit;
    }
    
    public void setConnectionId(long connectionId) {
    	this.connectionId = connectionId;    	
    }
    
    public long getConnectionId() {
    	return connectionId;    	
    }
    
    public void setClientConsumerId(long clientConsumerId) {
    	this.clientConsumerId = clientConsumerId;    	
    }
    
    public long getClientConsumerId() {
    	return clientConsumerId;    	
    }
    
    public void setDestinationId(int destinationId) {
    	this.destinationId = destinationId;
    }
    
    public int getDestinationId() {
    	return destinationId;
    }
    
    public void setConnectionConsumer(boolean connectionConsumer) {
    	this.connectionConsumer = connectionConsumer;
    }
    
    public boolean isConnectionConsumer() {
    	return connectionConsumer;
    }
    
    //meaningful only for topics
    String durableSubscriberName;
    boolean noLocal;
    int subscriptionId;
    int singletonId = -1;
    
    public void setDurableSubscriberName(String durableSubscriberName) {
        this.durableSubscriberName = durableSubscriberName;
    }

    public String getDurableSubscriberName() {
        return durableSubscriberName;
    }
    
    public void setNoLocal(boolean noLocal) {
        this.noLocal = noLocal;
    }
    
    public boolean isNoLocal() {
        return noLocal;
    }
    
    public void setSubscriptionId(int subscriptionId) {
    	this.subscriptionId = subscriptionId;    	
    }
    
    public int getSubscriptionId() {
    	return subscriptionId;
    }
    
    public void setSingletonId(int singletonId) {
    	this.singletonId = singletonId;    	
    }
    
    public int getSingletonId() {
    	return singletonId;
    }
    
    public String toString() {
        StringBuffer text = new StringBuffer();
        text.append(" [ ");        
        text.append(super.toString());
        text.append(", type = " + getConsumerType());        
        text.append(", selector = " + getSelector());
        text.append(", started = " + isStarted());        
        text.append(", connectionId = " + getConnectionId());        
        text.append(", clientConsumerId = " + getClientConsumerId());
        text.append(", destinationId = " + getDestinationId());        
        text.append(", clientLimit = " + getClientLimit());
        text.append(", connectionConsumer = " + isConnectionConsumer());
        text.append(", durableSubscriberName = " + getDurableSubscriberName());        
        text.append(", noLocal = " + isNoLocal());        
        text.append(", subscriptionId = " + getSubscriptionId());
        text.append(", singletonId = " + getSingletonId());           
        text.append(" ] ");
        return text.toString();
    }
}

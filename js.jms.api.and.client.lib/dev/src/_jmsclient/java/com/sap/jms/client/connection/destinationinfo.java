package com.sap.jms.client.connection;

public class DestinationInfo implements java.io.Serializable {
    
    public static enum DestinationType { QUEUE, TOPIC };
    
    private int id;
    private DestinationType type;
    private String name;
    private String vpName;
   
    public void setId(int id) {
        this.id = id;
    }
   
    public int getId() {
        return id;
    }
   
    public void setType(DestinationType type) {
        this.type = type;
    }
   
    public DestinationType getType() {
        return type;
    }
    
    public boolean isQueue() {
    	return getType() == DestinationType.QUEUE;
    }
    
    public boolean isTopic() {
    	return getType() == DestinationType.TOPIC;
    }    
    
    public void setName(String name) {
    	this.name = name;
    }
        
	public String getName() {
		return name;
	}
	
	public boolean isTemporary() {
		boolean temporary = getId() < 0;
		return temporary;
	}	
	
	public void setVpName(String vpName) {
		this.vpName = vpName;
	}
	
	public String getVpName() {	
		return vpName;
	}
    
    @Override
    public int hashCode(){
        return id;        
    }
    
    @Override
    public boolean equals(Object other){
        if (this == other) {
            return true;
        }
        if (!(other instanceof DestinationInfo)) {
            return false;
        }
        return this.id == ((DestinationInfo)other).id;            
    }    
    
    public String toString() {
        StringBuffer text = new StringBuffer();
        text.append(" [ ");       
        text.append(super.toString());
        text.append(", ID = " + getId());
        text.append(", type = " + getType());
        text.append(", name = " + getName());        
        text.append(", vp = " + getVpName());        
        text.append(", isTemporary = " + isTemporary());        
        text.append(" ] ");       
        return text.toString();
    }
    
}

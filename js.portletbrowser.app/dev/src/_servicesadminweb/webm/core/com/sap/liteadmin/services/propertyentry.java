package com.sap.liteadmin.services;

import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

public class PropertyEntry {
    public static final int ENTRY_TYPE_ONLINE_MODIFIABLE = 128;

  
	private String key = null;
	private String value = null;
    private boolean isChanged = false;
    private String description = "";
    private boolean isOnlineModifable = false;
	
	private Logger log = Logger.getLogger(PropertyEntry.class.getName());
	
	public PropertyEntry(String key, String value) {	
		this.key = key;
		this.value = value;
	}

	/**
	 * @return Returns the key.
	 */
	public String getKey() {		
		return key;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	
    /**
     * @return Returns the key.
     */
    public void setKey(String key) {        
        this.key = key;
    }

    /**
     * @return Returns the value.
     */
    public void setValue(String value) {
        this.value = value;
    }
	    
    
    public void setFlagValueChanged(ValueChangeEvent event) {
      isChanged = true;
    }

    public boolean isChanged() {
      return isChanged;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public boolean isOnlineModifable() {
      return isOnlineModifable;
    }

    public void setOnlineModifable(boolean isOnlineModifable) {
      this.isOnlineModifable = isOnlineModifable;
    }
}

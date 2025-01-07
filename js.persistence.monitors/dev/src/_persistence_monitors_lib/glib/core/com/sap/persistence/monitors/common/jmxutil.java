package com.sap.persistence.monitors.common;



import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.sap.jmx.ObjectNameFactory;

public class JmxUtil {
	

	    
	
	private ObjectName globalQuery = null;
    private ObjectName localQuery  = null;

	    public ObjectName getQuery(boolean global, String type, String name) throws MalformedObjectNameException {
	  
	    	if (global){
	    		if (this.globalQuery == null) {
	 	             this.globalQuery = 
	 	                ObjectNameFactory.getPatternForServerChild(type, null);
	 	        }
	 	        return this.globalQuery;
	    	} else
	    	{
	    	  if (this.localQuery == null) { 
	              this.localQuery = 
	            	  ObjectNameFactory.getNameForServerChildPerNode(
	                type, name, null, null);
	    	  }
	        return this.localQuery;
	    	}
	    }

	   
	    
	       
}

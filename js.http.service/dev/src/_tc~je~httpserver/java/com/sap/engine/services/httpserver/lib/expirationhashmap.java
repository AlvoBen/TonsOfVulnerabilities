package com.sap.engine.services.httpserver.lib;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sap.engine.services.httpserver.server.errorreport.ErrorCategorizationEntry;

/**
 * ExpirationHashMap is meant to optimize storing of ErrorCategorizationEntry objects. On adding a new object the map is checked
 * if the eldest (most long ago accessed) object has expired meaning - its lastErrorReport's validity period has passed. 
 * If so the outdated ErrorCategorizationEntry is removed from the map. 
 * This remove rule applies only for ErrorCategorizationEntry objects.
 * If the values of the hash map are instances of other types - such objects are never removed automatically.
 *  
 * @author Polina Genova, I043824
 *
 * @param <K>
 * @param <V>
 */
public class ExpirationHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 4032529370747988651L;
	private long validityPeriod = 24*60*60;  //In seconds. Default validity period is 24 hours.
	
	public ExpirationHashMap(int initCapacity){
		super(initCapacity, 1.0f, true);
	}
	
    public ExpirationHashMap(int initCapacity, long validityPeriod) {
	    this(initCapacity);
	    this.validityPeriod = validityPeriod;
	}


	//check if the eldest (most long ago accessed) object should be removed
	  @Override
		protected boolean removeEldestEntry(Map.Entry eldest) {
			if 	((validityPeriod <= 0) || !(eldest.getValue() instanceof ErrorCategorizationEntry)) {
				return false;
			} 
			Date lastErrorReportDate  = ((ErrorCategorizationEntry) eldest.getValue()).getLastErrorReportTime(); 
			Date currentDate = new Date();
			 if (lastErrorReportDate==null || (currentDate.getTime() - lastErrorReportDate.getTime() >= validityPeriod*1000)) {
					return true;
			  }else {
					return false;
			  }
		}
	  
	public long getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(long validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	  
	
	
}

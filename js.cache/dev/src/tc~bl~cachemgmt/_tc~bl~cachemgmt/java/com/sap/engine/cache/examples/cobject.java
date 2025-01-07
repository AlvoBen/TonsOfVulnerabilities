/*
 * Created on 2004.7.19
 *
 */
package com.sap.engine.cache.examples;

import java.io.Serializable;
import java.util.Date;

/**
 * @author petio-p
 * This object is used by examples as cached object
 *
 */
public class CObject implements Serializable {
  
  static final long serialVersionUID = 9018990980336549568L;
  
  private String name = null;
  private long timestamp = -1;
  
  public CObject(String name) {
    this.name = name;
    this.timestamp = System.currentTimeMillis();
  }
  
	/**
	 * @return The name of the Cached object
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The creation time of the Cached object
	 */
	public long getTimestamp() {
		return timestamp;
	}
  
  public String toString() {
    return "Name: " + name + "; Timestamp: " + new Date(timestamp);
  }
  
  public int hashCode() {
		return name.hashCode();
  }
  
  public boolean equals(Object obj) {
		return name.equals(obj);
  }

}

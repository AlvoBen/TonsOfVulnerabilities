/*
 * Created on 2005-2-7
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote.model;

/**
 * Represents an abstraction for SDU(Software Deployment Unit).
 * 
 * @author lalo-i
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.model.Sdu</code>.
 */
public interface Sdu {
  
  /**
   * @return the location of this <code>Sdu</code>
   */
  public String getLocation(); 
  
  /**
   * @return the name of this <code>Sdu</code>
   */
  public String getName(); 

  /**
   * @return the vendor of this <code>Sdu</code>
   */
  public String getVendor(); 

  /**
   * @return the (build) version of this <code>Sdu</code>
   */
  public String getVersion();
  
  /**
   * @return the file location of the <code>Sdu</code>s archive or null
   *         if the archive is already been undeployed  
   */
  public String getArchiveFileLocation();
  
  /**
   * @return String representation of this <code>Sdu</code>
   */
  public String toString(); 

}

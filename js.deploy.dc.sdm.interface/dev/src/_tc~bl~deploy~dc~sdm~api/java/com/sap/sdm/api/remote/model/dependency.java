/*
 * Created on 2005-2-8
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote.model;

/**
 * Represents dependency between <code>Sda</code>s. If <code>Sda</code> named <i><b>A</b></i>
 * depends on <code>Sda</code> named <i><b>B</b></i>, then this class will represent the properties
 * of <i><b>B</b></i> which can be retrieved by calling 
 * {@link com.sap.sdm.api.remote.model.Sda#getDependencies()} of <i><b>A</b></i>.
 * 
 * @author lalo-i
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.model.Dependency</code>.
 */
public interface Dependency {

  /**
   * @return the name of the dependant component
   */
  public String getName();
  
  /**
   * @return the vendor of the dependant component
   */
  public String getVendor();
}

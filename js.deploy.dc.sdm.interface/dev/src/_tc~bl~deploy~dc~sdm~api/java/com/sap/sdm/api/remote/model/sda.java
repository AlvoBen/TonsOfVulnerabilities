/*
 * Created on 2005-2-7
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote.model;

/**
 * Represents an abstraction of SDA(Software Deployment Archive)
 * 
 * @author lalo-i
 */
public interface Sda extends Sdu {
  
  /**
   * The value that is returned is one of the following set of
   * software types:
   * <ul>
   * <li>JAVA</li>
   * <li>FS</li>
   * <li>DBSC</li> 
   * <li>JDDSCHEMA</li> 
   * <li>engine-bootstrap</li> 
   * <li>engine-kernel</li> 
   * <li>primary-service</li> 
   * <li>primary-library</li> 
   * <li>primary-interface</li> 
   * <li>service</li> 
   * <li>library</li> 
   * <li>interface</li> 
   * <li>JAVA-LIB</li> 
   * <li>J2EE</li>
   * <li>single-module</li> 
   * <ul>
   * 
   * @return the software type of this <code>Sda</code>
   * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
   * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.model.Sda</code>.
   */
  public String getSoftwareType();
  
  /**
   * An array with all the deploy-time dependencies of component
   * represented by this <code>Sda</code> is returned.
   * 
   * @return an array of dependencies
   */
  public Dependency[] getDependencies();

}

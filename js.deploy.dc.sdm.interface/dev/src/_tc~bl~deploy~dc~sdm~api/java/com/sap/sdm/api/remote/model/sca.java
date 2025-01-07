/*
 * Created on 2005-2-7
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote.model;

/**
 * Represents an abstraction of SCA(Software Component Archive)
 * 
 * @author lalo-i
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.model.Sca</code>.
 */
public interface Sca extends Sdu {

  /**
   * @return release number of this <code>Sca</code> or null if there
   *         is no release information stored in <code>Sca</code>'s archive
   */
  public String getRelease();
  
  /**
   * @return SP number of this <code>Sca</code> or null if there
   *         is no SP number information stored in <code>Sca</code>'s archive
   */
  public String getSPNumber();
  
  /**
   * @return SP patch level of this <code>Sca</code> or null if there
   *         is no SP patch level information stored in <code>Sca</code>'s archive
   */
  public String getSPPatchLevel();  

  /**
   * @return an array of all the <code>Sda</code>s that are
   *         part of this <code>Sca</code>
   */
  public Sda[] getSdas();
}

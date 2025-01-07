package com.sap.sdm.api.remote;

/**
 * Represents a rule regarding handling of different versions of DCs
 * which could be used for deployment.
 * Use 
 * {@link com.sap.sdm.api.remote.HelperFactory#createComponentVersionHandlingRule(int)}
 * to create an instance of this interface.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @see com.sap.sdm.api.remote.DeployProcessor#setComponentVersionHandlingRule(ComponentVersionHandlingRule)
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule</code>.
 */
public interface ComponentVersionHandlingRule {

  /** 
   * Returns an <code>int</code> representation of this 
   * <code>ComponentVersionHandlingRule</code>.
   * 
   * @return an <code>int</code> representation of this 
   *          <code>ComponentVersionHandlingRule</code>
   * @see com.sap.sdm.api.remote.ComponentVersionHandlingRules 
   */
  public int getRuleAsInt() throws RemoteException;
  
  /** 
   * Returns a <code>String</code> representation of this 
   * <code>ComponentVersionHandlingRule</code>.
   * 
   * @return a <code>String</code> representation of this 
   *          <code>ComponentVersionHandlingRule</code>
   * @see com.sap.sdm.api.remote.ComponentVersionHandlingRules 
   */
  public String getRuleAsString() throws RemoteException;
  
  /** 
   * Returns a <code>String</code> containing a short 
   * description of this <code>ComponentVersionHandlingRule</code>.
   * 
   * @return a <code>String</code> containing a short 
   * description of this <code>ComponentVersionHandlingRule</code>
   */
  public String getRuleDescription() throws RemoteException;
  
}

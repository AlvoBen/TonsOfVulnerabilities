package com.sap.sdm.api.remote;

/**
 * Represents the configuration of the SDM server.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.ComponentManager</code>.
 */
public interface SDMConfig {
  
  /**
   * Returns the defined substitution variables.
   * 
   * @return a <code>DynSizeParamContainer</code> containing the defined
   *          substitution variables as <code>Param</code> objects
   */
  public DynSizeParamContainer getSubstVarContainer() throws RemoteException;
  
  /**
   * Returns the registered target systems.
   * 
   * @return a <code>TargetSystemContainer</code> containing the registered
   *          target systems
   */
  public TargetSystemContainer getTargetSystemContainer() throws RemoteException;

  /**
   * Returns the supported server types.
   * 
   * @return an array of <code>ServerType</code>
   */
  public ServerType[] getServerTypes() throws RemoteException;

}

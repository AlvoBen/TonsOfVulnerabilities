package com.sap.sdm.api.remote;

/**
 * Represents a target system.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public interface TargetSystem {

  /**
   * Returns the ID of this <code>TargetSystem</code>.
   * 
   * @return the ID of the target system
   */
  public String getID() throws RemoteException;
  
  /**
   * Returns a textual description of this <code>TargetSystem</code>.
   * 
   * @return a textual description of the target system; may be 
   *          <code>null</code>
   */
  public String getDescription() throws RemoteException;
  
  /**
   * Returns the server type of this <code>TargetSystem</code>.
   * 
   * @return the <code>ServerType</code> of this <code>TargetSystem</code>
   */
  public ServerType getType() throws RemoteException;
  
  /**
   * Returns the configuration of this <code>TargetSystem</code>.
   * 
   * @return a <code>FixedSizeParamContainer</code>, containing the 
   *          configuration of this <code>TargetSystem</code>
   */
  public FixedSizeParamContainer getConfiguration() throws RemoteException;
  
  /**
   * Indicates whether the configuration of this <code>TargetSystem</code> is
   * complete.
   * 
   * @return <code>true</code> if the configuration is complete;
   *          <code>false</code> otherwise
   */
  public boolean isConfigurationComplete() throws RemoteException;

}

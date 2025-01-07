package com.sap.sdm.api.remote;

/**
 * Represents a server type.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public interface ServerType {

  /**
   * Returns the name of this <code>ServerType</code>.
   * 
   * @return the name of the server type
   */
  public String getName() throws RemoteException;
  
  /**
   * Returns a textual description of this <code>ServerType</code>.
   * 
   * @return a textual description of this server type; may be 
   *          <code>null</code>
   */
  public String getDescription() throws RemoteException;

}

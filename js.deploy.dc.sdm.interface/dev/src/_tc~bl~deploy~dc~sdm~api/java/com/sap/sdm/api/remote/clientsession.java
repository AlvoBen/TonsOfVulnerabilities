package com.sap.sdm.api.remote;


/**
 * Represents a client session on an SDM server. A client session represents
 * a logical connection to an SDM server that may last longer than a physical
 * connection to a server.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.Client</code>.
 */
public interface ClientSession {

  /**
   * Returns a <code>Client</code> object that provides the functional methods
   * for a client.
   * 
   * @return a <code>Client</code>
   */
  public Client getClient() throws RemoteException;

  /**
   * Passivates this <code>ClientSession</code>. Passivating a 
   * <code>ClientSession</code> means that the physical connection to the
   * SDM server will be closed while the logical connection remains established.
   * A passivated <code>ClientSession</code> need not be explicitely activated,
   * instead it suffices to invoke any of the methods of <code>Client</code>.
   */
  public void passivateSession() throws RemoteException;

  /**
   * Closes this <code>ClientSession</code>. Closing a 
   * <code>ClientSession</code> means that both the physical and logical 
   * connection to the SDM server will be closed. Once a 
   * <code>ClientSession</code> has been closed, it becomes invalid.
   */
  public void closeSession() throws RemoteException;

}

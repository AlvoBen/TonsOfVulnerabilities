package com.sap.sdm.api.remote;

import com.sap.sdm.api.remote.cvers.CVersManager;

/**
 * An entry point to the functional methods provided by SDM for a remote client.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.Client</code>.

 */
public interface Client {
  
  /**
   * Returns the version information for the SDM Server this Client
   * is connected to.
   * You should compare this version number with the one returned
   * by {@link com.sap.sdm.api.remote.ClientSessionFactory#getAPIClientVersion}
   * to check whether the server is of the same version as the client.
   * 
   * @return an <code>int</code> containing the version number
   */
  public int getAPIServerVersion() throws RemoteException;

  /**
   * Returns an <code>SDMConfig</code>.
   * 
   * @return an <code>SDMConfig</code>
   */
  public SDMConfig getSDMConfiguration() throws RemoteException;
  
  /**
   * Returns a <code>RepositoryExplorer</code>. This method is
   * new with API client version 6 as long as all related with
   * Repository Explorer model objects and methods.
   * 
   * @return a <code>RepositoryExplorer</code>
   */
  public RepositoryExplorer getRepositoryExplorer() throws RemoteException;  
  
  /**
   * Returns a <code>DeployProcessor</code>.
   * 
   * @return a <code>DeployProcessor</code>
   */
  public DeployProcessor getDeployProcessor() throws RemoteException;
  
  /**
   * Returns a <code>UnDeployProcessor</code>.
   * 
   * This method is new with API client version 4.
   *
   * @return a <code>UnDeployProcessor</code>
   */
  public UnDeployProcessor getUnDeployProcessor() throws RemoteException;
  
  /**
   * Returns a <code>HelperFactory</code>.
   * 
   * @return a <code>HelperFactory</code>
   */
  public HelperFactory getHelperFactory() throws RemoteException;

  /**
   * Returns a <code>CVersManager</code>.
   * 
   * This method as long as all the functionality related to CVERS table
   * is new with API client version 8.
   *
   * @return a <code>CVersManager</code>
   */
  public CVersManager getCVersManager() throws RemoteException;  
  
  /**
   * Returns the part of the SDM log which belongs to this
   * client. This method is deprecated. It was replaced by 
   * {@link com.sap.sdm.api.remote.ClientLog#getAsURL}
   * 
   * @return a <code>URLMimic</code>
   * @deprecated
   */
  public URLMimic getClientLog() throws RemoteException;
  
  /**
   * Returns the <code>ClientLog</code>.
   * 
   * @return a <code>ClientLog</code>
   */
  public ClientLog getLog() throws RemoteException;
  
}

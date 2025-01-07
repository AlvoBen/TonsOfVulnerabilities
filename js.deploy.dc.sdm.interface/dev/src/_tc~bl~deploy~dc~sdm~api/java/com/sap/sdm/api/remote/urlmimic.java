package com.sap.sdm.api.remote;

/**
 * Represents a Uniform Resource Locator, mimicking the 
 * <code>java.net.URL</code> class.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public interface URLMimic {
  /**
   * Returns the protocol part of the represented URL.
   * 
   * @return the name of the protocol 
   */
  public String getProtocol() throws RemoteException;
  
  /**
   * Returns the host part of the represented URL.
   * 
   * @return the name of the host 
   */
  public String getHost() throws RemoteException;
  
  /**
   * Returns the port part of the represented URL.
   * 
   * @return the port number on the host 
   */
  public int    getPort() throws RemoteException;
  
  /**
   * Returns the file part of the represented URL.
   * 
   * @return the file name on the host 
   */
  public String getFile() throws RemoteException;

}

package com.sap.sdm.api.remote;

import java.io.IOException;

/**
 * Represents the part of the SDM log which belongs to this client.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.util.DALog</code>.
 */
public interface ClientLog {

  /**
   * Returns a URL identifying the part of the SDM log which 
   * belongs to this client.
   * 
   * @return a <code>URLMimic</code>
   */
  public URLMimic getAsURL() throws RemoteException;
  
  /**
   * Returns an array of Strings containing the part of the log text
   * of the SDM log which belongs to this client.
   * 
   * @return a <code>String[]</code>
   */
  public String[] getAsStrings() throws RemoteException, IOException;
}

package com.sap.sdm.api.remote;

import java.io.IOException;

/**
 * An abstract container for <code>Param</code> objects.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.params.ParamsProcessor</code>.
 * 
 */
public interface AbstractParamContainer {

  /** 
   * Returns all <code>Param</code> objects in this 
   * <code>AbstractParamContainer</code>.
   * 
   * @return an array of <code>Param</code>
   */
  public Param[] getParams() throws RemoteException;
  
  /** 
   * Returns the <code>Param</code> with the specified name, if it is
   * contained in this <code>AbstractParamContainer</code>.
   * 
   * @param name the name of the parameter
   * @return the <code>Param</code> with the specified name, if contained;
   *          <code>null</code> otherwise
   * @throws NullPointerException if <code>name</code> is <code>null</code>
   */  
  public Param getParamByName(String name) throws RemoteException;
  
  /**
   * Saves the container and its contents to the persistent SDM repository.
   * 
   * @throws IOException if an I/O exception has occurred
   */
  public void save() throws RemoteException, IOException;
  
}

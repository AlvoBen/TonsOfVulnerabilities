package com.sap.sdm.api.remote;

/**
 * Represents a parameter type.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public interface ParamType {

  /** 
   * Returns an <code>int</code> representation of this <code>ParamType</code>.
   * 
   * @return an <code>int</code> representation of this <code>ParamType</code>
   * @see com.sap.sdm.api.remote.ParamTypes 
   */
  public int getTypeAsInt() throws RemoteException;
  
  /** 
   * Returns a <code>String</code> representation of this 
   * <code>ParamType</code>.
   * 
   * @return a <code>String</code> representation of this 
   *          <code>ParamType</code>
   * @see com.sap.sdm.api.remote.ParamTypes 
   */
  public String getTypeAsString() throws RemoteException;
  
}

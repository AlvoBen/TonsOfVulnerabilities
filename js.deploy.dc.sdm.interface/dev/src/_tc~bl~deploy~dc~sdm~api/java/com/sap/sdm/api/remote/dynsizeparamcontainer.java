package com.sap.sdm.api.remote;

/**
 * A container for a variable number of <code>Param</code> objects. 
 * In a <code>DynSizeParamContainer</code>, <code>Param</code> objects can
 * be added and removed.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.params.ParamsProcessor</code>.
 */
public interface DynSizeParamContainer extends AbstractParamContainer {

  /**
   * Indicates whether the specified <code>Param</code> can be added to this
   * <code>DynSizeParamContainer</code>. The specified <code>Param</code> can
   * be added if this <code>DynSizeParamContainer</code> contains no 
   * <code>Param</code> with identical name. When container is used to store
   * substitutional variables, this method returns <code>false</code> in case
   * where one tries to add dynamic substitutional variable - this is only
   * done internally by the SDM itself.
   * 
   * @param param the <code>Param</code> to be added
   * @return <code>true</code> if <code>param</code> can be added;
   *          <code>false</code> otherwise
   * @throws NullPointerException if <code>param</code> is <code>null</code>
   */
  public boolean canAddParam(Param param) throws RemoteException;
  
  /** 
   * Adds a <code>Param</code> to this <code>DynSizeParamContainer</code>.
   * 
   * @param param the <code>Param</code> to be added
   * @throws IllegalArgumentException if <code>param</code> cannot be added
   * @throws NullPointerException if <code>param</code> is <code>null</code>
   */
  public void addParam(Param param) throws RemoteException;
  
  /**
   * Indicates whether the specified <code>Param</code> can be removed from 
   * this <code>DynSizeParamContainer</code>. The specified <code>Param</code>
   * can be removed from this <code>DynSizeParamContainer</code> if it is
   * contained in this <code>DynSizeParamContainer</code>.
   * 
   * @param param the <code>Param</code> to be removed
   * @return <code>true</code> if <code>param</code> can be removed;
   *          <code>false</code> otherwise
   * @throws NullPointerException if <code>param</code> is <code>null</code>
   */  
  public boolean canRemoveParam(Param param) throws RemoteException;
  
  /** 
   * Removes the specified <code>Param</code> from this 
   * <code>DynSizeParamContainer</code>.
   * 
   * @param param the <code>Param</code> to be removed
   * @throws IllegalArgumentException if <code>param</code> cannot be removed
   * @throws NullPointerException if <code>param</code> is <code>null</code>
   */
  public void removeParam(Param param) throws RemoteException;
    
  /**
   * Indicates whether a <code>Param</code> with the specified name can be 
   * removed from this <code>DynSizeParamContainer</code>. A <code>Param</code>
   * with the specified name can be removed from this 
   * <code>DynSizeParamContainer</code> if this 
   * <code>DynSizeParamContainer</code> contains a <code>Param</code> with the
   * specified name.
   * 
   * @param name the name of the <code>Param</code> to be removed
   * @return <code>true</code> if a <code>Param</code> with the specified
   *          name can be removed;
   *          <code>false</code> otherwise
   * @throws NullPointerException if <code>name</code> is <code>null</code>
   */  
  public boolean canRemoveParamByName(String name) throws RemoteException;
  
  /** 
   * Removes a <code>Param</code> with the specified name from this 
   * <code>DynSizeParamContainer</code>.
   * 
   * @param name the name of the <code>Param</code> to be removed
   * @throws IllegalArgumentException if no <code>Param</code> with the 
   *          specified name can be removed
   * @throws NullPointerException if <code>name</code> is <code>null</code>
   */ 
  public void removeParamByName(String name) throws RemoteException;
    
}

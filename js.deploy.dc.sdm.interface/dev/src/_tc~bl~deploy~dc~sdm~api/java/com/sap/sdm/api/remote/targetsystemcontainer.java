package com.sap.sdm.api.remote;

import java.io.IOException;

/**
 * A container for target systems. Within a <code>TargetSystemContainer</code>,
 * both the ID as well as the <code>ServerType</code> are key attributes for
 * the contained <code>TargetSystem</code> objects.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public interface TargetSystemContainer {

  /**
   * Returns all target systems in this container.
   * 
   * @return an array of <code>TargetSystem</code>
   */
  public TargetSystem[] getTargetSystems() throws RemoteException; 
  
  /**
   * Returns the <code>TargetSystem</code> with the specified ID.
   * 
   * @param id the ID of the <code>TargetSystem</code>
   * @return a <code>TargetSystem</code> with the specified ID, if contained
   *          by this <code>TargetSystemContainer</code>; <code>null</code>
   *          otherwise
   * @throws NullPointerException if <code>id</code> is <code>null</code>
   */
  public TargetSystem getTargetSystemByID(String id)
    throws RemoteException;

  /**
   * Returns the <code>TargetSystem</code> with the specified 
   * <code>ServerType</code>.
   * 
   * @param serverType the <code>ServerType</code>
   * @return a <code>TargetSystem</code> with the specified 
   *          <code>ServerType</code>, if contained in this 
   *          <code>TargetSystemContainer</code>; <code>null</code> otherwise
   * @throws NullPointerException if <code>serverType</code> is 
   *          <code>null</code>
   */
  public TargetSystem getTargetSystemByServerType(ServerType serverType)
    throws RemoteException;
    
  /**
   * Indicates whether the specified <code>TargetSystem</code> can be added to
   * this <code>TargetSystemContainer</code>. A <code>TargetSystem</code> can
   * be added to this <code>TargetSystemContainer</code>, if this 
   * <code>TargetSystemContainer</code> contains neither a 
   * <code>TargetSystem</code> with the same ID nor a <code>TargetSystem</code>
   * with the same <code>ServerType</code> as the specified 
   * <code>TargetSystem</code>.
   * 
   * @param targetSystem the specified <code>TargetSystem</code>
   * @return <code>true</code> if <code>targetSystem</code> can be added;
   *          <code>false</code> otherwise
   * @throws NullPointerException if <code>targetSystem</code> is 
   *          <code>null</code>
   */
  public boolean canAddTargetSystem(TargetSystem targetSystem)
    throws RemoteException;
    
  /**
   * Adds a <code>TargetSystem</code> to this 
   * <code>TargetSystemContainer</code>. As a precondition, no
   * <code>TargetSystem</code> with identical ID or identical 
   * <code>ServerType</code> must be contained in this 
   * <code>TargetSystemContainer</code>.
   * After adding the repository is saved to disk.
   * 
   * @param targetSystem the <code>TargetSystem</code> to be added
   * @throws IllegalArgumentException if this <code>TargetSystemContainer</code>
   *          contains a <code>TargetSystem</code> with the same ID or with the
   *          same <code>ServerType</code>
   * @throws NullPointerException if <code>targetSystem</code> is 
   *          <code>null</code>
   * @throws IOException if saving of the repository fails after the
   *         targetsystem was added
   * @see #canAddTargetSystem(TargetSystem)
   */
  public void addTargetSystem(TargetSystem targetSystem)
    throws RemoteException, IOException;
        
}

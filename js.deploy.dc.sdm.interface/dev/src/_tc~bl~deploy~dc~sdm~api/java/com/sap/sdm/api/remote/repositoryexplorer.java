/*
 * Created on 2005-2-8
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.api.remote;

import com.sap.sdm.api.remote.model.*;

/**
 * Provides methods for exploring the SDM repository. The SDM repository is the
 * place where all the deployed on the system components are registered.</br>
 * There are two types of components - <code>Sca</code>s(Software Component Archives) and
 * <code>Sda</code>s(Software Deployment Archives). Both are <code>Sdu</code>s(Software
 * Deployment Units). <code>Sca</code>s are an aggregation of finite number of
 * <code>Sda</code>s.
 * <p> 
 * <i>
 * Notice that all the objects and methods of Repository Explorer gives a
 * snapshot of the deployed by SDM components in exact time point. This is
 * considered not a major problem since in given time frame only one user
 * can be connected to SDM Server, and in this case the user is the SDM API client.
 * </i>
 * 
 * @author lalo-i
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.explorer.RepositoryExplorer</code>.
 */
public interface RepositoryExplorer {
  
  /**
   * 
   * @return array of all the currently deployed <code>Sdu</code>s found in the
   *          SDM repository
   */
  public Sdu[] findAll() throws RemoteException;
  
  /**
   * 
   * @param name the <code>Sca</code>'s name
   * @param vendor the <code>Sca</code>'s vendor
   * @return <code>Sca</code> object or null if:
   *         <ul>
   *          <li>there is no such <code>Sca</code> with these <code>name</code> and <code>vendor</code></li>
   *          <li>the component with these <code>name</code> and <code>vendor</code> is not a
   *              <code>Sca</code>, but <code>Sda</code></li>
   *         </ul>
   * @throws NullPointerException when the <code>name</code> or <code>vendor</code> are null
   * @throws IllegalArgumentException when the <code>Sdu</code> specified by this <code>name</code>
   *                                  and <code>vendor</code> is a <code>Sda</code>
   */
  public Sca findSca(String name, String vendor) throws RemoteException;

  /**
   * 
   * @param name the <code>Sda</code>'s name
   * @param vendor the <code>Sda</code>'s vendor
   * @return <code>Sda</code> object or null if:
   *         <ul>
   *          <li>there is no such <code>Sda</code> with these <code>name</code> and <code>vendor</code></li>
   *          <li>the component with these <code>name</code> and <code>vendor</code> is not a
   *              <code>Sda</code>, but <code>Sca</code></li>
   *         </ul>
   * @throws NullPointerException when the <code>name</code> or <code>vendor</code> are null
   * @throws IllegalArgumentException when the <code>Sdu</code> specified by this <code>name</code>
   *                                  and <code>vendor</code> is a <code>Sca</code>
   */
  public Sda findSda(String name, String vendor) throws RemoteException;
    
  /**
   * Method returns an array of all the <code>Sca</code>s that are known by SDM
   * as undeployed at the point of time this method is called.
   * If in SDM repository there is an information about <code>Sca</code> <b><i>A</i></b>
   * with different versions already undeployed, then all these versions of <b><i>A</i></b>
   * as different <code>Sca</code>s are returned.
   * 
   * @return array of all the <code>Sca</code>s that are currently marked as undeployed or null
   *         if there are no such <code>Sca</code>s
   */
  public Sca[] findAllUndeployedScas() throws RemoteException;
    
}

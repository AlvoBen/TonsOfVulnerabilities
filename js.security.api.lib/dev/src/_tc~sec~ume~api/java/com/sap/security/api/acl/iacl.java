package com.sap.security.api.acl;

import java.util.*;
import com.sap.security.api.*;

/**
 * <h2>ACL Context Interface</h2>
 *
 * <p>Definition of an Access Control List object
 * <p>This interface defines an Access Control List object (ACL object). It can contain
 * zero or more ACE's which specify the principals and the permissions.
 *
 * <p><b>NOTE</b>: Change operations on an ACL object do not implicitly trigger cluster-wide 
 *       cache invalidations. Therefore {@link #commit()} has to be called explicitly after 
 *       all updates are completed, to get the changes reflected in all runtime objects which
 *       might be cached in other cluster nodes.
 *
 * @version 1.0
 */

public interface IAcl {

  /**
   * This method adds a new ACL owner to current ACL object.
   * @param   caller      a current ACL owner.
   * @param   principal   new ACL owner (principal, for example user).
   * @return  true        when the new ACL owner was set successfully
   *          false       otherwise
   * @exception UMException if the data cannot be added.
   */
  public boolean addOwner(IPrincipal caller, IPrincipal principal) throws UMException;

  /**
   * This method removes an ACL owner from current ACL object.
   * @param   caller      an ACL owner.
   * @param   principal   another ACL owner (principal, for example user)
   * @return  true        when the ACL owner was removed successfully
   *          false       otherwise
   * @exception UMException if the data cannot be removed.
   */
  public boolean removeOwner(IPrincipal caller, IPrincipal principal) throws UMException;

  /**
   * This method checks, if an user (principal) is an ACL owner.
   * @param   principal   the checked user (principal).
   * @return  true        when the user is an ACL owner
   *          false       otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean isOwner(IPrincipal principal) throws UMException;

  /**
   * This method returns a list of ACL owners.
   * @return  the owners of the ACL (List of IPrincipals).
   * @exception UMException if the data cannot be read.
   */
  public List getOwners() throws UMException;

  /**
   * This method creates a new ACE object to current ACL. If an IAclEntry is inherited,
   * it represents the parent ACE's of the object.
   * @param   caller      an ACL owner.
   * @param   principal   principal for ACE
   * @param   permission  permission for the ACE
   * qparam   isInherited if the ACE is inherited
   * @return  IAclEntry   the ACE object
   *          null        if it is not possible to create an ACE
   * @exception UMException if the data cannot be created.
   */
  public IAclEntry createAclEntry(IPrincipal caller, IPrincipal principal, String permission, boolean isInherited) throws UMException;

  /**
   * This method removes an existing ACE object from the current ACL object.
   * @param   caller      an ACL owner.
   * @param   aclEntry    an ACE object.
   * @return  true        when the new ACE object was removed successfully
   *          false       otherwise
   * @exception UMException if the data cannot be removed.
   */
  public boolean removeAclEntry(IPrincipal caller, IAclEntry aclEntry) throws UMException;

  /**
   * This method removes all existing ACE objects from the current ACL object
   * except the ACE's with the owner permission, but does not delete the ACL.
   * @param   caller      an ACL owner.
   * @exception UMException if the data cannot be reseted.
   */
   public void resetAcl(IPrincipal caller) throws UMException;

  /**
   * This method returns a List of ACE objects which are assigned to the
   * current ACL object.
   * @return  a List of ACE objects
   * @exception UMException if the data cannot be read.
   */
  public List getAclEntries() throws UMException;

  /**
   * This method returns a List of ACE objects which are assigned to the
   * current ACL object concerning a specific user (principal).
   * @param   principal   user (principal).
   * @return  a List of ACE objects concerning a specific user (principal).
   * @exception UMException if the data cannot be read.
   */
  public List getAclEntries(IPrincipal principal) throws UMException;

  /**
   * This method checks if an user (principal) is authorised for a specific permission.
   * @param   principal   user (principal).
   * @param   permission  checked permission.
   * @return  true        if the principal is authorised for the specified permission
   *          false       otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean isAllowed(IPrincipal principal, String permission) throws UMException;

  /**
   * This method checks if an user (principal) is authorized for a specific permission.
   * but doesn't write an entry in the security audit log.
   * @param   principal   user or group
   * @param   permission  checked permission
   * @return  true        if the principal is authorized for the specified permission
   *          false       otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean hasPermission(IPrincipal principal, String permission) throws UMException;

  /**
   * This methode returns the ID of the object which is assigned to current ACL
   * object.
   * @return  an object ID.
   * @exception UMException if the data cannot be read.
   */
  public String getObjectId() throws UMException;

 /**
  * This method changes the object ID for the current ACL.
   * @return  true        if the object ID was changed successfully
   *          false       otherwise
   * @exception UMException if the data cannot be changed.
  */
  public boolean changeObjectID(IPrincipal caller, String objectID) throws UMException;

  /**
   * Prepares this ACL for update.
   * @return the concerning ACL Object.
   * @exception UMException if the data cannot be prepared.
   */
  public IAcl prepare() throws UMException;

  /**
   * Commits any changes made to this ACL (i.e. add/remove AclEntry/Owner).
   * If successful, a cluster-wide cache invalidation of the ACL is performed.
   * @exception UMException if the data cannot be commited.
   */
  public void commit() throws UMException;

}

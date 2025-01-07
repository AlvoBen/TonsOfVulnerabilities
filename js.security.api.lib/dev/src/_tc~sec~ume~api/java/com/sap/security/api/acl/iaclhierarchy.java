package com.sap.security.api.acl;

import com.sap.security.api.*;

/**
 * <h2>ACL Hierarchy Interface</h2>
 *
 * <p>Definition of an Access Control List Hierarchy
 * <p>This interface defines an Access Control List Hierarchy. With this, it
 * is possible to check if a principal has a specific permission for an object
 * or his parents.
 *
 * @version 1.0
 */


public interface IAclHierarchy {

  /**
   * This method checks, if a principal can perform a permission on the specified object ID array.
   * @param   objectIds  This object ID array represent the parent object IDs (the way from the specified object ID
   *                      to the parent nodes up to the roof).
   * @param   principal   the principal which has to be checked.
   * @param   permission  the permission which has to be checked.
   * @return  true        if the principal is authorised for the specified permission
   *          false       otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean isAllowed(String[] objectIds, IPrincipal principal, String permission) throws UMException;

  /**
   * This method propagades the ACEs from the root of the subtree to his nodes and leaves. These ACEs are inherited.
   * @param   caller            a principal who has to be the owner of the root ACL
   * @param   rootObjectID      a String which represents the root of the subtree. His ACEs will be propagated.
   * @param   childrenObjectIds a String array which represents all child object IDs under the root of the subtree.
   * @exception UMException if the data cannot be set.
   */
  public void propagade(IPrincipal caller, String rootObjectID, String[] childrenObjectIds) throws UMException;

  /**
   * This method checks if a principal can perform a permission on the specified object ID array.
   * @param   objectIds  This object ID array represent the parent object IDs (the way from the specified object ID
   *                      to the parent nodes up to the roof)
   * @param   principal   the principal which has to be checked
   * @param   permission  the permission which has to be checked
   * @exception UMException if the principal has not the specified permission
   */
  public void checkPermission(String[] objectIds, IPrincipal principal, String permission) throws UMException;

}

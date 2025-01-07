package com.sap.security.api.acl;

import com.sap.security.api.*;

/**
 * <h2>ACL Context Entry Interface</h2>
 *
 * <p>Definition of an Access Control List Entry
 * <p>Access-Control Entry (ACE) is an element in the Access-Control List (ACL).
 * An ACL can have zero or more ACEs. Each ACE controls or monitor access to an
 * object specified trustee.
 * <p>An entry specifies a principle (i.e. user) and a permission that is granted
 * to the principle.
 *
 * @version 1.0
 */


public interface IAclEntry {

  /**
   * This methods returns the principal object from current ACE object.
   * @return  IPrincipal  principal object.
   */
  public IPrincipal getPrincipal();

  /**
   * This method returns the permission object from current ACE object.
   * @return  String permission
   */
  public String getPermission();

  /**
   * This methods checks if the permission is covered by the ACE's permission.
   * @param   permission  the checked permission object.
   * @return  true        when the permission is covered
   *          false       otherwise
   */
  public boolean isAllowed(String permission);

  /**
   * This method checks if the ACE is inherited. If a ACE is inherited,
   * then this ACE represents the ACE's of the parent object.
   * @return  true    when the ACE is inherited
   *          false   otherwise
   */
  public boolean isInherited();

}

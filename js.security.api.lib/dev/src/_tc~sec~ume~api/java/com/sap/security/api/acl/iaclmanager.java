package com.sap.security.api.acl;

import java.util.*;
import com.sap.security.api.*;

/**
 * <h2>ACL Manager Interface</h2>
 *
 * <p>Definition of an Access Control List Manager
 * <p>The ACL Manager administers the Access Control Lists (ACL).
 * <p>This interface defines methods which are necessary to administer ACL's
 * and check if a principal has access to an object with a certain permission.
 * <p>Permissions:
 * <p>- An permission exists of an object type and an permission name separated by
 * by a point '.' (i.e. "default_type.read" ).
 * <p>Note: A point is not allowed in the object type, but in the permission name!
 * <p>- Optional: You can use global permissions (permissions without object type)
 * <p>- Permissions must be unique within the namespace of the ACL Manager you are
 * using. This means if you use an application specific ACL Manager, the permissions
 * have to be unique within your application. If you use the default ACL Manager,
 * the permission have to be globally unique.
 * <p>Object Id's:
 * <p>-They also have to be unique within the namespace of the ACL Manager you are
 * using (see above). Therefore they should have a prefix with the service name
 * and/or the object type.
 *<p>
 * The following code exsample shows some typical functions:
 * <p>
 * <pre>
 * // Get default ACL Manager
 * IAclMAnager manager = UMFactory.getAclManager();
 * // Get specific ACL Manager
 * IAclMAnager manager = UMFactory.getAclManager("Workflow");
 * 
 * //Create some Permissions
 * manager.addPermission("WorkflowPermission.read", null);
 * manager.addPermission("WorkflowPermission.write", null);
 * //Create a Permission Container
 * List members = new ArrayList(2);
 * members.add("WorkflowPermission.read");
 * members.add("WorkflowPermission.write");
 * manager.addPermission("WorkflowPermission.full_control", members);
 * 
 * //Create an ACL on an objectID
 * IUser userA;
 * IAcl acl = manager.createAcl(userA, "WorkflowItemABC");
 * //Get this ACL again
 * IAcl acls = manager.getAcls("WorkflowItemABC");
 * //Delete an ACL
 * manager.removeAcl(userA, "WorkflowItemABC");
 * //Delete all info's abaout a principal (concerning ACL info)
 * manager.deletePrincipal(usersA);
 * 
 * //Create an ACE (Access Control Entry) for user B (user A is ACL Owner)
 * IAclEntry aclEntry = acl.createAclEntry(userA, userB, "WorkflowPermission.read", false);
 * //Get all ACE's for a special principal
 * acl.getAclEntries(userB);
 * //Get all ACE's		    
 * acl.getAclEntries();
 * 
 * //check a permission on IAclManager
 * manager.isAllowed("WorkflowItemABC", usersA, "WorkflowPermission.read");
 * //check a permission on IAcl
 * acl.isAllowed(usersA, "WorkflowPermission.read");
 * //check a permission on IAclEntry
 * acl.isAllowed("WorkflowPermission.read");
 * 
 * //Delete an ACL Entry
 * acl.removeAclEntry(usersA, aclEntries);
 * //Reset the hole ACL (only deletion of ACE's)
 * acl.resetAcl(usersA);
 * </pre>
 * 
 * <p><b>NOTE</b>: {@link #deletePrincipal(java.lang.String)} and {@link #updatePrincipal(java.lang.String,int)} 
 *       are the only methods which implicitly trigger cluster-wide cache invalidation. Therefore all changes done
 *       via other methods of IAclManager need to be followed by {@link IAcl#commit()} on the affected IAcl objects
 *       to get the changes also reflected on other cluster nodes.
 *  
 * @version 1.0
 */


public interface IAclManager extends IConfigurable {

  /**
   * Max. length of object ID (incl. an optional service name / object type)
   */
   public final static int MAX_OBJECT_ID_LENGTH = 255;

  /**
   * Max. length of permission name
   */
   public final static int MAX_PERMISSION_NAME_LENGTH = 127;

  /**
   * Permission that an owner of an ACL gets automatically
   */
   public final static String OWNER_PERMISSION = "owner";

  /**
   * This method returns the maximum length of the object id.
   * @return  the maximum length of object id.
   */
  public int getMaxObjectIdLength();

  /**
   * This method returns the maximum length of the permission name.
   * @return  the maximum length of permission name.
   */
  public int getMaxPermissionNameLength();

  /**
   * This method creates a new ACL object for an object id.
   * @param   caller    an IPrincial which will be registered as owner.
   * @param   objectId  a string which defines the object.
   * @return  the new ACL object.
   * @exception UMException if the ACL cannot be created.
   */
  public IAcl createAcl(IPrincipal caller, String objectId) throws UMException;

  /**
   * This method reads the existing ACL object for a portal object.
   * @param   objectId  a string which defines the object.
   * @return  the ACL object or null if no object exists.
   * @exception UMException if the data cannot be read.
   */
  public IAcl getAcl(String objectId) throws UMException;

  /**
   * This method reads the existing ACL object for an array of object Ids.
   * @param   objectIds  a string array which defines the objects.
   * @return  an array of the ACL objects or null if no object exists.
   * @exception UMException if the data cannot be read.
   */
  public IAcl[] getAcls(String[] objectIds) throws UMException;

  /**
   * This method removes the existing ACL object for a given object Id.
   * @param   caller    a IPrincipal who has to be an owner of the acl.
   * @param   objectId  a string which defines the corresponding acl to remove.
   * @return  true      when the ACL object extsts and if it has been removed successfully;
   *          false     otherwise
   * @exception UMException if the data cannot be removed.
   */
  public boolean removeAcl(IPrincipal caller, String objectId) throws UMException;

  /**
   * This method removes the existing ACL object for a given acl.
   * @param   caller    a IPrincipal who has to be an owner of the acl.
   * @param   acl     a acl which has to be removed.
   * @return  true    when the ACL object was removed successfully;
   *          false   otherwise.
   * @exception UMException if the data cannot be removed.
   */
  public boolean removeAcl(IPrincipal caller, IAcl acl) throws UMException;

  /**
   * This method removes the existing ACL object for a number of given object Ids.
   * @param   caller    a IPrincipal who has to be an owner of the acls.
   * @param   objectIds  a string array which defines corresponding acls to remove.
   * @return  true      when the ACL objects were removed successfully;
   *          false     otherwise
   * @exception UMException if the data cannot be removed.
   */
  public boolean removeAcls(IPrincipal caller, String[] objectIds) throws UMException;

  /**
   * This method checks if a principal is authorized for a permission on an object.
   * @param   objectId    a string which defines the object.
   * @param   principal   user, group or role name.
   * @param   permission  checked action (permission).
   * @return  true      when the principal is authorized for the object and permission;
   *          false     otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean isAllowed(String objectId, IPrincipal principal, String permission) throws UMException;

  /**
   * This method checks if a principal is authorized for a number of permissions on an object.
   * @param   objectIds   a string array which defines the objects.
   * @param   principal   user, group or role name.
   * @param   permission  checked action (permission).
   * @return  true      when the principal is authorized for the object and permissions;
   *          false     otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean isAllowed(String[] objectIds, IPrincipal principal, String permission) throws UMException;

  /**
   * This method checks if a principal is authorized for a permission on an object,
   * but doesn't write an entry in the security audit log.
   * @param   objectId    a string which defines the object.
   * @param   principal   user, group or role
   * @param   permission  checked action (permission).
   * @return  true      when the principal is authorized for the object and permission;
   *          false     otherwise
   * @exception UMException if the data cannot be read.
   */
  public boolean hasPermission(String objectId, IPrincipal principal, String permission) throws UMException;

  /**
   * Adds a permission to the list of available permissions.
   * @param   permission  the permission to add (see class description)
   * @param   members     a list of permissions the added permission contains
   *                      or null if it contains no other permissions
   * @return  true        when the permission could be added successfully;
   *          false       otherwise
   * @exception UMException
   */
  public boolean addPermission(String permission, List members) throws UMException;

  /**
   * Adds a member (permission) to an existing permission.
   * @param   permission  the permission for which the member should be added
   * @param   member      a permission that should be a member of the given
   *                      permission
   * @return  true        when the member could be added successfully;
   *          false       otherwise
   * @exception UMException
   */
  public boolean addPermissionMember(String permission, String member) throws UMException;

  /**
   * Removes a permission from the list of available permissions.
   * @param   permission  the permission to remove (see class description)
   * @return  true        when the permission could be removed successfully;
   *          false       otherwise
   * @exception UMException
   */
  public boolean removePermission(String permission) throws UMException;

  /**
   * Removes a permission member from the given permission.
   * @param   permission  the parent permission
   * @param   member	  the permission to remove
   * @return  true        when the permission could be removed successfully;
   *          false       otherwise (when member was not a member of the given permission)
   * @exception UMException
   */
  public boolean removePermissionMember(String permission, String member) throws UMException;

  /**
   * Returns the permission members from the given permission.
   * @param   permission  the parent permission
   * @return  List  	  the member permissions
   * @exception UMException
   */
  public List getPermissionMembers(String permission) throws UMException;


  /**
   * Gets a list of permissions which are available for the specific object type
   * @param   objectType  the object type
   *                      <p> If it's an empty string, all global permissions
   *                      are delivered.
   * @return  the list of permissions
   * @exception UMException
   */
  public List getPermissions(String objectType) throws UMException;

  /**
   * Gets a list of all available permissions
   * @return  the permissions
   * @exception UMException
   */
  public List getAllPermissions() throws UMException;

  /**
   * This method returns the PermissionStatus for an action, a portal object and a user (principal).
   * @param   objectId    a string which define the object.
   * @param   principal   user, group or role name.
   * @param   permission  checked action (permission).
   * @return  an object that represents the status, whether the action is allowed,
   *          denied or undefined
   * @exception UMException if the data cannot be read.
   */
  public PermissionStatus getPermissionStatus(String objectId, IPrincipal principal, String permission) throws UMException;

  /**
   * This method deletes all data (owner, ACE) concerning a principal
   * @param   principal   principal, whose related data should be deleted.
   * @exception UMException if the data cannot be removed.
   * @deprecated        please use deletePrincipal(String principalID)
   */
  public void deletePrincipal(IPrincipal principal) throws UMException;

  /**
   * This method deletes all data (owner, ACE) concerning a principal
   * @param   principalID   uniqueID of principal, whose related data should be deleted.
   * @exception UMException if the data cannot be removed.
   */
  public void deletePrincipal(String principalID) throws UMException;
  
  /**
   * This method returns a List of all available ACL ids (String objects)
   * @return	List of ACL ids (String objects)
   * @exception UMException if the data cannot be read.
   */
  public List getAllAcls() throws UMException;
  
  /**
   * This method returns a List of ACL object ids (String objects), which fit the search criteria
   * @param   principalID	String of object id, for which a search should be
   * 					performed. The wildcard character "*" is permitted.
   * 					If a wildcard is set at begin or end of the object id,
   * 					a like search will be performed. If no wildcard is set,
   * 					a search for this exact id will be performed only. 
   * @return	List of ACL ids (String objects)
   * @exception UMException if the data cannot be read.
   */
  public List searchAcls(String principalID) throws UMException;
  
  /**
   * This method logs the passed information directly into the security audit log file. 
   * @param objectID	a string which defines the object.
   * @param objectName	complete readable object name 
   * @param comment		should contain information about the cause of (a group of) 
   * 					following ACL modifications
   */
  public void logAclInfo(String objectID, String objectName, String comment);
}

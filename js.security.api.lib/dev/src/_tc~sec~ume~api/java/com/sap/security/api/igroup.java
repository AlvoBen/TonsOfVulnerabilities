package com.sap.security.api;

/**
 * This interface provides read and write access to principals of type group.
 * <p>If you want to keep and persist an identifier as a  
 * reference to an instance of <code>IGroup</code> you must use 
 * method {@link IPrincipal#getUniqueID()}.
 * As this identifier contains internal information
 * and is usually not readable, it should not be used in end user interfaces.
 * <p>Use methods {@link IPrincipal#getDisplayName()} or 
 * {@link IGroup#getUniqueName()}
 * in order to display
 * attributes with a nice name for user interfaces.  
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @author  Alexander Primbs
 * @version $Revision: #1 $ <BR>
 *
 */

public interface IGroup extends com.sap.security.api.IPrincipalSet
{
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IGroup.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
    /**
     * Returns principals of type user belonging to this group.
     * @param  getChildMembers if set to <code>true</code>,
     * this method does a recursive search, that is children, grandchildren ...
     * of this group are checked and all users of this group and its subgroups
     * are returned. If this parameter is set to <code>false</code> only user members
     * of this group are returned.
     * @return Iterator of user members. The iterator contains uniqueIdOfUser 
     * strings
     */
    public java.util.Iterator getUserMembers(boolean getChildMembers);

    /**
     * Returns principals of type group belonging to this group.
     * @param  getChildMembers if set to <code>true</code>,
     * this method does a recursive search, that is children, grandchildren ...
     * of this group are checked and all groups which are member
     * of this group and its subgroups
     * are returned. If this parameter is set to <code>false</code> only group members
     * of this group are returned.
     * @return Iterator of group members, The iterator contains uniqueIdOfGroup 
     * strings.
     */
    public java.util.Iterator getGroupMembers(boolean getChildMembers);

    /***
     * Returns <code>true</code> if the passed principal of type user 
     * is a member of this group.
     *
     * @param uniqueIdOfUser - the uniqueIdOfUser which should be checked. 
     * uniqueIdOfUser must be a uniqueID which identifies a user object.
     * @param checkParents - recursive search is done, that is parents, grandparents, ...
     * of this group are checked if this user is a member of this or its parent groups
     * @return true if the principal is a member of this group, false otherwise.
     */
    public boolean isUserMember(String uniqueIdOfUser, boolean checkParents);

    /***
     * Returns <code>true</code> if the passed principal of type groups
     * is a member of this group.
     *
     * @param uniqueIdOfGroup - the uniqueIdOfGroup which should be checked. 
     * uniqueIdOfGroup must be a uniqueID which identifies a group object.
     * @param checkParents - recursive search is done, that is parents, grandparents, ...
     * of this group are checked if this group is a member of this or its parent groups
     * @return true if the principal is a member of this group, false otherwise.
     */
    public boolean isGroupMember(String uniqueIdOfGroup, boolean checkParents);

    /**
     * Adds the specified user member to the collection.
     *
     * @param uniqueIdOfUser - the uniqueIdOfUser to add to this collection.
     * @return :<code>true</code> if the member was successfully added
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate. 
     * @throws UMException if an error occurs     
     */
     public boolean addUserMember(String uniqueIdOfUser) throws UMException;

    /**
     * Adds the specified group member to this group.
     *
     * @param uniqueIdOfGroup - the uniqueIdOfGroup to add to this group.
     * @return :<code>true</code> if the member was successfully added
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.    
     * @throws UMException if an error occurs
     */
     public boolean addGroupMember(String uniqueIdOfGroup) throws UMException;

    /**
     * Remove the specified user member from the group.
     *
     * @param uniqueIdOfUser - the uniqueIdOfUser to remove from this group
     * @return :<code>true</code> if the member was successfully removed
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.      
     * @throws UMException if an error occurs
     */
     public boolean removeUserMember(String uniqueIdOfUser) throws UMException;

    /**
     * Remove the specified group member from the collection.
     *
     * @param uniqueIdOfGroup - the uniqueIdOfGroup to remove from this collection.
     * @return :<code>true</code> if the member was successfully removed.
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.      
     * @throws UMException if an error occurs     
     */
     public boolean removeGroupMember(String uniqueIdOfGroup) throws UMException;

    /**
     * Gets the list of all assigned roles of this principal including parent groups,
     *  grandparent groups,...
     * @param recursive if <code>true</code> returns all directly assigned roles
     * and also the roles 
     * which are assigned to parent groups (indirectly assigned roles). 
     * @return an iterator of all roles for this principal. The iterator contains
     * uniqueIdOfRole strings.
     */
     public java.util.Iterator getRoles(boolean recursive);

    /**
     * Gets the list of all parent groups including parents, grandparents, ...
     * @param recursive if <code>true</code> returns all parent groups of this group.
     * If this parameter is set to <code>false</code> only the groups are returned
     * which have a member of this group.
     * @return iterator of all parent principals of this collection.
     * The iterator contains uniqueIdOfGroup strings.     
     */
    public java.util.Iterator getParentGroups(boolean recursive);

   /**
     * Checks if the principal belongs to the passed role identified by uniqueIdOfRole.
     * @param uniqueIdOfRole of role which should be checked
     * @param recursive - a recursive search is done if this parameter 
     * is set to <code>true</code>. If this group is member of a 
     * group which is assigned to role identified by uniqueIdOfRole, 
     * <code>true</code> is returned. If this parameter is set to 
     * <code>false</code> it is only checked if this group is directly
     * assigned to the role.
     *     
     * @return <code>true</code> if this group is directly or indirectly 
     * (via group membership) assigned to role identified by uniqueIdOfRole.
     * <code>false</code> if this group is not assigned to this role
     */
    public boolean isMemberOfRole(String uniqueIdOfRole, boolean recursive);

    /**
     * Checks if the principal belongs to the passed parentGroup identified 
     * by uniqueIdOfGroup.
     *
     * @param uniqueIdOfGroup the ID of the collection
     * @param recursive - a recursive search is done if this parameter 
     * is set to <code>true</code>. If this group is member of a
     * group which is a member of the group identified by uniqueIdOfGroup, 
     * <code>true</code> is returned. If this parameter is set to 
     * <code>false</code> it is only checked if this group is a direct
     * member of this group.
     * 
     * returns <code>true</code> if this group is a member of the group 
     * identified by uniqueIdOfGroup.
     * 
     */
    public boolean isMemberOfGroup(String uniqueIdOfGroup, boolean recursive);

    /**
     * Assign this principal to the parent-group identified by uniqueIdOfGroup
     *
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.           
     *
     * @param	uniqueIdOfGroup       uniqueIdOfGroup of the parent group
     * @exception	UMException
     */
    public void addToGroup(String uniqueIdOfGroup) throws UMException;

    /**
     * Unassign this group from the parent-group identified by uniqueIdOfGroup.
     *
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.           
     *
     * @param uniqueIdOfGroup of the parent group
     * @exception	UMException
     */
    public void removeFromGroup(String uniqueIdOfGroup) throws UMException;

    /**
     * Assign this principal to the role identified by uniqueIdOfRole.
     *
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.           
     *     
     * @param uniqueIdOfRole       id of the role
     * @exception	UMException
     */
    public void addToRole(String uniqueIdOfRole) throws UMException;

    /**
     * Unassign this principal from role identified by uniqueIdOfRole
     *
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.           
     *     
     * @param uniqueIdOfRole of the role
     * @exception	UMException
     */
    public void removeFromRole(String uniqueIdOfRole) throws UMException;

    /**
     * Returns the description of this principal.
     *
     * @return String: the description of this principal
     *         <code>null</code>  : if no description exists
     */
    public String getDescription();

    /**
     * Sets the description of this principal.
     *
     * These changes will only take effect if you commit these 
     * changes to the group store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.           
     *     
     * @exception UMException if the description could not be set
     *
     */
    public boolean setDescription(String description) throws UMException;

    /**
     * Get uniqueName of this <code>IGroup</code> object.
     * A (usually) unique readable name of an instance of <code>IGroup</code>.
     * <p><b>Note</b>: Depending on the persistence layer
     * it is not guaranteed that this name
     * is unique. The uniqueName may change over time. Thus, do not persist the 
     * uniqueName. Always use the unique identifier (UniqueID) of 
     * {@link IPrincipal#getUniqueID()}
     * for this purpose.
     * <p>Use the uniqueName for searching and displaying in user interfaces.
     * @return uniqueName of object
     */
    public String getUniqueName();

}

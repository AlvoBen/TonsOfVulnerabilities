package com.sap.security.api;

/**
 * This interface provides read and write access to attributes and properties of instances of
 * type IRole. IRole extends IPrincipalSet and defines additional role specific characteristics.
 * <p>If you want to keep and persist an identifier as a  
 * reference to an instance of <code>IRole</code> you must use 
 * method {@link IPrincipal#getUniqueID()}.
 * As this identifier contains internal information
 * and is usually not readable, it should not be used in end user interfaces.
 * <p>Use methods {@link IPrincipal#getDisplayName()} or 
 * {@link IRole#getUniqueName()}
 * in order to display
 * attributes with a nice name for user interfaces.  
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @version 1.0
 */

public interface IRole extends com.sap.security.api.IPrincipalSet
{
  public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IRole.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";


    /**
     * Returns IDs of user principals belonging to this role
     * @param getChildMembers This method does a recursive search if the parameter getChildMembers
     * is set to <code>true</code>.
     * @return iterator of Strings of uniqueIds of this role
     * @exception NoSuchPCDRoleException if the role does not exist
     */
    public java.util.Iterator getUserMembers(boolean getChildMembers);

    /**
     * Returns IDs of group principals belonging to this role
     * @param getChildMembers This method does a recursive search if parameter getChildMembers
     * is set to <code>true</code>.
     * @return iterator of Strings of uniqueIds of this role
     * @exception NoSuchPCDRoleException if the role does not exist
     */
    public java.util.Iterator getGroupMembers(boolean getChildMembers);

    /***
     * Returns <code>true</code> if the passed principal of type user is assigned to this role.
     * @param member - uniqueId of the principal whose membership is to be checked.
	 * @param checkChildren this method does a recursive search if this parameter is set to
	 * <code>true</code>
     * @return true if the principal is a member of this collection, false otherwise.
     */
    public boolean isUserMember(String member, boolean checkChildren);

    /***
     * Returns <code>true</code> if the passed principal is a member of this role.
     *
     * @param member - uniqueId of the principal whose membership is to be checked.
	 * @param checkChildren this method does a recursive search if this parameter is set to 
	 * <code>true</code>
     * If a group is member of a group
     * which is assigned to this role, <code>true</code> is returned.
     * @return <code>true</code> if the group is assigned to this role,
     * <code>false</code> otherwise.
     */
    public boolean isGroupMember(String member, boolean checkChildren);

    /**
     * Adds the specified user member to this role.
     * These changes will only take effect if you commit these 
     * changes to the role data store {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.      
     *
     * @param newMember - the uniqueIdOfUser to add to this role.
     * @return <code>true</code> if the member was successfully added, <code>false</code> 
     * otherwise
     */
     public boolean addUserMember(String newMember) throws UMException;

    /**
     * Adds the specified group member to this role.
     * These changes will only take effect if you commit these 
     * changes to the role data store 
     * {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.      
     *
     * @param newMember - the uniqueIdOfGroup to add to this role.
     * @return <code>true</code> if the group was successfully added, <code>false</code> 
     * otherwise
     */
     public boolean addGroupMember(String newMember) throws UMException;

    /**
     * Remove the specified user member from this role.
     * These changes will only take effect if you commit these 
     * changes to the role data store 
     * {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.      
     *
     * @param oldMember - the uniqueIdOfUser to remove from this role.
     * @return <code>true</code> if the user member was successfully removed,
     * <code>false</code> otherwise
     */
     public boolean removeUserMember(String oldMember) throws UMException;

    /**
     * Remove the specified group member from this role.
     * These changes will only take effect if you commit these 
     * changes to the role data store 
     * {@link IPrincipalMaint#commit()} or to roll them
     * back (i.e. discard them) if appropriate.      
     *
     * @param oldMember - the uniqueIdOfGroup to remove from this collection.
     * @return <code>true</code> if the group member was successfully removed, 
     * <code>false</code> otherwise
     */
     public boolean removeGroupMember(String oldMember) throws UMException;

    /**
     * Returns the description of this principal.
     *
     * @return String: the description of this principal
     *         <code>null</code>  : if no description exists
     */
    public String getDescription();

    /**
     * Sets the description of this principal.
     * @param description String representing the description of a role
     * @exception UMException if the description could not be set
     */
    public boolean setDescription(String description) throws UMException;

    /**
     * Get uniqueName of this <code>IRole</code> object.
     * A (usually) unique readable name of an instance of IRole.
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

package com.sap.security.api;


/**
 * This interface provides read and write access to attributes and the state of an IPrincipalSet object.
 * IPrincipalSet extends IPrincipalMaint.
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @version 1.0
 */

public interface IPrincipalSet extends IPrincipalMaint
{

    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IPrincipalSet.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    /**
     * Returns principals belonging to this collection
     * @param getChildMembers This method does a recursive search if the parameter getChildMembers
     * is set to true.
     * 
     * @return iterator of Strings of uniqueIds of this principal set 
     */
    public java.util.Iterator getMembers(boolean getChildMembers);

    /***
     * Returns true if the passed principal is a member of the collection.
     * This method does a recursive search if parameter recursive is set to
     * <code>true</code>. If a principal belongs to a
     * collection which is a member of this collection, <code>true</code> is returned.
     *
     * @param uniqueIdOfPrincipal - the uniqueId of the principal whose membership is to be checked.
     * @param recursive If set to true a recursive search is done.
     * @return true if the principal is a member of this collection, false otherwise.
     */
    public boolean isMember(String uniqueIdOfPrincipal , boolean recursive);

    /**
     * Add the specified member to the collection. After
     * setting the appropriate data this object
     * must be commited to the principal store via {@link IPrincipalMaint#commit()}.
     *
     * @param newMember - the uniqueId of the object to add to this collection.
     * @return :true if the member was successfully added.
     * @throws UMException if an error occurs
     */
     public boolean addMember(String newMember) throws UMException;
    

    /**
     * Remove the specified member from the collection. After
     * setting the appropriate data this object
     * must be commited to the user store via {@link IPrincipalMaint#commit()}.
     *
     * @param oldMember - the uniqueId of the object to remove from this collection.
     * @return :true if the member was successfully removed
     * @throws UMException if an error occurs
     */
     public boolean removeMember(String oldMember) throws UMException;

}
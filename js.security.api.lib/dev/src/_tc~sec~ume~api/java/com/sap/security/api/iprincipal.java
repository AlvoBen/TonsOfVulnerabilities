package com.sap.security.api;

/**
 * This interface provides read-access to attributes and the state of an IPrincipal object.
 * <p>User Management Engine (UME) provides a unique Identifier 
 * {@link IPrincipal#getUniqueID()} for all 
 * instances of interface <code>IPrincipal</code> and subinterfaces for example
 * <code>IUser</code>, <code>IGroup</code>, <code>IRole</code>, etc. 
 * You can use this identifier to keep and persist 
 * references to principals. As this identifier contains internal information
 * and is usually not readable, it should not be used in end user interfaces.
 * <p>Use method {@link IPrincipal#getDisplayName()} in order to display an
 * attribute with a nice name for user interfaces. 
 * 
 * <p><b>NOTE</b>: Every method of every object which implements this interface may throw
 *       a PrincipalNotAccessibleException if the instantiated object was deleted,
 *       or is not accessible because of other reasons.
 *       As this interfacecan be extended, this interface can be freely used, 
 *       but must not be implemented.
 * 
 * @version 1.0
 * 
 */

public interface IPrincipal extends java.io.Serializable
{
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IPrincipal.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

/***
 * Constant used for default namespace
 ***/
    public static final String DEFAULT_NAMESPACE      = "com.sap.security.core.usermanagement";
/***
 * Constant used for default relation namespace
 ***/
    public static final String DEFAULT_RELATION_NAMESPACE     = "com.sap.security.core.usermanagement.relation";
/***
 * Constant used for default usermapping namespace
 ***/    
//    public static final String USERMAPPING_NAMESPACE  = "com.sap.security.core.usermanagement.usermapping";
/***
 * Constant used for transient data namespace
 ***/    
    public static final String TRANSIENT_NAMESPACE = "$transient$";

/***
 * Constant used for attribute creation date
 ***/    
    public static final String PRINCIPAL_CREATION_DATE             = "PRINCIPAL_CREATION_DATE";
/***
 * Constant used for attribute created by
 ***/    
    public static final String CREATED_BY             			   = "CREATED_BY";
/***
 * Constant used for attribute modification date
 ***/    
    public static final String PRINCIPAL_MODIFY_DATE               = "PRINCIPAL_MODIFY_DATE";
/***
 * Constant used for attribute last modified by
 ***/    
    public static final String LAST_MODIFIED_BY                    = "LAST_MODIFIED_BY";
/***
 * Constant used for member attribute
 ***/    
    public static final String PRINCIPAL_RELATION_MEMBER_ATTRIBUTE = "PRINCIPAL_RELATION_MEMBER_ATTRIBUTE";
/***
 * Constant used for parent member attribute
 ***/    
    public static final String PRINCIPAL_RELATION_PARENT_ATTRIBUTE = "PRINCIPAL_RELATION_PARENT_ATTRIBUTE";

/***
 * Constant used for displayname attribute
 ***/    
    public static final String    DISPLAYNAME                    = "displayname";
/***
 * Constant used for description attribute
 ***/       
    public static final String    DESCRIPTION                    = "description";
/***
 * Constant used for uniquename attribute
 ***/    
    public static final String    UNIQUE_NAME                    = "uniquename";

/***
 * Constant used for attribute string type
 ***/    
    public static final String STRING_TYPE                       = "String";
/***
 * Constant used for attribute byte type
 ***/    
    public static final String BYTE_TYPE                         = "Byte";

    /***
     * Constant used for datasource attribute
     ***/    
    public static final String    DATASOURCE                    = "datasource";

    /**
     * Gets the unique identifier which unambiguously identifies the object's 
     * <b>principal type and principal</b>'s data 
     * record(s) in the data store (e.g. a relational database).
     * <p><b>Note: the unique ID is a case sensitive string</b>
     * <p>Use this identifier to keep and persist references to principals.
     * As this identifier contains internal information and 
     * is usually not readable, it should not be displayed in
     * user interfaces. 
     * <p>UME implementation guarantees to return Strings
     * which are not longer than 255 characters.
     * @return a non-<code>null</code> String identifying this principal
     */
    public String getUniqueID ();


    /**
     * Gets the date when the object's data record was created in the data
     * store.
     * @return a Date object or <code>null</code> if creation date is not available
     */
    public java.util.Date created ();


    /**
     * Gets the date when the object's data record was last modified in the
     * data store.
     * @return a Date object or <code>null</code> if last modification date is not available     
     */
    public java.util.Date lastModified ();

    /**
     * Generic get method to access additional attributes. These attributes
     * are contained in separate namespaces, and are accessible via their names.
     * Attributes that are in no distinct namespace are located in the
     * namespace <code>null</code>. Each attribute can have multiple String
     * values.
     * @param	namespace	namespace the attribute is in (may be
     *                          <code>null</code>)
     * @param	name		name of the attribute
     * @return	the values of the respective attribute, or <code>null</code> if
     *          this namespace or attribute within this namespace does not exist
     */
    public String[] getAttribute (String namespace, String name);

    /**
     * Generic get method to access additional binary attributes. These attributes
     * are contained in separate namespaces, and are accessible via their names.
     * Attributes that are in no distinct namespace are located in the
     * namespace <code>null</code>.
     * @param	namespace	namespace the attribute is in (may be
     *                          <code>null</code>)
     * @param	name		name of the attribute
     * @return	the values of the respective attribute, or <code>null</code> if
     *          this namespace or attribute within this namespace does not exist
     */
    public byte[] getBinaryAttribute(String namespace, String name);

    /**
     * Gets all non-null namespaces defined for this user. Implementations must
     * guarantee that even if the namespace <code>null</code> exists, it is
     * not returned as an element in the array. Applications which need to
     * access the namespace <code>null</code> must check for its existence and
     * the contained attributes explicitly via <code>getAttributeNames(null)</code>.
     * @return all non-null namespaces defined for this user
     */
    public String[] getAttributeNamespaces ();


    /**
     * Gets the names of all attributes contained in the given
     * <code>namespace</code>, or <code>null</code> if that namespace does
     * not exist. If <code>namespace</code> exists but contains no
     * attributes, an empty array will be returned.
     * To retrieve the names of all attributes that are in no
     * distinct namespace, call this method with the parameter
     * <code>null</code>
     * @param namespace namespace or <code>null</code>
     * @return attribute names in <code>namespace</code> or <code>null</code>
     */
    public String[] getAttributeNames (String namespace);

    /**
     * Gets the principal's display name. Depending on the implementation, this
     * might be an alternative (alias) name, which is not required to be unique
     * and different from the user's unique ID.
     * displayName would be the preferred key to be used in the UI,
     * as it is more human readable and not language dependent.
     * @return a String representing the displayName
     */
    public String getDisplayName ();

    /**
     * Checks if this user's existence on the persistence storage was checked
     * 
     * @return <code>true</code> if the existence of this principal is already checked.
     * <code>false</code> if the existence of this principal is not checked yet.
     */
    public boolean isExistenceChecked();

    /**
     * Refresh Object
     * Reads all attributes of this object again from data store
     * <p> Note: The cached principal object is updated with this method. Calling this
     * method has impact on performance and on load of the system because 
     * the principal object is read again from the data store.
     *
     * @throws exception if object could not be refreshed
     */
    public void refresh() throws UMException;

    /**
     * Check if the object can be modified
     *
     * @return true if the principal object is mutable
     */
    public boolean isMutable();

    /**
     * To compare the two instances of the implementation.
     * @return true if both instances are of the same object
     * type and have the same uniqueId
     */
    public boolean equals(Object another);

	/**
	 * Returns a hash code value for the object. 
	 * This method is supported for the benefit of hashtables such as those provided
	 *  by java.util.Hashtable.
	 */
	public int hashCode();
	
    /**
     * Gets the type of the attribute. Returns {@link #STRING_TYPE} if the
     * attribute is of type String, or {@link #BYTE_TYPE} if it is a 
     * binary attribute.
     * Returns <code>null</code> if the attribute is not available.
     *
     * @return the type of the attribute
     */
    public String getAttributeType(String namespace, String attributeName);
    
    /**
     * Gets the list of all parent principals including parents, grandparents, ...
     * which have the specified principal types (e.g. USER,GRUP,ROLE etc.)
     * 
     * NOTE:      using this method can cause performance problems if used recursively,
     *            because the number of search requests depends on the principal hierarchy.
     *            In the worst case there will be 
     *            principalTypeIdentifiers.length X depth of the hierarchy
     * 
     *                     Level 1 | Level 2 | Level3
     *            example: USER u1
     *                          |-ROLE r1
     *                                  |-GRUP g1
     *                                  |-GRUP g3
     *                                  |-GRUP g4
     *                                  |-GRUP g5
     *                                  |-GRUP g6
     *                          |-ROLE r2
     *                          |-ROLE r3
     *                          |-GRUP g2
     *                          |-GRUP g3
     * 
     *                     getParents(new String[] {"USER","GRUP","ROLE"}, true);
     *                     will lead to 3 X 2 search requests!
     *            
     *
     * @return iterator of uniqueIds of all parent principals of this principal
     */
    public java.util.Iterator getParents(String[] principalTypeIdentifiers ,boolean recursive);
    
    /**
     * Returns an iterator which contains the {@link IMessage} objects assigned to this {@link IPrincipal} or
     * <code>null</code> if no messages are assigned. If the method is called with parameter <code>true</code>, 
     * every subsequent call will return <code>null</code> if no new message was assigned to this {@link IPrincipal}.
     * @param clearPermanentMessages Specifies whether permanent messages with life time {@link IMessage#LIFETIME_PERMANENT} should be removed from the message buffer.
     * @return The messages assigned to this {@link IPrincipal} object or <code>null</code>.
     */
    public java.util.Iterator getMessages(boolean clearPermanentMessages);

}

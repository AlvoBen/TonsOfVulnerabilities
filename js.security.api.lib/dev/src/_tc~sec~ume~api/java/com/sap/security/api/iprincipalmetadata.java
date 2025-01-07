package com.sap.security.api;

import java.util.Locale;


/**
 * This interface provides methods to 
 * <ol>
 * <li>access meta data of principal objects.</li>
 * </ol>
 */
public interface IPrincipalMetaData
{

    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IPrincipalMetaData.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    public static final int IPRINCIPAL    = 0x00;
    public static final int IPRINCIPALSET = 0x01;

	public static final String PARENT_NAMESPACE = "com.sap.caf.um.metadata";
	public static final String PARENT_ATTRIBUTE = "directparenttypes";

    /**
     * Returns the title of the IPrincipal object which is described by this
     * IPrincipalMetaData object.
     * Returns the title for the given locale or null, if no title is available
     * for the given locale.
     * 
     * @param locale the locale
     * @return the title for the given locale or null if it's not available
     */
    public String getTitle(Locale locale);

    /**
     * Returns the title of the IPrincipal object which is described by this
     * IPrincipalMetaData object.
     * Returns the title for the given locale or null, if no title is available
     * for the given locale.
     * 
     * @param locale the locale
     * @return the title for the given locale or null if it's not available
     */
    public String getDescription(Locale locale);

    /**
     * Sets the title for the given locale. If the given title is null, a already
     * set title for the given locale will be deleted.
     * Null values for the locale are allowed. It is recommended to set at least a description
     * for the locale en_US because this is used as default locale by most applications.
     *
     * @param title the title or null
     * @param locale the locale
     */
     public void setTitle(String title, Locale locale);
    
    /**
     * Sets the description for the given locale. If the given description is null, a already
     * set description for the given locale will be deleted.
     * Null values for the locale are allowed. It is recommended to set at least a description
     * for the locale en_US because this is used as default locale by most applications.
     *
     * @param description the description or null
     * @param locale the locale
     */
     public void setDescription(String description, Locale locale);
     
	/**
	 * Gets the {@link IPrincipalFactory#newPrincipal(String) principal type identifier} of the described IPrincipal object.
     * For details about the principal type identifier see {@link IPrincipalFactory#newPrincipal(String)}
	 * @return The {@link IPrincipalFactory#newPrincipal(String) principal type identifier}
	 */
     public String getPrincipalTypeIdentifier();

    /**
     * Gets the semantic type of the described IPrincipal object, e.g. IPrincipalMetaData.IPRINCIPAL
     * or IPrincipalMetaData.IPRINCIPALSET
     * @return The semantic type of the IPrincipalObject
     */
     public int getPrincipalType();
     
    /**
     * Generic method to associate arbitrary text data with a IPrincipalMetaData object.
     * The method will return <code>true</code> if <code>values</code> is
     * different from the attribute's previous values, <code>false</code>
     * otherwise.
     * Namespace and name can have up to 255 characters. Each value 
     * can have up to 255 characters.
     * @param namespace namespace of the attribute to set (max. 255 characters).
     * @param name      name of the attribute (max. 255 characters)
     * @param values    values of the attribute (each max. 255 characters)
     * @exception UMRuntimeException if either <code>namespace</code>
     *            or <code>name</code> is not supported
     */
    public boolean setAttribute (String namespace, String name, String[] values);

    /**
     * Generic method to associate arbitrary binary data with a IPrincipalMetaData object.
     * The method will return <code>true</code> if <code>values</code> is
     * different from the attribute's previous values, <code>false</code>
     * otherwise.
     * Namespace and name can have up to 255 characters.
     * @param namespace namespace of the attribute to set (max. 255 characters).
     * @param name      name of the attribute (max. 255 characters)
     * @param value     byte array of values of the attribute
     * @exception UMRuntimeException if either <code>namespace</code>
     *            or <code>name</code> is not supported
     */
    public boolean setBinaryAttribute(String namespace, String name, byte[] value);
     
    /**
     * Gets the type of the attribute. Returns {@link IPrincipal#STRING_TYPE} if the
     * attribute has the type String, or {@link IPrincipal#BYTE_TYPE} if it is a 
     * binary attribute.
     * Returns null if the attribute is not available.
     *
     * @return the type of the attribute
     */
    public String getAttributeType(String namespace, String attributeName);

    /**
     * Gets the names of all attributes contained in the given
     * <code>namespace</code>, or <code>null</code> if that namespace does
     * not exist. If <code>namespace</code> does exists but contains no
     * attributes, an empty array will be returned.
     * To retrieve the names of all attributes that are in no
     * distinct namespace, call this method with the parameter
     * <code>null</code>
     * @param namespace namespace or <code>null</code>
     * @return attribute names in <code>namespace</code> or <code>null</code>
     */
    public String[] getAttributeNames (String namespace);

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
     * Generic get method to access additional attributes. These attributes
     * are contained in separate namespaces, and are accessible via their names.
     * Attributes that are in no distinct namespace are formally located in the
     * namespace <code>null</code>. Each attribute can have multiple String
     * values.
     * @param   namespace   namespace the attribute is in (may be
     *                          <code>null</code>)
     * @param   name        name of the attribute
     * @return  the values of the respective attribute, or <code>null</code> if
     *          this namespace or attribute within this namespace does not exist
     */
    public String[] getAttribute (String namespace, String name);

    /**
     * Generic get method to access additional attributes. These attributes
     * are contained in separate namespaces, and are accessible via their names.
     * Attributes that are in no distinct namespace are formally located in the
     * namespace <code>null</code>.
     * @param   namespace   namespace the attribute is in (may be
     *                          <code>null</code>)
     * @param   name        name of the attribute
     * @return  the values of the respective attribute, or <code>null</code> if
     *          this namespace or attribute within this namespace does not exist
     */
    public byte[] getBinaryAttribute(String namespace, String name);

}
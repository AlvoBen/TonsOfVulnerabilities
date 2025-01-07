package com.sap.security.api;

import java.util.Iterator;
/**
 * This interface provides read-access to the user's attributes, and offers
 * basic support for authorization checking. Implementations of this interface
 * must make sure that all get-methods with a return type of <code>String</code>,
 * e.g. <code>getFax()</code> etc. return <code>null</code>
 * if that attribute either does not exist or has a <code>null</code>
 * value in the user store. This does NOT apply to the generic
 * <code>getAttribute...()</code> methods.
 * <p>If you want to keep and persist an identifier as a  
 * reference to an instance of <code>IUser</code> you must use 
 * method {@link IPrincipal#getUniqueID()}.
 * As this identifier contains internal information
 * and is usually not readable, it should not be used in end user interfaces.
 * <p>Use methods {@link IPrincipal#getDisplayName()} or 
 * {@link IUser#getUniqueName()}
 * in order to display
 * attributes with a nice name for user interfaces. 
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
 
public  interface IUser extends java.security.Principal, com.sap.security.api.IPrincipal
{
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IUser.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
/***
 * Constant used for user's default accessibility level 
 ***/
    public static final int       DEFAULT_ACCESSIBILITY_LEVEL = 0;
/***
 * Constant used for user's screen reader support 
 ***/
    public static final int       SCREENREADER_ACCESSIBILITY_LEVEL=1;

    /**
     * Gets the user ID. This method is included for backward compatibility with
     * the SAPMarkets user management only. New applications should not use it.
     * @deprecated new applications should use {@link IPrincipal#getUniqueID()}.
     */
    public String getUid ();


    /* JOB RELATED */


    /**
     * Gets the user's job title, e.g. Developer, Development Architect,
     * Development Manager etc.
     * @return String user's job title
     */
    public String getJobTitle ();



    /**
     * Gets the user's department.
     * @return String user's department
     */
    public String getDepartment ();


    /* ADDRESS */


    /**
     * Gets the user's academic title or title of nobility
     * @return String user's academic title
     */
    public String getTitle ();


    /**
     * Gets the user's salutation, e.g. Mr., Ms., Mrs.
     * @return String user's salutation
     */
    public String getSalutation ();


    /**
     * Gets the user's first name. This may include one or more middle names.
     * @return String user's first name
     */
    public String getFirstName ();


    /**
     * Gets the user's last name. This may include second names.
     * @return String user's last name
     */
    public String getLastName ();


    /**
     * Gets the user's street. This may include house numbers, street numbers
     * etc.
     * @return String user's street address
     */
    public String getStreet ();


    /**
     * Gets the user's city.
     * @return String user's city
     */
    public String getCity ();


    /**
     * Gets the zip code of the user's city.
     * @return String user's zip code
     */
    public String getZip ();


    /**
     * Gets the user's state or region.
     * @return String user's state or region
     */
    public String getState ();


    /**
     * Gets the ISO-3166 two-letter uppercase code of the country where the
     * user lives. NOTE that this is independent from the country available
     * via <code>getCountry()</code> of the user's Locale, since expatriate users
     * may not have their Locale's country set to the country where they
     * actually reside.
     * @return String user's country
     */
    public String getCountry ();


    /**
     * Gets the user's Locale; this determines e.g. the language in which text
     * is displayed to the user's and the style that numbers are formated.
     * Applications needing to get the country where the user resides must call
     * {@link #getCountry()} instead of using the Locale's country.
     * @return Locale user's locale object or <code>null</code> in case no locale
     * is stored for this IUser object.
     */
    public java.util.Locale getLocale ();


    /**
     * Gets the user's time zone.
     * @return TimeZone user's timezone object or <code>null</code> in case no 
     * timezone is stored for this IUser object.
     * <p>NOTE: Do not modify this TimeZone object. For performance reasons
     * the TimeZone object is only created once when method getTimeZone() is
     * called the first time. In case you modify this TimeZone object you may
     * encounter inconsistencies because the user object has a different Timezone
     * value set on the persistence layer.  
     */
    public java.util.TimeZone getTimeZone ();


    /**
     * Gets the three letter uppercase code of the user's currency, e.g. DEM,
     * EUR or USD.
     * @return String user's currency
     */
    public String getCurrency ();


    /**
     * Gets the user's default telephone number.
     * @return String user's telephone
     */
    public String getTelephone ();


    /**
     * Gets the user's default cellphone number.
     * @return String user's cell phone
     */
    public String getCellPhone ();


    /**
     * Gets the user's default fax number.
     * @return String user's fax
     */
    public String getFax ();


    /**
     * Gets the user's default email address.
     * @return String user's email
     */
    public String getEmail ();


    /**
     * Checks if this user belongs to a company.
     * @return <code>true</code> if user belongs to company
     */
    public boolean isCompanyUser ();


    /**
     * NOTE: Released for internal use only.
     * Gets the company ID of the user's company.
     * @return String company id
     */
    public String getCompany ();


    /**
     * Gets the user factory which instantiated this user object.
     * @return IUserFactory user factory
     */
    public IUserFactory getUserFactory ();

    /**
     * Returns <code>true</code> if the user has the given <code>permission</code>.
     * 
     * @param permission	Permission object which is checked     
     * @return <code>true</code> if the user has the given <code>permission</code>.
     */
    public boolean hasPermission (java.security.Permission permission);


    /**
     * If the user has the specified <code>permission</code>, this method does 
     * nothing. If not, it will throw an <code>AccessControlException</code>, 
     * and possibly trigger appropriate logging action.
     * 
     * @param permission	Permission object which is checked     
     * @exception AccessControlException if the user doesn't have the specified
     *                                   <code>permission</code>
     */
    public void checkPermission (java.security.Permission permission)
        throws java.security.AccessControlException;

	/**
	 * Returns <code>true</code> if the user has the given <code>permission</code>
	 * in the given context.
	 * 
	 * <b>Note: The context specific permission check is no longer supported.
	 *          It does the same as <code>hasPermission(java.security.Permission)</code>.</b> 
	 * 
	 * @param contextID		context for that the permission is checked     
	 * @param permission	Permission object which is checked     
	 * @return <code>true</code> if the user has the given <code>permission</code>.
	 */
	public boolean hasPermission (String contextID, java.security.Permission permission);


	/**
	 * If the user has the specified <code>permission</code> in the given context, 
	 * this method does nothing. If not, it will throw an <code>AccessControlException</code>, 
	 * and possibly trigger appropriate logging action.
	 * 
	 * <b>Note: The context specific permission check is no longer supported.
	 *          It does the same as <code>checkPermission(java.security.Permission)</code>.</b> 
	 * 
	 * @param contextID		context for that the permission is checked     
	 * @param permission	Permission object which is checked     
	 * @exception AccessControlException if the user doesn't have the specified
	 *                                   <code>permission</code>
	 */
	public void checkPermission (String contextID, java.security.Permission permission)
		throws java.security.AccessControlException;

    /**
     * 
     * Returns an array of useraccount objects which are assigned to this user.
     * Returns a empty array of type IUserAccount in case 
     * that this user is not assigned to any accounts.
     * @return IUserAccount[] user account objects associated with this user
     */
     public IUserAccount[] getUserAccounts() throws UMException;

	/**
	 * 
	 * Returns an iterator of String objects which 
	 * represent the uniqueIDs of assigned IUserAccount objects
	 * 
     * @return iterator of accounts assigned to this principal. The iterator contains 
     * String objects representing the uniqueIDs of assigned IUserAccount objects. 
	 * 
	 */
	 public java.util.Iterator getUserAccountUniqueIDs() throws UMException;

    /**
     * Gets the user's accessibilityLevel
     * used for Screen Reader Support
     * @return	accessibility level of screen reader support.
     * If no value is set in datastore
     * the default return value is
     * {@link #DEFAULT_ACCESSIBILITY_LEVEL}
     * Following constants can be returned:
     * {@link #DEFAULT_ACCESSIBILITY_LEVEL}
     * {@link #SCREENREADER_ACCESSIBILITY_LEVEL}
     */
     public int getAccessibilityLevel();

    /**
     * Gets the list of (all) assigned roles of this user including parent groups,
     *  grandparent groups,...
     * if recursive is set to <code>true</code>
     * 
     * NOTE: This method may also return roles which are already deleted.
     * 
     * @param recursive if true returns all parent roles
     * @return iterator of roles for this principal. The iterator contains 
     * uniqueIdOfRole strings
     */
     public java.util.Iterator getRoles(boolean recursive);

    /**
     * Gets the list of (all) parent groups including parents, grandparents, ...
     * if recursive is set to <code>true</code>
     * @param recursive if true returns all parent groups
     * @return iterator of groups of this user. The iterator contains 
     * uniqueIdOfGroup strings
     */
    public Iterator getParentGroups(boolean recursive);

   /**
     * Checks if the principal belongs to the passed roleId
     * This method does a recursive search if parameter <code>recursive</code>
     * is set to <code>true</code>. If this user belongs to a
     * group which is a member of a role, <code>true</code> is returned.
     * @param uniqueIdOfRole identifying the role
     * @param recursive if <code>true</code> checks also all parent groups     
     * @return <code>true</code> if this user is directly or indirectly (via role membership)
     * assigned.
     */
    public boolean isMemberOfRole(String uniqueIdOfRole, boolean recursive);

    /**
     * Checks if the principal belongs to the passed uniqueIdOfGroup
     * This method does a recursive search, so if this principal belongs to a
     * group which is a member of this group, true is returned.
     * @param uniqueIdOfGroup the ID of the collection
     *
     * @return true if the principal is directly or indirectly (via group membership)
     * assigned to uniqueIdOfGroup.
     *     
     */
    public boolean isMemberOfGroup(String uniqueIdOfGroup, boolean recursive);

    /**
     * Get uniqueName of this <code>IUser</code> object.
     * A (usually) unique readable name of an instance of IUser.
     * <p><b>Note</b>: Depending on the persistence layer
     * it is not guaranteed that this name
     * is unique. The uniqueName may change over time. Thus, do not persist the 
     * uniqueName. Always use the unique identifier of 
     * {@link IPrincipal#getUniqueID()}
     * for this purpose.
     * <p>Use the uniqueName for searching and displaying in user interfaces.
     * @return uniqueName of object
     */
    public String getUniqueName();

    /**
     * Generic get method to access additional transient attributes. These attributes
     * are contained in separate namespaces, and are accessible via their names.
     * Attributes that are in no distinct namespace are formally located in the
     * namespace <code>null</code>.
     * <p>Note: the transient attributes are instance specific.
     * @param	namespace	namespace the attribute is in (may be
     *                          <code>null</code>)
     * @param	name		name of the attribute
     * @return	the values of the respective attribute, or <code>null</code> if
     *          this namespace or attribute within this namespace does not exist
     */
    public Object getTransientAttribute(String namespace, String name);

    /**
     * Generic method to associate arbitrary data with a principal.
     * The method will return <code>true</code> if <code>values</code> is
     * different from the attribute's previous values, <code>false</code>
     * otherwise.
     * These texts are tansient and will not be stored in the persistence.
     *
     * @param namespace namespace of the attribute to set.
     * @param name      name of the attribute
     * @param o         value of the attribute
     */
    public boolean setTransientAttribute(String namespace, String name, Object o);

    /**
     * Returns the personID of the IUser or <code>null</code>.
     * 
     * @exception UMException if an error occurred.
     * @return the personID or null.
     */
    public String getPersonID() throws UMException;
    
    /**
     * Creates I18nFormatterFactory with the current user format settings and locale.
     * @return The utility object to handle user format information.
     * @throws UMException if an error occurred.
     */
    public com.sap.i18n.cpbase.I18nFormatterFactory getI18nFormatterFactory() throws UMException;

}

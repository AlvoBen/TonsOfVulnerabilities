package com.sap.security.api;

/**
 * This interface extends <code>IUser</code> and provides write access to
 * a user.
 * 
 * It provides methods to change the
 * user's attributes, to commit these changes to the user store or to roll them
 * back (i.e. discard them) if appropriate. It is intended for administration of
 * user profiles (including self-administration).
 * It does not cover account, password and authorization
 * management. The set-methods with boolean return values return <code>true</code>
 * if the new value is different from the previous value.
 * Calling set-methods with a value of
 * <code>null</code> effectively removes an attribute.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public  interface IUserMaint
    extends com.sap.security.api.IUser, com.sap.security.api.IPrincipalMaint {


    /**
     * Sets the user's academic title or title of nobility.
     * @param title String containing the user's title
     */
    public boolean setTitle (String title);


    /**
     * Sets the user's salutation. This may include academic titles.
     * @param salutation String containing the user's salutation
     */
    public boolean setSalutation (String salutation);


    /**
     * Sets the user's job title, e.g. Developer, Development Architect,
     * Development Manager etc.
     * @param jobtitle String containing the user's jobtitle
     */
    public boolean setJobTitle (String jobtitle);


    /**
     * Sets the user's department.
     * @param department String containing the user's department
     */
    public boolean setDepartment (String department);

    /**
     * Sets the user's display name.
     * @param displayName String containing the user's displayName
     */
    public boolean setDisplayName (String displayName);

    /**
     * Sets the user's first name. This may include one or more middle names.
     * @param firstname String containing the user's firstname
     */
    public boolean setFirstName (String firstname);


    /**
     * Sets the user's last name. This may include second names.
     * @param lastname String containing the user's lastname
     */
    public boolean setLastName (String lastname);


    /**
     * Sets the user's street. This may include house numbers, street numbers
     * etc.
     * @param street String containing the user's street
     */
    public boolean setStreet (String street);


    /**
     * Sets the user's city.
     * @param city String containing the user's city
     */
    public boolean setCity (String city);


    /**
     * Sets the zip code of the user's city.
     * @param zip String containing the user's zip
     */
    public boolean setZip (String zip);


    /**
     * Sets the user's state or region.
     * @param state String containing the user's state
     */
    public boolean setState (String state);


    /**
     * Sets the ISO-3166 two-letter uppercase code of the country where the
     * user lives. <p>NOTE that this is independent from the country available
     * via <code>getCountry()</code> of the user's Locale, since expatriate users
     * may not have their Locale's country set to the country where they
     * actually reside.
     * @param country String containing the user's country
     */
    public boolean setCountry (String country);


    /**
     * Sets the user's Locale; this determines e.g. the language in which text
     * is displayed to the user's and the style that numbers are formated.
     * @param locale String containing the user's locale object
     */
    public boolean setLocale (java.util.Locale locale);


    /**
     * Sets the user's time zone.
     * @param timezone String containing the user's timezone
     */
    public boolean setTimeZone (java.util.TimeZone timezone);


    /**
     * Sets the three letter uppercase code of the user's currency, e.g. DEM,
     * EUR or USD.
     * @param currency String containing the user's currency
     */
    public boolean setCurrency (String currency);


    /**
     * Sets the user's default telephone number.
     * @param telephone String containing the user's telephone
     */
    public boolean setTelephone (String telephone);


    /**
     * Sets the user's default cellphone number.
     * @param cellphone String containing the user's cellphone
     */
    public boolean setCellPhone (String cellphone);


    /**
     * Sets the user's default fax number.
     * @param fax String containing the user's fax
     */
    public boolean setFax (String fax);


    /**
     * Sets the user's default email address.
     * @param email String containing the user's email
     */
    public boolean setEmail (String email);

    /**
     * Sets the user's personID.
     * @param personid The new personID to set.
     */
    public boolean setPersonID(String personid);
    
    /**
     * Sets the unique ID of the user's company.
     * @param companyid String containing the user's companyid
     */
    public boolean setCompany (String companyid);

     /**
      * Associates user account to this user. Throws an exception if the display
      * names of these two objects do not match. Throws an exception if this method is being
      * called on a pre-existing user (meaning this method works only on the user objects which
      * are in process of creation but not yet saved in the storage).
      *
      * The purpose of this method to enable some implementations to save useraccount and user
      * data info in one step because many storage do not keep user data and account data
      * separately from each other. Default implementation uses user account factory
      * to save the account data and user factory to save to user data separately.
      */
      /** @todo check if account factory is sufficient */
     //public void setUserAccount(IUserAccount ua) throws UMException;

     /**
     * Set the user's accessibility level
     * used for Screen Reader Support
     * @param accessibilityLevel String containing the user's accessibility level
     * Valid values are following constants
     * {@link IUser#DEFAULT_ACCESSIBILITY_LEVEL} or
     * {@link IUser#SCREENREADER_ACCESSIBILITY_LEVEL}
     */
     public void setAccessibilityLevel(int accessibilityLevel ) throws UMException;

    /**
     * Assign this principal to a group identified by uniqueIdOfGroup.
     * Implicitly a commit is done if you call this method because the
     * membership of groups is stored in the group's object.
	 * Be aware that adding this user to a group is done independently
	 * of committing this IUserMaint changes to the user store or to roll them
 	 * back.
     * @param	uniqueIdOfGroup id of the group
     * @exception	UMException
     * @deprecated use {@link IGroupFactory#addUserToGroup(String,String)} instead
     */
    public void addToGroup(String uniqueIdOfGroup) throws UMException;

    /**
     * Unassign this principal from a group identified by uniqueIdOfGroup.
     * Implicitly a commit is done if you call this method because the
     * membership of groups is stored in the group's object data store.
	 * Be aware that removing this user from a group is done independently
	 * of committing this IUserMaint changes to the user store or to roll them
 	 * back.
     * @param	uniqueIdOfGroup id of the group
     * @exception	UMException
     * @deprecated use {@link IGroupFactory#removeUserFromGroup(String, String)} instead
     */
    public void removeFromGroup(String uniqueIdOfGroup) throws UMException;

    /**
     * Assign this principal to a role identified by uniqueIdOfRole.
     * Implicitly a commit is done if you call this method because the
     * membership of roles is stored in the role's object.
	 * Be aware that adding this user to a role is done independently
	 * of committing this IUserMaint changes to the user store or to roll them
 	 * back.
     * @param	uniqueIdOfRole uniqueId of a role
     * @exception	UMException
     * @deprecated use {@link IRoleFactory#addUserToRole(String, String)} instead
     */
    public void addToRole(String uniqueIdOfRole) throws UMException;

    /**
     * Unassign this principal from a role identified by uniqueIdOfRole.
     * Implicitly a commit is done if you call this method because the
     * membership of roles is stored in the role's object.
	 * Be aware that removing this user from a role is done independently
	 * of committing this IUserMaint changes to the user store or to roll them
 	 * back.
     * @param	uniqueIdOfRole uniqueId of a role
     * @exception	UMException
     * @deprecated use {@link IRoleFactory#removeUserFromRole(String, String)} instead
     */
    public void removeFromRole(String uniqueIdOfRole) throws UMException;

}

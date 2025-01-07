package com.sap.security.api;

import java.util.Locale;

/**
 * This class acts as a container for text values to be used as a search
 * filter when calling
 * {@link com.sap.security.api.IUserFactory#searchUsers(IUserSearchFilter)}.
 * All attributes are intialized with <code>null</code>.
 * Parameter mode in the set methods refer to the defined operators of
 * {@link ISearchAttribute}
 *  and can have following values:
 *   {@link ISearchAttribute#EQUALS_OPERATOR},
 *   {@link ISearchAttribute#LIKE_OPERATOR},
 *   {@link ISearchAttribute#GREATER_THAN_OPERATOR},
 *   {@link ISearchAttribute#LESS_THAN_OPERATOR}
 * @version 1.0
 */
public interface IUserSearchFilter extends IPrincipalSearchFilter{

    public final static String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IUserSearchFilter.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    /**
     * Get the title attribute value to match in the user search
     * @return The value of the title field or null if the field is not set
     */
    public String getTitle ();

    /**
     * Get the salutation attribute value to match in the user search
     * @return The value of the salutation field or null if the field is not set
     */
    public String getSalutation ();

    /**
     * Get the job title attribute value to match in the user search
     * @return The value of the job title field or null if the field is not set
     */
    public String getJobTitle ();

    /**
     * Get the department attribute value to match in the user search
     * @return The value of the department field or null if the field is not set
     */
    public String getDepartment ();

    /**
     * Get the first name attribute value to match in the user search
     * @return The value of the first name field or null if the field is not set
     */
    public String getFirstName ();

    /**
     * Get the last name attribute value to match in the user search
     * @return The value of the last name field or null if the field is not set
     */
    public String getLastName ();

    /**
     * Get the street attribute value to match in the user search
     * @return The value of the street field or null if the field is not set
     */
    public String getStreet ();

    /**
     * Get the city attribute value to match in the user search
     * @return The value of the city field or null if the field is not set
     */
    public String getCity ();

    /**
     * Get the zip attribute value to match in the user search
     * @return The value of the zip field or null if the field is not set
     */
    public String getZip ();

    /**
     * Get the state attribute value to match in the user search
     * @return The value of the state field or null if the field is not set
     */
    public String getState ();

    /**
     * Get the ISO-639 country attribute value to match in the user search
     * @return The value of the country field or null if the field is not set
     */
    public String getCountry ();

    /**
     * Get the currency attribute value to match in the user search
     * @return The value of the currency field or null if the field is not set
     */
    public String getCurrency ();

    /**
     * Get the telephone attribute value to match in the user search
     * @return The value of the telephone field or null if the field is not set
     */
    public String getTelephone ();

    /**
     * Get the cell phone attribute value to match in the user search
     * @return The value of the cell phone field or null if the field is not set
     */
    public String getCellPhone ();

    /**
     * Get the fax attribute value to match in the user search
     * @return The value of the fax field or null if the field is not set
     */
    public String getFax ();

    /**
     * Get the email attribute value to match in the user search
     * @return The value of the email field or null if the field is not set
     */
    public String getEmail ();

    /**
     * Get the company id attribute value to match in the user search
     * @return The value of the company id field or null if the field is not set
     * @deprecated use {@link IUserSearchFilter#getCompany()} instead.
     */
    public String getCompanyId ();

   /**
    * Get the company attribute value to match in the user search
    * @return The value of the company id field  or null if the field is not set
    */
   public String getCompany ();

    /**
     * Get the title attribute value to match in the user search
     * @param title The value which is searched in the title field of the user
     * @param mode Use the constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setTitle (String title, int mode, boolean caseSensitive);

    /**
     * Set the salutation attribute value to match in the user search
     * @param salutation The value which is searched in the salutation field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setSalutation (String salutation, int mode, boolean caseSensitive);

    /**
     * Set the job title attribute value to match in the user search
     * @param jobtitle The value which is searched in the job title field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setJobTitle (String jobtitle, int mode, boolean caseSensitive);

    /**
     * Set the department attribute value to match in the user search
     * @param department The value which is searched in the department field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setDepartment (String department, int mode, boolean caseSensitive);

    /**
     * Set the first name attribute value to match in the user search
     * @param firstname The value which is searched in the first name field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setFirstName (String firstname, int mode, boolean caseSensitive);

    /**
     * Set the last name attribute value to match in the user search
     * @param lastname The value which is searched in the last name field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setLastName (String lastname, int mode, boolean caseSensitive);

    /**
     * Set the street attribute value to match in the user search
     * @param street The value which is searched in the street field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setStreet (String street, int mode, boolean caseSensitive);

    /**
     * Set the city attribute value to match in the user search
     * @param city The value which is searched in the city field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setCity (String city, int mode, boolean caseSensitive);

    /**
     * Set the zip attribute value to match in the user search
     * @param zip The value which is searched in the zip field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setZip (String zip, int mode, boolean caseSensitive);

    /**
     * Set the state attribute value to match in the user search
     * @param state The value which is searched in the state field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setState (String state, int mode, boolean caseSensitive);

    /**
     * Set the ISO-639 country attribute value to match in the user search
     * @param country The value which is searched in the ISO-639 country field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setCountry (String country, int mode, boolean caseSensitive);

    /**
     * Set the currency attribute value to match in the user search
     * @param currency The value which is searched in the currency field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setCurrency (String currency, int mode, boolean caseSensitive);

    /**
     * Set the telephone attribute value to match in the user search
     * @param telephone The value which is searched in the telephone field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setTelephone (String telephone, int mode, boolean caseSensitive);

    /**
     * Set the cell phone attribute value to match in the user search
     * @param cellphone The value which is searched in the cell phone field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setCellPhone (String cellphone, int mode, boolean caseSensitive);

    /**
     * Set the fax attribute value to match in the user search
     * @param fax The value which is searched in the fax field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setFax (String fax, int mode, boolean caseSensitive);

    /**
     * Set the email attribute value to match in the user search
     * @param email The value which is searched in the email field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setEmail (String email, int mode, boolean caseSensitive);

    /**
     * Set the company attribute value to match in the user search
     * @param company The value which is searched in the company field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setCompany (String company, int mode, boolean caseSensitive);

	/**
	 * Set the company id attribute value to match in the user search
	 * @param company The value which is searched in the company field of users
	 * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
	 * @param caseSensitive Set case sensitivity
	 * @deprecated use {@link IUserSearchFilter#setCompany(String, int, boolean)} instead.
	 */
	public void setCompanyId (String company, int mode, boolean caseSensitive);

    /**
     * Get the locale attribute value to match in the user search
     * @return The value of the locale field or null if the field is not set
     */
    public Locale getLocale();

    /**
     * Set the locale attribute value to match in the user search
     * @param locale The value which is searched in the locale field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setLocale(Locale locale, int mode, boolean caseSensitive);

    /**
     * Set the description attribute value to match in the user search
     * @param description The value which is searched in the description field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setDescription(String description, int mode, boolean caseSensitive);

    /**
     * Get the description attribute value to match in the user search
     * @return The value of the description field or null if the field is not set
     */
    public String getDescription();

    /**
     * Set the unique name attribute value to match in the user search
     * @param uniqueName The value which is searched in the unique name field of users
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setUniqueName(String uniqueName, int mode, boolean caseSensitive);

    /**
     * Get the unique name attribute value to match in the user search
     * @return The value of the unique name field or null if the field is not set
     */
    public String getUniqueName();
}

package com.sap.security.api;

/**
 * This interface acts as a container for text values to be used as a search
 * filter when calling
 * {@link com.sap.security.api.IRoleFactory#searchRoles(IRoleSearchFilter)}.
 * All attributes are intialized with <code>null</code>.
 * @author d037363
 * @version 1.0
 */

public interface IRoleSearchFilter extends IPrincipalSearchFilter
{
    
  public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IRoleSearchFilter.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    /**
     * Set the description attribute value to match in the role search
     * @param description The value which is searched in the description field of roles
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setDescription(String description, int mode, boolean caseSensitive);

    /**
     * Get the description attribute value to match in the role search
     * @return The value of the description field
     */
    public String getDescription();

    /**
     * Set the uniquename attribute value to match in the role search
     * @param uniqueName The value which is searched in the uniquename field of roles
     * @param mode Use the constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setUniqueName(String uniqueName, int mode, boolean caseSensitive);

    /**
     * Get the uniquename attribute value to match in the role search
     * @return The value of the uniquename field
     */
    public String getUniqueName();
}

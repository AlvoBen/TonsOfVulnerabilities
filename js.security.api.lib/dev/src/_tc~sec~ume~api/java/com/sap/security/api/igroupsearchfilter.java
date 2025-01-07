package com.sap.security.api;


/**
 * This interface acts as a container for values to be used as a search
 * filter when calling {@link IGroupFactory#getGroupSearchFilter()}.
 * Using an initial IGroupSearchFilter object for a search will
 * select all groups which are available in the data store.
 * @author d022877
 * @version 1.0
 */
public interface IGroupSearchFilter extends IPrincipalSearchFilter
{

    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IGroupSearchFilter.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/**
	 * Set the description attribute value to match in the group search
	 * @param description The value which is searched in the description field of groups
	 * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
	 * @param caseSensitive Set case sensitivity
	 */
    public void setDescription(String description, int mode, boolean caseSensitive);

    /**
     * Set the uniquename attribute value to match in the group search
     * @param uniqueName The value which is searched in the uniquename field of groups
	 * @param mode The constant used in the search mode of 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setUniqueName(String uniqueName,int mode, boolean caseSensitive);

	/**
	 * Get the description attribute value to match in the group search
	 * @return The value of the description field
	 */
    public String getDescription();

    /**
     * Get the uniquename attribute value to match in the group search
     * @return The value of the uniquename field
     */
    public String getUniqueName();
}

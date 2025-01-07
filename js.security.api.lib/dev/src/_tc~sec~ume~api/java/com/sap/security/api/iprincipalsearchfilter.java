package com.sap.security.api;

/**
 * This interface provides the possibility for values to be used as a search
 * filter when calling {@link IPrincipalFactory#getPrincipalSearchFilter(boolean,String)}.
 * 
 * NOTE: For consistency reasons no leading or trailing spaces are allowed in
 *       namespaces, attribute names and String values.
 * 
 * Copyright:    Copyright (c) 2002
 * @author d037363
 * @version 1.0
 */

public interface IPrincipalSearchFilter {

    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IPrincipalSearchFilter.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

/***
 * Constant used to define the search operation. SEARCHMETHOD_AND can be used to combine 
 * search attributes with logical operator AND
 ***/	
    public static final int SEARCHMETHOD_AND = 0;
/***
 * Constant used to define the search operation. SEARCHMETHOD_OR can be used to combine 
 * search attributes with logical operator OR
 ***/	
    public static final int SEARCHMETHOD_OR  = 1;

    /**
     * Constant used to define namespace used for attributes you can not search for but used to 
     * specify specific search flags
     */
    public static final String TRANSIENT_SEARCH_NAMESPACE = "com.sap.security.core.search.transient";
    
    /**
     * transient search attribute used to activate the paged search on ldap persistence 
     * @deprecated
     */
    public static final String EXTENDED_SEARCH = "extendedsearch";

	/**
     * transient search attribute used to activate the permission check for PCD roles during a role search 
     * @deprecated
     */
    public static final String CHECK_ACCESS = "check_access";

    /**
     * transient search attribute used to hide UME roles during a role search 
     */
    public static final String HIDE_UME_ROLES = "hide_UME_roles";


	/**
	 * Set the search method. IPrincipalSearchFilter.SEARCHMETHOD_AND (default) or IPrincipalSearchFilter.SEARCHMETHOD_OR
     * 
	 * @param searchMethod The search method
	 */
    public void setSearchMethod(int searchMethod);

	/**
	 * Set the id of a direct child to match in the principal search. Makes only sense for principal sets like
     * IGroup, IRole and ICustomObjectSet
     * 
	 * @param principalID The unique id of the direct child
	 * @param mode Use constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
	 * @param caseSensitive The case sensitivity
	 */
    public void setDirectChild(String principalID, int mode, boolean caseSensitive);

    /**
     * Set the value of a attribute to match in the principal search.
     * If there's already a value set for the same attribute, the new
     * value will be appended.
     * 
     * @param namespace The namespace of the attribute
     * @param attribute The name of the attribute
     * @param value The value to match
	 * @param mode Use constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive The case sensitivity
     */
    public void setSearchAttribute(String namespace, String attribute, String value, int mode, boolean caseSensitive);


	/**
	 * Get the type of the principals to search.
     * e.g. USER,GRUP,ROLE,...
     * 
	 * @return String The type
	 */
    public String getSearchPrincipalType();

	/**
	 * Get the namespaces of the set search attributes
	 * @return String[] List of namespaces
	 */
    public String[] getSearchNamespaces();

	/**
	 * Get the attribute names of the set search attributes
	 * @param namespace The namespace of the attributes
	 * @return String[] The list of attributes
	 */
    public String[] getSearchAttributeNames(String namespace);

	/**
	 * Get the values to match the search for the given namespace and attribute
	 * @param namespace The namespace of the attribute
	 * @param attributeName The attribute's name
	 * @return String[] The list of values to match in the search
	 */
    public String[] getSearchAttributes(String namespace, String attributeName);

	/**
	 * Get the search operator of a specific criteria {@link com.sap.security.api.ISearchAttribute}
     * 
	 * @param namespace The namespace of the attribute
	 * @param attributeName The attribute's name
	 * @param attribute The value to match in the search
	 * @return int constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
	 */
    public int getSearchOperator(String namespace, String attributeName, Object attribute);

	/**
	 * Get the used search method. Returns IPrincipalSearchFilter.SEARCHMETHOD_AND or IPrincipalSearchFilter.SEARCHMETHOD_OR
	 * @return int The search method
	 */
    public int getSearchMethod();

	/**
	 * Removes all search attributes, namespaces and values.
	 */
    public void clear();

	/**
	 * Tests if this search filter has no components. 
	 * @return <code>true</code> if and only if this search filter 
	 * has no search criteria specified, that is, the search filter is in its 
	 * initial state; <code>false</code> otherwise
	 */
	public boolean isEmpty();

	/**
	 * Get the number of elements to search
	 * @return int
	 */
    public int getElementSize();

	/**
	 * Get the ISearchElement at a specific position in the list of search elements.
     * 
	 * @param i The index of the element in the list
	 * @return ISearchElement The search element object
	 * @deprecated use {@link #getSearchElementAt(int)} instead
	 */
    public com.sap.security.api.persistence.ISearchElement getElementAt(int i);

	/**
	 * Get the ISearchAttribute at a specific position in the list of search elements.
     * 
	 * @param i The index of the element in the list
	 * @return ISearchAttribute The search element object
	 */
    public ISearchAttribute getSearchElementAt(int i);

/**
 * Gets the display name to match.
 * @return String The displayname to match
 */
    public String getDisplayName ();

    /**
     * Set the displayname attribute value to match in the principal search.
     * The search will be done for all attributes which participate in the
     * creation of the displayname and the physical attribute displayname, if
     * available.
     * 
     * <p>NOTE: This method can only be used, if no other search attributes are
     *       set yet and vice versa.
     * 
     * @param displayname The value which is searched in the displayname field of principals
     * @param mode The search mode {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setDisplayName (String displayname, int mode, boolean caseSensitive);

	/**
	 * Set the maxium size of the search result. For an unlimited search result you have
	 * to specify a limit of '0' or smaller.
	 *
	 * <p>NOTE: This method can only be used, if only one search attribute
	 * 		is specified
	 * 
	 * @param resultsize The value which is used to limit the search result.
	 */
	public void setMaxSearchResultSize(int resultsize);

	/**
	 * Gets the max size of the search result
	 * @return int the max search result size 
	 */
	public int getMaxSearchResultSize();
    
    
}

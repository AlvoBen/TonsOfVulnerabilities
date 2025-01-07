/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.context;

import java.util.Iterator;

/**
 * SearchResult is an extension of Iterator and provides additional methods
 * do determine the state of the contained collection of objects.
 * 
 * @author  d031387
 * @version 6.40
 *
 */
public interface SearchResult extends Iterator {

	/**
	 * Constant used to define that the search was done successfully
	 * and that the search result contains all and only all matching objects.
	 **/
    public static final int SEARCH_RESULT_OK = 0x00;

	/**
	 * Constant used to define that the search result is not complete
	 * because of an error during execution of the search request. Some
	 * matching objects will therefore not be part of the search result,
	 * but the result does not contain any non-matching objects.
	 **/
    public static final int SEARCH_RESULT_INCOMPLETE = 0x01;    

	/**
	 * Constant used to define that the search result is undefined
	 * because it contains objects, which do not correctly match the search
	 * criteria. 
	 **/
    public static final int SEARCH_RESULT_UNDEFINED	= 0x02;        

	/**
	 * Constant used to define that a size limit exceeded. All objects
	 * in the search result match the search criteria, but not all matching
	 * objects are returned. The size limit may have been defined either in
	 * the search request (client) or in the persistency layer itself (server).
	 * 
	 * This is a special case of {@link SearchResult#SEARCH_RESULT_INCOMPLETE} 
	 **/    
	public static final int SIZE_LIMIT_EXCEEDED = 0x04;

	/**
	 * Constant used to define that a time limit exceeded. All objects
	 * in the search result match the search criteria, but not all matching
	 * objects are returned. The time limit may have been defined either in
	 * the search request (client) or in the persistency layer itself (server).
	 * 
	 * This is a special case of {@link SearchResult#SEARCH_RESULT_INCOMPLETE} 
	 **/    
	public static final int TIME_LIMIT_EXCEEDED = 0x08;

    /**
     * Returns the status of the search result as a bitmask
     * 
     * @return int: returns constants of interface ISearchResult:
     *  
     * {@link SearchResult#SEARCH_RESULT_OK},
     * {@link SearchResult#SEARCH_RESULT_INCOMPLETE},
     * {@link SearchResult#SIZE_LIMIT_EXCEEDED},
     * {@link SearchResult#TIME_LIMIT_EXCEEDED},
     * {@link SearchResult#SEARCH_RESULT_UNDEFINED}
     */
    public int getState();
    
}

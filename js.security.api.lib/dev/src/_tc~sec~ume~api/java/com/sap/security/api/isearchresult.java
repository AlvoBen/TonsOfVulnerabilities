package com.sap.security.api;

import java.util.Iterator;

/**
 * A read-only iterator that can return the size of the iteration
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public  interface ISearchResult extends Iterator {

/***
 * Constant used to define that the search was done successfully
 ***/
    public static final int SEARCH_RESULT_OK 			= 0x00;
/***
 * Constant used to define that the search result is not complete
 ***/
    public static final int SEARCH_RESULT_INCOMPLETE 	= 0x01;    
/***
 * Constant used to define that the search result is undefined
 ***/
    public static final int SEARCH_RESULT_UNDEFINED	 	= 0x02;        
/***
 * Constant used to define that a size limit exceeded occured
 ***/    
	public static final int SIZE_LIMIT_EXCEEDED 			= 0x04;
/***
 * Constant used to define that a time limit exceeded occured
 ***/    
	public static final int TIME_LIMIT_EXCEEDED 			= 0x08;

    /**
     * Returns the number of elements in the iteration.
     */
    public int size ();
    
    /**
     * Returns the status of the search result
     * @return int: returns constants of interface ISearchResult: 
     * {@link ISearchResult#SEARCH_RESULT_OK},
     * {@link ISearchResult#SEARCH_RESULT_INCOMPLETE},
     * {@link ISearchResult#SIZE_LIMIT_EXCEEDED},
     * {@link ISearchResult#TIME_LIMIT_EXCEEDED},
     * {@link ISearchResult#SEARCH_RESULT_UNDEFINED}
     */
    public int getState ();

    /**
     * Returns an iterator which contains the {@link IMessage} objects assigned to this {@link IPrincipal} or
     * <code>null</code> if no messages are assigned. If the method is called with parameter <code>true</code>, 
     * every subsequent call will return <code>null</code> if no new message was assigned to this {@link IPrincipal}.
     * @param clearPermanentMessages Specifies whether permanent messages with life time {@link IMessage#LIFETIME_PERMANENT} should be removed from the message buffer.
     * @return The messages assigned to this {@link IPrincipal} object or <code>null</code>.
     */
    public java.util.Iterator getMessages(boolean clearPermanentMessages);
    
}

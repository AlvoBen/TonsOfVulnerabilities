package com.sap.security.api;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Class PrincipalIterator is used to change the return type of 
 * iterators returned by this API (e.g. IUser.getRoles(...))
 * 
 * The following return types are available:<p>
 * <ul>
 * <li><pre>PrincipalIterator.ITERATOR_TYPE_PRINCIPALS</pre> (default) Returns IPrincipal objects</li>
 * <li><pre>PrincipalIterator.ITERATOR_TYPE_UNIQUEIDS_CHECKED</pre> Returns unique ids (String) of existing principals</li>
 * <li><pre>PrincipalIterator.ITERATOR_TYPE_UNIQUEIDS_NOT_CHECKED</pre> Returns unique ids (String) of principals without checking their existence</li>
 * </ul>
 * 
 * Example:<p>
 * <pre>Iterator it = user.getRoles(true);
 * PrincipalIterator pIterator = new PrincipalIterator(it, PrincipalIterator.ITERATOR_TYPE_PRINCIPALS);
 * while (pIterator.hasNext())
 * {
 * 	IPrincipal principal = (IPrincipal)pIterator.next();
 * }</pre>
 * 		
 */
public class PrincipalIterator implements java.util.Iterator
{
	protected static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/PrincipalIterator.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
			
	public static final int ITERATOR_TYPE_UNIQUEIDS_CHECKED 		= 0;	
	public static final int ITERATOR_TYPE_UNIQUEIDS_NOT_CHECKED 	= 1;
	public static final int ITERATOR_TYPE_PRINCIPALS 				= 2;
	
	protected AttributeList     mAttributeList;
	protected int 			    mType;	
	private Iterator 	mIterator;
	
	protected String mSearchCriteria;
	protected String mPrincipalType;
	protected int mSearchMode;
	protected boolean mCaseSensitivity; 
	
	protected PrincipalIterator()
	{
		//set the default
		mType = ITERATOR_TYPE_UNIQUEIDS_CHECKED;
	}

	/***
	 * Constructor of PrincipalIterator where the return type can be expicitely specified.
	 * 
	 * @param iterator the original iterator, which was returned by UME objects
	 * @param type the type which should be returned by the next()-method
	 ***/
	public PrincipalIterator(Iterator iterator, int type)
	{
		this(iterator, type, null);
	}

	/***
	 * Constructor of PrincipalIterator where the return type can be expicitely specified.
	 * 
	 * @param iterator the original iterator, which was returned by UME objects
	 * @param type the type which should be returned by the next()-method
	 * @param attributeList the attributes which are read and returned during the existence check
	 ***/
	public PrincipalIterator(Iterator iterator, int type, AttributeList attributeList)
	{
		if (iterator == null)
		{
			throw new IllegalArgumentException("Given iterator must not be null.");
		}
		if (iterator instanceof PrincipalIterator)
		{
			mIterator = iterator;	
			((PrincipalIterator)mIterator).mType = type;
			((PrincipalIterator)mIterator).mAttributeList = attributeList;
			((PrincipalIterator)mIterator).init();
		}
		else
		{
			throw new IllegalArgumentException("Only iterators returned by UME API are allowed.");
		}
	}
	
	/***
	 * Constructor of PrincipalIterator where the return type can be expicitely specified.
	 * 
	 * @param iterator the original iterator, which was returned by UME objects
	 * @param type the type which should be returned by the next()-method
	 * @param attributeList the attributes which are read and returned during the existence check
	 * @param searchCriteria simple search criteria to filter the elements
	 * @param principalType the principal types that should be returned
	 * @param mode the search mode defined in {@link com.sap.security.api.ISearchAttribute}
	 * @param caseSensitive The case sensitivity
	 ***/
	public PrincipalIterator(Iterator iterator, int type, AttributeList attributeList,String searchCriteria, String principalType, int mode, boolean caseSensitive)
	{
		this(iterator, type, attributeList);
		if (iterator instanceof PrincipalIterator)
		{
			((PrincipalIterator)mIterator).mSearchCriteria 	= searchCriteria;
			((PrincipalIterator)mIterator).mPrincipalType 	= principalType;
			((PrincipalIterator)mIterator).mSearchMode 		= mode;
			((PrincipalIterator)mIterator).mCaseSensitivity = caseSensitive;
			((PrincipalIterator)mIterator).init();
		}
		else
		{
			throw new IllegalArgumentException("Only iterators returned by UME API are allowed.");
		}
	}
	
	protected void init()
	{
		//nothing to do here
	}

	/***
	 * Constructor of PrincipalIterator.
	 * A PrincipalIterator which was instanciated by this constructor will return the default
	 * type (IPrincipal objects where the existence is checked).
	 * 
	 * @param iterator the original iterator, which was returned by UME objects
	 ***/
	public PrincipalIterator(Iterator iterator)
	{
		this(iterator, ITERATOR_TYPE_PRINCIPALS);
	}
	
	/***
	 * Checks whether there are more objects.
	 ***/
	public boolean hasNext() 
	{
		return mIterator.hasNext(); 
	}
	
	/***
	 * Returns the next object.
	 * @throws NoSuchElementException if there are no more objects to return.
	 ***/
	public Object next() 
	{
		return mIterator.next(); 
	}

	/***
	 * This method is not supported.
	 * @throws UnsupportedOperationException 
	 ***/
	public void remove() 
	{
		throw new UnsupportedOperationException("Removing elements is not supported.");
	}
}

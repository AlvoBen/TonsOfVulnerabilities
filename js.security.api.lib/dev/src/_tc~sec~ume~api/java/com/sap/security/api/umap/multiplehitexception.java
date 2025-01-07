package com.sap.security.api.umap;

import com.sap.security.api.umap.system.ISystemLandscapeObject;

/**
 * <p>This exception is thrown when a lookup for local users being mapped to
 *   a given backend user ID results in more than one hit.
 * </p>
 *
 * <p>See
 *   {@link IUserMapping#getInverseMappingData(String, ISystemLandscapeObject)}
 *   and {@link IUserMapping#getInverseMappingData(String, ISystemLandscapeObject)}.
 * </p>
 */
public class MultipleHitException extends NoLogonDataAvailableException
{
	private static final long serialVersionUID = -2783659265638409367L;
	
    String[] _userIds;

    /**
     * Create a new <code>MultipleHitException</code>.
     * 
     * @param userIds Array of unique IDs of the local users that are mapped to
     *                the backend user for which inverse user mapping was
     *                requested.
     */
	public MultipleHitException(String[] userIds)
    {
		super();
        _userIds = userIds;
	}

	/**
	 * Create a new <code>MultipleHitException</code> with an exception message.
     * 
	 * @param msg The Message containing additional information about the
     *        exception.
     * @param userIds Array of unique IDs of the local users that are mapped to
     *                the backend user for which inverse user mapping was
     *                requested.
	 */
	public MultipleHitException(String msg, String[] userIds)
    {
		super (msg);
        _userIds = userIds;
	}

    /**
     * Retrieve all local users that are mapped to the backend user for which
     * inverse user mapping was requested.
     * 
     * @return Array of unique IDs of the local users.
     */
    public String [] getUserNames ()
    {
        return _userIds;
    }
}

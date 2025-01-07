package com.sap.security.api.umap;

/**
 *  <p>This exception is thrown when an attempt is made to get the
 *    <code>IUserMapping</code> singleton when user mapping has not been
 *    properly initialized.
 *  </p>
 * 
 *  <p>See {@link com.sap.security.api.UMFactory#getUserMapping()}.
 *  </p>
 */
public class UserMappingNotAvailableException extends RuntimeException
{
	private static final long serialVersionUID = 3518169247542566598L;
	
    String [] _userids;

    /**
     * Constructor for UserMappingNotAvailableException.
     */
	public UserMappingNotAvailableException ()
    {
		super();
	}

	/**
	 * Constructor for UserMappingNotAvailableException.
	 * @param msg Exception message
	 */
	public UserMappingNotAvailableException (String msg)
    {
		super (msg);
	}
}

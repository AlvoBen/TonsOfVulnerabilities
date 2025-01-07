package com.sap.security.api;

/**
 * This exception is used, if the access to a already populated principal
 * fails. 
 * Example: You got a role object from the RoleFactory which doesn't
 *          have all available attributes populated.
 * 			If you access one of the not populated attributes, the role
 * 			tries to read it from the role's persistence adapter (lazy fetch).
 * 			If the role object on the persistence is not accessible for any
 *          reason (e.g. deleted in the meanwhile or backend down),
 * 			a PrincipalNotAccessibleException exception will be thrown.
 */
public class PrincipalNotAccessibleException extends UMRuntimeException 
{
	private static final long serialVersionUID = 3934167800376504577L;
	
	public PrincipalNotAccessibleException()
	{
		super();
	}

	public PrincipalNotAccessibleException(String message)
	{
		super(message);
	}

	public PrincipalNotAccessibleException(Throwable nestedException,String message)
	{
		super(nestedException, message);
	}

	public PrincipalNotAccessibleException(Throwable nestedException)
	{
		super(nestedException);
	}

}

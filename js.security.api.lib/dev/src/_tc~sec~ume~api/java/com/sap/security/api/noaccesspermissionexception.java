package com.sap.security.api;

/**
 * This exception is used, if the access to a already populated principal
 * fails because missing permission. 
 * Example: You got a role object from the RoleFactory where you do not have
 *          correct permissions for.
 */
public class NoAccessPermissionException extends PrincipalNotAccessibleException 
{
	private static final long serialVersionUID = -344227642091683711L;
	
	public static final String NO_ACCESS_PERMISSION_EXCEPTION_MESSAGE = "No access to principal due to missing permissions.";
	
	public NoAccessPermissionException()
	{
		super();
	}

	public NoAccessPermissionException(String message)
	{
		super(message);
	}

	public NoAccessPermissionException(Throwable nestedException,String message)
	{
		super(nestedException, message);
	}

	public NoAccessPermissionException(Throwable nestedException)
	{
		super(nestedException);
	}

}
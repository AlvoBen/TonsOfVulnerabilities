package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing role object in the
 * object store.
 */
public class NoSuchRoleException extends NoSuchPrincipalException
{
	private static final long serialVersionUID = -2726980123706261530L;
	
/**
 * Constructs a new NoSuchRoleException with a descriptive <code>message</code>.
 */
   public NoSuchRoleException(String id)
   {
      super(id);
   }
   
   public NoSuchRoleException(Throwable reason)
   {
   		super(reason);
   }
}

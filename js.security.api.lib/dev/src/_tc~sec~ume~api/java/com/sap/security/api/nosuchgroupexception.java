package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing group object in the
 * object store.
 *
 * @author  Andreas Sonntag
 * @created  19. April 2001
 */
public class NoSuchGroupException extends NoSuchPrincipalException
{
	private static final long serialVersionUID = -289688660812210219L;
   /**
    *  Constructor for the NoSuchGroupException object
    *
    * @param  id Description of exception
    */
   public NoSuchGroupException(String id)
   {
      super(id);
   }

   public NoSuchGroupException(Throwable reason)
   {
	  super(reason);
   }
   
   
}

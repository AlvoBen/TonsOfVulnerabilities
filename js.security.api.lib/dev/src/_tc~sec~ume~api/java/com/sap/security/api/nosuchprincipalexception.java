package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing principal in the
 * principal store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class NoSuchPrincipalException
    extends UMException
{

	private static final long serialVersionUID = 1388402708059456800L;
	
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/NoSuchPrincipalException.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
    /**
     * Constructs a new NoSuchPrincipalException.
     */
    public NoSuchPrincipalException ()
    {
     }

	public NoSuchPrincipalException (Throwable reason)
	{
		super(reason);
	 }

    /**
     * Constructs a new NoSuchPrincipalException with a descriptive <code>message</code>.
     */
    public NoSuchPrincipalException (String message)
    {
        super(message);
    }
    
	public NoSuchPrincipalException (Throwable reason,String message) {
		super(reason,message);
	}
    
}

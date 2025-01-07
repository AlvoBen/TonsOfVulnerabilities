package com.sap.security.api.umap.system;

/**
 * <p>This exception is thrown by different methods of
 *   {@link ISystemLandscapeWrapper} when a request to the underlying system
 *   landscape implementation fails.
 * </p>
 *
 * <p>The original exception thrown by the system landscape implementation can
 *   be retrieved by calling {@link #getImplementationException()}.
 * </p>
 */
public class ExceptionInImplementationException extends Exception {

	private static final long serialVersionUID = 1946875106948564420L;
	
    /**
     * Create an <code>ExceptionInImplementationException</code> without further
     * information.
     */
    public ExceptionInImplementationException()
    {
        super();
    }

    /**
     * Create an <code>ExceptionInImplementationException</code> with a message
     * string describing additional information about the original exception.
     * 
     * @param message The message text
     */
    public ExceptionInImplementationException(String message)
    {
        super(message);
    }
    
    /**
     * Create an <code>ExceptionInImplementationException</code> with a message
     * string and the original exception object.
     * 
     * @param message The message text
     * @param exception The original exception
     */
    public ExceptionInImplementationException(String message, Exception exception)
    {
        super(message, exception);
    }

    /**
     * Set the original exception thrown by the underlying system landscape
     * implementation.
     * 
     * @param exception The original exception
     */
    public void setImplementationException (Exception exception)
    {
        initCause(exception);
    }

    /**
     * Retrieve the original exception thrown by the underlying system landscape
     * implementation.
     * 
     * @return The original exception (may be <code>null</code>)
     */
    public Exception getImplementationException ()
    {
    	Throwable cause = getCause();
    	if(cause instanceof Exception) {
    		return (Exception) cause;
    	}

        return new Exception(cause);
    }

}

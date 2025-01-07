/*==============================================================================
    File:         CacheRuntimeException.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * The <code>CacheRuntimeException</code> class consitutes the base class for
 * all runtime exception occuring in the cache management infrastructure.
 * 
 * @author Michael Wintergerst
 * @version $Revision: #1 $
 */
public class CacheRuntimeException extends RuntimeException {
  
  static final long serialVersionUID = 1806878461676792595L;
  
    /**
     * The root cause causing this exception. This reference should be removed
     * as soon as we have implemented JDK 1.4 for the JTS environment. 
     */
    private Throwable rootCause;
    
    /**
     * Constructs an exception object with the specified localized message.
     * 
     * @param msg message text
     */
    public CacheRuntimeException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public CacheRuntimeException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public CacheRuntimeException(String msg, Throwable rootCause) {
        super(msg);
        
        this.rootCause = rootCause;
    }
    
    /**
     * Gets the root cause of this exception.
     * 
     * @return the root cause of this exception or <code>null</code> if 
     *         not specified
     */
    public Throwable getCause() {
        return rootCause;
    }
}
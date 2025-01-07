/*==============================================================================
    File:         CacheException.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * The <code>CacheException</code> class is the base class for all 
 * cache exceptions. 
 * <br>
 * This class allows only localized, hard-coded messages.
 *  
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public class CacheException extends Exception {
  
  static final long serialVersionUID = -2207852960527334722L;
  
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
    public CacheException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public CacheException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public CacheException(String msg, Throwable rootCause) {
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
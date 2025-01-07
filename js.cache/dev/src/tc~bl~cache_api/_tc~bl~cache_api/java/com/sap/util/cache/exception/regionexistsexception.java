/*==============================================================================
    File:         RegionExistsException.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * The <code>RegionExistsException</class> class indicates that a cache region
 * already exists.
 * 
 * @author Michael Wintergerst
 * @version $Revision: #1 $
 */
public class RegionExistsException extends CacheException {
  
  static final long serialVersionUID = -6354894751913120240L;
    
    /**
     * Constructs an exception object with the specified localized message.
     * 
     * @param msg message text
     */
    public RegionExistsException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public RegionExistsException(Throwable rootCause) {
        super(rootCause);
    }    
    
    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public RegionExistsException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
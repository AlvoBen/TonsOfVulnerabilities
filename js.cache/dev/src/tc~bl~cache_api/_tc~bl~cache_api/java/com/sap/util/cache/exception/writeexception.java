/*==============================================================================
    File:         WriteException.java       
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * The <code>WriteException</code> class constitutes an exception which can 
 * occur during a <code>put</code> operation.
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public class WriteException extends CacheException {
  
  static final long serialVersionUID = -946691821123751035L;
    
    /**
     * Constructs an exception object with the specified localized message.
     * 
     * @param msg message text
     */
    public WriteException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public WriteException(Throwable rootCause) {
        super(rootCause);
    }    
    
    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public WriteException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
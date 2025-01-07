/*==============================================================================
    File:         HolderRuntimeException.java       
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #2 $
    $Date: 2004/08/11 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * TODO
 *   
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #2 $
 */
public class HolderRuntimeException extends CacheRuntimeException {
  
  static final long serialVersionUID = -5371734789487456041L;
    
    /**
     * Constructs an exception object with the specified localized message.
     * 
     * @param msg message text
     */
    public HolderRuntimeException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public HolderRuntimeException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public HolderRuntimeException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
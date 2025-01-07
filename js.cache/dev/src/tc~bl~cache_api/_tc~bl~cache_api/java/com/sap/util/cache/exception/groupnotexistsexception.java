/*==============================================================================
    File:         GroupNotExistsException.java       
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * The <code>GroupNotExistsException</code> class indicates that 
 * a group does not exist. 
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public class GroupNotExistsException extends CacheException {

  static final long serialVersionUID = 4866964695267763303L;
  
    /**
     * Constructs an exception object with the specified localized message.
     * 
     * @param msg message text
     */
    public GroupNotExistsException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public GroupNotExistsException(Throwable rootCause) {
        super(rootCause);
    }    
    
    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public GroupNotExistsException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
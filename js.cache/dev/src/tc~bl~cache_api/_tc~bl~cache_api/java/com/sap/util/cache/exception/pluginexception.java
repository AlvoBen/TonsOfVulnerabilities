/*==============================================================================
    File:         PluginException.java       
    Created:      20.04.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.exception;

/**
 * The <code>PluginException</code> class constitutes an exception which can
 * occur within a plug-in module. 
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public class PluginException extends CacheException {
  
  static final long serialVersionUID = -942408133149506163L;
    
    /**
     * Constructs an exception object with the specified localized message.
     * 
     * @param msg message text
     */
    public PluginException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an exception object with a root cause.
     * 
     * @param rootCause throwable which caused this exception
     */
    public PluginException(Throwable rootCause) {
        super(rootCause);
    }    
    
    /**
     * Constructs an exception object with a message text and a root cause.
     * 
     * @param msg message text
     * @param rootCause throwable which caused this exception
     */
    public PluginException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
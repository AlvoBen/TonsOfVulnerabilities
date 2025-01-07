package com.sap.security.api;

/**
 * This interface is implemented by components of the user management which
 * need explicit configuration, e.g. user factories and authenticators.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public  interface IConfigurable {


/**
 * initialize a component of the user management with the given
 * @param properties Properties object.
 * @exception UMException if the initialization failed due to invalid
 *                        content of the <code>properties</code> object
 */
    public void initialize (java.util.Properties properties)
        throws UMException;

}

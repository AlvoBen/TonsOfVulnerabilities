package com.sap.security.api.session;

import javax.servlet.http.*;

/**
 * Single Sign On Session.
 *
 * But ISSOSession stands for a different concept. The purpose of HttpSession is
 * to create a context for the applications that remains valid throughout the
 * whole user session in a servlet engine. The duration of this validity is
 * usually configurable and can be customized in the deployment descriptor of
 * the J2EE engine (the default value for many J2EE engines is 30 minutes). The
 * idea behind ISSOSession is to wrap the Single Sign-On session of a user,
 * which is usually much longer than the HttpSession (in EP 5.0 it is 8 hours).
 * Beyond pure duration, the configuration of an http session is more influenced
 * by things like cache size and RAM size of the machine the VM is running on,
 * whereas configuration of SSO session depends more on security considerations.
 * Whenever the ISSOSession object times out in our implementation, the user
 * will be forced to relog on.
 *
 * @version 1.0
 * @author Guenther Wannenmacher
 * @deprecated Must not be used any longer.
 */
public interface ISSOSession extends HttpSession
{
    /**
     * Set multiple attributes for this session.
     */
    public void setAttributes(java.util.Map map);
}

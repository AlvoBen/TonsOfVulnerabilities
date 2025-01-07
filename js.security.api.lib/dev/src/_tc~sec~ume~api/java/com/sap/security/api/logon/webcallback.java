package com.sap.security.api.logon;

//import com.sap.security.core.InternalUMFactory;
//import com.sap.security.api.session.ISSOSession;

import javax.security.auth.callback.Callback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * JAAS Callback for Weblications. Use this one when a HTTPServletRequest 
 * or HTTPServletResponse is needed in the login module.
 * @version 1.0
 * @author Guenther Wannenmacher
 * @deprecated Must not be used any longer.
 */
public class WebCallback implements Callback {

    private HttpServletRequest  request;
    private HttpServletResponse response;
    private String              user;
    private char[]              password;

    /**
     * Creates a new WebCallback.
     */
    public WebCallback() {

    }

    /**
     * Gets the user to logon.
     * @return User or <code>null</code> if not available
     */
    public String getUser() {

        return user;
    }

    /**
     * Sets the request.
     * @param request the request
     */
    public void setRequest(HttpServletRequest request) {

        this.request = request;
    }

    /**
     * Gets the request.
     * @return request or <code>null</code> if not available
     */
    public HttpServletRequest getRequest() {

        return request;
    }

    /**
     * Sets the password for logon.
     * @param password the password of the user
     */
    public void setPassword(String password) {

        this.password = password.toCharArray();
    }

    /**
     * Sets the password for logon.
     * @param password the password of the user
     */
    public void setPassword(char[] password) {

        this.password = password;
    }

    /**
     * Get the password of the user.
     * @return password or <code>null</code> if not available
     */
    public char[] getPassword() {

        return password;
    }

    /**
     * Returns the response of the current callback.
     * @return response or <code>null</code> if not available
     */
    public HttpServletResponse getResponse() {

        return response;
    }

    /**
     * Sets the response for this callback.
     * @param response the response
     */
    public void setResponse(HttpServletResponse response) {

        this.response = response;
    }

    /**
     * Sets the user for this callback.
     * @param user the user
     */
    public void setUser(String user) {

        this.user = user;
    }
}

/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PreferencesValidator;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sap.engine.lib.security.http.HttpSecureSession;
import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.api.IPortletNode;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationComponents;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationConfig;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.security.api.IUser;
import com.sap.tc.logging.Location;

/**
 * The <code>PortletRequestImpl</code> class is an implementation of the <code>
 * PortletRequest</code> interface which defines the base interface to provide
 * client request information to a portlet. The portlet container uses two
 * specialized versions of this interface when invoking a portlet, ActionRequest
 * and RenderRequest. The portlet container creates these objects and passes
 * them as arguments to the portlet's processAction and render methods.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletRequestImpl extends HttpServletRequestWrapper implements PortletRequest {

  public static final String EXTRA_PARAMETER_PREFIX = "com.sap.engine.portletcontainer.";
  public static final String ATTRIBUTE_NAME_PREFIX = "com_sap_engine_portletcontainer_";
  public static final String PORTLET_SESSION_KEY = "com.sap.engine.portletcontainer.j_portlet_session";

  private Location currentLocation = Location.getLocation(getClass());
  private IPortletNode portletNode = null;

  private HttpServletResponse servletResponse= null;
  private PortletApplicationContext portletApplicationContext = null;
  private PortletApplicationConfig portletApplicationConfig = null;
  private String portletName = null;
  private PortletPreferences portletPreferences = null;
  private PortletSessionImpl portletSession = null;
  
  private Map parameters = new HashMap();

  /**
   * Creates new portlet request object by wrapping the passed servletRequest object.
   * 
   * @param servletRequest the servlet request object that contains the request 
   * the client has made.
   * @param servletResponse the servlet response object that contains the response 
   * the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified 
   * the requested portlet.
   * @param portletApplicationContext the <code>PortletApplicationContext</code> 
   * object that is used by the portlet to communicate with the Portlet Container.
   * @param requestType specifies the type of client request - action or render.
   */
  public PortletRequestImpl(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse, IPortletNode portletNode, 
      PortletApplicationContext portletApplicationContext, String requestType) {
    super(servletRequest);
    this.portletNode = portletNode;
    this.servletResponse = servletResponse;
    setParameters(portletNode, requestType);
    this.portletApplicationContext = portletApplicationContext;
    this.portletApplicationConfig = portletApplicationContext.getPortletApplicationConfig();
    portletName = portletNode.getPortletName();
    loadUserAttributes(servletRequest, portletNode);
    PreferencesValidator preferencesValidator = portletApplicationConfig.getPreferenceValidator(portletName);
    portletPreferences = new PortletPreferencesWrapper(portletNode.getPortletPreferences(), 
        preferencesValidator, portletNode.getMethod().equals(IPortletNode.ACTION_METHOD));
  }

  /**
   * Checks whether the given window state is valid to be set for this portlet 
   * in the context of the current request.
   * @param windowState window state to checked.
   * @return true, if it is valid for this portlet in this request to change to 
   * the given window state
   */
  public boolean isWindowStateAllowed(WindowState windowState) {
    // TODO Is it necessary to add additional check if the state is supported by the Portal?
    boolean result = false;
    if (windowState != null) {
      //check if the portal supports the state
      boolean isSupportedByPortal = false;
      Enumeration supportedStates = portletNode.getPortalContext().getSupportedWindowStates();
      while (supportedStates.hasMoreElements()) {
        if (((WindowState)supportedStates.nextElement()).toString().equalsIgnoreCase(windowState.toString())) {
          isSupportedByPortal = true;
        }
      }
      boolean isSupportedByPortlet = false;
      if (isSupportedByPortal) {
        // check if the portlet has declared the state as supported one (custom window states)
        if (windowState.equals(WindowState.NORMAL) || windowState.toString().equalsIgnoreCase("normal") ||
            windowState.equals(WindowState.MINIMIZED) || windowState.toString().equalsIgnoreCase("maximized") ||
            windowState.equals(WindowState.MINIMIZED) || windowState.toString().equalsIgnoreCase("minimized")) {
          isSupportedByPortlet = true;
        } else {
          Enumeration windowStates = portletApplicationContext.getPortletComponents().
            getAllWindowStates();
          while (windowStates.hasMoreElements()) {
            if (((String)windowStates.nextElement()).equalsIgnoreCase(windowState.toString())) {
              isSupportedByPortlet = true;
            }
          }
        }
      }
      //checks user permission to switch the state
      if (isSupportedByPortal && isSupportedByPortlet) {
        result = portletNode.isWindowStateAllowed(windowState);
      }
    }
    return result;
  }

  /**
   * Checks whether the given portlet mode is valid one to be set for this portlet 
   * in the context of the current request.
   * @param portletMode portlet mode to check.
   * @return true, if it is valid for this portlet in this request to change to the
   * given portlet mode.
   */
  public boolean isPortletModeAllowed(PortletMode portletMode) {
    boolean result = false;
    if (portletMode != null) {
      if (portletMode.equals(PortletMode.VIEW) || portletMode.toString().equalsIgnoreCase("view")) {
        // this mode must be always supported
        result = true;
      } else {
        //checks whether the Portal supports the Portlet Mode
        boolean isSupportedByPortal = false;
        Enumeration supportedModes = portletNode.getPortalContext().getSupportedPortletModes();
        while (supportedModes.hasMoreElements()) {
          if (((PortletMode)supportedModes.nextElement()).toString().equalsIgnoreCase(portletMode.toString())) {
            isSupportedByPortal = true;
          }
        }
        // checks whether the Portlet supports the Portlet Mode as well
        boolean isSupportedByPortlet = false;
        if (isSupportedByPortal) {
          //checks if the portlet mode is supported by the portlet
          Enumeration portletModes = portletApplicationContext.getPortletComponents().
          getAllPortletModes(portletNode.getPortletName());//TODO - result not used
          while (portletModes.hasMoreElements()) {
            if (((String)portletModes.nextElement()).equalsIgnoreCase(portletMode.toString())) {
              isSupportedByPortlet = true;
            }
          }
        }
        //checks user permission to switch the mode
        if (isSupportedByPortal && isSupportedByPortlet) {
          result = portletNode.isPortletModeAllowed(portletMode);
        }
      }
    }
    return result;
  }

  /**
   * Returns the current portlet mode of the portlet.
   * @return the portlet mode.
   */
  public PortletMode getPortletMode() {
    return portletNode.getPortletMode();
  }

  /**
   * Returns the current window state of the portlet.
   * @return the window state.
   */
  public WindowState getWindowState() {
    return portletNode.getWindowState();
  }

  /**
   * Returns the preferences object associated with the portlet.
   * @return the portlet preferences.
   */
  public PortletPreferences getPreferences() {
    return portletPreferences;
  }

  /**
   * Returns the current portlet session or, if there is no current session,
   * creates one and returns the new session.
   *  <p>
   * Creating a new portlet session will result in creating
   * a new <code>HttpSession</code> on which the portlet session is based on.
   * @return the portlet session.
   */
  public PortletSession getPortletSession() {
    PortletSession portletSession = getPortletSession(true);
    return portletSession;
  }

  /**
   * Returns the current portlet session or, if there is no current session
   * and the given flag is <CODE>true</CODE>, creates one and returns
   * the new session.
   * <P>
   * If the given flag is <CODE>false</CODE> and there is no current
   * portlet session, this method returns <CODE>null</CODE>.
   *  <p>
   * Creating a new portlet session will result in creating
   * a new <code>HttpSession</code> on which the portlet session is based on.
   * @param create
   *               <CODE>true</CODE> to create a new session, <BR>
   *               <CODE>false</CODE> to return <CODE>null</CODE> if there
   *               is no current session.
   * @return the portlet session.
   */
  public PortletSession getPortletSession(boolean create) {
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).traceDebug(
          "PortletRequestImpl.getPortletSession(create=[" + create + "]).");
    }
    //The only way to get the session for this application is to obtain it
    //during the service() for which getPortletSession should call dispach each time.
    //The request should already be dispatched to portlet web application - this is needed for PortletSession:
    //After dispatch finishes the request is no more dispatched
    
    HttpSession httpSession = getWrappedHttpServletRequest().getSession(create);

    if (httpSession != null) {
	    HttpSecureSession secureSession = (HttpSecureSession) httpSession;
	    synchronized (secureSession) {
        String portletWindowId = portletNode.getContextName();
        String secureSessionKey = PORTLET_SESSION_KEY + portletWindowId;
        //We store the portlet session as secure attribute of http session in order to be serialized together and with the windowId
        portletSession = (PortletSessionImpl) secureSession.getSecurityAttribute(secureSessionKey);
		    
		    if (portletSession == null) {
		      portletSession = new PortletSessionImpl(httpSession, portletNode.getPortletApplicationName(), portletWindowId);
		      secureSession.setSecurityAttribute(secureSessionKey, portletSession);
		      if (LogContext.getLocation(LogContext.LOCATION_REQUESTS).getLocation().beDebug()) {
			      LogContext.getLocation(LogContext.LOCATION_REQUESTS).traceDebug("Create new session; portletContextName=[" +
			          portletWindowId + "], portletName=[" + portletNode.getPortletName() + "], portletAppname=" +
			          portletNode.getPortletApplicationName() + "], getMethod=[" + portletNode.getMethod() + "]");
		      }
		    } else {
		      if (LogContext.getLocation(LogContext.LOCATION_REQUESTS).getLocation().beDebug()) {
			      LogContext.getLocation(LogContext.LOCATION_REQUESTS).traceDebug("Get existing session; portletContextName=[" +
			          portletWindowId + "], portletName=[" + portletNode.getPortletName() + "], portletAppname=" +
			          portletNode.getPortletApplicationName() + "], getMethod=[" + portletNode.getMethod() + "]");
		      }
		    }
	    }
    } else {
      if (currentLocation.beDebug()) {
        LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).traceWarning("ASJ.portlet.000047",
            "Cannot get the PortletSession because the request's HTTP session is NULL. PortletRequestImpl.getPortletSession(create=[{0}])", new Object[]{create}, null, null);
      }
    }

    return portletSession;
  }

  /**
   * Returns the value of the specified request property
   * as a <code>String</code>. If the request did not include a property
   * of the specified name, this method returns <code>null</code>.
   * <p>
   * A portlet can access portal/portlet-container specific properties 
   * through this method and, if available, the
   * headers of the HTTP client request.
   * <p>
   * This method should only be used if the 
   * property has only one value. If the property might have
   * more than one value, use {@link #getProperties}.
   * <p>
   * If this method is used with a multivalued
   * parameter, the value returned is equal to the first value
   * in the Enumeration returned by <code>getProperties</code>.
   *
   * @param name a <code>String</code> specifying the
   *				property name.
   *
   * @return a <code>String</code> containing the
   *				value of the requested property, or <code>null</code>
   *				if the request does not have a property of that name.
   *
   * @exception IllegalArgumentException if name is <code>null</code>.
   */	
  public String getProperty(String name) {
		if (name == null) {
		  LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000048", "Property name cannot be null.", null, null);
		  throw new IllegalArgumentException("Property name cannot be null.");
		}

		// get properties from request header
		String prop = this.getWrappedHttpServletRequest().getHeader(name);
    return prop;
  }

  /**
   * Returns all the values of the specified request property
   * as a <code>Enumeration</code> of <code>String</code> objects.
   * <p>
   * If the request did not include any propertys
   * of the specified name, this method returns an empty
   * <code>Enumeration</code>.
   * The property name is case insensitive. You can use
   * this method with any request property.
   *
   * @param name a <code>String</code> specifying the	property name.
   * @return a <code>Enumeration</code> containing the values of the requested property. If
   * the request does not have any properties of that name return an empty 
   * <code>Enumeration</code>.
   * @exception IllegalArgumentException if name is <code>null</code>.
   */		
  public Enumeration getProperties(String name) {
		if (name == null) {
		  LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000049", "Property name cannot be null.", null, null);		  
		  throw new IllegalArgumentException("Property name cannot be null.");
		}

		Set s = new HashSet();

		// get properties from request header
		Enumeration props = this.getWrappedHttpServletRequest().getHeaders(name);
		if (props != null) {
			while (props.hasMoreElements()) {
				s.add(props.nextElement());
			}
		}

		return Collections.enumeration(s);
  }

  /**
   * Returns a <code>Enumeration</code> of all the property names
   * this request contains. If the request has no properties, this method returns
   * an empty <code>Enumeration</code>.
   * @return an <code>Enumeration</code> of all the property names sent with this
   * request; if the request has no properties, an empty <code>Enumeration</code>.
   */
  public Enumeration getPropertyNames() {
    Set s = new HashSet();

		// get properties from request header
		Enumeration props = this.getWrappedHttpServletRequest().getHeaderNames();
		if (props != null) {
			while (props.hasMoreElements()) {
				s.add(props.nextElement());
			}
		}

    return Collections.enumeration(s);
  }

  /**
   * Returns the context of the calling portal.
   * @return the context of the calling portal.
   */
  public PortalContext getPortalContext() {
    return portletNode.getPortalContext();
  }

  /**
   * Returns the name of the authentication scheme used for the 
   * connection between client and portal, for example, <code>BASIC_AUTH</code>, 
   * <code>CLIENT_CERT_AUTH</code>, a custom one or <code>null</code> if there 
   * was no authentication.
   * @return one of the static members <code>BASIC_AUTH</code>,	<code>FORM_AUTH</code>, 
   * <code>CLIENT_CERT_AUTH</code>, <code>DIGEST_AUTH</code> (suitable for == comparison) 
   * indicating the authentication scheme, a custom one, or <code>null</code> 
   * if the request was	not authenticated.     
   */
  public String getAuthType() {
    return this.getWrappedHttpServletRequest().getAuthType();
  }

  /**
   * Returns the context path which is the path prefix associated with the deployed 
   * portlet application. If the portlet application is rooted at the
   * base of the web server URL namespace (also known as "default" context), 
   * this path must be an empty string. Otherwise, it must be the path the
   * portlet application is rooted to, the path must start with a '/' and 
   * it must not end with a '/' character.
   * <p>
   * To encode a URL the {@link PortletResponse#encodeURL} method must be used.
   *
   * @return a <code>String</code> specifying the	portion of the request URL that 
   * indicates the context of the request
   *
   * @see PortletResponse#encodeURL
   */
  public String getContextPath() {
    String result = "";
    if (portletApplicationContext.isDefault()) {
      result = "";
    } else {
      result = "/".concat(portletApplicationContext.getPortletAlias());
    }
    return result;
  }

  /**
   * Returns the login of the user making this request, if the user 
   * has been authenticated, or null if the user has not been authenticated.
   * @return a <code>String</code> specifying the login	of the user making this 
   * request, or <code>null</code> if the user login is not known.
   */
  public String getRemoteUser() {
    return this.getWrappedHttpServletRequest().getRemoteUser();
  }

  /**
   * Returns a java.security.Principal object containing the name of the 
   * current authenticated user.
   * @return a <code>java.security.Principal</code> containing
   * the name of the user making this request, or	<code>null</code> if the user 
   * has not been	authenticated.
   */
  public Principal getUserPrincipal() {
    return this.getWrappedHttpServletRequest().getUserPrincipal();
  }

  /**
   * Returns a boolean indicating whether the authenticated user is 
   * included in the specified logical "role".  Roles and role membership can be
   * defined using deployment descriptors.  If the user has not been
   * authenticated, the method returns <code>false</code>.
   * @param role a <code>String</code> specifying the name of the role.
   * @return a <code>boolean</code> indicating whether the user making this 
   * request belongs to a given role;	<code>false</code> if the user has not been 
   * authenticated.
   */
  public boolean isUserInRole(String role) {
    String currentPortletName = portletNode.getPortletName();
    PortletApplicationConfig appCfg = portletApplicationContext.getPortletApplicationConfig();
    //Get the application security role that the user may be mapped into:
    String linkedRole = appCfg.getLinkedSecurityRoleName(portletNode.getPortletName(), role);

    if (linkedRole == null) {
      //no such security-role-ref was found
      return false;
    }
    
    /*
     // already dispatched to the portlet web application
    PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
    IWebModuleContext ctx = pce.getPortletContainerExtensionContext().getWebModuleContext(portletNode.getPortletApplicationName());
    IRequestDispatcher disp = ctx.getRequestDispatcher(getWrappedHttpServletRequest(), servletResponse);
    //The only way to obtain isUserInRole is by dispatching each time it is called:
    PortletSecurityDispatchHandler handler = new PortletSecurityDispatchHandler(linkedRole);
    disp.dispatch(handler);
    */
    
    boolean result = getWrappedHttpServletRequest().isUserInRole(linkedRole);
    return result;
  }

  /**
   * Returns the value of the named attribute as an <code>Object</code>,
   * or <code>null</code> if no attribute of the given name exists. 
   * <p>
   * Attribute names should follow the same conventions as package
   * names. The specification reserves names matching <code>java.*</code>,
   * <code>javax.*</code> and <code>com.sun.*</code>. 
   * <p>
   * In a distributed portlet web application the <code>Object</code>
   * needs to be serializable.
   * @param name	a <code>String</code> specifying the name of the attribute.
   * @return an <code>Object</code> containing the value of the attribute, or 
   * <code>null</code> if	the attribute does not exist.
   * @exception IllegalArgumentException if name is <code>null</code>.
   */
  public Object getAttribute(String name) {
		if (name == null) {
		  LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000050", "Attribute name == null", null, null);
		  throw new IllegalArgumentException("Attribute name == null");
		}

		if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.sun.")) {
			return getWrappedHttpServletRequest().getAttribute(name);
		} else {
      // before remove encode the attr. name and remove encoded attr. name 
			return this.getWrappedHttpServletRequest().getAttribute(encodeName(portletNode.getContextName(), name));
		}
  }

  /**
   * Returns an <code>Enumeration</code> containing the names of the attributes 
   * available to this request. This method returns an empty <code>Enumeration</code>
   * if the request has no attributes available to it.
   * @return an <code>Enumeration</code> of strings containing the names 
   * of the request attributes, or an empty <code>Enumeration</code> if the request 
   * has no attributes available to it.
   */
  public Enumeration getAttributeNames() {
		Enumeration attributes = this.getWrappedHttpServletRequest().getAttributeNames();
		Vector attributesTmp = new Vector();
		while (attributes.hasMoreElements()) {
		  String attrName = (String)attributes.nextElement();
		  if (attrName.startsWith("java.") || attrName.startsWith("javax.") || attrName.startsWith("com.sun.")) {
		    attributesTmp.add(attrName);
		  } else {
		    String decodedName = decodeName(portletNode.getContextName(), attrName);
		    if (decodedName != null) {
		      attributesTmp.add(decodedName);
		    }
		  }
		}
    return attributesTmp.elements();
  }

  /**
   * Returns the value of a request parameter as a <code>String</code>,
   * or <code>null</code> if the parameter does not exist. Request parameters
   * are extra information sent with the request. The returned parameter 
   * are "x-www-form-urlencoded" decoded.
   * <p>
   * Only parameters targeted to the current portlet are accessible.
   * <p>
   * This method should only be used if the parameter has only one value. If the 
   * parameter might have more than one value, use {@link #getParameterValues}.
   * <p>
   * If this method is used with a multivalued parameter, the value returned is 
   * equal to the first value in the array returned by <code>getParameterValues</code>.
   * @param name 	a <code>String</code> specifying the name of the parameter.
   * @return a <code>String</code> representing the	single value of the parameter.
   *
   * @see 		#getParameterValues
   *
   * @exception  IllegalArgumentException if name is <code>null</code>.
   *
   */
  public String getParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Parameter name == null");
		}
    
    String [] values = (String[]) parameters.get(name);
		if (values != null) {
			return values[0];
		}
		return null;
  }

  /**
   * Returns an <code>Enumeration</code> of <code>String</code> objects containing 
   * the names of the parameters contained in this request. If the request has 
   * no parameters, the method returns an empty <code>Enumeration</code>. 
   * <p>
   * Only parameters targeted to the current portlet are returned.
   * @return an <code>Enumeration</code> of <code>String</code>	objects, each 
   * <code>String</code> containing	the name of a request parameter; or an 
   * empty <code>Enumeration</code> if the request has no parameters.
   */
  public Enumeration getParameterNames() {
		return Collections.enumeration(parameters.keySet());
  }

  /**
   * Returns an array of <code>String</code> objects containing all of the values 
   * the given request parameter has, or   * <code>null</code> if the parameter 
   * does not exist. The returned parameters are "x-www-form-urlencoded" decoded.
   * <p>
   * If the parameter has a single value, the array has a length of 1.
   * @param name	a <code>String</code> containing the name of the parameter the 
   * value of which is requested.
   * @return an array of <code>String</code> objects	containing the parameter values.
   *
   * @see		#getParameter
   *
   * @exception IllegalArgumentException if name is <code>null</code>.
   */  
  public String[] getParameterValues(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Parameter name == null");
		}

		String[] result = null;
		String[] values = (String[])parameters.get(name);
		if (values != null) {
			int length = values.length;
			result = new String[length];
			System.arraycopy(values, 0, result, 0, length);
		}
		return result;
  }

  /** 
   * Returns a <code>Map</code> of the parameters of this request.
   * Request parameters are extra information sent with the request.  
   * The returned parameters are "x-www-form-urlencoded" decoded.
   * <p>
   * The values in the returned <code>Map</code> are from type
   * String array (<code>String[]</code>).
   * <p>
   * If no parameters exist this method returns an empty <code>Map</code>.
   * @return an immutable <code>Map</code> containing parameter names as 
   * keys and parameter values as map values, or an empty <code>Map</code>
   * if no parameters exist. The keys in the parameter map are of type String. 
   * The values in the parameter map are of type String array (<code>String[]</code>).
   */
  public Map getParameterMap() {
    Map result = new HashMap(parameters);
    for (Iterator iter = result.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry)iter.next();
      if (!(entry.getKey() instanceof String)) {
        LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000051", "Parameter map keys must not be null and of type java.lang.String.", null, null);
        throw new IllegalArgumentException("Parameter map keys must not be null and of type java.lang.String.");
      }
      try {
        String [] entryValue = (String[]) entry.getValue();
        String [] value = null;
        if (entryValue != null) {
          int length = entryValue.length;
          value = new String[length];
          System.arraycopy(entryValue, 0, value, 0, length);
        }
        entry.setValue(value);
      } catch (ClassCastException ex) {
        LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000052", "Parameter map values must not be null and of type java.lang.String[].", null, null);
        throw new IllegalArgumentException("Parameter map values must not be null and of type java.lang.String[].");
       }
    }
    return result;
  }

  /**
   * Returns a boolean indicating whether this request was made 
   * using a secure channel between client and the portal, such as HTTPS.
   * @return  true, if the request was made using a secure channel.
   */
  public boolean isSecure() {
    return this.getWrappedHttpServletRequest().isSecure();
  }

  /**
   * Stores an attribute in this request.
   * <p>Attribute names should follow the same conventions as package names. 
   * Names beginning with <code>java.*</code>, <code>javax.*</code>, and 
   * <code>com.sun.*</code> are reserved for use by Sun Microsystems.
   * <br> If the value passed into this method is <code>null</code>, 
   * the effect is the same as calling {@link #removeAttribute}.
   * @param name a <code>String</code> specifying the name of the attribute.
   * @param object the <code>Object</code> to be stored.
   * @exception IllegalArgumentException if name is <code>null</code>.
   */
  public void setAttribute(String name, Object object) {
    if (name == null) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000053", "Attribute name cannot be null", null, null);
	  throw new IllegalArgumentException("Attribute name cannot be null");
    }

		if (object == null) {
			this.removeAttribute(name);
		} else if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.sun.")) {
		  //(API)Names beginning with java.*, javax.*, and com.sun.* are reserved for use by Sun Microsystems. 
			//Reserved names go directly in the underlying request
			getWrappedHttpServletRequest().setAttribute(name, object);
		} else {
		  // encode attribute name in Portlet namespace before setting
			this.getWrappedHttpServletRequest().setAttribute(encodeName(portletNode.getContextName(), name), object);
		}
  }

  /**
   * Removes an attribute from this request.  This method is not
   * generally needed, as attributes only persist as long as the request
   * is being handled.
   * <p>Attribute names should follow the same conventions as package names. 
   * Names beginning with <code>java.*</code>, <code>javax.*</code>, and 
   * <code>com.sun.*</code> are reserved for use by Sun Microsystems.
   * @param name a <code>String</code> specifying	the name of the attribute to 
   * be removed.
   * @exception IllegalArgumentException if name is <code>null</code>.
   */
  public void removeAttribute(String name) {
		if (name == null) {
		  LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000054", "Attribute name cannot be null", null, null);		  
		  throw new IllegalArgumentException("Attribute name cannot be null");
		}
		//(API) Names beginning with java.*, javax.*, and com.sun.* are reserved for use by Sun Microsystems. 
		if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.sun.")) {
			// Reserved names must be removed directly from the underlying request
			getWrappedHttpServletRequest().removeAttribute(name);
		} else {
      // before remove encode the attr. name and remove encoded attr. name 
			this.getWrappedHttpServletRequest().removeAttribute(encodeName(portletNode.getContextName(), name));
		}
  }

  /**
   * Returns the session ID indicated in the client request. This session ID may 
   * not be a valid one, it may be an old one that has expired or has been invalidated.
   * If the client request did not specify a session ID, this method returns 
   * <code>null</code>.
   * @return	a <code>String</code> specifying the session ID, or <code>null</code> 
   * if the request did not specify a session ID.
   * @see		#isRequestedSessionIdValid
   */
  public String getRequestedSessionId() {
    /*
    //Already dispatched to the portlet web application:
    PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
    IWebModuleContext ctx = pce.getPortletContainerExtensionContext().getWebModuleContext(portletNode.getPortletApplicationName());
    IRequestDispatcher disp = ctx.getRequestDispatcher(getWrappedHttpServletRequest(), servletResponse);
    PortletSessionIDDispatchHandler handler = new PortletSessionIDDispatchHandler();
    disp.dispatch(handler);
    //After dispatch finishes the request is no more dispatched
    String result = handler.getRequestedSessionId();
    */
    String result = getWrappedHttpServletRequest().getRequestedSessionId();
    return result;
  }

  /**
   * Checks whether the requested session ID is still valid.
   * @return <code>true</code> if this request has an id for a valid session
   * in the current session context; <code>false</code> otherwise.
   * @see			#getPortletSession
   */
  public boolean isRequestedSessionIdValid() {
    /*
    //Already dispatched to the portlet web application:
    PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
    IWebModuleContext ctx = pce.getPortletContainerExtensionContext().getWebModuleContext(portletNode.getPortletApplicationName());
    IRequestDispatcher disp = ctx.getRequestDispatcher(getWrappedHttpServletRequest(), servletResponse);
    PortletSessionIDDispatchHandler handler = new PortletSessionIDDispatchHandler();
    disp.dispatch(handler);
    //After dispatch finishes the request is no more dispatched
    boolean result = handler.isRequestedSessionIdValid();
     */
    boolean result = getWrappedHttpServletRequest().isRequestedSessionIdValid();
    return result;
  }

  /**
   * Returns the portal preferred content type for the response.
   * <p>
   * The content type only includes the MIME type, not the character set.
   * <p>
   * Only content types that the portlet has defined in its deployment descriptor 
   * are valid return values for this method call. If the portlet has defined
   * <code>'*'</code> or <code>'* / *'</code> as supported content types, these 
   * may also be valid return values.
   * @return preferred MIME type of the response.
   */
  public String getResponseContentType() {
    // TODO Returns the portal preferred content type for the response.(API)
    Enumeration allTypes = getResponseContentTypes();
    String result = null;
    if (allTypes.hasMoreElements()) {
      result = (String) allTypes.nextElement();
    }
    return result;
  }

  /**
   * Gets a list of content types which the portal accepts for the response.
   * This list is ordered with the most preferable types listed first.
   * <p>
   * The content type only includes the MIME type, not the character set.
   * <p>
   * Only content types that the portlet has defined in its deployment descriptor 
   * are valid return values for this method call. If the portlet has defined
   * <code>'*'</code> or <code>'* / *'</code> as supported content
   * types, these may also be valid return values.
   * @return ordered list of MIME types for the response.
   */
  public Enumeration getResponseContentTypes() {
    Vector v = new Vector();
    Map typesModesMap = portletApplicationConfig.getSupportedModesMap(portletName);
    PortletMode currentPortletMode = portletNode.getPortletMode();

    Enumeration types = portletApplicationConfig.getMimeTypes(portletName);

    while (types.hasMoreElements()) {
      String type = (String) types.nextElement();
      List modes = (List) typesModesMap.get(type);
      if (PortletMode.VIEW.equals(currentPortletMode)) {
        //the view mode is always supported even if not declared in <supports> in DD
        v.add(type);
      } else if (modes != null && modes.contains(currentPortletMode)) {
        //this type is allowed for the current portlet mode and vise versa
        v.add(type);
      }
    }

    return Collections.enumeration(v);
  }

  /**
   * Returns the preferred Locale in which the portal will accept content.
   * The Locale may be based on the Accept-Language header of the client.
   * @return  the prefered Locale in which the portal will accept content.
   */
  public Locale getLocale() {
    return this.getWrappedHttpServletRequest().getLocale();
  }

  /**
   * Returns an Enumeration of Locale objects indicating, in decreasing
   * order starting with the preferred locale in which the portal will
   * accept content for this request.
   * The Locales may be based on the Accept-Language header of the client.
   * @return  an Enumeration of Locales, in decreasing order, in which 
   * the portal will accept content for this request.
   */
  public Enumeration getLocales() {
    return this.getWrappedHttpServletRequest().getLocales();
  }

  /**
   * Returns the name of the scheme used to make this request.
   * For example, <code>http</code>, <code>https</code>, or <code>ftp</code>.
   * Different schemes have different rules for constructing URLs, as noted in RFC 1738.
   * @return a <code>String</code> containing the name of the scheme used to make 
   * this request.
   */
  public String getScheme() {
    return this.getWrappedHttpServletRequest().getScheme();
  }

  /**
   * Returns the host name of the server that received the request.
   * @return a <code>String</code> containing the name	of the server to which 
   * the request was sent.
   */
  public String getServerName() {
    return this.getWrappedHttpServletRequest().getServerName();
  }

  /**
   * Returns the port number on which this request was received.
   * @return an integer specifying the port number.
   */
  public int getServerPort() {
    return this.getWrappedHttpServletRequest().getServerPort();
  }

  /**
   * Returns the wrapped http servlet request object.
   * @return the wrapped request object.
   */
  public HttpServletRequest getWrappedHttpServletRequest() {
		return (HttpServletRequest)super.getRequest();
  }
  
  /**
   * Returns the <code>PortletConfig</code> object of the requested portlet.
   * @return the <code>PortletConfig</code> object of the requested portlet.
   */
  public PortletConfig getPortletConfig() {
    return portletApplicationContext.getPortletComponents().getPortletConfig(portletNode.getPortletName());
  }

  /**
   * Encodes the specified name.
   * @param portletId the ID of the portlet (<code>IPortletNode.getContextName()</code>).
   * @param name parameter to be encoded.
   * @return the encoded name as string.
   */
  private String encodeName(String portletId, String name){
    String encodedName = null;
    StringBuffer buffer = new StringBuffer(50);
    buffer.append(ATTRIBUTE_NAME_PREFIX);
    buffer.append(portletId);
    buffer.append('_');
    buffer.append(name);
    encodedName = buffer.toString();
    return encodedName; 
  }
  
  /**
   * Decodes the specified name.
   * @param portletId the ID of the portlet (<code>IPortletNode.getContextName()</code>).
   * @param name parameter to be decoded.
   * @return the decoded name as string.
   */
  private String decodeName(String portletId, String name) {
    String prefix = ATTRIBUTE_NAME_PREFIX + portletId + "_";
    String result = null;
    if (name.startsWith(prefix)) {
      result = name.substring(prefix.length());
    }
    return result;
  }
  
  /**
   * Exposes the supported by Portal/Portlet Container user attributes
   * to the portlets of the portlet application.
   *
   * Only the user attributes that are declared to be used by the portlet
   * application can be accessed.
   *
   * If the request is done in the context of an un-authenticated user,
   * calls to the getAttribute method of the request using the USER_INFO
   * constant must return null.
   *
   * If the portlet request already contains a map of user attributes, this
   * map is ignored and replaced with the map generated by the portlet container.
   *
   * @param servletRequest the servlet request object that contains the request
   * the client has made.
   * @param portletNode the <code>IPortletNode</code> object that specified
   * the requested portlet.
   */
  private void loadUserAttributes(HttpServletRequest servletRequest, IPortletNode portletNode) {
    IUser user = portletNode.getUser();
    Set userAttributesNames = portletApplicationContext.getPortletApplicationConfig().getUserAttributes();
    Map previous = (Map) servletRequest.getAttribute(PortletRequest.USER_INFO);
    if (user == null) {
      LogContext.getLocation(LogContext.LOCATION_REQUESTS).trace(
          "User info not available in the portlet node for context name [" +
          portletNode.getContextName() + "] in application [" +
          portletApplicationContext.getPortletApplicationName() + "]!");
    } else {
      if (previous != null) {
        //Log message
        LogContext.getLocation(LogContext.LOCATION_REQUESTS).trace(
            "Will not expose user attributes because they are already set: [" +
            previous.keySet().toString() + "] for " +
            "context name [" + portletNode.getContextName() + "] in application [" +
            portletApplicationContext.getPortletApplicationName() + "]!");
      }
      PortletApplicationComponents comp = portletApplicationContext.getPortletComponents();
      UserAttributesMap attributesMap = comp.getUserAttribtuesMap();
      Map newAttributes = attributesMap.getAttributes(user, userAttributesNames);
      servletRequest.setAttribute(PortletRequest.USER_INFO, newAttributes);
    }
  }//end of loadUserAttributes

  /**
   * Sets the parameters for this <code>PortletRequest</code> request.
   * @param portletNode the <code>IPortletNode</code> object that specified 
   * the requested portlet.
   * @param requestType specifies the type of client request - action or render.
   */
  private void setParameters(IPortletNode portletNode, String requestType) {
    if (requestType.equals(IPortletNode.RENDER_METHOD)) {
      Enumeration parameterNames = portletNode.getParameterNames();
      if (parameterNames != null) {
        while (parameterNames.hasMoreElements()) {
          String parameterName = (String)parameterNames.nextElement();
          String [] parameterValues = portletNode.getParameterValues(parameterName);
          parameters.put(parameterName, parameterValues);
        }
      }
    } else {
      Map wrappedParameters = this.getWrappedHttpServletRequest().getParameterMap(); 
      Map newParameters = new HashMap(wrappedParameters);
      Vector extraParameters = new Vector();
      Iterator iterator = newParameters.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        String[] value = (String[])entry.getValue();
        String key = (String)entry.getKey();
        int length = value.length;
        String[] newValue = new String[length];
        if (key.startsWith(EXTRA_PARAMETER_PREFIX)) {
          extraParameters.add(key);
        }
        System.arraycopy(value, 0, newValue, 0, length);
        entry.setValue(newValue);
      }

      parameters = newParameters;
      // extra parameters must be invisible to the portlets
      Enumeration keys = extraParameters.elements();
      while (keys.hasMoreElements()) {
        parameters.remove(keys.nextElement());
      }

    }
  }
  
  /**
   * @return Returns the portletNode.
   */
  public IPortletNode getPortletNode() {
    return portletNode;
  }  
}

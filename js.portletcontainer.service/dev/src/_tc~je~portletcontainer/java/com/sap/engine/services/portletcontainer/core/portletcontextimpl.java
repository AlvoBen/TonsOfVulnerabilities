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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import com.sap.engine.services.portletcontainer.ServiceContext;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationConfig;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.engine.services.portletcontainer.core.dispatch.PortletRequestDispatcherImpl;

/**
 * The <code>PortletContextImpl</code> class is an implementation of the <code>
 * PortletContext</code>interface which defines a portlet view of the 
 * portlet container. The PortletContextImpl also makes resources available to 
 * the portlet. Using the context, a portlet can access the portlet log, 
 * and obtain URL references to resources. 
 *
 * There is one context per "portlet application" per Java Virtual Machine. 
 * (A "portlet application" is a collection of portlets, servlets, and content
 * installed under a specific subset of the server URL namespace, 
 * such as /catalog. They are possibly installed via a .war file.) As a web 
 * application, a portlet application also has a servlet context. The portlet 
 * context leverages most of its functionality from the servlet context of 
 * the portlet application. 
 * 
 * Attibutes stored in the context are global for all users and all components
 * in the portlet application. 
 *
 * In the case of a web application marked "distributed" in its deployment 
 * descriptor, there will be one context instance for each virtual machine. In 
 * this situation, the context cannot be used as a location to share global 
 * information (because the information is not truly global). Use an external
 * resource, such as a database to achieve sharing on a global scope. 
 * 
 * A portlet application is an extended web application. As a web application, 
 * a portlet application also has a servlet context. The portlet context 
 * leverages most of its functionality from the servlet context of the portlet 
 * application.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletContextImpl implements javax.portlet.PortletContext {

  private PortletApplicationConfig portletApplicationConfig = null;
  private PortletApplicationContext portletAppCtx = null;
  private javax.servlet.ServletContext servletContext = null;
  
  /**
   * Creates new <code>PortletContextImpl</code> object on top of the passed 
   * <code>ServletContext</code>. The portlet context leverages most of its
   * functionality from the servlet context of the portlet application.
   * @param servletContext the servlet context of the portlet application
   * @param portletApplicationConfig
   * @param portletAppCtx
   */
  public PortletContextImpl(ServletContext servletContext, 
    PortletApplicationConfig portletApplicationConfig, PortletApplicationContext portletAppCtx) {
    this.servletContext = servletContext;
    this.portletApplicationConfig = portletApplicationConfig;
    this.portletAppCtx = portletAppCtx;
  }
  
  /**
   * Returns the name and version of the portlet container in which the
   * portlet is running.
   * <P>
   * The form of the returned string is <code>containername/versionnumber</code>.
   * @return   the string containing at least name and version number.
   */
  public String getServerInfo() {
    return ServiceContext.getServiceContext().getFullServerVersion();
  }

  /**
   * Returns a {@link PortletRequestDispatcher} object that acts
   * as a wrapper for the resource located at the given path.
   * A <code>PortletRequestDispatcher</code> object can be used include the
   * resource in a response. The resource can be dynamic or static.
   * <p>The pathname must begin with a slash (<code> / </code>) and is interpreted 
   * as relative to the current context root.
   * <p>This method returns <code>null</code> if the <code>PortletContext</code>
   * cannot return a <code>PortletRequestDispatcher</code> for any reason.
   * @param path a <code>String</code> specifying the pathname to the resource.
   * @return a <code>PortletRequestDispatcher</code> object that acts as a wrapper 
   * for the resource at the specified path.
   * @see PortletRequestDispatcher
   */
  public PortletRequestDispatcher getRequestDispatcher(String path) {
    RequestDispatcher rd = servletContext.getRequestDispatcher(path);
    if ( rd != null ) {
      return new PortletRequestDispatcherImpl(rd, portletAppCtx);
    } else {
      return null;
    }
  }

  /**
   * Returns a {@link PortletRequestDispatcher} object that acts
   * as a wrapper for the named servlet.
   * <p>Servlets (and also JSP pages) may be given names via server 
   * administration or via a web application deployment descriptor.
   * <p>This method returns <code>null</code> if the <code>PortletContext</code> 
   * cannot return a <code>PortletRequestDispatcher</code> for any reason.
   * @param name 	a <code>String</code> specifying the name of a servlet to be 
   * wrapped.
   * @return a <code>PortletRequestDispatcher</code> object	that acts as a 
   * wrapper for the named servlet.
   * @see 		PortletRequestDispatcher
   */
  public PortletRequestDispatcher getNamedDispatcher(String name) {
    RequestDispatcher rd = servletContext.getNamedDispatcher(name);
    if ( rd!=null ) {
      return new PortletRequestDispatcherImpl(rd, portletAppCtx);
    } else {
      return null ;
    }
  }

  /**
   * Returns the resource located at the given path as an InputStream object.
   * The data in the InputStream can be of any type or length. The method returns 
   * null if no resource exists at the given path.
   * <p>
   * In order to access protected resources the path has to be prefixed with 
   * <code>/WEB-INF/</code> (for example <code>/WEB-INF/myportlet/myportlet.jsp</code>). 
   * Otherwise, the direct path is used (for example <code>/myportlet/myportlet.jsp</code>).
   * @param path     the path to the resource.
   * @return    the input stream.
   */
  public InputStream getResourceAsStream(String path) {
    return servletContext.getResourceAsStream(path);
  }

  /**
   * Returns the major version of the Portlet API that this portlet
   * container supports.
   *
   * @return  1 i.e. implements Version 1.0
   */
  public int getMajorVersion() {
    return 1;
  }

  /**
   * Returns the minor version of the Portlet API that this portlet
   * container supports.
   *
   * @return  0 i.e. implements Version 1.0
   */
  public int getMinorVersion() {
    return 0;
  }

  /**
   * Returns the MIME type of the specified file, or <code>null</code> if 
   * the MIME type is not known. The MIME type is determined
   * by the configuration of the portlet container and may be specified
   * in a web application deployment descriptor. Common MIME
   * types are <code>text/html</code> and <code>image/gif</code>.
   * @param file a <code>String</code> specifying the name of a file.
   * @return a <code>String</code> specifying the MIME type of the file.
   */
  public String getMimeType(String file) {
    return servletContext.getMimeType(file);
  }

  /**
   * Returns a <code>String</code> containing the real path 
   * for a given virtual path. For example, the path <code>/index.html</code>
   * returns the absolute file path of the portlet container file system.
   * @param path 	a <code>String</code> specifying a virtual path.
   * @return a <code>String</code> specifying the real path, or null if the 
   * transformation cannot be performed.
   */
  public String getRealPath(String path) {
    return servletContext.getRealPath(path);
  }

  /**
   * Returns a directory-like listing of all the paths to resources within 
   * the web application longest sub-path of which matches the supplied path 
   * argument. Paths indicating subdirectory paths 
   * end with a slash (<code>/</code>). The returned paths are all 
   * relative to the root of the web application and have a leading slash. 
   * @param path the partial path used to match the resources, which must start 
   * with a slash
   * @return a Set containing the directory listing, or <code>null</code> if 
   * there are no resources in the web application of which the path begins with 
   * the supplied path.
   */
  public Set getResourcePaths(String path) {
    return servletContext.getResourcePaths(path);
  }

  /**
   * Returns a URL to the resource that is mapped to a specified
   * path. The path must begin with a slash (<code>/</code>) and is interpreted
   * as relative to the current context root.
   * <p>This method allows the portlet container to make a resource available 
   * to portlets from any source. Resources can be located on a local or remote
   * file system, in a database, or in a <code>.war</code> file. 
   * <p>This method returns <code>null</code> if no resource is mapped to the 
   * pathname.
   * <p>The resource content is returned directly, so be aware that 
   * requesting a <code>.jsp</code> page returns the JSP source code.
   * Use a <code>RequestDispatcher</code> instead to include results of 
   * an execution.
   * @param path a <code>String</code> specifying	the path to the resource.
   * @return the resource located at the named path, or <code>null</code> if 
   * there is no resource at that path.
   * @exception MalformedURLException if the pathname is not given in 
   * the correct form.
   *
   */
  public URL getResource(String path) throws MalformedURLException {
    return servletContext.getResource(path);
  }

  /**
   * Returns the portlet container attribute with the given name, 
   * or null if there is no attribute by that name. An attribute allows a portlet 
   * container to give the portlet additional information not already provided 
   * by this interface. A list of supported attributes can be retrieved using
   * <code>getAttributeNames</code>.
   * <p>The attribute is returned as a <code>java.lang.Object</code>
   * or some subclass.
   * Attribute names should follow the same convention as package
   * names. The Java Portlet API specification reserves names
   * matching <code>java.*</code>, <code>javax.*</code>,
   * and <code>sun.*</code>.
   * @param name 	a <code>String</code> specifying the name	of the attribute.
   * @return 		an <code>Object</code> containing the value	of the attribute, 
   * or <code>null</code>	if no attribute exists matching the given	name.
   * @exception	IllegalArgumentException if name is <code>null</code>.
   */
  public Object getAttribute(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Attribute name is null.");
    }
    return servletContext.getAttribute(name);
  }

  /**
   * Returns an <code>Enumeration</code> containing the attribute names 
   * available within this portlet context, or an emtpy
   * <code>Enumeration</code> if no attributes are available. Use the
   * {@link #getAttribute} method with an attribute name
   * to get the value of an attribute.
   * @return 		an <code>Enumeration</code> of attribute names.
   */
  public Enumeration getAttributeNames() {
    return servletContext.getAttributeNames();
  }

  /**
   * Returns a String containing the value of the named context-wide initialization 
   * parameter, or <code>null</code> if the parameter does not exist.
   * This method provides configuration information which may be useful for 
   * an entire "portlet application".
   * @param	name	a <code>String</code> containing the name of the requested parameter. 
   * @return 		a <code>String</code> containing the value of the initialization 
   * parameter, or <code>null</code> if the parameter does not exist.
   * @exception	IllegalArgumentException if name is <code>null</code>.
   */
  public String getInitParameter(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Parameter name is null.");
    }
    return servletContext.getInitParameter(name);
  }

  /**
   * Returns the names of the context initialization parameters as an 
   * <code>Enumeration</code> of String objects, or an empty Enumeration 
   * if the context has no initialization parameters.
   * @return an <code>Enumeration</code> of <code>String</code> objects containing 
   * the names of the context initialization parameters.
   */  
  public Enumeration getInitParameterNames() {
    return servletContext.getInitParameterNames();
  }

  /**
   * Writes the specified message to a portlet log file, usually an event log.
   * The name and type of the portlet log file is specific to the portlet container.
   * <p>
   * This method mapps to the <code>ServletContext.log</code> method.
   * The portlet container may in addition log this message in a
   * portlet container specific log file.
   * @param msg 	a <code>String</code> specifying the message to be written 
   * to the log file.
   */
  public void log(String msg) {
    servletContext.log(msg);
  }

  /**
   * Writes an explanatory message and a stack trace for a given Throwable exception
   * to the portlet log file. The name and type of the portlet log file is 
   * specific to the portlet container, usually an event log.
   * <p>
   * This method is mapped to the <code>ServletContext.log</code> method.
   * The portlet container may in addition log this message in a
   * portlet container specific log file.
   * @param msg	a <code>String</code> that describes the error or exception.
   * @param throwable the <code>Throwable</code> error or exception.
   */
  public void log(String msg, Throwable throwable) {
    servletContext.log(msg, throwable);
  }

  /**
   * Removes the attribute with the given name from the portlet context.
   * After removal, subsequent calls to {@link #getAttribute} to retrieve the 
   * attribute's value will return <code>null</code>.
   * @param name	a <code>String</code> specifying the name	of the attribute 
   * to be removed
   * @exception	IllegalArgumentException if name is <code>null</code>.
   */
  public void removeAttribute(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Attribute name is null.");
    }
    servletContext.removeAttribute(name);
  }

  /**
   * Binds an object to a given attribute name in this portlet context.
   * If the name specified is already used for an attribute, this method 
   * removes the old attribute and binds the name to the new attribute.
   * <p>
   * If a null value is passed, the effect is the same as calling 
   * <code>removeAttribute()</code>.
   * <p>Attribute names should follow the same convention as package
   * names. The Java Portlet API specification reserves names
   * matching <code>java.*</code>, <code>javax.*</code>, and
   * <code>sun.*</code>.
   * @param name 	a <code>String</code> specifying the name	of the attribute.
   * @param object 	an <code>Object</code> representing the	attribute to be bound.
   * @exception	IllegalArgumentException if name is <code>null</code>.
   */
  public void setAttribute(String name, Object object) {
    if (name == null) {
      throw new IllegalArgumentException("Attribute name is null.");
    }
    servletContext.setAttribute(name, object);
  }

  /**
   * Returns the name of this portlet application correponding to this 
   * PortletContext as specified in the <code>web.xml</code> deployment 
   * descriptor for this web application by the <code>display-name</code> element.
   * @return  The name of the web application or null if no name has been declared in the deployment descriptor.
   */
  public String getPortletContextName() {
    /* 
     * Returns the name of this portlet application corresponding to this PortletContext 
     * as specified in the web.xml deployment descriptor for this web application by 
     * the display-name element.
     */
    return portletAppCtx.getDisplayName();
  }
  
  
}

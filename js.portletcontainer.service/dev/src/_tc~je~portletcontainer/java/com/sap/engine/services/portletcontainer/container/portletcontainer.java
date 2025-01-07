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
package com.sap.engine.services.portletcontainer.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.UnavailableException;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.ServiceContext;
import com.sap.engine.services.portletcontainer.api.IPortletContainer;
import com.sap.engine.services.portletcontainer.api.IPortletNode;
import com.sap.engine.services.portletcontainer.container.request.ActionDispatchHandler;
import com.sap.engine.services.portletcontainer.container.request.RenderDispatchHandler;
import com.sap.engine.services.portletcontainer.core.ActionRequestImpl;
import com.sap.engine.services.portletcontainer.core.ActionResponseContext;
import com.sap.engine.services.portletcontainer.core.ActionResponseImpl;
import com.sap.engine.services.portletcontainer.core.RenderRequestImpl;
import com.sap.engine.services.portletcontainer.core.RenderResponseImpl;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationConfig;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionDeploymentException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModuleContext;
import com.sap.engine.services.servlets_jsp.webcontainer_api.request.IRequestDispatcher;
import com.sap.tc.logging.Location;

/**
 * The <code>PortletContainer</code> class is the entry point for the Portlet
 * Container. It implements the <code>IPortletContainer</code> interface to
 * allow the Portal application to invoke portlets.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletContainer implements IPortletContainer {

  protected static final String TRANSPORT_GUARANTEE_NONE = "NONE";
  protected static final String TRANSPORT_GUARANTEE_INTEGRAL = "INTEGRAL";
  protected static final String TRANSPORT_GUARANTEE_CONFIDENTIAL = "CONFIDENTIAL";
  protected static final String HTTPS_SCHEME = "https";
  
  private Location currentLocation = Location.getLocation(getClass());
  private ServiceContext serviceContext = null;

  /**
   * Creates a new <code>PortletContainer</code> object.
   * @param serviceContext the <code>ServiceContext</code> object that holds information
   * for the Portlet Container service
   */
  public PortletContainer(ServiceContext serviceContext) {
    this.serviceContext = serviceContext;
  }

  /**
   * Invokes the <code>processAction</code> method of the portlet specified in the passed
   * <code>IPortletNode</code> object. The Portlet Container creates the <code>ActionRequest</code>
   * and the <code>ActionResponse</code> objects using the passed HttpServletRequest and
   * HttpServletResponse objects. Sets the request parameters and invokes the <code>processAction</code>
   * method of the portlet defined by the <code>portletNode</code> object.
   *
   * @param request the servlet request object that contains the request the client has made.
   * @param response the servlet response object that contains the response the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified the portlet to be invoked.
   * @throws IOException - if the streaming causes an I/O problem.
   * @throws PortletException - if the portlet has problems fulfilling the request.
   * @throws UnavailableException - if the portlet is unavailable to process the action at this time.
   * @throws PortletSecurityException - if the portlet cannot fullfill this request because of security reasons.
   */
  public void processAction(HttpServletRequest request, HttpServletResponse response,
    IPortletNode portletNode) throws IOException, PortletException {    
    
    PortletApplicationContext portletApplicationContext = null;

    portletApplicationContext = getContext(portletNode.getPortletApplicationName());
    if (portletApplicationContext == null || portletApplicationContext.isDestroying()) {
      String applicationName = portletNode.getPortletApplicationName();
      // handle application not available
      throw new UnavailableException("Portlet " + applicationName + "is not available");
    }
    
    checkSecurityConstraint(request, portletNode, portletApplicationContext);

    portletApplicationContext.addRequest();
    Thread currentThread = Thread.currentThread();
    ClassLoader threadLoader = null;
    String portletName = portletNode.getPortletName();
    Portlet portlet = null;
    ActionRequestImpl actionRequest = null;
    ActionResponseImpl actionResponse = null;
    ActionResponseContext actionResponseContext = null;
    String location = null;
    try {
      portletNode.setRenderParameters(new HashMap());
      actionRequest = new ActionRequestImpl(request, response, portletNode, portletApplicationContext);
      actionResponse = new ActionResponseImpl(actionRequest, response, portletNode);
      threadLoader = currentThread.getContextClassLoader();
      currentThread.setContextClassLoader(portletApplicationContext.getClassLoader());
      portlet = portletApplicationContext.getPortletComponents().getPortlet(portletName);
      //Dispatch request to the portlet web application:
      PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
      IWebModuleContext ctx = pce.getPortletContainerExtensionContext().getWebModuleContext(portletNode.getPortletApplicationName());
      IRequestDispatcher disp = ctx.getRequestDispatcher(actionRequest, actionResponse);
      ActionDispatchHandler handler = new ActionDispatchHandler(portlet);
      disp.dispatch(handler);
      
      if (handler.getPortletException() != null) {
        throw handler.getPortletException();
      }
      
      if (handler.getIOException() != null) {
        throw handler.getIOException();
      }
            
      actionResponseContext = (ActionResponseContext)actionResponse;
      setActionResponseChanges(portletNode, response, actionResponseContext);
    } catch (ThreadDeath tde) {
      throw tde;
    } catch (OutOfMemoryError o) {
      throw o;
    } catch (PortletException pe) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000006", "Cannot complete the action request. The requested portlet is [{0}].", new Object[]{portletName}, pe, null, null);    
      if (pe instanceof UnavailableException) {
        portletApplicationContext.getPortletComponents().setPortletUnavailable(portlet, portletName, (UnavailableException)pe);
      }
      throw pe;
    } catch (IOException ioe) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000007", "Cannot complete the action request. The requested portlet is [{0}].", new Object[]{portletName}, ioe, null, null);   
      throw ioe;
    } catch (WebContainerExtensionDeploymentException wcede) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000008", "Cannot complete the action request. The requested portlet is [{0}].", new Object[]{portletName}, wcede, null, null);
      throw new PortletException(wcede);
    } finally {
      currentThread.setContextClassLoader(threadLoader);
    }
    
    portletApplicationContext.removeRequest();
  }
 
  /**
   * Invoked after the end of processAction method to make changes made to the
   * ActionResponse permanent. 
   * @param portletNode the <code>IPortletNode</code> object that specified the 
   * invoked portlet.
   * @param servletResponse the servlet response object that contains the response
   * the portlet sends to the client.
   * @param actionResponseContext the context used to get the changes to the 
   * ActionResponse made during the processAction method.
   */
  private void setActionResponseChanges(IPortletNode portletNode, 
    HttpServletResponse servletResponse, ActionResponseContext actionResponseContext) 
    throws IOException {
      String location = actionResponseContext.getRedirectLocation();
      if (location != null) {
        HttpServletResponse redirectResponse = servletResponse;
        while (redirectResponse instanceof HttpServletResponseWrapper) {
          redirectResponse = (HttpServletResponse)
            ((HttpServletResponseWrapper)redirectResponse).getResponse();
        }
        redirectResponse.sendRedirect(location);      
      }
      
      Map renderParameters = actionResponseContext.getRenderParameters();
      portletNode.setRenderParameters(renderParameters);
      
      PortletMode mode = actionResponseContext.getChangedPortletMode();
      if (mode != null) {
        portletNode.setPortletMode(mode);
      }
      
      WindowState state = actionResponseContext.getChangedWindowState();
      if(state != null) {
        portletNode.setWindowState(state);
      }
  }

  /**
   * Invokes the <code>render</code> method of the portlet specified in the given <code>IPortletNode</code>
   * object. The Portlet Container creates the <code>RenderRequest</code> and the
   * <code>RenderResponse</code> objects using the passed HttpServletRequest and
   * HttpServletResponse objects. Sets the render request parameters and invokes the <code>render</code>
   * method of the portlet defined by the <code>portletNode</code> object.
   * 
   * @param request the servlet request object that contains the request the client has made.
   * @param response the servlet response object that contains the response the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified the portlet to be invoked.
   * @throws IOException - if the streaming causes an I/O problem.
   * @throws PortletException - if the portlet has problems fulfilling the rendering request.
   * @throws PortletSecurityException - if the portlet cannot fullfill this request because of security reasons.
   * @throws UnavailableException - if the portlet is unavailable to perform render at this time.
   */
  public void render(HttpServletRequest request, HttpServletResponse response,
    IPortletNode portletNode) throws IOException, PortletException {
    PortletApplicationContext portletApplicationContext = null;
    portletApplicationContext = getContext(portletNode.getPortletApplicationName());
    if (portletApplicationContext == null || portletApplicationContext.isDestroying()) {
      String applicationName = portletNode.getPortletApplicationName();
      // handle application not available
      throw new UnavailableException("Portlet " + applicationName + "is not available");
    }
    
    checkSecurityConstraint(request, portletNode, portletApplicationContext);

    portletApplicationContext.addRequest();
    Thread currentThread = Thread.currentThread();
    ClassLoader threadLoader = null;
    String portletName = portletNode.getPortletName();
    Portlet portlet = null;
    RenderRequestImpl renderRequest = null;
    RenderResponseImpl renderResponse = null;
    try {
      renderRequest = new RenderRequestImpl(request, response, portletNode, portletApplicationContext);
      renderResponse = new RenderResponseImpl(portletNode, renderRequest, response);
      threadLoader = currentThread.getContextClassLoader();
      currentThread.setContextClassLoader(portletApplicationContext.getClassLoader());
      portlet = portletApplicationContext.getPortletComponents().getPortlet(portletName);
      portletNode.setExpirationCache(portletApplicationContext.getPortletApplicationConfig().getExpirationCache(portletName));
      //Dispatch request to the portlet web application:
      PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
      IWebModuleContext ctx = pce.getPortletContainerExtensionContext().getWebModuleContext(portletNode.getPortletApplicationName());
      IRequestDispatcher disp = ctx.getRequestDispatcher(renderRequest, renderResponse);
      RenderDispatchHandler handler = new RenderDispatchHandler(portlet);
      disp.dispatch(handler);
      
      if (handler.getPortletException() != null) {
        throw handler.getPortletException();
      }
      
      if (handler.getIOException() != null) {
        throw handler.getIOException();
      }      

    } catch (ThreadDeath tde) {
      throw tde;
    } catch (OutOfMemoryError o) {
      throw o;
    } catch (PortletException pe) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000009", "Cannot complete the render request. The requested portlet is [{0}].", new Object[]{portletName}, pe, null, null);
      if (pe instanceof UnavailableException) {
        portletApplicationContext.getPortletComponents().setPortletUnavailable(portlet, portletName, (UnavailableException)pe);
      }
      throw pe;
    } catch (IOException ioe) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000010", "Cannot complete the render request. The requested portlet is [{0}].", new Object[]{portletName}, ioe, null, null);
      throw ioe;
    } catch (WebContainerExtensionDeploymentException wcede) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000011", "Cannot complete the action request. The requested portlet is [{0}].", new Object[]{portletName}, wcede, null, null);       
      throw new PortletException(wcede);
    } finally {
      currentThread.setContextClassLoader(threadLoader);
    }
    portletApplicationContext.removeRequest();
  }

  // TODO private methods

  /**
   * @param portletApplicationName
   * @return
   */
  private PortletApplicationContext getContext(String portletApplicationName) throws PortletException {
    PortletApplicationContext portletApplicationContext = null;
    PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
    if (portletApplicationName != null) {
      
      portletApplicationContext = pce.getPortletApplicationContext(portletApplicationName);
      if (portletApplicationContext != null) {
        return portletApplicationContext;
      } else {
        try {
          IWebModuleContext ctx = pce.getPortletContainerExtensionContext().getWebModuleContext(portletApplicationName);
          portletApplicationContext = pce.getPortletApplicationContext(portletApplicationName);
        } catch (WebContainerExtensionDeploymentException wcede) {
          LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000012",  "Cannot complete the action request. The requested portlet is [{0}].", new Object[]{portletApplicationName}, wcede, null, null);
          throw new PortletException(wcede);
        }
      }
    }
    return portletApplicationContext;
  }

  /**
   * Makes security check for the given request and portlet.
   * @param request
   * @param portletNode
   * @param portletApplicationContext
   * @throws PortletSecurityException thrown if constraint is INTEGRAL or CONFIDENTIAL and not HTTPS scheme is used
   */
  private void checkSecurityConstraint(HttpServletRequest request, IPortletNode portletNode,
                                                PortletApplicationContext portletApplicationContext) 
                                      throws PortletSecurityException, PortletException {
    
    try {
  		String portletName = portletNode.getPortletName();
  		PortletApplicationConfig pac = portletApplicationContext.getPortletApplicationConfig();
  		List constraints = pac.getTransportGuarantee(portletName);
  		String scheme = request.getScheme().trim().toLowerCase();
      
      /* 
       * The combination of user-data-constraints that apply to a common urlpattern
       * and http-method shall yield the union of connection types accepted by
       * the individual constraints as acceptable connection types. (Servlet 2.4 - SRV.12.8.1)
       */
  		
      
  		if ( !HTTPS_SCHEME.equals(scheme) &&
          (constraints.contains(TRANSPORT_GUARANTEE_CONFIDENTIAL) || 
            constraints.contains(TRANSPORT_GUARANTEE_INTEGRAL)) && !constraints.contains(TRANSPORT_GUARANTEE_NONE)) {
        //TODO: add localizable message
  		  String exceptionMessage = "Security check failed for portlet [" +
            portletName + "] in module [" + portletApplicationContext.getPortletModuleName() +
            "] in application [" + portletApplicationContext.getPortletApplicationName() + "]:" +
            " The current request scheme [" + scheme + "] does not match the declared security constraint " +
            constraints + ".";
        LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000013",
        		"Security check failed for portlet [{0}] in module [{1}] in application [{2}]:" +
                " The current request scheme [{3}] does not match the declared security constraint {4}.", 
                new Object[]{portletName, portletApplicationContext.getPortletModuleName(), portletApplicationContext.getPortletApplicationName(), scheme, constraints}, null, null);
        throw new PortletSecurityException(exceptionMessage);
  		}
  	} catch (RuntimeException e) {
      String exceptionMessage = "Unable to perform the security check for portlet [" +
            (portletNode != null ? portletNode.getPortletName() : "") + 
            "] in module [" + (portletApplicationContext != null ? portletApplicationContext.getPortletModuleName() : "") +
            "] in application [" + (portletApplicationContext != null ? 
                portletApplicationContext.getPortletApplicationName() : "") + "].";
      String portletName = (portletNode != null ? portletNode.getPortletName() : "");
      String portletModuleName = (portletApplicationContext != null ? portletApplicationContext.getPortletModuleName() : "");
      String portletApplicationName = (portletApplicationContext != null ? portletApplicationContext.getPortletApplicationName() : "");
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000014", 
    		  "Unable to perform the security check for portlet [{0}] in module [{1}] in application [{2}].", new Object[]{portletName, portletModuleName, portletApplicationName}, e, null, null);
      throw new PortletException(exceptionMessage, e);
  	}
  }
}
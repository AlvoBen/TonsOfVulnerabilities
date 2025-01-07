/*
 * Copyright (c) 2005-2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.services.portletcontainer.container.PortletContainerExtension;
import com.sap.engine.services.portletcontainer.container.PortletContainerInterfaceImpl;
import com.sap.engine.services.servlets_jsp.webcontainer_api.IWebContainerProvider;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionDeploymentException;
import com.sap.tc.logging.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * This class accomplishes the interaction between the Portlet Container and
 * server and resources on the server. Service is started and stopped and registered
 * from Application-ServiceContext through this class.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletContainerServiceFrame extends ContainerEventListenerAdapter implements ApplicationServiceFrame {

  public static final String WEB_DEPLOYMENT_DESCRIPTOR_NAME = "web.xml";
  public static final String PORTLET_CONTAINER_DESCRIPTOR_NAME = "portlet.xml";
  public static String PORTLET_CONTAINER_NAME = "Portlet";

  private Location currentLocation = Location.getLocation(getClass());
  private ServiceContext serviceContext = null;
  private Set interfaceNames = null;
  private IWebContainerProvider webContainerInterface = null;
  private PortletContainerExtension portletContainerExtension = null;
  private PortletContainerInterface runtimeInterface = null;

  private static final int MASK = MASK_INTERFACE_AVAILABLE | MASK_INTERFACE_NOT_AVAILABLE;
  private static final String WEB_CONTAINER_API = "tc~je~webcontainer~api";


  /**
   * Implements the same method from the <code>ApplicationServiceFrame</code> interface.
   * This method is called when the Portlet Container Service is going to be started.
   * Registers the Portlet Container service as a listener for events from the WCE.
   * Registers the Portlet Container service runtime interface -
   * <code>PortletContainerInterface</code>.
   *
   * @param applicationServiceContext
   * @throws ServiceException if the Web Container service cannot be started.
   * @see com.sap.engine.frame.ApplicationServiceFrame#start(com.sap.engine.frame.ApplicationServiceContext)
   */
  public void start(ApplicationServiceContext applicationServiceContext) throws ServiceException {
    // start time of the service
    long time = System.currentTimeMillis();

    try {
      LogContext.init();

      long newtime = System.currentTimeMillis();
      LogContext.getLocation(LogContext.LOCATION_SERVICE).traceDebug("initLogging() >>> " + (newtime - time));

      new com.sap.engine.services.portletcontainer.exceptions.PortletResourceAccessor().init(Location.getLocation("com.sap.engine.services.PortletContainer"));

      long newtime1 = System.currentTimeMillis();
      LogContext.getLocation(LogContext.LOCATION_SERVICE).traceDebug("initPortletResourceAccessor >>> " + (newtime1 - newtime));

      serviceContext = new ServiceContext(applicationServiceContext);

      newtime = System.currentTimeMillis();
      LogContext.getLocation(LogContext.LOCATION_SERVICE).traceDebug("init ServiceContext >>> " + (newtime - newtime1));

      webContainerInterface = (IWebContainerProvider)applicationServiceContext.getContainerContext().getObjectRegistry().getProvidedInterface(WEB_CONTAINER_API);
      if (webContainerInterface == null) {
        LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000071", "IWebContainerProvider interface is null!", null, null);
      } else {
        portletContainerExtension = new PortletContainerExtension();
        serviceContext.setPortletContainerExteinsion(portletContainerExtension);
        webContainerInterface.registerWebContainerExtension(portletContainerExtension, PORTLET_CONTAINER_NAME, PORTLET_CONTAINER_DESCRIPTOR_NAME);
      }

      newtime1 = System.currentTimeMillis();
      LogContext.getLocation(LogContext.LOCATION_SERVICE).traceDebug("webContainerInterface.registerWebContainerExtension() >>> " + (newtime1 - newtime));

      applicationServiceContext.getContainerContext().getObjectRegistry().registerInterface(getRuntimeInterface());

      newtime = System.currentTimeMillis();
      LogContext.getLocation(LogContext.LOCATION_SERVICE).traceDebug("applicationServiceContext.getServiceState().registerInterface() >>> " + (newtime - newtime1));

    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      LogContext.getCategory(LogContext.CATEGORY_SERVICE).logFatal(currentLocation, "ASJ.portlet.000072", "Service Web Container Extension cannot start!", e, null, null);
      stop();
      throw new ServiceException(e);
    }

    LogContext.getLocation(LogContext.LOCATION_SERVICE).traceDebug("Whole time for Portlet Container service start >>> " + (System.currentTimeMillis() - time));
  }

  /**
   * Implements the same method from the <code>ServiceFrame<code> interface.
   * This method is called when the Portlet Container service is going to be stopped.
   * Unregisters the Portlet Container service as a listener for events from the WCE.
   *
   * @throws ServiceRuntimeException
   * @see com.sap.engine.frame.ServiceFrame#stop()
   */
  public void stop() throws ServiceRuntimeException {
    if (webContainerInterface != null) {
      webContainerInterface.unregisterWebContainerExtension(PORTLET_CONTAINER_NAME);
      webContainerInterface = null;
    }
    portletContainerExtension = null;
    serviceContext.setPortletContainerExteinsion(null);
    serviceContext.getApplicationServiceContext().getContainerContext().getObjectRegistry().unregisterInterface();
  }

  /**
   * Returns the Portlet Container runtime interface.
   *
   * @return the Portlet Container runtime interface.
   */
  private PortletContainerInterface getRuntimeInterface() {
    if (runtimeInterface == null) {
      try {
        runtimeInterface = new PortletContainerInterfaceImpl(serviceContext);
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        LogContext.getCategory(LogContext.CATEGORY_SERVICE).logFatal(currentLocation, "ASJ.portlet.000073", "Can't get the runtime interface of the portlet service.", e, null, null);
      }
    }
    return runtimeInterface;
  }

}

/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
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
import java.io.InputStream;
import java.util.Vector;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.PortletContainerServiceFrame;
import com.sap.engine.services.portletcontainer.exceptions.WCEDeploymentException;
import com.sap.engine.services.portletcontainer.exceptions.WCEWarningException;
import com.sap.engine.services.portletcontainer.container.descriptor.PortletDeploymentDescriptor;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationDestroyer;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationInitializer;
import com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerLifecycle;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionDeploymentException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionWarningException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IModuleDescriptor;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModule;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModuleContext;
import com.sap.tc.logging.Location;

/**
 * The <code>PortletContainerLifecycleHandler</code> class implements the
 * <code>IWebContainerLifecycle</code> interface to allow portlet to handle
 * life-cycle events from the Web Container.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletContainerLifecycleHandler implements IWebContainerLifecycle {
  private Location currentLocation = Location.getLocation(getClass());
  private PortletContainerExtension portletContainerExtension = null;
  private long destroyTimeout = 5000;

  /**
   * The constructor used for creating the <code>PortletContainerDeployHandler</code>
   * object.
   * @param portletExtension
   */
  public PortletContainerLifecycleHandler(PortletContainerExtension portletExtension) {
    this.portletContainerExtension = portletExtension;
  }//end of constructor

  /**
   * The method is called at the end of IWebContainer start() method on each server node.
   *
   * @param webModuleContext
   * @throws WebContainerExtensionDeploymentException
   *          throwing WebContainerExtensionDeploymentException means
   *          that the start of the application fails, it will remain in STOPPED state.
   * @throws WebContainerExtensionWarningException
   */
  public void onStart(IWebModuleContext webModuleContext) throws WebContainerExtensionDeploymentException,
    WebContainerExtensionWarningException {

    Vector warnings = new Vector();

    PortletApplicationContext portletApplicationContext = null;

    IWebModule webModule = webModuleContext.getWebModule();
    if (webModule == null) {
      throw new WCEDeploymentException(WCEDeploymentException.CANNOT_LOAD_WEB_MODULE);
    }

    String portletModuleName = getPortletModuleName(webModuleContext);

    try {
      //create portlet application context
      portletApplicationContext = createContext(webModuleContext);
      //init portlet application components
      Vector initPortletAppWarnings = initPortletAppComponents(portletApplicationContext, webModuleContext);

      if (initPortletAppWarnings != null && initPortletAppWarnings.size() != 0) {
        warnings.addAll(initPortletAppWarnings);
      }

      //store portlet application context
      portletContainerExtension.portletApplicationStarted(portletModuleName, portletApplicationContext);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      LogContext.getCategory(LogContext.CATEGORY_DEPLOY).logError(currentLocation, "ASJ.portlet.000017", "Error in starting portlet application [{0}/{1}].", new Object[]{getPortletApplicationName(webModuleContext), portletModuleName}, e, null, null);
      if (portletApplicationContext != null) {
        destroyPortletAppComponents(portletApplicationContext, webModuleContext);
      }

      throw new WCEDeploymentException(WCEDeploymentException.ERROR_IN_STARTING_PORTLET_APPLICATION, new Object[]{portletModuleName}, e);
    }

    makeWarningException(warnings);
  }

  /**
   * This method is called by the Web Container when web application is stopped.
   * It destroys all loaded portlet components.
   * @param webModuleContext
   *
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerLifecycle#onStop(com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModuleContext)
   */
  public void onStop(IWebModuleContext webModuleContext) throws WebContainerExtensionWarningException {
    String portletModuleName = getPortletModuleName(webModuleContext);

    PortletApplicationContext portletApplicationContext = portletContainerExtension.getPortletApplicationContext(portletModuleName);
    if (portletApplicationContext == null) {
      LogContext.getCategory(LogContext.CATEGORY_DEPLOY).logWarning(currentLocation, "ASJ.portlet.000018", "Cannot stop portlet application [{0}/{1}]. Such application does not exist or is not started.", new Object[]{getPortletApplicationName(webModuleContext), portletModuleName}, null, null);      
      return;
    }

    //Stop portlet application
    destroyPortletAppComponents(portletApplicationContext, webModuleContext);
    portletApplicationContext.destroy();
    //remove portlet from list with stored applications
    portletContainerExtension.removeStartedApplication(portletModuleName);
  }

  /**
   * Creates portlet application's context.
   *
   * @param webModuleContext
   * @return portlet application context
   * @throws WebContainerExtensionDeploymentException
   *
   */
  private PortletApplicationContext createContext(IWebModuleContext webModuleContext) throws
    WebContainerExtensionDeploymentException {
    PortletApplicationContext portletApplicationContext = null;
    portletApplicationContext = new PortletApplicationContext(getPortletModuleName(webModuleContext), webModuleContext);
    portletApplicationContext.init(getPortletDD(webModuleContext)); //here should be passed Security and Session context
    return portletApplicationContext;
  }

  /**
   * Creates the <code>PortletDeploymentDescriptor</code> object that is the representation
   * of the portlet application deployment descriptor.
   *
   * @param webModuleContext
   * @return the loaded portlet deployment descriptor.
   * @throws WebContainerExtensionDeploymentException
   *
   */
  private PortletDeploymentDescriptor getPortletDD(IWebModuleContext webModuleContext) throws
    WebContainerExtensionDeploymentException {

    PortletDeploymentDescriptor portletDD = new PortletDeploymentDescriptor();
    IModuleDescriptor moduleDescriptor = webModuleContext.getWebModule().getDescriptor(PortletContainerServiceFrame.PORTLET_CONTAINER_DESCRIPTOR_NAME);
    String portletModuleName = getPortletModuleName(webModuleContext);
    InputStream inputStream = null;
    try {
      inputStream = moduleDescriptor.getInputStream();
      portletDD.loadDescriptorFromStream(inputStream, false);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000019", "Error in loading portlet.xml file of the portlet application [{0}/{1}].", new Object[]{getPortletApplicationName(webModuleContext), portletModuleName}, e, null, null);
      throw new WCEDeploymentException(WCEDeploymentException.ERROR_LOADING_PORTLET_XML, new Object[]{portletModuleName}, e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException io) {
          LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000020", "Cannot close portlet.xml file for application [{0}/{1}].", new Object[]{getPortletApplicationName(webModuleContext), portletModuleName}, io, null, null);
        }
      }
    }
    return portletDD;
  }

  /**
   * Returns the portlet module name.
   * @param webModuleContext
   * @return the name of the Portlet Module
   */
  private String getPortletModuleName(IWebModuleContext webModuleContext) {
    return webModuleContext.getWebModule().getModuleName();
  }

  /**
   * Returns the portlet application context.
   * @param webModuleContext
   * @return the portlet application context.
   */
  private String getPortletApplicationName(IWebModuleContext webModuleContext) {
    return webModuleContext.getWebModule().getApplicationName();
  }

  /**
   * Initializes the portlet application components.
   * @param portletApplicationContext the portlet application context.
   * @return the list with warnings.
   */
  private Vector initPortletAppComponents(PortletApplicationContext portletApplicationContext, IWebModuleContext webModuleContext) throws WebContainerExtensionDeploymentException {
    String portletModuleName = portletApplicationContext.getPortletModuleName();
    String portletApplicationName = portletApplicationContext.getPortletApplicationName();

    PortletApplicationInitializer initializer = new PortletApplicationInitializer(portletApplicationContext);

    webModuleContext.getRequestDispatcher().dispatch(initializer);

    if (initializer.getException() != null) {
      LogContext.getCategory(LogContext.CATEGORY_DEPLOY).logError(currentLocation, "ASJ.portlet.000021", "Error while starting portlet application [{0}/{1}].", new Object[]{portletApplicationName, portletModuleName}, initializer.getException(), null, null);
      throw new WCEDeploymentException(WCEDeploymentException.ERROR_IN_STARTING_PORTLET_APPLICATION, new Object[]{portletModuleName}, initializer.getException());
    }
    return initializer.getWarnings();
  }

  /**
   * Destroys the portlet application components when the portlet application is
   * stopped.
   * @param portletApplicationContext
   */
  private void destroyPortletAppComponents(PortletApplicationContext portletApplicationContext, IWebModuleContext webModuleContext) {
    String portletModuleName = portletApplicationContext.getPortletModuleName();
    String portletApplicationName = portletApplicationContext.getPortletApplicationName();

    portletApplicationContext.setDestroyingMode(true);

    if (portletApplicationContext.getAllCurrentRequests() > 0) {
      synchronized (portletApplicationContext.getSynchObject()) {
        long startTime = System.currentTimeMillis();
        long delta = 0;
        //while (portletApplicationContext.getAllCurrentRequests() > 0 && delta < ServiceContext.getServiceContext().getPortletContainerProperties().getDestroyTimeout()) {
        while (portletApplicationContext.getAllCurrentRequests() > 0 && delta < destroyTimeout) {
          try {
            //portletApplicationContext.getSynchObject().wait(ServiceContext.getServiceContext().getPortletContainerProperties().getDestroyTimeout());
            portletApplicationContext.getSynchObject().wait(destroyTimeout);
          } catch (OutOfMemoryError e) {
            throw e;
          } catch (ThreadDeath e) {
            throw e;
          } catch (Throwable e) {
            LogContext.getCategory(LogContext.CATEGORY_DEPLOY).logError(currentLocation, "ASJ.portlet.000022", "Thread interrupted while waiting the destroy time out [{0}/{1}].", new Object[]{portletApplicationName, portletModuleName}, e, null, null);
          }
          delta = System.currentTimeMillis() - startTime;
        }
      }
    }

    PortletApplicationDestroyer destroyer = new PortletApplicationDestroyer(portletApplicationContext);
    webModuleContext.getRequestDispatcher().dispatch(destroyer);
  }

  /**
   * Create the warning exceptions.
   * @param warnings
   */
  private void makeWarningException(Vector warnings) throws WebContainerExtensionWarningException {
    if (warnings != null && warnings.size() != 0) {
      WCEWarningException warningExc = new WCEWarningException(WCEWarningException.WARNINGS_DURING_STARTING_PORTLET_APPLICATION);
      for (int i = 0; i < warnings.size(); i++) {
        warningExc.addWarning(warnings.elementAt(i).toString());
      }
      throw warningExc;
    }
  }

}

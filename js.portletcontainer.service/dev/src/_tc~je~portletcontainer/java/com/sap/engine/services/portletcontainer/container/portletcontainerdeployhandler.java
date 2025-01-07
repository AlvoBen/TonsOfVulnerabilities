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

import com.sap.engine.services.servlets_jsp.webcontainer_api.container.DeployInfo;
import com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerDeploy;
import com.sap.engine.services.servlets_jsp.webcontainer_api.container.ReferenceObjectImpl;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionDeploymentException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionWarningException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModule;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IModuleDescriptor;
import com.sap.engine.services.portletcontainer.container.descriptor.PortletDeploymentDescriptor;
import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.PortletContainerServiceFrame;
import com.sap.engine.services.portletcontainer.exceptions.WCEDeploymentException;
import com.sap.tc.logging.Location;

import java.io.InputStream;
import java.io.IOException;

/**
 * The <coe>PortletContainerDeployHandler</code> class implements the <code>
 * IWebContainerDeploy</code> interface to allow portlet container to handle 
 * deploy events from the Web Container.
 * 
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletContainerDeployHandler implements IWebContainerDeploy {
  private Location currentLocation = Location.getLocation(getClass());

  /**
   * The constructor used for creating the <code>PortletContainerDeployHandler</code> 
   * object. 
   */
  public PortletContainerDeployHandler() {
  }//end of constructor

  /**
   * Called when a new web module is deployed with the web container. Returns files' 
   * names which will be loaded with the ApplicationLoader during startup of the 
   * application.
   * @param webModule the web module loaded by the web container.
   * @param rootDirectory the "root" directory where .war file is extracted.
   *
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerDeploy#onDeploy(com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModule, String)
   */
  public DeployInfo onDeploy(IWebModule webModule, String rootDirectory) throws WebContainerExtensionDeploymentException {
    DeployInfo deployInfo = new DeployInfo();

    // Parse portlet.xml in order to validated portlet deployment descriptor.
    PortletDeploymentDescriptor portletDD = new PortletDeploymentDescriptor();
    IModuleDescriptor moduleDescriptor = webModule.getDescriptor(PortletContainerServiceFrame.PORTLET_CONTAINER_DESCRIPTOR_NAME);
    InputStream inputStream = null;
    try {
      inputStream = moduleDescriptor.getInputStream();
      portletDD.loadDescriptorFromStream(inputStream, true);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000015", "Error in loading portlet.xml file of the portlet application [{0}].", new Object[]{webModule.getModuleName()}, e, null, null);    
      throw new WCEDeploymentException(WCEDeploymentException.ERROR_LOADING_PORTLET_XML, new Object[]{webModule.getModuleName()}, e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException io) {
          LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000016", "Cannot close portlet.xml file for application [{0}].", new Object[]{webModule.getModuleName()}, io, null, null);          
        }
      }
    }

    ReferenceObjectImpl ref1 = new ReferenceObjectImpl("tc~je~portlet~plb", "library", "weak");
    ReferenceObjectImpl ref2 = new ReferenceObjectImpl("tc~je~portlet_taglib~plb", "library", "weak");
    ReferenceObjectImpl[] refs = new ReferenceObjectImpl[]{ref1, ref2};
    deployInfo.setPublicReferences(refs);

    return deployInfo;
  }//end of onDeploy(IWebModule webModule)

  /**
   * Called when web application is removed from web container.
   * @param webModule web module that is removed.
   * @exception WebContainerExtensionDeploymentException
   * 
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerDeploy#onRemove(com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModule)
   */
  public void onRemove(IWebModule webModule) throws WebContainerExtensionDeploymentException,
    WebContainerExtensionWarningException {
  }//end of onRemove(IWebModule webModule)

}//end of class

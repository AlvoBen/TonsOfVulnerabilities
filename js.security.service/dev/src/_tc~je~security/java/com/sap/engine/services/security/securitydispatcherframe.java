/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security;

import java.net.URL;
import java.security.Security;

import com.sap.engine.system.SystemURLStreamHandlerFactory;
import com.sap.engine.frame.CommunicationServiceFrame;
import com.sap.engine.frame.CommunicationServiceContext;
import com.sap.engine.frame.ServiceException;
import iaik.security.provider.IAIK;

/**
 *  Security service frame for the dispatcher. It registers IAIK provider if available.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class SecurityDispatcherFrame implements CommunicationServiceFrame {

  /**
   * @see com.sap.engine.frame.CommunicationServiceFrame
   */
  public void start(CommunicationServiceContext serviceContext) throws ServiceException {
    IAIK.addAsJDK14Provider();
    SystemURLStreamHandlerFactory urlStreamHandlerFactory = new SystemURLStreamHandlerFactory();
    urlStreamHandlerFactory.registerClassLoader(getClass().getClassLoader());
    urlStreamHandlerFactory.registerHandlerPackage("iaik.protocol");
    URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
  }

  /**
   * @see com.sap.engine.frame.ServiceFrame
   */
  public void stop() {
    Security.removeProvider("IAIK");
  }

}


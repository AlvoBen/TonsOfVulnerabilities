/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.interfaces.cross.CrossInterface;

import java.util.Properties;

public abstract class P4ContainerEventListenerImpl implements ContainerEventListener{

  public static final String LOGER_NAME = "P4";
  public static String LOG_INTERFACE_NAME = "log";
  public static String CROSS_INTERFACE_NAME = "cross";
  public static String SECURITY_PROVIDER_NAME = "security";
  public static String APPLICATION_CONTEXT_NAME = "appcontext";

  protected P4Provider p4Provider = null;
  protected static CrossInterface crossInterface = null;

  //protected Logger log = null;
  protected Object lock = new Object();



  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
//    if (useEC && interfaceName.equals(APPLICATION_CONTEXT_NAME)) {
//      cec = (ComponentExecutionContext) interfaceImpl;
//      P4ObjectBroker.init().setExecContext(cec);
//    } //else if (interfaceName.equals(LOG_INTERFACE_NAME)) {


//      try {
//        log = ((LogInterface) interfaceImpl).createLogger(LOGER_NAME, null);
//      } catch (LoggerAlreadyExistingException exc) {
//        log = exc.getExistingLogger();
//      }
//    }
  }

  public void interfaceNotAvailable(String interfaceName) {
//     if (interfaceName.equals("log")) {
//       log = null;
//     }
   }

  public void containerStarted(){
  }

  public void beginContainerStop() {
  }

  public void serviceNotStarted(String serviceName) {
  }

  public void beginServiceStop(String serviceName) {
  }

  public void serviceStopped(String serviceName) {
  }

  public void markForShutdown(long time) {
  }

  public abstract void serviceStarted(String serviceName, Object serviceInterface);
  public abstract boolean setServiceProperty(String key, String value);
  public abstract boolean setServiceProperties(Properties serviceProperties);
}

/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.server;

import com.sap.engine.frame.state.ManagementListener;

import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.internal.giop.IncomingMessage;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.admin.RMIIIOPResource;


import com.sap.engine.admin.model.ManagementModelManager;

import java.util.Vector;

/**
 * This class implements IIOP management interface
 *
 * @author Nikolai Neychev
 * @version 6.30
 */
public class IIOPManagement implements IIOPManagementInterface {

    private Vector managedResources = new Vector();
  /**
   * Gets the exported object count
   * @return The object count
   */
  public int getExportedRemoteObjectsCount() {
    return ((ClientORB) ClientORB.init()).getObjectCount();
  }

  public int getIIOPThreadUsageRate() {
    return (IncomingMessage.getBusyThreads() * 100) / CommunicationLayerImpl.broker.getMessageProcessor().getNumberOfConcurrentThreads();
  }

  /**
   * Registers management listener
   * @param managementListener The management listener
   */
  public void registerManagementListener(ManagementListener managementListener) {
  }
/*
  public boolean processModelAction(ModelAction modelAction) throws ModelActionException {
    if (modelAction == null) {
      return false;
    } else if (modelAction instanceof LifecycleAction) {
      LifecycleAction lifecycleAction = (LifecycleAction)modelAction;

      switch (lifecycleAction.getAction()) {
        case LifecycleAction.CREATE_MODEL: {
          ManagementModelManager mmManager = lifecycleAction.getManagementModelManager();
          if (mmManager == null) {
            String message = "Error: lifecycleAction.getManagementModelManager() returned null. Cannot register Web modules for JMX management";
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beWarning()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).warningT("CorbaServiceFrame.start()", message);
            }
            throw new ModelActionException(message);
          }

          try {
            RMIIIOPResource resource = new RMIIIOPResource("ORB", mmManager);
            managedResources.add(resource);
          } catch (OutOfMemoryError e) {
            throw e;
          } catch (ThreadDeath e) {
            throw e;
          } catch (Exception e) {
            String message = "RMI_IIOPResource \"ORB\" is not completely registered for JMX management";
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beWarning()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).warningT(message, e.getMessage());
            }
            throw new ModelActionException(message, e);
          }
        }
          return true;
        case LifecycleAction.DESTROY_MODEL: {
          try {
            synchronized (managedResources) {
              RMIIIOPResource[]  resources = (RMIIIOPResource[]) managedResources.toArray(new RMIIIOPResource[0]);
              for (int i = 0; i < resources.length; i++) {
                resources[i].destroy();
              }

              managedResources.removeAllElements();
            }
          } catch (Exception ex) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beWarning()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).warningT("JNDIResource \"/\" is not completely destroyed for JMX management.", ex.getMessage());
            }
          }
        }
          return true;
        default:
//        JNDIFrame.log.logWarning("WARNING: Received LifecycleAction with unknown action code==(" + lifecycleAction.getAction() + ").");
            return false;
      }

    }
    return false;
  }
  */
  public void registerResources(ManagementModelManager mmManager) {
        try {
          RMIIIOPResource resource = new RMIIIOPResource("ORB", mmManager);
          managedResources.add(resource);
        } catch (OutOfMemoryError e) {
          throw e;
        } catch (ThreadDeath e) {
          throw e;
        } catch (Exception e) {
          String message = "RMI_IIOPResource \"ORB\" is not completely registered for JMX management";
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beWarning()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).warningT(message, e.getMessage());
          }
          //throw what???
        }
  }
  
  public void unregisterResources() {
      try {
          synchronized (managedResources) {
            RMIIIOPResource[]  resources = (RMIIIOPResource[]) managedResources.toArray(new RMIIIOPResource[0]);
            for (int i = 0; i < resources.length; i++) {
              resources[i].destroy();
            }

            managedResources.removeAllElements();
          }
      } catch (Exception ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beWarning()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).warningT("JNDIResource \"/\" is not completely destroyed for JMX management", ex.getMessage());
          }
        }
  }

}

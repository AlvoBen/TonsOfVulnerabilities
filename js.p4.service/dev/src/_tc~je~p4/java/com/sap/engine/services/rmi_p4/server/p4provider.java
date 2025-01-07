/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.interfaces.cross.ProtocolProvider;
import com.sap.engine.interfaces.cross.ProviderContainer;
import com.sap.engine.interfaces.cross.MessageProcessor;
import com.sap.engine.interfaces.security.ResourceContext;
import com.sap.engine.services.rmi_p4.*;
import com.sap.engine.services.rmi_p4.reflect.P4InvocationHandler;
import com.sap.engine.services.rmi_p4.reflect.LocalInvocationHandler;
import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.util.Hashtable;
import java.util.Vector;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

/**
 * Public class P4Provider is the implemenatation of
 * ProtocolProvider interface for P4 remote protocol
 *
 * @author Georgy Stanev
 * @version 7.0
 */
public class P4Provider implements ProtocolProvider {

  /**
   * A P4SessionProcessor instance to be registered
   */
  private static P4SessionProcessor sessionProcessor;
  private static P4MessageProcessor messageProcessor;
  /**
   * P4ObjectBroker initiated
   */
  private P4ObjectBroker broker = P4ObjectBroker.init();
  protected boolean generateStubs = true;

  /**
   * Constructor for the P4Provider
   *
   * @param _session - P4SessionProcessor
   * @param generate - stubs to be generated
   */
  public P4Provider(P4SessionProcessor _session, boolean generate) {
    sessionProcessor = _session;
    generateStubs = generate;
    messageProcessor = new P4MessageProcessor(sessionProcessor);
    sessionProcessor.setMessageProcessor(messageProcessor);
  }

  /**
   * This method generates all the necessary files, i.e.
   * Skeleton and Stub files an redistributes them according
   * to whether they are aimed to be client side or server side
   *
   * @param objects    - objects for which Skeletons and Stubs are generated
   * @param interfaces - interfaces for which Stubs are generated
   * @param workDir    - working directory
   * @return new ProviderContainer with all the information necessary
   */
  public ProviderContainer generateSupport(Class[] objects, Class[] interfaces, Hashtable access, String workDir) {
    if (!generateStubs) {
      return new ProviderContainer(new String[0], new String[0], workDir);
    }
    Vector vAll = new Vector();
    Vector temp = null;
    Vector client = new Vector();

    for (int i = 0; i < objects.length; i++) {
      try {
        P4StubSkeletonGenerator generator = new P4StubSkeletonGenerator(objects[i], workDir, access);
//        generator.setApplyExecutionContext(applyExecContext);
        temp = generator.generate();
        int size = temp.size();

        for (int j = 0; j < size; j++) {
          String generated = (String) temp.elementAt(j);
          vAll.addElement(generated);
          if (generated.endsWith("_Stub")) {
            client.addElement(generated);
          }
        }
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4Provider.generateSupport", P4Logger.exceptionTrace(ex));
        }
      }
    }

    String[] serverSupport = new String[vAll.size()];
    vAll.copyInto(serverSupport);
    String[] clientSupport = new String[client.size()];
    client.copyInto(clientSupport);
    return new ProviderContainer(serverSupport, clientSupport, workDir);
  }


  public void setInitialObject(String name, java.rmi.Remote initialObject) {
    sessionProcessor.setInitialObject(name, initialObject);
  }

  public void removeInitialObject(String name) {

  }

  public String getName() {
    return "p4";
  }

  public void exportObject(java.rmi.Remote rm, ClassLoader loader) {
  }

  /**
   * Unexport object form both P4 and IIOP.
   */
  public void unexportObject(java.rmi.Remote remote) {
    try {
      P4RemoteObject p4RemoteObjectInstance = broker.loadObject(remote);
      broker.disconnect(p4RemoteObjectInstance);
      p4RemoteObjectInstance.clearIIOPExportedStubs();
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4Provider.unexportObject(Remote)", "Problem with unexport remote object: \r\n" + P4Logger.exceptionTrace(e));
      }
    }
  }

  public ObjectReference getObjectReference(Object object) {
    try {
      RemoteObjectInfo info = null;
      if (object == null) {
        return null;
      } else if (object instanceof StubImpl) {
        info = ((StubImpl)object).info;
      } else if (Proxy.isProxyClass(object.getClass())) {
        InvocationHandler handler = Proxy.getInvocationHandler(object);
        if (handler instanceof P4InvocationHandler) {
          info = ((P4InvocationHandler)Proxy.getInvocationHandler(object)).getInfo().info;
        } else if (handler instanceof LocalInvocationHandler) {
          info = ((LocalInvocationHandler)Proxy.getInvocationHandler(object)).info;
        }
      }
      if (info != null) {
        info = P4ServerObjectInfo.cloneInfo(info);
        if (info.ownerId == broker.brokerId && info.server_id == broker.id) {
           info.connectionProfiles = broker.getConnectionProfiles(); // update profiles
        }
        return (ObjectReference)info;
      } else {
        P4ObjectBroker broker = P4ObjectBroker.init();
        P4RemoteObject p4Object = broker.loadObject((java.rmi.Remote) object);
        broker.setURLList(p4Object);
        info = p4Object.getObjectInfo();
        //For OS 390 this may cause problems
        info.connectionProfiles = broker.getConnectionProfiles();
        return (ObjectReference) info;
      }
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4Provider.getObjectReference(Object)", P4Logger.exceptionTrace(ex));
      }
      return null;
    }
  }

  public Object narrow(Object obj, Class _class) throws ClassCastException {
    if (obj instanceof StubBaseInfo) {
      return broker.narrow(obj, _class);
    }
    if (obj instanceof RemoteObjectInfo) {
      StubBaseInfo sbi = StubBaseInfo.makeStubBaseInfo((RemoteObjectInfo) obj);
      return broker.narrow(sbi, _class);
    }
    return broker.narrow(obj, _class);
  }
  /**
   * @deprecated
   */
  public static  ResourceContext getResourceContext() {
    return null;
  }


  public Class getProviderClass() {
    return RemoteObjectInfo.class;
  }

  public MessageProcessor getMessageProcessor() {
      return messageProcessor;
  }

}


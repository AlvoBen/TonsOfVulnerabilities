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
package com.sap.engine.services.iiop.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.interfaces.cross.ProtocolProvider;
import com.sap.engine.interfaces.cross.MessageProcessor;
import com.sap.engine.services.iiop.internal.IIOPObjectReference;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import java.util.Vector;
import java.rmi.Remote;

/**
 * Public class IIOPProvider is the implemenatation of
 * ProtocolProvider interface for IIOP remote protocol
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class IIOPProvider extends IIOPGenerator implements ProtocolProvider {

  private IIOPMessageProcessor messageProcessor;

  private static String[] symbols = {".", "_", ":"};

  public IIOPProvider(ApplicationServiceContext context) {
    super();
    this.messageProcessor = new IIOPMessageProcessor();
  }

  public void setInitialObject(String name, Remote initialObject) {
  }

  public void removeInitialObject(String name) {
  }

  public String getName() {
    return "iiop";
  }

  public void exportObject(Remote rm, ClassLoader loader) {
    try {
      loadObject(rm);
    } catch (ClassNotFoundException cnfex) {
      // ok allready logged
      return;
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beInfo()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).infoT("IIOPProvider.exportObject(Remote, ClassLoader)", LoggerConfigurator.exceptionTrace(e));
      }
      return;
    }
  }

  public void unexportObject(Remote rm) throws java.rmi.NoSuchObjectException{
              Tie tie = Util.getTie(rm);

    if (tie == null) {
      throw new java.rmi.NoSuchObjectException("Object is not exported in IIOP provider");
    } else {
      Util.unexportObject(rm);
    }
  }

  public ObjectReference getObjectReference(java.lang.Object obj) {
    String ior = "";
    String name = "";
    try {
      org.omg.CORBA.portable.ObjectImpl corbaObj = (org.omg.CORBA.portable.ObjectImpl) loadObject(obj);
      String temp = corbaObj._ids()[0];
      name = temp.substring(temp.indexOf(symbols[2]) + 1, temp.lastIndexOf(symbols[2])); // ":"
      ior = org.omg.CORBA.ORB.init().object_to_string(corbaObj);
    } catch (ClassNotFoundException cnfex) {
      // ok allready logged
      return null;
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beWarning()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).warningT("IIOPProvider.getObjectReference(Object)", "Can't get IOR for : " + obj + " , will use IORReference..." + LoggerConfigurator.exceptionTrace(ex));
      }
      return null;
    }
    return new IIOPObjectReference(ior, name);
  }

  public Object narrow(Object obj, Class _class) throws ClassCastException {
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).infoT("IIOPProvider.narrow(Object, Object)", " Object"+obj+" Class"+_class);
    }

    Class cl = obj.getClass();

    if (_class.isAssignableFrom(cl)) {
      return obj;
    }

    String clName = _class.getName();
    StringBuffer buffer = new StringBuffer(clName);
    buffer.insert(clName.lastIndexOf(symbols[0]) + 1, symbols[1]); // 0 - "." ; 1 - "_"
    buffer.append("_Stub");
    javax.rmi.CORBA.Stub stub = null;
    try {
      Class cl1 = Class.forName(buffer.toString(), true, cl.getClassLoader());
      stub = (javax.rmi.CORBA.Stub) cl1.newInstance();
      stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("IIOPProvider.narrow(Object, Object)", LoggerConfigurator.exceptionTrace(ex));
      }
    }
    return stub;
  }

  public static org.omg.CORBA.Object loadObject(Object obj) throws ClassNotFoundException {
    if (obj instanceof org.omg.CORBA.Object) {
      return (org.omg.CORBA.Object) obj;
    }

    if (obj instanceof Remote) {
      Object ob = Util.getTie((Remote) obj);

      if (ob != null) {
        return (org.omg.CORBA.Object) ob;
      }

      if (java.lang.reflect.Proxy.isProxyClass(obj.getClass())) {
        javax.rmi.CORBA.Tie tie = new IIOPGenericTie();
        tie.setTarget((Remote) obj);
        Util.registerTarget(tie, (Remote) obj);
        return (org.omg.CORBA.Object) tie;
      }

      Class cl = obj.getClass();
      String initialTieName = null;

      String clName;
      StringBuffer buffer = new StringBuffer();

      while ((cl != null) && (!cl.equals(javax.rmi.PortableRemoteObject.class)) && (!cl.equals(java.lang.Object.class))) {
        clName = cl.getName();
        buffer.append(clName);
        buffer.insert(clName.lastIndexOf(symbols[0]) + 1, symbols[1]); // 0 - "." ; 1 - "_"
        buffer.append("_Tie");

        String tieBufferContent = buffer.toString();
        if (initialTieName == null) {
          initialTieName = tieBufferContent;
        }

        buffer.setLength(0);

        try {
          Class cl1 = Class.forName(tieBufferContent, true, cl.getClassLoader());
          javax.rmi.CORBA.Tie tie = (javax.rmi.CORBA.Tie) cl1.newInstance();
          tie.setTarget((Remote) obj);
          Util.registerTarget(tie, (Remote) obj);
          return (org.omg.CORBA.Object) tie;
        } catch (Exception ex) {
          //$JL-EXC$
          cl = cl.getSuperclass();
        }
      }

      ClassNotFoundException cnfe = new ClassNotFoundException(initialTieName);
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).bePath()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).pathT("IIOPProvider.loadObject(Object)", "Cannot load tie class for " + initialTieName);
      }

      throw cnfe;
    }

    ClassNotFoundException cnfe = new ClassNotFoundException("Incapable " + obj.getClass());
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("IIOPProvider.loadObject(Object)", "Error during loading object " + obj.getClass().getName());
    }
    throw cnfe;
  }

   public Class getProviderClass(){
    return Vector.class;
  }

  public MessageProcessor getMessageProcessor() {
    return messageProcessor;
  }
}


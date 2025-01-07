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
package com.sap.engine.services.iiop.internal;

import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.ORB;

import javax.rmi.PortableRemoteObject;

/*
 * @author Georgy Stanev
 * @version 4.0
 */
public class IIOPObjectReference implements ObjectReference, java.io.Serializable {

  static final long serialVersionUID = -2445346501365698828L;

  String ior = null;
  String className = "";
  static ORB orb = null;
  static Object mon = new Object();

  public IIOPObjectReference(String IOR, String className) {
    this.ior = IOR;
    this.className = className;
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("IIOPObjectReference.IIOPObjectReference(String, String)", "IIOP Reference created for " + ior);
    }
  }

  public Object toObject(ClassLoader loader) {
    try {
      synchronized (mon) {
        if (orb == null) {
          String[] args = new String[0];
          orb = ORB.init(args, null);
        }
      }
      return PortableRemoteObject.narrow(orb.string_to_object(ior), loader.loadClass(className));
    } catch (ClassNotFoundException clne) {
      return orb.string_to_object(ior);
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("IIOPObjectReference.toObject(ClassLoader)", LoggerConfigurator.exceptionTrace(ex));
      }
    }
    return new Object();
  }

  public Object toObject(ClassLoader loader, Object properties){
    return toObject(loader);
  }

}


/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.implclient;

import java.rmi.*;
import javax.rmi.*;
import javax.rmi.CORBA.*;

import org.omg.CORBA.ORB;
import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.interfaces.cross.ObjectReferenceImpl;

/**
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class IIOPReferenceFactory implements RemoteObjectReferenceFactory {

  public static final String IIOP_SERVICE_NAME = "iiop";
  protected static ORB orb = null;

  public IIOPReferenceFactory() {

  }

  public IIOPReferenceFactory(ORB orb) {
    //    this.orb = orb;
  }

  public ObjectReference getObjectReference(Remote remote) {
    org.omg.CORBA.portable.ObjectImpl corbaObject = (org.omg.CORBA.portable.ObjectImpl) Util.getTie(remote);

    if (orb == null) {
      String[] args = new String[0];
      orb = ORB.init(args, null);
    }

    String temp = corbaObject._ids()[0];
    String name = temp.substring(temp.indexOf(":") + 1, temp.lastIndexOf(":"));
    return new IIOPObjectReference(orb.object_to_string(corbaObject), name);
  }

  public String protocolName() {
    return IIOP_SERVICE_NAME;
  }

  protected static ORB getOrb() {
    if (orb == null) {
      orb = ORB.init(new String[0], null);
    }

    return orb;
  }

}


class IIOPObjectReference extends ObjectReferenceImpl {

  String ior = null;
  String className = "";

  public IIOPObjectReference(String IOR, String className) {
    super(-1);
    this.ior = IOR;
    this.className = className;
    super.setProtocolName(IIOPReferenceFactory.IIOP_SERVICE_NAME);
  }

  public Object toObject(ClassLoader loader) {
    ORB orb = null;
    try {
      try {
        orb = IIOPReferenceFactory.getOrb(); //ORB.init();
        return PortableRemoteObject.narrow(orb.string_to_object(ior), loader.loadClass(className));
      } catch (ClassNotFoundException clne) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        return orb.string_to_object(ior);
      } catch (Exception ex) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        orb = ORB.init();
        return PortableRemoteObject.narrow(orb.string_to_object(ior), loader.loadClass(className));
      }
    } catch (ClassNotFoundException clne) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
      return orb.string_to_object(ior);
    } catch (Exception ex) {
      return null;
    }
  }

  public Object toObject(ClassLoader loader, Object properties) {
    return toObject(loader);
  }
}


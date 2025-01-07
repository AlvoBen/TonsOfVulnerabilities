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

package com.sap.engine.services.iiop.PortableServer;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import com.sap.engine.services.iiop.internal.ClientORB;

public class ServantDelegate implements org.omg.PortableServer.portable.Delegate {

  private ORB orb = null;
  private POA rootPOA = null;

  public ServantDelegate(ORB orb) {
    this.orb = orb;
    this.rootPOA = ((ClientORB) orb).getRootPOA();
  }

  public org.omg.CORBA.ORB orb(Servant self) {
    return orb;
  }

  public org.omg.CORBA.Object this_object(Servant self) {
    try {
      return rootPOA.servant_to_reference(self);
    } catch (Exception e) {
      return null;
    }
  }

  public POA poa(Servant self) {
    return rootPOA;
  }

  public byte[] object_id(Servant self) {
    try {
      return rootPOA.servant_to_id(self);
    } catch (Exception e) {
      return null;
    }
  }

  public POA default_POA(Servant self) {
    return rootPOA;
  }

  public boolean is_a(Servant self, String repId) {
    String[] repositoryIds = self._all_interfaces(poa(self), object_id(self));
    for (int i = 0; i < repositoryIds.length; i++) {
      if (repId.equals(repositoryIds[i])) {
        return true;
      }
    }
    return false;
  }

  public boolean non_existent(Servant self) {
    throw new org.omg.CORBA.NO_IMPLEMENT("Method is not implemented");
  }

  public org.omg.CORBA.Object get_interface_def(Servant Self) {
    throw new org.omg.CORBA.NO_IMPLEMENT("Method is not implemented");
  }
}
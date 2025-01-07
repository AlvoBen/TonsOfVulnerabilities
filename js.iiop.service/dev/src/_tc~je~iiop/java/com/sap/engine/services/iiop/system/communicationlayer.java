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
package com.sap.engine.services.iiop.system;

import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.interfaces.csiv2.SimpleProfileInterface;

public abstract class CommunicationLayer {

  //holds a reference to the current ORB implementation
  protected static com.sap.engine.services.iiop.internal.ClientORB orb;
  protected static CommunicationLayer thisLayer = null;

  /**
   *  Returns a new instance of <code>CommunicationLayer</code>. The name of the class to be
   *  loaded is passed as a parameter. If the class file could not be found the <code>null</code> is
   *  returned.
   *
   * @param   s         the name of the class to be loaded
   * @param   _orb      ORB assigned to this class
   * @return   CommunicationLayer   instance that was loaded; <code>null</code> on load failure
   */
  public static CommunicationLayer init(String s, com.sap.engine.services.iiop.internal.ClientORB _orb) {
    if (thisLayer == null) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beInfo()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).infoT("CommunicationLayer.init(String, ClientORB)", "Trying to load the instance of CommunicationLayer class" + s);
      }
      orb = _orb;
      try {
        ClassLoader loader = CommunicationLayer.class.getClassLoader();
        Class layerClass = loader.loadClass(s);
        thisLayer = (CommunicationLayer) layerClass.newInstance();
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("CommunicationLayer.init(String, ClientORB)", "couldn't load the instance of CommunicationLayer class" + LoggerConfigurator.exceptionTrace(e));
        }
      }
    }

    return thisLayer;
  }


  public abstract String[] getURL(); //Maj niama da e string

  public abstract org.omg.CORBA.portable.Delegate getDelegate(IOR ior);

  public abstract int openSSLServerSocket(int port) throws java.io.IOException;

  public void setSSLProvider(String sslProviderClass, String[] sslProviderParams) {
  }

  public static com.sap.engine.services.iiop.internal.ClientORB getORB() {
    return orb;
  }

  protected TransportAddress parseSSLTransportAddress(org.omg.CORBA.ORB orb, Profile profile) {
    SimpleProfileInterface[] simpleProfiles = profile.getComponents();
    SimpleProfileInterface taggedComponent = null;

    for (SimpleProfileInterface aComponent : simpleProfiles) {
      if (aComponent.getTag() == TAG_CSI_SEC_MECH_LIST.value) {
        taggedComponent = aComponent;
        break;
      } else if (aComponent.getTag() == TAG_SSL_SEC_TRANS.value) {
        taggedComponent = aComponent;
        break;
      }
    }

    if (taggedComponent == null) {
      return null;
    } else if (taggedComponent.getTag() == TAG_SSL_SEC_TRANS.value) {
      CORBAInputStream stream = new CORBAInputStream(orb, taggedComponent.getData());
      stream.setEndian(stream.read_boolean());
      SSL sslComp = SSLHelper.read(stream);
      return new TransportAddress(profile.getHost(), sslComp.port);
    } else {
      CORBAInputStream stream = new CORBAInputStream(orb, taggedComponent.getData());
      stream.setEndian(stream.read_boolean());
      CompoundSecMech[] list = CompoundSecMechListHelper.read(stream).mechanism_list;

      if (list == null || list.length == 0) {
        return null;
      }

      SimpleProfileInterface transportMech = list[0].transport_mech;

      if (transportMech.getTag() == TAG_NULL_TAG.value) {
        return null;
      } else if (transportMech.getTag() == TAG_TLS_SEC_TRANS.value) {
        CORBAInputStream inputStream = new CORBAInputStream(orb, transportMech.getData());
        inputStream.setEndian(inputStream.read_boolean());
        TLS_SEC_TRANS transport = TLS_SEC_TRANSHelper.read(inputStream);
        TransportAddress[] addresses = transport.addresses;

        if (addresses == null || addresses.length == 0) {
          return null;
        }

        return addresses[0];
      }
    }

    return null;
  }

}


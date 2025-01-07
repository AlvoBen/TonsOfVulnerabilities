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

package com.sap.engine.services.iiop.csiv2.interceptors;

import org.omg.CORBA.*;
import org.omg.IOP.Codec;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.csiv2.SAS_ContextSec;
import com.sap.engine.interfaces.csiv2.SimpleProfileInterface;
import com.sap.engine.frame.client.*;
import com.sap.engine.services.iiop.csiv2.CSI.IdentityToken;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import javax.security.auth.Subject;
/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class AppclientInterceptor extends ClientInterceptor {


  public AppclientInterceptor(ORB orb, Codec codec) {
    super(orb, codec);
  }

  protected SecurityContextObject getCurrentSecurityContext() {
    try {
      return (SecurityContextObject) ClientFactory.getThreadContextFactory().getThreadContext().getContextObject("security");
    } catch (ClientException e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("AppclientInterceptor.getCurrentSecurityContext()", LoggerConfigurator.exceptionTrace(e));
      }
      return null;
    }
  }

  protected IdentityToken createIdentityToken(Subject subject, SAS_ContextSec sasContext, SimpleProfileInterface transportMech) {
    if (transportMech != null) {
      if (transportMech.getTag() == TAG_TLS_SEC_TRANS.value) {
        byte[] transportData = transportMech.getData();
        CORBAInputStream inputStream = new CORBAInputStream(orb, transportData);
        boolean endian = inputStream.read_boolean();
        inputStream.setEndian(endian);
        TLS_SEC_TRANS transport = TLS_SEC_TRANSHelper.read(inputStream);
        if ((transport.target_requires & EstablishTrustInClient.value) == EstablishTrustInClient.value) {
          return new IdentityToken();
        }
      }
    }
    return super.createIdentityToken(subject, sasContext, transportMech);
  }
}

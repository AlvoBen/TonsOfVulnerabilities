/**
 *  Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.server.jaas.mapping;

import java.util.Iterator;
import java.util.Map;

import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.auth.CallerImpersonationConfiguration;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class CallerImpersonationMappingLoginModule implements LoginModule {

  private final static Location LOCATION = Location.getLocation(CallerImpersonationMappingLoginModule.class);
  private ManagedConnectionFactory mcf = null;

  public CallerImpersonationMappingLoginModule() {
  }

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    try {
      Subject newSubject = null;
      SecurityContextObject context = (SecurityContextObject) SecurityServerFrame.getServiceContext().getCoreContext().getThreadSystem().getThreadContext().getContextObject("security");
      if (context != null) {
        newSubject = context.getSession().getSubject();
      }
      if (newSubject != null) {
        subject.getPrincipals().clear();
        subject.getPrincipals().addAll(newSubject.getPrincipals());
        subject.getPrivateCredentials().clear();
        mcf = (ManagedConnectionFactory) options.get(CallerImpersonationConfiguration.MCF);

        if (mcf != null) {
          Iterator privateCredIter = newSubject.getPrivateCredentials().iterator();
          Object privateCred = null;

          while (privateCredIter.hasNext()) {
            privateCred = privateCredIter.next();

            if (privateCred instanceof PasswordCredential) {
              ((PasswordCredential) privateCred).setManagedConnectionFactory(mcf);
            }

            subject.getPrivateCredentials().add(privateCred);
          }
        } else {
          subject.getPrivateCredentials().addAll(newSubject.getPrivateCredentials());
        }

        subject.getPublicCredentials().clear();
        subject.getPublicCredentials().addAll(newSubject.getPublicCredentials());
      }
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.INFO, "initialize", e);
    }
  }

  public boolean login() throws LoginException {
    return true;
  }

  public boolean commit() throws LoginException {
    return true;
  }

  public boolean abort() throws LoginException {
    return true;
  }

  public boolean logout() throws LoginException {
    return true;
  }

}


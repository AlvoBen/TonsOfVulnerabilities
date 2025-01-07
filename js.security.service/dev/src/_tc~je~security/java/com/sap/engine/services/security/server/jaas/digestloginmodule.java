/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.server.jaas;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.log.LogInterface;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.security.Base64;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.http.AuthenticationInfo;
import com.sap.engine.lib.security.http.DigestAuthenticationInfo;
import com.sap.engine.lib.security.http.DigestChallenge;
import com.sap.engine.lib.security.http.DigestUtil;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class DigestLoginModule extends AbstractLoginModule {

  private static final String HEADER_NAME = "Authorization";
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_LOCATION + ".DigestLoginModule");

  private CallbackHandler callbackHandler = null;
  private Subject subject = null;
  private Map sharedState = null;
  private char[] password = null;
  private MessageDigest mDigest = null;
  private HttpGetterCallback[] digestCallbacks = null;
  private UserContext userContext = null;
  private UserInfo userInfo = null;
  private SecurityContext securityContext = null;
  private boolean successful;
  private boolean shouldBeIgnored;
  private boolean nameSet = false;

  private String username = null;
  private String realm = null;
  private String nonce = null;
  private String nc = null;
  private String cnonce = null;
  private String qop = null;
  private String uri = null;
  private String response = null;
  private String algorithm = "MD5";
  private String method = null;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    super.initialize(subject, callbackHandler, sharedState, options);
    
    this.callbackHandler = callbackHandler;
    this.subject = subject;
    this.sharedState = sharedState;
    successful = false;
    shouldBeIgnored = false;

    securityContext = SecurityContextImpl.getRoot();
    userContext = securityContext.getUserStoreContext().getActiveUserStore().getUserContext();
    if (userContext == null) {
      throw new SecurityException("Unable to get user context.");
    }
  }

  public boolean login() throws LoginException {
    digestCallbacks = new HttpGetterCallback[] {new HttpGetterCallback(), new HttpGetterCallback()};

    digestCallbacks[0].setType(HttpCallback.HEADER);
    digestCallbacks[0].setName(HEADER_NAME);
    digestCallbacks[1].setType(HttpCallback.METHOD_TYPE);

    try {
      callbackHandler.handle(digestCallbacks);
    } catch (UnsupportedCallbackException e) {
      shouldBeIgnored = true;
      return false;
    } catch (IOException e) {
      throwUserLoginException(e, LoginExceptionDetails.IO_EXCEPTION);
    }

    try {
      initDirectiveValues((String) digestCallbacks[0].getValue());
      initMethod((String) digestCallbacks[1].getValue());

      if (username == null) {
        throwNewLoginException("No user name provided.", LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
        return false;
      }

      try {
        refreshUserInfo(username);
        userInfo = userContext.getUserInfo(username);
      } catch (SecurityException e) {
        throwUserLoginException(e, LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
      }

      if (userInfo == null) {
        throwNewLoginException("No such user " + username + " found in the userstore.", LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
      }

      userContext.fillSubject(userInfo, subject);
      password = getPassword(subject);

      if (password == null) {
        SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, LOCATION, "ASJ.secsrv.000152", "This user store is not configured to support DIGEST login of users! The userstore property RETRIEVE_PASSWORDS should be set to 'true'. If the user store does not support retrieving of passwords at all, then this property is missing.");
        throw new BaseLoginException("This user store is not configured to support DIGEST login of users. For more information contact your system administrator.");
      }

      boolean auth = authenticate();

      if (auth) {
        username = userInfo.getName();

        if (sharedState.get(AbstractLoginModule.NAME) == null) {
          sharedState.put(AbstractLoginModule.NAME, username);
          nameSet = true;
        }
        successful = true;
        return true;
      }

      throwNewLoginException("Client request does not contain proper credentials.", LoginExceptionDetails.DIGEST_CREDENTIALS_NOT_VALID);
    } catch (LoginException e) {
      throw e;
    } catch (Exception e) {
      throwUserLoginException(e, LoginExceptionDetails.DIGEST_CREDENTIALS_NOT_VALID);
    }

    return false;
  }

  public boolean commit() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        Principal principal = new Principal(username);
        principal.setAuthenticationMethod(Principal.AUTH_METHOD_DIGEST);

        subject.getPrincipals().add(principal);
        subject.getPublicCredentials().add(generateAuthenticationInfo());

        if (nameSet) {
          sharedState.put(AbstractLoginModule.PRINCIPAL, principal);
        }
      } else {
        password = null;
        username = null;
        realm = null;
        nonce = null;
        nc = null;
        cnonce = null;
        qop = null;
        uri = null;
        response = null;
        method = null;
        userInfo = null;
      }

      return true;
    } else {
      shouldBeIgnored = false;
      return false;
    }
  }

  public boolean abort() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        password = null;
        username = null;
        realm = null;
        nonce = null;
        nc = null;
        cnonce = null;
        qop = null;
        uri = null;
        response = null;
        method = null;
        successful = false;
        userInfo = null;
      }

      return true;
    } else {
      shouldBeIgnored = false;
      return false;
    }
  }

  public boolean logout() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        userContext.emptySubject(subject);
        removeCredentials();
        successful = false;
      }

      return true;
    } else {
      return false;
    }
  }

  private void removeCredentials() {
    try {
		  Iterator publicCredentials = subject.getPublicCredentials().iterator();
		  Object credential = null;
		  while (publicCredentials.hasNext())  {
		    credential = publicCredentials.next();
		    if (credential instanceof AuthenticationInfo) {
		      publicCredentials.remove();
		    }
		  }
    } catch (Exception e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "Cannot remove security credentials of class com.sap.engine.lib.security.http.AuthenticationInfo from subject on logout.", e);
      }
    }
  }

  private boolean authenticate() throws SecurityException {
    mDigest = DigestUtil.messageDigest(algorithm);
    String clientDigest = response;
    String serverDigest = getAuthenticationString(true);
    if (serverDigest.equals(clientDigest)) {
       return true;
    }
    return false;
  }

  private String getAuthenticationString(boolean request) {
    String a2 = null;
    if (request) {
      a2 = method + ":" + uri;
    } else {
      a2 = ":" + uri;
    }
    String md5a2 = DigestUtil.md5Encode(mDigest.digest(a2.getBytes()));
    String md5a1 = getDigest(username, realm);
    if (md5a1 == null || md5a2 == null) {
      throw new SecurityException("Cannot perform the digest algorithm due to missing MD5 implementation.");
    }
    String serverDigestValue = md5a1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + md5a2;
    return DigestUtil.md5Encode(mDigest.digest(serverDigestValue.getBytes()));
  }

  /**
   * Return the digest associated with given principal's user name.
   */
  private String getDigest(String username, String realm) {
    String digestValue = username + ":" + realm + ":" + new String(password);
    byte[] digest = mDigest.digest(digestValue.getBytes());
    return DigestUtil.md5Encode(digest);
  }

  private AuthenticationInfo generateAuthenticationInfo() {
    DigestAuthenticationInfo auth = new DigestAuthenticationInfo();
    //Next Nonce ?!?!?!?!?!?!
    String decodedNonce = null;

    try {
      decodedNonce = new String(Base64.decode(nonce.getBytes()));
      String remoteAddress = decodedNonce.substring(0, decodedNonce.indexOf(":"));
      auth.setNextNonce(DigestChallenge.generateNonce(remoteAddress));
    } catch (Exception _) {
      logThrowable(LogInterface.DEBUG, _);
    }

    auth.setMessageQOP(qop);
    auth.setResponse(getAuthenticationString(false));
    auth.setCNonce(cnonce);
    auth.setNonceCount(nc);
    return auth;
  }

  private char[] getPassword(Subject sbj) {
    Object[] privateCred = sbj.getPrivateCredentials().toArray();
    for (int i = 0; i < privateCred.length; i++) {
      if (privateCred[i] instanceof PasswordCredential) {
        return ((PasswordCredential) privateCred[i]).getPassword();
      }
    }
    return null;
  }

  private void initDirectiveValues(String header) {
    // username, realm, nonce, nc, cnonce, qop, response, algorithm, ( uri ??? )
    if (header != null && header.startsWith("Digest ")) {
      header = header.substring(7).trim();
      StringTokenizer commaTokenizer = new StringTokenizer(header, ",");

      while (commaTokenizer.hasMoreTokens()) {
        String currentToken = commaTokenizer.nextToken();
        int equalSign = currentToken.indexOf('=');
        
        if (equalSign < 0) {
          return;
        }

        String currentTokenName = currentToken.substring(0, equalSign).trim();
        String currentTokenValue = currentToken.substring(equalSign + 1).trim();

        if ("username".equals(currentTokenName)) {
          username = removeQuotes(currentTokenValue);
        }

        if ("realm".equals(currentTokenName)) {
          realm = removeQuotes(currentTokenValue);
        }

        if ("nonce".equals(currentTokenName)) {
          nonce = removeQuotes(currentTokenValue);
        }

        if ("nc".equals(currentTokenName)) {
          nc = currentTokenValue;
        }

        if ("cnonce".equals(currentTokenName)) {
          cnonce = removeQuotes(currentTokenValue);
        }

        if ("qop".equals(currentTokenName)) {
          qop = removeQuotes(currentTokenValue);
        }

        if ("uri".equals(currentTokenName)) {
          uri = removeQuotes(currentTokenValue);
        }

        if ("response".equals(currentTokenName)) {
          response = removeQuotes(currentTokenValue);
        }

        if ("algorithm".equals(currentTokenName)) {
          algorithm = removeQuotes(currentTokenValue);
        }
      }
    }
  }

  private String removeQuotes(String str) {
    int len = str.length();

    if (len > 2) {
      return str.substring(1, len - 1);
    }

    return str;
  }

//  private void initClientIP(String uri) {
//    this.uri = uri;
//  }

  private void initMethod(String method) {
    this.method = method;
  }

}



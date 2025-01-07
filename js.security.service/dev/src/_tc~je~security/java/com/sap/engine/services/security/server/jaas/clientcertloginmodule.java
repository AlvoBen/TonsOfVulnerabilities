/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.server.jaas;

import iaik.asn1.CodingException;
import iaik.x509.X509ExtensionException;
import iaik.x509.X509ExtensionInitException;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.lang.reflect.InvocationTargetException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.security.ClientCertificatePrincipal;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.server.jaas.cclm.RuleData;
import com.sap.engine.services.security.server.jaas.cclm.RuleHelper;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *  Login Module for login by certificate.
 *
 * @author Rumen Barov i033802
 * @version 2.00 
 */
public class ClientCertLoginModule extends AbstractLoginModule {

  private static final Location location = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_LOCATION + ".ClientCertLoginModule");
  
  protected String userName = null;
  protected UserContext userContext = null;
  protected X509Certificate[] arrX509Cert = null;
  protected boolean bLoginSuccessful = false;
  protected boolean bShouldBeIgnored = false;
  protected boolean bNameSetInSharedState = false;
  protected CallbackHandler callbackHandler = null;
  protected Subject subject = null;
  protected Map sharedState = null;
  protected Vector rules = new Vector();
  protected Properties opt = new Properties();
 
  public void initialize( Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options ){
  	final String METHOD = "initialize";
  	location.entering(METHOD);
  	
  	super.initialize( subject, callbackHandler, sharedState, options );
    this.callbackHandler = callbackHandler;
    this.subject = subject;
    this.sharedState = sharedState;
    this.opt.putAll( options );
    
    // backward compatibility in next lines 
    boolean bTakeFromCN = isTakeFromCN_ON( options );
    int iRemovedObsoleteOptions = removeObsoleteOptions( this.opt );
    if (0 < iRemovedObsoleteOptions) {
    	if (location.beWarning()) {
            location.warningT("Obsolete options passed to ClientCertLoginModule. Please fix policy configurations.");
        }
    	if (location.beDebug()) {
            location.debugT(METHOD, "Options of the {0} after removing obsoletes: {1} \n", new Object[] {ClientCertLoginModule.class, opt} );
        }
    }

    rules = RuleHelper.options2Rules( this.opt );
	if ( bTakeFromCN ) {
	    RuleData rule = new RuleData();
	    rule.setGetUserFrom( RuleData.GETUSERFROM_VAL_SUBJECTNAME );
	    rule.setAttributeName( "CN" );
	    rules.add( rule );
	}
	  
	//implement default behavior
	if ( 0 == rules.size() ) {
	    RuleData rule = new RuleData();
	    rule.setGetUserFrom( RuleData.GETUSERFROM_VAL_WHOLECERT );
	    rules.add( rule );
	}
	
	for ( int i = 0; i < rules.size(); i++ ) {
	  	RuleData rule = (RuleData) rules.get(i);
	  	if (location.beDebug()) {
            location.debugT("Rule " + String.valueOf(i) + ": " + rule);
        }
	  	rule.checkConsistency();
	}
    
    SecurityContext securityContext = SecurityContextImpl.getRoot();
    userContext = securityContext.getUserStoreContext().getActiveUserStore().getUserContext();
    if ( userContext == null ) {
      throw new SecurityException("Unable to get user context.");
    }

    CertRevocServiceWrapper.init(location);
    location.exiting(METHOD);
  }

  public boolean login() throws LoginException {
  	final String METHOD = "login";
  	location.entering(METHOD);
  	
    HttpGetterCallback certCallback = new HttpGetterCallback();
    certCallback.setType( HttpGetterCallback.CERTIFICATE );
    Object tempCerts = null;
    try {
      callbackHandler.handle( new Callback[] { certCallback } );
      tempCerts = certCallback.getValue();
    } catch ( UnsupportedCallbackException e ) {
      location.debugT(e.getMessage());
      bShouldBeIgnored = true;
      return false;
    } catch ( IOException e ) {
      throwUserLoginException( e, LoginExceptionDetails.IO_EXCEPTION );
    }


    if ( null == tempCerts ) {
      location.debugT("No certificate provided by the callback.");
      bShouldBeIgnored = true;
      return false;
    }

    if ( tempCerts instanceof X509Certificate[] ) {
      arrX509Cert = (X509Certificate[]) tempCerts;
    } else {
      location.debugT("Certificate is not of type X509Certificate.");
      bShouldBeIgnored = true;
      return false;
    }
    
    if (location.beDebug()) {
	    location.debugT("Certificates provided by the callback:");
      for (X509Certificate anArrX509Cert : arrX509Cert) {
        location.debugT(anArrX509Cert.toString());
      }
    }

    UserInfo userInfo = null;
    if ( ( arrX509Cert.length > 0 ) && ( arrX509Cert[0] != null ) ) {
      X509Certificate cert = arrX509Cert[0];
      
      location.debugT("Certificate used to resolve the user: {0}",  new Object[] {cert});

      // check certificate validity
      try {
        cert.checkValidity();
      } catch (CertificateExpiredException e1) {
        location.infoT("Certificate is expired. Certificate expiration date: {0}", new Object[] {cert.getNotAfter()});
        bShouldBeIgnored = true;
        return false;
      } catch (CertificateNotYetValidException e1) {
        location.infoT("Certificate is not yet valid. Certificate will be valid after: {0}", new Object[] {cert.getNotBefore()});
        bShouldBeIgnored = true;
        return false;
      }  
      
      try {
        userInfo = getUserNameFromCert(convert(arrX509Cert));
        if (userInfo == null) {
          throw new BaseLoginException("No user is associated with this certificate", LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE);
        }
      } catch ( CertificateException e ) {
      	location.infoT(e.getMessage());
        bShouldBeIgnored = true;
        return false;
      } catch ( X509ExtensionException e ) {
      	location.infoT("Cannot convert certificate to iaik.x509.X509Certificate. {0}", new Object[] {e.getMessage()});
        bShouldBeIgnored = true;
        return false;
      } catch ( CodingException e ) {
      	location.infoT(e.getMessage());
        bShouldBeIgnored = true;
        return false;
      } catch ( IllegalArgumentException e ) {
      	location.infoT(e.getMessage());
        bShouldBeIgnored = true;
        return false;
      }
      
      userName = userInfo.getName(); //get the canonical name
      
      // AOK - update shared state.
      if ( sharedState.get( AbstractLoginModule.NAME ) == null ) {
        sharedState.put( AbstractLoginModule.NAME, userName );
        bNameSetInSharedState = true;
        location.debugT("User {0} put in shared state.", new Object[] {userName});
      }
      bLoginSuccessful = true;
    } else {
      location.infoT( "No certificate provided." );
      bShouldBeIgnored = true;
      bLoginSuccessful = false;
    }
    location.exiting(METHOD);
    return bLoginSuccessful;
  }

  public boolean commit() throws LoginException{
    if ( !bShouldBeIgnored ) {
      if ( bLoginSuccessful ) {
        Principal principal = new ClientCertificatePrincipal( userName );
        principal.setAuthenticationMethod( Principal.AUTH_METHOD_CERTIFICATE ); //for 7_10

        subject.getPrincipals().add( principal );
        subject.getPublicCredentials().add( arrX509Cert );

        if ( bNameSetInSharedState ) {
          sharedState.put( AbstractLoginModule.PRINCIPAL, principal );
        }
      } else {
        userName = null;
        arrX509Cert = null;
      }

      return true;
    } else {
      bShouldBeIgnored = false;
      return false;
    }
  }

  public boolean abort() throws LoginException{
    if ( !bShouldBeIgnored ) {
      if ( bLoginSuccessful ) {
        userName = null;
        arrX509Cert = null;
        bLoginSuccessful = false;
      }

      return true;
    } else {
      bShouldBeIgnored = false;
      return false;
    }
  }

  public boolean logout() throws LoginException{
    if ( !bShouldBeIgnored ) {
      if ( bLoginSuccessful ) {
        userContext.emptySubject( subject );
        removeCredentials();
        bLoginSuccessful = false;
      }
      return true;
    } else {
      return false;
    }
  }

  private void removeCredentials(){
    try {
      Iterator publicCredentials = subject.getPublicCredentials( X509Certificate.class ).iterator();

      while ( publicCredentials.hasNext() ) {
        publicCredentials.next();
        publicCredentials.remove();
      }
    } catch ( Exception e ) {
      if ( location.beWarning() ) {
        location.traceThrowableT( Severity.WARNING, "Security credentials of class java.security.cert.X509Certificate cannot be removed from the subject on logout.", e );
      }
    }
  }

  /**
   * @return the username according to the rules passed to the login module. May return <code>null</code>.
   * @throws LoginException when using <code>wholeCert</code> mapping, and no user is bind to the given certificate.
   * @throws CodingException
   * @throws X509ExtensionInitException
   */
  protected UserInfo getUserNameFromCert( iaik.x509.X509Certificate[] clientChain ) throws LoginException, X509ExtensionInitException, CodingException, IllegalArgumentException {
  	UserInfo userInfo = null;
	  String user = null;
  	X509Certificate cert = clientChain[0];
  	
  	for ( int j = 0; j < rules.size(); j++ ) {
      RuleData rule = (RuleData) rules.get( j );
      if ( rule.isFilterPassedByCert( cert ) ) {
         // revocation check of the chain
        iaik.x509.X509Certificate revokedCert = null;
        try {
          revokedCert = checkForRevokedCertificates(clientChain);
        } catch (Exception e){
          SimpleLogger.traceThrowable(Severity.ERROR, location, "ASJ.secsrv.000163", "Certificate Revocation Check failed: ", e);
          throwUserLoginException(e, LoginExceptionDetails.CERTIFICATE_IS_NOT_TRUSTED);
        }
        if (revokedCert != null) {
          if (location.beDebug()) {
            location.debugT("Certificate [{0}] has been revoked by certificateRevocation profile {1}" , new Object[]{revokedCert, "ClientCertLoginModule"});
          }
          String certAsString = revokedCert.getSubjectDN() + " / " + revokedCert.getIssuerDN() + " / " + revokedCert.getSerialNumber(); 
          if (location.beInfo()) {
            location.infoT("Certificate [" + certAsString + "] has been revoked by certificateRevocation profile 'ClientCertLoginModule'.");
          }
          throw new BaseLoginException("Certificate has been revoked!", LoginExceptionDetails.CERTIFICATE_IS_NOT_TRUSTED, Severity.INFO);
        }

        if ( rule.getGetUserFrom().equalsIgnoreCase( RuleData.GETUSERFROM_VAL_SUBJECTNAME ) ) {
          user = RuleHelper.getUsernameFromSubject(cert, rule.getAttributeName() );
        }

        if ( rule.getGetUserFrom().equalsIgnoreCase( RuleData.GETUSERFROM_VAL_EXPERTMODE ) ) {
          user = RuleHelper.getUsernameFromExtension(clientChain[0].getExtension( rule.getOid() ), rule.getAttributeName() );
        }

        if ( rule.getGetUserFrom().equalsIgnoreCase( RuleData.GETUSERFROM_VAL_WHOLECERT ) ) {
          try {
            user = userContext.getUserInfo( cert ).getName();
          } catch ( SecurityException e ) {
            throwUserLoginException( e, LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE );
          }
        }

        location.debugT("Rule{0} {1} matched user = {2}", new Object[] {String.valueOf(j), rule, user});        
        if ( user != null ) {
          try {
            if ( rule.isLogonWithAlias() ) {
              location.debugT("User tries to authenticate with alias {0}", new Object[] {user});

              userInfo = userContext.getUserInfoByLogonAlias( user );
            } else {
              refreshUserInfo( user );
              userInfo = userContext.getUserInfo( user );
            }
          } catch ( SecurityException e ) {
            throwUserLoginException( e, LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE );
          }
        }

        break; //certificate is ok, username should have been successfully extracted
      }//if (cert passes rule filters)
    }//for (rule)
  	
  	location.debugT("UserInfo = \n{0}", new Object[] {userInfo});
  	return userInfo;
  }

  private static iaik.x509.X509Certificate[] convert(X509Certificate[] arrX509Cert) throws CertificateException {
    iaik.x509.X509Certificate[] res = new iaik.x509.X509Certificate[arrX509Cert.length];

    for ( int i = 0 ; i < arrX509Cert.length ; i++ ) {
      res[i] = new iaik.x509.X509Certificate(arrX509Cert[i].getEncoded());
    }

    return res;
  }

  private static iaik.x509.X509Certificate checkForRevokedCertificates(iaik.x509.X509Certificate[] clientChain) throws IllegalAccessException, InvocationTargetException {
    if (location.beDebug()) location.logT(Severity.DEBUG, " checkForRevokedCertificates {");

    if (!CertRevocServiceWrapper.isInitialized()) {
      // CertRevocation Service is not available for any reason - the check passes
      if (location.beDebug())location.logT(Severity.DEBUG, " checkForRevokedCertificates } ok (missing CertRevoc service)");
      return null;
    }

    for (int i = 0; i < clientChain.length; i++) {

      if (!CertRevocServiceWrapper.isValid("ClientCertLoginModule", clientChain[i])) {
        if (location.beDebug())location.logT(Severity.DEBUG, " checkForRevokedCertificates } ok (revoced cert found)");
        if (location.beDebug())location.logT(Severity.DEBUG, " [" + i + "] " + clientChain[i].getSubjectDN() + "  @@ " + clientChain[i].getIssuerDN() + "  REVOKED");
        return clientChain[i];
      } else {
        if (location.beDebug())location.logT(Severity.DEBUG, " [" + i + "] " + clientChain[i].getSubjectDN() + "  @@ " + clientChain[i].getIssuerDN() + "  OK");
      }
    }

    if (location.beDebug()) location.logT(Severity.DEBUG, " checkForRevokedCertificates } ok (all certs are OK)");
    return null;
  }

  public static int tryToRemoveOption( Map<Object, Object> options, String key ){
    int res = 0;
    Set<Object> set = options.keySet();
    Iterator<Object> it = set.iterator();
    while ( it.hasNext() ) {
      Object o = it.next();
      String s = "";
      if ( o instanceof String ) {
        s = (String) o;
      }
      if ( s.equalsIgnoreCase( key ) ) {
        it.remove();
        res++;
      }
    }
    return res;
  }

  public static int removeObsoleteOptions( Map<Object, Object> options ){
    int iRemoved = 0;
    iRemoved += tryToRemoveOption( options, "CertAuth" );
    iRemoved += tryToRemoveOption( options, "Subject" );
    iRemoved += tryToRemoveOption( options, "Issuer" );
    iRemoved += tryToRemoveOption( options, "SerialNumber" );
    iRemoved += tryToRemoveOption( options, "take.username.from.cn" );
    return iRemoved;
  }

  /**
   * @param options - a set of string options
   * @return true if a key <code>take.username.from.cn</code> exists and its corresponding value is <code>true</code>
   */
  public static boolean isTakeFromCN_ON( Map options ){
    boolean res = false;
    Set set = options.keySet();
    for (Object aSet : set) {
      Object value = options.get(aSet);
      String sKey = "";
      String sValue = "";
      if (aSet instanceof String) {
        sKey = (String) aSet;
      }
      if (value instanceof String) {
        sValue = (String) value;
      }

      if (sKey.equalsIgnoreCase("take.username.from.cn") && sValue.equalsIgnoreCase("true")) {
        res = true;
        break;
      }
    }
    return res;
  }
}



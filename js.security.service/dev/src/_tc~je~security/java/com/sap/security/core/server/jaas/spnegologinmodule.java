package com.sap.security.core.server.jaas;

import iaik.asn1.ASN;
import iaik.asn1.ASN1Object;
import iaik.asn1.CodingException;
import iaik.asn1.DerCoder;
import iaik.asn1.ObjectID;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Arrays;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletResponse;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.WebCallbackHandler;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSetterCallback;
import com.sap.security.api.UMException;
import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.security.core.server.jaas.spnego.SPNegoProtocolException;
import com.sap.security.core.server.jaas.spnego.SpNegoState;
import com.sap.security.core.server.jaas.spnego.asn1.BasicSpNegoType;
import com.sap.security.core.server.jaas.spnego.asn1.KerberosApReq;
import com.sap.security.core.server.jaas.spnego.asn1.KerberosTicket;
import com.sap.security.core.server.jaas.spnego.asn1.SPNegoToken;
import com.sap.security.core.server.jaas.spnego.asn1.SpNegoASN1;
import com.sap.security.core.server.jaas.spnego.asn1.SpNegoInit;
import com.sap.security.core.server.jaas.spnego.asn1.SpNegoTarg;
import com.sap.security.core.server.jaas.spnego.util.Base64;
import com.sap.security.core.server.jaas.spnego.util.ConfigurationHelper;
import com.sap.security.core.server.jaas.spnego.util.IpSessionCache;
import com.sap.security.core.server.jaas.spnego.util.ThreadTokenCache;
import com.sap.security.core.server.jaas.spnego.util.TokenStatus;
import com.sap.security.core.server.jaas.spnego.util.Utils;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This login module provides Kerberos authentication to the J2EE engine. It is
 * based on the JGSS API and the SPNego protocol (Simple and Protected
 * Negotiation protocol).
 * <hr>
 * The following parameters are supported: <table border="1">
 * <tr>
 * <td align="center" bgcolor="grey"><b>name </b></td>
 * <td align="center" bgcolor="grey"><b>Required/Optional </b></td>
 * <td align="center" bgcolor="grey"><b>Allowed values </b></td>
 * <td align="center" bgcolor="grey"><b>default </b></td>
 * <td align="center" bgcolor="grey"><b>description </b></td>
 * </tr>
 * <tr>
 * <td>org.ietf.jgss.name</td>
 * <td align="center" valign="center">R</td>
 * <td></td>
 * <td> Kerberos name of the J2EE Engine</td>
 * </tr>
 * <tr>
 * <td> org.ietf.jgss.name.type</td>
 * <td align="center" valign="center">O</td>
 * <td align="center" valign="center">0,1</td>
 * <td align="center" valign="center">1</td>
 * <td>Specifies whether the Kerberos name is a host based principal (e.g.
 * HTTP@hades.customer.de or a user name like in the examples of this document</td>
 * </tr>
 * <tr>
 * <td> org.ietf.jgss.mech</td>
 * <td align="center" valign="center">O</td>
 * <td></td>
 * <td> 1.2.840.113554.1.2.2</td>
 * <td>Mechanism. Specifies the GSS mechanism for the name parameter</td>
 * </tr>
 * <tr>
 * <td> org.ietf.jgss.supp_mechs</td>
 * <td align="center" valign="center">O</td>
 * <td></td>
 * <td> 1.2.840.113554.1.2.2, 1.2.840.48018.1.2.2</td>
 * <td>Supported SPNego mechanisms. Currently the 2 mechanisms are supported
 * (both are Kerberos)</td>
 * </tr>
 * <tr>
 * <td> com.sap.security.core.server.jaas.spnego.creds_in_thread</td>
 * <td align="center" valign="center">O</td>
 * <td align="center" valign="center">true,false</td>
 * <td align="center" valign="center">false</td>
 * <td>Specifies whether the first credential acquisistion takes place in a
 * separate thread. Recommended on SUN platforms.</td>
 * </tr>
 * <tr>
 * <td> com.sap.security.core.server.jaas.spnego.ttc.item.lifetime</td>
 * <td align="center" valign="center">O</td>
 * <td align="center" valign="center"></td>
 * <td align="center" valign="center">1000</td>
 * <td><b>Undocumented parameter </b>. Specifies the lifetime of an object in
 * the thread token cache (see
 * {@link com.sap.security.core.server.jaas.spnego.util.ThreadTokenCache}).
 * </td>
 * </tr>
 * <tr>
 * <td> com.sap.security.core.server.jaas.spnego.ttc.cleanup</td>
 * <td align="center" valign="center">O</td>
 * <td align="center" valign="center"></td>
 * <td align="center" valign="center">30000</td>
 * <td> <b>Undocumented parameter </b>. Specifies the duration between cleanups
 * of the thread token cache (see
 * {@link com.sap.security.core.server.jaas.spnego.util.ThreadTokenCache}).
 * </td>
 * </tr>
 * <tr>
 * <td> com.sap.security.core.server.jaas.spnego.isc.item.lifetime</td>
 * <td align="center" valign="center">O</td>
 * <td align="center" valign="center"></td>
 * <td align="center" valign="center">5000</td>
 * <td><b>Undocumented parameter </b>. Specifies the lifetime of an object in
 * the thread token cache (see
 * {@link com.sap.security.core.server.jaas.spnego.util.IpSessionCache}). </td>
 * </tr>
 * <tr>
 * <td> com.sap.security.core.server.jaas.spnego.isc.cleanup</td>
 * <td align="center" valign="center">O</td>
 * <td align="center" valign="center"></td>
 * <td align="center" valign="center">30000</td>
 * <td> <b>Undocumented parameter </b>. Specifies the duration between cleanups
 * of the thread token cache (see
 * {@link com.sap.security.core.server.jaas.spnego.util.IpSessionCache}). </td>
 * </tr>
 * </table> This javadoc should not replace the official documentation. However,
 * some general remark for the understanding of this code are necessary:
 * <ul>
 * <li>The study of the SPNego protocol is fundamental. Noone can understand
 * this code who's not familiar with the protocol and the ASN.1 structure of
 * SPNego tokens. Both is sufficiently described in a <a
 * href="http://msdn.microsoft.com/library/en-us/dnsecure/html/http-sso-1.asp">
 * document </a> provided by microsoft.
 * <li>In addition to the point above it is important to understand the role of
 * the GSS API and its java port, jgss, respectively. The Java binding for GSS
 * API (jgss) is described in RFC 2853. The GSS API is described in RFC 2743. A
 * short summary:
 * <p>
 * <small>GSS API is an interface for secure message exchange. It does not
 * define a protocol it is only an API that allows to provide a hook within an
 * application. This hook can be used afterwards by a GSS API implementation.
 * Different implementations may use different mechanisms/protocols for secure
 * message exchange. <br>
 * As of version 1.4 the Java Runtime supports the Java port of GSS (see the <a
 * href="#org.ietf.jgss.javadocs">javadocs of package org.ietf.jgss </a>). The
 * JGSS interface comes as a SPI and everybody can implement a provider for
 * this. Fortunately, all JDK providers come with a fully operational
 * implementation, which uses Kerberos as underlying security mechanism. For the
 * SUN JDK, we recommend the documents <a href="#jaas_and_gssapi">[2] </a> and
 * <a href="#sso_with_kerberos_java">[3] </a>. <a href="#jaas_and_gssapi">[2]
 * </a> provides a good introduction and entry point for the entire topic.
 * <p>
 * The important remark in our context is that the jgss interface completely
 * hides all Kerberos specifics. We never directly deal with Kerberos principal
 * names, for instance, but always with GSS names. </small>
 * <p>
 * <b>References: </b>
 * <ol>
 * <li><a name="org.ietf.jgss.javadocs" /> <a
 * href="http://java.sun.com/j2se/1.4.2/docs/api/org/ietf/jgss/package-summary.html">
 * javadocs package org.ietf.jgss </a>
 * <li><a name="jaas_and_gssapi" /> <a
 * href="http://java.sun.com/j2se/1.4.2/docs/guide/security/jgss/tutorials/index.html">
 * Introduction to JAAS and GSS-API </a>
 * <li><a name="sso_with_kerberos_java" /> <a
 * href="http://java.sun.com/j2se/1.4.2/docs/guide/security/jgss/single-signon.html">
 * SSO with Kerberos in Java </a>
 * </ol>
 * <li>After having understood the fundamentals of GSS-API we must focus on the
 * SPNego protocol. The only purpose of SPNego is to use GSS-API generated
 * tokens for authentication and/or more but to enhance this with a way to
 * negotiate a mechanism first. Once the negotiation is finished, SPNego only
 * wraps tokens generated by GSS-API calls.
 * <li>This implementation contains some specifics that the reader must know to
 * understand the code:
 * <ol>
 * <li>The login module contains a so called &quot;ThreadTokenCache&quot;. The
 * purpose of this cache is to avoid that SPNego tokens are processed twice in
 * one request. This can happen because in certain scenarios the portal calls
 * first IAuthentication.getLoggedInUser() and then ILogonAuthenticator.login().
 * Both calls result in a processing of the login module stack. In this
 * situation the SPNegoLoginModule would be called twice with the same token.
 * Here the cache avoids Kerberos to detect a replay attack and to destroy the
 * context. For more information about the ThreadTokenCache check out the
 * documentation of
 * {@link com.sap.security.core.server.jaas.spnego.util.ThreadTokenCache}.
 * <li>There is another cache, the so called
 * {@link com.sap.security.core.server.jaas.spnego.util.IpSessionCache}. It
 * caches GSSContext objects (we need to keep this object during the entire
 * negotiation). We could have used the http session but it didn't work well
 * (the context got lost all the time). Again, the javadoc of
 * {@link com.sap.security.core.server.jaas.spnego.util.IpSessionCache} contains
 * information about the specifics of the class and how it works.
 * <li>The
 * {@link com.sap.security.core.server.jaas.spnego.util.ConfigurationHelper}
 * class is the proxy of the LoginModule to its configuration. A new instance of
 * ConfigurationHelper is instantiated every time the LoginModule is called. In
 * a static variable the ConfigurationHelper stores the GSSCredential object (if
 * successfully acquired) and a hash value of the LoginModule options. In case
 * the configuration changed (indicated by a different hash value) the
 * credentials are reacquired. In addition, the LoginModule can query whether
 * the properties have changed. By this mechanism the LoginModule can change the
 * cache parameters on the fly. By consequence, LoginModule option changes do
 * not require restart.
 * </ol>
 * </ul>
 * Other important points:
 * <ul>
 * <li>Here are some more helpful resources:
 * <ol>
 * <li> <a name="ibm_devworks_1"> <a
 * href="http://www-106.ibm.com/developerworks/forums/dw_thread.jsp?forum=178&thread=71011&cat=10">
 * JGSS Exception text included in gss token </a>
 * <li><a name="ibm_devworks_2"> <a
 * href="http://www-106.ibm.com/developerworks/forums/dw_thread.jsp?forum=178&thread=69812&cat=10">
 * Cryptographic key type des-cbc-md5 not found </a> (one of my posts at IBM
 * developerWorks)
 * <li><a name="msdn_ms_commitment"> <a
 * href="http://support.microsoft.com/default.aspx?scid=kb;en-us;308074"> The
 * POST Method Does Not Work If You Are Using Kerberos Authentication </a>
 * <li><a name="msdn_kerberos_resources"> <a
 * href="http://www.microsoft.com/windows2000/technologies/security/kerberos/default.asp">
 * Microsoft Windows 2000 Kerberos Resources </a>, a useful collection of MS
 * Kerberos articles
 * <li><a name="msdn_ms_commitment"> <a
 * href="http://www.mozilla.org/projects/netlib/integrated-auth.html">
 * Integrated Authentication with Mozilla </a>, interesting article how to use
 * Windows integrated authentication with the Mozilla browser
 * </ol>
 * http://www.microsoft.com/windows2000/technologies/security/kerberos/default.asp
 * <li>The IBM implementation doesn't throw a GSSException in
 * {@link org.ietf.jgss.GSSContext#acceptSecContext(byte[],int,int)} in case of
 * a replay attack, for instance. Instead, a return gss token is created that
 * contains the exception text. This is unfortunate but apparently unavoidable.
 * IBM's comment on this can be found at <a href="#ibm_devworks_1">[1] </a>
 * <li>There is a major problem with the valid scenario of a password fallback
 * in case of unsuccessful Kerberos authentication. Microsoft has committed that
 * they have a bug in Internet Explorer. I have experienced this bug also with
 * IE 5.5, not only with IE 6.0. See MSDN article <a
 * href="#msdn_ms_commitment">[3] </a>.
 * </ul>
 */
public class SPNegoLoginModule implements LoginModule {

  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_SPNEGO_LOCATION + ".SPNegoLoginModule");
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);

  private static ThreadTokenCache threadTokenCache = null;
  private static IpSessionCache ipSessionCache = null;
  private ConfigurationHelper configHelper = null;

  // JAAS fields
  private CallbackHandler callbackHandler = null;
  private Map sharedState = null;
  private Subject subject = null;

  private GSSCredential delegatedCredentials = null;

  // user resolution fields
  private String resolutionMode = null;
  private String resolutionAttribute = null;
  private String resolutionDNAttribute = null;

  private String negotiationResponse = null;

  private int currentNegState = IConstants.SPNEGO_NEG_ACCEPT_INITIAL;

  /**
   * When 401 with a challenge header is sent back to the client this module
   * throws a LoginException. In this case we want to keep the context objects
   * stored in the cache. We then set this parameter to false. Then the internal
   * cache state is not cleared in the abort () call.
   */
  private boolean mustAbort = true;

  /**
   * @see IConstants#CONF_SET_HTTP_STATUSCODE_ON_FAIL
   */
  private String kerberosPrincipalName = null;
  private String userName = null;
  private Set principals = new HashSet();

  static {
    ASN.register(new ASN(0x0, "SPNegoToken", ASN.APPLICATION), SpNegoASN1.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#login()
   */

  public boolean login() throws LoginException {

    Object spnegoStateObject = this.getSessionAttribute(IConstants.SPNEGO_SESSION);
    SpNegoState spnegoState = null;
    String headerReceived = this.getHeader(IConstants.AUTH_HEADER_NAME);

    if (spnegoStateObject == null) {
      // set negstate = INITIAL in constructor
      spnegoState = new SpNegoState();
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Creating new instance of SpNegoState (" + spnegoState + ")");
      }
      this.setSessionAttribute(IConstants.SPNEGO_SESSION, spnegoState);
    } else {
      spnegoState = (SpNegoState) spnegoStateObject;
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Received instance of SpNegoState (" + spnegoState + ")");
      }
    }

    currentNegState = spnegoState.negstate;

    if (spnegoState.negstate == IConstants.SPNEGO_NEG_ACCEPT_INITIAL || spnegoState.negstate == IConstants.SPNEGO_NEG_ACCEPT_INCOMPLETE) {
      if (headerReceived == null) {
        String[] realms = configHelper.getConfiguredRealms();
        if (realms != null && realms.length != 0) {
          boolean areCredentialsAcquired = false;
          // credentialsAcquired will be set to true if GSS credentials are
          // acquired for at least one Kerberos realm
          for (int i = 0; i < realms.length; i++) {
            String realm = realms[i];
            try {
              if (LOCATION.beDebug()) {
                LOCATION.debugT("Acquiring credentials for realm " + realm);
              }
              GSSCredential credentials = configHelper.getCredentials(realm);
              if (LOCATION.beDebug()) {
                LOCATION.debugT("Credentials for realm " + realm + " successfully acquired: " + credentials.getName());
              }
              areCredentialsAcquired = true;
            } catch (Exception e) {
              traceThrowableT(Severity.ERROR, "Acquiring credentials for realm " + realm + " failed", e);
            }
          }
          if (!areCredentialsAcquired) {
            throw new DetailedLoginException("Unable to acquire GSS credentials for at least one Kerberos realm", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
          }
        } else {
          if (LOCATION.beError()) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000131", "SPNegoLoginModule is not configured to accept credentials from any Kerberos realm. Check option '{0}' exists and its value is valid.", new Object[]{IConstants.CONF_GSS_NAME});
          }
          return false;
        }
        this.setSessionAttribute(IConstants.SPNEGO_SESSION, spnegoState);
        // Don't destroy the session in this.abort()
        // since we want to keep the session
        mustAbort = false;
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Access Denied - responseHeader is NULL");
        }
        // finish by throwing LoginException
        throw new DetailedLoginException("Access Denied. No authorization header received.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
      }
      try {

        if (!headerReceived.startsWith(IConstants.NEGOTIATE + " ")) {
          if (LOCATION.beWarning()) {
            SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000132", "Unknown authorization header received during SPNego authentication:{0}. The header is expected to start with:{1}", new Object[]{headerReceived, IConstants.NEGOTIATE});
          }
          throw new DetailedLoginException("Unknown authorization header.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
        }
        String spnegoTokenString = headerReceived.substring(IConstants.NEGOTIATE.length()).trim();
        // Check if token has already been processed by this thread.
        synchronized (threadTokenCache) {
          TokenStatus tokenStatus = threadTokenCache.containsToken(spnegoTokenString);
          if (null != tokenStatus) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("SPNegoToken = " + spnegoTokenString + " found in ThreadCache and was " + (tokenStatus.isAuthenticated ? "" : "NOT") + " authenticated.");
            }

            // If token has already been processed
            // by this thread before then
            // we set the user name and return true
            if (tokenStatus.isAuthenticated) {
              // use userName and kerberosPrincipalName from thread cache
              userName = tokenStatus.userName;
              kerberosPrincipalName = tokenStatus.kerberosPrincipalName;
              if (LOCATION.beDebug()) {
                LOCATION.debugT("UserName = " + userName + " and KerberosPrincipalName = " + kerberosPrincipalName + " found in ThreadCache");
              }
              sharedState.put(IConstants.LOGIN_NAME, userName);
              return true;
            } else {
              return false;
            }
          }
        }

        String handshakeResult = doHandshake(spnegoState, spnegoTokenString);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("handshake result is " + handshakeResult);
        }

        if (handshakeResult != null) {
          // Means that handshake is not finished.
          setSessionAttribute(IConstants.SPNEGO_SESSION, spnegoState);

          this.negotiationResponse = handshakeResult;
          // Don't destroy the session in this.abort()
          // since we want to keep the session
          mustAbort = false;

          if (LOCATION.beDebug()) {
            LOCATION.debugT("Access Denied - responseHeader is set to " + headerReceived);
          }
          // finish by throwing LoginException
          throw new DetailedLoginException("Access Denied.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
        }
      } catch (SPNegoProtocolException spnegoProtocolException) {
        if (LOCATION.beWarning()) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000133", "Authentication failed. Error during handshake. Check the trace file for details.");
        }
        traceThrowableT(Severity.WARNING, "Error during handshake.", spnegoProtocolException);

        // ??? this must be revised
        // negstate is modified but spnego state is not updated into httpsession
        spnegoState.negstate = IConstants.SPNEGO_NEG_REJECTED;

        throw spnegoProtocolException;
      }
    } else {
      // state == REJECTED || state == COMPLETED 
      return false;
    }
    return true;
  }

  void checkAuthorizationHeaderToken(byte[] token) throws SPNegoProtocolException {
    final String NTLM_SSP = "NTLMSSP";
    int byteCount = NTLM_SSP.getBytes().length;
    if (token.length > byteCount) {
      String tokenString = new String(token, 0, byteCount);
      if (tokenString.equals(NTLM_SSP)) {
        if (LOCATION.beWarning()) {
          LOCATION.warningT("NTLM token found in authorization header during SPNego authentication.");
        }
        throw new SPNegoProtocolException("NTLM token received in authorization header.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#logout()
   */
  public boolean logout() throws LoginException {
    SpNegoState state = (SpNegoState) this.getSessionAttribute(IConstants.SPNEGO_SESSION);
    if (state != null) {
      release(state);
      this.setSessionAttribute(IConstants.SPNEGO_SESSION, null);
    }
    rollBack();
    return true;
  }

  public void checkLoginModuleOptions(Map options) {
    Set entries = options.entrySet();
    Iterator iterator = entries.iterator();
    while (iterator.hasNext()) {
      Map.Entry option = (Map.Entry) iterator.next();
      if (option.getKey() instanceof String && option.getValue() instanceof String) {
        String key = (String) option.getKey();
        String val = (String) option.getValue();

        if (key.equals(IConstants.CONF_SET_HTTP_STATUSCODE_ON_FAIL)) {
          if (!isBooleanValue(val)) {
            if (LOCATION.beError()) {
              SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000134", "Option {0} has invalid value. Value = {1}. The allowed values are true and false.", new Object[]{IConstants.CONF_SET_HTTP_STATUSCODE_ON_FAIL, val});
            }
            throw new IllegalArgumentException("Invalid login module option.");
          }
        } else if (key.equals(IConstants.CONF_GSS_NAME)) {
          // can be anything
        } else if (key.equals(IConstants.CONF_GSS_NAME_TYPE)) {
          if (!val.equals("0") && !val.equals("1")) {
            if (LOCATION.beError()) {
              SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000135", "Option {0} has invalid value. Value = {1} The allowed values are 0 and 1.", new Object[]{IConstants.CONF_GSS_NAME_TYPE, val});
            }
            throw new IllegalArgumentException("Invalid login module option.");
          }
        } else if (key.equals(IConstants.CONF_GSS_MECH)) {
          if (!val.equals("1.2.840.48018.1.2.2") && !val.equals("1.2.840.113554.1.2.2")) {
            if (LOCATION.beError()) {
              SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000136", "Option {0} has invalid value. Value = {1}. The allowed values are 1.2.840.48018.1.2.2 and 1.2.840.113554.1.2.2.", new Object[]{IConstants.CONF_GSS_MECH, val});
            }
            throw new IllegalArgumentException("Invalid login module option.");
          }
        } else if (key.equals(IConstants.CONF_SUPPORTED_MECHS)) {
          // not decided
        } else if (key.equals(IConstants.CONF_UID_RESOLUTION_MODE)) {

          List<String> resolutionModesList = Arrays.asList(IConstants.RESOLUTION_MODES);
          if (!resolutionModesList.contains(val)) {
            if (LOCATION.beError()) {
              SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000137", "Option {0} has invalid value = {1}. The allowed values are {3}.", new Object[]{IConstants.CONF_UID_RESOLUTION_MODE, val, resolutionModesList.toString()});
            }
            throw new IllegalArgumentException("Invalid login module option.");
          }
        } else if (key.equals(IConstants.CONF_UID_RESOLUTION_ATTR)) {
          // can be anything
        } else if (key.equals(IConstants.CONF_UID_RESOLUTION_DN)) {
          // can be anything
        } else if (key.equals(IConstants.CONF_CREDS_IN_THREAD)) {
          if (!isBooleanValue(val)) {
            if (LOCATION.beError()) {
              SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000138", "Option {0} has invalid value. Value = {1}. The allowed values are true and false.", new Object[]{IConstants.CONF_CREDS_IN_THREAD, val});
            }
            throw new IllegalArgumentException("Invalid login module option.");
          }
        } else {
          // Unknown login module option
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
   *      javax.security.auth.callback.CallbackHandler, java.util.Map,
   *      java.util.Map)
   */
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {

    Properties properties = new Properties();
    int threadTokenCacheCleanup = -1;
    int threadTokenCacheItemLifeTime = -1;

    int ipSessionCleanup = -1;
    int ipSessionCacheItemLifeTime = -1;

    properties.putAll(options);

    try {

      checkLoginModuleOptions(options);

      // read parameters for the various cache implementations
      threadTokenCacheCleanup = Integer.parseInt(properties.getProperty(IConstants.CONF_TTC_CLEANUP, IConstants.DEFAULT_TTC_CLEANUP));
      threadTokenCacheItemLifeTime = Integer.parseInt(properties.getProperty(IConstants.CONF_TTC_ITEM_LIFETIME, IConstants.DEFAULT_TTC_ITEM_LIFETIME));
      ipSessionCleanup = Integer.parseInt(properties.getProperty(IConstants.CONF_ISC_CLEANUP, IConstants.DEFAULT_ISC_CLEANUP));
      ipSessionCacheItemLifeTime = Integer.parseInt(properties.getProperty(IConstants.CONF_ISC_ITEM_LIFETIME, IConstants.DEFAULT_ISC_ITEM_LIFETIME));
      resolutionMode = properties.getProperty(IConstants.CONF_UID_RESOLUTION_MODE, IConstants.DEFAULT_UID_RESOLUTION_MODE);
      // Resolve default for com.sap.spnego.uid.resolution.attr based
      // on value of com.sap.spnego.uid.resolution.mode
      resolutionAttribute = properties.getProperty(IConstants.CONF_UID_RESOLUTION_ATTR);
      if (resolutionAttribute == null) {
        if (IConstants.UID_RESOLUTION_MODE_SIMPLE.equals(resolutionMode)) {
          resolutionAttribute = IConstants.DEFAULT_UID_RESOLUTION_ATTR_KRB5;
        } else if (IConstants.UID_RESOLUTION_MODE_PREFIXBASED.equals(resolutionMode)) {
          resolutionAttribute = IConstants.DEFAULT_UID_RESOLUTION_ATTR_UN;
        } else if (IConstants.UID_RESOLUTION_MODE_NONE.equals(resolutionMode)) {
          // do nothing. _uid_res_attr is not needed
        } else if (IConstants.UID_RESOLUTION_MODE_KPNBASED.equals(resolutionMode)) {
          // do nothing. _uid_res_attr is not needed
        } else {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Invalid resolution mode option value.");
          }
        }
      }

      // the DN attribute is only needed if all
      // of the following requirements are met:
      // o The datasource is a multi domain windows environment
      // o the samaccountname is not unique throughout the forest
      // 
      // The dn attribute is not maintained in UME by default. It
      // must be created and mapped to the distinguishedName in ADS
      resolutionDNAttribute = properties.getProperty(IConstants.CONF_UID_RESOLUTION_DN, IConstants.DEFAULT_UID_RESOLUTION_DN);
      synchronized (SPNegoLoginModule.class) {
        if (threadTokenCache == null) {
          threadTokenCache = new ThreadTokenCache(threadTokenCacheCleanup, threadTokenCacheItemLifeTime);
        }
        if (ipSessionCache == null) {
          ipSessionCache = new IpSessionCache(ipSessionCleanup, ipSessionCacheItemLifeTime);
        }
      }
      // Before we instantiate the ConfigurationHelper, we check
      // whether
      // the properties have changed (only before the new
      // instantiation
      // takes place)
      if (ConfigurationHelper.havePropsChanged(properties)) {
        threadTokenCache.resetParameters(threadTokenCacheCleanup, threadTokenCacheItemLifeTime);
        ipSessionCache.resetParameters(ipSessionCleanup, ipSessionCacheItemLifeTime);
      }
      configHelper = new ConfigurationHelper(properties);
    } catch (IllegalArgumentException e) {
      traceThrowableT(Severity.ERROR, "Configuration error in SPNegoLoginModule", e);
      throw e;
    } catch (GSSException e) {
      traceThrowableT(Severity.ERROR, "Exception in SPNegologinModule.initialize.", e);

      RuntimeException runtimeException = new RuntimeException(e.getMessage());
      runtimeException.fillInStackTrace();
      throw runtimeException;
    }
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.subject = subject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#commit()
   */
  public boolean commit() throws LoginException {
    boolean result = true;
    SpNegoState state = (SpNegoState) this.getSessionAttribute(IConstants.SPNEGO_SESSION);
    if (userName == null) {
      result = false;
    } else {
      // set principals in subject

      Principal p = new com.sap.engine.lib.security.Principal(userName);
      principals.add(p);

      p = createKerberosPrincipal(kerberosPrincipalName);
      principals.add(p);

      subject.getPrincipals().addAll(principals);

      if (delegatedCredentials != null) {
        subject.getPrivateCredentials().add(delegatedCredentials);
      }
    }
    release(state);
    this.setSessionAttribute(IConstants.SPNEGO_SESSION, null);
    threadTokenCache.cleanup();
    ipSessionCache.cleanup();
    return result;
  }

  /**
   * Create and return javax.security.auth.kerberos.KerberosPrincipal.
   * Reflection is used because 6.40 uses jdk1.3. We need totally equal sources
   * for all releases, so methods in both 6.40 and 7.10 use reflection,
   * regardless of the fact 7.10 runs java 1.5
   * 
   * @param kpn
   * @return (Principal) javax.security.auth.kerberos.KerberosPrincipal or
   *         <code>null</code> on error.
   */
  private java.security.Principal createKerberosPrincipal(String kpn) {
    Principal principal = null;
    String method = "SPNegoLoginModule.createKerberosPrincipal";
    if (null != kpn) {
      String classname = "javax.security.auth.kerberos.KerberosPrincipal";
      try {
        Class krbprinc = Class.forName(classname);
        Constructor constr = krbprinc.getConstructor(new Class[] { String.class });
        principal = (java.security.Principal) constr.newInstance(new Object[] { kpn });

      } catch (Exception e) {
        traceThrowableT(Severity.ERROR, method + " - Cannot add a " + classname + " to Subject.", e);
      }
    } else {
      if (LOCATION.beError()) {
        LOCATION.errorT(method + " - No KPN found!");
      }
    }
    return principal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#abort()
   */
  public boolean abort() throws LoginException {
    if (!mustAbort) {

      if (LOCATION.beDebug()) {
        LOCATION.debugT("set Response Status " + HttpServletResponse.SC_UNAUTHORIZED);
      }
      setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      String headerToSend = IConstants.NEGOTIATE;

      if (negotiationResponse != null) {
        headerToSend += " " + negotiationResponse;
      }

      setHeader(IConstants.WWW_AUTHENTICATE_NAME, headerToSend);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("set Header " + IConstants.WWW_AUTHENTICATE_NAME + " = " + headerToSend);
      }

      mustAbort = true;

    } else {

      //TODO:
      // This variable is used to provide fallback mechanism during spnego auhtnetication in case 
      // old implementation of the PortalSecurityHandler class is used - before consolidation of callbackhandlers.
      // It should be removed with all relevant checks after the new version of PortalSecurityHandler is submitted.
      boolean isOldPortalSecurityHandler = false;
      
      if ((callbackHandler instanceof WebCallbackHandler) && (callbackHandler.getClass() != WebCallbackHandler.class)) {
        isOldPortalSecurityHandler = true;
      }
      
      if (isOldPortalSecurityHandler) {
        setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }

      // remove the session attributes from the session.
      SpNegoState state = (SpNegoState) this.getSessionAttribute(IConstants.SPNEGO_SESSION);
      LOCATION.debugT("SpnegoState during abort: " + state.toString());
      if (state != null) {
        if (state.negstate == IConstants.SPNEGO_NEG_ACCEPT_COMPLETED) {
          //Password change dialog or need more information from the user
          state.negstate = currentNegState;
          if (isOldPortalSecurityHandler) {
            setStatus(HttpServletResponse.SC_OK);
          }
        } else {
          release(state);
          this.setSessionAttribute(IConstants.SPNEGO_SESSION, null);
        }
      }
      rollBack();
    }
    return true;
  }

  // ////////////////////////////////////////////////////////////////////
  //
  // H E L P E R F U N C T I O N S
  // 
  // ////////////////////////////////////////////////////////////////////

  /**
   * @param This is the current negotiation status and the current gss context.
   *        [in/out]
   * @param the "Negotiate" header. This should be a base64 encoded SPNego
   *        token.
   * @return the response SPNego token or null in case of failure or completion.
   *         The current GSS context will be fed with the token from the header.
   *         If the result of gsscontext.acceptSecurityContext() is
   *         gss_incomplete, then the response token will be wrapped as SPNego
   *         token, base64 encoded and returned. Otherwise, state.negresult is
   *         set appropriately and null is returned.
   */
  private String doHandshake(SpNegoState state, String headerReceived) throws SPNegoProtocolException {
    byte token[] = null;
    byte gssintoken[] = null; // the token that we put into gss_accept_context
    byte gssouttoken[] = null; // the token that gss_accept_context returns

    String responseHeader = null;
    ASN1Object asn1 = null;
    SPNegoToken spnegoToken = null;
    SpNegoInit spnegoInit = null;
    SpNegoTarg spnegoTarg = null;
    BasicSpNegoType basicSPNegoType = null;

    int supportedMechanismIndex = -1;
    // Do base 64 decoding and after that...
    token = Base64.decode(headerReceived);
    // check for NTLM token
    checkAuthorizationHeaderToken(token);

    try {
      // ... the ASN.1 decoding.
      asn1 = DerCoder.decode(token);
      if (state.negstate == IConstants.SPNEGO_NEG_ACCEPT_INITIAL) {
        // we have a complete SPNego token with SPNEGO Wrapper
        spnegoToken = new SPNegoToken();
        spnegoToken.decode(asn1);
        basicSPNegoType = spnegoToken.getSpnego();
        if (!(basicSPNegoType instanceof BasicSpNegoType)) {
          if (LOCATION.beError()) {
            LOCATION.errorT("Token is class type " + basicSPNegoType.getClass().getName() + ". The expected token type is " + BasicSpNegoType.class.getName());
          }
          throw new SPNegoProtocolException("Init token expected at this time.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
        }
        spnegoInit = (SpNegoInit) basicSPNegoType;
        // trace init token
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Received an SPNego Init token:\n" + spnegoInit);
        }
        //
        // Check whether OIDs and stuff is correct
        // and store the used mech in a variable
        //
        supportedMechanismIndex = this.getSupportedMechanismIndex(spnegoInit.getMechTypeList());
        if (supportedMechanismIndex == -1) {
          // No supported mechanism available. We
          // must stop the handshake.
          if (LOCATION.beError()) {
            LOCATION.logT(Severity.ERROR, "ASJ.secsrv.000139", "No supported mechanism found. Supported mechanisms are Kerberos V5 and Kerberos V5 Legacy.");
          }
          throw new SPNegoProtocolException("No supported mechanism found.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
        } else {
          state.mechanism = spnegoInit.getMechTypeList()[supportedMechanismIndex];
          if (LOCATION.beInfo()) {
            LOCATION.infoT("Supported mechanism found: " + state.mechanism.getID());
          }
        }

        gssintoken = spnegoInit.getMechToken();
        KerberosApReq kerberosApReq = new KerberosApReq(gssintoken);
        KerberosTicket kerberosTicket = kerberosApReq.getTicket();
        if (LOCATION.beDebug()) {
          LOCATION.debugT("SPNego Init token contains : " + kerberosApReq);
        }

        String realm = kerberosTicket.getRealm();
        String principalName = kerberosTicket.getPrincipalName();
        if (LOCATION.beInfo()) {
          LOCATION.infoT("SPNego Init token contains realm: " + realm);
          LOCATION.infoT("SPNego Init token contains servicePrincipalName: " + principalName);
        }

        String strToken = Utils.dumpIntoString(gssintoken, 0, gssintoken.length);
        if (LOCATION.beInfo()) {
          LOCATION.infoT("SPNego Init token contains mech token: \n" + strToken);
        }

        try {
          GSSCredential creds = configHelper.getCredentials(realm);
          GSSManager gssman = GSSManager.getInstance();
          GSSContext ctx = gssman.createContext(creds);
          state.gsscontext = ctx;
          if (LOCATION.beDebug()) {
            LOCATION.debugT("GSS Context created");
          }

          if (supportedMechanismIndex == 0) {
            gssouttoken = state.gsscontext.acceptSecContext(gssintoken, 0, gssintoken.length);
          } else {
            // Here, we have found a mechanism, but it is not the one
            // prefferred by the client, so we don't send back a token
            if (LOCATION.beInfo()) {
              LOCATION.infoT("Accepted mechanism " + state.mechanism.getID() + " is not the default mechanism. Will not send back a token.");
            }
          }

        } catch (GSSException gsse) {
          traceThrowableT(Severity.ERROR, "CreateContext failed: " + gsse, gsse);
          throw new SPNegoProtocolException(gsse.getMessage(), DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
        }

      } else if (state.negstate == IConstants.SPNEGO_NEG_ACCEPT_INCOMPLETE) {
        // Now the token is probably only a Targ Token (without the
        // gss header). see
        // http://msdn.microsoft.com/library/en-us/dnsecure/html/http-sso-2.asp?fram=true
        // for details.
        spnegoTarg = new SpNegoTarg();
        spnegoTarg.decode(asn1);
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Received SPNego Targ token:\n" + spnegoTarg.toString());
        }
        gssintoken = spnegoTarg.getResponseToken();
        if (LOCATION.beInfo()) {
          LOCATION.infoT("gss token received: \n" + Utils.dumpIntoString(gssintoken, 0, gssintoken.length));
        }
        // check if state ok
        if (state.gsscontext == null) {
          if (LOCATION.beError()) {
            LOCATION.errorT("Inconsistent state! negstate = SPNEGO_NEG_ACCEPT_INCOMPLETE and gsscontext = null.");
          }
          throw new SPNegoProtocolException("Internal error occured.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
        }
        try {
          gssouttoken = state.gsscontext.acceptSecContext(gssintoken, 0, gssintoken.length);
        } catch (GSSException gsse) {
          traceThrowableT(Severity.ERROR, "accept context failed: " + gsse, gsse);
          throw new SPNegoProtocolException("Context establishment failed.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
        }
      }
      if (gssouttoken != null) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("GSS out token generated: \n" + Utils.dumpIntoString(gssouttoken, 0, gssouttoken.length));
        }
      }
      if (state.gsscontext != null && state.gsscontext.isEstablished()) {
        state.negstate = IConstants.SPNEGO_NEG_ACCEPT_COMPLETED;
        if (LOCATION.beInfo()) {
          LOCATION.infoT("gss context established.");
        }
        this.setGSSInfo(state);
        synchronized (threadTokenCache) {
          threadTokenCache.put(headerReceived, userName, kerberosPrincipalName);
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("gss context negociation continues.");
        }
        state.negstate = IConstants.SPNEGO_NEG_ACCEPT_INCOMPLETE;
        if (LOCATION.beDebug()) {
          LOCATION.debugT("SpNegoState set into HTTP session: " + state);
        }
        spnegoTarg = new SpNegoTarg();
        spnegoTarg.setNegResult(IConstants.SPNEGO_NEG_ACCEPT_INCOMPLETE);
        spnegoTarg.setSupportedMech(state.mechanism);
        // in case gssouttoken is null, we just send a Targ token back
        // to tell IE which mechanism we intend to support.
        if (gssouttoken != null) {
          spnegoTarg.setResponseToken(gssouttoken);
        }
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Created SPNego Targ token:\n" + spnegoTarg);
        }
        responseHeader = Base64.encode(DerCoder.encode(spnegoTarg.toASN1Object()));
        synchronized (threadTokenCache) {
          threadTokenCache.put(headerReceived, null, null);
        }
      }
    } catch (CodingException e) {
      traceThrowableT(Severity.ERROR, "Decoding error in parsing of spnego token.", e);
      throw new SPNegoProtocolException("Unexpected decoding error.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
    }
    return responseHeader;
  }

  /**
   * Stores the src name into the state and the GSS Credentials of the client if
   * available
   * 
   * @param state
   */
  private void setGSSInfo(SpNegoState state) throws SPNegoProtocolException {
    GSSName peer = null;
    try {
      peer = state.gsscontext.getSrcName();
      peer = peer.canonicalize(new Oid(IConstants.OID_JGSS_MECHTYPE));
      if (state.gsscontext.getCredDelegState()) {
        delegatedCredentials = state.gsscontext.getDelegCred();
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Credentials of " + peer + " can be delegated: " + delegatedCredentials);
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Credentials of " + peer + " cannot be delegated.");
        }
      }
    } catch (GSSException e) {
      traceThrowableT(Severity.ERROR, "Problem with name computation.", e);
      throw new SPNegoProtocolException("Problem with name computation.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
    }
    kerberosPrincipalName = peer.toString();
    if (LOCATION.beInfo()) {
      LOCATION.infoT("SPNego authentication succeeded. Authenticated KPN is " + kerberosPrincipalName);
      LOCATION.infoT("Resolution mode is " + resolutionMode);
    }

    try {
      userName = Utils.getLogonUidForKPN(kerberosPrincipalName, resolutionMode, resolutionAttribute, resolutionDNAttribute);
    } catch (UMException exc) {
      traceThrowableT(Severity.ERROR, "Problem with resolving the user.", exc);
      String message = "Could not find user by KPN=" + kerberosPrincipalName + ".";
      if (LOCATION.beError()) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000140", "Could not find user by KPN={0}. Check user attributes in the useradmin.", new Object[]{kerberosPrincipalName});
      }
      throw new SPNegoProtocolException(message, DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
    }

    if (null != userName) {
      sharedState.put(IConstants.LOGIN_NAME, userName);
    }

  }

  /**
   * Generic method to query information from the http request.
   * 
   * @param string 
   *        name of the parameter
   * @param type 
   *        one of
   *        <ul>
   *        <li>HttpCallback.CLIENT_IP
   *        <li> HttpCallback.HEADER
   *        <li>HttpCallback.SESSION_ATTRIBUTE
   *        <li>HttpCallback.METHOD_TYPE
   *        <li> HttpCallback.RESPONSE_CODE
   *        <li>HttpCallback.SET_HEADER
   *        </ul>
   * @return
   */
  private Object getGeneric(String string, byte type) throws LoginException {
    Object object = null;
    HttpGetterCallback hgc = new HttpGetterCallback();
    hgc.setType(type);
    hgc.setName(string);
    try {
      callbackHandler.handle(new Callback[] { hgc });
    } catch (IOException e) {
      traceThrowableT(Severity.ERROR, "Error during attempt to read object, type " + type + " name " + string, e);
      throw new LoginException("Error in SPNegoLoginModule.");
    } catch (UnsupportedCallbackException e) {
      traceThrowableT(Severity.ERROR, "Error during attempt to read object, type " + type + " name " + string, e);
      throw new LoginException("Error in SPNegoLoginModule.");
    }
    object = hgc.getValue();
    return object;
  }

  /**
   * Calls getGeneric (header, HttpCallback.HEADER);
   * 
   * @param header
   * @return the http header variable
   * @throws LoginException
   */
  private String getHeader(String header) throws LoginException {
    return (String) this.getGeneric(header, HttpCallback.HEADER);
  }

  /**
   * @param b
   * @return
   */
  private Object getSessionAttribute(String key) throws LoginException {
    return this.getGeneric(key, HttpCallback.SESSION_ATTRIBUTE);
  }

  /**
   * @param key
   * @param value
   * @param type
   * @throws LoginException
   */
  private void setGeneric(String key, Object value, byte type) throws LoginException {
    if (key != null && key.equals(IConstants.SPNEGO_SESSION) && value == null && type == HttpCallback.SESSION_ATTRIBUTE) {
      return;
    }

    HttpSetterCallback hgc = new HttpSetterCallback();
    hgc.setType(type);
    if (key != null) {
      hgc.setName(key);
    }
    if (value != null) {
      hgc.setValue(value);
    }
    try {
      callbackHandler.handle(new Callback[] { hgc });
    } catch (IOException e) {
      traceThrowableT(Severity.ERROR, "Error during attempt to write object, type " + type + " name " + key + " value " + value, e);
      throw new LoginException("Error in SPNegoLoginModule.");
    } catch (UnsupportedCallbackException e) {
      traceThrowableT(Severity.ERROR, "Error during attempt to write object, type " + type + " name " + key + " value " + value, e);
      throw new LoginException("Error in SPNegoLoginModule.");
    }
  }

  /**
   * @param key
   * @param value
   */
  private void setSessionAttribute(String key, Object o) throws LoginException {
    this.setGeneric(key, o, HttpCallback.SESSION_ATTRIBUTE);
  }

  /**
   * @param key
   * @param value
   */
  private void setHeader(String key, String value) throws LoginException {
    this.setGeneric(key, value, HttpCallback.SET_HEADER);
  }

  private void setStatus(int status) throws LoginException {
    setGeneric(null, "" + status, HttpCallback.RESPONSE_CODE);
  }

  /**
   * @param objectIDs
   * @return index of supported mechanism, -1 if no mechanisms is supported
   */
  private int getSupportedMechanismIndex(ObjectID[] mechsFromToken) {
    Set setSuppMechs = new HashSet();
    ObjectID[] supportedMechanisms = configHelper.getSupportedMechs();
    for (int i = 0; i < supportedMechanisms.length; i++) {
      setSuppMechs.add(supportedMechanisms[i]);
    }
    for (int idx = 0; idx < mechsFromToken.length; idx++) {
      if (setSuppMechs.contains(mechsFromToken[idx])) {
        return idx;
      }
    }
    return -1;
  }

  private void release(SpNegoState state) {
    // release state
    if (state != null) {
      if (state.gsscontext != null) {
        try {
          state.gsscontext.dispose();
        } catch (GSSException e) {
          // $JL-EXC$
        }
      }
    }
  }

  private void rollBack() {
    if (subject != null) {
      if (0 < principals.size()) {
        subject.getPrincipals().remove(principals);
      }
      if (delegatedCredentials != null) {
        subject.getPrivateCredentials().remove(delegatedCredentials);
      }
    }
    delegatedCredentials = null;
    userName = null;
    delegatedCredentials = null;
  }

  protected static void traceThrowableT(int severity, String msg, Throwable t) {
    LOCATION.traceThrowableT(severity, msg, t);
  }

  protected boolean getBooleanValue(String s) {
    if ("false".equalsIgnoreCase(s)) {
      return false;
    }
    return true;
  }

  protected boolean isBooleanValue(String s) {
    boolean res = false;
    res = "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    return res;
  }

  protected void checkBooleanValue(String s) {
    if (!isBooleanValue(s)) {
      throw new IllegalArgumentException("Not a boolean value :" + s);
    }
  }

  // /////////////////////////////////////////////////////////////////////////////
  //
  // main method for test purposes
  //
  // /////////////////////////////////////////////////////////////////////////////
  public static void main(String[] args) throws Exception {

    if ("parseinit".equals(args[0])) {
      String token = args[1];
      byte[] bytes = Base64.decode(token);
      ASN1Object asn1 = DerCoder.decode(bytes);
      if (asn1 instanceof SpNegoASN1) {
        System.out.println("token is spnego token.");
        System.out.println(asn1.getValue().toString());
      }
    }
  }
}

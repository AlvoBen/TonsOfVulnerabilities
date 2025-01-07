package com.sap.security.core.server.jaas;

import iaik.x509.X509Certificate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.keystore.KeystoreManager;
import com.sap.engine.interfaces.security.SecurityThreadContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.security.api.UMException;
import com.sap.security.api.logon.AuthSchemePrincipal;
import com.sap.security.api.ticket.InfoUnit;
import com.sap.security.core.imp.AbstractPrincipal;
import com.sap.security.core.imp.TenantFactory;
import com.sap.security.core.ticket.imp.Ticket;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Helper Class for the creation and evaluation of SAP Logon Tickets.
 * Additionally this class provides access to the user cache.
 *
 * <p>Copyright (c) 2003 SAP AG.
 * @version 1.0
 */
public class SAPLogonTicketHelper {

  /** Message Digest MD5 */
  public static MessageDigest mDigest= null;


  protected static final String AUTH_NAME = "sap.security.auth.configuration.name";
  
  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION);

  private static char[] CONST_2F = new char[]{'%','2','F'};
  private static char[] CONST_3D = new char[]{'%','3','D'};
  /** keystore manager for access to J2EE Engine keystore */
  private static KeystoreManager m_manager = null;
  private static KeyStore ticketKeystoreView = null;
  private static String currentKeystoreView = null;

  private static String systemID = null;
  
  
  /**
   * Method for evaluation of SAP Logon Tickets.
   *
   * <p>The ticketProps may include the following information</p>
   *
   * keystore - keystore view <br>
   * alias - alias for private key <br>
   * client - client of the issuing system
   * debug - 0:debug off, 1:debug on<br>
   * trustedsys<i> - system id and client of trusted signer system,
   * e.g. "B6Q, 000" <br>
   * trustediss<i> - issuer of trusted signer certificate, e.g. CN=B6Q, O=SAP-AG, C=DE <br>
   * trusteddn<i> - subject of trusted signer certificate, e.g. CN=B6Q, O=SAP-AG, C=DE </p>
   *
   * @param ticket SAP Logon Ticket to be evaluated
   * @param ticketProps properties for the ticket evaluation
   */
  public static void evaluateTicket(Ticket ticket, Map ticketProps)
    throws Exception {
    final String METHOD= "evaluateTicket() with ticket: {0} \n and options: {1}.";
    Exception exception;
    Ticket sapLogonTicket;
    try {
      // logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { ticket, ticketProps });
      }

      // System ID and client of J2EE Engine
      String ownID= (String) ticketProps.get(UMEAdapter.SYSTEM);
      String ownClient= (String) ticketProps.get("client");
      try {
        // throw exception if no ticket for evaluation are available
        if (ticket == null) {
          exception=
            new DetailedLoginException("Received no ticket for ticket evaluation.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
          throw (LoginException) exception;
        }
        // throw exception if no properties for evaluation are available
        if (ticketProps == null) {
          exception=
            new DetailedLoginException("Received no properties for ticket evaluation.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
          throw (LoginException) exception;
        }

        sapLogonTicket= ticket;
        // set debug option
        if (ticketProps.get("debug") != null
          && ((String) ticketProps.get("debug")).equalsIgnoreCase("1"))
          sapLogonTicket.debug= true;
        // set trusted certificates
        X509Certificate[] certs=
          (X509Certificate[]) ticketProps.get("certificates");
        sapLogonTicket.setCertificates(certs);
        // set verifiy mode and evaluate ticket
        sapLogonTicket.setMode(0);
        sapLogonTicket.verify();
      } catch (DetailedLoginException e) {
        throw e;
      } catch (Exception e) {
        exception = new DetailedLoginException(e.toString(), LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
        throw (LoginException) exception;
      }

      // if ticket is valid, check wether the ticket issuer is trusted
      // (= ticket has been created by this server or ticket issuer is
      // set as trusted in the login module properties and the issuer
      // certificate is set as trusted)
      String ticketSystemId = sapLogonTicket.getSystemID().trim();
      String ticketClient = sapLogonTicket.getSystemClient().trim();
      X509Certificate ticketSignerCertificate = sapLogonTicket.getSignerCertificate();

      if (!(ownID.equals(ticketSystemId) && ownClient.equals(ticketClient) && ticketSignerCertificate.equals(ticketProps.get("cert")))) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("The recieved SAP Logon Ticket is not created on this sytem. This system is with system id {0}, client {1} and signer certificate {2}. The ticket issuing system is: system ID - [{3}], client - [{4}], and signer certificate - [{5}]. Authentication stack: [{6}].  Check the login module options and UME properties.",
                                          new Object[] {ownID, ownClient, ticketProps.get("cert"), ticketSystemId, ticketClient, ticketSignerCertificate, ticketProps.get(SAPLogonTicketHelper.AUTH_NAME)});
        }

        if (!isSystemTrusted(ticketProps, ticketSystemId, ticketClient, ticketSignerCertificate)) {
          exception = new DetailedLoginException("Authentication failed: Issuer of SAP Logon Ticket is not trusted. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
          throw (LoginException) exception;
        } else {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("OK: The recieved SAP Logon Ticket is trusted. Authentication stack: [{0}].", new Object[] {ticketProps.get(SAPLogonTicketHelper.AUTH_NAME)});
          }
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("OK: The recieved SAP Logon Ticket is created on the same system. Authentication stack: [{0}].", new Object[] {ticketProps.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  }


  /**
   * method to create SAP Logon Tickets
   *
   * <p>The ticketProps may include the following information</p>
   *
   * key -  key for signing SAP Logon Tickets<br>
   * cert - certificate for signing SAP Logon Tickets<br>
   * user - user of SAP Logon Ticket<br>
   * system - issuing system<br>
   * client - client of the issuing system <br>
   * debug - 0:debug off, 1:debug on<br>
   * validity - hours of ticket validity<br>
   * validityMin - minutes if ticket validity<br>
   * inclcert - 0:cert not included in ticket,
   * 1:cert included</p>
   *
   * @param ticketProps properties for the ticket creation
   */
  public static Ticket createTicket(Map ticketProps) throws Exception {
    final String METHOD= "createTicket()";
    Exception exception;
    try {
      //logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { ticketProps });
      }

      Ticket sapLogonTicket= null;
      PrivateKey key;
      X509Certificate cert;
      String user;
	  String mappeduser;
      String client;
      String system;
      String strValid= null;
      String strValidMin= null;

      // throw exception if no properties for ticket creation have been specified
      if (ticketProps == null) {
        exception=
          new DetailedLoginException("Received no properties for ticket evaluation. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
        throw (LoginException) exception;
      }

      // throw exception if the ID of the issuing system have been specified
      system= (String) ticketProps.get(UMEAdapter.SYSTEM);
      if (system == null) {
        throw new DetailedLoginException("Issuing system needs to be specified. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }

      // throw exception if the client of the issuing system have been specified
      client= (String) ticketProps.get("client");
      if (client == null) {
        throw new DetailedLoginException("Issuing client needs to be specified. Check the login module options and UME properties. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }

      // get user (portal(UME) user)
      // in certain situations only mappeduser might be
      // filled.
      user= (String) ticketProps.get ("user");

	  // get mappeduser (R/3 user)
      mappeduser = (String) ticketProps.get ("mappeduser");
//      if (mappeduser == null) {
//        throw new DetailedLoginException("User needs to be specified.", LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
//      }

      // throw exception if the keypair for ticket creation has not been specified
      key= (PrivateKey) ticketProps.get("key");
      cert= (X509Certificate) ticketProps.get("cert");
      if (key == null || cert == null) {
        throw new DetailedLoginException("Signing keypair needs to be specified. Check the login module options and UME properties. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }
      // native creation of ticket currently not possible

      // set debug options
      sapLogonTicket= new Ticket();
      if (ticketProps.get("debug") != null
        && ((String) ticketProps.get("debug")).equalsIgnoreCase("1"))
        sapLogonTicket.debug= true;
      sapLogonTicket.setPrivateKeyPair(key, cert);
      sapLogonTicket.setMode(Ticket.MODE_CREATE);

      if (user != null) {
        InfoUnit iuUserID = new InfoUnit(0x20, InfoUnit.jcharToUTF8("portal:" + user));
        sapLogonTicket.addInfoUnit (iuUserID);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("User set in SAP Logon Ticket is [{0}]. Authentication stack: [{1}].", new Object[] {user, ticketProps.get(AUTH_NAME)});
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("No user set in SAP Logon Ticket. The authenticated user is [{0}]. Authentication stack: [{1}].", new Object[] {user, ticketProps.get(AUTH_NAME)});
        }
      }

	  //todo get authscheme name from somewhere...
      Object o = ticketProps.get (UMEAdapter.AUTHSCHEME);
	  InfoUnit iuAuthscheme = new InfoUnit(InfoUnit.ID_AUTHSCHEME,
			  InfoUnit.jcharToUTF8((o==null?"default":(String)o)));
	  sapLogonTicket.addInfoUnit(iuAuthscheme);

      if (mappeduser != null) {
        sapLogonTicket.setUser (mappeduser);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Mapped user [{0}] set in SAP Logon Ticket. The authenticated user is [{1}]. Authentication stack: [{2}].", new Object[] {mappeduser, user, ticketProps.get(AUTH_NAME)});
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("No mapped user set in SAP Logon Ticket. The authenticated user is [{0}]. Authentication stack: [{1}].", new Object[] {user, ticketProps.get(AUTH_NAME)});
        }
      }

      sapLogonTicket.setSystemClient(client);
      sapLogonTicket.setSystemID(system);

      // specify wether certificate should be included in the ticket
      if (ticketProps.get("inclcert") != null)
        sapLogonTicket.setIncludeOwnCert(
          ((String) ticketProps.get("inclcert")).equals("1"));
      strValid= (String) ticketProps.get("validity");
      strValidMin= (String) ticketProps.get("validityMin");
      if (null != strValid)
        sapLogonTicket.setValidTime(Integer.parseInt(strValid));
      if (null != strValidMin)
        sapLogonTicket.setValidTimeMin(Integer.parseInt(strValidMin));

      sapLogonTicket.create();

      return sapLogonTicket;
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  }


  /**
   * Method to create SAP Assertion Tickets
   *
   * <p>The ticketProps may include the following information:
   *
   * key -  key for signing SAP Assertion Tickets<br>
   * cert - certificate for signing SAP Assertion Tickets<br>
   * user - the authnticated user for which SAP Assertion Ticket is created<br>
   * system - issuing system<br>
   * client - client of the issuing system <br>
   * validity - hours of ticket validity<br>
   * validityMin - minutes if ticket validity<br>
   * inclcert - 0:cert not included in ticket, 1:cert included
   * mappeduser - the mapped user
   * j_authscheme - the auth scheme for which the ticket is created.
   * </p>
   *
   * @param ticketProps properties for the ticket creation
   */
  public static SAPAuthenticationAssertionTicket createAssertionTicket(Map ticketProps, Map sharedState) throws Exception {
    final String METHOD = "createAssertionTicket()";
    Exception exception;

    try {
      //logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { ticketProps });
      }

      SAPAuthenticationAssertionTicket sapAssertionTicket = null;
      PrivateKey key;
      X509Certificate cert;
      String user;
	    String mappeduser;
      String client;
      String system;
      String strValid = null;
      String strValidMin = null;

      // throw exception if no properties for ticket creation have been specified
      if (ticketProps == null) {
        exception = new DetailedLoginException("Received no properties for ticket evaluation. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
        throw (LoginException) exception;
      }

      if (sharedState == null) {
        sharedState = new Hashtable();
      }

      extractKeyAndCertificate(ticketProps, sharedState);

      // throw exception if the ID of the issuing system have not been specified
      system = (String) ticketProps.get(UMEAdapter.SYSTEM);
      if (system == null) {
        throw new DetailedLoginException("Issuing system needs to be specified. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }

      // throw exception if the client of the issuing system have not been specified
      client = (String) ticketProps.get("client");
      if (client == null) {
        throw new DetailedLoginException("Issuing client needs to be specified. Check the login module options and UME properties. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }

      // throw exception if the authenticated user has not been specified
      user = (String) ticketProps.get("user");
 
      // get mappeduser
      mappeduser = (String) ticketProps.get("mappeduser");
  
      // throw exception if the keypair for ticket creation has not been specified
      key = (PrivateKey) ticketProps.get("key");
      cert = (X509Certificate) ticketProps.get("cert");
      if (key == null || cert == null) {
        throw new DetailedLoginException("Signing keypair needs to be specified. Check the login module options and UME properties. Authentication stack: " + ticketProps.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }

      // set debug options
      sapAssertionTicket = new SAPAuthenticationAssertionTicket();
      if (ticketProps.get("debug") != null && ((String) ticketProps.get("debug")).equalsIgnoreCase("1"))
        sapAssertionTicket.debug = true;

      sapAssertionTicket.setPrivateKeyPair(key, cert);
      sapAssertionTicket.setMode(Ticket.MODE_CREATE);

      
      if (user != null) {
        InfoUnit iuUserID = new InfoUnit(0x20, InfoUnit.jcharToUTF8("portal:" + user));
        sapAssertionTicket.addInfoUnit(iuUserID);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("User set in SAP Assertion Ticket is [{0}]. Authentication stack: [{1}].", new Object[] {user, ticketProps.get(AUTH_NAME)});
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("No user set in SAP Assertion Ticket. The authenticated user is [{0}]. Authentication stack: [{1}].", new Object[] {user, ticketProps.get(AUTH_NAME)});
        }
      }

	  //todo get authscheme name from somewhere...
      Object object = ticketProps.get (UMEAdapter.AUTHSCHEME);
	    InfoUnit iuAuthscheme = new InfoUnit(InfoUnit.ID_AUTHSCHEME, InfoUnit.jcharToUTF8(((object == null) ? "default" : (String) object)));
	    sapAssertionTicket.addInfoUnit(iuAuthscheme);

      object = ticketProps.get(UMEAdapter.RECIPIENT_SID);
      if (object != null) {
        InfoUnit recipientSIDInfoUnit = new InfoUnit(InfoUnit.ID_RECIPIENT_SID, InfoUnit.jcharToUTF8((String) object));
        sapAssertionTicket.addInfoUnit(recipientSIDInfoUnit);
      }

      object = ticketProps.get(UMEAdapter.RECIPIENT_CLIENT);
      if (object != null) {
        InfoUnit recipientClientInfoUnit = new InfoUnit(InfoUnit.ID_RECIPIENT_CLIENT, InfoUnit.jcharToUTF8((String) object));
        sapAssertionTicket.addInfoUnit(recipientClientInfoUnit);
      }

      if (mappeduser != null) {
        sapAssertionTicket.setUser(mappeduser);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Mapped user [{0}] set in SAP Assertion Ticket. The authenticated user is [{1}]. Authentication stack: [{2}].", new Object[] {mappeduser, user, ticketProps.get(AUTH_NAME)});
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("No mapped user set in SAP Assertion Ticket. The authenticated user is [{0}]. Authentication stack: [{1}].", new Object[] {user, ticketProps.get(AUTH_NAME)});
        }
      }

      sapAssertionTicket.setSystemClient(client);
      sapAssertionTicket.setSystemID(system);

      // specify wether certificate should be included in the ticket
      if (ticketProps.get("inclcert") != null)
        sapAssertionTicket.setIncludeOwnCert(((String) ticketProps.get("inclcert")).equals("1"));

      strValid = (String) ticketProps.get("validity");
      strValidMin = (String) ticketProps.get("validityMin");
      if (null != strValid)
        sapAssertionTicket.setValidTime(Integer.parseInt(strValid));
      if (null != strValidMin)
        sapAssertionTicket.setValidTimeMin(Integer.parseInt(strValidMin));

      sapAssertionTicket.setDoNotCacheTicket(true);

      sapAssertionTicket.create();
      
      if (LOCATION.beDebug()) {
      	LOCATION.debugT("Created assertion ticket: \n" + sapAssertionTicket.toString());
      }

      return sapAssertionTicket;
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  }

  /**
   * Method to create SAP Assertion Tickets. The user name and authscheme for the ticket are taken from the
   * current security session. If there is no logged in user in the thread, then an exception is
   * thrown. If there is no authscheme in the current session, then the default authscheme is
   * used.
   *
   * @param recipientSID - the system ID of the recipient system.
   * @param recipientClient - the client ID of the recipient client in the system.
   *
   * @return  the created SAPAuthenticationAssertionTicket.
   *
   * @throws IllegalStateException  if there is no logged in user in the current thread.
   */
  public static SAPAuthenticationAssertionTicket createAssertionTicket(String recipientSID, String recipientClient) throws Exception {
    String localUser = null;
    String authscheme = null;

    Principal principal = SecurityThreadContext.getCallerPrincipal();

    if (principal != null) {
      localUser = principal.getName();

      if (localUser == null) {
        LOCATION.errorT("Error while creating assertion ticket on demand. The name in the current principal is null.");
        throw new IllegalStateException("The name in the current principal is null.");
      }

      boolean isAuthenticated = SecurityThreadContext.isAuthenticated();

      if (!isAuthenticated) {
        LOCATION.errorT("Error while creating assertion ticket on demand. No logged in user found.");
        throw new IllegalStateException("No logged in user found.");
      }
    } else {
      LOCATION.errorT("Error while creating assertion ticket on demand. Current principal is null.");
      throw new IllegalStateException("Current principal is null.");
    }

    Principal[] principals = SecurityThreadContext.getAllPrincipals();

    if ((principals != null) && (principals.length > 0)) {
      for (int i = 0; i < principals.length; i++) {
        Principal tempPrincipal = principals[i];

        if (tempPrincipal instanceof AuthSchemePrincipal) {
          authscheme = tempPrincipal.getName();

          if (authscheme == null) {
            LOCATION.errorT("Error while creating assertion ticket on demand. The authscheme name in the principal is null.");
            throw new IllegalStateException("Authscheme name in principal is null.");
          } else {
	    	if (LOCATION.beDebug()) {
            	LOCATION.debugT("The current authscheme is " + authscheme);
          	}
          }

          break;
        }
      }
    } else {
      LOCATION.errorT("Error while creating assertion ticket on demand. There is no principal in the thread.");
      throw new IllegalStateException("No principals in the thread.");
    }

    if (authscheme == null) {
      if (LOCATION.beInfo()) {
        LOCATION.infoT("No autshcheme found in security session while creating assertion ticket on demand. The default one will be used.");
      }

      authscheme = "default";
    }

    //todo check the ticket validity period
    return createAssertionTicket(localUser, authscheme, recipientSID, recipientClient);
  }

  /**
   * Method to create SAP Assertion Tickets.
   *
   * @param localUser - user of SAP Assertion Ticket
   * @param authscheme - the auth scheme for which the ticket is created
   * @param recipientSID
   * @param recipientClient
   *
   * @return  the created SAPAuthenticationAssertionTicket.
   */
  public static SAPAuthenticationAssertionTicket createAssertionTicket(String localUser, String authscheme, String recipientSID, String recipientClient) throws Exception {
    Properties ticketProperties = new Properties();
    Properties sharedState = new Properties();

    sharedState.put(UMEAdapter.LOGIN_USER, localUser);

    UMEAdapter adapter = new UMEAdapter(sharedState, ticketProperties, true);

    ticketProperties = adapter.getMergedOptions();
    // sets the user and the mapped user.
    adapter.setMappedUser();

    ticketProperties.put(UMEAdapter.AUTHSCHEME, authscheme);
    ticketProperties.put(UMEAdapter.SYSTEM, SAPLogonTicketHelper.systemID);

    if ((recipientSID != null) && !"".equals(recipientSID) && (recipientClient != null) && !"".equals(recipientClient)) {
      ticketProperties.put(UMEAdapter.RECIPIENT_SID, recipientSID);
      ticketProperties.put(UMEAdapter.RECIPIENT_CLIENT, recipientClient);
    }

    return createAssertionTicket(ticketProperties, null);
  }

  /**
   * method to check wether a SAP Logon Ticket was created
   * by a trusted System
   *
   * @param props includes information about trusted systems
   * @param sys system id of the issuing system
   * @param client client of the issuing system
   * @param signerCert certificate that was used for signing the ticket
   */
  public static boolean isSystemTrusted(Map props, String sys, String client, X509Certificate signerCert) throws UMException, LoginException {
    final String METHOD= "isSystemTrusted()";
    Boolean b= null;
    try {

      //logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { props, sys, client, signerCert });
      }

      String token;
      String optionSYS;
      String optionSysID;
      String optionSysClient;
      String optionISS;
      String optionDN;
      int seperator;

      Object[] keys= props.keySet().toArray();

      // iterate through all trusted system entries
      int k= 0;
      for (int i= 0; i < keys.length; i++) {
        if (((String) keys[i]).startsWith("trustedsys")) {
          k++;
          token= ((String) keys[i]).substring(10);
          optionSYS= (String) props.get("trustedsys" + token);
          optionISS= (String) props.get("trustediss" + token);
          optionDN= (String) props.get("trusteddn" + token);

          if (optionSYS != null && optionISS != null && optionDN != null) {
            seperator= optionSYS.indexOf(",");
            optionSysID= optionSYS.substring(0, seperator).trim();
            optionSysClient= optionSYS.substring(seperator + 1).trim();
            CompoundName compDN= null;
            CompoundName compDNCert= null;
            CompoundName compISS= null;
            CompoundName compISSCert= null;
            Properties syntax= new Properties();
            syntax.setProperty("jndi.syntax.direction", "right_to_left");
            syntax.setProperty("jndi.syntax.escape", "\\");
            syntax.setProperty("jndi.syntax.trimblanks", UMEAdapter.TRUE);
            syntax.setProperty("jndi.syntax.separator", ",");
            // create compound name for issuer/subject dn in order
            // to compare ticket issuer/subject with issuer/subject
            // in trusted entry
            try {
              compDNCert=
                new CompoundName(signerCert.getSubjectDN().getName(), syntax);
              compISSCert=
                new CompoundName(signerCert.getIssuerDN().getName(), syntax);
              compDN= new CompoundName(optionDN, syntax);
              compISS= new CompoundName(optionISS, syntax);
            } catch (InvalidNameException e) {
              e.printStackTrace();
            }
            // is the singer certificate trusted and
            // does the issuer/subject match with the trusted entry?
            if (compDN.equals(compDNCert)
              && compISS.equals(compISSCert)
              && optionSysID.equalsIgnoreCase(sys)
              && optionSysClient.equalsIgnoreCase(client)) {

              if (LOCATION.beInfo()) {
                LOCATION.infoT("OK: The system defined in the ACL of EvaluateTicketLoginModule in [{0}] authentication stack under the name [trustedsys{1}] equals SAP Logon Ticket issuing sistem. The system in the ACL is: system ID - [{2}], client - [{3}], certificate with issuer DN - [{4}] and subject DN - [{5}]. SAP Logon Ticket issuing system is: system ID - [{6}], client - [{7}], certificate with issuer DN - [{8}] and subject DN - [{9}].",
                                                new Object[] {props.get(AUTH_NAME), token, optionSysID, optionSysClient, optionISS, optionDN, sys, client, signerCert.getIssuerDN().getName(), signerCert.getSubjectDN().getName()});
              }

              if (TenantFactory.getInstance().isBPOEnabled()) {
                String tenant = (String) props.get("tenant" + token);

                if (tenant != null) {
                  props.put("trustedTenant", tenant);

                  LOCATION.debugT("Tenant [{0}] is found in the login module properties of [{1}] authentication stack.", new Object[] {tenant, props.get(AUTH_NAME)});
                } else {
                  LOCATION.errorT("The UME is in tenant mode but no tenant is specified in ticket login module ACL in [{0}] authentication stack.", new Object[] {props.get(AUTH_NAME)});
                  SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000121", "The UME is in tenant mode but no tenant is specified in ticket login module ACL in [{0}] authentication stack.", new Object[] {props.get(AUTH_NAME)});

                  throw new LoginException("The UME is in tenant mode but no tenant is specified in ticket login module ACL.");
                }
              }


              b= new Boolean(true);
              return true;
            } else {
              if (LOCATION.beInfo()) {
                LOCATION.infoT("The system defined in the ACL of EvaluateTicketLoginModule in [{0}] authentication stack under the name [trustedsys{1}] does not equal to SAP Logon Ticket issuing sistem. The system in the ACL is: system ID - [{2}], client - [{3}], certificate with issuer DN - [{4}] and subject DN - [{5}]. SAP Logon Ticket issuing system is: system ID - [{6}], client - [{7}], certificate with issuer DN - [{8}] and subject DN - [{9}]. Check the login module options and UME properties.",
                                                new Object[] {props.get(AUTH_NAME), token, optionSysID, optionSysClient, optionISS, optionDN, sys, client, signerCert.getIssuerDN().getName(), signerCert.getSubjectDN().getName()});
              }
            }
          }
        }
      }

      // if no trusted systems are declared, no system is trusted.
      if (k == 0) {
        if (CATEGORY.beWarning()) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000122", "No trusted systems are configured in EvaluateTicketLoginModule options in [{0}] authentication stack. The ticket issuing system is: system ID - [{1}], client - [{2}], certificate with issuer DN - [{3}] and subject DN - [{4}].",
          new Object[] { props.get(AUTH_NAME), sys, client, signerCert.getIssuerDN().getName(), signerCert.getSubjectDN().getName()});
        }
        return false;
      }

      if (CATEGORY.beWarning()) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000123", "None of the systems defined in the ACL of EvaluateTicketLoginModule in [{0}] authentication stack equals to SAP Logon Ticket issuing system. The ticket issuing system is: system ID - [{1}], client - [{2}], certificate with issuer DN - [{3}] and subject DN - [{4}].",
        new Object[] {props.get(AUTH_NAME), sys, client, signerCert.getIssuerDN().getName(), signerCert.getSubjectDN().getName()});
      }

      b = new Boolean(false);
      return false;
    } finally {
      // logging info
      if (b != null)
        LOCATION.exiting(METHOD, b);
      else
        LOCATION.exiting(METHOD);
    }
  }

  public static void setSystemID(String systemID) {
    if (SAPLogonTicketHelper.systemID == null) {
      SAPLogonTicketHelper.systemID = systemID;
    }
  }

  /**
   * Internal method for getting the specified J2EE Engine keystore
   * view. If view does not exist, null is returned.
  * */
  static KeyStore getTicketKeyStore(String keystore, String password, Map options)
    throws Exception {

    final String METHOD= "getTicketKeyStore()";
    try {
      //logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { keystore, password });
      }
      
      if (ticketKeystoreView == null || !currentKeystoreView.equalsIgnoreCase(keystore)) {
        if (m_manager == null) {
          // No keystore manager means no keystore view
          if (CATEGORY.beError()) {
            SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000124", "TicketKeystore view cannot be retrieved. Keystore manager is not initialized. Check if keystore service is started.");
          }
          
          return null;
        } else {
          currentKeystoreView = keystore;
          ticketKeystoreView = m_manager.getKeystore(keystore);
        }
      }

      return ticketKeystoreView;

    } catch (Exception e) {
      if (UMEAdapter.DEFAULT_KEYSTORE_VIEW.equals(keystore)) {
        if (CATEGORY.beError()) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000125", "The default keystore view [{0}] does not exist. Authentication stack: [{1}]. " +
          	"The possible reasons for that problem are: keystore does not exist or the user has no permission to read from the keystore view. " +
          	"You can delete the TicketKeystore and restart the engine so that the engine automatically re-creates it.", 
          	new Object[] {keystore, options.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
      } else {
        if (CATEGORY.beError()) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000126",
          	"Error in retrieving keystore view [{0}]. The keystore view does not exist or user has no permission to read it. " +
          	"The default keystore view for SSO with SAP Logon Ticket is [{1}]. Check the login module options in [{2}] authentication stack and UME properties " +
          	"so that they point to the default keystore view.",
          	new Object[] { keystore, UMEAdapter.DEFAULT_KEYSTORE_VIEW, options.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
      }
      
      throw e;
    } finally {
      // logging information
      LOCATION.exiting(METHOD);
    }
  }

  public static void setKeyStoreManager(KeystoreManager keystoreManager) {
    if (keystoreManager != null) {
      m_manager = keystoreManager;
    }
  }

  /** Internal method for getting all
   * trusted certificates from a keystore view */
  static Vector getTrustedCerts(KeyStore store) throws Exception {
    Vector certs= null;
    String entry= null;
    certs= new Vector();
    Enumeration enumeration = store.aliases();

    while (enumeration.hasMoreElements()) {
      entry = (String) enumeration.nextElement();
      
      if (store.isCertificateEntry(entry)) {
        certs.add(store.getCertificate(entry));
      } else if (store.isKeyEntry(entry)) {
        java.security.cert.Certificate[] trustCerts = store.getCertificateChain(entry);

        for (int i = 0; i < trustCerts.length; i++) {
          certs.add(trustCerts[i]);
        }
      }
    }
    return certs;
  }


  /** Script-languages like ASP may have problems with the signes "/", "="
   * in the MYSAPSSO2 cookie.
   * Therefore they are replaced by "%2F", "%3D", which are not part of
   * the base64 alphabet. 
   * For synchronization with ABAP ticket creation "+" is replaced by "!".
   * The class URL-Decoder makes the
   * same except that "+" becomes " "
   */
  public static String replaceSpecialChar(String value) {
    int specialCharCount = countSpecialChars(value);
    if (specialCharCount > 0) {
      int size = value.length();
      StringBuilder replaced_cookie = new StringBuilder(size+specialCharCount);       
      for (int i= 0; i < size; i++) {        
        char chr = value.charAt(i);        
        switch (chr) {
          case '+': {
            replaced_cookie.append('!');
            break;
          }
          
          case '/': {
            replaced_cookie.append(CONST_2F);
            break;
          }
          
          case '=': {
            replaced_cookie.append(CONST_3D);
            break;
          }
          
          default: {
            replaced_cookie.append(chr);
          }
        }
      }
      return replaced_cookie.toString();
    } else {
      return value;
    }
  }
  
  private static int countSpecialChars(String s) {
    int count = 0;
    int length = s.length();
    for (int i = 0; i < length; i++) {
      switch (s.charAt(i)) {
        case '+': count++; break;
        case '=': count += CONST_3D.length; break;
        case '/': count += CONST_2F.length; break;
      }
    }
    return count;
  }

  /** Internal method for URL cookie decoding.<br>
   * The characters "+", "=", "/" have been replaced by "%2B", "%3D",
   * "%2F" in the MYSAPSSO2 cookie, because script languages like ASP might
   * have difficulties with them.
   * The encoding has to be repeated as long, as the cookie includes '%' (0-2 times).
   * Reason: Some browsers like tomcat 4.0 encode and
   * decode all cookies by their own with an UrlEndoder resp. UrlDecoder
   * before adding them to the response resp. extracting them from the request.
   * If the cookie is set by such a browser and is extracted by another browser,
   * the cookie value still might be encoded twice (once by our classes and
   * twice by the browser.)
   * Decoding the ticket too often will cause problems, because '+' is translated
   * into ' ' by the UrlDecoder.
   * Also the r/3 system creates tickets with '!' instead of '+', therefore
   * our decode method can not be used (or we would also have to replace '?'),
   * so it is better to use the UrlDecoder.
   */
  public static String undoReplaceChar(String value) {
    if (value != null && value.length() > 0) {
      try {
        while (value.indexOf('%') != -1) {
          value= URLDecoder.decode(value, "UTF-8");
        }
      } catch (UnsupportedEncodingException e) {
        LOCATION.traceThrowableT(Severity.ERROR, "Unsupported encoding for ticket decoding.", e);
      }
      
      if (value.indexOf(' ') != -1) {
        value = value.replace(' ','+');    
      }
      if (value.indexOf('!') != -1) {
        value = value.replace('!','+');    
      }
    }
    return value;
  }

  protected static String removeEndingSpaces(String string) {
    int length = string.length();
    int i = length - 1;

    while (i >= 0) {
      if (Character.isWhitespace(string.charAt(i))) {
        i--;
      } else {
        break;
      }
    }

    if (i == (length - 1)) {
      return string;
    } else {
      return string.substring(0, (i + 1));
    }
  }

  protected static String getTenantAwareUsername(String username, Map options) throws UMException {
    String tenantAwareUserName = username;

    if (username != null) {
      TenantFactory tenantFactory = TenantFactory.getInstance();

      if (tenantFactory.isBPOEnabled()) {
        String tenant = (String) options.get("trustedTenant");

        if (tenant != null) {
          if (!username.startsWith(tenant + AbstractPrincipal.CLIENT_SEPARATOR_CHARACTER)) {
            tenantAwareUserName = tenant + AbstractPrincipal.CLIENT_SEPARATOR_CHARACTER + username;
          } else {
            tenantAwareUserName = username;
          }

          LOCATION.debugT("The tenant aware user name is " + tenantAwareUserName);
        } else {
          tenantAwareUserName = username;

          LOCATION.debugT("UME is in tenant mode but no tenant is specified in EvaluateTicketLoginModule ACL. Probably the ticket has been issued by the same system and in this case the user name should already have tenant prefix. User name is: " + tenantAwareUserName);
        }
      }
    }

    return tenantAwareUserName;
  }

  private static void extractKeyAndCertificate(Map options, Map sharedState) throws Exception {
    // get keystore properties
    String keystore = (String) options.get("keystore");
    String password = (String) options.get("password");
    String alias = (String) options.get("alias");

    // get keystore instance
    PrivateKey key;
    X509Certificate cert;

    KeyStore store = SAPLogonTicketHelper.getTicketKeyStore(keystore, password, options);

    // check, if the keystore is emty (at the moment keystoreManager
    // returns an emty keystore even if no keystore object exists)
    if (store == null) {
      if (CATEGORY.beError()) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000127", "Keystore view [{0}] does not exist or has no entries. Authentication stack: [{1}]. The default kestore view is [{2}]. " +
            "The possible reasons for that problem are: keystore does not exist, keystore has no entries, the user has no permission to read from the keystore view. " +
        	"You can delete the TicketKeystore and restart the engine so that the engine automatically re-creates it.", 
        	new Object[] {keystore, sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW}); 
      }

      throw new DetailedLoginException("Ticket keystore view does not exist or has no entries.", LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
    }

    // get signer certificate
    java.security.cert.X509Certificate tempCert = null;
    try {
      tempCert = (java.security.cert.X509Certificate) store.getCertificate(alias);
    } catch (Exception e) {
      SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000128", "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {alias, keystore, sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
      LOCATION.traceThrowableT(Severity.ERROR, "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}].", new Object[] {alias, keystore, sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
      throw e;
    }

    if (tempCert != null) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Certificate found under [{0}] alias. Authentication stack: [{1}].", new Object[] {alias, sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }
    }

    try {
      key = (PrivateKey) store.getKey(alias, password.toCharArray());
    } catch (Exception e) {
      SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000129", "Key under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {alias, keystore, sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
      LOCATION.traceThrowableT(Severity.ERROR, "Key under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}].", new Object[] {alias, keystore, sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
      throw e;
    }

    if (key == null || tempCert == null) {
      SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000130", "Keypair for signing not found in keystore view [{0}] under alias [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {keystore, alias, sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});

      throw new DetailedLoginException("keypair for signing not found in keystore", LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
    }

    // store private key and certificate together with
    // other options
    cert = (iaik.x509.X509Certificate) tempCert;
    options.put("key", key);
    options.put("cert", cert);
  }
}

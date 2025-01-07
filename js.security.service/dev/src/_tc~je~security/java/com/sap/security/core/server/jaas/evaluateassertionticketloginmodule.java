package com.sap.security.core.server.jaas;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.*;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSetterCallback;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.IAuthScheme;
import com.sap.security.api.logon.AuthSchemePrincipal;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.api.ticket.InfoUnit;
import com.sap.security.api.ticket.TicketException;
import com.sap.security.api.umap.NoLogonDataAvailableException;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.ticket.imp.Ticket;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;

/**
 * <p>JAAS Login Module for the Evaluation of SAP Authentication Assertion Tickets.
 * Together with the
 * {@link CreateTicketLoginModule}
 * this jaas login module can be used to enable SAP Authentication Assertion Ticket
 * based Single Sign-On.
 * </p>
 *
 * <p>After successful evaluation an J2EE Engine Principal
 * and a credential object with the SAP Authentication Assertion Ticket will be added
 * to the subject.</p>
 *
 * @author Svetlana Stancheva
 * @version 6.40
 */
public class EvaluateAssertionTicketLoginModule extends AbstractLoginModule {

  public static final String TICKET_HEADER = "MYSAPSSO2";

  /**
   *  Pattern that's used to create the system key for system
   *  lookup out of sysid and client.
   *  {0} will be replaced by the sysid, {1} by the client.
   */
  private static final String DEFAULT_UMAP_SYSTEM_PATTERN = "{0}CLNT{1}";
  private static final String DEFAULT_INFO_UNIT_ENCODING = "UTF8";
  private static final boolean DEFAULT_RECIPIENT_VALIDATE_VALUE = true; 
  
  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION + ".EvaluateAssertionTicketLoginModule");

  /** subject representing the user */
  Subject m_subject;

  /** shared state with the other login modules */
  Map m_sharedState;

  /** callback handler for retrieving logon data */
  CallbackHandler m_handler;

  /** modified options for this login module */
  Properties m_options = new Properties();

  /** ticket credential for subject */
  SAPAuthenticationAssertionTicketCredential m_credential = null;

  /** SAP Authentication Assertion Ticket */
  String m_ticketString = null;

  /** principal for subject */
  Principal m_principal = null;

  /** authscheme for subject (satisfied authscheme) */
  String m_authscheme = null;

  /** authscheme to satisfy */
  String m_authscheme_to_be;

  /** authscheme principal */
  AuthSchemePrincipal m_authscheme_principal;

  /** internal state of login */
  boolean m_succeeded = false;

  /** Exception dummy */
  Exception m_exception;

  /** for extended UME ticket features */
  private UMEAdapter m_umeadapter = null;

  /** default time tolerance for ticket validity */
  private static final int DEFAULT_TIME_TOL = 3;

  /**  time tolerance for ticket validity */
  private int m_timetolerance;

  /** username */
  String m_username;

  /** pattern for system lookup for reverse user mapping */
  String m_umap_system_pattern;

  //  String m_sysID= null;
  //  Properties m_keystoreProps= new Properties();

  /**
   * <p>Initialize the <code>EvaluateTicketLoginModule</code>.
   * This method is called by the {@link javax.security.auth.login.LoginContext}.
   * The purpose of this method is to initialize the
   * <code>LoginModule</code> with all relevant information. </p>
   * <p>The following <code>options</code> are available: <br>
   * <p>
   * debug - 0:debug off (default), 1:debug on<br>
   * trustedsys<i> - system id and client of trusted signer system,
   * e.g. "B6Q, 000" <br>
   * trustediss<i> - issuer of trusted signer certificate, e.g. CN=B6Q, O=SAP-AG, C=DE <br>
   * trusteddn<i> - subject of trusted signer certificate, e.g. CN=B6Q, O=SAP-AG, C=DE </p>
   *
   * @param subject the subject to be authenticated.
   * @param handler callback handler for communication
   *			with the user
   * @param sharedState shared state used to pass information
   * from one login module to another.
   * @param options options that are specified in the login
   * {@link javax.security.auth.login.Configuration}
   */
  public void initialize(Subject subject, CallbackHandler handler, Map sharedState, Map options) {
    final String METHOD = "initialize()";

    try {
      // logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { subject, handler });
      }

      super.initialize(subject, handler, sharedState, options);

      // store informations passed by the LoginContext
      m_subject = subject;
      m_handler = handler;
      m_sharedState = sharedState;
      m_options.putAll(options);

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of EvaluateAssertionTicketLoginModule in [{0}] authentication stack are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }

      m_umeadapter = new UMEAdapter(sharedState, m_options, true);
	    m_options = m_umeadapter.getMergedOptions();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of EvaluateAssertionTicketLoginModule in [{0}] authentication stack after merge with UME properties are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }

      // option for internal use only - not documented!
      Object option = m_options.get("timetolerance");
      if (option == null || option.equals("")) {
        m_timetolerance= DEFAULT_TIME_TOL;
      } else {
        m_timetolerance= Integer.parseInt((String) option);
      }

      m_authscheme_to_be = (String) options.get ("j_authscheme");
      if (m_authscheme_to_be==null) {
        m_authscheme_to_be = "default";
      }

      m_umap_system_pattern = (String) options.get ("umap-system-pattern");
      if (m_umap_system_pattern==null) {
          m_umap_system_pattern = DEFAULT_UMAP_SYSTEM_PATTERN;
      }

      option = m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME);
      if (option != null) {
        m_options.put(SAPLogonTicketHelper.AUTH_NAME, option);
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of EvaluateAssertionTicketLoginModule in [{0}] authentication stack after adding the default values are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  } // end initialize(...)

  /** <p>Method to authenticate the user by means of his
   * SAP Authentication Assertion Ticket.</p>
   * <p>It will first be checked wether the SAP Authentication Assertion Ticket of the
   * user is still valid. If this is not the case,
   * the login method returns false. If the ticket is
   * still valid, it will be checked
   * wether the SAP Authentication Assertion Ticket is issued by a trusted system.
   * All trusted Systems can be specified in the
   * options of this login module.<br>
   * @see javax.security.auth.spi.LoginModule#login()
   */
  public boolean login() throws javax.security.auth.login.LoginException {
    final String METHOD= "login()";
    Boolean b = null;
    KeyStore store;
    java.security.cert.X509Certificate tempCert;
    X509Certificate cert;
    Vector certs;
    String stringTicket = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      // get SAP Assertion Ticket
      SAPAuthenticationAssertionTicket ticket = null;

      stringTicket = getSAPAssertionTicket();
      m_ticketString = SAPLogonTicketHelper.undoReplaceChar(stringTicket);
      if (m_ticketString == null || m_ticketString.equals("")) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Received no SAP Authentication Assertion Ticket. Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
        m_exception = new DetailedLoginException("Received no SAP Authentication Assertion Ticket.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
        throw (LoginException) m_exception;
      }

      // create ticket object from String
      ticket = new SAPAuthenticationAssertionTicket();
      ticket.setTicket(m_ticketString);
      if (LOCATION.beInfo()) {
        LOCATION.infoT("SAP Authentication Assertion Ticket received. Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Authentication Assertion Ticket: \n [{0}]. Authentication stack: [{1}].", new Object[] {ticket.toString(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      // check wether the ticket is still valid
      if (!ticket.isValid(m_timetolerance)) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("SAP Authentication Assertion Ticket has expired or it is not valid yet. The ticket is valid from {0} until {1} and now it is {2}. Authentication stack: [{3}]. Check the system time as well as the login module options and UME properties.", new Object[] {ticket.getStartValidDate().getTime(), ticket.getExpirationDate().getTime(), Calendar.getInstance (new SimpleTimeZone(0, "GMT")).getTime(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
        
        throw new DetailedLoginException("SAP Authentication Assertion Ticket has expired or it is not valid yet.", LoginExceptionDetails.SAP_LOGON_TICKET_HAS_EXPIRED);
      }

      // a ticket validation has to be made.
      // Start by getting the SID
      String sid = (String) m_sharedState.get("System-ID");

	    if (sid != null) {
	  	  m_options.put("system", sid);
        if (LOCATION.beDebug()) {
		      LOCATION.debugT("Found SID: [{0}].", new Object[] {sid});
        }
	    } else {
		    m_exception = new DetailedLoginException("Cannot continue. The system ID of the target system is not available.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
		    throw (LoginException) m_exception;
      }
	  
      // get keystore properties.
      String keystore = (String) m_options.get("keystore");
      String password = (String) m_options.get("password");
      String alias = (String) m_options.get("alias");

      // get ticket keystore view
      store = SAPLogonTicketHelper.getTicketKeyStore(keystore, password, m_options);

      if (store == null) {
        if (CATEGORY.beError()) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000114", "Keystore view [{0}] does not exist or has no entries. Authentication stack: [{1}]. The default kestore view is [{2}]. " +
        	    "The possible reasons for that problem are: keystore does not exist, keystore has no entries, the user has no permission to read from the keystore view. " +
        	    "You can delete the TicketKeystore and restart the engine so that the engine automatically re-creates it.", 
        	    new Object[] {keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW}); 
        }
        throw new DetailedLoginException("Keystore view for ticket evaluation does not exist or has no entries.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
      }

      // get trusted certificates and signer certificate
      // out of the specified keystore
      try {
        certs = SAPLogonTicketHelper.getTrustedCerts(store);
      } catch (Exception e) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000111", "The certificates contained in keystore view [{0}] cannot be retrieved. Authentication stack: [{1}]. The default kestore view is [{2}]. The default keypair alias is [{3}]. Check the login module options and UME properties.", new Object[] {keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
        LOCATION.traceThrowableT(Severity.ERROR, "The certificates contained in keystore view [{0}] cannot be retrieved. Authentication stack: [{1}].", new Object[] {keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw e;
      }

      try {
        tempCert = (java.security.cert.X509Certificate) store.getCertificate(alias);
      } catch (Exception e) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000112",  "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {alias, keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
        LOCATION.traceThrowableT(Severity.ERROR, "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}].", new Object[] {alias, keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw e;
      }

      if (tempCert == null) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Signing key pair not found in keystore view [{0}] under alias [{1}].The default keystore is [{2}]. Default alias is [{3}]. Authentication stack: [{4}]. Check the login module options and UME properties.", new Object[] {keystore, alias, UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
        m_exception = new DetailedLoginException("Signing key pair not found.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
        throw (LoginException) m_exception;
      }

      cert = (iaik.x509.X509Certificate) tempCert;

      // add trusted certificates and signer certificate to
      // evaluation properties
      m_options.put("certificates", certs.toArray(new X509Certificate[0]));
      m_options.put("cert", cert);

      // check wether ticket is trusted
      SAPLogonTicketHelper.evaluateTicket(ticket, m_options);
      
      IUMConfiguration config = InternalUMFactory.getConfiguration();
	  boolean isVerifyOn = config.getBooleanDynamic(ILoginConstants.SSOASSERTION_RECIPIENT_VALIDATE, DEFAULT_RECIPIENT_VALIDATE_VALUE);
	  
	  if (isVerifyOn) {
		  
		InfoUnit recipientSIDInfoUnit = ticket.getInfoUnit(InfoUnit.ID_RECIPIENT_SID);
		InfoUnit recipientClientInfoUnit = null;
		  
		if (recipientSIDInfoUnit != null) {
		  recipientClientInfoUnit= ticket.getInfoUnit(InfoUnit.ID_RECIPIENT_CLIENT);
	
		  if (recipientClientInfoUnit != null) {
			String clientID = InternalUMFactory.getClient();
			String recipientSID = recipientSIDInfoUnit.getString(DEFAULT_INFO_UNIT_ENCODING);
			recipientSID = SAPLogonTicketHelper.removeEndingSpaces(recipientSID);
			String recipientClient = recipientClientInfoUnit.getString(DEFAULT_INFO_UNIT_ENCODING);
			
			//check if recipient SID and recipient client ID equals to own SID and client
			if (sid == null || clientID == null || !clientID.equals(recipientClient) || !sid.equals(recipientSID)) {
			  if (LOCATION.beInfo()) {
		        LOCATION.infoT("SAP Authentication Assertion Ticket has recipient SID [{0}] and recipient " +
		        		"client ID [{1}], which are not equal to own SID and client ID [{2}, {3}].", new Object[] {recipientSID, recipientClient, sid, clientID});
			  }
				
			  throw new DetailedLoginException("Client ID or System ID are not the same as the recipient.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
			} else {
			  LOCATION.pathT("Recipient SID and Client ID are validated successfully.");
			}
		  }
		}
	  }

      // get user from ticket
      getUserFromTicket(ticket);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("The verification of assertion ticket of user [{0}] is successful. Authentication stack: [{1}].", new Object[] {m_username, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      refreshUserInfo(m_username);

      getAuthschemeFromTicket (ticket);
      if (LOCATION.beInfo()) {
        LOCATION.infoT("SAP Authentication Assertion Ticket contains authentication scheme [{0}]. Authentication stack: [{1}].", new Object[] {m_authscheme, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      verifyAuthschemesOk ();

      // remember state of login method
      m_succeeded = true;
      b = new Boolean(true);
      return true;
    } catch (Throwable e) {
       if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
      }
    } finally {
      try {
        removeSSOHeader(stringTicket);
      } catch (Exception e) {
        if (LOCATION.beInfo()) {
          LOCATION.traceThrowableT(Severity.INFO, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
      }

      // logging info
      LOCATION.exiting(METHOD, b);
    } //end login()
  }


  /**
   *
   * @throws LoginException
   */
  private void verifyAuthschemesOk () throws LoginException {
    IAuthScheme is_authscheme = null;
    IAuthScheme should_authscheme = null;

    is_authscheme = InternalUMFactory.getAuthSchemeFactory().getAuthScheme(m_authscheme);
    should_authscheme = InternalUMFactory.getAuthSchemeFactory().getAuthScheme(m_authscheme_to_be);

    if (is_authscheme.getPriority()<should_authscheme.getPriority()) {
      if (LOCATION.beInfo()) {
        LOCATION.infoT("authscheme not sufficient: [{0}] < [{1}]. Authntication stack: [{2}]. Authenticated user: [{3}]", new Object[] {is_authscheme.getName (), should_authscheme.getName(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_username});
      }
      throw new DetailedLoginException ("Authentication scheme not sufficient: " + is_authscheme.getName () + " < " + should_authscheme.getName(), LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
    }
  }

  /**
   *
   * @param ticket
   */
  private void getAuthschemeFromTicket(Ticket ticket) {
    InfoUnit iu;

    try {
      iu = ticket.getInfoUnit(InfoUnit.ID_AUTHSCHEME);

      m_authscheme = "default";
      if (iu != null) {
        m_authscheme = iu.getString("UTF8");
      }
    } catch (TicketException e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT (Severity.WARNING, "Ticket parsing problem. Authntication stack: [{0}]. Authenticated user: [{1}]", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_username}, e);
      }
    } catch (UnsupportedEncodingException e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "Encoding problem. Authntication stack: [{0}]. Authenticated user: [{1}]", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_username}, e);
      }
    }
  }

  /**
   *
   * @param ticket
   *
   * @throws UnsupportedEncodingException
   * @throws TicketException
   * @throws NoLogonDataAvailableException
   * @throws UMException
   */
  private void getUserFromTicket (Ticket ticket) throws UnsupportedEncodingException, TicketException, NoLogonDataAvailableException, UMException {
    
    if (m_umeadapter.usePortalUserId()) {
      m_username = ticket.getUser("portal");
    }
    
    if (m_username == null) {
      String r3user = ticket.getUser();

      if (r3user == null) {
        if (LOCATION.beError()) {
          LOCATION.errorT("Corrupted ticket: No UME user and no r3 user. Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
          throw new TicketException("Ticket without UME user and r3 user");
        } // if (LOCATION.beError ())
      }

      r3user = SAPLogonTicketHelper.removeEndingSpaces(r3user);
      String user_uid = null;

      // if m_username==null, we retry everything with the mastersystem.
      if (m_username == null) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Inverse mapping not successful (user=" + r3user + "). Trying mastersystem.");
        }

        user_uid = UMFactory.getUserMapping().getInverseMappingData(SAPLogonTicketHelper.getTenantAwareUsername(r3user, m_options), null);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("User {0} found in ticket. The mapped user is {1}", new Object[] {r3user, user_uid});
        }

        if (user_uid == null) {
          if (CATEGORY.beError()) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000113",  "Inverse user mapping not successful (user={0}). Check user mapping configuration.", new Object[] {r3user});
          }
          throw new UMException("Inverse user mapping failed.");
        } else {
          IUser user = UMFactory.getUserFactory().getUser(user_uid);

          if (null == user) {
            if (LOCATION.beWarning()) {
              LOCATION.warningT("Inverse user mapping [user={0}, mastersystem] not found", new Object[] {r3user});
            } // if (LOCATION.beWarning ())
          } else {
            m_username = user.getUserAccounts()[0].getLogonUid();
          }
        }
      }
    } else {
      m_username = SAPLogonTicketHelper.removeEndingSpaces(m_username);
    }

    m_username = SAPLogonTicketHelper.getTenantAwareUsername(m_username, m_options);
  }

  /** Method to commit the logged in user. <br>
   * If the user had a valid SAP Authentication Assertion Ticket,
   * a user principal and a credential object with the SAP Authentication Assertion Ticket
   * is created and added to the subject.
   * @see javax.security.auth.spi.LoginModule#commit()
   */
  public boolean commit() throws LoginException {
    final String METHOD = "commit()";
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      // if login failed, only internal data needs to be reset
      if (m_succeeded == false) {
        m_username = null;
        m_subject = null;
        m_handler = null;
        m_options = null;
        m_ticketString = null;

        if (LOCATION.beDebug()) {
          LOCATION.debugT("Internal Login Module data has been reset.");
        }
        b = new Boolean(false);
        return false;
      }

      // add principal to subject
      m_principal = new Principal(m_username);
      m_principal.setAuthenticationMethod(Principal.AUTH_METHOD_SAP_ASSERTION_TICKET);
      m_subject.getPrincipals().add(m_principal);
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Added principal [{0}] of class [{1}] to Subject.", new Object[] {m_username, m_username.getClass()});
      }

      if (m_authscheme_to_be != null) {
        m_authscheme_principal = new AuthSchemePrincipal(m_authscheme_to_be);

        m_subject.getPrincipals().add(m_authscheme_principal);
      }

      // add ticket credential to subject
      m_credential = new SAPAuthenticationAssertionTicketCredential(m_ticketString, m_username);
      m_subject.getPrivateCredentials().add(m_credential);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Authentication Assertion Ticket added to private credentials.");
      }

      b = new Boolean(true);
      return true;
    } finally {
      // logging info
      if (b != null) {
        LOCATION.exiting(METHOD, b);
      } else {
        LOCATION.exiting(METHOD);
      }
    }
  } // end commit()

  /** Method to abort the login of the user.
   * @see javax.security.auth.spi.LoginModule#abort()
   */
  public boolean abort() throws LoginException {
    final String METHOD = "abort()";
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      rollBack();

      b = new Boolean(true);
      return true;
    } finally {
      // logging info
      if (b != null) {
        LOCATION.exiting(METHOD, b);
      } else {
        LOCATION.exiting(METHOD);
      }
    }
  } //end abort()

  /** Method to logout the user.
   * @see javax.security.auth.spi.LoginModule#logout()
   */
  public boolean logout() throws LoginException {
    final String METHOD = "logout()";
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      rollBack();

      b = new Boolean(true);
      return true;
    } finally {
      // logging info
      if (b != null) {
        LOCATION.exiting(METHOD, b);
      }
    }
  } //end logout()

  /**
   * Internal method to reset all data for this login module
   */
  void rollBack() {
    // remove principal
    if (m_subject != null) {
      if (m_principal != null) {
        m_subject.getPrincipals().remove(m_principal);
      }

      // remove credential
      if (m_credential != null) {
        m_subject.getPrivateCredentials().remove(m_credential);
      }

      if (m_authscheme_principal != null) {
        m_subject.getPrincipals().remove(m_authscheme_principal);
      }
    }

    // reset internal state
    m_username = null;
    m_subject = null;
    m_handler = null;
    m_options = null;
    m_principal = null;
    m_credential = null;
    m_username = null;
    m_ticketString = null;
    m_succeeded = false;

    if (LOCATION.beDebug()) {
	    LOCATION.debugT("Internal Login Module data has been reset.");
    }
  }

  /** Internal method to get SAP Assertion Ticket */
  private String getSAPAssertionTicket() throws LoginException, UnsupportedCallbackException, IOException {
    // check wether callback handler is available
    if (m_handler == null) {
      m_exception = new DetailedLoginException("Error: No CallbackHandler available to gather authentication information from the user. Authentication stack: " + m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_GET_SAP_LOGON_TICKET);
      throw (LoginException) m_exception;
    }

    // retrieve SAP Authentication Assertion Ticket from handler
    Callback[] callbacks = new Callback[1];

    callbacks[0] = new HttpGetterCallback();
    ((HttpGetterCallback) callbacks[0]).setType(HttpCallback.HEADER);
    ((HttpGetterCallback) callbacks[0]).setName(TICKET_HEADER);

    m_handler.handle(callbacks);

    return (String) ((HttpGetterCallback) callbacks[0]).getValue();

  }

  private void removeSSOHeader(String ticket) throws UnsupportedCallbackException, IOException, LoginException {
    // check wether a callback handler is available
    if (m_handler == null) {
      m_exception = new DetailedLoginException("Error: no CallbackHandler available to gather authentication information from the user. Authentication stack: " + m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      throw (LoginException) m_exception;
    }

//    if (this.m_umeadapter.isUMEConfigurationActive() ) {
		  // set ticket cookie using advanced features of UME adapter
//		  this.m_umeadapter.setTicketAsCookie(m_handler, value, user);
//    } else {
		  // set ticket cookie standard way
    Callback[] callbacks = new Callback[1];

    callbacks[0] = new HttpSetterCallback();
    ((HttpSetterCallback) callbacks[0]).setType(HttpCallback.REMOVE_HEADER);
    ((HttpSetterCallback) callbacks[0]).setName(TICKET_HEADER);
    ((HttpSetterCallback) callbacks[0]).setValue(ticket);

    m_handler.handle(callbacks);
//    }
  }

}

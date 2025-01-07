package com.sap.security.core.server.jaas;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.login.SecurityContext;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.AuthSchemePrincipal;
import com.sap.security.api.logon.IAuthScheme;
import com.sap.security.api.ticket.InfoUnit;
import com.sap.security.api.ticket.TicketException;
import com.sap.security.api.umap.NoLogonDataAvailableException;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.ticket.imp.Ticket;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * <p>JAAS Login Module for the Evaluation of SAP Logon Tickets.
 * Together with the
 * {@link CreateTicketLoginModule}
 * this jaas login module can be used to enable SAP Logon Ticket
 * based Single Sign-On.
 * </p>
 *
 * <p>This login module does authenticates a user by means of his SAP
 * Logon Ticket. It is first checked wether the user has already been
 * authenticated and can
 * be found in the usercache. If this is not the case, the ticket will
 * be evaluated. After successful evaluation an J2EE Engine Principal
 * and a credential object with the SAP Logon Ticket will be added
 * to the subject.</p>
 *
 * <p>A login module stack configuration could look like the following
 * com.sap.security.core.server.jaas.EvaluateTicketLoginModule sufficient
 * trustedsys1="B6Q, 050" <br>
 * trustediss1="CN=Test, O=SAP-AG, C=DE" <br>
 * trusteddn1="CN=Test,O=SAP-AG,C=DE"; <br>
 * com.sap.engine.services.security.server.jaas.ClientCertLoginModule
 * optional <br>
 * com.sap.security.core.server.jaas.CreateTicketLoginModule sufficient <br>
 * com.sap.engine.services.security.server.jaas.BasicPasswordLoginModule
 * optional <br>
 * com.sap.security.core.server.jaas.CreateTicketLoginModule sufficient <br>
 * </p>
 *
 * <p>Copyright (c) 2003 SAP AG</p>
 * @version 1.0
 */
public class EvaluateTicketLoginModule extends AbstractLoginModule {

  /** subject representing the user */
  Subject m_subject;

  /** shared state with the other login modules */
  Map m_sharedState;

  /** callback handler for retrieving logon data */
  CallbackHandler m_handler;

  /** modified options for this login module */
  Properties m_options= new Properties();

  /** ticket credential for subject */
  SAPLogonTicketCredential m_credential= null;

  /** SAP Logon Ticket */
  String m_ticketString= null;

  /** principal for subject */
  Principal m_principal= null;

  /** authscheme in the ticket */
  String m_authscheme = null;

  /** authscheme of the stack I'm invoked in */
  String m_authscheme_to_be;

  /** authscheme principal */
  AuthSchemePrincipal m_authscheme_principal;

  /** internal state of login */
  boolean m_succeeded= false;

  /** Exception dummy */
  Exception m_exception;

  /** for extended UME ticket features */
  private UMEAdapter m_umeadapter = null;
  
  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION + ".EvaluateTicketLoginModule");
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);

  /** default time tolerance for ticket validity */
  private static final int DEFAULT_TIME_TOL = 3;

  /**  time tolerance for ticket validity */
  private int m_timetolerance;

  /** username */
  String m_username;

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
  public void initialize(
    Subject subject,
    CallbackHandler handler,
    Map sharedState,
    Map options) {

    final String METHOD= "initialize()";
    try {
      // logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { subject, handler });
      }

      super.initialize(subject, handler, sharedState, options);

      // store informations passed by the LoginContext
      m_subject= subject;
      m_handler= handler;
      m_sharedState= sharedState;
      m_options.putAll(options);

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of EvaluateTicketLoginModule in [{0}] authentication stack are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }

      m_umeadapter = new UMEAdapter(sharedState,m_options);
	    m_options = m_umeadapter.getMergedOptions();
      if (LOCATION.beDebug()) {
        LOCATION.logT(Severity.DEBUG, "The options of EvaluateTicketLoginModule in [{0}] authentication stack after merge with UME properties are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }

      // set default value for properties that
      // have not been specified in the login module options
      Object option;

      // option for internal use only - not documented!
      option= (String) m_options.get("timetolerance");
      if (option == null || option.equals(""))
        m_timetolerance= DEFAULT_TIME_TOL;
      else
        m_timetolerance= Integer.parseInt((String) option);

      m_authscheme_to_be = (String) m_options.get ("j_authscheme");
      if (m_authscheme_to_be==null)
        m_authscheme_to_be = "default";

      option = m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME);
      if (option != null) {
        m_options.put(SAPLogonTicketHelper.AUTH_NAME, option);
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of EvaluateTicketLoginModule in [{0}] authentication stack after adding the default values are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  } // end initialize(...)

  /** <p>Method to authenticate the user by means of his
   * SAP Logon Ticket.</p>
   * <p>It will first be checked wether the SAP Logon Ticket of the
   * user is still valid. If this is not the case,
   * the MYSAPSSO2 ticket will be
   * removed and the login method returns false. If the ticket is
   * still valid, the user will be searched in the usercache. If the
   * user is found, the login
   * method returns true. Otherwise it will be checked
   * wether the SAP Logon Ticket is issued by a trusted system.
   * All trusted Systems can be specified in the
   * options of this login module.<br>
   * @see javax.security.auth.spi.LoginModule#login()
   */
  public boolean login() throws javax.security.auth.login.LoginException {
    final String METHOD= "login()";
    Boolean b= null;
    KeyStore store;
    java.security.cert.X509Certificate tempCert;
    X509Certificate cert;
    Vector certs;
    try {
      // logging info
      LOCATION.entering(METHOD);

      // get SAP Logon Ticket
      Ticket ticket= null;
      m_ticketString= SAPLogonTicketHelper.undoReplaceChar(getSAPLogonTicket());
      if (m_ticketString == null || m_ticketString.equals("")) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Received no SAPLogonTicket. Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
        }
        return false;
      }

      // create ticket object from String
      ticket= new Ticket();
      ticket.setTicket(m_ticketString);
      if (LOCATION.beInfo()) {
        LOCATION.infoT("SAP Logon Ticket received. Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Ticket: \n [{0}]. Authentication stack: [{1}].", new Object[] {ticket.toString(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      // check wether the ticket is still valid
      if (!ticket.isValid(m_timetolerance)) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("SAP Logon Ticket has expired or it is not valid yet. The ticket is valid from {0} until {1} and now it is {2}. Authentication stack: [{3}].", new Object[] {ticket.getStartValidDate().getTime(), ticket.getExpirationDate().getTime(), Calendar.getInstance (new SimpleTimeZone(0, "GMT")).getTime(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
        }

        // if ticket is not valid, overwrite MYSAPSSO2 Cookie with ""
        setSSOCookie("","");
        
        throw new DetailedLoginException("SAP Logon Ticket has expired or it is not valid yet.", LoginExceptionDetails.SAP_LOGON_TICKET_HAS_EXPIRED);
      } else {
        // if ticket is valid, search user in security session
        m_username = getUserFromSecuritySesion(m_ticketString);
        if (m_username != null) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Authentication succeeded. The authenticated user is [{0}]. Authentication stack: [{1}].", new Object[] {m_username, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
          }

          refreshUserInfo(m_username);

          // ticket comes from cache, so it doesn't
          // need to be verified
          ticket.setEnforceVerify (false);
          getAuthschemeFromTicket (ticket);
          if (LOCATION.beInfo()) {
            LOCATION.infoT("Ticket contains authscheme [{0}]. Authentication stack: [{1}].", new Object[] {m_authscheme, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
          }

          verifyAuthschemesOk ();

          m_succeeded= true;
          b= new Boolean(true);
          return true;
        }
      }

      // if user is not found in cache, a new ticket validation has
      // to be made. Start by getting the SID
      String sid= (String) m_sharedState.get("System-ID");
	  if (sid != null){
	  	m_options.put("system", sid);
      if (LOCATION.beDebug()) {
		    LOCATION.debugT("Found SID: [{0}].", new Object[] {sid});
      }
	  }
	  else{
		m_exception=
			new DetailedLoginException("Can not continue. Own SID not available.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
		throw (LoginException) m_exception;
	  }

      // get keystore properties.
      String keystore= (String) m_options.get("keystore");
      String password= (String) m_options.get("password");
      String alias= (String) m_options.get("alias");

      // get ticket keystore view
      store= SAPLogonTicketHelper.getTicketKeyStore(keystore, password, m_options);

      if (store == null) {
        if (LOCATION.beError()) {
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
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000115", "The certificates contained in keystore view [{0}] cannot be retrieved. Authentication stack: [{1}]. The default kestore view is [{2}]. The default keypair alias is [{3}]. Check the login module options and UME properties.", new Object[] {keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
        LOCATION.traceThrowableT(Severity.ERROR, "The certificates contained in keystore view [{0}] cannot be retrieved. Authentication stack: [{1}].", new Object[] {keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw e;
      }

      try {
        tempCert = (java.security.cert.X509Certificate) store.getCertificate(alias);
      } catch (Exception e) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000115", "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {alias, keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
        LOCATION.traceThrowableT(Severity.ERROR, "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}].", new Object[] {alias, keystore, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw e;
      }

      if (tempCert == null) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000117", "Keypair for signing not found in keystore view [{0}] under alias [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {keystore, alias, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
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

      // get user from ticket
      getUserFromTicket (ticket);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Ticket verify of user [{0}] is successful. Authentication stack: [{1}].", new Object[] {m_username, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      refreshUserInfo(m_username);

      getAuthschemeFromTicket (ticket);
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Ticket contains authscheme [{0}]. Authentication stack: [{1}].", new Object[] {m_authscheme, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      verifyAuthschemesOk ();

      // remember state of login method
      m_succeeded= true;
      b= new Boolean(true);
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
      // logging info
      LOCATION.exiting(METHOD, b);
    } //end login()
  }


  /**
    *
    */
    private void verifyAuthschemesOk ()
        throws LoginException
    {
        IAuthScheme is_authscheme = null,
                    should_authscheme = null;

        is_authscheme = InternalUMFactory.getAuthSchemeFactory().getAuthScheme (m_authscheme);
        if (is_authscheme == null) {
          if (LOCATION.beError()) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000118", "The authscheme found in the ticket does not exists. Authscheme = {0} ", new Object[] { m_authscheme } );
          }
          throw new DetailedLoginException ("The authscheme found in the ticket does not exists.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
        }

        should_authscheme = InternalUMFactory.getAuthSchemeFactory().getAuthScheme (m_authscheme_to_be);
        if (should_authscheme == null) {
          if (LOCATION.beError()) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000119", "The target authscheme does not exists. Authscheme = {0} ", new Object[] { m_authscheme_to_be } );
          }
          throw new DetailedLoginException ("The target authscheme does not exists.", LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
        }
         
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Ticket authscheme is {0} with priority {1}.", new Object[] { is_authscheme.getName(), new Integer(is_authscheme.getPriority()) } );
          LOCATION.debugT("Target authscheme is {0} with priority {1}.", new Object[] { should_authscheme.getName(), new Integer(should_authscheme.getPriority()) } );
        }
          
        if (is_authscheme.getPriority()<should_authscheme.getPriority()) {
          if (LOCATION.beInfo()) {
            LOCATION.infoT("authscheme not sufficient: [{0}] < [{1}]. Authntication stack: [{2}]. Authenticated user: [{3}]", new Object[] {is_authscheme.getName (), should_authscheme.getName(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_username});
          }
          throw new DetailedLoginException ("authscheme not sufficient: " + is_authscheme.getName () + "<" + should_authscheme.getName(), LoginExceptionDetails.SAP_LOGON_TICKET_IS_NOT_TRUSTED);
        }
    }

/**
   * @param ticket
   */
  private void getAuthschemeFromTicket(Ticket ticket)
  {
      InfoUnit iu;
      try {
          iu = ticket.getInfoUnit(InfoUnit.ID_AUTHSCHEME);

          m_authscheme = "default";
          if (iu!=null) {
              m_authscheme = iu.getString("UTF8");
          }
      } catch (TicketException e) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Ticket parsing problem. Authntication stack: [{0}]. Authenticated user: [{1}]", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_username}, e);
        }
      } catch (UnsupportedEncodingException e) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Encoding problem. Authntication stack: [{0}]. Authenticated user: [{1}]", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_username}, e);
        }
      }
  }

/**
   * @param ticket
   */
  private void getUserFromTicket (Ticket ticket) throws UnsupportedEncodingException, TicketException, NoLogonDataAvailableException, UMException {
    if (m_umeadapter.usePortalUserId()) {
      m_username = ticket.getUser("portal");
    }

    if (m_username == null) {
      String userid = ticket.getUser ();

      if (userid != null) {
        userid = SAPLogonTicketHelper.removeEndingSpaces(userid);
      }

      userid = SAPLogonTicketHelper.getTenantAwareUsername(userid, m_options);
      String user = UMFactory.getUserMapping().getInverseMappingData (userid, null);
      if (LOCATION.beDebug()) {
      	LOCATION.debugT("User {0} found in ticket. The mapped user is {1}", new Object[] {userid, user});
      }

      if (user != null) {
        IUser iuser = UMFactory.getUserFactory().getUser (user);

        m_username  = iuser.getUserAccounts()[0].getLogonUid();
      } else {
        if (CATEGORY.beError()) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000120", "Inverse mapping for user [{0}] not possible. Check user mapping configuration.", new Object[] {userid});
        }
        throw new UMException("Inverse user mapping failed.");
      }
    } else {
      m_username = SAPLogonTicketHelper.removeEndingSpaces(m_username);
    }
    m_username = SAPLogonTicketHelper.getTenantAwareUsername(m_username, m_options);

  }

/** Method to commit the logged in user. <br>
   * If the user was not found in cache but had a valid SAP Logon Ticket,
   * a user principal and a credential object with the SAP Logon Ticket
   * is created and added to the subject.
   * @see javax.security.auth.spi.LoginModule#commit()
   */
  public boolean commit() throws LoginException {
    final String METHOD= "commit()";
    Boolean b= null;
    try {
      // logging info
      LOCATION.entering(METHOD);

      // if login failed, only internal data needs to be reset
      if (m_succeeded == false) {
        m_username= null;
        m_subject= null;
        m_handler= null;
        m_options= null;
        m_ticketString= null;
        if (LOCATION.beDebug()) {
		      LOCATION.debugT("Internal Login Module data has been reset.");
        }
        b= new Boolean(false);
        return false;
      }

      // add principal to subject
      m_principal= new Principal(m_username);
      m_principal.setAuthenticationMethod(Principal.AUTH_METHOD_SAP_LOGON_TICKET);
      m_subject.getPrincipals().add(m_principal);
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Added principal [{0}] of class [{1}] to Subject.", new Object[] {m_username, m_username.getClass()});
      }

      String max_authscheme = getMaxAuthScheme (m_authscheme_to_be, m_authscheme);
      m_authscheme_principal = new AuthSchemePrincipal (max_authscheme);

      m_subject.getPrincipals().add (m_authscheme_principal);

      // add ticket credential to subject
      m_credential= new SAPLogonTicketCredential(m_ticketString, m_username);
      m_subject.getPrivateCredentials().add(m_credential);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Logon Ticket added to private credentials.");
      }

      b= new Boolean(true);
      return true;
    } finally {
      // logging info
      if (b != null)
        LOCATION.exiting(METHOD, b);
      else
        LOCATION.exiting(METHOD);
    }
  } // end commit()

  /**
 * @param m_authscheme_to_be
 * @param m_authscheme
 * @return The authscheme that has the higher priority
 */
  private String getMaxAuthScheme (String m_authscheme_to_be, String m_authscheme)
  {
      IAuthScheme is_authscheme = null,
                  should_authscheme = null;

      if (m_authscheme_to_be==null)
        return m_authscheme;

      is_authscheme = InternalUMFactory.getAuthSchemeFactory().getAuthScheme (m_authscheme);
      should_authscheme = InternalUMFactory.getAuthSchemeFactory().getAuthScheme (m_authscheme_to_be);

      if (is_authscheme.getPriority()>should_authscheme.getPriority()) {
          return is_authscheme.getName ();
      }
      else {
          return should_authscheme.getName ();
      }
  }

/** Method to abort the login of the user.
   * @see javax.security.auth.spi.LoginModule#abort()
   */
  public boolean abort() throws LoginException {
    final String METHOD= "abort()";
    Boolean b= null;
    try {
      // logging info
      LOCATION.entering(METHOD);

      rollBack();

      b= new Boolean(true);
      return true;
    } finally {
      // logging info
      if (b != null)
        LOCATION.exiting(METHOD, b);
      else
        LOCATION.exiting(METHOD);
    }
  } //end abort()

  /** Method to logout the user.
   * @see javax.security.auth.spi.LoginModule#logout()
   */
  public boolean logout() throws LoginException {
    final String METHOD= "logout()";
    Boolean b= null;
    try {
      // logging info
      LOCATION.entering(METHOD);

      rollBack();

      b= new Boolean(true);
      return true;
    } finally {
      // logging info
      if (b != null)
        LOCATION.exiting(METHOD, b);
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

      if (m_authscheme_principal!=null) {
        m_subject.getPrincipals().remove (m_authscheme_principal);
      }
    }

    // reset internal state
    m_username= null;
    m_subject= null;
    m_handler= null;
    m_options= null;
    m_principal= null;
    m_credential= null;
    m_username= null;
    m_ticketString= null;
    m_succeeded= false;
    if (LOCATION.beDebug()) {
	    LOCATION.debugT("Internal Login Module data has been reset.");
    }
  }

  /** Internal method to get SAP Logon Ticket */
  String getSAPLogonTicket()
    throws LoginException, UnsupportedCallbackException, IOException {

    // check wether callback handler is available
    if (m_handler == null) {
      m_exception = new DetailedLoginException("Error: No CallbackHandler available to garner authentication information from the user. " + m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_GET_SAP_LOGON_TICKET);
      throw (LoginException) m_exception;
    }

    // retrieve SAP Logon Ticket from handler
    Callback[] callbacks= null;
    callbacks= new Callback[1];
    callbacks[0]= new HttpGetterCallback();
    ((HttpGetterCallback) callbacks[0]).setType(HttpCallback.COOKIE);
    ((HttpGetterCallback) callbacks[0]).setName("MYSAPSSO2");
    m_handler.handle(callbacks);
    return (String) ((HttpGetterCallback) callbacks[0]).getValue();

  } //end getSAPLogonTicket()


  /** Internal method to pass MYSAPSSO2 cookie */

  void setSSOCookie(String value, String user)
    throws UnsupportedCallbackException, IOException, LoginException {

    // check wether a callback handler is available
    if (m_handler == null) {
      m_exception = new DetailedLoginException("Error: No CallbackHandler available to garner authentication information from the user. Authentication stack: " + m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      throw (LoginException) m_exception;
    }

		// set ticket cookie using advanced features of UME adapter
		this.m_umeadapter.setTicketAsCookie(m_handler, value, user);

    // trace information
    if (LOCATION.beInfo()) {
      if (value.equals("")) {
        LOCATION.infoT("Callback handler [{0}] adviced to overwrite MYSAPSSO2 cookie with empty string. Authentication stack: [{1}].", new Object[] {m_handler.getClass().getName(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      } else {
        LOCATION.infoT("Callback handler [{0}] adviced to set SAP Logon Ticket in MYSAPSSO2 cookie. Authentication stack: [{1}].", new Object[] {m_handler.getClass().getName(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }
    }
  } //end setSSOCookie(String value)

  private String getUserFromSecuritySesion(String ticket) {
    try {
      SecurityContext securityContext = SecurityServerFrame.getCurrentSecurityContextObject();
      SecuritySession securitySession = securityContext.getSession();

      Subject subject = securitySession.getSubject();
      Set ticketCredentials = subject.getPrivateCredentials(SAPLogonTicketCredential.class);

      if ((ticketCredentials != null) && !ticketCredentials.isEmpty()) {
        Object[] tickets = ticketCredentials.toArray();

        for (int i = 0; i < tickets.length; i++) {
          SAPLogonTicketCredential ticketCredential = (SAPLogonTicketCredential) tickets[i];
          String ticketString = ticketCredential.getTicketString();

          if (ticketString.equals(ticket)) {
            java.security.Principal principal = securitySession.getPrincipal();
            String userName = principal.getName();

            if (LOCATION.beDebug()) {
              LOCATION.debugT("The given ticket is found in the current security session. The authenticated user is: [{0}].", new Object[] {userName});
            }

            return userName;
          }
        }

        if (LOCATION.beDebug()) {
          LOCATION.debugT("The given ticket does not equal to any of the tickets stores in the security session.");
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("No SAP Logon Ticket found in the security session.");
        }
      }

      return null;
    } catch (Exception e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "Exception while checking SAP Logon Ticket against the current security session.", e);
      }

      return null;
    }
  }

}
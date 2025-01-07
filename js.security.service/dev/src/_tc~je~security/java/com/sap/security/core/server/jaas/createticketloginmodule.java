package com.sap.security.core.server.jaas;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.security.api.UMException;
import com.sap.security.api.logon.AuthSchemePrincipal;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.imp.TenantFactory;
import com.sap.security.core.ticket.imp.Ticket;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * <p>JAAS Login Module for the Creation of SAP Logon Tickets.
 * Together with the {@link EvaluateTicketLoginModule}
 * this login module can be used to enable SAP Logon Ticket
 * based Single Sign-On. </p>
 * <p>This login module does not authenticates the user.
 * Instead it uses the sharedState to find out if a user
 * has been successfully authenticated by another
 * {@link javax.security.auth.spi.LoginModule}.
 * If <code>AbstractLoginModule.NAME</code> is
 * set, a SAP Logon Ticket for this user will be
 * created. Otherwise the {@link CreateTicketLoginModule}
 * will do nothing. </p>
 * <p>After succeesful authentication the ticket is stored in the
 * subject. The user is stored in the usercache for
 * faster retrieval, key SAP Logon Ticket. </p>
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
/** Todo */
public class CreateTicketLoginModule extends AbstractLoginModule {
//public class CreateTicketLoginModule implements LoginModule {
  
  private static final String OPTION_COMMIT_ONLY = "commitOnly";
  private static final String OPTION_COMMIT_ONLY_TRUE = "true";
  
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

  /** key for the user cache entry */
  Object m_cacheKey= null;

  /** indicator, if MYSAPSSO2 Cookie has been set */
  boolean m_cookieSet= false;

  /** internal state of login */
  boolean m_succeeded= false;

  /** Exception dummy */
  Exception m_exception= null;

  /** for extended UME ticket features */
  private UMEAdapter m_umeadapter = null;

  /** for session logout */
  private boolean isSessionLogout = false;
  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION + ".CreateTicketLoginModule");

  /** default severity */
  static final int SEVERITY;

  /** used resource bundle */
  //	private static final String RESOURCE_BUNDEL;

  private String previousTicket = null;
  private String authenticationStack;
  
  static {
    SEVERITY= Severity.INFO;
    //		RESOURCE_BUNDEL = "/System/Security/Jaas";

  }

  /**
   * <p>Initialize the <code>CreateTicketLoginModule</code>.
   * This method is called by the {@link javax.security.auth.login.LoginContext}.
   * The purpose of this method is to initialize the
   * <code>LoginModule</code> with all relevant information. </p>
   * <p>The following <code>options</code> are available: <br>
   * <p>
   * debug - 0:debug off (default), 1:debug on</p>
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
        LOCATION.entering(METHOD, new Object[] { subject, handler, sharedState, options });
      }

      super.initialize(subject, handler, sharedState, options);

      // store informations passed by the LoginContext
      m_subject= subject;
      m_handler= handler;
      m_sharedState= sharedState;
      m_options.putAll(options);
      authenticationStack = (String) m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME);

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of CreateTicketLoginModule in [{0}] authentication stack are: [{1}].", new Object[] {authenticationStack, m_options});
      }

      m_umeadapter = new UMEAdapter(sharedState,m_options);
      m_options = m_umeadapter.getMergedOptions();

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of CreateTicketLoginModule in [{0}] authentication stack after merge with UME properties are: [{1}].", new Object[] {authenticationStack, m_options});
      }

      // set default values for properties that
      // have not been specified in the login module options
      if (authenticationStack != null) {
        m_options.put(SAPLogonTicketHelper.AUTH_NAME, authenticationStack);
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of CreateTicketLoginModule in [{0}] authentication stack after adding the default values are: [{1}].", new Object[] {authenticationStack, m_options});
      }

      if (m_sharedState.get("sap.security.auth.session.logout") != null) {
        isSessionLogout = true;
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  } //end initialize


  /** Method to login the user. <br>
   * This login module does not authenticates the user by itself.
   * The login method returns true if a user has successfully
   * been authenticated by
   * another login module so that this login module will be able
   * to create a SAP Logon Ticket for this user in the commit method. <br>
   * The indication that a user has successfully been authenticated by
   * anothers login modules login method is that the user name has been written in
   * the shared state, key <code>AbstractLoginModule.NAME</code>.
   * @see javax.security.auth.spi.LoginModule#login()*/
  public boolean login() throws javax.security.auth.login.LoginException {
    final String METHOD= "login()";
    Boolean b= null;
    try {
      // logging info
      LOCATION.entering(METHOD);

      // check wether authenticated user is available
      /** Todo */
      Object option= m_sharedState.get(AbstractLoginModule.NAME);
	  // Object option= m_sharedState.get("javax.security.auth.login.name");
      if (option != null && !option.equals("")) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Authenticated user found: user={0}. Authentication stack: [{1}].", new Object[] {option, authenticationStack});
        }
        m_options.put("user", option);

        refreshUserInfo((String) option);

        m_succeeded= true;
        b =new Boolean(true);
        return true;
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("No authenticated user found.");
        }
		    b = new Boolean(false);
        return false;
      }

    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end login()


  /** Method to commit the logged in user. <br>
   * This login module does not authenticates the user.
   * It simply creates a SAP Logon Ticket for a user that has successfully
   * been authenticated by another login module.
   * The indication that also the commit method of the other
   * login module succeeded is that the username in
   * the shared state, key <code>AbstractLoginModule.NAME</code>
   * is still the same as in the login method.
   * If the SAP Logon Ticket has been created successfully it will
   * be passed to the http response. The user will be cached for faster
   * retrieval, key SAP Logon Ticket.
   * @see javax.security.auth.spi.LoginModule#commit()
   */
  public boolean commit() throws javax.security.auth.login.LoginException {
    final String METHOD= "commit()";

    String user;
    Boolean b= null;

    try {
      // logging info
      LOCATION.entering(METHOD);
      
      // if login failed, only internal data needs to be reset
      if (m_succeeded == false) {
        String commitOnlyValue = (String) m_options.get(OPTION_COMMIT_ONLY);
        String loginName = (String) m_sharedState.get(AbstractLoginModule.NAME);
        if (loginName != null && OPTION_COMMIT_ONLY_TRUE.equalsIgnoreCase(commitOnlyValue)) {
          if (LOCATION.beInfo()) {
            LOCATION.infoT("Authenticated user found on commit: user={0}. Authentication stack: [{1}].", new Object[] {loginName, authenticationStack});
          }          
          m_options.put("user", loginName);
        } else {
          m_subject= null;
          m_handler= null;
          m_sharedState= null;
          m_options= null;
          b = new Boolean(false);
          return false;
        }
      }

      Ticket ticket;
      String ticketString= null;

      // check wether the username is still in shared state
      // otherwise the commit method of the login module doing the
      // authentication failed and no SAP Logon Ticket shall be created
	  /** Todo */
	  Object option= m_sharedState.get(AbstractLoginModule.NAME);
	  // Object option= m_sharedState.get("javax.security.auth.login.name");
      if ((option != null) && option.equals(m_options.get("user"))) {
        user= (String) option;
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Authenticated user still in shared state.");
        }
      } else {
        m_exception = new DetailedLoginException("Commit method of login module doing the actual authentication seems to have failed. Authentication stack: " + authenticationStack, LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
        throw m_exception;
      }

      // get system id
	  option = m_options.get("system");
      if (option != null) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("System ID found: [{0}].", new Object[] {option});
        }
      } else {
        m_exception = new DetailedLoginException("Can not create SAP Logon Ticket. SID not available.", LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
        throw m_exception;
      }

      // get keystore properties
      String keystore= (String) m_options.get("keystore");
      String password= (String) m_options.get("password");
      String alias= (String) m_options.get("alias");

      // get keystore instance
      KeyStore store = SAPLogonTicketHelper.getTicketKeyStore(keystore, password, m_options);

      // check, if the keystore is emty is (at the moment keystoreManager
      // returns an emty keystore even if no keystore object exists)
      if (store == null) {
        if (CATEGORY.beError()) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.secsrv.000107", "Keystore view [{0}] does not exist or has no entries. Authentication stack: [{1}]. The default kestore view is [{2}]. " +
              "The possible reasons for that problem are: keystore does not exist, keystore has no entries, the user has no permission to read from the keystore view. " +
              "You can delete the TicketKeystore and restart the engine so that the engine automatically re-creates it.", 
          		new Object[] {keystore, authenticationStack, UMEAdapter.DEFAULT_KEYSTORE_VIEW});
        }
        throw new DetailedLoginException("Ticket keystore view does not exist or has no entries.", LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }

      // get signer certificate
      java.security.cert.X509Certificate tempCert = getCertificate(store, alias);
      if (tempCert == null) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000108", "Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {alias, keystore, authenticationStack, UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
        LOCATION.errorT("Certificate under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}].", new Object[] {alias, keystore, authenticationStack});
        throw new Exception("Cannot read certificate from keystore.");
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Certificate found under [{0}] alias. Authentication stack: [{1}].", new Object[] {alias, authenticationStack});
        }
      }

      PrivateKey key = getPrivateKey(store, alias, password);
      if (key == null) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.secsrv.000109", "Key under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}]. The default kestore view is [{3}]. The default keypair alias is [{4}]. Check the login module options and UME properties.", new Object[] {alias, keystore, authenticationStack, UMEAdapter.DEFAULT_KEYSTORE_VIEW, UMEAdapter.DEFAULT_KEYPAIR_ALIAS});
        LOCATION.errorT("Key under alias [{0}] cannot be retrieved from keystore view [{1}]. Authentication stack: [{2}].", new Object[] {alias, keystore, authenticationStack});
        throw new Exception("Cannot read private key from keystore.");
      }

      // store private key and certificate together with
      // other options
      X509Certificate cert = (iaik.x509.X509Certificate) tempCert;
      m_options.put("key", key);
      m_options.put("cert", cert);
      // Sets the R/3 user into
      // the m_options
      m_umeadapter.setMappedUser ();

      // handle BPO client stuff
      handleBPOClient ();

      if (null==m_options.get ("user") && null==m_options.get ("mappeduser")) {
          // no user name to be set => why proceed? Ticket without users
          // doesn't make sense.
          if (LOCATION.beInfo()) {
              LOCATION.infoT ("No portal and no ABAP user. Ticket creation aborted.");
          }
          b = new Boolean (false);
          m_cookieSet = false;
          return false;
      }

      // create SAP Logon Ticket
      ticket= SAPLogonTicketHelper.createTicket(m_options);
      ticketString= ticket.getTicket();
      if (LOCATION.beInfo()) {
        LOCATION.infoT("New SAP Logon Ticket for user [{0}] has been created. Authentication stack: [{1}].", new Object[] {user, authenticationStack});
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("The created ticket is: \n [{0}]. \nAuthentication stack: [{1}].", new Object[] {ticket.toString(), authenticationStack});
      }

      // add ticket credential to subject
      m_credential= new SAPLogonTicketCredential(ticketString, user);
      m_subject.getPrivateCredentials().add(m_credential);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Logon Ticket added to private credentials.");
      }

      // advice callback handler to set ticket as http request cookie,
      // with the critical char "+", "/", "=" replaced by hex
      ticketString= SAPLogonTicketHelper.replaceSpecialChar(ticketString);
      setSSOCookie(ticketString, user);
      setAuthschemePrincipal ();

      m_cookieSet = true;
      b = new Boolean(true);
      return true;

    } catch (Throwable e) {
      if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beError()) {
          LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end commit()


  /**
   * Adds AuthSchemePrincipal to the subject that holds the authenticated authscheme template
   */
  private void setAuthschemePrincipal () {
    String authscheme = (String) m_options.get (ILoginConstants.LOGON_AUTHSCHEME_ALIAS);
    
    if (authscheme != null) {
      AuthSchemePrincipal authSchemePrincipal = new AuthSchemePrincipal(authscheme);
      m_subject.getPrincipals().add(authSchemePrincipal);
      
      if (LOCATION.beDebug()) {
        LOCATION.debugT("AuthSchemePrincipal added to subject: {0}", new Object[] {authSchemePrincipal});
      }
    } else {
      if (LOCATION.beInfo()) {
        LOCATION.infoT("AuthSchemePrincipal not added to subject because authentication stack [{0}] does not have associated authscheme template.", 
            new Object[] {authenticationStack});
      }
    }
  }

    /**
     *
     */
    private void handleBPOClient ()
        throws LoginException
    {
        try {
            TenantFactory tf        = TenantFactory.getInstance ();
            String        bpoclient = null;
            if (tf.isBPOEnabled ()) {
                String user = (String) m_sharedState.get ("javax.security.auth.login.name");
                bpoclient = tf.getSAPLogonTicketClient (user);
                m_options.put ("client", bpoclient);
            }
            else {
                return ;
            }
        }
        catch (UMException e) {
          LOCATION.traceThrowableT(Severity.FATAL, e.getLocalizedMessage(), e);
          throw new LoginException ("Error while checking multi tenancy state.");
        }
    }

/** Method to abort the login of the user.
   * @see javax.security.auth.spi.LoginModule#abort()
   */
  public boolean abort() throws javax.security.auth.login.LoginException {
    final String METHOD= "abort()";
    Boolean b= null;
    try {

      // logging info
      LOCATION.entering(METHOD);

      // remove all things changed
      rollBack();

      b= new Boolean(true);
      return true;

    } catch (Throwable e) {
      if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end abort()


  /** Method to logout the user.
   * @see javax.security.auth.spi.LoginModule#logout()*/
  public boolean logout() throws javax.security.auth.login.LoginException {
    final String METHOD= "logout()";
    Boolean b= null;
    try {

      // logging info
      LOCATION.entering(METHOD);

      // remove all things changed
      rollBack();

      b= new Boolean(true);
      return true;

    } catch (Throwable e) {
      if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {authenticationStack}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end logout


  /** internal method to pass MYSAPSSO2 cookie */
  void setSSOCookie(String value, String user)
    throws UnsupportedCallbackException, IOException, LoginException {

    // check wether a callback handler is available
    if (m_handler == null) {
      m_exception=
        new DetailedLoginException(
          "Error: no CallbackHandler available to garner "
            + "authentication information from the user.", LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      throw (LoginException) m_exception;
    }

    if ((value != null) && (value != "")) {
      HttpGetterCallback oldTicketCallback = new HttpGetterCallback();
      oldTicketCallback.setType(HttpCallback.COOKIE);
      oldTicketCallback.setName("MYSAPSSO2");
      
      m_handler.handle(new Callback[] {oldTicketCallback});
      
      previousTicket = (String) oldTicketCallback.getValue();
    } else {
      if (previousTicket != null) {
        value = previousTicket;
      }
    }
    
		// set ticket cookie using advanced features of UME adapter
		this.m_umeadapter.setTicketAsCookie(m_handler, value, user);

    // trace information
    if (LOCATION.beInfo()) {
      if (value.equals(""))
        LOCATION.infoT("Callback Handler advised to overwrite MYSAPSSO2 cookie with empty string.");
      else
        LOCATION.infoT("Callback Handler advised to set SAP Logon Ticket in MYSAPSSO2 cookie.");
    }
  } // end setSSOCookie


  /**
   * internal method to reset all data for this login module
   */
  void rollBack() throws LoginException, UnsupportedCallbackException, IOException {

    // delete ticket credential if they have been set
    if ((m_subject != null) && (m_credential != null)){
      m_subject.getPrivateCredentials().remove(m_credential);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Logon Ticket has been removed from private credentials.");
      }
    }

    // delete SSO Cookie if it has been set or the session is logged out ()
    if (m_cookieSet || isSessionLogout) {
      setSSOCookie("", "");
    }

    // set all member variables to null resp. false
    m_subject= null;
    m_handler= null;
    m_sharedState= null;
    m_options= null;
    m_credential= null;
    m_cacheKey= null;
    m_cookieSet= false;
    m_succeeded= false;
    this.m_umeadapter = null;
  }
  
  private PrivateKey getPrivateKey(final KeyStore store, final String alias, final String password) {
    PrivateKey privateKey = 
      AccessController.doPrivileged(new PrivilegedAction<PrivateKey>() {
        public PrivateKey run() {
          try {
            return (PrivateKey) store.getKey(alias, password.toCharArray());
          } catch (Exception e) {
            LOCATION.traceThrowableT(Severity.ERROR, "Error reading private key from keystore.", e);
            return null;
          }
        }
      });
    return privateKey;
  }
  
  private java.security.cert.X509Certificate getCertificate(final KeyStore store, final String alias) {
    java.security.cert.X509Certificate cert = 
      AccessController.doPrivileged(new PrivilegedAction<java.security.cert.X509Certificate>() {
        public java.security.cert.X509Certificate run() {
          try {
            return (java.security.cert.X509Certificate) store.getCertificate(alias);
          } catch (Exception e) {
            LOCATION.traceThrowableT(Severity.ERROR, "Error reading certificate from keystore.", e);
            return null;
          }
        }
      });
    return cert;
  }
}
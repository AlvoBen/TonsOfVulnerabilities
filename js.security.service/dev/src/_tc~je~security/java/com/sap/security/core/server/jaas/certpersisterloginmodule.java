package com.sap.security.core.server.jaas;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.http.HttpGetterCallback;

import com.sap.security.api.IUserAccount;
import com.sap.security.api.UMFactory;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.security.cert.X509Certificate;

import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;


/**
 * Login module for mapping a client certificate to a user.
 * checks whether the subject's principals set contains a
 * com.sap.security.core.logon.imp.DefaultPrincipal and tries to
 * store the certificate in the request (if there is one) at
 * this DefaultPrincipal's useraccount.
 *
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
 * inclcert="0" <br>
 * com.sap.engine.services.security.server.jaas.BasicPasswordLoginModule
 * optional <br>
 * com.sap.security.core.server.jaas.CreateTicketLoginModule sufficient <br>
 * inclcert="0" </p>
 *
 * @version 1.0
 * @author Marc-Philip Werner
 * @author Guenther Wannenmacher
 */
public class CertPersisterLoginModule extends AbstractLoginModule {
    static Location LOCATION;
    static int SEVERITY;

    static {
      LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_CERTIFICATE_LOCATION + ".CertPersisterLoginModule");
      SEVERITY = Severity.INFO;
    }

    private boolean success = false;
    private boolean accmodified = false;
    private X509Certificate[] oldCerts = null;
    private IUserAccount uacc = null;

    /** shared state with the other login modules */
    protected Map m_sharedState;

    /** modified options for this login module */
    protected Properties m_options = new Properties();
    private boolean m_succeeded = false;
    protected UMEAdapter _ume_adapter;
    protected CallbackHandler _callback_handler;
    protected Subject _subject;

    /** Exception dummy */
    Exception m_exception = null;

    public void initialize(Subject subject, CallbackHandler handler, Map sharedState, Map options) {
      	final String METHOD = "initialize()";
      	
        LOCATION.entering(METHOD, new Object[] { subject, handler, sharedState, options });

        try {
            Properties props = new Properties();
            props.putAll(options);

            m_options = new Properties();
            m_options.putAll(options);
            m_sharedState = sharedState;
            _callback_handler = handler;
            _subject = subject;

            _ume_adapter = new UMEAdapter(sharedState, props);
        } finally {
            LOCATION.exiting(METHOD);
        }
    }

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
    public boolean login() throws LoginException {
        final String METHOD = "login()";
        Boolean b = null;

        try {
            // logging info
            LOCATION.entering(METHOD);

            // check wether authenticated user is available
            Object option = m_sharedState.get(AbstractLoginModule.NAME);

            if ((option != null) && !option.equals("")) {
                LOCATION.infoT("Authenticated user found: user=" + option + ".");
                m_options.put("user", option);

                refreshUserInfo((String) option);

                m_succeeded = true;
                b = new Boolean(true);

                return true;
            } else {
                LOCATION.infoT("No authenticated user found.");
                b = new Boolean(false);

                return false;
            }
        } finally {
            // logging info
            LOCATION.exiting(METHOD, b);
        }
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#commit()
     */
    public boolean commit() throws LoginException {
        final String METHOD = "commit()";
        String user;

        try {
            // logging info
            LOCATION.entering(METHOD);

            // try to find the user
            Object option = m_sharedState.get(AbstractLoginModule.NAME);

            // Object option= m_sharedState.get("javax.security.auth.login.name");
            if ((option != null) && option.equals(m_options.get("user"))) {
                user = (String) option;
                LOCATION.debugT("Authenticated user still in shared state.");
            } else {
                m_exception = new DetailedLoginException(
                        "Commit method of login module doing the actual " +
                        "authentication seems to have failed.",
                        LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE);
                return false;// throw m_exception;
            }

            this.uacc = UMFactory.getUserAccountFactory().getUserAccountByLogonId(user);

            // do nothing if it's locked
            if (this.uacc.isLocked()) {
                return true;
            }

            // it's not locked, get the mutable one
            if (!this.uacc.isMutable()) {
                this.uacc = UMFactory.getUserAccountFactory().getMutableUserAccount(this.uacc.getUniqueID());
            }

            if (this._callback_handler == null) {
                throw new LoginException("error: no CallbackHandler available");
            }

            // get the certificate from the request
            HttpGetterCallback hgc = new HttpGetterCallback();
            hgc.setName("no_cert_storing");
            hgc.setType(HttpGetterCallback.REQUEST_PARAMETER);

            try {
                _callback_handler.handle(new Callback[] { hgc });

                //check whether we should store at all...
                String[] parameters = (String[]) hgc.getValue();
                if ((parameters != null) && (parameters.length > 0)) {
                    for (int i = 0; i < parameters.length; i++) {
                        if ("on".equals(parameters[i]))
                            return true;
                    }
                }
            } catch (UnsupportedCallbackException uce) {
                throw new LoginException("Error: " + uce.getCallback().toString() + " not available to get cerificate from request");
            }

            hgc = new HttpGetterCallback();
            hgc.setName("javax.servlet.request.X509Certificate");
            hgc.setType(HttpGetterCallback.CERTIFICATE);

            try {
                _callback_handler.handle(new Callback[] { hgc });
            } catch (UnsupportedCallbackException uce) {
                throw new LoginException("Error: " + uce.getCallback().toString() +
                    " not available to get cerificate from request");
            }

            // get client certificate
            X509Certificate[] _certs = (X509Certificate[]) hgc.getValue();
            LOCATION.debugT("login", "found certificates {0}", new Object[] { _certs });

            if (_certs != null) {
                // save old certificates
                oldCerts = this.uacc.getCertificates();
                this.uacc.setCertificates(_certs);
                this.uacc.save();
                this.uacc.commit();
                this.accmodified = true;
            }

            this.success = true;
        } catch (Throwable e) {
            if (e instanceof Error) {
              LOCATION.traceThrowableT(Severity.ERROR, e.getLocalizedMessage(), e);
              throw (Error) e;
            } else if (e instanceof RuntimeException) {
              LOCATION.traceThrowableT(Severity.ERROR, e.getLocalizedMessage(), e);
              throw (RuntimeException) e;
            } else if (e instanceof LoginException) {
              if (LOCATION.beWarning()) {
                LOCATION.traceThrowableT(Severity.WARNING, e.getLocalizedMessage(), e);
              }
              throw (LoginException) e;
            } else {
              if (LOCATION.beWarning()) {
                LOCATION.traceThrowableT(Severity.WARNING, e.getLocalizedMessage(), e);
              }
              throw new DetailedLoginException(e.toString(),
                    LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE);
            }
        } finally {
            // logging info
            LOCATION.exiting(METHOD);
        }

        return true;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#abort()
     */
    public boolean abort() throws LoginException {
        // restore old status is we were successful but overall failure
        if (!this.success) {
            return true;
        }

        if (this.accmodified) {
            try {
                this.uacc.setCertificates(oldCerts);
                this.uacc.save();
                this.uacc.commit();
            } catch (Exception ex) {
                LOCATION.warningT("abort", "Can't restore old certificates.",
                    new Object[] { ex });
//                throwNewLoginException("No certificate provided.", LoginExceptionDetails.CERTIFICATE_IS_NOT_TRUSTED);
                return false;
            }
        }

        return true;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#logout()
     */
    public boolean logout() throws LoginException {
        // TODO Auto-generated method stub
        return false;
    }
}

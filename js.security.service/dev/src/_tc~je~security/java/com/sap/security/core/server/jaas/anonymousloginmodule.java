/*
 * Created on 23.05.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.sap.security.core.server.jaas;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.Principal;
import com.sap.security.api.ISecurityPolicy;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;


/**
 * LoginModule that logs in the anonymous user specified by the parameter j_user. If the logonID is not in the guest users list, the login fails.
 *
 * @author d040850 (Guenther Wannenmacher)
 * @deprecated Must not be used any longer.
 */
public class AnonymousLoginModule extends AbstractLoginModule {
  
  private static Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_LOCATION + ".AnonymousLoginModule");

    private CallbackHandler _callbackHandler;
    private Subject _subject;
    private Map _sharedState;
    private Map _options;
    private boolean _debug;
    private boolean _succeeded;
    private boolean _nameSet;
    private String _userId;

    public AnonymousLoginModule() {
        _callbackHandler = null;
        _subject = null;
        _sharedState = null;
        _options = null;
        _debug = false;
        _succeeded = false;
        _nameSet = false;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler,
        Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        _callbackHandler = callbackHandler;
        _subject = subject;
        _sharedState = sharedState;
        _options = options;
        _debug = "true".equalsIgnoreCase((String) _options.get("debug"));
    }

    public boolean login() throws LoginException {
        if (_debug) {
            LOCATION.errorT("login()");
        }

        NameCallback nameCallback = new NameCallback("User:");

        try {
            _callbackHandler.handle(new Callback[] { nameCallback });
        } catch (UnsupportedCallbackException e) {
            return false;
        } catch (IOException e) {
            throwUserLoginException(e, (byte) 18);
        }

        _userId = nameCallback.getName();

        if (_debug) {
            LOCATION.errorT("UserID: " + _userId);
        }

        if (_userId == null) {
            throwNewLoginException("UserId is null", (byte) 0);
        }

        if (!UMFactory.getAnonymousUserFactory().isAnonymousUser(_userId)) {
			if (_debug) {
				LOCATION.errorT("UserId is not in guest user list.");
			}
            throwNewLoginException("UserId is not in guest user list.", (byte) 0);
        }
        
        try{
        	IUserAccount anonUserAccount = UMFactory.getUserAccountFactory().getUserAccountByLogonId(_userId);
        	
			if (_debug) {
				LOCATION.errorT("Getting mutable user for unlocking.");
			}

        	if (!anonUserAccount.isMutable()) {
        		anonUserAccount = UMFactory.getUserAccountFactory().getMutableUserAccount(anonUserAccount.getUniqueID());
        	}
        	
			if (_debug) {
				LOCATION.errorT("Is password change required? " + anonUserAccount.isPasswordChangeRequired());
			}

        	if (anonUserAccount.isPasswordChangeRequired()) {
				if (_debug) {
					LOCATION.errorT("Setting newly generated password.");
				}
        		// change the password that it never expires.
        		ISecurityPolicy secpol = UMFactory.getSecurityPolicy();
        		secpol.setUserName(_userId);
        		// first set interims password. Password stay expired, but now we know it ;-)
        		String interimPassword = secpol.generatePassword();
        		anonUserAccount.setPassword(interimPassword);
        		// set password with new password. Password is not expired anymore.
        		anonUserAccount.setPassword(interimPassword, secpol.generatePassword());
        	}
        	
			if (_debug) {
				LOCATION.errorT("Is user locked? " + anonUserAccount.isLocked());
			}

        	if (anonUserAccount.isLocked()) {
        		// unlock the account if auto locked. stays locked if locked by admin.
        		if (anonUserAccount.getLockReason() != IUserAccount.LOCKED_BY_ADMIN) {
					if (_debug) {
						LOCATION.errorT("Unlocking user.");
					}
        			anonUserAccount.setLocked(false, IUserAccount.LOCKED_NO);
        		} else if (_debug) {
					LOCATION.errorT("User is locked by admin. No unlocking!");
				}

        	}
        	
        } catch (UMException umex) {
			LOCATION.errorT("Error while checking user information.");
			throwUserLoginException(umex);
        }

        try {
            refreshUserInfo(_userId);
        } catch (SecurityException e) {
            LOCATION.errorT("Error while refreshing user information.");
            throwUserLoginException(e);
        }

        if (_sharedState.get("javax.security.auth.login.name") == null) {
            _sharedState.put("javax.security.auth.login.name", _userId);
            _nameSet = true;
        }

        _succeeded = true;

        return _succeeded;
    }

    public boolean commit() throws LoginException {
        if (_debug) {
            LOCATION.errorT("commit()");
        }

        if (_succeeded) {
            Principal principal = new Principal(_userId);
            _subject.getPrincipals().add(principal);

            if (_nameSet) {
                _sharedState.put("javax.security.auth.login.principal", principal);
            }

            return true;
        } else {
            _userId = null;

            return false;
        }
    }

    public boolean abort() throws LoginException {
        if (_debug) {
            LOCATION.errorT("abort()");
        }

        _userId = null;
        _succeeded = false;

        return true;
    }

    public boolean logout() throws LoginException {
        if (_debug) {
            LOCATION.errorT("logout()");
        }
 
        return true;
    }
    
}

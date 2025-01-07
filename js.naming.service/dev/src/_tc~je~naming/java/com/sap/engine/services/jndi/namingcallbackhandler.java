/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.jndi;

/*
 * @author Svetlana Stancheva, Elitsa Pancheva
 * @version 6.30
 */

import com.sap.engine.lib.security.PasswordChangeCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSetterCallback;
import com.sap.tc.logging.Location;

import javax.security.auth.callback.*;
import javax.naming.Context;
import java.io.IOException;
import java.util.Hashtable;

public class NamingCallbackHandler implements CallbackHandler {
	
	private final static Location LOG_LOCATION = Location.getLocation(NamingCallbackHandler.class);

    public static final String SECURITY_CREDENTIAL = "sap.security.credential.";
    public static final String DIVIDER = ".";

    Hashtable properties = null;

    public NamingCallbackHandler(Hashtable properties) {
        this.properties = properties;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                ((NameCallback) callbacks[i]).setName((String) properties.get(Context.SECURITY_PRINCIPAL));
            } else if (callbacks[i] instanceof PasswordChangeCallback) {
                throw new UnsupportedCallbackException(callbacks[i], " Unsupported callback! ");
            } else if (callbacks[i] instanceof PasswordCallback) {
                String pass = (String) properties.get(Context.SECURITY_CREDENTIALS);
                ((PasswordCallback) callbacks[i]).setPassword(pass != null ? pass.toCharArray() : null);
            } else if (callbacks[i] instanceof HttpGetterCallback) {
                HttpGetterCallback getterCallback = (HttpGetterCallback) callbacks[i];
                // The key for naming properties for security credentials except the standart ones (username and password) is:
                // security-credential-<type_of_the_credential>-<name_of_the_credential>
                // Both the type and the name of the credential identify what kind of credential is requested.
                Object value = properties.get(SECURITY_CREDENTIAL + getterCallback.getType() + DIVIDER + getterCallback.getName());

                getterCallback.setValue(value);
            } else if (callbacks[i] instanceof HttpSetterCallback) {
                // Do nothing. Naming cannot set credentials.
            } else {
                if (LOG_LOCATION.beInfo()) {
                	LOG_LOCATION.infoT("Unsupported callback: " + callbacks[i] + ".");
                }
                throw new UnsupportedCallbackException(callbacks[i], " Unsupported callback! ");
            }
        }
    }

}
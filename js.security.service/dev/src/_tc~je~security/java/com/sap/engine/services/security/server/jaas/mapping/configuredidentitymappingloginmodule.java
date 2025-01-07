/**
 *  Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.server.jaas.mapping;

import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.interfaces.security.auth.ConfiguredIdentityConfiguration;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.Iterator;

public class ConfiguredIdentityMappingLoginModule extends AbstractLoginModule {
	private Map sharedState = null;
	private String name = null;
	private boolean nameSet = false;

	private UserStoreFactory usf = null;
	private String configIdentity = null;
	private String userStore = null;
	private ManagedConnectionFactory mcf = null;
	private final static Location LOCATION = Location.getLocation(ConfiguredIdentityMappingLoginModule.class);

	public ConfiguredIdentityMappingLoginModule() {
		this.usf = com.sap.engine.services.security.server.SecurityContextImpl.getRoot().getUserStoreContext();
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		this.sharedState = sharedState;
		configIdentity = (String) options.get(ConfiguredIdentityConfiguration.CONFIG_IDENTITY);
		userStore = (String) options.get(ConfiguredIdentityConfiguration.USER_STORE);

		if (userStore == null || configIdentity == null) {
			return;
		}

		try {
			UserInfo info = usf.getUserStore(userStore).getUserContext().getUserInfo(configIdentity);
			Subject newSubject = new Subject();
			usf.getUserStore(userStore).getUserContext().fillSubject(info, newSubject);

			if (newSubject != null) {
				name = ((Principal) newSubject.getPrincipals().iterator().next()).getName();
				subject.getPrincipals().clear();
				subject.getPrincipals().addAll(newSubject.getPrincipals());
				subject.getPrivateCredentials().clear();
				mcf = (ManagedConnectionFactory) options.get(ConfiguredIdentityConfiguration.MCF);

				if (mcf != null) {
					Iterator privateCredIter = newSubject.getPrivateCredentials().iterator();
					Object privateCred = null;

					while (privateCredIter.hasNext()) {
						privateCred = privateCredIter.next();

						if (privateCred instanceof PasswordCredential) {
							((PasswordCredential) privateCred).setManagedConnectionFactory(mcf);
						}

						subject.getPrivateCredentials().add(privateCred);
					}
				} else {
					subject.getPrivateCredentials().addAll(newSubject.getPrivateCredentials());
				}

				subject.getPublicCredentials().clear();
				subject.getPublicCredentials().addAll(newSubject.getPublicCredentials());
			}
		} catch (Exception e) {
		  LOCATION.traceThrowableT(Severity.INFO, "initialize", e);
		}
	}

	public boolean login() throws LoginException {
	if (sharedState.get(AbstractLoginModule.NAME) == null) {
			sharedState.put(AbstractLoginModule.NAME, name);
			nameSet = true;
		}
		return true;
	}

	public boolean commit() throws LoginException {
		if (nameSet) {
			sharedState.put(AbstractLoginModule.PRINCIPAL, new Principal(name));
		}
		return true;
	}

	public boolean abort() throws LoginException {
		return true;
	}

	public boolean logout() throws LoginException {
		return true;
	}

}


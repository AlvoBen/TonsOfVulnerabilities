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
import com.sap.engine.interfaces.security.userstore.context.GroupInfo;
import com.sap.engine.interfaces.security.auth.PrincipalMappingConfiguration;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.lib.util.HashMapObjectObject;
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
import java.security.Principal;

public class PrincipalMappingLoginModule extends AbstractLoginModule {
	private Map sharedState = null;
	private String name = null;
	private boolean nameSet = false;

	private HashMapObjectObject userMapping = null;
	private HashMapObjectObject groupMapping = null;
	private String userStore = null;
	private UserStoreFactory usf = null;
	private ManagedConnectionFactory mcf = null;
	private static final Location LOCATION = Location.getLocation(PrincipalMappingLoginModule.class);

	public PrincipalMappingLoginModule() {
		this.usf = com.sap.engine.services.security.server.SecurityContextImpl.getRoot().getUserStoreContext();
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		this.sharedState = sharedState;
		userStore = (String) options.get(PrincipalMappingConfiguration.USER_STORE);
		userMapping = (HashMapObjectObject) options.get(PrincipalMappingConfiguration.USER_MAPPING);

		if (userStore == null || userMapping == null) {
			return;
		}

		groupMapping = (HashMapObjectObject) options.get(PrincipalMappingConfiguration.GROUP_MAPPING);
		
		try {
			String userName = ((Principal) subject.getPrincipals().toArray()[0]).getName();
			Object info = usf.getActiveUserStore().getUserContext().getUserInfo(userName);
			Subject newSubject = loadSubject(info, true);

			if (newSubject != null) {
				name = ((Principal) newSubject.getPrincipals().iterator().next()).getName();
				subject.getPrincipals().clear();
				subject.getPrincipals().addAll(newSubject.getPrincipals());
				subject.getPrivateCredentials().clear();
				mcf = (ManagedConnectionFactory) options.get(PrincipalMappingConfiguration.MCF);

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
			sharedState.put(AbstractLoginModule.PRINCIPAL, new com.sap.engine.lib.security.Principal(name));
		}
		return true;
	}

	public boolean abort() throws LoginException {
		return true;
	}

	public boolean logout() throws LoginException {
		return true;
	}

	private Subject loadSubject(Object info, boolean isUser) {
		if (info == null) {
			return null;
		}

		String target = null;
		UserInfo targetInfo = null;
		Subject subject = new Subject();
		
		if (isUser) {
		  target = (String) userMapping.get(((UserInfo) info).getName());
		} else {
		  target = (String) groupMapping.get(((GroupInfo) info).getName());
		}
		
		if (target != null) {
			targetInfo = usf.getUserStore(userStore).getUserContext().getUserInfo(target);
			if (targetInfo != null) {
				try {
					usf.getUserStore(userStore).getUserContext().fillSubject(targetInfo, subject);
					return subject;
				} catch (Exception e) {
				  LOCATION.traceThrowableT(Severity.INFO, "Unable to obtain Subject with mapped credentials.", e);
				}
			}
		}

		try {
			java.util.Iterator parents = (isUser) ? ((UserInfo) info).getParentGroups() : ((GroupInfo) info).getParentGroups();

			while (parents.hasNext()) {
				subject = loadSubject(usf.getActiveUserStore().getGroupContext().getGroupInfo((String) parents.next()), false);

				if (subject != null) {
					return subject;
				}
			}
		} catch (Exception e) {
		  LOCATION.traceThrowableT(Severity.INFO, "Unable to obtain Subject with mapped credentials.", e);
		}
		return null;
	}

}
package com.sap.engine.services.dc.cm.utils;

import javax.security.auth.Subject;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.ValidatorUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;

public class SecurityUtil {
	
	private static Location location = DCLog.getLocation(SecurityUtil.class);

	private SecurityUtil() {
	}

	public static Subject getUserSubject(final String userUniqueId) {
		if (userUniqueId == null) {
			return null;
		}

		IUser user = null;
		try {
			if (userUniqueId != null) {
				user = UMFactory.getUserFactory().getUser(userUniqueId);
			}
		} catch (UMException e) {
			DCLog
					.logErrorThrowable(location, 
							"ASJ.dpl_dc.004308",
							"Cannot retrieve UME performer for post online deployment operation.",
							e);
			user = null;
		}

		final Subject userSubject = new Subject();

		final ServiceConfigurer serviceConfigurer = ServiceConfigurer
				.getInstance();
		ValidatorUtils.validateNull(serviceConfigurer, "ServiceConfigurer");

		final SecurityContext securityContext = serviceConfigurer
				.getSecurityContext();
		ValidatorUtils.validateNull(securityContext, "SecurityContext");

		final UserStoreFactory userStoreFactory = securityContext
				.getUserStoreContext();
		ValidatorUtils.validateNull(userStoreFactory, "UserStoreFactory");

		final UserStore userStore = userStoreFactory.getActiveUserStore();
		ValidatorUtils.validateNull(userStore, "UserStore");

		final UserContext userContext = userStore.getUserContext();

		userContext.fillSubject(userContext.getUserInfo(user.getUniqueName()),
				userSubject);

		return userSubject;

	}

}

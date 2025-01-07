package com.sap.engine.services.deploy.server;

import java.rmi.RemoteException;
import java.security.Permission;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.ServiceAccessPermission;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;

/**
 * Used to check whether the current user has needed authorizations to perform
 * desired deploy operations. This class is intended only for internal use by
 * deploy service.
 * 
 * @author Emil Dinchev
 */
public final class AuthorizationChecker {
	private static final Location location = 
		Location.getLocation(AuthorizationChecker.class);
	private static final Permission deployPermission = new ServiceAccessPermission(
			PropManager.getInstance().getServiceName(), "");

	/**
	 * Check whether the current user is authorized to perform deploy
	 * operations. A remote exception will be thrown, if the user has no
	 * permissions.
	 * 
	 * @param permissionType
	 *            string used only for logging purposes.
	 * @throws RemoteException
	 *             if the user has not permissions.
	 */
	public void checkAuthorization(String permissionType)
			throws RemoteException {
		final ApplicationServiceContext appSrvCtx = PropManager.getInstance()
				.getAppServiceCtx();
		final ThreadContext threadContext = appSrvCtx.getCoreContext()
				.getThreadSystem().getThreadContext();
		if (threadContext == null) {// system call
			return;
		}

		final SecurityContextObject securityContextObject = ((SecurityContextObject) threadContext
				.getContextObject(SecurityContextObject.NAME));
		final String principalName = securityContextObject.getSession()
				.getPrincipal().getName();

		try {
			final IUser user = UMFactory.getUserFactory().getUserByLogonID(
					principalName);

			if (!user.hasPermission(PropManager.getInstance().getServiceName(),
					deployPermission)) {
				throw new RemoteException("ASJ.dpl_ds.006199 The user "
						+ principalName
						+ " could not be authorized, because doesn't own the "
						+ deployPermission + " permission for "
						+ permissionType + ".");
			}
		} catch (UMException e) {
			throw new RemoteException("ASJ.dpl_ds.006200 The user "
					+ principalName
					+ " could not be authorized due to UME issues.", e);
		}
	}
}

package com.sap.engine.services.dc.cm.undeploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.isDebugLoggable;
import static com.sap.engine.services.dc.util.logging.DCLog.logDebugThrowable;

import com.sap.engine.services.dc.cm.security.authorize.AuthorizationException;
import com.sap.engine.services.dc.cm.security.authorize.Authorizer;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

public class AuthorizationVisitor implements UndeployItemVisitor{
	
	private Location location = DCLog.getLocation(this.getClass());
	
	SoftwareTypeService softwareTypeService;
	
	AuthorizationVisitor(SoftwareTypeService softwareTypeService) {
		this.softwareTypeService = softwareTypeService;
	}

	public void visit(UndeployItem undeployItem) {
		final SoftwareType softwareType = (undeployItem.getSda() != null ? undeployItem
				.getSda().getSoftwareType()
				: null);
		if (softwareTypeService.getOfflineSoftwareTypes()
				.contains(softwareType)) {
			Boolean isAuthorized4Offline = null;
			AuthorizationException authorized4OfflineException = null;

			if (isAuthorized4Offline == null) {
				try {
					isAuthorized4Offline = Boolean.FALSE;
					final Authorizer authorizer = AuthorizerFactory
							.getInstance().createAuthorizer();
					authorizer.isAuthorized4Offline();
					isAuthorized4Offline = Boolean.TRUE;
				} catch (AuthorizationException e) {
					authorized4OfflineException = e;
					if (isDebugLoggable()) {
						logDebugThrowable(location, null, authorized4OfflineException
								.getMessage(), authorized4OfflineException);
					}
				}
			}

			if (isAuthorized4Offline.equals(Boolean.FALSE)) {
				undeployItem
						.setUndeployItemStatus(UndeployItemStatus.PREREQUISITE_VIOLATED);
				undeployItem
						.setDescription("The specified software type '"
								+ undeployItem.getSda().getSoftwareType()
								+ "' is an offline one, but "
								+ " the user is not authorized for offline deployment. "
								+ authorized4OfflineException.getMessage());
			}
		}
	}

	public void visit(ScaUndeployItem undeployItem) {
		// do nothing
	}

}

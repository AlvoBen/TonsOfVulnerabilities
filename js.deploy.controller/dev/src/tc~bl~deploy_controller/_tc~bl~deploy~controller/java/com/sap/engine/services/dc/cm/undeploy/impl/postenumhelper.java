package com.sap.engine.services.dc.cm.undeploy.impl;

import  com.sap.engine.services.dc.util.logging.DCLog;

import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.OnlineOfflineSoftwareType;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-6-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class PostEnumHelper {
	
	private static Location location = DCLog.getLocation(PostEnumHelper.class);

	private static SoftwareTypeService softwareTypeService;

	private PostEnumHelper() {
	}

	static boolean isNext(GenericUndeployItem undeployItem,
			OnlineOfflineSoftwareType onOffSoftwareType)
			throws EnumRuntimeException {
		if (!isSoftwareTypeAdmitted(undeployItem, onOffSoftwareType)) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, 
						"The item [{0}] is not admitted for processing. Only items with software types corresponding to [{1}] are admitted.",
						new Object[] { undeployItem, onOffSoftwareType });
			}
			return false;
		}

		if (!isUndeploymentStatusAdmitted(undeployItem, onOffSoftwareType)) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, 
						"The item [{0}] is not admitted for processing in this [{1}] step, because of its deployment status [{2}].",
						new Object[] { undeployItem.getId(), onOffSoftwareType,
								undeployItem.getUndeployItemStatus() });
			}
			return false;
		}

		return true;
	}

	private static boolean isSoftwareTypeAdmitted(GenericUndeployItem item,
			OnlineOfflineSoftwareType onOffSoftwareType) {
		if (OnlineOfflineSoftwareType.OFFLINE.equals(onOffSoftwareType)) {
			OffSoftwareTypeVisitor visitor = new OffSoftwareTypeVisitor();
			item.accept(visitor);
			return visitor.isOfflineSoftwareType();
		} else {
			return true;
		}
	}

	private static boolean isUndeploymentStatusAdmitted(GenericUndeployItem item,
			OnlineOfflineSoftwareType onOffSoftwareType) {
		if (OnlineOfflineSoftwareType.OFFLINE.equals(onOffSoftwareType)) {
			if (UndeployItemStatus.OFFLINE_SUCCESS.equals(item
					.getUndeployItemStatus())
					|| UndeployItemStatus.OFFLINE_WARNING.equals(item
							.getUndeployItemStatus())
					|| UndeployItemStatus.OFFLINE_ABORTED.equals(item
							.getUndeployItemStatus())) {
				return true;
			} else {
				return false;
			}
		} else {
			if (UndeployItemStatus.ADMITTED
					.equals(item.getUndeployItemStatus())
					|| UndeployItemStatus.OFFLINE_ADMITTED.equals(item
							.getUndeployItemStatus())) {
				return true;
			} else {
				return false;
			}
		}
	}

	private static synchronized SoftwareTypeService getSoftwareTypeService() {
		if (softwareTypeService == null) {
			final Server server = ServerFactory.getInstance().createServer();
			softwareTypeService = (SoftwareTypeService) server
					.getServerService(ServerFactory.getInstance()
							.createSoftwareTypeRequest());
		}

		return softwareTypeService;
	}

	
	private static class OffSoftwareTypeVisitor implements UndeployItemVisitor {

		private boolean isOfflineSoftwareType = false;;
	
		private OffSoftwareTypeVisitor() {
		}
		
		
		private boolean isOfflineSoftwareType() {
			return isOfflineSoftwareType;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(UndeployItem undeployItem) {
			final SoftwareType swtType = undeployItem.getSda().getSoftwareType();
			isOfflineSoftwareType = getSoftwareTypeService().getOfflineSoftwareTypes().contains(swtType);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(ScaUndeployItem undeployItem) {
			isOfflineSoftwareType = false;
		}

	}
	
}

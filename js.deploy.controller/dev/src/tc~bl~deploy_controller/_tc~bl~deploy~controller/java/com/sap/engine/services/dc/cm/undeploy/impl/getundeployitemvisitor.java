package com.sap.engine.services.dc.cm.undeploy.impl;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentNotFoundException;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.VersionHelper;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.repo.Version;
import com.sap.tc.logging.Location;

public class GetUndeployItemVisitor implements UndeployItemVisitor{
	
	private Location location = DCLog.getLocation(this.getClass());
	
	GetUndeployItemVisitor() {
	}

	public void visit(UndeployItem undeployItem) {
		try{
			final SdaId sdaId = RepositoryComponentsFactory.getInstance()
			.createSdaId(undeployItem.getName(), undeployItem.getVendor());
			final Sda sda = (Sda) getUndeployItemSda(sdaId, undeployItem.getLocation(), undeployItem.getVersion());
			undeployItem.setSda(sda);
			undeployItem.setUndeployItemStatus(UndeployItemStatus.ADMITTED);
		} catch (UndeploymentNotFoundException unfe) {
			DCLog
					.logWarning(location, 
							"ASJ.dpl_dc.002551",
							"Undeploy item  [{0}] could not be undeployed because it is not deployed.",
							new Object[] { undeployItem });
			undeployItem.setUndeployItemStatus(UndeployItemStatus.NOT_DEPLOYED);
			undeployItem.setDescription(unfe.getLocalizedMessage());
		}
	}

	public void visit(ScaUndeployItem undeployItem) {
		try{
			final ScaId scaId = RepositoryComponentsFactory.getInstance()
			.createScaId(undeployItem.getName(), undeployItem.getVendor());
			final Sca sca = (Sca) getUndeployItemSda(scaId, undeployItem.getLocation(), undeployItem.getVersion());
			undeployItem.setSca(sca);
			undeployItem.setUndeployItemStatus(UndeployItemStatus.ADMITTED);
		} catch (UndeploymentNotFoundException unfe) {
			DCLog
					.logWarning(location, 
							"ASJ.dpl_dc.002551",
							"Undeploy item  [{0}] could not be undeployed because it is not deployed.",
							new Object[] { undeployItem });
			undeployItem.setUndeployItemStatus(UndeployItemStatus.NOT_DEPLOYED);
			undeployItem.setDescription(unfe.getLocalizedMessage());
		}
	}

	
	private Sdu getUndeployItemSda(SduId sduId, String location, Version version)
	throws UndeploymentNotFoundException {
		
		final Sdu sdu = (Sdu) RepositoryContainer.getDeploymentsContainer()
				.getDeployment(sduId);
		if (sdu == null) {
			UndeploymentNotFoundException unfe = new UndeploymentNotFoundException(
					"There is no component which corresponds to the specified undeployment item '"
							+ sduId + "'.");
			unfe.setMessageID("ASJ.dpl_dc.003222");
			throw unfe;
		} else {
			final VersionHelper versionHelper = RepositoryComponentsFactory
					.getInstance().createVersionHelper();
		
			if (location != null
					&& version != null) {
				if (location.equalsIgnoreCase(
						sdu.getLocation())
						&& versionHelper.isEquivalent(
								version, sdu.getVersion())) {
					return sdu;
				} else {
					String sduStr;
					if(sdu instanceof Sda){
						sduStr = "sda";
					}else if(sdu instanceof Sca){
						sduStr = "sca";
					}else{
						sduStr = "sdu";
					}
					UndeploymentNotFoundException unfe = new UndeploymentNotFoundException(
							"There is no component which corresponds to the specified undeployment item '"
									+ sduId
									+ "'. The found component which corresponds to the specified 'name' "
									+ "and 'vendor' has different version specific properties (" + sduStr + ": '"
									+ sdu + "').");
					unfe.setMessageID("ASJ.dpl_dc.003223");
					throw unfe;
				}
			}
		
			return sdu;
		}
	}
	
}

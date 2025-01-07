package com.sap.engine.services.dc.frame.impl;

import java.util.List;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageNotFoundException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

public class PostOnlineFailedVisitor implements DeploymentBatchItemVisitor {
	
	private Location location = DCLog.getLocation(this.getClass());
	private DeploymentData deploymentData;
	private List<DeploymentBatchItem> postOnlines;
	DeploymentDataStorageManager storageManager;

	public PostOnlineFailedVisitor(DeploymentData deploymentData,
			List<DeploymentBatchItem> postOnlines) {
		this.deploymentData = deploymentData;
		this.postOnlines = postOnlines;
		storageManager = DeploymentDataStorageFactory.getInstance()
				.createDeploymentDataStorageManager(
						ServiceConfigurer.getInstance()
								.getConfigurationHandlerFactory());
	}

	/**
	 * Abbort the post onlines when the registration of the callback fails
	 */
	public void visit(DeploymentItem item) {

		item.setDeploymentStatus(DeploymentStatus.ABORTED);
		item.addDescription("Post online item is aborted because "
				+ "the whole post online phase faled. "
				+ "See the error log for details.");

		persist(item);

	}

	private void persist(DeploymentBatchItem item) {
		try {
			storageManager.persist(deploymentData.getSessionId(), item);
		} catch (DeplDataStorageNotFoundException e) {
			DCLog.logErrorThrowable(location, e);
		} catch (DeplDataStorageException e) {
			DCLog.logErrorThrowable(location, e);
		}

	}

	/**
	 * Abort the SCAs that come after the post onlines no matter if some of
	 * their SDAs have been deployed
	 */
	public void visit(CompositeDeploymentItem item) {

		item.setDeploymentStatus(DeploymentStatus.ABORTED);
		item.addDescription("Post online item is aborted because "
				+ "the whole post online phase faled. "
				+ "See the error log for details.");

		persist(item);
		// TODO extract this logic in a separate class ( it is used on some
		// other places a well )
		// Set<DeploymentStatus> successfulStatuses = new
		// HashSet<DeploymentStatus>();
		// successfulStatuses.add(DeploymentStatus.OFFLINE_SUCCESS);
		// successfulStatuses.add(DeploymentStatus.OFFLINE_WARNING);
		// successfulStatuses.add(DeploymentStatus.SUCCESS);
		// successfulStatuses.add(DeploymentStatus.WARNING);
		//
		//		
		// boolean successfulFound = false;
		// Collection<DeploymentItem> sdas = item.getDeploymentItems();
		// if(sdas.size() == 0){ // empty scas are skipped alway successful
		// item.setDeploymentStatus(DeploymentStatus.SKIPPED);
		// item.addDescription(
		// "The item is skipped bacause registration of the deploy callback for post online failed"
		// );
		// return;
		// }
		// for(DeploymentItem sda : sdas ){
		//			
		// if(successfulStatuses.contains( sda.getDeploymentStatus() ) ){
		// successfulFound = true;
		// break;
		// }
		//			
		// }
		// if(successfulFound){
		// item.setDeploymentStatus(DeploymentStatus.SUCCESS);
		// }

	}

}
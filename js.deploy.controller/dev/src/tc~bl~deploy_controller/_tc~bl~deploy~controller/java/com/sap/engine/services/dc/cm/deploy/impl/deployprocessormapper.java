package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.tracePath;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeployProcessorMapper {
	private  final Location location = DCLog.getLocation(this.getClass());

	private static final DeployProcessorMapper INSTANCE = new DeployProcessorMapper();

	static DeployProcessorMapper getInstance() {
		return INSTANCE;
	}

	private DeployProcessorMapper() {
	}

	
	AbstractDeployProcessor map(DeploymentBatchItem item,
			DeployPhase deployPhase,
			LifeCycleDeployStrategy lifeCycleDeployStrategy) {
		
		if ( DeployPhase.UNKNOWN.equals(deployPhase)) { // SCA 
			
			if( ! (item instanceof CompositeDeploymentItem) ){
				throw new IllegalStateException("Only composite items can have status unknown");
			}
			CompositeDeploymentItem compositeItem = (CompositeDeploymentItem)item;
			
			Collection<DeploymentItem> dItems = compositeItem.getDeploymentItems();
			
			int admitted = 0;
			int offline_admitted = 0;
			for(DeploymentItem dItem : dItems){
				
				if(dItem.getDeploymentStatus().equals(DeploymentStatus.ADMITTED)){
					admitted ++;
				}

				if(dItem.getDeploymentStatus().equals(DeploymentStatus.OFFLINE_ADMITTED)){
					offline_admitted ++;
				}	
			}
			
			if(admitted == 0 && offline_admitted > 0){
				if (location.bePath()) {
					tracePath(location, 
							"DeployProcessorMapper.map() The item [{0}] will be processed as offlne" +
							" because it has [{1}] admitted and [{2}] offline admitted items.",
							new Object[] { item, admitted, offline_admitted });
				}
				// only offline items admitted for deployment. Process as offline
				return OfflineDeployProcessor.getInstance();
				
			} else {
				return map(lifeCycleDeployStrategy);
			}
			
			
		} else { // SDA
			if(DeployPhase.OFFLINE.equals(deployPhase)){
				return OfflineDeployProcessor.getInstance();
			} else {
				return map(lifeCycleDeployStrategy);	
			}
			
		}
	}
	
	AbstractDeployProcessor map(LifeCycleDeployStrategy lifeCycleDeployStrategy){
		
		if (LifeCycleDeployStrategy.BULK.equals(lifeCycleDeployStrategy)) {
			return BulkOnlineDeployProcessor.getInstance();
		} else if (LifeCycleDeployStrategy.SEQUENTIAL
				.equals(lifeCycleDeployStrategy)) {
			return SequentialOnlineDeployProcessor.getInstance();
		} else if (LifeCycleDeployStrategy.DISABLE_LCM
				.equals(lifeCycleDeployStrategy)) {
			return DeliverOnlineDeployProcessor.getInstance();
		} else {
			throw new IllegalStateException(
					"ASJ.dpl_dc.003057 The used life cycle strategy '"
							+ lifeCycleDeployStrategy
							+ "' is not supported.");
		}
	}

}

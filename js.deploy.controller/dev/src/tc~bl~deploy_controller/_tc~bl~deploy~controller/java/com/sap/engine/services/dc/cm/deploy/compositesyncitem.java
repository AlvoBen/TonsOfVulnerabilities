package com.sap.engine.services.dc.cm.deploy;

import java.util.Collection;

public interface CompositeSyncItem extends SyncItem {

	public Collection<DeploymentSyncItem> getDeploymentSyncItems();
}

package com.sap.engine.services.dc.cm.deploy;

import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.repo.SoftwareType;

public interface DeploymentSyncItem extends SyncItem {

	public RollingInfo getRollingInfo();

	public SoftwareType getSoftwareType();
}

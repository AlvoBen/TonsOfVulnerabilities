package com.sap.engine.services.dc.cm.deploy;

public interface SyncItemVisitor {

	public void visit(DeploymentSyncItem syncItem);

	public void visit(CompositeSyncItem syncItem);

}

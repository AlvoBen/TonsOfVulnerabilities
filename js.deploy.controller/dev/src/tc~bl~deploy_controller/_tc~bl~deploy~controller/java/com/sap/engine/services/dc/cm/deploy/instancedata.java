package com.sap.engine.services.dc.cm.deploy;

public interface InstanceData {

	public int getInstanceId();

	public boolean isProcessed();

	public void setProcessed(boolean isProcessed);

	public SyncRequest getSyncRequest();

}

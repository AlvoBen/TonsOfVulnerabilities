package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

import com.sap.engine.services.dc.cm.dscr.RollingInfo;

public interface SyncItem extends Serializable {

	public BatchItemId getBatchItemId();

	public void accept(SyncItemVisitor visitor);

}

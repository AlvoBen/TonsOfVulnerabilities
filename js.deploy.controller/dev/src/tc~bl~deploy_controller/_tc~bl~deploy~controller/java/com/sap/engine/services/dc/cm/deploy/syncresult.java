package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

public interface SyncResult extends Serializable {

	public SyncContext getSyncContext();

	public int getSenderId();

	public SyncException getSyncException();

}

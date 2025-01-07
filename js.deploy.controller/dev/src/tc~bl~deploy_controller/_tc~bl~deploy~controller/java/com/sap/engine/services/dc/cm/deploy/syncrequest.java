package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.Collection;

public interface SyncRequest extends Serializable {

	public SyncContext getSyncContext();

	public int getSenderId();

	public Collection<SyncItem> getSyncItems();

	public boolean isOffline();

}

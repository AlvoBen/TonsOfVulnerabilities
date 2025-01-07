package com.sap.engine.services.dc.cm.deploy;

public class SyncItemException extends SyncException {

	private static final long serialVersionUID = 6204985667582758869L;
	private SyncItem syncItem;

	public SyncItemException(String errMessage, SyncItem syncItem) {
		super(errMessage);
		this.syncItem = syncItem;
	}

	public SyncItemException(String errMessage, Throwable throwable,
			SyncItem syncItem) {
		super(errMessage, throwable);
		this.syncItem = syncItem;
	}

	public SyncItem getSyncItem() {
		return syncItem;
	}

}

package com.sap.engine.services.dc.cm.deploy;

import com.sap.engine.services.dc.cm.CMException;

public class SyncException extends CMException {

	private static final long serialVersionUID = 6298186778727652624L;

	public SyncException(String errMessage) {
		super(errMessage);
	}

	public SyncException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}

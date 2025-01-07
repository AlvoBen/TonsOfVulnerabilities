package com.sap.engine.services.dc.cm.deploy;

public class NotEnoughDiskSpaceException extends DeploymentException {

	private static final long serialVersionUID = 2957811072526831436L;

	public NotEnoughDiskSpaceException(final String errMessage) {
		super(errMessage);
	}

	public NotEnoughDiskSpaceException(final String errMessage,
			final Throwable throwable) {
		super(errMessage, throwable);
	}

}

package com.sap.sdm.is.cs.ncwrapper.impl;

import com.sap.bc.cts.tp.log.Logger;

final class SdmLoggerWrapper extends Logger {
	private final static com.sap.sdm.util.log.Logger wrappedSdmLogger = com.sap.sdm.util.log.Logger
			.getLogger();

	public void info(String message) {
		wrappedSdmLogger.info(message);
	}

	public void warning(String message) {
		wrappedSdmLogger.warning(message);
	}

	public void warning(String message, Throwable throwable) {
		wrappedSdmLogger.warning(message, throwable);
	}

	public void error(String message) {
		wrappedSdmLogger.error(message);
	}

	public void error(String message, Throwable throwable) {
		wrappedSdmLogger.error(message, throwable);
	}

	public void fatal(String message) {
		wrappedSdmLogger.fatal(message);
	}

	public void fatal(String message, Throwable throwable) {
		wrappedSdmLogger.fatal(message, throwable);
	}
}
package com.sap.sdm.is.cs.ncwrapper.impl;

import com.sap.bc.cts.tp.log.Trace;

final class SdmTraceWrapper extends Trace {
	private final com.sap.sdm.util.log.Trace wrappedSdmTrace;

	SdmTraceWrapper(com.sap.sdm.util.log.Trace wrappedSdmTrace) {
		this.wrappedSdmTrace = wrappedSdmTrace;
	}

	public void entering(String methodName) {
		wrappedSdmTrace.entering(methodName);
	}

	public void exiting() {
		wrappedSdmTrace.exiting();
	}

	public void exiting(String methodName) {
		wrappedSdmTrace.exiting(methodName);
	}

	public void debug(String debugInfo) {
		wrappedSdmTrace.debug(debugInfo);
	}

	public void debug(String debugInfo, Throwable throwable) {
		wrappedSdmTrace.debug(debugInfo, throwable);
	}
}
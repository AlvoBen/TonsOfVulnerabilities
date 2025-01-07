package com.sap.sdm.is.cs.ncwrapper.impl;

import com.sap.bc.cts.tp.log.Trace;
import com.sap.bc.cts.tp.log.TraceFactory;

final class SdmTraceFactoryWrapper implements TraceFactory {
	com.sap.sdm.util.log.TraceFactory wrappedSdmTraceFactory = com.sap.sdm.util.log.Trace
			.getTraceFactory();

	public boolean isTracingTurnedOn(Class forClass) {
		return wrappedSdmTraceFactory.isTracingTurnedOn(forClass);
	}

	public Trace getTrace(Class forClass) {
		return new SdmTraceWrapper(wrappedSdmTraceFactory.getTrace(forClass));
	}
}
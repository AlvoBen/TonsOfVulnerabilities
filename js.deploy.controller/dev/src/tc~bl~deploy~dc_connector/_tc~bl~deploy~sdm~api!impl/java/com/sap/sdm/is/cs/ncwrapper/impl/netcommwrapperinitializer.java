package com.sap.sdm.is.cs.ncwrapper.impl;

import com.sap.bc.cts.tp.log.Logger;
import com.sap.bc.cts.tp.log.Trace;
import com.sap.bc.cts.tp.log.TraceFactory;
import com.sap.sdm.is.cs.ncwrapper.NCWrapperFactory;

/**
 * @author Java Change Management May 17, 2004
 */
public final class NetCommWrapperInitializer {
	static {
		// Hopefully, setting the wrappers in a static initializer is early
		// enough such that the other tp.net classes will be loaded afterwards
		// (and thus can use the wrapped loggers instead of their default
		// loggers)

		Logger.setLogger(new SdmLoggerWrapper());
		Trace.setTraceFactory(new SdmTraceFactoryWrapper());
	}

	public static void init() {
		NCWrapperFactory.setFactory(NCWrapperFactoryImpl.INSTANCE);
	}

}

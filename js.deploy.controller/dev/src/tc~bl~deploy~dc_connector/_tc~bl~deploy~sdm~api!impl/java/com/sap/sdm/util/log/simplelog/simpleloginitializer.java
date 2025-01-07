package com.sap.sdm.util.log.simplelog;

import com.sap.sdm.util.log.Logger;
import com.sap.sdm.util.log.Trace;

/**
 * @author Christian Gabrisch 07.08.2003
 */
public final class SimpleLogInitializer {
	public static void init() {
		Trace.setTraceFactory(SimpleTraceFactory.getInstance());
		Logger.setGuiLogger(new SimpleLogger());
		Logger.setLogger(new SimpleLogger());
	}
}

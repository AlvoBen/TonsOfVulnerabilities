package com.sap.engine.services.dc.util;

import com.sap.jvm.Capabilities;

public final class JvmUtils {

	private static final boolean isSapJvm;

	static {
		isSapJvm = Capabilities.hasVmMonitoring();
	}

	public static boolean isSapJvm() {
		return isSapJvm;
	}

}

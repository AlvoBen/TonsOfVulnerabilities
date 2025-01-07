package com.sap.engine.lib.refgraph.impl.util;

import com.sap.jvm.util.StringPool;

public final class StringUtils {
	
	private StringUtils(){}
	
	public static String intern(final String string) {
		return StringPool.pool( string );
	}
}

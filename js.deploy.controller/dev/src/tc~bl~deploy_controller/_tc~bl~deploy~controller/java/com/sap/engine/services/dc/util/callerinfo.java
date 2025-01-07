package com.sap.engine.services.dc.util;

import java.lang.reflect.Method;

/**
 * Wraps <code>CallerInfoImportingSM</code> to get caller related data
 * <p>
 * NOTE: Package com.sap.engine.services.dc.util.sm.* must not be accessible in
 * offline phase and extramile
 * 
 * @author Anton Georgiev
 * @since 7.20
 */
public class CallerInfo {

	private static final String CI_IM_SM = "com.sap.engine.services.dc.util.sm.CallerInfoImportingSM";
	private static final String GET_HOST = "getHost";

	private static CallerInfo INSTANCE;

	private CallerInfo() {
	}

	private static synchronized CallerInfo getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CallerInfo();
		}
		return INSTANCE;
	}

	public static String getHost() {
		try {
			final Class callerInfoImportingSM = getInstance().getClass()
					.getClassLoader().loadClass(CI_IM_SM);
			final Method getHost = callerInfoImportingSM.getMethod(GET_HOST,
					null);
			Object result = getHost.invoke(callerInfoImportingSM, null);
			return (String) result;
		} catch (OutOfMemoryError e) {
			// OOM, ThreadDeath and Internal error are not consumed
			throw e;
		} catch (ThreadDeath e) {
			throw e;
		} catch (InternalError e) {
			throw e;
		} catch (Throwable th) {
			return Constants.UNKNOWN + "(" + th.getMessage() + ")";
		}
	}
}

package com.sap.engine.services.deploy.server;

/**
 * @author nikolai.g.nikolov@sap.com
 * @version Mar 20, 2003
 * @deprecated to be deleted
 */
public interface ApplicationLoadingInterceptor {

	byte[] intercept(String applicationName, String className, byte[] data,
			int offset, int length);
}

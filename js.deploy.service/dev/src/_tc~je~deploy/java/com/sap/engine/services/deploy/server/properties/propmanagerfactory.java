package com.sap.engine.services.deploy.server.properties;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.services.deploy.server.properties.impl.PropManagerImpl;

/**
 * This factory is used to initialize the proper PropManager instance, according
 * the needs of DeployService. It is used by <tt>js.deploy.service</tt> and
 * <tt>sl.tools</tt> projects.
 * 
 * @author Emil Dinchev
 */
public final class PropManagerFactory {

	private PropManagerFactory() {
		// to prevent the instantiation.
	}

	/**
	 * Initialize online instance of the property manager. It is supposed that
	 * this method is called once, during the service start.
	 * 
	 * @param asCtx
	 *            Application service context. Not null.
	 * @return property manager instance, configured for online mode.
	 */
	public static PropManager initInstance(
		final ApplicationServiceContext asCtx) throws ServiceException {
		PropManager instance = new PropManagerImpl(asCtx);
		PropManager.setInstance(instance);
		return instance;
	}

	/**
	 * Initialize offline instance of the property manager. It is supposed that
	 * this method is called once, during the setUp of the <tt>JUnit</tt> tests,
	 * and during the migration made by <tt>sl.tools</tt>.
	 * 
	 * @param appsWorkDir
	 *            applications work directory.
	 * @param ceId
	 *            cluster element ID.
	 * @param ceName
	 *            cluster element name.
	 * @return property manager instance configured for offline mode.
	 */
	public static PropManager initInstance(final String appsWorkDir,
			final int ceId, final String ceName) {
		PropManager instance = new PropManagerImpl(appsWorkDir, ceId, ceName);
		PropManager.setInstance(instance);
		return instance;
	}
}

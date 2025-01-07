package com.sap.engine.deployment;

import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.DeploymentManager;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.proxy.LoginInfo;
import com.sap.engine.deployment.exceptions.SAPDeploymentManagerCreationException;

/**
 * The DeploymentFactory interface is a deployment driver for a J2EE plaform
 * product. It returns a DeploymentManager object which represents a connection
 * to a specific J2EE platform product.
 * 
 * <p>
 * Each application server vendor must provide an implementation of this class
 * in order for the J2EE Deployment API to work with their product.
 * 
 * <p>
 * The class implementing this interface should have a public no-argument
 * constructor, and it should be stateless (two instances of the class should
 * always behave the same). It is suggested but not required that the class have
 * a static initializer that registers an instance of the class with the
 * DeploymentFactoryManager class.
 * 
 * <p>
 * A <tt>connected</tt> or <tt>disconnected</tt> DeploymentManager can be
 * requested. A DeploymentManager that runs connected to the platform can
 * provide access to J2EE resources. A DeploymentManager that runs disconnected
 * only provides module deployment configuration support.
 * 
 * @see javax.enterprise.deploy.shared.factories.DeploymentFactoryManager
 * 
 * @author Mariela Todorova
 */
public class SAPDeploymentFactory implements DeploymentFactory {
	private static final Location location = Location
			.getLocation(SAPDeploymentFactory.class);
	public static final String SEP = ":";
	public static final String URI = "AS_Java_Deployer";
	public static final String DEFAULT_HOST = "localhost";
	public static final String DEFAULT_PORT = "50004";

	public SAPDeploymentFactory() {
		PropertiesHolder.init();
		Logger.initLogging();
	}

	/**
	 * Tests whether this factory can create a DeploymentManager object based on
	 * the specificed URI. This does not indicate whether such an attempt will
	 * be successful, only whether the factory can handle the uri.
	 * 
	 * @param uri
	 *            The uri to check
	 * @return <tt>true</tt> if the factory can handle the uri.
	 */
	public boolean handlesURI(String uri) {
		Logger.trace(location, Severity.DEBUG, "Checking URI " + uri);

		if (uri == null || uri.equals("")) {
			return false;
		}

		if (uri.trim().endsWith(URI)) {
			return true;
		}

		return false;
	}

	/**
	 * Return a <tt>connected</tt> DeploymentManager instance.
	 * 
	 * @param uri
	 *            The URI that specifies the connection parameters
	 * @param username
	 *            An optional username (may be <tt>null</tt> if no
	 *            authentication is required for this platform).
	 * @param password
	 *            An optional password (may be <tt>null</yy> if
	 *        no authentication is required for this platform).
	 * @return A ready DeploymentManager instance.
	 * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException
	 *             occurs when a DeploymentManager could not be returned (server
	 *             down, unable to authenticate, etc).
	 */
	public DeploymentManager getDeploymentManager(String uri, String username,
			String password) throws SAPDeploymentManagerCreationException {
		if (!handlesURI(uri)) {
			return null;
		}

		String[] info = getHostAndPort(uri);
		Logger.trace(location, Severity.DEBUG, "Connecting to " + info[0] + ":"
				+ info[1]);
		LoginInfo login = new LoginInfo(info[0], info[1], username, password);
		return new SAPDeploymentManager(login);
	}

	/**
	 * Return a <tt>disconnected</tt> DeploymentManager instance.
	 * 
	 * @param uri
	 *            the uri of the DeploymentManager to return.
	 * @return A DeploymentManager <tt>disconnected</tt> instance.
	 * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException
	 *             occurs if the DeploymentManager could not be created.
	 */
	public DeploymentManager getDisconnectedDeploymentManager(String uri)
			throws SAPDeploymentManagerCreationException {
		if (!handlesURI(uri)) {
			return null;
		}

		Logger.trace(location, Severity.DEBUG,
				"Getting disconnected Deployment Manager");
		return new SAPDeploymentManager();
	}

	/**
	 * Provide a string with the name of this vendor's DeploymentManager.
	 * 
	 * @return the name of the vendor's DeploymentManager.
	 */
	public String getDisplayName() {
		return "AS Java Deployer";
	}

	/**
	 * Provide a string identifying version of this vendor's DeploymentManager.
	 * 
	 * @return the name of the vendor's DeploymentManager.
	 */
	public String getProductVersion() {
		return "7.1";
	}

	private String[] getHostAndPort(String uri) {
		String host = DEFAULT_HOST;
		String port = DEFAULT_PORT;

		if (uri.indexOf(SEP) > -1) {
			uri = uri.trim();
			String hostAndPort = uri.substring(0, uri.lastIndexOf(SEP));

			if (hostAndPort.indexOf(SEP) > -1) {
				host = hostAndPort.substring(0, hostAndPort.lastIndexOf(SEP));
				port = hostAndPort.substring(hostAndPort.lastIndexOf(SEP) + 1);
			} else {
				host = hostAndPort;
			}
		}

		return new String[] { host, port };
	}

}

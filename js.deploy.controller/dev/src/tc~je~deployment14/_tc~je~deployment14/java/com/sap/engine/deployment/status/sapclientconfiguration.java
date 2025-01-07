package com.sap.engine.deployment.status;

import javax.enterprise.deploy.spi.status.ClientConfiguration;
import com.sap.engine.deployment.exceptions.SAPClientExecuteException;

/**
 * The ClientConfiguration object installs, configures and executes an
 * Application Client. This class resolves the settings for installing and
 * running the application client.
 * 
 * @author Mariela Todorova
 */
public class SAPClientConfiguration implements ClientConfiguration {
	static final long serialVersionUID = 5271556957126078757L;

	/**
	 * This method performs an exec and starts the application client running in
	 * another process.
	 * 
	 * @throws ClientExecuteException
	 *             when the configuration is incomplete.
	 */
	public void execute() throws SAPClientExecuteException {
	}

}

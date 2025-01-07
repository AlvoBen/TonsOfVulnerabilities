/*
 * Created on Oct 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.library_container.deploy;

import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.library_container.LCResourceAccessor;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;

/**
 * Library Container subtype of <code>DeploymentException</code>.
 * 
 * @author I024067
 * 
 */
public class LCDeploymentException extends DeploymentException {

	static final long serialVersionUID = 821290503418917012L;

	/**
	 * Constructs new instance of this class
	 * 
	 * @param key
	 *            key for the message of the exception
	 */
	public LCDeploymentException(String key) {
		super(new LocalizableTextFormatter(LCResourceAccessor
				.getResourceAccessor(), key), LCResourceAccessor.getCategory(),
				Severity.ERROR, LCResourceAccessor.getLocation());
	}

	/**
	 * Constructs new instance of this class
	 * 
	 * @param key
	 *            key for the message of the exception
	 * @param t
	 *            cause exception
	 */
	public LCDeploymentException(String key, Throwable t) {
		super(new LocalizableTextFormatter(LCResourceAccessor
				.getResourceAccessor(), key), t, LCResourceAccessor
				.getCategory(), Severity.ERROR, LCResourceAccessor
				.getLocation());
	}

	/**
	 * Constructs new instance of this class
	 * 
	 * @param key
	 *            key for the message of the exception
	 * @param args
	 *            arguments for the message of the exception
	 */
	public LCDeploymentException(String key, Object[] args) {
		super(new LocalizableTextFormatter(LCResourceAccessor
				.getResourceAccessor(), key, args), LCResourceAccessor
				.getCategory(), Severity.ERROR, LCResourceAccessor
				.getLocation());
	}

	/**
	 * Constructs new instance of this class
	 * 
	 * @param key
	 *            key for the message of the exception
	 * @param args
	 *            arguments for the message of the exception
	 * @param t
	 *            cause exception
	 */
	public LCDeploymentException(String key, Object[] args, Throwable t) {
		super(new LocalizableTextFormatter(LCResourceAccessor
				.getResourceAccessor(), key, args), t, LCResourceAccessor
				.getCategory(), Severity.ERROR, LCResourceAccessor
				.getLocation());
	}

}

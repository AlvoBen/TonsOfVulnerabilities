package com.sap.sdm.util.log;

/**
 * Indicates an error during the configuration of logging and tracing.
 * 
 * @author Christian Gabrisch 07.01.2003
 */
public final class LogConfigurationException extends Exception {
	public LogConfigurationException(String message) {
		super(message);
	}
}

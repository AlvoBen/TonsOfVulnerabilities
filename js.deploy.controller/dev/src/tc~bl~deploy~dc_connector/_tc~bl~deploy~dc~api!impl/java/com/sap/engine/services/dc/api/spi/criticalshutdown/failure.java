package com.sap.engine.services.dc.api.spi.criticalshutdown;

import java.util.List;

/**
 * 
 * Instances of this interface describe startup failures of the java instance.
 * 
 * @see FailureAdapter
 * @author I040924
 * 
 */
public interface Failure {

	/**
	 * 
	 * 
	 * @return a list of processes that caused this failure
	 * @see Process
	 */
	public List getProcesses();

	/**
	 * 
	 * @return some additional overall description of the failure if applicable
	 */
	public String getDescription();

}
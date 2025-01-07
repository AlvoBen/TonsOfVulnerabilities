package com.sap.engine.interfaces.security.sanity;

/**
 * This interface needs to be implemented by tasks that can be monitored via the
 * Security Console
 */
public interface ISecuritySanityMBean {

	/**
	 *   
	 * @return
	 *   The name of the task 
	 * 
	 */

	public String getTaskName();

	/**
	 *   
	 * @return
	 *   The description of the task 
	 * 
	 */

	public String getTaskDescription();

	/**
	 *   
	 * @return
	 *   The description of the test performed 
	 * 
	 */

	public String getTestDescription();

	/**
	 * Initialize the components for the Security Check
	 * 
	 *   
	 * @return
	 *   ISecuritySanityTestResult containing the test result; returns dummy, if not yet tested;
	 * 	 
	 * 
	 */

	public ISecuritySanityTestResult getTestResult();

	/**
	 * Initialize the components for the Security Check
	 * 
	 *   
	 * @return
	 *   Composite object (ISecuritySanityTestResult) containing 
	 * 	 the test result
	 * 
	 */

	public ISecuritySanityTestResult test();

}
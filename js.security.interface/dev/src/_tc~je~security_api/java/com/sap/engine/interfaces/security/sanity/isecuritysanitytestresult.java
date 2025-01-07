package com.sap.engine.interfaces.security.sanity;

/**
 * This interface needs to be implemented in order to supply a test result
 */
public interface ISecuritySanityTestResult {

	/**
	 * @return
	 *   
	 *  get Follow on activity as a string
	 */

	public String getFollowOnActivity();

	/**
	 * @return
	 *   
	 *  short description of the test result
	 */

	public String getResultShortDescription();

	/**
	 * @return
	 *   
	 *  long description of the test result
	 */

	public String getResultLongDescription();

	/**
	 * @return
	 *   
	 *  return the sanity test Status
	 */

	public int getStatus();

}
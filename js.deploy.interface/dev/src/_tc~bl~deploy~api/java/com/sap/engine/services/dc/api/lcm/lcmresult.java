package com.sap.engine.services.dc.api.lcm;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The Result is returned by execution of the <code>LifeCycleManager</code>
 * commands.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-24</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface LCMResult {
	/**
	 * Returns current component status.
	 * 
	 * @return current component status
	 */
	public LCMResultStatus getLCMResultStatus();

	/**
	 * Returns short description in case of warning or error.
	 * 
	 * @return description
	 */
	public String getDescription();

	public String toString();
}
/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.selfcheck;

/**
 *<DL>
 *<DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Self Checker provides funtionality to check the DC reposiotry consitency.
 * The main idea is to check if all dependencies are satisfied and that all
 * sontained in the SCA SDAs are available.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Apr 4, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public interface SelfChecker {
	/**
	 * performs the check
	 * 
	 * @return <code>SelfCheckerResult</code>
	 * @throws SelfCheckerException
	 */
	public SelfCheckerResult doCheck() throws SelfCheckerException;

}

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
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Returned by
 * {@link com.sap.engine.services.dc.api.selfcheck.SelfChecker#doCheck()} and
 * shows the current DC repository status</DD>
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
public interface SelfCheckerResult {

	/**
	 * @return current DC repo status.
	 */
	public SelfCheckerStatus getStatus();

	/**
	 * 
	 * @return short description in case of error.
	 */
	public String getDescription();

}
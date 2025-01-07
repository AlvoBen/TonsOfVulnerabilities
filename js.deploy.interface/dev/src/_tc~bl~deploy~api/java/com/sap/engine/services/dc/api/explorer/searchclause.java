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
package com.sap.engine.services.dc.api.explorer;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Describes a single search clause for <code>SearchCriteria</code>.</DD>
 * <DT><B>Usage: </B></DT>
 * <DD>DeployItem deployItem = deployProcessor.createDeployItem( pathToSDA );</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-26</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.explorer.SearchCriteria
 */
public interface SearchClause {
	public static final String NAME_KEY = "name";
	public static final String VENDOR_KEY = "vendor";
	public static final String LOCATION_KEY = "location";
	public static final String VERSION_KEY = "version";

	public String getKey();

	public String getValue();

	public SearchClauseTarget getTarget();

}
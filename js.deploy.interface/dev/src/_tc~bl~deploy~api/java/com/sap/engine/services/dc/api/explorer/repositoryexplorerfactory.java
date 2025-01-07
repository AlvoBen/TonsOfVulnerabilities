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
 * <DD>Factory providing mechanism for creating <code>RepositoryExplorer</code>.
 * </DD>
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
 */
public abstract class RepositoryExplorerFactory {

	/**
	 * creates new <code>RepositoryExplorer</code>
	 * 
	 * @return new <code>RepositoryExplorer</code> instance.
	 * @throws RepositoryExplorerException
	 */
	public abstract RepositoryExplorer createRepositoryExplorer()
			throws RepositoryExplorerException;
	/*
	 * public SearchCriteria createSearchCriteria() throws
	 * RepositoryExploringException;
	 * 
	 * public SearchClause createSearchClause(String key, String value) throws
	 * RepositoryExploringException;
	 * 
	 * public SearchClause createSearchClause( String key,String value,
	 * SearchClauseTarget target) throws RepositoryExploringException;
	 */
}
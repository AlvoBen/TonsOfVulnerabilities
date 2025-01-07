package com.sap.engine.services.dc.api.filters;

import com.sap.engine.services.dc.api.model.SoftwareType;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team Description: Factory providing mechanism for
 * creating Batch filters.</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-1-25</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface BatchFilterFactory {
	/**
	 * Creates batch filter for distinct software type
	 * 
	 * @param softwareType
	 *            new created instance for given software type
	 * @return
	 */
	public BatchFilter createSoftwareTypeBatchFilter(SoftwareType softwareType);

}
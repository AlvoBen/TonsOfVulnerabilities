package com.sap.engine.services.dc.api.filters;

import com.sap.engine.services.dc.api.model.SoftwareType;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team
 * <DT><B>Description:</B></DT>
 * <DD>Batch filter for concrete software type.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2005-1-25</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface SoftwareTypeBatchFilter extends BatchFilter {

	public SoftwareType getSoftwareType();

}

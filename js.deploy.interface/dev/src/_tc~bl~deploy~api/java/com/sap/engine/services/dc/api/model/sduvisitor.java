package com.sap.engine.services.dc.api.model;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>visitor interface.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.model.Sda
 * @see com.sap.engine.services.dc.api.model.Sca
 */
public interface SduVisitor {

	public void visit(Sda sda);

	public void visit(Sca sca);

}

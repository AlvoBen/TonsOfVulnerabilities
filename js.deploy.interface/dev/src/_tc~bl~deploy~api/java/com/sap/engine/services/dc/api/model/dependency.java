package com.sap.engine.services.dc.api.model;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Describes a Sdu dependency.</DD>
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
 */
public interface Dependency {
	/**
	 * Returns the name of the sdu from which the current sdu depends.
	 * 
	 * @return name of the sdu
	 */
	public String getName();

	/**
	 * Return the vendor of the sdu from which the current sdu depends.
	 * 
	 * @return vendor of the sdu
	 */
	public String getVendor();

}

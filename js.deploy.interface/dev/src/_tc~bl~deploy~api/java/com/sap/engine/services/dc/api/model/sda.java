package com.sap.engine.services.dc.api.model;

import java.util.Set;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Represents a single Software deployment archive.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface Sda extends Sdu {

	/**
	 * Returns the <code>SoftwareType</code> of this SDA.
	 * 
	 * @return <code>SoftwareType</code>
	 */
	public SoftwareType getSoftwareType();

	/**
	 * Returns set with <code>Dependency</code> objects. If there are no
	 * dependencies a empty set is returned.
	 * 
	 * @return set with <code>Dependency</code> objects.
	 * @see com.sap.engine.services.dc.api.model.Dependency
	 */
	public Set getDependencies();

	/**
	 * Returns <code>ScaId</code> which contains the current Sda. If the SDA was
	 * deployed as a top level component then the method returns null.
	 * 
	 * @return <code>ScaId</code>
	 */
	public ScaId getScaId();

	/**
	 * Set with all components which has dependencies to this component.
	 * 
	 * @return set with all <code>Dependency</code> from
	 */
	public Set getDependingFrom();
}

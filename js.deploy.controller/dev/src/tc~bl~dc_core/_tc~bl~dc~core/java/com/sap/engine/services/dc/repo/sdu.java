package com.sap.engine.services.dc.repo;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface Sdu extends Serializable {

	public SduId getId();

	public String getName();

	public String getVendor();

	public String getLocation();

	public Version getVersion();

	public String getCrc();

	public void setCrc(String aCrc);

	public Map getProperties();

	/**
	 * Indicates whether this <code>Sdu</code> and the <code>otherSdu</code> can
	 * be compared with respect to the version they represent. Subtypes of
	 * <code>Sdu</code> define the actual criteria for SDUs to be comparable.
	 * 
	 * @param otherSdu
	 *            the other sdu.
	 * @return <code>true</code> if both instances of <code>Sdu</code> are
	 *         comparable; <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if <code>otherSdu</code> has <code>SduId</code> different
	 *             than this <code>Sdu</code>'s id.
	 * @throws NullPointerException
	 *             if <code>otherSdu</code> is <code>null</code>.
	 */
	public abstract boolean isComparable(Sdu otherSdu);

	/**
	 * Indicates whether <code>this</code> Sdu represents the same Sdu as the
	 * <code>otherSdu</code>. Subtypes of <code>Sdu</code> define the actual
	 * criteria for components to represent the same Sdu.
	 * 
	 * @param otherSdu
	 *            the other Sdu
	 * @return <code>true</code> if both instances of <code>Sdu</code> represent
	 *         the same Sdu; <code>false</code> otherwise
	 * @throws IllegalArgumentException
	 *             if this <code>Sdu</code> and <code>otherSdu</code> are not
	 *             comparable
	 * @throws NullPointerException
	 *             if <code>otherSdu</code> is <code>null</code>
	 */
	public abstract boolean isSameSdu(Sdu otherSdu);

	public void accept(SduVisitor visitor);

	public String getComponentElementXML();

	public String getCsnComponent();

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();
}

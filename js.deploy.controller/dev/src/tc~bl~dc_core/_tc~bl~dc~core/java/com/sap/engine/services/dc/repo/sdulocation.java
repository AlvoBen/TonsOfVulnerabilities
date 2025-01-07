package com.sap.engine.services.dc.repo;

/**
 * 
 * Title: J2EE Deployment Team Description: Describes the physical location of
 * an <code>Sdu</code>. The <code>SduLocation</code> helps to model the fact
 * that the physical location of an <code>Sdu</code> is not an attribute of the
 * <code>Sdu</code> itself. Instead, different locations of the same
 * <code>Sdu</code> may be possible which would be represented by different
 * <code>SduLocation</code>s which would all reference the same <code>Sdu</code>
 * .
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-8
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SduLocation {

	/**
	 * Returns the <code>Sdu</code> this <code>SduLocation</code> describes the
	 * location of.
	 */
	public Sdu getSdu();

	/**
	 * Returns the location of the <code>Sdu</code>.
	 */
	public String getLocation();

	/**
	 * Accepts a concrete <code>SduLocationVisitor</code> visitor.
	 * 
	 * @param visitor
	 *            specifies the concrete <code>SduLocationVisitor</code>
	 *            visitor.
	 */
	public void accept(SduLocationVisitor visitor);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}

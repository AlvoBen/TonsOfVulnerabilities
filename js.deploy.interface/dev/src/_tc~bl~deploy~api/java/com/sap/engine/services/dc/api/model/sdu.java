package com.sap.engine.services.dc.api.model;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Unit represents common data for both sdas and scas.</DD>
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
public interface Sdu {

	/**
	 * Returns the name of the sdu.
	 * 
	 * @return sdus name
	 */
	public String getName();

	/**
	 * Returns the vendor of the sdu.
	 * 
	 * @return sdu's vendor
	 */
	public String getVendor();

	/**
	 * Returns the location of the sdu.
	 * 
	 * @return sdus location
	 */
	public String getLocation();

	/**
	 * Returns sdu's version
	 * 
	 * @return sdus version
	 */
	public Version getVersion();

	/**
	 * 
	 * Returns <code>String</code> representing the component element data. The
	 * data is in XML format.
	 * 
	 * @return <code>String</code> representing the component element data
	 */
	public String getComponentElementXML();

	/**
	 * Retrieves ComponentId. Either ScaId or SdaId
	 * 
	 * @return sdu id
	 */
	public SduId getId();

	/**
	 * Accept external visitor.
	 * 
	 * @param visitor
	 *            <code>SduVisitor</code>
	 */
	public void accept(SduVisitor visitor);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

	/**
	 * CSN Component, the developer's unit name which is responsible for
	 * development of this deployment item.
	 * 
	 * @return CSN Component if available, otherwise empty string
	 *         <code>""</code>
	 */
	public String getCsnComponent();
}

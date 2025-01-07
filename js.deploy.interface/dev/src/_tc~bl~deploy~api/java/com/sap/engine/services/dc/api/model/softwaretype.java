package com.sap.engine.services.dc.api.model;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>This interface keeps all known component software types.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2005-1-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface SoftwareType {
	/** Subtype for <code>DBSC</code> and <code>JDDSCHEMA</code> software types. */
	public static final String SUB_TYPE_NAME_CORE = "core";	
	/** Subtype for <code>CONTENT</code> software types. */
	public static final String SUB_TYPE_NAME_SL_SDA = "SL-SDA";

	/**
	 * The software subtypes below are suitable for <code>single-module</code>
	 * software type.
	 */
	/** Software subtype for RAR module. */
	public static final String SUB_SINGLE_MODULE_RAR = "rar";
	/** Software subtype for PAR module. */
	public static final String SUB_SINGLE_MODULE_PAR = "par";
	/** Software subtype for EPA module. */
	public static final String SUB_SINGLE_MODULE_EPA = "epa";
	/** Software subtype for EJB module. */
	public static final String SUB_SINGLE_MODULE_EJB = "ejb";
	/** Software subtype for WAR module. */
	public static final String SUB_SINGLE_MODULE_WAR = "war";
	/** Software subtype for CAR module. */
	public static final String SUB_SINGLE_MODULE_CAR = "car";

	/** Base software types. */
	/** Software type for JAVA applications. */
	public static final String SOFTWARE_TYPE_JAVA = "JAVA";
	/** Software type for file system components. */
	public static final String SOFTWARE_TYPE_FS = "FS";
	/** Software type for DB content. */
	public static final String SOFTWARE_TYPE_DBSC = "DBSC";
	/** Software type for DB schema. */
	public static final String SOFTWARE_TYPE_JDDSCHEMA = "JDDSCHEMA";
	/** Software type for Bootstrap components. */
	public static final String SOFTWARE_TYPE_ENGINE_BOOT = "engine-bootstrap";
	/** Software type for Engine kernel components. */
	public static final String SOFTWARE_TYPE_ENGINE_KERN = "engine-kernel";
	/** Software type for J2EE engine templates. */
	public static final String SOFTWARE_TYPE_J2EE_TEMPLATE = "j2ee-template";
	/** Software type for primary services. */
	public static final String SOFTWARE_TYPE_ENGINE_SRV_OFFL = "primary-service";
	/** Software type for primary libraries. */
	public static final String SOFTWARE_TYPE_ENGINE_LIB_OFFL = "primary-library";
	/** Software type for primary interfaces. */
	public static final String SOFTWARE_TYPE_ENGINE_IF_OFFL = "primary-interface";
	/** Software type for online libraries. */
	public static final String SOFTWARE_TYPE_ENGINE_LIB_ONL = "library";
	/** Software type for JAVA libraries. */
	public static final String SOFTWARE_TYPE_ENGINE_LIB_620 = "JAVA-LIB";
	/** Software type for J2EE applications. */
	public static final String SOFTWARE_TYPE_ENGINE_APPL = "J2EE";
	/** Software type for single modules. */
	public static final String SOFTWARE_TYPE_ENGINE_MODULE = "single-module";

	/**
	 * Returns the name of this software type.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns the name of the subtype of this software type.
	 * 
	 * @return subtype name
	 */
	public String getSubTypeName();

	/**
	 * Returns description of this software type.
	 * 
	 * @return description
	 */
	public String getDescription();

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}

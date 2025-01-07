package com.sap.sdm.is.cs.remoteproxy.common;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class ArrayElem {

	private final String ifName;
	private final String instanceID;
	private final String value;
	private final boolean isJavaImmutable;
	private final boolean hasCacheableNoArgMethods;

	public ArrayElem(String ifName, String instanceID, String value,
			boolean hasCacheableNoArgMethods) {
		this.ifName = ifName;
		this.instanceID = instanceID;
		this.value = value;
		if ((this.ifName.equals(ClassNames.BOOLEANCLASSNAME))
				|| (this.ifName.equals(ClassNames.BYTECLASSNAME))
				|| (this.ifName.equals(ClassNames.DOUBLECLASSNAME))
				|| (this.ifName.equals(ClassNames.FLOATCLASSNAME))
				|| (this.ifName.equals(ClassNames.INTEGERCLASSNAME))
				|| (this.ifName.equals(ClassNames.LONGCLASSNAME))
				|| (this.ifName.equals(ClassNames.SHORTCLASSNAME))
				|| (this.ifName.equals(ClassNames.STRINGCLASSNAME))
				|| (this.ifName.equals(ClassNames.BOOLEANTYPENAME))
				|| (this.ifName.equals(ClassNames.BYTETYPENAME))
				|| (this.ifName.equals(ClassNames.DOUBLETYPENAME))
				|| (this.ifName.equals(ClassNames.FLOATTYPENAME))
				|| (this.ifName.equals(ClassNames.INTEGERTYPENAME))
				|| (this.ifName.equals(ClassNames.LONGTYPENAME))
				|| (this.ifName.equals(ClassNames.SHORTTYPENAME))
				|| (this.ifName.equals(ClassNames.VOIDTYPENAME))
				|| (this.instanceID == null)) {
			this.isJavaImmutable = true;
		} else {
			this.isJavaImmutable = false;
		}

		this.hasCacheableNoArgMethods = hasCacheableNoArgMethods;
	}

	public String getInterfaceName() {
		return this.ifName;
	}

	public String getInstanceID() {
		return this.instanceID;
	}

	public String getValue() {
		return this.value;
	}

	public boolean isJavaImmutable() {
		return this.isJavaImmutable;
	}

	public boolean hasCacheableNoArgMethods() {
		return this.hasCacheableNoArgMethods;
	}
}

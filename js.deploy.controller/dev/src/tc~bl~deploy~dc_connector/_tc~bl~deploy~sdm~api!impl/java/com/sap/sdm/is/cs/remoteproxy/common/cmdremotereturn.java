package com.sap.sdm.is.cs.remoteproxy.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdRemoteReturn implements CmdIF {
	public final static String NAME = "RemoteReturn";

	private final String ifName;
	private final String instanceID;
	private final String value;
	private final ArrayElem[] arrElemArr;
	private final boolean isJavaImmutable;
	private final boolean hasCacheableNoArgMethods;
	private final boolean containsArr;

	public CmdRemoteReturn(String ifName, String instanceID, String value,
			ArrayElem[] arrElemArr, boolean hasCacheableNoArgMethods) {
		this.ifName = ifName;
		this.instanceID = instanceID;
		this.value = value;
		this.arrElemArr = arrElemArr;
		if (this.arrElemArr != null) {
			this.containsArr = true;
		} else {
			this.containsArr = false;
		}

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

	public String getMyName() {
		return NAME;
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

	public ArrayElem[] getArrElementArray() {
		return this.arrElemArr;
	}

	public Object getValueAsObject() {
		if (this.ifName.equals(ClassNames.VOIDTYPENAME)) {
			return null;
		} else if (this.ifName.equals(ClassNames.BOOLEANCLASSNAME)
				|| this.ifName.equals(ClassNames.BOOLEANTYPENAME)) {
			return new Boolean(this.value);
		} else if (this.ifName.equals(ClassNames.BYTECLASSNAME)
				|| this.ifName.equals(ClassNames.BYTETYPENAME)) {
			return new Byte(this.value);
		} else if (this.ifName.equals(ClassNames.DOUBLECLASSNAME)
				|| this.ifName.equals(ClassNames.DOUBLETYPENAME)) {
			return new Double(this.value);
		} else if (this.ifName.equals(ClassNames.FLOATCLASSNAME)
				|| this.ifName.equals(ClassNames.FLOATTYPENAME)) {
			return new Float(this.value);
		} else if (this.ifName.equals(ClassNames.INTEGERCLASSNAME)
				|| this.ifName.equals(ClassNames.INTEGERTYPENAME)) {
			return new Integer(this.value);
		} else if (this.ifName.equals(ClassNames.LONGCLASSNAME)
				|| this.ifName.equals(ClassNames.LONGTYPENAME)) {
			return new Long(this.value);
		} else if (this.ifName.equals(ClassNames.SHORTCLASSNAME)
				|| this.ifName.equals(ClassNames.SHORTTYPENAME)) {
			return new Short(this.value);
		} else if (this.ifName.equals(ClassNames.STRINGCLASSNAME)) {
			return this.value;
		} else {
			return null;
		}
	}

	public boolean isJavaImmutable() {
		return this.isJavaImmutable;
	}

	public boolean hasCacheableNoArgMethods() {
		return hasCacheableNoArgMethods;
	}

	public boolean containsArray() {
		return this.containsArr;
	}

}

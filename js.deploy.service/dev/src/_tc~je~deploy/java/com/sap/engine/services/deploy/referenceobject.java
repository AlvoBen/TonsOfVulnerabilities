/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy;

import java.io.IOException;

import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.ReferenceType;
import com.sap.engine.services.deploy.container.util.PrintIt;
import com.sap.engine.services.deploy.ear.common.EqualUtils;
import com.sap.engine.services.deploy.server.utils.DSCloneUtils;
import com.sap.engine.services.deploy.server.utils.DSEqualUtils;
import com.sap.engine.services.deploy.server.utils.StringUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;

/* This class belongs to the public API of the DeployService project. */
/**
 * Class representing a reference from an application to a component and the
 * referenced (target) component itself. References are checked when starting 
 * the application. All the target components should be available (deployed and
 * started). ReferenceObject also holds information about the target component:
 * its name, provider name and type. Reference target type could be:
 * <code>application, service, library or interface</code>. It represents the
 * type of the component to which the reference is made. Default value is
 * <code>application</code>.
 * 
 * @author Radoslav Tsiklovski, Rumiana Angelova
 */
public class ReferenceObject implements ReferenceObjectIntf, PrintIt,
		Comparable {

	static final long serialVersionUID = -616263081989987029L;

	private String referenceTarget = null;
	private String referenceTargetType = null;
	private String referenceProviderName = null;
	private String referenceType = null;
	private ReferenceType characteristic = null;
	private int _hashcode;
	private String _toString = null;

	/**
	 * Empty Constructor of the class.
	 */
	public ReferenceObject() {
		characteristic = new ReferenceType();
	}

	/**
	 * Constructs new object from the given <code>ReferenceObjectIntf</code>
	 * 
	 * @param refIntf
	 *            reference object interface
	 */
	public ReferenceObject(ReferenceObjectIntf refIntf) {
		this();
		setReferenceProviderName(refIntf.getReferenceProviderName());
		setReferenceTarget(refIntf.getReferenceTarget());
		setReferenceTargetType(refIntf.getReferenceTargetType());
		setReferenceType(refIntf.getReferenceType());
		update();
	}

	/**
	 * Constructor specifying reference target name, reference target type and
	 * reference type. Reference target type shows the type of the referenced
	 * component: <code>application, service, library or interface</code>.
	 * Reference type shows the type of the reference: <code>weak or hard</code>
	 * .
	 * 
	 * @param referenceTarget
	 *            the name of the reference target.
	 * @param referenceTargetType
	 *            the type of the reference target.
	 * @param referenceType
	 *            the type of the reference.
	 */
	public ReferenceObject(String referenceTarget, String referenceTargetType,
			String referenceType) {
		this();
		this.setReferenceTargetType(referenceTargetType);
		this.setReferenceType(referenceType);
		this.setReferenceTarget(referenceTarget);
		update();
	}

	/**
	 * Constructor specifying composite name and reference type. Composite name
	 * is a compound name consisting of reference target type, followed by colon
	 * and reference target name. Reference target type could be:
	 * <code>application, service, library or interface.</code> If reference
	 * target type is missing it is set to <code>application</code>. Reference
	 * type is <code>weak or hard.</code>
	 * 
	 * @param compositeName
	 *            the composite name of the reference target containing its type
	 *            and name.
	 * @param referenceType
	 *            the type of the reference.
	 */
	public ReferenceObject(String compositeName, String referenceType) {
		this();
		this.setCompositeName(compositeName);
		this.setReferenceType(referenceType);
		update();
	}

	/**
	 * Sets composite name for the reference target component. It consists of
	 * reference target type, followed by colon and reference target name.
	 * Reference target type could be:
	 * <code>application, service, library or interface.</code> If reference
	 * target type is missing it is set to <code>application.</code>
	 * 
	 * @param compositeName
	 *            the composite name of the reference target containing its type
	 *            and name.
	 * 
	 * @throws IllegalArgumentException
	 *             thrown if incorrect value for composite name is set.
	 */
	public void setCompositeName(String compositeName)
			throws IllegalArgumentException {
		if (compositeName == null) {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006226 not specified composite name of the reference");
		}
		String tarType = null;
		String tarName = null;
		if (compositeName.startsWith(REF_TARGET_TYPE_LIBRARY + ":")) {
			tarType = REF_TARGET_TYPE_LIBRARY;
			if (compositeName.length() == (REF_TARGET_TYPE_LIBRARY + ":")
					.length()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_ds.006227 no library name is specified");
			}
			tarName = compositeName.substring((REF_TARGET_TYPE_LIBRARY + ":")
					.length());
		} else if (compositeName.startsWith(REF_TARGET_TYPE_SERVICE + ":")) {
			tarType = REF_TARGET_TYPE_SERVICE;
			if (compositeName.length() == (REF_TARGET_TYPE_SERVICE + ":")
					.length()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_ds.006228 no service name is specified");
			}
			tarName = compositeName.substring((REF_TARGET_TYPE_SERVICE + ":")
					.length());
		} else if (compositeName.startsWith(REF_TARGET_TYPE_INTERFACE + ":")) {
			tarType = REF_TARGET_TYPE_INTERFACE;
			if (compositeName.length() == (REF_TARGET_TYPE_INTERFACE + ":")
					.length()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_ds.006229 no interface name is specified");
			}
			tarName = compositeName.substring((REF_TARGET_TYPE_INTERFACE + ":")
					.length());
		} else if (compositeName.startsWith(REF_TARGET_TYPE_APPLICATION + ":")) {
			tarType = REF_TARGET_TYPE_APPLICATION;
			if (compositeName.length() == (REF_TARGET_TYPE_APPLICATION + ":")
					.length()) {
				throw new IllegalArgumentException(
						"ASJ.dpl_ds.006230 no application name is specified");
			}
			tarName = compositeName
					.substring((REF_TARGET_TYPE_APPLICATION + ":").length());
		} else {
			tarType = REF_TARGET_TYPE_APPLICATION;
			tarName = compositeName;
		}

		if (tarType.equals(REF_TARGET_TYPE_APPLICATION)) {
			int index = tarName.indexOf('/');
			if (index != -1) {
				setReferenceProviderName(tarName.substring(0, index));
				tarName = tarName.substring(index + 1);
			}
		}

		setReferenceTarget(tarName);
		setReferenceTargetType(tarType);
		update();
	}

	/**
	 * Returns the name of the referenced component.
	 * 
	 * @return the reference target name.
	 */
	public String getReferenceTarget() {
		return referenceTarget;
	}

	/**
	 * Sets the name of the referenced component. If reference target name
	 * contains forbidden characters, they are replaced by the tilde character.
	 * 
	 * @param referenceTarget
	 *            the reference target name to be set.
	 */
	public void setReferenceTarget(String referenceTarget) {
		if (referenceTarget != null) {
			referenceTarget = StringUtils.intern(referenceTarget.replace('/',
					'~'));
		}
		this.referenceTarget = referenceTarget;
		update();
	}

	/**
	 * Returns the type of the reference target. It could be:
	 * <code>application, service, library or interface.</code>
	 * 
	 * @return reference target type.
	 */
	public String getReferenceTargetType() {
		return this.referenceTargetType;
	}

	/**
	 * Sets the type of the reference target. Legal values are:
	 * <code>application, service, library and interface.</code> Default value
	 * is <code>application.</code>
	 * 
	 * @param _referenceTargetType
	 *            the reference target type to be set. Legal values are:
	 *            <code>application, service, library and interface.</code>
	 * 
	 * @throws IllegalArgumentException
	 *             thrown if incorrect value for reference target type is set.
	 */
	public void setReferenceTargetType(String _referenceTargetType)
			throws IllegalArgumentException {
		if ((ReferenceObject.REF_TARGET_TYPE_LIBRARY
				.equals(_referenceTargetType))
				|| (ReferenceObject.REF_TARGET_TYPE_INTERFACE
						.equals(_referenceTargetType))
				|| (ReferenceObject.REF_TARGET_TYPE_SERVICE
						.equals(_referenceTargetType))) {
			this.referenceTargetType = StringUtils.intern(_referenceTargetType);
			if (this.referenceTarget != null) {
				this.referenceTarget = this.referenceTarget.replace('/', '~');
			}
		} else if (ReferenceObject.REF_TARGET_TYPE_APPLICATION
				.equals(_referenceTargetType)) {
			this.referenceTargetType = StringUtils.intern(_referenceTargetType);
		} else {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006231 Unsupported reference_target type: "
							+ _referenceTargetType);
		}
		update();
	}

	/**
	 * Returns the type of the reference. It can be <code>weak or hard.</code>
	 * 
	 * @return reference type.
	 */
	public String getReferenceType() {
		return referenceType;
	}

	/**
	 * Sets the type of the reference. Legal values are
	 * <code>weak and hard.</code> Default value is <code>weak.</code>
	 * 
	 * @param _referenceType
	 *            the reference type to be set.
	 * 
	 * @throws IllegalArgumentException
	 *             thrown if incorrect value for reference type is set.
	 */
	public void setReferenceType(String _referenceType)
			throws IllegalArgumentException {
		if (ReferenceObject.REF_TYPE_HARD.equals(_referenceType)
				|| ReferenceObject.REF_TYPE_WEAK.equals(_referenceType)) {
			this.referenceType = StringUtils.intern(_referenceType);
		} else {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006232 Unsupported reference type: "
							+ _referenceType);
		}
		update();
	}

	/**
	 * Sets the name of the provider of the reference target component.
	 * 
	 * @param referenceProviderName
	 *            the provider name of the reference target.
	 */
	public void setReferenceProviderName(String referenceProviderName) {
		this.referenceProviderName = StringUtils.intern(referenceProviderName);
		update();
	}

	/**
	 * Gets the name of the provider of the reference target component.
	 * 
	 * @return provider name of the reference target.
	 */
	public String getReferenceProviderName() {
		return this.referenceProviderName;
	}

	/**
	 * @return the reference type
	 */
	public ReferenceType getCharacteristic() {
		return characteristic;
	}

	/**
	 * Sets the reference type
	 * 
	 * @param characteristic
	 *            the reference type
	 */
	public void setCharacteristic(ReferenceType characteristic) {
		ValidateUtils.nullValidator(characteristic, "ReferenceType");
		this.characteristic = characteristic;
		update();
	}

	/**
	 * Checks if this object and the parameter one have equal values of all the
	 * fields except these for reference type, which do not have to be equal.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return boolean <code>true</code> - if all the fields have equal values
	 *         except the reference type; <code>false</code> - otherwise.
	 */
	public boolean equalsSimple(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (hashCode() != obj.hashCode()) {
			return false;
		}

		if (!(obj instanceof ReferenceObject)) {
			return false;
		}

		ReferenceObject refObject = (ReferenceObject) obj;

		if (!DSEqualUtils.equalObjects(this.getReferenceTarget(), refObject
				.getReferenceTarget())) {
			return false;
		}

		if (!DSEqualUtils.equalObjects(this.getReferenceTargetType(), refObject
				.getReferenceTargetType())) {
			return false;
		}

		if (!DSEqualUtils.equalObjects(this.getReferenceProviderName(),
				refObject.getReferenceProviderName())) {
			return false;
		}

		if (!(DSEqualUtils.equalObjects(this.getCharacteristic(), refObject
				.getCharacteristic()))) {
			return false;
		}

		return true;
	}

	/**
	 * Checks if this object and the parameter one have equal values of all
	 * fields.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return boolean <code>true </code> - if all the fields have equal values,
	 *         <code>false </code> - otherwise.
	 */
	@Override
    public boolean equals(Object obj) {
		if (!equalsSimple(obj)) {
			return false;
		}

		if (!EqualUtils.equalObjects(this.getReferenceType(),
				((ReferenceObject) obj).getReferenceType())) {
			return false;
		}

		return true;
	}

	@Override
    public int hashCode() {
		return this._hashcode;
	}

	private void update() {
		int result;
		result = (referenceTarget != null ? referenceTarget.hashCode() : 0);
		result = 29
				* result
				+ (referenceTargetType != null ? referenceTargetType.hashCode()
						: 0);
		result = 29
				* result
				+ (referenceProviderName != null ? referenceProviderName
						.hashCode() : 0);
		result = 29 * result
				+ (referenceType != null ? referenceType.hashCode() : 0);
		result = 29 * result + getCharacteristic().hashCode();// cannot be
		// null

		this._hashcode = result;
		_toString = null;
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return a clone of this instance.
	 */
	@Override
    public Object clone() {
		ReferenceObject refClone = new ReferenceObject();

		refClone.setReferenceTarget(DSCloneUtils.clone(this
				.getReferenceTarget()));
		refClone.setReferenceTargetType(DSCloneUtils.clone(this
				.getReferenceTargetType()));
		refClone.setReferenceType(DSCloneUtils.clone(this.getReferenceType()));
		refClone.setReferenceProviderName(DSCloneUtils.clone(this
				.getReferenceProviderName()));
		refClone
				.setCharacteristic((ReferenceType) (getCharacteristic().clone()));// cannot
		// be
		// null

		return refClone;
	}

	/**
	 * Provides a String representation of this object.
	 * 
	 * @return a String representing the reference target.
	 */
	@Override
    public String toString() {
		if (_toString == null) {
			String target = getName();
			if (!REF_TARGET_TYPE_APPLICATION.equals(getReferenceTargetType())) {
				target = getReferenceTargetType() + ":" + target;
			}
			this._toString = StringUtils.intern(target);
		}
		return _toString;
	}

	/**
	 * Returns the name of the referenced component, including its provider
	 * name.
	 * 
	 * @return the name of the referenced component.
	 */
	public String getName() {
		String target = getReferenceTarget();
		if (REF_TARGET_TYPE_APPLICATION.equals(getReferenceTargetType())) {
			if (getReferenceProviderName() == null
					|| getReferenceProviderName().equals("")) {
				target = "sap.com/" + target;
			} else {
				target = getReferenceProviderName() + "/" + target;
			}
		} else {
			if (!(getReferenceProviderName() == null)
					&& !getReferenceProviderName().equals("")
					&& !getReferenceProviderName().equals("engine.sap.com")
					&& !getReferenceProviderName().equals("sap.com")) {
				target = getReferenceProviderName() + "~" + target;
			}
		}
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.dpl_info.module.PrintIt#print(java
	 * .lang.String)
	 */
	public String print(String shift) {
		StringBuilder builder = new StringBuilder(256);
		builder.append(shift);
		builder.append(getReferenceType());
		builder.append(" to ");
		builder.append(toString());
		builder.append(" (f=");
		builder.append(getCharacteristic().isFunctional());
		builder.append(", cl=");
		builder.append(getCharacteristic().isClassloading());
		builder.append(", p=");
		builder.append(getCharacteristic().isPersistent());
		builder.append(")");
		return builder.toString();
	}

	public int compareTo(Object o) {
		if (o instanceof ReferenceObjectIntf) {
			final ReferenceObjectIntf obj = (ReferenceObjectIntf) o;

			int i = compareTo(getReferenceTargetType(), obj
					.getReferenceTargetType());
			if (i != 0) {
				return i;
			}
			i = compareTo(getReferenceProviderName(), obj
					.getReferenceProviderName());
			if (i != 0) {
				return i;
			}
			i = compareTo(getReferenceTarget(), obj.getReferenceTarget());
			if (i != 0) {
				return i;
			}
			i = compareTo(getReferenceType(), obj.getReferenceType());
			return i;
		} else {
			return 0;
		}
	}

	private int compareTo(String s, String t) {
		if (s == null) {
			if (t == null) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (t == null) {
				return -1;
			} else {
				return s.compareTo(t);
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.referenceTarget = StringUtils.intern(this.referenceTarget);
		this.referenceTargetType = StringUtils.intern(this.referenceTargetType);
		this.referenceProviderName = StringUtils
				.intern(this.referenceProviderName);
		this.referenceType = StringUtils.intern(this.referenceType);
		this._toString = StringUtils.intern(this._toString);
	}

}

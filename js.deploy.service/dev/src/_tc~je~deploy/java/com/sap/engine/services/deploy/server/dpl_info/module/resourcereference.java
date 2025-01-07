/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.dpl_info.module;

import java.io.IOException;
import java.io.Serializable;

import com.sap.engine.services.deploy.container.ResourceReferenceType;
import com.sap.engine.services.deploy.container.util.PrintIt;
import com.sap.engine.services.deploy.ear.common.CloneUtils;
import com.sap.engine.services.deploy.ear.common.EqualUtils;
import com.sap.engine.services.deploy.server.utils.DSHashCodeUtils;
import com.sap.engine.services.deploy.server.utils.StringUtils;

/**
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
// TODO: 1) To be made immutable
// 2) To move Resource(name, type) to Container API
// 3) To be made as compound object, containing the Resource and
// ReferenceCharacteristic
// 4) To be moved to Container API
public class ResourceReference implements Serializable, PrintIt {
	static final long serialVersionUID = 1L;
	private String resRefName;
	private String resRefType;
	private String referenceType;
	private ResourceReferenceType type;

	public ResourceReference() {
		this(null, null, null);
	}

	public ResourceReference(String resRefName, String resRefType,
		String referenceType) {
		this(resRefName, resRefType, referenceType, true, true);
	}

	public ResourceReference(String resRefName, String resRefType,
		String referenceType, boolean isFunctional, boolean isClassloading) {
		this(resRefName, resRefType, referenceType, new ResourceReferenceType(
				isFunctional, isClassloading));
	}

	public ResourceReference(String resRefName, String resRefType,
		String referenceType, ResourceReferenceType type) {
		this.resRefName = StringUtils.intern(resRefName);
		this.resRefType = StringUtils.intern(resRefType);
		this.referenceType = StringUtils.intern(referenceType);
		setType(type);
	}

	/**
	 * @return
	 */
	public String getReferenceType() {
		return referenceType;
	}

	/**
	 * @return
	 */
	public String getResRefName() {
		return resRefName;
	}

	/**
	 * @return
	 */
	public String getResRefType() {
		return resRefType;
	}

	/**
	 * The reference type, which might be
	 * 
	 * @param string
	 */
	public void setReferenceType(String string) {
		referenceType = StringUtils.intern(string);
	}

	/**
	 * @param string
	 */
	public void setResRefName(String string) {
		resRefName = StringUtils.intern(string);
	}

	/**
	 * @param string
	 */
	public void setResRefType(String string) {
		resRefType = StringUtils.intern(string);
	}

	@Override
	public int hashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + DSHashCodeUtils.hashCode(getResRefName());
		result = result * multiplier
				+ DSHashCodeUtils.hashCode(getResRefType());
		result = result * multiplier
				+ DSHashCodeUtils.hashCode(getReferenceType());
		result = result * multiplier + DSHashCodeUtils.hashCode(getType());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ResourceReference)) {
			return false;
		}

		final ResourceReference other = (ResourceReference) obj;

		if (!EqualUtils.equalObjects(getResRefName(), other.getResRefName())) {
			return false;
		}

		if (!EqualUtils.equalObjects(getResRefType(), other.getResRefType())) {
			return false;
		}

		if (!EqualUtils.equalObjects(getReferenceType(), other
				.getReferenceType())) {
			return false;
		}

		if (!EqualUtils.equalObjects(getType(), other.getType())) {
			return false;
		}

		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new ResourceReference(CloneUtils.clone(getResRefName()), 
			CloneUtils.clone(getResRefType()),
			CloneUtils.clone(getReferenceType()),
			getType() == null ? getType() :
				(ResourceReferenceType) getType().clone());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.dpl_info.module.PrintIt#print(java
	 * .lang.String)
	 */
	public String print(String shift) {
		return shift + getReferenceType() + " to " + getResRefType() + ":"
				+ getResRefName() + " (f=" + getType().isFunctional() + ", cl="
				+ getType().isClassloading() + ")";
	}

	public ResourceReferenceType getType() {
		return type;
	}

	public void setType(ResourceReferenceType type) {
		this.type = (type == null ? new ResourceReferenceType() : type);
	}

	@Override
	public String toString() {
		return print("");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
		ClassNotFoundException {
		in.defaultReadObject();
		this.referenceType = StringUtils.intern(this.referenceType);
		this.resRefName = StringUtils.intern(this.resRefName);
		this.resRefType = StringUtils.intern(this.resRefType);

	}
}
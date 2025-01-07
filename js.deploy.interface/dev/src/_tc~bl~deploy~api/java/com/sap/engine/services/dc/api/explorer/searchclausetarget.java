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
package com.sap.engine.services.dc.api.explorer;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Describes search clause target for the repository explorer.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-26</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class SearchClauseTarget {

	public static final SearchClauseTarget SDU = new SearchClauseTarget(
			new Integer(0), "Sdu");

	public static final SearchClauseTarget SCA = new SearchClauseTarget(
			new Integer(1), "Sca");

	public static final SearchClauseTarget SDA = new SearchClauseTarget(
			new Integer(2), "Sda");

	private final Integer id;
	private final String name;

	private SearchClauseTarget(Integer id, String name) {
		checkArg(id, "id");
		checkArg(name, "name");
		this.id = id;
		this.name = name;
	}

	private void checkArg(Object arg, String argName) {
		if (arg == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DCAPI.1000] The argument '" + argName
							+ "' is null.");
		}
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SearchClauseTarget)) {
			return false;
		}

		SearchClauseTarget other = (SearchClauseTarget) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
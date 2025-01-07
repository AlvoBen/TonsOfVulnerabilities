/*
 * Created on 2005-7-4 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.ErrorHandlingRule;
import com.sap.sdm.api.remote.ErrorHandlingRules;

/**
 * @author radoslav-i
 */
class P4ErrorHandlingRuleImpl implements ErrorHandlingRule {

	public static final ErrorHandlingRule ON_ERROR_IGNORE = new P4ErrorHandlingRuleImpl(
			ErrorHandlingRules.ON_ERROR_IGNORE,
			ErrorHandlingRules.ON_ERROR_IGNORE_S,
			ErrorHandlingRules.ON_ERROR_IGNORE_DESC);

	public static final ErrorHandlingRule ON_ERROR_SKIP_DEPENDING = new P4ErrorHandlingRuleImpl(
			ErrorHandlingRules.ON_ERROR_SKIP_DEPENDING,
			ErrorHandlingRules.ON_ERROR_SKIP_DEPENDING_S,
			ErrorHandlingRules.ON_ERROR_SKIP_DEPENDING_DESC);

	public static final ErrorHandlingRule ON_ERROR_STOP = new P4ErrorHandlingRuleImpl(
			ErrorHandlingRules.ON_ERROR_STOP,
			ErrorHandlingRules.ON_ERROR_STOP_S,
			ErrorHandlingRules.ON_ERROR_STOP_DESC);

	private final int ruleAsInt;
	private final String ruleAsString;
	private final String ruleDescription;

	private P4ErrorHandlingRuleImpl(int ruleAsInt, String ruleAsString,
			String ruleDescription) {
		this.ruleAsInt = ruleAsInt;
		this.ruleAsString = ruleAsString;
		this.ruleDescription = ruleDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ErrorHandlingRule#getRuleAsInt()
	 */
	public int getRuleAsInt() {
		return ruleAsInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ErrorHandlingRule#getRuleAsString()
	 */
	public String getRuleAsString() {
		return ruleAsString;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof P4ErrorHandlingRuleImpl)) {
			return false;
		}

		P4ErrorHandlingRuleImpl other = (P4ErrorHandlingRuleImpl) obj;

		if (this.ruleAsInt != other.ruleAsInt) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return ruleAsInt;
	}

	static ErrorHandlingRule getErrorHandlingRuleByInt(int ruleAsInt) {
		if (ruleAsInt == ErrorHandlingRules.ON_ERROR_STOP) {
			return ON_ERROR_STOP;
		} else if (ruleAsInt == ErrorHandlingRules.ON_ERROR_SKIP_DEPENDING) {
			return ON_ERROR_SKIP_DEPENDING;
		} else if (ruleAsInt == ErrorHandlingRules.ON_ERROR_IGNORE) {
			return ON_ERROR_IGNORE;
		} else {
			throw new IllegalArgumentException("Unknown ErrorHandlingRule - "
					+ "int representation: " + ruleAsInt);
		}
	}
}

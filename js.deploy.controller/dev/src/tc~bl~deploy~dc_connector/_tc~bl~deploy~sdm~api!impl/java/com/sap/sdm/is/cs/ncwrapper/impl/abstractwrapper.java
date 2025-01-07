package com.sap.sdm.is.cs.ncwrapper.impl;

/**
 * @author Java Change Management May 18, 2004
 */
abstract class AbstractWrapper {
	private final Object wrapped;

	protected AbstractWrapper(Object wrapped) {
		this.wrapped = wrapped;
	}

	public final boolean equals(Object other) {
		return wrapped.equals(other);
	}

	public final int hashCode() {
		return wrapped.hashCode();
	}

	public final String toString() {
		return wrapped.toString();
	}
}

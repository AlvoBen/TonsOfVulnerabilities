/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph;

/**
 * Class representing the edges in the graph. Every edge has two nodes of type
 * N, a type and a nested object. Edges are immutable.
 * 
 * @author Luchesar Cekov
 */
public final class Edge<N> {
	public enum Type {
		WEAK, HARD
	}

	private final N first;
	private final N second;
	private final Object userObject;
	private final Edge.Type type;

	/**
	 * @param first
	 *            not null.
	 * @param second
	 *            not null.
	 * @param type
	 *            not null.
	 * @param nestedObject
	 *            can be null.
	 */
	public Edge(final N first, final N second, final Edge.Type type,
		final Object userObject) {
		if (first == null || second == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.first = first;
		this.second = second;
		this.type = type;
		this.userObject = userObject;
	}

	public N getFirst() {
		return first;
	}

	public N getSecond() {
		return second;
	}

	public Object getNestedObject() {
		return userObject;
	}

	public Edge.Type getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o == null || !getClass().equals(o.getClass())) {
			return false;
		}
		if (this == o) {
			return true;
		}
		final Edge<N> other = (Edge<N>) o;

		return first.equals(other.first) && second.equals(other.second)
				&& type.equals(other.type)
				&& isNestedObjectEquals(other.userObject);
	}

	private boolean isNestedObjectEquals(Object aNestedObject) {
		return (userObject != null && userObject.equals(aNestedObject))
				|| (userObject == null && aNestedObject == null);
	}

	@Override
	public int hashCode() {
		int result = first.hashCode();
		result = 29 * result + second.hashCode();
		result = 29 * result + type.hashCode();
		result = 29 * result + (userObject != null ? userObject.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {		
		return "first: [" + getFirst() + "], second: [" + getSecond()
			+ "], type: [" + getType() + "], userObject: ["
			+ getNestedObject() + "]";
	}
}

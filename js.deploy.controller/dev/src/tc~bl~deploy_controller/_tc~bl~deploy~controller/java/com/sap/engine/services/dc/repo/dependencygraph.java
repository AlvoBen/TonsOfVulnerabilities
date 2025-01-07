package com.sap.engine.services.dc.repo;

import java.util.Collection;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public interface DependencyGraph {

	/**
	 * Returns the items contained in this dependency graph.
	 */
	public Collection getItems();

	/**
	 * Returns the items that have no dependencies on other items in this
	 * dependency graph. An item returned by this method may still define
	 * dependencies. These are, however, not resolvable by any other item in
	 * this dependency graph.
	 */
	public Collection getIndependentItems();

	/**
	 * Returns the items that have no dependent on them items.
	 */
	public Collection getSourceItems();

	/**
	 * Indicates whether this dependency graph contains cycles.
	 */
	public boolean containsCycles();

	/**
	 * Returns the dependency cycles of this dependency graph. If there are no
	 * dependency cycles, the method returns an empty array.
	 */
	public DependencyCycle[] getCycles();

	/**
	 * Checks weather the graph is empty.
	 * 
	 * @return true if graph contains no elepements, otherwise - false.
	 */
	public boolean isEmpty();
}

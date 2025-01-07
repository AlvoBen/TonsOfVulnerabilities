/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.injection;

import java.util.*;

/**
 * This matrix is introduced in the injection library in order to provide 
 * an easy way to access the injectors and to relieve the library user of 
 * his responsibilities to choose a data structure to keep the injectors
 * in (array, Set, etc).
 * 
 * Sometimes it is needed to sort the injectors. That is why this matrix
 * actually represents an ordered collection (i.e. a list) of injectors.
 * Add operations are available only at the beginning or at the end of the
 * list but this must satisfy all the needs of the interested components.
 *
 * @author  Vesselin Mitrov, vesselin.mitrov@sap.com
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public class InjectionMatrix {

	private final LinkedList mInjectors = new LinkedList();

	/**
	 * Executes all the injections associated with this matrix. It actually invokes
	 * the inject(Object target) method on each Injector. 
	 * 
	 * @param target the injection target
	 * @exception InjectionException thrown in case of any problems during injection execution
	 */
	public void inject(Object target) throws InjectionException {
		// TODO log target
		for (Iterator iter = mInjectors.iterator(); iter.hasNext(); ) {
			Injector injector = (Injector) iter.next();
			injector.inject(target);
		}
	}
	
	/**
	 * Adds an Injector at the end
	 * 
	 * @param injector the injector to add
	 */
	public void addInjector(Injector injector) {
		mInjectors.add(injector);
	}
	
	/**
	 * Adds an Injector at the beginning
	 * 
	 * @param injector the injector to add
	 */
	public void addFirst(Injector injector) {
		mInjectors.addFirst(injector);
	}
	
	/**
	 * Adds a Collection of injectors at the end
	 * 
	 * @param injectors
	 */
	public void addInjectors(Collection injectors) {
		mInjectors.addAll(injectors);
	}
	
}
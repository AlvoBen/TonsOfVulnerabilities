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

/**
 * Base type for all injections. It contains all the data needed to perform the actual
 * injection and also provides a convenient way to do this.
 * 
 * Either a field or a method can be the target of an injection. That is why there will be 
 * two default implementations - MethodInjector and FieldInjector.
 * 
 * @author  Vesselin Mitrov, vesselin.mitrov@sap.com
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public interface Injector {

	/**
	 * Performs the injection operation on the instance passed as an argument
	 * 
	 * @param instance the injection target
	 * @exception InjectionException thrown in case of any problems during injection execution
	 */
	public void inject(Object instance) throws InjectionException;

	/**
	 * Gets the type name of the injection target
	 *
	 * @return the type name of the injection target
	 */
	public String getTargetType();

	/**
	 * Sets the factory for obtaining the proper objects to be injected
	 *
	 * @param factory the factory for obtaining the proper objects to be injected
	 */
	public void setFactory(ObjectFactory factory);
}
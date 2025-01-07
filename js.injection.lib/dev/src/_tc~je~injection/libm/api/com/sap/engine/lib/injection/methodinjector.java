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

import java.lang.reflect.*;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class describes an injection which target is method. There should be a separate
 * instance of this class per each method injection.
 * 
 * It keeps the Method to be the target of the injection and use the Reflection API to 
 * execute the actual injection operation, i.e. invoke the method on the given instance.
 *
 * @author  Vesselin Mitrov, vesselin.mitrov@sap.com
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public class MethodInjector extends ClassElementInjector {
	
	private static final Location LOCATION = Location.getLocation("com.sap.engine.lib.injection");
	
	// the factory for obtaining the proper objects to be injected
	private ObjectFactory factory;
	
	// the target of the injection
	private final Method method;
	
	/**
	 * Constructor
	 * 
	 * @param _factory the factory for obtaining the proper objects to be injected
	 * @param _method the target of the injection
	 */
	public MethodInjector(ObjectFactory _factory, Method _method) {
		final String METHOD = "MethodInjector.<init>(ObjectFactory _factory, Method _method)";
		
		if (LOCATION.bePath()) {
			LOCATION.logT(Severity.PATH, METHOD, "factory = {0}, method = {1}", new Object[] { _factory, _method } );
		}
		
		if (_method == null) {
			throw new IllegalArgumentException("Method should not be null.");
		}
		
		factory = _factory;
		method = _method;
		method.setAccessible(true);
	}

	/**
	 * Contructor.
	 * Note: The ObjectFactory must be set before performing any injection operation.
	 *
	 * @param _method the target of injection
	 */
	public MethodInjector(Method _method) {
		this(null, _method);
	}

	/**
	 * Sets the factory for obtaining the proper objects to be injected
	 *
	 * @param _factory the factory for obtaining the proper objects to be injected
	 */
	public void setFactory(ObjectFactory _factory) {
		final String METHOD = "MethodInjector.setFactory(ObjectFactory _factory)";

		if (LOCATION.bePath()) {
			LOCATION.logT(Severity.PATH, METHOD, "factory = {0}", new Object[] { _factory } );
		}

		if (_factory == null) {
			throw new IllegalArgumentException("ObjectFactory should not be null.");
		}

		factory = _factory;
	}

	/**
	 * Gets the type name of the injection target
	 *
	 * @return the type name of the injection target
	 */
	public String getTargetType() {
		return method.getParameterTypes()[0].getName();
	}

	/**
	 * Performs the injection operation on the instance passed as an argument
	 * 
	 * @param instance the injection target
	 * @exception InjectionException thrown in case of any problems during method injection execution
	 */
	public void inject(Object instance) throws InjectionException {
		final String METHOD = "MethodInjector.inject(Object instance)";
		
		if (LOCATION.bePath()) {
			LOCATION.logT(Severity.PATH, METHOD, "instance = {0}", new Object[] { instance });
		}

		Object value = null;
		try {
			value = factory.getObject();
		} catch (Exception e) {
			throw new InjectionException("Injection on method " + method.getName() + " of instance " + instance + " failed. Could not get a value to be injected from the factory.", e);
		}

		if (value == null) {
			if (LOCATION.beDebug()) {
				LOCATION.logT(Severity.DEBUG, METHOD, "Value returned from factory {0} is null. Nothing will be injected", new Object[]{factory});
			}

			return;
		}
		if (LOCATION.beDebug()) {
			LOCATION.logT(Severity.DEBUG, METHOD, "value = {0}", new Object[] { value });
		}
		
		/**
		 * Validation block
		 */
		Class[] params = method.getParameterTypes();
		if (params == null || params.length != 1) {
			String errorMessage = "Injection on setter method " + method.getName() + " of instance " + instance
							+ " failed because the setter method has not a single parameter.";
			LOCATION.logT(Severity.ERROR, METHOD, errorMessage); 
			throw new InjectionException(errorMessage);			
		}
		Class paramClass = params[0];
		Class valueClass = value.getClass();
		if (!checkAssignability(paramClass, valueClass)) {
			String errorMessage = "Injection on method " + method.getName() + " of instance " + instance
							+ " failed because the single parameter with type " + paramClass.getName() + " is not assignable from injection object " + value + ".";
			LOCATION.logT(Severity.ERROR, METHOD, errorMessage);
			throw new InjectionException(errorMessage);
		}
		
		// performing the injection, i.e. invoking the setter method specified for injection
		try {
			method.invoke(instance, new Object[] { value });
			LOCATION.logT(Severity.DEBUG, METHOD, "Injection passed successfully.");
		} catch (InvocationTargetException target) {
			LOCATION.traceThrowableT(Severity.ERROR, METHOD, "Injection failed", target.getCause());
			throw new InjectionException("Injection on method " + method.getName() + " of instance " + instance + " failed.",
					target.getCause() != null ? target.getCause() : target);
		} catch (Exception e) {
			LOCATION.traceThrowableT(Severity.ERROR, METHOD, "Injection failed", e);
			throw new InjectionException("Injection on method " + method.getName() + " of instance " + instance + " failed.", e);
		}
	}

}

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
 * This class describes an injection which target is field. There should be a separate
 * instance of this class per each field injection.
 * 
 * It keeps the Field to be the target of the injection and use the Reflection API to 
 * execute the actual injection operation, i.e. set the field on the given instance.
 *
 * @author  Vesselin Mitrov, vesselin.mitrov@sap.com
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public class FieldInjector extends ClassElementInjector {
	
	private static final Location LOCATION = Location.getLocation("com.sap.engine.lib.injection");

	// the factory for obtaining the proper objects to be injected
	private ObjectFactory factory;
	
	// the target of the injection
	private final Field field;
	
	/**
	 * Contructor
	 * 
	 * @param _factory the factory for obtaining the proper objects to be injected
	 * @param _field the target of injection
	 */
	public FieldInjector(ObjectFactory _factory, Field _field) {
		final String METHOD = "FieldInjector.<init>(ObjectFactory _factory, Field _field)";
		
		if (LOCATION.bePath()) {
			LOCATION.logT(Severity.PATH, METHOD, "factory = {0}, field = {1}", new Object[] { _factory, _field } );
		}
		
		if (_field == null) {
			throw new IllegalArgumentException("Field should not be null.");
		}
		
		factory = _factory;
		field = _field;
		field.setAccessible(true);
	}

	/**
	 * Contructor.
	 * Note: The ObjectFactory must be set before performing any injection operation.
	 *
	 * @param _field the target of injection
	 */
	public FieldInjector(Field _field) {
		this(null, _field);
	}

	/**
	 * Sets the factory for obtaining the proper objects to be injected
	 *
	 * @param _factory the factory for obtaining the proper objects to be injected
	 */
	public void setFactory(ObjectFactory _factory) {
		final String METHOD = "FieldInjector.setFactory(ObjectFactory _factory)";

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
		return field.getType().getName();
	}

	/**
	 * Performs the injection operation on the instance passed as an argument
	 * 
	 * @param instance the injection target
	 * @exception InjectionException thrown in case of any problems during field injection execution
	 */
	public void inject(Object instance) throws InjectionException {
		final String METHOD = "FieldInjector.inject(Object instance)";
		
		if (LOCATION.bePath()) {
			LOCATION.logT(Severity.PATH, METHOD, "instance = {0}", new Object[] { instance });
		}

		Object value = null;
		try {
			value = factory.getObject();
		} catch (Exception e) {
			throw new InjectionException("Injection on field " + field.getName() + " of instance " + instance + " failed. Could not get a value to be injected from the factory.", e);
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
		Class fieldClass = field.getType();
		Class valueClass = value.getClass();
		if (!checkAssignability(fieldClass, valueClass)) {
			String errorMessage = "Injection on field " + field.getName() + " of instance " + instance
							+ " failed because the field is not assignable from injection object " + value + ".";
			LOCATION.logT(Severity.ERROR, METHOD, errorMessage); 
			throw new InjectionException(errorMessage);
		}
		
		// injecting the value to instance
		try {
			field.set(instance, value);
			LOCATION.logT(Severity.DEBUG, METHOD, "Injection passed successfully.");
		} catch (Exception e) {
			LOCATION.traceThrowableT(Severity.ERROR, METHOD, "Injection failed", e);
			throw new InjectionException("Injection on field " + field.getName() + " of instance " + instance + " failed.", e);
		}
	}

}
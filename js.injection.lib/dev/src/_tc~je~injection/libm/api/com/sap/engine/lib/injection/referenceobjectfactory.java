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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.naming.Reference;

/**
 *
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public class ReferenceObjectFactory implements ObjectFactory {

	public static final String EJB_CONTEXT_FACTORY = "com.sap.engine.services.ejb3.runtime.impl.EJBContextFactory";
	public static final String EJB_OBJECT_FACTORY = "com.sap.engine.services.ejb3.runtime.impl.EJBObjectFactory";
	public static final String RESOURCE_OBJECT_FACTORY = "com.sap.engine.services.connector.ResourceObjectFactory";
	public static final String ORPERSISTENCE_OBJECT_FACTORY = "com.sap.engine.services.orpersistence.container.ORPersistenceObjectFactory";
	public static final String WEBSERVICE_OBJECT_FACTORY = "com.sap.engine.interfaces.webservices.server.wsclient.ServiceObjectFactory";
	public static final String NAMING_OBJECT_FACTORY = "com.sap.engine.services.jndi.ComponentObjectFactory";
	public static final String WEBSERVICE_CONTEXT_OBJECT_FACTORY = "com.sap.engine.services.webservices.jaxws.ctx.WebServiceContextObjectFactory";
	public static final String MAILSESSION_OBJECT_FACTORY = "com.sap.engine.services.javamail.server.MailSesionObjectFactory";
	public static final String SCA_OBJECT_FACTORY = "com.sap.engine.services.sca.runtime.SCAObjectFactory";

	public static final String SERVICE_EJB_LOADER = "service:ejb";
	public static final String SERVICE_CONNECTOR_LOADER = "service:connector";
	public static final String SERVICE_ORPERSISTENCE_LOADER = "service:orpersistence";
	public static final String INTERFACE_WEBSERVICE_LOADER = "interface:webservices";
	public static final String LIBRARY_WEBSERVICES_LOADER = "library:webservices_lib";
	public static final String SERVICE_JAVAMAIL_LOADER = "service:javamail";
	public static final String SERVICE_SCA_LOADER = "service:tc~je~sca~integration";

	private static final Set<String> STATELESS_FACTORIES_NAMES = new HashSet<String>();
	static {
		STATELESS_FACTORIES_NAMES.add(EJB_CONTEXT_FACTORY);
		STATELESS_FACTORIES_NAMES.add(EJB_OBJECT_FACTORY);
		STATELESS_FACTORIES_NAMES.add(RESOURCE_OBJECT_FACTORY);
		STATELESS_FACTORIES_NAMES.add(ORPERSISTENCE_OBJECT_FACTORY);
	}
	private static final Map<String, javax.naming.spi.ObjectFactory> STATELESS_FACTORIES_CACHE = new Hashtable<String, javax.naming.spi.ObjectFactory>();
	
	
	private javax.naming.spi.ObjectFactory objectFactory;
	private javax.naming.Reference reference;
	
	
	public ReferenceObjectFactory(String className, String factoryName, String factoryLocation, ClassLoader factoryLoader) throws InjectionException {
	    this.reference = new Reference(className, factoryName, factoryLocation);

	    if (STATELESS_FACTORIES_NAMES.contains(factoryName)) {
	    	objectFactory = STATELESS_FACTORIES_CACHE.get(factoryName);
	    	if (objectFactory == null) {
	    		objectFactory = createObjectFactory(factoryName, factoryLoader);
	    		STATELESS_FACTORIES_CACHE.put(factoryName, objectFactory);
	    	}
	    } else {
	    	objectFactory = createObjectFactory(factoryName, factoryLoader);
	    }
	}

	
	private javax.naming.spi.ObjectFactory createObjectFactory(String factoryName, ClassLoader factoryLoader) throws InjectionException {
		try {
			Class factoryClass = Class.forName(factoryName, true, factoryLoader);
			return (javax.naming.spi.ObjectFactory) factoryClass.newInstance();
		} catch (ClassNotFoundException cnfe) {
			throw new InjectionException("Cannot load object factory class " + factoryName + " using loader " + factoryLoader, cnfe);
		} catch (InstantiationException ie) {
			throw new InjectionException("Cannot create instance of " + factoryName, ie);
		} catch (IllegalAccessException iae) {
			throw new InjectionException("Cannot create instance of " + factoryName, iae);
		}
	}

	
	/**
	 * Returns an object according to the specific type of this factory
	 *
	 * @return the object returned by the javax.naming.spi.ObjectFactory.getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable) method
	 * @see com.sap.engine.lib.injection.ObjectFactory#getObject()
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	public Object getObject() throws Exception {
		return objectFactory.getObjectInstance(this.getReference(), null, null, null);
	}

	
	public Reference getReference() {
	    return reference;
	}

}

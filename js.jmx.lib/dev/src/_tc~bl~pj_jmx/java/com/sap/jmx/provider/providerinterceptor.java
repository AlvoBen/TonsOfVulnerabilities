/*
 * Created on Jun 16, 2004
 *
 */
package com.sap.jmx.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.sap.jmx.provider.lazycache.LazyMBeanSystem;
import com.sap.jmx.provider.lazycache.LazyMBeanSystemImpl;
import com.sap.jmx.provider.registry.MBeanProviderInfo;
import com.sap.jmx.provider.registry.ProviderMBeanSystem;
import com.sap.jmx.provider.registry.ProviderMBeanSystemImpl;
import com.sap.pj.jmx.server.interceptor.BasicMBeanServerInterceptor;
import com.sap.pj.jmx.server.interceptor.InvocationContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author i024127
 *
 */
public class ProviderInterceptor extends BasicMBeanServerInterceptor implements ProviderInterceptorMBean, MBeanServerAccess {

	//a location used to log errors
	private static final Location LOCATION = Location.getLocation(ProviderInterceptor.class);
	
	//reference to the Lazy MBean System
	private LazyMBeanSystem lazySystem;
	
	//reference to the Provider Registry
	private ProviderMBeanSystem providerRegistry;
	
	//default MBean server domain
	private String defaultDomain;
		
	//constructor
	public ProviderInterceptor(String defaultDomain) {
		this.defaultDomain = defaultDomain;
		
		//init the lazy MBean System
		lazySystem = LazyMBeanSystemImpl.getLazyMBeanSystem(this);
		
		//init the provider MBean registry
		providerRegistry = ProviderMBeanSystemImpl.getProviderMBeanSystem();
	}
	
	//---- Methods from Provider Interceptor MBean interface ----
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#registerStandardProvider(com.sap.jmx.provider.StandardMBeanProvider)
	 */
	public synchronized String registerStandardProvider(StandardMBeanProvider provider) throws ProviderException, JMException {
		MBeanProviderInfo providerInfo = providerRegistry.registerProvider(provider);
		ProviderRootContext context = ProviderContextImpl.createProviderContext(this);
		try {
			provider.init(context);
		} catch(Exception e) {
			LOCATION.traceThrowableT(Severity.ERROR, "Error occured during registration of standard mbean provider with name " + providerInfo.getProviderName(), e);
			try {
				unregisterProvider(providerInfo.getProviderName());
			} catch(Exception ee) {
				LOCATION.traceThrowableT(Severity.DEBUG, "Error occured during roll back of the standard mbean provider registartion", ee);				
			}
			throw new ProviderException(e.getMessage());
		}
		return providerInfo.getProviderName();
	}

	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#registerExtendedProvider(com.sap.jmx.provider.ExtendedMBeanProvider)
	 */
	public synchronized String registerExtendedProvider(ExtendedMBeanProvider provider) throws ProviderException, JMException {
		MBeanProviderInfo providerInfo = providerRegistry.registerProvider(provider);
		ProviderRootContext context = ProviderContextImpl.createProviderContext(this);
		try {
			provider.init(context);
		} catch(Exception e) {
			LOCATION.traceThrowableT(Severity.ERROR, "Error occured during registration of extended mbean provider with name " + providerInfo.getProviderName(), e);
			try {
				unregisterProvider(providerInfo.getProviderName());
			} catch(Exception ee) {
				LOCATION.traceThrowableT(Severity.DEBUG, "Error occured during roll back of the extended mbean provider registartion", ee);				
			}
			throw new ProviderException(e.getMessage());
		}
		providerRegistry.activateExtendedProvider(providerInfo.getProviderName(), providerInfo);
		return providerInfo.getProviderName();		
	}

	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#registerProvider(java.lang.String, com.sap.jmx.provider.Provider)
	 */
	public synchronized void registerProvider(String name, Provider provider) {
		providerRegistry.registerProvider(name, provider);
	}	
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#unregisterProvider(java.lang.String)
	 */
	public synchronized void unregisterProvider(String name)  throws ProviderException, JMException {
		providerRegistry.unregisterProvider(name, false, this);
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#unregisterProvider(java.lang.String, boolean)
	 */
	public synchronized void unregisterProvider(String name, boolean closeConnections)  throws ProviderException, JMException {
		providerRegistry.unregisterProvider(name,closeConnections, this);
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#createConnection(java.lang.String, java.util.Properties)
	 */
	public synchronized String createConnection(String providerName, Properties properties) throws ProviderException, JMException {
		return providerRegistry.createConnection(providerName, properties, this);
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#closeConnection(java.lang.String)
	 */
	public synchronized void closeConnection(String connectionName) throws ProviderException, JMException {
		providerRegistry.closeConnection(connectionName, this, false);
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#closeConnection(java.lang.String, boolean)
	 */
	public void closeConnection(String connectionName, boolean wait) throws ProviderException, JMException {
		providerRegistry.closeConnection(connectionName, this, wait);
	}

	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#allCalls()
	 */
	public int allCalls() {
		return lazySystem.getAllCalls(); 
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#cacheSize()
	 */
	public int cacheSize() {
		return lazySystem.getCacheSize();
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#cacheHits()
	 */
	public int cacheHits() {
		return lazySystem.getCacheHits();
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#dumpCache()
	 */
	public void dumpCache() throws Exception {
		if (LOCATION.beDebug()) {
			LOCATION.debugT(lazySystem.getCacheDump());
		}
	}

	/**
	 * @see com.sap.jmx.provider.ProviderInterceptorMBean#getProviderNames()
	 */
	public String[] getProviderNames() {
		return providerRegistry.getProviderNames();
	}	
	
	//---- Methods from Basic MBean Server Interceptor MBean class ----
	
	/**
	 * @see com.sap.pj.jmx.server.interceptor.BasicMBeanServerInterceptorMBean#getType()
	 */
	public String getType() {
		return "ProviderInterceptor";
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.BasicMBeanServerInterceptorMBean#getAttribute(InvocationContext context, ObjectName name, String attribute)
	 */	
	public Object getAttribute(InvocationContext context, ObjectName name, String attribute)
	 														throws	AttributeNotFoundException,	InstanceNotFoundException,
	 																MBeanException,	ReflectionException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.getAttribute(context, name, attribute);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			 }
		} else {
			return super.getAttribute(context, name, attribute);
		}
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.BasicMBeanServerInterceptorMBean#getAttributes(InvocationContext context, ObjectName name, String[] attributes)
	 */	
	public AttributeList getAttributes(InvocationContext context, ObjectName name, String[] attributes)
			 												throws InstanceNotFoundException, ReflectionException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.getAttributes(context, name, attributes);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			return super.getAttributes(context, name, attributes);
		}
	}		

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#getMBeanInfo(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName)
	 */
	public MBeanInfo getMBeanInfo(InvocationContext context, ObjectName name)
															throws 	InstanceNotFoundException, IntrospectionException,
																	ReflectionException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.getMBeanInfo(context, name);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			return super.getMBeanInfo(context, name);
		}
	}		

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#getObjectInstance(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName)
	 */
	public ObjectInstance getObjectInstance(InvocationContext context, ObjectName name)
																throws InstanceNotFoundException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.getObjectInstance(context, name);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			return super.getObjectInstance(context, name);
		}
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#invoke(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName, java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	public Object invoke(InvocationContext context, ObjectName name, String operationName, Object[] params,	String[] signature)
																	throws InstanceNotFoundException, MBeanException, 
																			ReflectionException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.invoke(context, name, operationName, params, signature);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			return super.invoke(context, name, operationName, params, signature);
		}
	}
	
	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#isInstanceOf(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName, java.lang.String)
	 */
	public boolean isInstanceOf(InvocationContext context, ObjectName name,	String className)
																	throws InstanceNotFoundException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.isInstanceOf(context, name, className);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			return super.isInstanceOf(context, name, className);
		}
	}
	
	/** 
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#setAttribute(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName, javax.management.Attribute)
	 */
	public void setAttribute(InvocationContext context, ObjectName name, Attribute attribute)
															throws	AttributeNotFoundException,	InstanceNotFoundException,
																	InvalidAttributeValueException, MBeanException,	ReflectionException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				super.setAttribute(context, name, attribute);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			super.setAttribute(context, name, attribute);
		}
	}
	
	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#setAttributes(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName, javax.management.AttributeList)
	 */
	public AttributeList setAttributes(InvocationContext context, ObjectName name, AttributeList attributes)
			 												throws InstanceNotFoundException, ReflectionException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
				// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
				name = fullName;
			}
			
			boolean isLocked = lazySystem.lockMBean(name, fullName, provider, this, false);
			try {
				return super.setAttributes(context, name, attributes);
			} finally {
				if (isLocked) {
					lazySystem.unlockMBean(fullName);
				}
			}
		} else {
			return super.setAttributes(context, name, attributes);
		}
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#getClassLoaderFor(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName)
	 */
	public ClassLoader getClassLoaderFor(InvocationContext context, ObjectName name) throws InstanceNotFoundException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		if (provider != null) {
			return provider.getMBeanProvider().getClass().getClassLoader();
			//return this.getClass().getClassLoader();
		} else {
			return super.getClassLoaderFor(context, name);
		}		
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#getClassLoader(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName)
	 */
	public ClassLoader getClassLoader(InvocationContext context, ObjectName name) throws InstanceNotFoundException {
		MBeanProviderInfo provider = providerRegistry.getProvider(name);
		if (provider != null) {
			return provider.getMBeanProvider().getClass().getClassLoader();
			//return this.getClass().getClassLoader();
		} else {
			return super.getClassLoader(context, name);
		}		
	}
	
	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#unregisterMBean(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName)
	 */
	public void unregisterMBean(InvocationContext context, ObjectName name)	throws InstanceNotFoundException, MBeanRegistrationException {
		MBeanProviderInfo provider = providerRegistry.addProviderSharedLock(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			try {
				while (!lazySystem.forceExclusiveLock(fullName)) {}
						
				lazySystem.unregisterMBean(fullName);			
				super.unregisterMBean(context, name);
			} finally {
				lazySystem.removeExclusiveLock(fullName);
				providerRegistry.releaseProviderSharedLock(provider);
			}
		} else {
			super.unregisterMBean(context, name);
		}
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#queryNames(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName, javax.management.QueryExp)
	 */
	public Set queryNames(InvocationContext context, ObjectName name, QueryExp query) {
		if (name != null) {
			MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
			if ((provider != null) && (provider.getType() == MBeanProviderInfo.EXTENDED_PROVIDER_TYPE) && (provider.addSharedLock())){
				ExtendedMBeanProvider extendedMBeanProvider = (ExtendedMBeanProvider) provider.getMBeanProvider();
				Set result = null;
				try {
					result = extendedMBeanProvider.queryNames(name, query);
				} finally {
					provider.releaseSharedLock();
				}
				return result;
			}
		}
		Collection extProviders = providerRegistry.getExtendedProviders();
		if (!extProviders.isEmpty()) {
			Iterator iter = extProviders.iterator();
			HashSet result = new HashSet();
			while (iter.hasNext()) {
				MBeanProviderInfo provider = (MBeanProviderInfo) iter.next();
				try {
					Set tmpResult = ((ExtendedMBeanProvider) provider.getMBeanProvider()).queryNames(name, query);
					if (tmpResult != null) {
						result.addAll(tmpResult);
					}
				} catch(Exception e) {
					LOCATION.traceThrowableT(Severity.ERROR, "Error occured during execution of query request from lazy mbean provider " + provider.getProviderName(), e);					
				}
			}
			Set mbeanServerResult = super.queryNames(context, name, query);
			if (mbeanServerResult != null) {
				result.addAll(mbeanServerResult);
			}
			return result;
		} else {
			return super.queryNames(context, name, query);
		}
	}	
	
	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#getMBeanCount(com.sap.pj.jmx.server.interceptor.InvocationContext)
	 */
	public Integer getMBeanCount(InvocationContext context) {
		Collection extProviders = providerRegistry.getExtendedProviders();
		if (!extProviders.isEmpty()) {
			Iterator iter = extProviders.iterator();
			int totalAddCount = 0; 			
			while (iter.hasNext()) {
				MBeanProviderInfo provider = (MBeanProviderInfo) iter.next();
				try {
					int lazyCount = ((ExtendedMBeanProvider) provider.getMBeanProvider()).getLazyMBeanCount();
					if (lazyCount >= 0) {
						int alreadyRegistered = provider.getMBeanCount();
						if (lazyCount > alreadyRegistered) {
							totalAddCount += (lazyCount - alreadyRegistered);
						}
					} else {
						LOCATION.warningT("Warning occured during execution of getLazyMBeanCount request (value is less than 0) from lazy mbean provider " + provider.getProviderName());						
					}
				} catch(Exception e) {
					LOCATION.traceThrowableT(Severity.ERROR, "Error occured during execution of  getLazyMBeanCount request from lazy mbean provider " + provider.getProviderName(), e);					
				}
			}
			Integer mbeanServerResult = super.getMBeanCount(context);
			return new Integer(mbeanServerResult.intValue() + totalAddCount);
		} else {
			return super.getMBeanCount(context);
		}
	}

	/**
	 * @see com.sap.pj.jmx.server.interceptor.MBeanServerInterceptor#isRegistered(com.sap.pj.jmx.server.interceptor.InvocationContext, javax.management.ObjectName)
	 */
	public boolean isRegistered(InvocationContext context, ObjectName name) {
		if (name != null) {
			MBeanProviderInfo provider = providerRegistry.getProvider(name);
		
			if ((provider != null) && (provider.getType() == MBeanProviderInfo.EXTENDED_PROVIDER_TYPE) && (provider.addSharedLock())){
				ExtendedMBeanProvider extendedMBeanProvider = (ExtendedMBeanProvider) provider.getMBeanProvider();
				boolean result;
				try {
					result = extendedMBeanProvider.isLazyRegistered(name);
				} finally {
					provider.releaseSharedLock();
				}
				return result;
			} 
		}
		return super.isRegistered(context, name);	
	}
	
	
	//---- Methods from MBean Server Access interface ----
	
	/** 
	 * @see com.sap.jmx.provider.MBeanServerAccess#unregisterFromMBeanServer(javax.management.ObjectName)
	 */
	public void unregisterFromMBeanServer(ObjectName name)	throws InstanceNotFoundException, MBeanRegistrationException {
		super.unregisterMBean(null, name);
	}
	
	/**
	 * @see com.sap.jmx.provider.MBeanServerAccess#registerToMBeanServer(javax.management.ObjectName, java.lang.Object)
	 */
	public void registerToMBeanServer(ObjectName name, Object object) throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
		super.registerMBean(null, object, name);
	}
	
	/**
	 * @see com.sap.jmx.provider.MBeanServerAccess#getDefaultDomain()
	 */
	public String getDefaultDomain() {
		return defaultDomain;
	}

	/** 
	 * @see com.sap.jmx.provider.MBeanServerAccess#getFullName(javax.management.ObjectName)
	 */
	public ObjectName getFullName(ObjectName name) {
		if (name == null) {
			return null;
		}
		if (name.getDomain().length() == 0) {
			try {
				return new ObjectName(defaultDomain + name.getCanonicalName());
			} catch(MalformedObjectNameException monException) {
				LOCATION.traceThrowableT(Severity.ERROR, "Error when trying to extend the object name " + name + " with the default domain " + defaultDomain, monException);		
				return name;
			}
		} else {
			return name;
		}
	}
	
	/** 
	 * @see com.sap.jmx.provider.MBeanServerAccess#getLocalSAP_ITSAMJ2eeNode()
	 */
	public ObjectName getLocalSAP_ITSAMJ2eeNode() {
		try {
			Set result = super.queryNames(null, new ObjectName("*:*,cimclass=SAP_ITSAMJ2eeNode,SAP_J2EEClusterNode=\"\""), null);
			if ((result != null) && (!result.isEmpty())) {
				return (ObjectName) result.iterator().next();
			} else {
				return null;
			}
		} catch(MalformedObjectNameException monException) {
			LOCATION.traceThrowableT(Severity.ERROR, "Error when trying to query for local J2ee Node object name", monException);		
			return null;
		}
	}
	
	public void invalidateMBean(ObjectName name, boolean wait) throws InstanceNotFoundException, MBeanRegistrationException {
		MBeanProviderInfo provider = providerRegistry.addProviderSharedLock(name);
		
		if (provider != null) {
			ObjectName fullName = getFullName(name);
			
			/*if (wait) {
				lazySystem.addExclusiveLock(fullName);
				try {			
					if (lazySystem.unregisterMBean(fullName)) {			
						super.unregisterMBean(null, name);
					}
				} finally {
					lazySystem.removeExclusiveLock(fullName);
					providerRegistry.releaseProviderSharedLock(provider);
				}
			} else {*/
				while (!lazySystem.forceExclusiveLock(fullName)) {}
				
				try {			
					if (lazySystem.unregisterMBean(fullName)) {			
						super.unregisterMBean(null, name);
					}
				} finally {
					lazySystem.removeExclusiveLock(fullName);
					providerRegistry.releaseProviderSharedLock(provider);
				}
			//}
		}
	}
}
package com.sap.jmx.provider.lazycache;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.sap.jmx.provider.MBeanServerAccess;
import com.sap.jmx.provider.registry.MBeanProviderInfo;
import com.sap.jmx.provider.registry.ProviderConnectionInfo;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class LazyMBeanSystemImpl implements LazyMBeanSystem {

	// -----Static Part-----

	// -----attributes------

	// an instance of LazyMBeanSystem
	private static LazyMBeanSystem lazySystem = null;

	private static int timeStamp = 0;

	// a location used to log errors
	private static final Location LOCATION = Location
			.getLocation(LazyMBeanSystemImpl.class);

	// ------methods--------

	// a factory method for getting the LazyMBeanSystem instance
	public static synchronized LazyMBeanSystem getLazyMBeanSystem(
			MBeanServerAccess server) {
		if (lazySystem == null) {
			lazySystem = new LazyMBeanSystemImpl(server);
		}
		return lazySystem;
	}

	public static synchronized int getTimeStamp() {
		return timeStamp++;
	}

	// ------Instance Part-----

	// ------attributes------

	// cache repository
	private LazyObjectNameCache cache;

	// mbean lazy loader
	private LazyMBeanLoader loader;

	private int allCalls;

	private int cacheHits;

	// ------methods--------

	// constructor
	private LazyMBeanSystemImpl(MBeanServerAccess server) {
		cache = new LazyObjectNameCache();
		loader = new LazyMBeanLoader(this, server);
		allCalls = 0;
		cacheHits = 0;
	}

	public void addExclusiveLock(ObjectName name) {
		cache.addExclusiveLock(name);
	}

	public boolean forceExclusiveLock(ObjectName name) {
		return cache.forceExclusiveLock(name);
	}
	
	public boolean registerMBean(ObjectName name, ObjectName fullName, Object mBean,	MBeanProviderInfo provider, MBeanServerAccess server, boolean excludeFromLazyGC) throws JMException {
		if (!cache.addSharedLock(fullName)) {
			return registerMBean(name, fullName, mBean, provider, server, false,
					excludeFromLazyGC, false);
		} else {
			cache.removeSharedLock(fullName);
			return true;
		}
	}

	public boolean lockMBean(ObjectName name, ObjectName fullName, MBeanProviderInfo provider, MBeanServerAccess server, boolean lazy) {
		allCalls++;
		if (!cache.addSharedLock(fullName)) {
			Object mBean = provider.instantiateMBean(name);
			try {
				return registerMBean(name, fullName, mBean, provider, server, true,
						false, lazy);
			} catch (JMException jmException) {
				LOCATION.traceThrowableT(Severity.WARNING,
						"an exception occured during registration of lazy mbean with object name : "
								+ name.getCanonicalName(), jmException);
				return false;
			}
		} else {
			cacheHits++;
			return true;
		}
	}

	public void removeExclusiveLock(ObjectName name) {
		cache.removeExclusiveLock(name);
	}

	public boolean unregisterMBean(ObjectName name) {
		return cache.unregisterMBean(name);
	}

	public void unlockMBean(ObjectName fullName) {
		cache.removeSharedLock(fullName);
	}

	public int getAllCalls() {
		return allCalls;
	}

	public int getCacheSize() {
		return cache.getSize();
	}

	public int getCacheHits() {
		return cacheHits;
	}

	public String getCacheDump() {
		return cache.dump();
	}

	public void refreshConnected(ObjectName name) throws JMException {
		if (cache.addSharedLock(name)) {
			try {
				LazyMBeanInfo lazyMBean = cache.getMBeanInfo(name);
				if (lazyMBean != null) {
					loader.lazyLoad(lazyMBean);
				}
			} finally {
				cache.removeSharedLock(name);
			}
		}
	}

	public Object getMBean(ObjectName name, ObjectName fullName, MBeanProviderInfo provider, MBeanServerAccess server, boolean instantiate) throws JMException {
		if (!cache.addSharedLock(fullName)) {
			if (instantiate) {
				Object mBean = provider.instantiateMBean(name);
				if (registerMBean(name, fullName, mBean, provider, server, false, false,
						false)) {
					return mBean;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			try {
				LazyMBeanInfo lazyMBean = cache.getMBeanInfo(fullName);
				if (lazyMBean != null) {
					return lazyMBean.getMBean();
				} else {
					return null;
				}
			} finally {
				cache.removeSharedLock(fullName);
			}
		}
	}

	public void timeoutMBeans(long time, MBeanServerAccess server) {
		LazyMBeanInfo info = cache.getLast();
		while ((info != null) && (info.getObjectTimeStamp() < time)) {
			try {
				server.unregisterMBean(null, info.getObjectName());
			} catch (Exception e) {
				LOCATION.traceThrowableT(Severity.DEBUG,
						"Exception during lazy unregistration", e);
				// nothing to do
				// $JL-EXC$
			}
			info = cache.getLast();
		}
	}

	public void reduceCacheSize(int targetSize, MBeanServerAccess server) {
		LazyMBeanInfo info = cache.getLast();
		while ((info != null) && (cache.getSize() > targetSize)) {
			try {
				server.unregisterMBean(null, info.getObjectName());
			} catch (Exception e) {
				LOCATION.traceThrowableT(Severity.DEBUG, "Exception during lazy un-registration", e);
				// nothing to do
				// $JL-EXC$
			}
			info = cache.getLast();
		}
	}
	
	// ----private methods----
	private boolean registerMBean(ObjectName name, ObjectName fullName, Object mBean, MBeanProviderInfo provider, MBeanServerAccess server, boolean keepLock, boolean excludeFromLazyGC, boolean lazy) throws JMException {
		if ((mBean != null) && (provider != null) && (provider.addSharedLock())) {
			cache.addExclusiveLock(fullName);
			try {
				if (!cache.isRegistered(fullName)) {
					try {
						server.registerToMBeanServer(name, mBean);
						LazyMBeanInfo info = cache.registerMBean(fullName, mBean, excludeFromLazyGC);
						if (!lazy) {
							// add to lazy list
							loader.lazyLoad(info);
						}
						info.setProvider(provider);
						provider.register(info);					
					} catch (NotCompliantMBeanException ncmbException) {
						LOCATION.traceThrowableT(Severity.WARNING,
								"try to register lazy mbean with wrong object name : "
								+ name.getCanonicalName(), ncmbException);
						cache.removeExclusiveLock(fullName);
						provider.releaseSharedLock();
						throw ncmbException;
					} catch (InstanceAlreadyExistsException iaeException) {
						LOCATION.traceThrowableT(Severity.WARNING,
								"try to register twice the lazy mbean with object name : "
								+ name.getCanonicalName(), iaeException);
						cache.removeExclusiveLock(fullName);
						provider.releaseSharedLock();
						throw iaeException;
					} catch (MBeanRegistrationException mbrException) {
						LOCATION.traceThrowableT(Severity.WARNING,
								"an error occured when register the lazy mbean with object name : "
								+ name.getCanonicalName(), mbrException);
						cache.removeExclusiveLock(fullName);
						provider.releaseSharedLock();
						throw mbrException;
					}
				}
			} finally {
				provider.releaseSharedLock();
			}
			if (cache.getSize() > LazyMBeanLoader.CACHE_SIZE_LIMIT) {
				loader.notifyIt();
			}
		} else {
			return false;
		}
		if (keepLock) {
			cache.reduceExclusiveLock(fullName);
		} else {
			cache.removeExclusiveLock(fullName);
		}
		return true;
	}
}

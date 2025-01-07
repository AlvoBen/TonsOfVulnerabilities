/*
 * Created on Jun 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.jmx.provider.lazycache;

import javax.management.ObjectName;

import com.sap.jmx.provider.MBeanProvider;
import com.sap.jmx.provider.MBeanServerAccess;
import com.sap.jmx.provider.registry.MBeanProviderInfo;
import com.sap.jmx.provider.registry.ProviderMBeanSystem;
import com.sap.jmx.provider.registry.ProviderMBeanSystemImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Jasen Minov
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LazyMBeanLoader implements Runnable {

	//a location used to log errors
	private static final Location LOCATION = Location.getLocation(LazyMBeanLoader.class);
	
	private static final int MAX_QUEUE_LENGTH = 1000;
	
	public static final int CACHE_SIZE_LIMIT = 10000;
	
	private static final int CACHE_SIZE_REDUCTION = 8000;
	
	private int count;
	
	private boolean waitFlag;

	private LazyMBeanInfo first;
	private LazyMBeanInfo last;
	
	private LazyMBeanSystem lazySystem;
	private MBeanServerAccess server;
	
	//check for timeout & connection timeout -> each 10 min? 
	private long regularCheckInterval = 600000;
	//unregister MBeans after timeout of 1 hour
	private long lazyMBeanTimeout = 3600000;
	
	public LazyMBeanLoader(LazyMBeanSystem lazySystem, MBeanServerAccess server) {
		this.lazySystem = lazySystem;
		this.server = server;
		
		count = 0;
		
		waitFlag = false;
		(new Thread(this)).start();
	}
	
	public void run() {
		long lastTimeoutCheck = System.currentTimeMillis();
		ProviderMBeanSystem providerSystem = ProviderMBeanSystemImpl.getProviderMBeanSystem();
		while (true) {
			LazyMBeanInfo info = null;
			synchronized (this) {
				if (count == 0) {
					waitFlag = true;					
					try {
						wait(regularCheckInterval);
					} catch(InterruptedException iException) {
						LOCATION.traceThrowableT(Severity.ERROR, "Lazy MBean Loader was interrupted", iException);
					}
					waitFlag = false;					
				}
			}
			
			//check size limit
			if (lazySystem.getCacheSize() > CACHE_SIZE_LIMIT) {
				lazySystem.reduceCacheSize(CACHE_SIZE_REDUCTION, server);
			}
			
			info = getFirst();
			if ((info != null) && (info.isRegistered())) {
				ObjectName objectName = info.getObjectName();			
				MBeanProviderInfo provider = providerSystem.addProviderSharedLock(objectName);
				if (provider != null) {
					try {
						Object mbean = info.getMBean();
						if ((objectName != null) && (mbean != null)) {
							MBeanProvider mbeanProvider = provider.getMBeanProvider();
							if (mbeanProvider != null) {
								ObjectName[] connected = null;
								try {							
									connected = mbeanProvider.getConnected(objectName, mbean);
								} catch(Exception e) {
									LOCATION.traceThrowableT(Severity.WARNING, "An exception occured while tried to load connected MBeans", e);							
								}
							
								if (connected != null) {
									for (int i = 0; i < connected.length; i++) {
										ObjectName fullName = server.getFullName(connected[i]);
										if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
											connected[i] = fullName;
										}
										if (lazySystem.lockMBean(connected[i], fullName, provider, server, true)) {
											lazySystem.unlockMBean(fullName);
										}
									}
								}
							}
						}
					} finally {
						providerSystem.releaseProviderSharedLock(provider);						
					}
				}
			}
			long currentTime = System.currentTimeMillis();
			if ((currentTime - lastTimeoutCheck) > regularCheckInterval) {
				lastTimeoutCheck = currentTime;
				//check timeout of the MBeans
				lazySystem.timeoutMBeans(currentTime - lazyMBeanTimeout, server);
				
				//check timeout of the connections
				providerSystem.timeoutConnections(currentTime, server);
			}
		}		
	}

	private synchronized LazyMBeanInfo getFirst() {
		if (first == null) {
			return null;
		}
		LazyMBeanInfo info = first;
		first = first.getNextToLoad();
		info.setNextToLoad(null);
		if (last == info) {
			last = null;
		}
		count--;
		return info;
	}
	
	public synchronized void lazyLoad(LazyMBeanInfo info) {
		//it is possible to be part from the queue; than return
		if ((info.getNextToLoad() != null) || (first == info)) {
			return;
		}
		
		if (last != null) {
			last.setNextToLoad(info);
			last = info;
		} else {
			first = info;
			last = info;
			
		}
		count++;		
		if (waitFlag) {				
			notify();
		}

		if (count > MAX_QUEUE_LENGTH) {
			//skip the oldest, but don't exceed the queue limit
			first = first.getNextToLoad();
			count--;
		}
	}
	
	public synchronized void notifyIt() {
		if (waitFlag) {
			notify();
		}
	}
}
package com.sap.jmx.provider.registry;

import javax.management.ObjectName;

import com.sap.jmx.provider.MBeanProvider;
import com.sap.jmx.provider.lazycache.LazyMBeanInfo;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class MBeanProviderInfo {

	//----- Static part -----
	
	//constant for Standard providers type
	public static final int STANDARD_PROVIDER_TYPE = 0;
	
	//constant for the Extended providers type
	public static final int EXTENDED_PROVIDER_TYPE = 1;
	
	//constant for the Connection providers type
	public static final int CONNECTION_PROVIDER_TYPE = 2;
	
	//location where to log the errors
	private static final Location LOCATION = Location.getLocation(MBeanProviderInfo.class);
	
	//----- Instance part -----
	
	//the MBeanProvider instance
	private MBeanProvider provider;
	
	//type of the provider
	private int type;
	
	//indicates is it active
	private boolean active;
	
	//a list with all registered mbeans associated with the provider
	private LazyMBeanInfo mbeans;
	
	//size of the list with all registered mbeans associated with the provider	
	private int mbeanCount;
	
	/**
	 *  This flag indicates a exclusive lock that is set
	 */
	private boolean hasExclusive;
	
	/**
	 *  This flag indicates the count of shared locks set
	 */	
	private int lockCount;

	/**
	 *  This flag indicates the number of requests that wait for exclusive lock
	 */	
	private boolean waitForExclusive;
	
	private String providerName;
	
	//constructor
	public MBeanProviderInfo(MBeanProvider provider, int type) {
		this.provider = provider;
		this.type = type;
		active = true;
		mbeans = null;
		
		hasExclusive = false;
		lockCount = 0;
		waitForExclusive = false;
		mbeanCount = 0;
	}
	
	
	/**
	 * Get the MBean provider instance
	 * 
	 * @return The MBean provider instance 
	 */
	public MBeanProvider getMBeanProvider() {
		return provider;
	}
	
	/**
	 * If is active delegates the call to the mbean provider to instantiate the MBean
	 * 
	 * @param name	Name of the mbean
	 * @return	Instance of the mbean
	 */
	public Object instantiateMBean(ObjectName name) {
		if (active) {
			return provider.instantiateMBean(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Deactivate the MBean provider
	 */
	public void deactivate() {
		active = false;
	}
	
	/**
	 * Get type of the MBean provider
	 * 
	 * @return	The type of the MBean provider
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Add new mbean associatied with the provider
	 *  
	 * @param name Name of the mbean
	 */
	public synchronized void register(LazyMBeanInfo name) {
		name.nextProviderMBean = mbeans;
		name.prevProviderMBean = null;
		if (mbeans != null) {
			mbeans.prevProviderMBean = name;
		}
		mbeans = name;
		mbeanCount++;
	}
	
	/**
	 * Remove association of mbean with the provider
	 * 
	 * @param name Name of the MBean
	 */
	public synchronized void unregister(LazyMBeanInfo name) {
		boolean dec = false;
		if (name.nextProviderMBean != null) {
			name.nextProviderMBean.prevProviderMBean = name.prevProviderMBean;
			dec = true;
		}
		if (name.prevProviderMBean != null) {
			name.prevProviderMBean.nextProviderMBean = name.nextProviderMBean;
			dec = true;			
		}
		if (mbeans == name) {
			mbeans = name.nextProviderMBean;
			dec = true;
		}
		if (dec && (mbeanCount > 0)) {
			mbeanCount--;
		}
	}
	
	/**
	 * Removes the association of all means associated with the provider
	 * 
	 * @return List with all the mbeans that were associated with the provider 
	 */
	public synchronized LazyMBeanInfo unregisterAll() {
		LazyMBeanInfo result = mbeans;
		mbeans = null;
		mbeanCount = 0;
		return result;
	}

	public int getMBeanCount() {
		return mbeanCount;
	}
	
	/**
	 * Add shared lock on the provider
	 * 
	 * @return	If successful or not
	 */
	public synchronized boolean addSharedLock() {
		if (active && (!waitForExclusive) && (!hasExclusive)) {
			lockCount++;
			return true;			
		} else {
			return false;
		}
	}

	/**
	 * Add exclusive lock on the provider
	 */
	public synchronized void addExclusiveLock() {
		waitForExclusive = true;
		while (lockCount > 0) {
			try {
				wait();
			} catch(InterruptedException iException) {
				LOCATION.traceThrowableT(Severity.ERROR, "A thread waiting for exclusive lock was interrupted", iException);
				throw new RuntimeException(iException);
			}
		}
		hasExclusive = true;
		waitForExclusive = false;		
	}

	/**
	 * Release exclusive lock 
	 */
	public synchronized void releaseExclusiveLock() {
		hasExclusive = false;
		notify();
	}
	
	/**
	 * Release shared lock
	 */
	public synchronized void releaseSharedLock() {
		lockCount--;
		if (lockCount == 0) {
			notify();
		}
	}
	
	public String getProviderName() {
		return providerName;
	}
	
	public void setProviderName(String name) {
		providerName = name;
	}
	
}

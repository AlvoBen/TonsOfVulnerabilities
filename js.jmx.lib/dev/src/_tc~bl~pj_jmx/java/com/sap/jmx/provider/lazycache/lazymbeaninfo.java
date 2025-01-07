/*
 * Created on Jun 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.jmx.provider.lazycache;

import javax.management.ObjectName;

import com.sap.jmx.provider.registry.MBeanProviderInfo;
import com.sap.jmx.provider.registry.ProviderConnectionInfo;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Jasen Minov
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author i024127
 *
 */
/**
 * @author i024127
 *
 */
public class LazyMBeanInfo {

	//----static part----
	  private static final Location LOCATION = Location.getLocation(LazyMBeanInfo.class);	
	
	//----instance part----
	
	//----constructor----
	
	/**
	 * 	The object name of the mbean
	 */
	private ObjectName name;
	
	/**
	 *  This flag indicates a exclusive lock that is set to the mbean
	 */
	private boolean hasExclusive;
	
	/**
	 *  This flag indicates the count of shared locks set to the mbean
	 */	
	private int lockCount;

	/**
	 *  This flag indicates the number of requests that wait for exclusive lock
	 */	
	private int waitForExclusive;

	/**
	 * Next LazyMBeanInfo within the queue
	 */
	LazyMBeanInfo next;
	
	/**
	 * Previous LazyMBeanInfo within the queue
	 */
	LazyMBeanInfo prev;
	
	/**
	 * The timestamp of the MBean within the LRU queue
	 */	
	private long objectTimeStamp;
	
	/**
	 * Is registered into the MBean server
	 */		
	private boolean isRegistered;
	
	private boolean excludeGC;
	
	/**
	 * Next LazyMBeanInfo within the provider queue
	 */
	public LazyMBeanInfo nextProviderMBean;
	
	/**
	 * Previous LazyMBeanInfo within the provider queue
	 */
	public LazyMBeanInfo prevProviderMBean;

	/**
	 * Next LazyMBeanInfo within the mbean loader queue
	 */	
	private LazyMBeanInfo nextToLoad;
	
	private Object mbean;
	
	private MBeanProviderInfo provider;
	
	public LazyMBeanInfo(ObjectName name) {
		this.name = name;
		
		hasExclusive = false;
		lockCount = 0;
		
		waitForExclusive = 0;
		
		next = null;
		prev = null;
		
		objectTimeStamp = -1;
		isRegistered = false;
		excludeGC = false;
		
		nextProviderMBean = null;
		prevProviderMBean = null;
		
		mbean = null;
		
		nextToLoad = null;
		
		provider = null;
	}

	//try to add exclusive lock (no wait); result is returned
	public synchronized boolean addExclusiveLock() {
		if (hasExclusive || (lockCount > 0)) {
			return false;
		} else {
			hasExclusive = true;
			return true;
		}
	}
	
	//try to force exclusive lock (no wait); result is returned
	public synchronized boolean forceExclusiveLock() {
		if (hasExclusive) {
			return false;
		} else {
			hasExclusive = true;
			return true;
		}
	}	
	
	//waits for exclusive lock over the mbean
	public synchronized void waitForExclusiveLock() {
		while (hasExclusive || (lockCount > 0)) {
			try {
				waitForExclusive++;
				wait();		
			} catch(InterruptedException iException) {
				LOCATION.traceThrowableT(Severity.ERROR, "thread that was waiting for exclusive lock was interrupted", iException);
			} finally {
				waitForExclusive--;
			}
		}
	}
	
	//waits for forced lock
	public synchronized void waitForForcedExclusiveLock() {
		while (hasExclusive) {
			try {
				waitForExclusive++;
				wait();		
			} catch(InterruptedException iException) {
				LOCATION.traceThrowableT(Severity.ERROR, "thread that was waiting for forced exclusive lock was interrupted", iException);
			} finally {
				waitForExclusive--;
			}
		}
	}
	

	//adds shared lock is possible (if there is no exclusive lock and mo waiting thread for exclusive lock
	public synchronized boolean addLock() {
		if (hasExclusive || (waitForExclusive > 0)) {
			return false;
		} else {
			lockCount++;
			return true;
		}
	}	

	//returns the timestamp of the object name
	public long getObjectTimeStamp() {
		return objectTimeStamp;
	}
	
	//set the timestamp of the object name
	public void setObjectTimeStamp(long timeStamp) {
		objectTimeStamp = timeStamp;
		if ((provider != null) && (provider.getType() == MBeanProviderInfo.CONNECTION_PROVIDER_TYPE)) {
			((ProviderConnectionInfo)provider).setAccessTimestamp(timeStamp);
		}
	}
	
	//waits till the exclusive lock is removed 
	public synchronized void waitForLock() {
		while (hasExclusive) {
			try {
				wait();		
			} catch(InterruptedException iException) {
				LOCATION.traceThrowableT(Severity.ERROR, "thread that was waiting removing the exclusive lock was interrupted", iException);			}
		}
	}
	
	//this flag keeps the info is the mbean registered into the mbean server
	public boolean isRegistered() {
		return isRegistered;
	}

	//set registered flag
	public void setRegistered(boolean isRegistered, Object mbean) {
		this.isRegistered = isRegistered;
		this.mbean = mbean;
	}
	
	//reduce the exclusive lock to normal one	
	public synchronized void reduceExclusiveLock() {
		hasExclusive = false;
		lockCount++;
		notifyAll();	
	}

	//remove the exclusive lock	
	public synchronized void removeExclusiveLock() {
		hasExclusive = false;
		notifyAll();
	}
	
	//remove shared lock
	public synchronized int removeLock() {
		lockCount--;
		if (waitForExclusive > 0) {
			notifyAll();
		}
		return lockCount;
	}
	
	public int getLockCount() {
		return lockCount;
	}
	
	public void setExcludeGC(boolean excludeFlag) {
		excludeGC = excludeFlag;
	}
	
	public boolean getExcludeGC() {
		return excludeGC;
	}

	public ObjectName getObjectName() {
		return name;
	}

	public void setNextToLoad(LazyMBeanInfo info) {
		nextToLoad = info;
	}
	
	public LazyMBeanInfo getNextToLoad() {
		return nextToLoad;
	}
	
	public Object getMBean() {
		return mbean;
	}
	
	public void setProvider(MBeanProviderInfo provider) {
		this.provider = provider;
	}
	
	public MBeanProviderInfo getProvider() {
		return provider;
	}
	
	public String toString() {
		String result =  " IsRegistered: " + isRegistered + 
					 " HasExclusive: " + hasExclusive +
					 " WaitForExclusive: " + waitForExclusive +
					 " LockCount: " + lockCount + 
					 " ObjectTimeStamp: " + objectTimeStamp + 
					 " ExcludeGC: " + excludeGC; 
		return result;
	}
}
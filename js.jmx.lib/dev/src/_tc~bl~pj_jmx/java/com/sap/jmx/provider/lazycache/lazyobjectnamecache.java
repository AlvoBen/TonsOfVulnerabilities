/*
 * Created on Jun 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.jmx.provider.lazycache;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;

import javax.management.ObjectName;

import com.sap.jmx.provider.registry.MBeanProviderInfo;

/**
 * @author Jasen Minov
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LazyObjectNameCache {

	//Hashtable ObjectName --> LazyMBeanInfo
	private Hashtable data;
	
	//LRU queue of LazyMBeanInfo
	private LazyMBeanQueue queue;
	
	//----constructor---- 
	public LazyObjectNameCache() {
		data = new Hashtable();
		queue = new LazyMBeanQueue();
	}
	
	//----public methods----
	public void addExclusiveLock(ObjectName name) {
		LazyMBeanInfo info = null;
		boolean succeeded = false;
		while (!succeeded) {
			synchronized (this) {
				info = (LazyMBeanInfo) data.get(name);
				if (info == null) {
					info = new LazyMBeanInfo(name);
					data.put(name, info);
				}
				succeeded = info.addExclusiveLock();				
			}

			if (!succeeded) {
				info.waitForExclusiveLock();
			} 
		}
	}

	public boolean forceExclusiveLock(ObjectName name) {
		LazyMBeanInfo info = null;
		boolean succeeded = false;
		synchronized (this) {
			info = (LazyMBeanInfo) data.get(name);
			if (info == null) {
				info = new LazyMBeanInfo(name);
				data.put(name, info);
			}
			succeeded = info.forceExclusiveLock();				
		}
		if (!succeeded) {
			info.waitForForcedExclusiveLock();
			return false;
		} 
		return true;
	}
	
	public boolean addSharedLock(ObjectName name) {
		LazyMBeanInfo info = null;
		boolean succeeded = false;
		while (!succeeded) {
			synchronized (this) {
				info = (LazyMBeanInfo) data.get(name);
				if ((info == null) || (!info.isRegistered())) {
					return false;
				}
				succeeded = info.addLock();
			}
			if (!succeeded) {
				info.waitForLock();
			} 
		}
		queue.moveAhead(info);
		return true;	
	}
	
	//check is an MBean is registered; should be used only when the thread is set already exclusive lock for the mbean
	public boolean isRegistered(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		return info.isRegistered();
	}

	//reduce the lock from exclusive to shared
	public void reduceExclusiveLock(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		info.reduceExclusiveLock();
		queue.moveAhead(info);
	}

	//register the mbean
	public LazyMBeanInfo registerMBean(ObjectName name, Object mbean, boolean excludeFromLazyGC) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		info.setRegistered(true, mbean);
		info.setExcludeGC(excludeFromLazyGC);
		return info;
	}
	
	//remove exclusive lock
	public void removeExclusiveLock(ObjectName name) {
		LazyMBeanInfo info = null;
		synchronized (this) {
			info = (LazyMBeanInfo) data.get(name);
			if (!info.isRegistered()) {
				if (info.getLockCount() == 0) {
					data.remove(name);
				}
				MBeanProviderInfo provider = info.getProvider();
				if (provider != null) {
					provider.unregister(info);
				}
			}
		}
		info.removeExclusiveLock();
	}

	public boolean unregisterMBean(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		if (info != null) {
			boolean result = info.isRegistered();
			info.setRegistered(false, null);
			return result;	
		} else {
			return false;
		}
	}

	public void removeSharedLock(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		if (!info.isRegistered()) { //forced exclusive lock was set and MBean was invalidated
			synchronized (this) { //we need synch section here
				info = (LazyMBeanInfo) data.get(name);
				if ((info.removeLock() == 0) && (!info.isRegistered())) {
					data.remove(name);					
				}
			}			
		} else {
			info.removeLock();
		}
	}
	
	public int getSize() {
		return data.size();
	}
	
	public String dump() {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(result);
		out.println("Data size: " + data.size());
		Iterator i = data.keySet().iterator();
		while (i.hasNext()) {
			ObjectName name = (ObjectName) i.next();
			out.println();
			out.print(name);
			out.println(" " + data.get(name));
		}
		out.println();
		out.flush();
		out.close();
		return result.toString();
	}
	
	public LazyMBeanInfo getMBeanInfo(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		if (info != null) {
			return info;		
		} else {
			return null;
		}
	}	
	
	public LazyMBeanInfo getLast() {
		return queue.getLast();
	}
	//----private methods----
	
/*
	
	

	
	//return true if registration is successfull; false - it is already registered
	
	
	public void setMainFlag(ObjectName name, boolean flag) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		info.setMainFlag(flag);
	}
	
	public boolean getMainFlag(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		return info.getMainFlag();
	}
	
	
	public synchronized boolean addExclusiveLockIfNotRegistered(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		if (info != null) {
			return false;
		} else {
			info = new LazyMBeanInfo(name);
			info.addExclusiveLock();			
			data.put(name, info);
			return true;
		}
	}
	
	
	
	public LazyMBeanInfo getLazyMBeanInfo(ObjectName name) {
		return (LazyMBeanInfo) data.get(name);
	}
	
	public boolean isFull() {
		return data.size() > MAX_CACHE_SIZE;
	}
	
	public ObjectName lockLastForRemove() {
		LazyMBeanInfo info = null;
		while (true) {
			boolean succeeded = false;
			info = LazyMBeanInfo.getLast();
			if (info != null) {
				succeeded = info.addExclusiveLock();
			} else {
				return null;
			}
			if (!succeeded) {
				info.waitForExclusiveLock();
			} else {
				if (LazyMBeanInfo.getLast() == info) { //to be sure that is not deleted meanwhile
					return info.getObjectName();					
				} else {
					info.removeExclusiveLock();
				}
			}
		}
	}
	
	public Hashtable getData() {
		return data;
	}
	
	public LazyMBeanInfo getMBeanInfo(ObjectName name) {
		LazyMBeanInfo info = (LazyMBeanInfo) data.get(name);
		if (info != null) {
			return info;		
		} else {
			return null;
		}
	}*/
}
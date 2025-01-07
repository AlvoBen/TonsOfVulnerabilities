package com.sap.jmx.provider.lazycache;

public class LazyMBeanQueue {

	private LazyMBeanInfo first;
	private LazyMBeanInfo last;
	
	public LazyMBeanQueue() {
		first = null;
		last = null;
	}
	
	//moves an mbean in the first position into the queue
	public synchronized void moveAhead(LazyMBeanInfo info) {
		if (first == info) {
			return;
		}
		
		if (info.prev != null) {
			info.prev.next = info.next; 	
		}
		if (info.next != null) {
			info.next.prev = info.prev;
		}
		
		if ((last == info) && (info.prev != null)) {
			last = info.prev;
		}
		
		if (first != null) {
			first.prev = info;
		}
		info.next = first;
		info.prev = null;
		first = info;
		
		info.setObjectTimeStamp(System.currentTimeMillis());
	}
	
	public synchronized LazyMBeanInfo getLast() {
		return last;
	}
}

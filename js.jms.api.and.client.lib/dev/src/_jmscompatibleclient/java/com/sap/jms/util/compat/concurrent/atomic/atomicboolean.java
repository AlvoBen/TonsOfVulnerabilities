package com.sap.jms.util.compat.concurrent.atomic;

public class AtomicBoolean {
	
	private boolean value = false;
	private Object monitor = new Object();
	
	public AtomicBoolean() {		
		this(false);
	}
	

	public AtomicBoolean(boolean initialValue) {
		set(initialValue);
	}

	public boolean compareAndSet(boolean expect, boolean update) {
		boolean result = false;
		synchronized (monitor) {
			if (expect == value) {
				value = update;
				result = true;
			}
		}
		
		return result;
	}

	public boolean get() {
		synchronized (monitor) {
			return value;
		}
	}
 
	public boolean getAndSet(boolean newValue) {
		boolean result = false; 
		synchronized (monitor) {
			result = value;
			value = newValue;
		}
		return result;		
	}

	public void set(boolean newValue) {
		synchronized (monitor) {
			value = newValue;
		}
	}
 
	public String toString() {
		return "" + value;
	}
 
	public boolean weakCompareAndSet(boolean expect, boolean update) { 
		return compareAndSet(expect, update);
	}	


}

package com.sap.jms.util.compat.concurrent.atomic;

public class AtomicReference  {
	
	private Object value = null; 
	private Object monitor = new Object();
 
	public AtomicReference() {
		this(null);
	}
 
	public AtomicReference(Object initialValue) {
		set(initialValue);		
	}
   
	public boolean compareAndSet(Object expect, Object update) {
		boolean result = false;
		synchronized (monitor) {
			if (expect == value) {
				value = update;
				result = true;
			}
		}
		return result;		
	}
	 
	public Object get() {
		synchronized (monitor) {
			return value;
		}
	}
	 
	public Object getAndSet(Object newValue) {
		synchronized (monitor) {
			Object result = value;
			value = newValue;
			return result;
		}
	}
	 
	public void set(Object newValue) {
		synchronized (monitor) {
			value = newValue;
		}
	}
	 
	public String toString() {
		return "" + value;
	}
	 
	public boolean weakCompareAndSet(Object expect, Object update) {
		return compareAndSet(expect, update);
	}
}

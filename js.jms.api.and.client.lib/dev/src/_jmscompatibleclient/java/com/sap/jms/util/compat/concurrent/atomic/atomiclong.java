package com.sap.jms.util.compat.concurrent.atomic;

public class AtomicLong {
	
	private long value;
	private Object monitor = new Object();
	
	public AtomicLong() {
		this(0);
	}		

	public AtomicLong(long initialValue) {
		set(initialValue);
	}
	
	public long addAndGet(long delta) {
		long result;
		synchronized (monitor) {
			value += delta;
			result = value;
		}
		
		return result;
	}
 
	public boolean compareAndSet(long expect, long update) {
		boolean result = false;
		synchronized (monitor) {
			if (expect == value) {
				value = update;
				result = true;
			}
		} 

		return result;
	}
 
	public long decrementAndGet() {
		synchronized (monitor) {
			return (--value);
		}
	}
 
	public double doubleValue() {
		synchronized (monitor) {
			return (double)value;
		}
	}
 
	public float floatValue() {
		synchronized (monitor) {
			return (float)value;
		}
	}
 
	public long get() {
		synchronized (monitor) {
			return value;
		}
	}
 
	public long getAndAdd(long delta) {
		synchronized (monitor) {
			long result = value;
			value += delta;
			return result;		
		}
	}
 
	public long getAndDecrement() {
		synchronized (monitor) {
			long result = value;
			value--;
			return result;
		}
	}
 
	public long getAndIncrement() {
		synchronized (monitor) {
			long result = value;
			value++;
			return result;
		}
	}
 
	public long getAndSet(long newValue) {
		synchronized (monitor) {
			long result = value;
			value = newValue;
			return result;
		}
	}
 
	public long incrementAndGet() {
		synchronized (monitor) {
		return (++value);
		}
	}
 
	public int intValue() {
		synchronized (monitor) {
			return (int) value;
		}
	}
 
	public long longValue() {
		synchronized (monitor) {
			return value;
		}
	}
 
	public void set(long newValue) {
		synchronized (monitor) {
			value = newValue;
		}
	}
 
	public String toString() {
		return "" + value;
	}
 
	public boolean weakCompareAndSet(long expect, long update) {
		return compareAndSet(expect, update);		
	}
}

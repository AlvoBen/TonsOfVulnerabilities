package com.sap.engine.services.library_container.deploy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Instances of this class wrap instances of <code>java.util.Map</code> to provide an optimal synchronized access to the elements stored in them.
 * A minimal set of operations over the stored objects is defined according to the needs of the Applications Library Container.
 * @author I039168
 * First created on 20.01.2009
 * @param <Key> type for the keys stored in the internal map
 * @param <Value> type for the values stored in the internal map
 */
class LCConcurrentMapWrapper<Key, Value> {

	private Map<Key, Value> holder;
	private final static ReadWriteLock rwLock = new ReentrantReadWriteLock();

	/**
	 * Creates a wrapper around an empty <code>java.util.HashMap</code>.
	 * @see HashMap#HashMap()
	 */
	LCConcurrentMapWrapper() {
		this.holder = new HashMap<Key, Value>();
	}
	
	/**
	 * Creates a wrapper around an empty <code>java.util.HashMap</code> with the specified initial capacity.
	 * @param initialCapacity initial capacity of storage
	 * @see HashMap#HashMap(int)
	 */
	LCConcurrentMapWrapper (int initialCapacity) {
		this.holder = new HashMap<Key, Value>(initialCapacity);
	}
	
	/**
	 * Synchronized version of {@link java.util.Map#get(Object)}
	 * @see Map#get(Object)
	 */
	public Value get (Key key) {
		rwLock.readLock().lock();
		try {
			return holder.get(key);
		} finally {
			rwLock.readLock().unlock();
		}
	}
	
	/**
	 * Synchronized version of {@link java.util.Map#remove(Object)}
	 * @see Map#remove(Object)
	 */
	public Value remove (Key key) {
		rwLock.writeLock().lock();
		try {
			return holder.remove(key);
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * Synchronized version of {@link java.util.Map#put(Object, Object)}
	 * @see Map#put(Object, Object)
	 */
	public Value put (Key key, Value value) {
		rwLock.writeLock().lock();
		try {
			Value result = holder.get(key); 
			holder.put(key, value);
			return result;
		} finally {
			rwLock.writeLock().unlock();
		}
	}
}

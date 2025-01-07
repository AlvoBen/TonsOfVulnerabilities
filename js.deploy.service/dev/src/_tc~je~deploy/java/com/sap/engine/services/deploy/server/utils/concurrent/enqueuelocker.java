package com.sap.engine.services.deploy.server.utils.concurrent;

/**
 * This interface defines methods used to obtain enqueue locks.
 * @author Emil Dinchev
 */
public interface EnqueueLocker {
	/**
	 * Every enqueue lock has its key and type.
	 * @see com.sap.engine.frame.core.locking.LockingConstants
	 */
	public class EnqueueLock {
		private final String key;
		private final char type;
		
		/**
		 * @param key the name of the enqueue lock.
		 * @param type the type of the lock.
		 * @see com.sap.engine.frame.core.locking.LockingConstants
		 */
		public EnqueueLock(final String key, final char type) {
			this.key = key;
			this.type = type;
		}
		
		public String getKey() {
			return key;
		}
		
		public char getType() {
			return type;
		}
	}

	/**
	 * Acquires enqueue lock.
	 * @param lock enqueue lock to be acquired.
	 * @return <tt>true</tt> if succeeded.
	 */
	boolean lock(EnqueueLock lock);
	
	
	/**
	 * Releases enqueue lock.
	 * @param lock enqueue lock to be released.
	 */
	void unlock(EnqueueLock lock);
}
package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This smart doggy retrieves the lock owners from their holes.
 * @author Emil Dinchev
 * @param <N> the node type.
 */
final class LockOwnerRetriever<N> {
	private final ThreadSystem threadSystem;
	private final LockingThreadLocal<N> threadLocal;
	private final boolean failFastOnLockAttempt;

	static final Location location = 
		Location.getLocation(LockOwnerRetriever.class);

	static class LockingThreadLocal<N>
		extends InheritableThreadLocal<LockOwner<N>> {
	
		@Override
		protected LockOwner<N> initialValue() {
			LockOwner<N> lockOwner = new LockOwner<N>();
			lockOwner.setThreadLocal(this);
			return lockOwner;
		}

		@Override
		protected LockOwner<N> childValue(LockOwner<N> parent) {
			assert parent != null;
			final LockOwner<N> child = initialValue();
			child.inherit(parent);
	        return child;
	    }
	}
	
	protected LockOwnerRetriever(final ThreadSystem threadSystem,
		final boolean failFastOnLockAttempt) {
		this.threadSystem = threadSystem;
		this.failFastOnLockAttempt = failFastOnLockAttempt;
		threadLocal = new LockingThreadLocal<N>();
	}

	@SuppressWarnings("unchecked")
	public LockOwner<N> retrieve() {
		LockOwner<N> lockOwner;
		final ThreadContext threadContext = threadSystem.getThreadContext();
		if(threadContext == null) {
			// This is a system thread.
			lockOwner = threadLocal.get();
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"{0} is retrieved by system thread.", lockOwner);
			}
		} else {
			final String id = LockOwner.class.getCanonicalName();
			lockOwner = (LockOwner<N>)threadContext.getContextObject(id);
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"{0} is retrieved by application thread.", lockOwner);
			}

		}
		// lockOwner cannot be null here.
		lockOwner.init(failFastOnLockAttempt);
		return lockOwner;
	}

	public void activate() {
		threadSystem.registerContextObject(
			LockOwner.class.getCanonicalName(), 
			new LockOwner<N>());
	}
	
	public void deactivate() {
		threadSystem.unregisterContextObject(
			LockOwner.class.getCanonicalName());
	}
}
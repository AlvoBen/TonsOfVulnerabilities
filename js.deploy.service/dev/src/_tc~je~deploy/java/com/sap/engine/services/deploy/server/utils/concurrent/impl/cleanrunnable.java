package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import java.util.concurrent.CountDownLatch;

import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is used to execute synchronously runnable in clean application 
 * thread. The provider of the original runnable interface is responsible to 
 * collect exceptions and warnings during the execution.
 * @param <N> The type of nodes.
 * 
 *  * @see com.sap.engine.services.deploy.server
 *     .DeployCommunicatorImpl#execute(Runnable)
 * 
 * @author Emil Dinchev
 */
public final class CleanRunnable<N> implements Runnable {
	private static final Location location = 
		Location.getLocation(CleanRunnable.class);
	private final CountDownLatch latch;
	private final Runnable delegate;
	private final LockOwnerRetriever<N> lockOwnerRetriever;
	private final LockOwner<N> parent;

	/**
	 * Create new clean runnable.
	 * @param delegate the runnable to be executed.
	 * @param latch the count down latch used for synchronization.
	 * @param lockManager the lock manager.
	 */
	public CleanRunnable(final Runnable delegate, 
		final CountDownLatch latch, final LockManager<N> lockManager) {
		this.delegate = delegate;
		this.latch = latch;
		lockOwnerRetriever = ((LockManagerImpl<N>)lockManager)
			.getLockOwnerRetriever();
		parent = lockOwnerRetriever.retrieve();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		final LockOwner<N> lockOwner = lockOwnerRetriever.retrieve();
		lockOwner.inherit(parent);
		lockOwner.setFailFastOnLockAttempt(false);
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null, 
				"A new application thread was created : " + lockOwner);
		}
		try {
			delegate.run();
		} finally {
			latch.countDown();
		}
	}
}

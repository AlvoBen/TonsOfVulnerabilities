package com.sap.engine.services.dc.api.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.event.DeploymentEvent;
import com.sap.engine.services.dc.api.event.DeploymentListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.impl.ClientFactoryImpl;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.Executor;

/**
 * This class knows how to dispatch deployment events to its registered
 * listeners. The dispatching can be synchronous or asynchronous.
 * 
 * @author I040924
 * 
 */
public class DeployEventDispatcher {

	private String name;
	private final Object allListenersMutex = new Object();

	private Collection syncListeners = Collections
			.unmodifiableCollection(new ArrayList());
	private Collection asyncListeners = Collections
			.unmodifiableCollection(new ArrayList());

	protected DALog daLog;

	private DeployEventDispatcher(String name, DALog logger) {
		this.name = name;
		this.daLog = logger;
	}

	private Executor executor = null;

	/**
	 * Create an instance of the event dispatcher that will log messages to the
	 * specified logger
	 * 
	 * @param logger
	 *            the logger
	 * @return an instance
	 */
	public static final DeployEventDispatcher createInstance(String name,
			DALog logger) {

		if (name == null) {
			name = "Not specified";
		}
		if (logger == null) {
			throw new IllegalArgumentException("The logger cannot be null.");
		}
		return new DeployEventDispatcher(name, logger);

	}

	/**
	 * Add a deployment listener with the specified notification mode
	 * 
	 * @param listener
	 *            the listener to be added. If the listener is alredy registered
	 *            it will be removed first.
	 * @param eventMode
	 *            the notification mode
	 */
	public void addDeploymentListener(DeploymentListener listener,
			final EventMode eventMode) {

		if (listener == null) {
			throw new IllegalArgumentException("The listener cannot be null.");
		}
		if (eventMode == null) {
			throw new IllegalArgumentException("The event mode cannot be null.");
		}
		// args check
		if (!(EventMode.SYNCHRONOUS.equals(eventMode) || EventMode.ASYNCHRONOUS
				.equals(eventMode))) {
			throw new IllegalArgumentException("Unrecognized event mode: "
					+ eventMode);
		}

		synchronized (this.allListenersMutex) {

			if (this.asyncListeners.contains(listener)) {

				if (EventMode.ASYNCHRONOUS.equals(eventMode)) {
					if (daLog.isDebugTraceable()) {
						this.daLog
								.traceDebug(
										"The listener [{0}] is already added as asynchronous",
										new Object[] { listener });
					}
					return;
				} else { // assuming mode is SYNCHRONOUS

					Collection tempAsync = new ArrayList(this.asyncListeners);
					if (tempAsync.contains(listener)) {
						if (daLog.isDebugTraceable()) {
							this.daLog
									.traceDebug(
											"The listener [{0}] is already added as asynchronous and it will be removed.",
											new Object[] { listener });
						}
						tempAsync.remove(listener);
					}

					Collection tempSync = new ArrayList(this.syncListeners);

					tempSync.add(listener);

					this.asyncListeners = Collections
							.unmodifiableCollection(tempAsync);
					this.syncListeners = Collections
							.unmodifiableCollection(tempSync);

					return;

				}
			}

			if (this.syncListeners.contains(listener)) {
				if (EventMode.SYNCHRONOUS.equals(eventMode)) {
					if (daLog.isDebugTraceable()) {
						this.daLog
								.traceDebug(
										"The listener [{0}] is already added as synchronous",
										new Object[] { listener });
					}
					return;
				} else { // assuming mode is ASYNCHRONOUS

					Collection tempSync = new ArrayList(this.syncListeners);
					if (tempSync.contains(listener)) {
						if (daLog.isDebugTraceable()) {
							this.daLog
									.traceDebug(
											"The listener [{0}] is already added as synchronous and it will be removed.",
											new Object[] { listener });
						}
						tempSync.remove(listener);
					}

					Collection tempAsync = new ArrayList(this.asyncListeners);
					tempAsync.add(listener);

					this.syncListeners = Collections
							.unmodifiableCollection(tempSync);
					this.asyncListeners = Collections
							.unmodifiableCollection(tempAsync);

					return;
				}

			}

			// got so far which means that the listener is not added yet
			if (EventMode.SYNCHRONOUS.equals(eventMode)) {

				// make a copy of the current collection in order not to affect
				// current iterations if any
				Collection temp = new ArrayList(this.syncListeners);
				temp.add(listener);
				this.syncListeners = Collections.unmodifiableCollection(temp);
			}

			if (EventMode.ASYNCHRONOUS.equals(eventMode)) {

				// make a copy of the current collection in order not to affect
				// current iterations if any
				Collection temp = new ArrayList(this.asyncListeners);
				temp.add(listener);
				this.asyncListeners = Collections.unmodifiableCollection(temp);
			}

		}

	}

	/**
	 * Remove the specified listener
	 * 
	 * @param listener
	 */
	public void removeListener(DeploymentListener listener) {

		if (listener == null) {
			throw new IllegalArgumentException("The listener cannot be null.");
		}
		synchronized (this.allListenersMutex) {

			if (this.syncListeners.contains(listener)) {

				Collection temp = new ArrayList(this.syncListeners);
				temp.remove(listener);
				this.syncListeners = Collections.unmodifiableCollection(temp);
				return;
			}

			if (this.asyncListeners.contains(listener)) {

				Collection temp = new ArrayList(this.asyncListeners);
				temp.remove(listener);
				this.asyncListeners = Collections.unmodifiableCollection(temp);
				return;
			}

			if (daLog.isDebugTraceable()) {
				this.daLog
						.traceDebug(
								"Trying to remove a listener [{0}] that is not currently added",
								new Object[] { listener });
			}
		}

	}

	/**
	 * Remove all the listeners
	 * 
	 * @param listener
	 */
	public void removeAllListeners() {

		synchronized (this.allListenersMutex) {

			this.syncListeners = new ArrayList(0);
			this.asyncListeners = new ArrayList(0);

		}

	}

	/**
	 * Call this method to notify all the listeners that are registered with
	 * this dispatcher about the deployment event
	 * 
	 * @param evt
	 */
	public void dispatchDeploymentEvent(DeploymentEvent evt) {

		if (evt == null) {
			throw new IllegalArgumentException("The event cannot be null.");
		}

		// notify sync listeners first so control can be returned to DC as soon
		// as possible
		if (daLog.isDebugTraceable()) {
			this.daLog
					.traceDebug("Starting to notify the synchronous listeners.");
		}
		notifySyncListeners(evt);
		if (daLog.isDebugTraceable()) {
			this.daLog
					.traceDebug("Finished notifying the synchronous listeners.");
			this.daLog
					.traceDebug("Starting to schedule the notification of the asynchrounous listeners.");
		}
		notifyAsyncListeners(evt);
		if (daLog.isDebugTraceable()) {
			this.daLog
					.traceDebug("Finished scheduling the notification of the asynchronous listeners.");
		}
	}

	private void notifyAsyncListeners(final DeploymentEvent evt) {

		Iterator iterator = this.asyncListeners.iterator();

		while (iterator.hasNext()) {
			final DeploymentListener listener = (DeploymentListener) iterator
					.next();

			Runnable r = new Runnable() {
				public void run() {

					try {
						if (daLog.isDebugTraceable()) {
							DeployEventDispatcher.this.daLog
									.traceDebug(
											"Starting to notify asynchronous listener [{0}]",
											new Object[] { listener });
						}
						listener.deploymentPerformed(evt);
						if (daLog.isDebugTraceable()) {
							DeployEventDispatcher.this.daLog
									.traceDebug(
											"Finished notifying asynchronous listener [{0}]",
											new Object[] { listener });
						}

					} catch (OutOfMemoryError e) { // OOM, ThreadDeath and
						// Internal error are not
						// consumed
						throw e;
					} catch (ThreadDeath e) {
						throw e;

					} catch (InternalError e) {
						throw e;

					} catch (Throwable t) { // all other throwables are just
						// logged
						DeployEventDispatcher.this.daLog
								.logThrowable(
										"ASJ.dpl_api.001085",
										"Unexpected exception during the notification of an asynchronous listener: [{0}]",
										t, new Object[] { listener });
					}

				}
			};

			executeAsync(r);

		}

	}

	private void notifySyncListeners(DeploymentEvent evt) {

		Iterator iterator = this.syncListeners.iterator();

		while (iterator.hasNext()) {
			DeploymentListener listener = (DeploymentListener) iterator.next();
			try {
				if (daLog.isDebugTraceable()) {
					this.daLog.traceDebug(
							"Starting to notify synchronous listener [{0}]",
							new Object[] { listener });
				}
				listener.deploymentPerformed(evt);
				if (daLog.isDebugTraceable()) {
					this.daLog.traceDebug(
							"Finished notifying synchronous listener [{0}]",
							new Object[] { listener });
				}

			} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal
				// error are not consumed
				throw e;
			} catch (ThreadDeath e) {
				throw e;

			} catch (InternalError e) {
				throw e;

			} catch (Throwable t) { // all other throwables are just logged
				DeployEventDispatcher.this.daLog
						.logThrowable(
								"ASJ.dpl_api.001086",
								"Unexpected exception during the notification of a synchronous listener: [{0}]",
								t, new Object[] { listener });
			}
		}
	}

	private void executeAsync(Runnable task) {

		// appropriate async executor should be setup during the start of DC
		// connector frame so async
		// notification can take place within the engine using the engine thread
		// pool
		synchronized (this) {
			if (this.executor == null) {
				ClientFactoryImpl factory = (ClientFactoryImpl) ClientFactory
						.getInstance();
				this.executor = factory.getAsyncExecutor();
			}
		}

		executor.execute(task);
	}

	public int getListenersCount() {
		synchronized (this.allListenersMutex) {
			int count = this.asyncListeners.size() + this.syncListeners.size();
			return count;
		}
	}

	public boolean contains(DeploymentListener listener) {
		synchronized (this.allListenersMutex) {
			return this.syncListeners.contains(listener)
					|| this.asyncListeners.contains(listener);

		}
	}

	public boolean contains(DeploymentListener listener, EventMode eventMode) {
		synchronized (this.allListenersMutex) {
			if (EventMode.SYNCHRONOUS.equals(eventMode)) {
				return this.syncListeners.contains(listener);
			} else if (EventMode.ASYNCHRONOUS.equals(eventMode)) {
				return this.asyncListeners.contains(listener);
			} else {
				throw new IllegalArgumentException("Unrecognized event mode: "
						+ eventMode);
			}

		}
	}

	public String toString() {
		return this.name;
	}
}

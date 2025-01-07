package com.sap.engine.services.dc.event;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.sm.CallerInfoImportingSM;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description: This class tracks all the global
 * listeners which have to be triggered in case a concrete event has been
 * performed. For example, if a deployment has been done on some other node all
 * the deployment related global listeners have to be triggered.
 * 
 * The triggering mechanism should be done by the type
 * <code>com.sap.engine.services.dc.manage.messaging.MessageProcessor</code>. A
 * <code>DCEvent</code> object has to be spread over all the cluster instances
 * nodes and all the running Deploy Controller services have to process it. In
 * order to send a message with <code>DCEvent</code> object refer to the type
 * <code>com.sap.engine.services.dc.manage.messaging.MessageSender</code>.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class GlobalListenersList {
	
	private Location location = DCLog.getLocation(this.getClass());

	private static GlobalListenersList INSTANCE;

	private List<DeploymentListener> deployListeners;
	private List<UndeploymentListener> undeployListeners;
	private List<ClusterListener> clusterListeners;

	private Queue<DeploymentEvent> deployEventQueue = new LinkedList<DeploymentEvent>();
	private Queue<UndeploymentEvent> undeployEventQueue = new LinkedList<UndeploymentEvent>();

	// the access to the deployListeners and the deploy event queue is
	// synchronized
	private Object deployMutex = new Object();
	private Object undeployMutex = new Object();

	// The flushing of the queues should happen only once per queue
	private boolean scheduledFlushOfDeployQueue = false;
	private boolean scheduledFlushOfUndeployQueue = false;

	private GlobalListenersList() {

		this.deployListeners = Collections
				.unmodifiableList(new ArrayList<DeploymentListener>());
		this.undeployListeners = Collections
				.unmodifiableList(new ArrayList<UndeploymentListener>());
		this.clusterListeners = Collections
				.unmodifiableList(new ArrayList<ClusterListener>());

	}

	public synchronized static GlobalListenersList getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GlobalListenersList();
		}

		return INSTANCE;
	}

	public void addListener(ClusterListener listener) {

		synchronized (this.clusterListeners) {

			logAddListener(listener);
			if (!this.clusterListeners.contains(listener)) {
				List<ClusterListener> temp = new ArrayList<ClusterListener>(
						this.clusterListeners.size() + 1);
				temp.addAll(this.clusterListeners);
				temp.add(listener);
				this.clusterListeners = Collections.unmodifiableList(temp);
			} else {
				logListenerAlreadyAdded(listener);
			}

		}

	}

	public void addListener(final DeploymentListener listener) {

		synchronized (this.deployMutex) {

			// the first Remote listener added should trigger a flush of the
			// queued events if we are still deploying
			// flushing of the deploy event queue should happen only once and
			// only this remote listener should be notified

			logAddListener(listener);
			if (!this.deployListeners.contains(listener)) {
				List<DeploymentListener> temp = new ArrayList<DeploymentListener>(
						this.deployListeners.size() + 1);
				temp.addAll(this.deployListeners);
				temp.add(listener);
				this.deployListeners = Collections.unmodifiableList(temp);
			} else {
				logListenerAlreadyAdded(listener);
			}

			// when the first deployment
			boolean scheduleFlushOfTheQueue = !this.scheduledFlushOfDeployQueue
					&& DCManager.getInstance().getDCState().equals(
							DCState.DEPLOYING) && isRemoteListener(listener);

			if (scheduleFlushOfTheQueue) {
				if (location.bePath()) {
					tracePath(location, "Scheduling flush of the GlobalListenersList deploy queue");
				}

				ServiceConfigurer.getInstance().getExecutor().execute(
						new Runnable() {

							public void run() {
								flushDeployQueue(listener);
							}

						});
				this.scheduledFlushOfDeployQueue = true;
			}

		}

	}

	/**
	 * 
	 * This method is used in order to determine if the listener being added is
	 * remote or local. This is required because the queuing and subsequent
	 * delivery of the events is done for the first remote listener being added
	 * 
	 * @param listener
	 *            - not really used now
	 * @return
	 */
	private boolean isRemoteListener(DeployControllerListener listener) {

		// it would have been nice if the listeners added through the
		// dc_connector
		// weren't instances of Remote but it is not the case. The only safe way
		// to determine if the
		// listener is added through the dc_connector is to examine the call
		// stack for
		// com.sap.engine.services.dc.api ( if the listener is added remotely
		// this will not be
		// part of the call stack )
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : elements) {
			if (element.getClassName().contains(
					"com.sap.engine.services.dc.api")) {
				return false;
			}
		}
		return true;

	}

	public void addListener(final UndeploymentListener listener) {

		synchronized (this.undeployMutex) {

			// the first Remote listener added should trigger a flush of the
			// queued events if we are still deploying
			// flushing of the deploy event queue should happen only once and
			// only this remote listener should be notified

			logAddListener(listener);
			if (!this.undeployListeners.contains(listener)) {
				List<UndeploymentListener> temp = new ArrayList<UndeploymentListener>(
						this.undeployListeners.size() + 1);
				temp.addAll(this.undeployListeners);
				temp.add(listener);
				this.undeployListeners = Collections.unmodifiableList(temp);
			} else {
				logListenerAlreadyAdded(listener);
			}

			boolean scheduleFlushOfTheQueue = !this.scheduledFlushOfUndeployQueue
					&& DCManager.getInstance().getDCState().equals(
							DCState.UNDEPLOYING) && isRemoteListener(listener);

			if (scheduleFlushOfTheQueue) {

				if (location.bePath()) {
					tracePath(location, "Scheduling flush of the GlobalListenersList undeploy queue ...");
				}

				ServiceConfigurer.getInstance().getExecutor().execute(
						new Runnable() {

							public void run() {
								flushUndeployQueue(listener);
							}

						});
				this.scheduledFlushOfUndeployQueue = true;
			}

		}

	}

	public void removeListener(ClusterListener listener) {

		synchronized (this.clusterListeners) {

			logRemoveListener(listener);
			if (this.clusterListeners.contains(listener)) {
				List<ClusterListener> temp = new ArrayList<ClusterListener>(
						this.clusterListeners.size());
				temp.addAll(this.clusterListeners);
				temp.remove(listener);
				this.clusterListeners = Collections.unmodifiableList(temp);
			} else {
				logListenerNotAdded(listener);
			}
		}

	}

	public void removeListener(DeploymentListener listener) {

		synchronized (this.deployMutex) {

			logRemoveListener(listener);
			if (this.deployListeners.contains(listener)) {
				List<DeploymentListener> temp = new ArrayList<DeploymentListener>(
						this.deployListeners.size());
				temp.addAll(this.deployListeners);
				temp.remove(listener);
				this.deployListeners = Collections.unmodifiableList(temp);
			} else {
				logListenerNotAdded(listener);
			}

		}
	}

	public void removeListener(UndeploymentListener listener) {

		synchronized (this.undeployMutex) {

			logRemoveListener(listener);
			if (this.undeployListeners.contains(listener)) {
				List<UndeploymentListener> temp = new ArrayList<UndeploymentListener>(
						this.undeployListeners.size());
				temp.addAll(this.undeployListeners);
				temp.remove(listener);
				this.undeployListeners = Collections.unmodifiableList(temp);
			} else {
				logListenerNotAdded(listener);
			}

		}
	}

	/**
	 * 
	 * The queue should be flushed only to the first remote listener added
	 * 
	 * @param listener
	 *            The first remote listener added
	 */
	private void flushDeployQueue(DeploymentListener listener) {

		synchronized (this.deployMutex) {

			if (this.deployEventQueue.size() == 0) {
				return;
			}

			try {

				while (true) {

					DeploymentEvent event = this.deployEventQueue.poll();
					if (event == null) {
						break;
					}

					if (location.beDebug()) {
						traceDebug(
								location, 
								"Dispatching delayed event: [{0}] to listener [{1}]",
								new Object[] { event, listener });
					}

					fireGlobalEvent(event, listener);

				}

			} catch (NotificationException e) {
				DCLog.logErrorThrowable(location, e);
				removeListener(listener);
			}

			if (location.bePath()) {
				tracePath(location, "Finished flushing the deploy queue");
			}

		}

	}

	private void flushUndeployQueue(UndeploymentListener listener) {

		synchronized (this.undeployMutex) {

			if (this.undeployEventQueue.size() == 0) {
				return;
			}

			try {

				while (true) {

					UndeploymentEvent event = this.undeployEventQueue.poll();
					if (event == null) {
						break;
					}

					if (location.beDebug()) {
						traceDebug(
								location,
								"Dispatching delayed event: [{0}] to listener [{1}]",
								new Object[] { event, listener });
					}

					fireGlobalEvent(event, listener);

				}

			} catch (NotificationException e) {
				DCLog.logErrorThrowable(location, e);
				removeListener(listener);
			}

			if (location.bePath()) {
				tracePath(location, "Finished flushing the undeploy queue");
			}
		}

	}

	public void fireGlobalEvent(ClusterEvent event) {

		// get a reference to the current (immutable) list
		List<ClusterListener> listeners = this.clusterListeners;

		if (listeners.size() == 0) {
			return;
		}

		logAboutToDispatchEvent(event);

		List<ClusterListener> badOnes = null;

		for (ClusterListener listener : listeners) {

			try {
				listener.clusterRestartTriggered(event);

			} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal
				// error are not consumed
				throw e;
			} catch (ThreadDeath e) {
				throw e;

			} catch (InternalError e) {
				throw e;

			} catch (Throwable t) { // the listener is misbehaving. Schedule for
				// removal

				String listenerDescription;
				try {
					listenerDescription = listener.toString();
				} catch (Throwable th) {
					listenerDescription = "N/A";
				}

				DCLog.logErrorThrowable(location, null,
						"A problem occured while notifying the listener "
								+ listenerDescription + " for the event "
								+ event, t);
				if (badOnes == null) {
					badOnes = new ArrayList<ClusterListener>();
				}
				badOnes.add(listener);

			}

		}

		// remove the bad ones so that no attempt is made to notify them anymore
		// and they are garbage collected
		if (badOnes != null) {
			for (ClusterListener listener : badOnes) {
				removeListener(listener);

			}
		}
	}

	/**
	 * Notify all the registered global deployment listeners
	 * 
	 * @param event
	 */
	public void fireGlobalEvent(DeploymentEvent event) {

		List<DeploymentListener> listeners = null;
		DCState dcState = DCManager.getInstance().getDCState();
		synchronized (this.deployMutex) {

			/*
			 * The events should be queued if all the conditions below are met:
			 * 1. We are in a deployment process after a restart ( DCState is
			 * DEPLOYING ) 2. The flush of the deployment queue hasn't been
			 * scheduled yet ( it is scheduled when the first remote listener is
			 * added )
			 * 
			 * The first remote listener added while we are still deploying will
			 * cause the flush of the queue
			 */

			if (!this.scheduledFlushOfDeployQueue
					&& DCState.DEPLOYING.equals(dcState)) {

				// queue the events to cover the case when the remote listener
				// hasn't been reconnected yet
				// after the restart
				// NOTE: if there is another remote global deployment listener
				// added before DC API manages
				// to reconnect its global listeners it will prevent the
				// delivery of the queued events to the
				// DC API, which was the goal of this queuing implementation
				if (location.bePath()) {
					tracePath(location, 
							"Because the DCState is [{0}] and the flush of the event queue wasn't scheduled yet, the event will be enqueued.",
							new Object[] { dcState });
				}

				this.deployEventQueue.offer(event);
			}

			// get the current listenerList object ( if a listener is added or
			// removed the object will be overwritten )
			listeners = this.deployListeners;

			if (listeners.size() == 0) {

				return;

			}

			logAboutToDispatchEvent(event);

			// the event dispatching is done synchronously because it is assumed
			// that
			// under normal circumstances all the long running tasks during
			// notification
			// will be done in asynchronous listeners ( implemented in DC API )
			fireGlobalEvent(event, listeners);

		}

	}

	/**
	 * 
	 * Deliver the given event to the list of listeners and remove the listeners
	 * that failed during notification
	 * 
	 * @param event
	 * @param listeners
	 */
	private void fireGlobalEvent(DeploymentEvent event,
			List<DeploymentListener> listeners) {

		List<DeploymentListener> badOnes = null;

		for (DeploymentListener listener : listeners) {

			try {

				fireGlobalEvent(event, listener);

			} catch (NotificationException e) {
				if (location.beWarning()) {
					traceWarning(
							location,
							"ASJ.dpl_dc.001168",
							"A problem occurrend during the listener notification [{0}]",
							new Object[] { e.getMessage() });
				}
				if (DCLog.isDebugLoggable()) {
					DCLog.logDebugThrowable(location, e);
				}

				if (badOnes == null) {
					badOnes = new ArrayList<DeploymentListener>();
				}
				badOnes.add(listener);

			}

		}

		// remove the bad ones so that no attempt is made to notify them anymore
		// and they are garbage collected
		if (badOnes != null) {
			for (DeploymentListener listener : badOnes) {
				removeListener(listener);

			}
		}

	}

	/**
	 * 
	 * Deliver the given event to the list of listeners and remove the listeners
	 * that failed during notification
	 * 
	 * @param event
	 * @param listeners
	 */
	private void fireGlobalEvent(UndeploymentEvent event,
			List<UndeploymentListener> listeners) {

		List<UndeploymentListener> badOnes = null;

		for (UndeploymentListener listener : listeners) {

			try {

				fireGlobalEvent(event, listener);

			} catch (NotificationException e) {

				DCLog.logErrorThrowable(location, e);
				if (badOnes == null) {
					badOnes = new ArrayList<UndeploymentListener>();
				}
				badOnes.add(listener);

			}

		}

		// remove the bad ones so that no attempt is made to notify them anymore
		// and they are garbage collected
		if (badOnes != null) {
			for (UndeploymentListener listener : badOnes) {
				removeListener(listener);

			}
		}

	}

	/**
	 * Dispatch the event to the given listener
	 * 
	 * @param listener
	 * @param event
	 * @throws NotificationException
	 *             this exception is thrown if there is a problem notifying the
	 *             listener, other than OutOfMemory, ThreadDeath etc. This could
	 *             be used to remove the listener
	 */
	private void fireGlobalEvent(DeploymentEvent event,
			DeploymentListener listener) throws NotificationException {

		try {
			listener.deploymentPerformed(event);

		} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal error
			// are not consumed
			throw e;
		} catch (ThreadDeath e) {
			throw e;

		} catch (InternalError e) {
			throw e;

		} catch (Throwable t) { // the listener is misbehaving. Schedule for
			// removal

			String listenerDescription;
			try {
				listenerDescription = listener.toString();
			} catch (Throwable th) {
				DCLog.logErrorThrowable(location, null,
						"Failed to obtain listener description", th);
				listenerDescription = "N/A";
			}

			throw new NotificationException(
					"A problem occured while notifying the listener "
							+ listenerDescription + " for the event " + event,
					t);

		}

	}

	/**
	 * Dispatch the event to the given listener
	 * 
	 * @param listener
	 * @param event
	 * @throws NotificationException
	 *             this exception is thrown if there is a problem notifying the
	 *             listener, other than OutOfMemory, ThreadDeath etc. This could
	 *             be used to remove the listener
	 */
	private void fireGlobalEvent(UndeploymentEvent event,
			UndeploymentListener listener) throws NotificationException {

		try {
			listener.undeploymentPerformed(event);

		} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal error
			// are not consumed
			throw e;
		} catch (ThreadDeath e) {
			throw e;

		} catch (InternalError e) {
			throw e;

		} catch (Throwable t) { // the listener is misbehaving. Schedule for
			// removal

			String listenerDescription;
			try {
				listenerDescription = listener.toString();
			} catch (Throwable th) {
				DCLog.logErrorThrowable(location, null,
						"Failed to obtain listener description", th);
				listenerDescription = "N/A";
			}

			throw new NotificationException(
					"A problem occured while notifying the listener "
							+ listenerDescription + " for the event " + event,
					t);

		}

	}

	public void fireGlobalEvent(UndeploymentEvent event) {

		List<UndeploymentListener> listeners = null;
		synchronized (this.undeployMutex) {

			/*
			 * The events should be queued if all the conditions below are met:
			 * 1. We are in a deployment process after a restart ( DCState is
			 * DEPLOYING ) 2. The flush of the deployment queue hasn't been
			 * scheduled yet ( it is scheduled when the first remote listener is
			 * added )
			 * 
			 * The first remote listener added while we are still deploying will
			 * cause the flush of the queue
			 */

			DCState dcState = DCManager.getInstance().getDCState();
			if (!this.scheduledFlushOfUndeployQueue
					&& DCState.UNDEPLOYING.equals(dcState)) {

				// queue the events to cover the case when the remote listener
				// hasn't been reconnected yet
				// after the restart
				// NOTE: if there is another remote global deployment listener
				// added before DC API manages
				// to reconnect its global listeners it will prevent the
				// delivery of the queued events to the
				// DC API, which was the goal of this queuing implementation
				if (location.bePath()) {
					tracePath(location, 
							"Because the DCState is [{0}] and the flush of the event queue wasn't scheduled yet, the event will be enqueued.",
							new Object[] { dcState });
				}

				this.undeployEventQueue.offer(event);
			}

			// get the current listenerList object ( if a listener is added or
			// removed the object will be overwritten )
			listeners = this.undeployListeners;

			if (listeners.size() == 0) {

				return;

			}

			logAboutToDispatchEvent(event);

			// the event dispatching is done synchronously because it is assumed
			// that
			// under normal circumstances all the long running tasks during
			// notification
			// will be done in asynchronous listeners ( implemented in DC API )
			fireGlobalEvent(event, listeners);

		}

	}

	/**
	 * This method must be called when the deploy / undeploy operation after the
	 * restart is finished so that the queued events, that were preserved
	 * because there were no listeners connected yet, are cleared. It is assumed
	 * that no transition from state WORKING to state UN/DEPLOYING is possible
	 * but even if it is it shouldn't do any harm
	 */
	public void clearEventQueues() {

		if (location.beDebug()) {
			traceDebug(location, "Clearing the event queues");
		}

		synchronized (this.deployMutex) {
			this.deployEventQueue.clear();
		}

		synchronized (this.undeployMutex) {
			this.undeployEventQueue.clear();
		}

	}

	private void logAddListener(DeployControllerListener listener) {
		if (location.beDebug()) {
			traceDebug(location, "GlobalListenersList - adding listener: [{0}]",
					new Object[] { listener });
		}
	}

	private void logRemoveListener(DeployControllerListener listener) {
		if (location.beDebug()) {
			traceDebug(location, "GlobalListenersList - removing listener: [{0}]",
					new Object[] { listener });
		}
	}

	private void logListenerAlreadyAdded(DeployControllerListener listener) {
		if (location.beDebug()) {
			traceDebug(
					location,
					"GlobalListenersList - listener already added: [{0}]",
					new Object[] { listener });
		}
	}

	private void logListenerNotAdded(DeployControllerListener listener) {
		if (location.beDebug()) {
			traceDebug(location, "GlobalListenersList - listener not added: [{0}]",
					new Object[] { listener });
		}
	}

	private void logAboutToDispatchEvent(DCEvent event) {
		if (location.beDebug()) {
			traceDebug(location, "About to dispatch event: [{0}]",
					new Object[] { event });
		}
	}

}

class NotificationException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotificationException(String string, Throwable t) {
		super(string, t);
	}

}
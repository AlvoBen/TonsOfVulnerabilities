package com.sap.engine.services.dc.api.event;

import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.event.DeploymentListener;

/**
 * Receives remote deployment events and dispatches them to its dispatcher.
 * 
 * @author I040924
 * 
 */
public abstract class RemoteDeploymentListener implements DeploymentListener {

	private DeployEventDispatcher dispatcher;
	protected final DALog daLog;

	public RemoteDeploymentListener(DeployEventDispatcher dispatcher,
			DALog logger) {
		this.dispatcher = dispatcher;
		this.daLog = logger;
	}

	public void setDispatcher(DeployEventDispatcher dispatcher) {

		if (dispatcher == null) {
			throw new IllegalArgumentException("The dispatcher cannot be null");
		}

		this.dispatcher = dispatcher;
	}

	public DeployEventDispatcher getDispatcher() {
		return this.dispatcher;
	}

	public void deploymentPerformed(
			com.sap.engine.services.dc.event.DeploymentEvent evt) {

		if (evt == null) {

			// there might be p4 serialization problem or bug in DC
			// in this case just log an error and consume the event
			// instead of throwing NPE to DC
			this.daLog.logError("ASJ.dpl_api.001087",
					"The remote event is null");
			return;

		}

		// wrap all the dc api functionality in try / catch clause in order to
		// detect
		// any problems during event dispatching
		try {
			String eventDescription = "[sduId="
					+ evt.getDeploymentBatchItem().getSdu().getId().toString()
					+ ", action=" + evt.getDeploymentEventAction().toString();

			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("Received remote deployment event [{0}]",
						new Object[] { eventDescription });
			}

			try {
				DeploymentEvent daEvt = mapDeployEvent(evt);
				if (daEvt != null) {
					this.dispatcher.dispatchDeploymentEvent(daEvt);
					if (daLog.isDebugTraceable()) {
						this.daLog
								.traceDebug(
										"Dispatched remote deployment event [{0}] to dispatcher [{1}]",
										new Object[] { eventDescription,
												this.dispatcher });
					}
				} else {
					if (daLog.isDebugTraceable()) {
						this.daLog.traceDebug(
								"Remote deployment event [{0}] was filtered",
								new Object[] { eventDescription });
					}
				}
			} catch (MappingException e) {
				this.daLog
						.logThrowable(
								"ASJ.dpl_api.001088",
								"An error occured while mapping the remote deployment event [{0}] : ",
								e, new Object[] { eventDescription });
			}

		} catch (Throwable t) {
			this.daLog.logThrowable("ASJ.dpl_api.001089",
					"Unexpected throwable while dispatching event [{0}]", t,
					new Object[] { evt });
		}

	}

	/**
	 * Analyze the remote event, filter it if necessary or try to map it to a
	 * DC_API one that can be handed to the DC_API listeners.
	 * 
	 * @param evt
	 *            the remote event
	 * @return the resulting event or null if the event if filtered
	 * @throws MappingException
	 *             - when there is a problem
	 */
	protected abstract DeploymentEvent mapDeployEvent(
			com.sap.engine.services.dc.event.DeploymentEvent evt)
			throws MappingException;

	public int getId() {
		return this.hashCode();
	}

}

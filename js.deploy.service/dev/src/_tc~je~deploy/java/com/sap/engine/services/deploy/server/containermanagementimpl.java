package com.sap.engine.services.deploy.server;

import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.migration.exceptions.CMigrationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetectorExt;
import com.sap.engine.services.deploy.ear.jar.moduledetect.ModuleDetectorWrapper;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.container.ContainerInfoValidator;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.tc.logging.Location;

public class ContainerManagementImpl implements ContainerManagement {

	private static final Location location = 
		Location.getLocation(ContainerManagementImpl.class);

	private final DeployServiceImpl deploy;
	private final Containers mContainers;
	private final TransactionManager tManager;
	private final DeployEventSystem eventSystem;

	public ContainerManagementImpl(DeployServiceImpl deploy,
			Containers mContainers, TransactionManager tManager,
			DeployEventSystem eventSystem) {
		this.deploy = deploy;
		this.mContainers = mContainers;
		this.tManager = tManager;
		this.eventSystem = eventSystem;
	}

	/**
	 * Registers a new container when a service with container is started and
	 * send DeployEvent about that.
	 * 
	 * @param containerName
	 *            the name of the container that is being registered. not used
	 *            anymore
	 * @param container
	 *            the container that is being registered represented by
	 *            ContainerInterface implementation.
	 * 
	 * @see com.sap.engine.services.deploy.container.ContainerManagement#registerContainer(java.lang.String,
	 *      com.sap.engine.services.deploy.container.ContainerInterface)
	 */
	public DeployCommunicator registerContainer(final String containerName,
			final ContainerInterface container) {
		DSLog.logInfo(location, "ASJ.dpl_ds.000567",
				"Container {0} will be registered with implementation {1}",
				containerName, container);
		ContainerInfoValidator.validateAndFix(container);

		final ContainerInfo cInfo = container.getContainerInfo();
		final ContainerWrapper wrapper = mContainers.getContainer(cInfo
				.getName());
		if (wrapper != null && wrapper.hasContainerInfoWrapper()) {
			wrapper.setContainer(container);
		} else {
			DSLog
					.traceWarningWithFaultyComponentCSN(
							location, 
							container,
							"ASJ.dpl_ds.000462",
							"The container [{0}] does not provide containers-info.xml file and cannot be used when it is not started.",
							containerName);
			ContainerWrapper containerWrapper = new ContainerWrapper(container);
			ModuleDetector detector = cInfo.getModuleDetector();
			if (detector != null) {
				containerWrapper.setDetectorWrapper(new ModuleDetectorWrapper(
						detector, detector instanceof ModuleDetectorExt));
			}
			mContainers.addContainer(cInfo.getName(), containerWrapper);
		}
		final DeployCommunicatorImpl dCom = new DeployCommunicatorImpl(deploy,
				cInfo, eventSystem, tManager);
		mContainers.addCommunicator(cInfo.getName(), dCom);
		container.addProgressListener(eventSystem);
		fireContainerEvent(container.getContainerInfo(),
				DeployEvent.LOCAL_ACTION_START,
				DeployEvent.REGISTER_CONTAINER_INTERFACE);
		return dCom;
	}

	/**
	 * Unregisters a container when the corresponding service is stopped and
	 * send DeployEvent about that.
	 * 
	 * @param containerName
	 *            the name of the stopped service providing a container.
	 * @see com.sap.engine.services.deploy.container.ContainerManagement#unregisterContainer(java.lang.String)
	 */
	public void unregisterContainer(String containerName) {		

		final ContainerWrapper containerWrapper = mContainers
				.getContainer(containerName);
		ContainerInfo info;
		if (containerWrapper != null
				&& containerWrapper.getRealContainerInterface() != null) {
			containerWrapper.removeProgressListener(eventSystem);
			if (containerWrapper.hasContainerInfoWrapper()) {
				// remove real container but leave the wrapper
				containerWrapper.setContainer(null);
				containerWrapper.setDetectorWrapper(null);
			} else {
				mContainers.remove(containerName);
			}
			info = containerWrapper.getContainerInfoWithoutStart();
			fireContainerEvent(info, DeployEvent.LOCAL_ACTION_FINISH,
					DeployEvent.UNREGISTER_CONTAINER_INTERFACE);
		}
		try {
			if (CMigrationInvoker.getInstance().existsMigrator(containerName)) {
				unregisterMigrator(containerName);
			}
		} catch (CMigrationException cmEx) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006352",
					"Exception while unregistering mirgator", cmEx);
		}
		DSLog.traceInfo(location, "ASJ.dpl_ds.000568",
				"Container [{0}] was unregistered; all applications deployed on this container were stopped", containerName);
	}

	private void fireContainerEvent(ContainerInfo cInfo, byte action,
			byte actionType) {
		final DeployEvent dEvent = new DeployEvent(cInfo.getServiceName(),
				action, actionType, PropManager.getInstance().getClElemName());
		dEvent.setWhoCausedGroupOperation(cInfo.getName());
		eventSystem.fireDeployEvent(dEvent, DeployConstants.SERVICE_TYPE, null);
	}

	private void unregisterMigrator(String migratorName)
			throws CMigrationException {
		CMigrationInvoker.getInstance().unregisterMigrator(migratorName);
	}
}

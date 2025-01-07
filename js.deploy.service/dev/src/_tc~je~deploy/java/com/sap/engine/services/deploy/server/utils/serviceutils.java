package com.sap.engine.services.deploy.server.utils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.DeployServiceFactory;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.tc.logging.Location;

/**
 * Provides a synchronous start of container service. Wait until the container
 * is registered.
 * 
 * @author I043963
 * 
 */
public class ServiceUtils {
	
	private static final Location location = 
		Location.getLocation(ServiceUtils.class);
	
	private final static String SYNC_TYPE = "ContainerWrapper";
	private final static Map<String, CountDownLatch> latches = 
		Collections.synchronizedMap(new HashMap<String, CountDownLatch>());
	private final static String WAIT_CONTAINER_REGISTRATION = 
		"WaitContainerRegistration";
	private final static int WAIT_TIMEOUT = 1000 * 60;
	private final static Object monitor = new Object();

	/**
	 * Start component, either application or service, that provides container
	 * and wait until container is registered or timeout occurs.
	 * 
	 * @param comp
	 *            the Component that provides container
	 * @param contName
	 *            the name of the container provided by this component
	 * @throws ServerDeploymentException
	 * @throws ServiceException
	 */
	public static void startComponentAndWait(Component comp, String contName)
	    throws ServerDeploymentException, ServiceException {

		final byte state = PropManager.getInstance().getClusterMonitor()
		    .getCurrentParticipant().getState();

		if(state != ClusterElement.RUNNING && 
			state != ClusterElement.STARTING &&
			state != ClusterElement.APPS_STARTING) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.SERVER_NOT_STARTED, new Object[] {comp,
					contName, getServerStateAsString(state)});
			sde.setMessageID("ASJ.dpl_ds.005126");
			throw sde;
		}

		synchronized(monitor) {
			final CountDownLatch latch = new CountDownLatch(1);
			latches.put(comp.getName(), latch);

			if(isComponentStartNeeded(comp)) {
				startComponent(comp, contName);
			}
			ContainerWrapper container = 
				Containers.getInstance().getContainer(contName);
			if(container != null) {
				try {
					if(!Containers.getInstance()
						.isContainerRegistered(contName)) {
						latch.await(WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
					}
					if(!Containers.getInstance()
						.isContainerRegistered(contName)) {
						ServerDeploymentException sde = 
							new ServerDeploymentException(
								DSLogConstants.TIME_OUT, comp,
								WAIT_CONTAINER_REGISTRATION, WAIT_TIMEOUT,
								SYNC_TYPE);
						sde.setMessageID("ASJ.dpl_ds.006007");
						throw sde;
					}
				} catch(InterruptedException ex) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION, 
							new String[] { WAIT_CONTAINER_REGISTRATION, 
								comp.getName()}, ex);
					sde.setMessageID("ASJ.dpl_ds.005029");
					throw sde;
				} finally {
					latches.remove(comp.getName());
				}
			} else {
				latches.remove(comp.getName());
			}
		}
	}

	private static void startComponent(Component comp, String contName)
	    throws ServerDeploymentException, ServiceException {
		if(location.beDebug()) {
			DSLog.traceDebug(
					location,
				"Trying to start component [{0}] of type [{1}] " +
				"in status [{2}]  and wait until register the container, " +
				"which it provides or timeout occures.",
				comp.getName(), comp.getType(),
				getComponentStatusAsString(comp));
		}

		if(comp.getType().equals(Component.Type.APPLICATION)) {
			try {
				DeployServiceFactory.getDeployService().startApplicationAndWait(
					comp.getName(), 
					new String[] {PropManager.getInstance().getClElemName()});
			} catch(RemoteException re) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_START_APPLICATION, new Object[] {
						comp.getName(), contName}, re);
				sde.setMessageID("ASJ.dpl_ds.005124");
				throw sde;
			}
		} else {
			final ServiceMonitor sm = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext().getSystemMonitor()
				.getService(comp.getName());
			sm.start();
		}
	}

	private static byte getComponentStatus(Component comp) {
		if(comp.getType().equals(Component.Type.APPLICATION)) {
			return Applications.get(comp.getName()).getStatus().getId();
		}
		final ServiceMonitor sm = PropManager.getInstance()
			.getAppServiceCtx().getContainerContext().getSystemMonitor()
			.getService(comp.getName());
		return sm.getStatus();
	}

	private static byte getComponentInternalStatus(String compName) {
		final ServiceMonitor sm = PropManager.getInstance().getAppServiceCtx()
		    .getContainerContext().getSystemMonitor().getService(compName);
		return sm.getInternalStatus();
	}

	private static boolean isComponentStartNeeded(Component comp)
	    throws ServerDeploymentException {
		if(comp.getType().equals(Component.Type.APPLICATION)) {
			return Applications.get(comp.getName()).getStatus().getId() !=
				Status.STARTED.getId();
		} else if(comp.getType().equals(Component.Type.SERVICE)) {
			byte status = getComponentStatus(comp);
			byte internal_status = getComponentInternalStatus(comp.getName());
			// if service status is loaded and internal status is not failed, we
			// can try to start this service
			if(status == ServiceMonitor.STATUS_LOADED &&
				internal_status != ServiceMonitor.INTERNAL_STATUS_START_FAIL) {
				return true;
			}
			ArrayList<String> containers = Containers.getInstance()
				.getContainersForComponent(comp.getName());
			boolean isContainerProvider = containers != null &&
				containers.size() > 0;
			// we need to check if this service provides container
			if(isContainerProvider) {
				// we must check 2 possibilities - if service is not loaded
				// and not already started or if internal status is failed
				// in both cases we throw an exception that service failed
				// to start
				if(status != ServiceMonitor.STATUS_LOADED &&
					status != ServiceMonitor.STATUS_ACTIVE ||
					internal_status == ServiceMonitor.INTERNAL_STATUS_START_FAIL) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.SERVICE_FAILED_TO_START,
						comp.getName(), containers.get(0));
					sde.setMessageID("ASJ.dpl_ds.005128");
					throw sde;
				}
			}
		}
		return false;
	}

	private static String getComponentStatusAsString(Component comp) {
		if(comp.getType().equals(Component.Type.APPLICATION)) {
			return getApplicationStatusAsString(getComponentStatus(comp));
		}
		return getServiceStatusAsString(getComponentStatus(comp)) + "/" +
		getServiceStatusAsString(getComponentInternalStatus(comp.getName()));
	}

	/**
	 * Notify waiting thread to stop wait. This method is called when the
	 * corresponding container is registered through
	 * <tt>DeployServiceImpl.registerContainer()</tt>. If the container
	 * registration was not triggered via call to
	 * <tt>ServiceUtils.startComponentAndWait(Component component)</tt>, then
	 * the corresponding latch will be <tt>null</tt>. Usually this will be the
	 * case when the server is bootstrapped and the corresponding service was
	 * started independently.
	 * 
	 * @param serviceName
	 *            the name of the service providing the container.
	 */
	public static void notifyAllStopWaiting(String serviceName) {
		final CountDownLatch latch = latches.get(serviceName);
		if(latch != null) {
			// The service is started via call to startComponentAndWait()
			latch.countDown();
		}
	}

	/**
	 * Wrap all exceptions in ServiceRuntimeException to avoid changes in
	 * ContainerInterface and ContainerInfo method signatures.
	 * 
	 * @param comp
	 *            the name of the service providing the container.
	 * @param contName
	 *            the name of the container.
	 */
	public static void startComponentAndWaitRE(Component comp, String contName) {
		try {
			ServiceUtils.startComponentAndWait(comp, contName);
		} catch(ServerDeploymentException ex) {
			throw new RuntimeException("Failed to start [" + comp
			    + "], which provides container [" + contName + "].", ex);
		} catch(ServiceException ex) {
			throw new RuntimeException("Failed to start [" + comp
			    + "], which provides container [" + contName + "].", ex);
		}
	}

	private static String getServiceStatusAsString(byte stat) {
		switch(stat) {
		case ComponentMonitor.STATUS_LOADED:
			return "Loaded";
		case ComponentMonitor.STATUS_ACTIVE:
			return "Active";
		case ServiceMonitor.INTERNAL_STATUS_STARTED:
			return "Started";
		case ServiceMonitor.INTERNAL_STATUS_START_FAIL:
			return "Failed to start";
		case ServiceMonitor.INTERNAL_STATUS_STOPPING:
			return "Stopping";
		case ServiceMonitor.INTERNAL_STATUS_STARTING:
			return "Starting";
		case ServiceMonitor.INTERNAL_STATUS_STOPPED:
			return "Stopped";
		case ServiceMonitor.INTERNAL_STATUS_STOP_FAIL:
			return "Stop failed";
		case ServiceMonitor.DISABLED:
			return "Disabled";
		default:
			return "unknown";
		}
	}

	private static String getServerStateAsString(byte state) {

		switch(state) {
		case ClusterElement.APPS_STARTING:
			return "Starting Applications";
		case ClusterElement.BOOTSTRAPING:
			return "Bootstraping";
		case ClusterElement.DEPLOYING:
			return "Deploying";
		case ClusterElement.INITIAL:
			return "Initial";
		case ClusterElement.PREPARED_STOP:
			return "Prepared stop";
		case ClusterElement.PREPARING_STOP:
			return "Preparing stop";
		case ClusterElement.DEBUGGING:
			return "Debugging";
		case ClusterElement.STARTING:
			return "Starting";
		case ClusterElement.STOPPED:
			return "STOPPED";
		case ClusterElement.STOPPED_MANUALY:
			return "STOPPED MANUALY";
		case ClusterElement.STOPPING:
			return "STOPPING";
		case ClusterElement.WAIT_START:
			return "Wait to start";
		case ClusterElement.WAIT_STOP:
			return "Wait to stop";
		case ClusterElement.RUNNING:
			return "Running";
		default:
			return "unknown";
		}
	}

	private static String getApplicationStatusAsString(byte id) {
		Status status = Status.getStatusByID(id);
		return status.getName();
	}
}
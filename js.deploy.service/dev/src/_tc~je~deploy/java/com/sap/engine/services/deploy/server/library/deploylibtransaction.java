/*
 * DeployLibTransaction.java
 *
 * Created on April 9, 2002, 1:41 AM
 */
package com.sap.engine.services.deploy.server.library;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.sap.engine.frame.container.deploy.ComponentDeploymentException;
import com.sap.engine.frame.container.deploy.DeployContext;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.StringUtils;
import com.sap.tc.logging.Location;

/**
 * 
 * @author Radoslav Tsiklovski, Rumiana Angelova
 * @version 6.30
 */
public class DeployLibTransaction extends LibraryTransaction {
	
	private static final Location location = 
		Location.getLocation(DeployLibTransaction.class);
	
	private final static Map<String, CountDownLatch> latchs = Collections
			.synchronizedMap(new HashMap<String, CountDownLatch>());
	private final static String COMPONENT_NAME = "*";

	private String jar = null;
	private String runtimeName;

	public DeployLibTransaction(final String jar, final String name,
		final byte type, final DeployServiceContext ctx)
		throws DeploymentException {
		super(ctx, getLocalTransactionType(), 
			type, getLibName(jar, name));
		if (jar == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_DEPLOY_LIB,
				" the jar name is not specified.");
			sde.setMessageID("ASJ.dpl_ds.005020");
			throw sde;
			
		} else if (!(new File(jar)).exists()) {
			ServerDeploymentException sde =  new ServerDeploymentException(
				ExceptionConstants.CANNOT_DEPLOY_LIB,
				"jar name " + jar + " doesn't exist.");
			sde.setMessageID("ASJ.dpl_ds.005020");
			throw sde;
		}
		this.jar = jar;
		latchs.put(COMPONENT_NAME, new CountDownLatch(1));
	}

	private static final String getLibName(final String jar, final String name) {
		String libName;
		if (name != null) {
			libName = name;
		} else {
			libName = jar;
			if (jar.lastIndexOf(File.separatorChar) > -1) {
				libName = jar.substring(
					jar.lastIndexOf(File.separatorChar) + 1);
			}
			if (libName.endsWith(".jar")) {
				libName = libName.substring(0, libName.length() - 4);
			}
		}
		return StringUtils.intern(libName);
	}

	private static String getLocalTransactionType() {
		return DeployConstants.deployLib;
	}

	public void begin() throws DeploymentException {
		DeployContext dc = PropManager.getInstance().getAppServiceCtx()
			.getContainerContext().getDeployContext();
		try {
			ComponentMonitor cMonitor = null;
			String cType = null;
			if (type == LIBRARY) {
				if (location.bePath()) {
					DSLog.tracePath(location, "Start deploying library [{0}]", jar);
				}
				runtimeName = dc.deployLibrary(new File(jar));

				if (location.bePath()) {
					DSLog.tracePath(location, "End deploying [{0}] library [{1}]",
						runtimeName, jar);
				}
				cMonitor = PropManager.getInstance().getAppServiceCtx()
					.getContainerContext().getSystemMonitor().getLibrary(
						runtimeName);
				cType = DeployConstants.RESOURCE_TYPE_LIBRARY;
			} else if (type == INTERFACE) {
				if (location.bePath()) {
					DSLog.tracePath(location, "Start deploying interface [{0}]", jar);
				}
				runtimeName = dc.deployInterface(new File(jar));
				if (location.bePath()) {
					DSLog.tracePath(location, "End deploying interface [{0}]", jar);
				}
				cMonitor = PropManager.getInstance().getAppServiceCtx()
					.getContainerContext().getSystemMonitor().getInterface(
						runtimeName);
				cType = DeployConstants.RESOURCE_TYPE_INTERFACE;
			} else if (type == SERVICE) {
				if (location.bePath()) {
					DSLog.tracePath(location, "Start deploying service [{0}]", jar);
				}
				runtimeName = dc.deployService(new File(jar));
				if (location.bePath()) {
					DSLog.tracePath(location, "End deploying service [{0}]",
							jar);
				}
				cMonitor = PropManager.getInstance().getAppServiceCtx()
						.getContainerContext().getSystemMonitor().getService(
								runtimeName);
				cType = DeployConstants.RESOURCE_TYPE_SERVICE;
			} else {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNKNOWN_SERVER_COMPONENT_TYPE,
					String.valueOf(type), jar);
				sde.setMessageID("ASJ.dpl_ds.005022");
				throw sde;
			}
			waitWithTimeout4Notification(cMonitor, cType);
		} catch (ComponentDeploymentException ex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.ERROR_DURING_PROCESS_SERVER_COMPONENT,
				new String[] { getTransactionType(), jar }, ex);
			sde.setMessageID("ASJ.dpl_ds.005023");
			throw sde;
		}
		this.setSuccessfullyFinished(true);
	}

	private void waitWithTimeout4Notification(ComponentMonitor cMonitor,
		String type) throws ServerDeploymentException {
		if (cMonitor.getStatus() == ComponentMonitor.STATUS_LOADED) {
			final long timeout = PropManager.getInstance()
				.getTimeout4LibraryLoadedEvent();
			final CountDownLatch latch = latchs.get(COMPONENT_NAME);
			try {
				if (!latch.await(timeout, TimeUnit.MILLISECONDS)) {
					DSLog.traceDebug(
							location, 
							"Timeout while waiting for notification during deploy of {0} {1}",
							type, cMonitor.getComponentName());
				}
			} catch (InterruptedException e) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION, 
					new String[] { getTransactionType(), 
						cMonitor.getComponentName() }, e);
				sde.setMessageID("ASJ.dpl_ds.005029");
				throw sde;
			}

		} else {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
						"The [{0}] [{1}] has status [{2}] and won't wait for its resource available event.",
						cMonitor.getComponentName(), type, cMonitor.getStatus());
			}
		}
	}

	/**
	 * @param compName
	 *            component name. Currently this parameter is not used and is
	 *            replaced with COMPONENT_NAME, because of different runtime and
	 *            deploy names
	 */
	public static void notifyAllComponentLoadedEvent(String compName) {
		final CountDownLatch latch = latchs.get(COMPONENT_NAME);
		if (latch == null) {
			DSLog.traceDebug(location, "Cannot obtain the countdown latch for {0}",
				COMPONENT_NAME);
			return;
		}
		latch.countDown();
	}

	@Override
	public void commit() {
		cleanUpJarFile();
	}

	@Override
	public void rollback() {
		cleanUpJarFile();
	}

	private void cleanUpJarFile() {
		if (jar.replace('\\', '/').indexOf("deploy/work/deploying/") != -1) {
			File libraryJarFile = new File(jar);
			if (libraryJarFile.exists()) {
				libraryJarFile.delete();
			}
		}
	}

	public String getComponentRuntimeName() {
		return runtimeName;
	}
}

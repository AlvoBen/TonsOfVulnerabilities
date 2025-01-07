/*
 * RemoveLibTransaction.java
 *
 * Created on April 13, 2002, 5:58 AM
 */
package com.sap.engine.services.deploy.server.library;

import java.util.ArrayList;

import com.sap.engine.frame.container.deploy.ComponentDeploymentException;
import com.sap.engine.frame.container.deploy.DeployContext;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * 
 * @author Radoslav Tsiklovski, Rumiana Angelova
 * @version 6.30
 */
public class RemoveLibTransaction extends LibraryTransaction {
	
	private static final Location location = 
		Location.getLocation(RemoveLibTransaction.class);

	private final String providerName;

	/** Creates new RemoveLibTransaction */
	public RemoveLibTransaction(final String _providerName,
		final String _libName, final byte type, final DeployServiceContext ctx) {
		super(ctx, DeployConstants.removeLib, type, _libName);
		this.providerName = _providerName;
	}

	public void begin() throws DeploymentException,
			ComponentNotDeployedException {
		final DeployContext dc = PropManager.getInstance().getAppServiceCtx()
				.getContainerContext().getDeployContext();
		final String libName = getModuleID();
		try {
			if (type == LIBRARY) {
				if (location.bePath()) {
					DSLog.tracePath(location, "Start removing library with vendor [{0}] and name [{1}]",
							providerName, libName);
				}
				dc.removeLibrary(providerName, libName);
				if (location.bePath()) {
					DSLog.tracePath(location, "End removing library with vendor [{0}] and name [{1}]",
							providerName, libName);
				}
			} else if (type == INTERFACE) {
				if (location.bePath()) {
					DSLog.tracePath(location, "Start removing interface with vendor [{0}] and name [{1}]",
							providerName, libName);
				}
				dc.removeInterface(providerName, libName);
				if (location.bePath()) {
					DSLog.tracePath(location, "End removing interface with vendor [{0}] and name [{1}]",
							providerName, libName);
				}
			} else if (type == SERVICE) {
				if (location.bePath()) {
					DSLog.tracePath(location, "Start removing service with vendor [{0}] and name [{1}]",
							providerName, libName);
				}
				dc.removeService(providerName, libName);
				deleteContainerFromCacheIfAny();
				if (location.bePath()) {
					DSLog.tracePath(location, "End removing service with vendor [{0}] and name [{1}]",
							providerName, libName);
				}
			} else {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNKNOWN_SERVER_COMPONENT_TYPE,
					String.valueOf(type), libName);
				sde.setMessageID("ASJ.dpl_ds.005022");
				throw sde;
			}
		} catch (ComponentDeploymentException ex) {
			if (ex instanceof com.sap.engine.frame.container.deploy.ComponentNotDeployedException) {
				throw new ComponentNotDeployedException(
						ExceptionConstants.SERVER_COMPONENT_NOT_DEPLOYED,
						new String[] {
							type == LIBRARY ? "Library"
								: (type == INTERFACE ? "Interface"
								: "Service"), libName, "remove it" }, ex);
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.ERROR_DURING_PROCESS_SERVER_COMPONENT,
				new String[] { getTransactionType(), libName }, ex);
			sde.setMessageID("ASJ.dpl_ds.005023");
			throw sde;
		}
		this.setSuccessfullyFinished(true);
	}
	
	protected void deleteContainerFromCacheIfAny() {
		Containers allContainers = Containers.getInstance();
		ArrayList<String> containers = allContainers
		.getContainersForComponent(getModuleID());
		if (containers != null && containers.size() > 0) {
			for (String container: containers) {
				if (location.beDebug()) {
					DSLog.traceDebug(location, "Removing from cache container [{0}] provided by service [{1}].",
						container, getModuleID());
				}
				allContainers.remove(container);
			}
		}
	}
}
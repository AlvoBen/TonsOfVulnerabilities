package com.sap.engine.services.dc.repo;

import static com.sap.engine.services.dc.util.PerformanceUtil.isBoostPerformanceDisabled;
import static com.sap.engine.services.dc.util.ThreadUtil.popTask;
import static com.sap.engine.services.dc.util.ThreadUtil.pushTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-16
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class RepositoryContainer {

	private static DeploymentsContainer deploymentsContainer;
	private static ThreadLocal<DeploymentsContainer> deploymentsContainerPerThread;
	private static CsnContainer csnContainer;

	private RepositoryContainer() {
	}

	public static synchronized void initDeploymentsContainer(
			final Set allSdus, final Repository repo, final ConfigurationHandler cfgHandler) throws RepositoryException {
		try {
			if (isBoostPerformanceDisabled()) {
				pushTask("[Deploy Controller] � loading and initilizing repository");
			}

			if (deploymentsContainer != null) {
				new RepositoryException(
						DCExceptionConstants.REPO_ERROR_DPL_CONTAINER_ALREADY_INIT);
			}
			
			deploymentsContainer = repo.createDeploymentsContainer(allSdus, cfgHandler);

			deploymentsContainerPerThread = new ThreadLocal<DeploymentsContainer>();

			csnContainer = new CsnContainer(getDeploymentsContainer());
		} finally {
			if (isBoostPerformanceDisabled()) {
				popTask();
			}
		}
	}

	public static synchronized void clear() {
		if (deploymentsContainer != null) {
			deploymentsContainer.clear();
			deploymentsContainer = null;
		}
	}

	public synchronized static DeploymentsContainer getDeploymentsContainer() {
		DeploymentsContainer deploymentsContainer = deploymentsContainerPerThread
				.get();
		return (deploymentsContainer == null) ? RepositoryContainer.deploymentsContainer
				: deploymentsContainer;
	}

	public static CsnContainer getCsnContainer() {
		return csnContainer;
	}
	
	/**
	 * Use this method for junit test purposed only !!!
	 * @param container
	 */
	public static void setCsnContainer(CsnContainer container){
		csnContainer = container;
	}

	public static void setThreadDeploymentsContainer(
			final DeploymentsContainer deploymentsContainer) {
		if (deploymentsContainerPerThread == null) {
			deploymentsContainerPerThread = new ThreadLocal<DeploymentsContainer>();
		}
		deploymentsContainerPerThread.set(deploymentsContainer);
	}

	public static DeploymentsContainer cloneDeploymentsContainer(
			final DeploymentsContainer deploymentsContainer)
			throws RepositoryException {
		DeploymentsContainer clonedDeploymentsContainer = RepositoryFactory
				.getInstance().createRepository().createDeploymentsContainer();
		HashSet allDeployments = new HashSet();
		try {
			for (Sdu sdu : deploymentsContainer.getAllDeployments()) {
				allDeployments.add(deepCopy(sdu));
			}
		} catch (Exception e) {
			throw new RepositoryException(
					DCExceptionConstants.REPO_ERROR_WHILE_CLONING_REPOSITORY, e);
		}
		clonedDeploymentsContainer.init(allDeployments);
		return clonedDeploymentsContainer;
	}

	static public Object deepCopy(Object oldObj) throws Exception {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			// serialize and pass the object
			oos.writeObject(oldObj);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos
					.toByteArray());
			ois = new ObjectInputStream(bin);
			// return the new object
			return ois.readObject();
		} finally {
			oos.close();
			ois.close();
		}
	}

	public static class CsnContainer {
		private final DeploymentsContainer deploymentsContainer;
		private final Map<SduId, Sdu> components = new ConcurrentHashMap<SduId, Sdu>(
				10);

		public CsnContainer(final DeploymentsContainer deploymentsContainer) {
			this.deploymentsContainer = deploymentsContainer;
		}

		public String getCsnByComponentName(final String vendorAndName) {
			final SdaId id = RepositoryComponentsFactory.getInstance()
					.createSdaId(vendorAndName);

			// try to find it in current items for processing
			Sdu resultSdu = components.get(id);
			if (resultSdu != null) {
				return resultSdu.getCsnComponent();
			}

			// try to find it in deployed components
			resultSdu = deploymentsContainer.getDeployment(id);
			if (resultSdu != null) {
				return resultSdu.getCsnComponent();
			}
			return null;
		}

		public Sdu remove(final Sdu sdu) {
			return components.remove(sdu.getId());
		}

		public Sdu add(final Sdu sdu) {
			return components.put(sdu.getId(), sdu);
		}
	}

}

/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.cache.containers;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.container.ContainerInfoWrapper;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.tc.logging.Location;

/**
 * This class is intended only for internal use by deploy service.
 * 
 * @author Luchesar Cekov
 */
public class Containers {
	
	private  final Location location = 
		Location.getLocation(this.getClass());
	
	private static Containers mInstance;
	
	private final Map<String, ContainerWrapper> mContainers;
	private final Map<String, DeployCommunicator> mCommunicators;
	private final J2eeNameTypeIndex j2eeNameTypeIndex;

	private static final String TAG_INFO = "containerInfo";
	private static final String INFO_FILE_NAME = "containers-info.xml";
	private static final String INFO_SCHEMA_FILE_NAME =	"com/sap/engine/services/deploy/container/containers-info.xsd";
	private static final String SCHEMA_LANGUAGE_URL = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String SCHEMA_SOURCE_URL = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	private static final String XML_SCHEMA_URL = "http://www.w3.org/2001/XMLSchema";
	private DocumentBuilder docBuilder;// usage synchronized by this

	private Containers() {
		mContainers = new ConcurrentHashMap<String, ContainerWrapper>();
		j2eeNameTypeIndex = new J2eeNameTypeIndex();
		mCommunicators = new ConcurrentHashMap<String, DeployCommunicator>(20);
	}

	/**
	 * Add new container from serviceMonitor containers-info.xml.
	 * 
	 * @param serviceMonitor
	 * @throws RemoteException
	 */
	public synchronized void addContainers(ServiceMonitor serviceMonitor)
		throws RemoteException {
		final InputStream is = serviceMonitor.getDescriptorContainer()
			.getPersistentEntryStream(INFO_FILE_NAME, true);
		addContainers(is, new Component(serviceMonitor.getComponentName(),
			Component.Type.SERVICE));
	}

	public synchronized void addContainers(InputStream is, Component comp) {
		if (is == null) {
			return;
		}
		try {
			Document doc = docBuilder.parse(is);
			NodeList containers = doc.getDocumentElement()
				.getElementsByTagName(TAG_INFO);
			for (int i = 0; i < containers.getLength(); ++i) {
				ContainerInfoWrapper cInfo = new ContainerInfoWrapper(
					(Element) containers.item(i), comp);
				ContainerInterface ci = getContainer(cInfo.getName());
				checkIfContainerProviderIsUnique(comp, cInfo, ci);
				addContainer(cInfo.getName(), new ContainerWrapper(cInfo));
				if (location.beDebug()) {
					DSLog.traceDebug(location, 
						"Added container [{0}] because component [{1}] with type [{2}] has containers-info.xml. \n [{3}].",
						cInfo.getName(), comp.getName(), 
						comp.getType(), cInfo.toString());
				}
			}
		} catch (SAXException sae) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006375", 
				"Exception in parsing containers-info.xml", 
				new RemoteException(DSLog.getLocalizedMessage(
					ExceptionConstants.CANNOT_INIT_CONTAINER_XML_ERROR,
					new Object[] { comp.getName() }), sae));
		} catch (IOException ioe) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006376", 
				"Exception in reading containers-info.xml", 
				new RemoteException(DSLog.getLocalizedMessage(
					ExceptionConstants.CANNOT_INIT_CONTAINER_IO_ERROR,
					new Object[] { comp.getName() }), ioe));
		}
	}

	private void checkIfContainerProviderIsUnique(Component comp,
		ContainerInfoWrapper cInfo, ContainerInterface ci)
		throws RemoteException {
		if (ci != null) {
			// we have to check if there are two different providers for
			// the same container, and throw an Exception in this case
			if (!ci.getContainerInfo().getComponent().getName()
				.equals(comp.getName())) {
				DSLog.traceWarning(location, "ASJ.dpl_ds.004053",
					"Container [{0}] appears to have two different provider components: [{1}] and [{2}], which is wrong.",
					new Object[] { cInfo.getName(), 
						ci.getContainerInfo().getComponent().getName(), 
						comp.getName() });
				throw new RemoteException(
					DSLog.getLocalizedMessage(
						ExceptionConstants.CANNOT_HAVE_SAME_CONTAINER_BY_DIFFERENT_PROVIDERS,
						new Object[] { cInfo.getName(),
							ci.getContainerInfo().getComponent().getName(),
							comp.getName() }));
			}
		}
	}

	/**
	 * Remove container with name get from serviceMonitor containers-info.xml
	 * 
	 * @param serviceMonitor
	 * @throws RemoteException
	 */
	public synchronized void removeContainers(ServiceMonitor serviceMonitor)
		throws RemoteException {
		ArrayList<String> containersToRemove = getContainersForComponent(
			serviceMonitor.getComponentName());
		for (String contName : containersToRemove) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Container removed. Container info is: [{0}].",
					getContainer(contName).getContainerInfoWithoutStart());
			}
			remove(contName);
		}
	}

	/**
	 * Read all containers-info.xml files, from all services that provide one
	 * and add them in list.
	 * 
	 * @throws RemoteException
	 */
	public synchronized void initContainers() throws RemoteException {
		try {
			docBuilder = getDocumentBuilder(this.getClass().getClassLoader()
				.getResourceAsStream(INFO_SCHEMA_FILE_NAME));
			ServiceMonitor[] serviceMonitors = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext()
				.getSystemMonitor().getServiceDescriptorsContainingFile(
					INFO_FILE_NAME);
			if (location.beInfo()) {
				DSLog.traceInfo(location, "ASJ.dpl_ds.008729",
					"The container services that have containers-info.xml are the following:[{0}].",
					CAConvertor.asSet(serviceMonitors));
			}
			for (ServiceMonitor sm : serviceMonitors) {
				addContainers(sm);
			}
		} catch (ParserConfigurationException e) {
			throw new RemoteException(DSLog.getLocalizedMessage(
				ExceptionConstants.CANNOT_INIT_CONTAINERS_XML_GENERAL_ERROR,
				new Object[0]), e);
		} catch (ServiceException e) {
			throw new RemoteException(DSLog.getLocalizedMessage(
				ExceptionConstants.CANNOT_INIT_CONTAINERS, new Object[0]), e);
		}
	}

	private DocumentBuilder getDocumentBuilder(final Object xsd)
		throws ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		if (xsd != null) {
			docBuilderFactory.setValidating(true);
			docBuilderFactory.setAttribute(SCHEMA_LANGUAGE_URL, XML_SCHEMA_URL);
			docBuilderFactory.setAttribute(SCHEMA_SOURCE_URL, xsd);
		}

		final DocumentBuilder documentBuilder = docBuilderFactory
			.newDocumentBuilder();
		documentBuilder.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}

			public void fatalError(SAXParseException exception)
					throws SAXException {
				throw exception;
			}

			public void warning(SAXParseException exception)
					throws SAXException {
				throw exception;
			}
		});

		return documentBuilder;
	}

	public static synchronized Containers getInstance() {
		if (mInstance == null) {
			mInstance = new Containers();
		}
		return mInstance;
	}

	public Map<String, ContainerWrapper> getContainersMap() {
		return Collections.unmodifiableMap(mContainers);
	}

	/**
	 * Add new container to the map of available containers.
	 * @param containerName the name of the container.
	 * @param aContainer the corresponding container proxy, which is used for
	 * lazy start of the containers.
	 * @throws IllegalArgumentException in case that the corresponding container
	 * does not support parallel operations.
	 */
	public synchronized void addContainer(final String containerName,
		final ContainerWrapper aContainer) {
		final ContainerInfo cInfo = aContainer.getContainerInfoWithoutStart();
		if (!cInfo.isSupportingParallelism()) {
			throw new IllegalArgumentException(
				"Container [" + cInfo.getName()	+ "] registered from [" +
				cInfo.getComponent().getType() + "] [" +
				cInfo.getComponent().getName() +
				"] doesn't support parallel operations. \r\n" +
				"Hint: 1). Contact container owner for fix, " +
				"because all containers must support parallel operations " +
				"in 7.20 and higher.");
		}
		mContainers.put(containerName, aContainer);
		j2eeNameTypeIndex.addContainer(cInfo);
	}

	public synchronized void remove(String containerName) {
		j2eeNameTypeIndex.removeContainer(containerName);
		mContainers.remove(containerName);
		mCommunicators.remove(containerName);
	}

	public ContainerWrapper getContainer(String aName) {
		return mContainers.get(aName);
	}

	public Set<String> getNames() {
		return mContainers.keySet();
	}

	public Collection<ContainerWrapper> getAll() {
		return mContainers.values();
	}

	public int size() {
		return mContainers.size();
	}

	public void clear() {
		mContainers.clear();
	}

	public Set<String> getRegisteredJ2eeContainerNames() {
		return Collections.unmodifiableSet(
			j2eeNameTypeIndex.j2eeContName2moduleType.keySet());
	}

	public ContainerInterface getJ2eeContainer(J2EEModule.Type j2eeModuleType) {
		String j2eeContainerName = j2eeNameTypeIndex.moduleType2j2eeContName
			.get(j2eeModuleType);
		if (j2eeContainerName == null) {
			return null;
		}
		return getContainer(j2eeContainerName);
	}

	private class J2eeNameTypeIndex {
		Map<String, J2EEModule.Type> j2eeContName2moduleType;
		Map<J2EEModule.Type, String> moduleType2j2eeContName;

		public J2eeNameTypeIndex() {
			j2eeContName2moduleType = new Hashtable<String, J2EEModule.Type>(4);
			moduleType2j2eeContName = new Hashtable<J2EEModule.Type, String>(4);
		}

		public void addContainer(ContainerInfo cinfo) {
			if (cinfo.isJ2EEContainer() && cinfo.getJ2EEModuleName() != null) {
				J2EEModule.Type j2eeType = J2EEModule.Type.valueOf(cinfo
						.getJ2EEModuleName());
				j2eeContName2moduleType.put(cinfo.getName(), j2eeType);
				moduleType2j2eeContName.put(j2eeType, cinfo.getName());
			}
		}

		public void removeContainer(String containerName) {
			ContainerInterface container = getContainer(containerName);
			if (container == null) {
				return;
			}
			ContainerInfo cinfo = container.getContainerInfo();
			if (cinfo.isJ2EEContainer() && cinfo.getJ2EEModuleName() != null) {
				J2EEModule.Type j2eeType = J2EEModule.Type.valueOf(cinfo
						.getJ2EEModuleName());
				j2eeContName2moduleType.remove(cinfo.getName());
				moduleType2j2eeContName.remove(j2eeType);
			}
		}
	}

	public synchronized ArrayList<String> getContainersForComponent(
			final String compName) {

		final ArrayList<String> containerNames = new ArrayList<String>();
		Set<String> keys = getNames();
		for (String containerName : keys) {
			ContainerWrapper container = getContainer(containerName);
			ContainerInfo cInfo = container.getContainerInfoWithoutStart();
			if (cInfo.getServiceName() != null
					&& cInfo.getServiceName().equals(compName) || cInfo
					.getComponent() != null
					&& cInfo.getComponent().getName().equals(compName)) {
				containerNames.add(containerName);
			}
		}
		return containerNames;
	}

	/**
	 * Checks if the container with this name is registered.
	 * 
	 * @param contName The name of the component providing container
	 * @return true if container with this name is registered
	 */
	public synchronized boolean isContainerRegistered(String contName) {
		// container must be in containers list and its real container should
		// not be null
		ContainerWrapper container = getContainer(contName);
		if (container != null && container.getRealContainerInterface() != null) {		
			return true;
		} 			
		return false;
	}
	
//	public synchronized ArrayList<String> getContainersForComponent(
//			final String compName) {
//
//		final ArrayList<String> containerNames = new ArrayList<String>();
//		Set<String> keys = getNames();
//		for (String containerName : keys) {
//			ContainerWrapper container = getContainer(containerName);
//			ContainerInfo cInfo = container.getContainerInfoWithoutStart();
//			if (cInfo.getServiceName() != null
//					&& cInfo.getServiceName().equals(compName)) {
//				containerNames.add(containerName);
//			}
//		}
//		return containerNames;
//	}
//
//	public synchronized boolean isContainerRegistered(String compName) {
//		// container must be in containers list and its real container should
//		// not be null
//		Set<String> keys = getNames();
//		for (String containerName : keys) {
//			ContainerWrapper container = getContainer(containerName);
//			ContainerInfo cInfo = container.getContainerInfoWithoutStart();
//			if (container.getRealContainerInterface() != null
//					&& (cInfo.getServiceName() != null
//							&& cInfo.getServiceName().equals(compName) || cInfo
//							.getComponent() != null
//							&& cInfo.getComponent().getName().equals(compName))) {
//				return true;
//			}
//		}
//		return false;
//	}


	public synchronized void addCommunicator(String contName,
		DeployCommunicator dComm) {
		mCommunicators.put(contName, dComm);
	}

	public synchronized DeployCommunicator getCommunicator(String contName) {
		return mCommunicators.get(contName);
	}

}
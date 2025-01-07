/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.library_container;

import java.util.HashSet;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.interfaces.resourcecontext.ResourceContextFactory;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.library_container.deploy.LibraryContainer;

/**
 * @author Rumiana Angelova
 * @version 7.1
 * 
 *          TODO To change the template for this generated type comment go to
 *          Window - Preferences - Java - Code Style - Code Templates
 */
public class LibraryContainerFrame extends ContainerEventListenerAdapter
		implements ApplicationServiceFrame {
	private LibraryContainer container = null;
	private ContainerManagement cManagement = null;

	static private final int MASK = MASK_INTERFACE_AVAILABLE
			| MASK_INTERFACE_NOT_AVAILABLE | MASK_SERVICE_STARTED;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.frame.ApplicationServiceFrame#start(com.sap.engine.frame
	 * .ApplicationServiceContext)
	 */
	public void start(ApplicationServiceContext serviceContext)
			throws ServiceException {
		container = new LibraryContainer(serviceContext);
		HashSet interestingNames = new HashSet(1);
		interestingNames.add("container");
		serviceContext.getServiceState().registerContainerEventListener(MASK,
				interestingNames, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.ServiceFrame#stop()
	 */
	public void stop() throws ServiceRuntimeException {
		// TODO Auto-generated method stub
		cManagement.unregisterContainer(container.getContainerInfo().getName());
		container = null;
	}

	public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
		if (interfaceName.equals("container")) {
			cManagement = (ContainerManagement) interfaceImpl;
			DeployCommunicator deployCommunicator = cManagement
					.registerContainer(container.getContainerInfo().getName(),
							container);
			if (container != null) {
				container.setDeployCommunicator(deployCommunicator);
			}
		} else if (interfaceName.equals("resourcecontext_api")) {
			ResourceContextFactory resourceContextFactory = (ResourceContextFactory) interfaceImpl;
			if (container != null) {
				container.setResourceContext(resourceContextFactory);
			}
		}
	}

}

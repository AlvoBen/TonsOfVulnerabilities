/*
 * Created on 2005-7-1 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.sdm.api.remote.model.Dependency;
import com.sap.sdm.api.remote.model.Sca;
import com.sap.sdm.api.remote.model.Sda;

/**
 * @author radoslav-i
 */
class P4ModelFactoryImpl {

	private static final P4ModelFactoryImpl INSTANCE = new P4ModelFactoryImpl();

	private P4ModelFactoryImpl() {
	}

	static P4ModelFactoryImpl getInstance() {
		return INSTANCE;
	}

	Sda createSda(com.sap.engine.services.dc.api.model.Sda sda) {
		return new P4SdaImpl(sda);
	}

	Sca createSca(
			com.sap.engine.services.dc.api.model.Sca sca,
			com.sap.engine.services.dc.api.explorer.RepositoryExplorer repositoryExplorer)
			throws RepositoryExplorerException {
		return new P4ScaImpl(sca, repositoryExplorer);
	}

	Dependency createDependency(
			com.sap.engine.services.dc.api.model.Dependency dependecy) {
		return new P4DepenedencyImpl(dependecy);
	}
}

/*
 * Created on 2005-7-1 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import java.util.ArrayList;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.RepositoryExplorer;
import com.sap.sdm.api.remote.model.Sca;
import com.sap.sdm.api.remote.model.Sda;
import com.sap.sdm.api.remote.model.Sdu;
import com.sap.sdm.apiimpl.remote.client.APIRemoteExceptionImpl;

/**
 * @author radoslav-i
 */
class P4RepositoryExplorerImpl implements RepositoryExplorer {

	private final com.sap.engine.services.dc.api.Client dcClient;

	static P4RepositoryExplorerImpl createInstance(
			com.sap.engine.services.dc.api.Client dcClient) {
		return new P4RepositoryExplorerImpl(dcClient);
	}

	private P4RepositoryExplorerImpl(
			com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.RepositoryExplorer#findAll()
	 */
	public Sdu[] findAll() throws RemoteException {
		try {
			final com.sap.engine.services.dc.api.explorer.RepositoryExplorer dcRepoExplorer = dcClient
					.getComponentManager().getRepositoryExplorerFactory()
					.createRepositoryExplorer();
			final com.sap.engine.services.dc.api.model.Sdu[] dcSdus = dcRepoExplorer
					.findAll();
			Sdu[] sdmSdus = new Sdu[dcSdus.length];
			for (int i = 0; i < dcSdus.length; i++) {
				sdmSdus[i] = buildSdmSdu(dcSdus[i], dcRepoExplorer);
			}
			return sdmSdus;
		} catch (RepositoryExplorerException re) {
			throw new APIRemoteExceptionImpl(
					"Error occurred while performing find in repository"
							+ "via the Deploy Controller API.", re);
		} catch (ConnectionException ce) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing find in repository "
							+ "via the Deploy Controller API.", ce);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.RepositoryExplorer#findSca(java.lang.String,
	 * java.lang.String)
	 */
	public Sca findSca(String aName, String aVendor) throws RemoteException {
		try {
			final com.sap.engine.services.dc.api.explorer.RepositoryExplorer dcRepoExplorer = dcClient
					.getComponentManager().getRepositoryExplorerFactory()
					.createRepositoryExplorer();
			final com.sap.engine.services.dc.api.model.Sca dcSca = dcRepoExplorer
					.findSca(aName, aVendor);
			return (Sca) buildSdmSdu(dcSca, dcRepoExplorer);
		} catch (RepositoryExplorerException re) {
			throw new APIRemoteExceptionImpl(
					"Error occurred while performing find in repository"
							+ "via the Deploy Controller API.", re);
		} catch (ConnectionException ce) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing find in repository "
							+ "via the Deploy Controller API.", ce);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.RepositoryExplorer#findSda(java.lang.String,
	 * java.lang.String)
	 */
	public Sda findSda(String aName, String aVendor) throws RemoteException {
		try {
			final com.sap.engine.services.dc.api.explorer.RepositoryExplorer dcRepoExplorer = dcClient
					.getComponentManager().getRepositoryExplorerFactory()
					.createRepositoryExplorer();
			final com.sap.engine.services.dc.api.model.Sda dcSda = dcRepoExplorer
					.findSda(aName, aVendor);
			return (Sda) buildSdmSdu(dcSda, dcRepoExplorer);
		} catch (RepositoryExplorerException re) {
			throw new APIRemoteExceptionImpl(
					"Error occurred while performing find in repository"
							+ "via the Deploy Controller API.", re);
		} catch (ConnectionException ce) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing find in repository "
							+ "via the Deploy Controller API.", ce);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.RepositoryExplorer#findAllUndeployedScas()
	 */
	public Sca[] findAllUndeployedScas() throws RemoteException {
		try {
			final com.sap.engine.services.dc.api.explorer.RepositoryExplorer dcRepoExplorer = dcClient
					.getComponentManager().getRepositoryExplorerFactory()
					.createRepositoryExplorer();
			final com.sap.engine.services.dc.api.model.Sdu[] dcSdus = dcRepoExplorer
					.findAll();
			ArrayList colDcScas = new ArrayList();
			for (int i = 0; i < dcSdus.length; i++) {
				if (dcSdus[i] instanceof com.sap.engine.services.dc.api.model.Sca) {
					colDcScas.add(dcSdus[i]);
				}
			}

			Sca[] sdmScas = new Sca[colDcScas.size()];
			for (int j = 0, scasSize = colDcScas.size(); j < scasSize; j++) {
				sdmScas[j] = (Sca) buildSdmSdu(
						(com.sap.engine.services.dc.api.model.Sca) colDcScas
								.get(j), dcRepoExplorer);
			}
			return sdmScas;
		} catch (RepositoryExplorerException re) {
			throw new APIRemoteExceptionImpl(
					"Error occurred while performing find in repository"
							+ "via the Deploy Controller API.", re);
		} catch (ConnectionException ce) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing find in repository "
							+ "via the Deploy Controller API.", ce);
		}
	}

	private Sdu buildSdmSdu(
			com.sap.engine.services.dc.api.model.Sdu sdu,
			com.sap.engine.services.dc.api.explorer.RepositoryExplorer dcRepoExplorer)
			throws RepositoryExplorerException, RemoteException {

		if (sdu == null) {
			return null;
		}

		if (sdu instanceof com.sap.engine.services.dc.api.model.Sca) {
			return P4ModelFactoryImpl.getInstance().createSca(
					(com.sap.engine.services.dc.api.model.Sca) sdu,
					dcRepoExplorer);
		} else if (sdu instanceof com.sap.engine.services.dc.api.model.Sda) {
			return P4ModelFactoryImpl.getInstance().createSda(
					(com.sap.engine.services.dc.api.model.Sda) sdu);
		} else {
			throw new APIRemoteExceptionImpl(
					"Error occurred while performing build SDM sdu"
							+ "via the Deploy Controller API.Unknown instance of sdu: "
							+ sdu);
		}
	}
}

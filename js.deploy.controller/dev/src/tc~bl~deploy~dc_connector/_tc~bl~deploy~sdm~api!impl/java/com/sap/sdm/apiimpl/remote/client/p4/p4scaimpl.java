/*
 * Created on 2005-2-8
 *
 * Author: Lalo Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.sdm.api.remote.model.Sca;
import com.sap.sdm.api.remote.model.Sda;

final class P4ScaImpl extends P4SduImpl implements Sca {

	private Sda[] groupedSdas;

	P4ScaImpl(
			com.sap.engine.services.dc.api.model.Sca sca,
			com.sap.engine.services.dc.api.explorer.RepositoryExplorer repositoryExplorer)
			throws RepositoryExplorerException {
		super(sca);
		groupedSdas = buildGroupedSdas(sca, repositoryExplorer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sca#getRelease()
	 */
	public String getRelease() {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sca#getSPNumber()
	 */
	public String getSPNumber() {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sca#getSPPatchLevel()
	 */
	public String getSPPatchLevel() {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sca#getSdas()
	 */
	public Sda[] getSdas() {
		return groupedSdas;
	}

	public String toString() {
		StringBuffer sBuf = new StringBuffer(super.toString());
		for (int i = 0; i < groupedSdas.length; i++) {
			sBuf.append(groupedSdas[i].toString());
		}
		return sBuf.toString();
	}

	private Sda[] buildGroupedSdas(
			com.sap.engine.services.dc.api.model.Sca sca,
			com.sap.engine.services.dc.api.explorer.RepositoryExplorer repositoryExplorer)
			throws RepositoryExplorerException {
		Set sdaIds = sca.getSdaIds();
		Sda[] rezSdas = new Sda[sdaIds.size()];
		int i = 0;
		for (Iterator iter = sdaIds.iterator(); iter.hasNext();) {
			SdaId sdu = (SdaId) iter.next();
			com.sap.engine.services.dc.api.model.Sda sda = repositoryExplorer
					.findSda(sdu.getName(), sdu.getVendor());

			rezSdas[i] = P4ModelFactoryImpl.getInstance().createSda(sda);

			i++;
		}
		return rezSdas;

	}
}

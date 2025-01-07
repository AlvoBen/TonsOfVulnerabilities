package com.sap.engine.services.dc.compvers.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.compvers.CompVersException;
import com.sap.engine.services.dc.compvers.CompVersFactory;
import com.sap.engine.services.dc.compvers.CompVersManager;
import com.sap.engine.services.dc.compvers.CompVersSynchResult;
import com.sap.engine.services.dc.compvers.CompVersSynchStatus;
import com.sap.engine.services.dc.compvers.CompVersSyncher;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class CompVersSyncherImpl implements CompVersSyncher {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final static String EOL = System.getProperty("line.separator");

	CompVersSyncherImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.compvers.CompVersSyncher#synch()
	 */
	public CompVersSynchResult synch() {
		final CompVersSynchResultImpl synchResult = new CompVersSynchResultImpl();

		final CompVersManager compVersManager;
		try {
			compVersManager = CompVersFactory.getInstance()
					.createCompVersManager();
		} catch (CompVersException cve) {
			synchResult.setResultText("Could not initialize CompVersManager. "
					+ "No synchronization is possible. " + cve.getMessage());
			synchResult
					.setCompVersSynchStatus(CompVersSynchStatus.COMPVERS_SYNC_FAILED);

			return synchResult;
		}

		final Repository repo = RepositoryFactory.getInstance()
				.createRepository();
		final Collection sdus;
		try {
			sdus = repo.loadSdus(null);
		} catch (RepositoryException re) {
			synchResult.setResultText("Could not load the deployed SDUs. "
					+ "No synchronization is possible. " + re.getMessage());
			synchResult
					.setCompVersSynchStatus(CompVersSynchStatus.COMPVERS_SYNC_FAILED);

			return synchResult;
		}

		final StringBuffer resultText = new StringBuffer();
		for (Iterator iter = sdus.iterator(); iter.hasNext();) {
			final Sdu sdu = (Sdu) iter.next();
			resultText.append("Sdu '").append(sdu).append("' synchronization ");
			try {
				// TODO: log the following:
				DCLog.logInfo(location, "ASJ.dpl_dc.005703",
						"Updating [{0}] with component [{1}] ...",
						new Object[] { CompVersConstants.COMPVERS_NAME, sdu });

				compVersManager.sduChanged(sdu);

				synchResult.incrementSuccesses();
				resultText.append(" succeed!").append(EOL);
			} catch (CompVersException cve) {
				synchResult.incrementFailed();
				resultText.append(" failed!").append(EOL);
			}
		}
		synchResult.setResultText(resultText.toString());

		return synchResult;
	}
}

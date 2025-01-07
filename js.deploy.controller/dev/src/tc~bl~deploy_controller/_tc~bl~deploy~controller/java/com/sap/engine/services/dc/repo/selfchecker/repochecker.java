/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.repo.selfchecker;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduVisitor;
import com.sap.engine.services.dc.selfcheck.SelfChecker;
import com.sap.engine.services.dc.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.selfcheck.SelfCheckerFactory;
import com.sap.engine.services.dc.selfcheck.SelfCheckerResult;
import com.sap.engine.services.dc.selfcheck.SelfCheckerStatus;
import com.sap.engine.services.dc.util.Constants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Mar 28, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class RepoChecker implements SelfChecker {
	
	private Location location = getLocation(this.getClass());
	
	private Set dbSdus;
	private Set memorySdus;
	private SelfCheckerStatus status = SelfCheckerStatus.OK;
	private final Map sduMap;

	public RepoChecker() {
		super();
		this.sduMap = new HashMap();
	}

	private void prepare() throws SelfCheckerException {
		this.memorySdus = new HashSet();
		Sdu[] allSdus = RepositoryContainer.getDeploymentsContainer()
				.getAllDeployments();
		this.memorySdus.addAll(Arrays.asList(allSdus));// Memory
		try {
			this.dbSdus = RepositoryFactory.getInstance().createRepository()
					.loadSdus(null);// DB
		} catch (RepositoryException e) {
			throw new SelfCheckerException("ASJ.dpl_dc.003364 "
					+ e.getMessage(), e);
		}
	}

	private boolean checkEquality(StringBuffer buffer) {
		if (this.memorySdus == null) {
			buffer.append("Memory representation is null.").append(
					Constants.EOL);
			setStatusBroken();
			return false;
		}
		if (this.dbSdus == null) {
			buffer.append("DB representation is null.").append(Constants.EOL);
			setStatusBroken();
			return false;
		}
		Sdu checkSdu;

		SduVisitor sduVisitor = new SduVisitor() {
			public void visit(Sda sda) {
				RepoChecker.this.sduMap.put(sda.getId(), sda);
			}

			public void visit(Sca sca) {
			}
		};

		for (Iterator iter = this.memorySdus.iterator(); iter.hasNext();) {
			checkSdu = (Sdu) iter.next();
			if (this.dbSdus.contains(checkSdu)) {
				continue;
			} else {
				setStatusBroken();
				buffer
						.append(
								checkSdu
										+ " exists in memory representation but is missing in DB.")
						.append(Constants.EOL);
			}
		}

		for (Iterator iter = this.dbSdus.iterator(); iter.hasNext();) {
			checkSdu = (Sdu) iter.next();
			if (this.memorySdus.contains(checkSdu)) {
				checkSdu.accept(sduVisitor);// insert sdu id to the util map
				continue;
			} else {
				setStatusBroken();
				buffer
						.append(
								checkSdu
										+ " exists in DB but is missing in memory representation.")
						.append(Constants.EOL);
			}
		}
		return true;
	}

	private void checkDependencies(StringBuffer buffer) {
		Sdu checkSdu;
		CheckDepsSduVisitor sduVisitor = new CheckDepsSduVisitor(this.sduMap,
				this.memorySdus);
		StringBuffer errBuffer;
		for (Iterator iter = this.memorySdus.iterator(); iter.hasNext();) {
			checkSdu = (Sdu) iter.next();
			checkSdu.accept(sduVisitor);
			errBuffer = sduVisitor.getErrBuffer();
			if (errBuffer != null && errBuffer.length() > 0) {
				setStatusBroken();
				buffer.append(checkSdu).append(" is inconsistent due to :")
						.append(Constants.EOL).append(errBuffer);
			}
		}
	}

	public SelfCheckerResult doCheck() throws SelfCheckerException {
		StringBuffer buffer = new StringBuffer();
		long startTime = System.currentTimeMillis();
		if (location.bePath()) {
			tracePath(location, "SelfCheck procedure started.");
		}
		prepare();
		if (location.beInfo()) {
			traceInfo(
					location, 
					"SelfCheck repository prepared. Time: [{0}] ms.",
					new Object[] { (System.currentTimeMillis() - startTime) });
		}
		if (checkEquality(buffer)) {
			if (location.beInfo()) {
				traceInfo(
						location, 
						"SelfCheck DB to memory repository compared. Time: [{0}] ms.",
						new Object[] { (System.currentTimeMillis() - startTime) });
			}
			checkDependencies(buffer);
			if (location.beInfo()) {
				traceInfo(
						location, 
						"SelfCheck dependencies checked. Time: [{0}] ms.",
						new Object[] { (System.currentTimeMillis() - startTime) });
			}
		}
		if (location.beInfo()) {
			traceInfo(
					location,
					"SelfCheck procedure finished. Total time: [{0}] ms.",
					new Object[] { (System.currentTimeMillis() - startTime) });
		}
		return SelfCheckerFactory.getInstance().createSelfCheckerResult(
				this.status, buffer.toString());
	}

	private void setStatusBroken() {
		this.status = SelfCheckerStatus.ERROR;
	}

	public String getName() {
		return "Repository consistency checker";
	}
}

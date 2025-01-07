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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.repo.DCReferenceSubstitutor;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduVisitor;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Mar 29, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class CheckDepsSduVisitor implements SduVisitor {
	private final Map dbMap;
	private final Set inMemorySdus;
	private final StringBuffer errBuffer;
	private final RepositoryComponentsFactory repositoryComponentsFactory;

	CheckDepsSduVisitor(Map dbMap, Set inMemorySdus) {
		this.dbMap = dbMap;
		this.inMemorySdus = inMemorySdus;
		this.errBuffer = new StringBuffer();
		this.repositoryComponentsFactory = RepositoryComponentsFactory
				.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
	 * .dc.repo.Sda)
	 */
	public void visit(Sda sda) {
		this.errBuffer.setLength(0);
		Set dependencies = sda.getDependencies();//
		if (dependencies == null || dependencies.isEmpty()) {
			return;
		}
		Dependency nextDependency;
		SdaId sdaId, rawSdaId;
		Sdu dbSdu;
		for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
			nextDependency = (Dependency) iter.next();

			rawSdaId = this.repositoryComponentsFactory.createSdaId(
					nextDependency.getName(), nextDependency.getVendor());
			sdaId = DCReferenceSubstitutor.substituteFor(rawSdaId);
			dbSdu = (Sdu) this.dbMap.get(sdaId);

			if (dbSdu == null) {
				this.errBuffer.append(Constants.TAB).append(
						"Cannot find corresponding DB SDU for sdaId '").append(
						sdaId).append("'").append(Constants.EOL);
			} else {
				if (!this.inMemorySdus.contains(dbSdu)) {
					this.errBuffer.append(Constants.TAB).append(
							"Cannot find corresponding memory SDU for '")
							.append(dbSdu).append("'").append(Constants.EOL);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
	 * .dc.repo.Sca)
	 */
	public void visit(Sca sca) {
		// do nothing for SCAs
	}

	public StringBuffer getErrBuffer() {
		return this.errBuffer;
	}

}

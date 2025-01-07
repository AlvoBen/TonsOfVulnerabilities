/*
 * MakeReferencesTransaction.java
 *
 * Created on April 17, 2002, 3:24 PM
 */
package com.sap.engine.services.deploy.server.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.tc.logging.Location;

/**
 * 
 * @author Radoslav Tsiklovski, Rumiana Angelova
 * @version 6.30
 */
public class MakeReferencesTransaction extends ReferencesTransaction {
	
	private static final Location location = 
		Location.getLocation(MakeReferencesTransaction.class);

	private ReferenceObject[] libraries = null;

	/** Creates new MakeReferencesTransaction */
	public MakeReferencesTransaction(final String fromApplication,
			final ReferenceObject[] toLibraries, final DeployServiceContext ctx)
			throws DeploymentException {
		super(fromApplication, ctx, DeployConstants.makeRefs);

		if (toLibraries == null || toLibraries.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.MISSING_PARAMETERS,
				getTransactionType(), "destinations");
			sde.setMessageID("ASJ.dpl_ds.005024");
			throw sde;
		}

		libraries = toLibraries;
		ctx.getReferenceResolver().checkCycleReferences(
			getModuleID(), toLibraries);
	}

	@Override
	protected ReferenceObject[] addRefs() {
		generateWarningMessage(libraries, null);

		return DUtils.concatReferences(info.getReferences(), libraries);
	}

	@Override
	protected ReferenceObject[] removeRefs() {
		return null;
	}

	public void beginLocal() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(
					location, 
					"Begin local [{0}] for application [{1}]",
					getTransactionType());
		}
		if (libraries != null) {
			final List<String> warnings = new ArrayList<String>();
			for (int i = 0; i < libraries.length; i++) {
				final Component library = new Component(libraries[i].getName(),	
					libraries[i].getReferenceTargetType());
				final boolean isHard = 
					libraries[i].getReferenceType().equals("hard");
				try {
					if(isHard) {
						ctx.getReferenceResolver().startReferencedComponent(
							library, getModuleID(), true, warnings);
					}
				} catch (DeploymentException dex) {
					DSLog.logErrorThrowable(
							location,
							"ASJ.dpl_ds.006381",
							"Error in make references by ReferenceResolver",
							dex);
				}
			}
			for (int i = 0; i < warnings.size(); i++) {
				addWarning(warnings.get(i));
			}
		}
	}

	public void commit() {
		if (location.bePath()) {
			DSLog.tracePath(
					location,
					"Commit [{0}] for application [{1}]",
					getTransactionType(), 
					getModuleID());
		}
		commitUniversal();
	}

	public void commitLocal() {
		if (location.bePath()) {
			DSLog.tracePath(
					location, 
					"Commit local [{0}] for application [{1}]",
					getTransactionType(), 
					getModuleID());
		}
		commitUniversal();
	}

	private void commitUniversal() {
		DeploymentInfo di = Applications.get(getModuleID());
		for (int i = 0; i < libraries.length; i++) {
			di.addReference(libraries[i]);
			// communicator.addReferenceInternally(this.getModuleID(),
			// libraries[i]);
		}
		this.setSuccessfullyFinished(true);
	}

	public ReferenceObject[] getReferenceObjects() {
		return libraries;
	}

	@Override
	protected Map<String, Object> prepareNotification() {
		final Map<String, Object> cmd = super.prepareNotification();
		cmd.put("ref_objects", getReferenceObjects());
		return cmd;
	}
}
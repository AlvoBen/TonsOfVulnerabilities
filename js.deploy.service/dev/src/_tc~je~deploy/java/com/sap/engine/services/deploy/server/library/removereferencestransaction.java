/*
 * RemoveReferencesTransaction.java
 *
 * Created on April 17, 2002, 6:42 PM
 */
package com.sap.engine.services.deploy.server.library;

import java.util.ArrayList;
import java.util.Map;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LoggingUtilities;

/**
 * 
 * @author Radoslav Tsiklovski, Rumiana Angelova
 * @version 6.30
 */
public class RemoveReferencesTransaction extends ReferencesTransaction {

	private static final Location location = 
		Location.getLocation(RemoveReferencesTransaction.class);
	
	private String[] libNames = null;

	/** Creates new RemoveReferencesTransaction */
	public RemoveReferencesTransaction(final String fromApplication,
		final String[] toLibraries, final DeployServiceContext ctx)
		throws DeploymentException {
		super(fromApplication, ctx, DeployConstants.removeRefs);

		if (toLibraries == null || toLibraries.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.MISSING_PARAMETERS,
				getTransactionType(), "destinations" );
			sde.setMessageID("ASJ.dpl_ds.005024");
			throw sde;
		}

		libNames = new String[toLibraries.length];
		for (int i = 0; i < libNames.length; i++) {
			// references from all types can be removed no need for check.
			libNames[i] = toLibraries[i];
		}
	}

	@Override
	protected ReferenceObject[] addRefs() {
		return null;
	}

	@Override
	protected ReferenceObject[] removeRefs() {
		ArrayList res = new ArrayList();
		ReferenceObject[] oldRefs = info.getReferences();
		if (oldRefs != null) {
			for (int i = 0; i < oldRefs.length; i++) {
				res.add(oldRefs[i]);
			}
		}
		if (libNames != null) {
			final ArrayList notFound = new ArrayList();
			final ArrayList found = new ArrayList();
			for (int i = 0; i < libNames.length; i++) {
				notFound.add(libNames[i]);
				for (int j = 0; j < res.size(); j++) {
					if (libNames[i].equals(res.get(j).toString())) {
						res.remove(j);
						found.add(libNames[i]);
						notFound.remove(libNames[i]);
						break;
					}
				}
			}
			generateWarningMessage(toArr(found), toArr(notFound));
		}

		ReferenceObject[] newRefs = new ReferenceObject[res.size()];
		res.toArray(newRefs);
		return newRefs;
	}

	private String[] toArr(ArrayList list) {
		if (list == null) {
			return null;
		}
		final String[] result = new String[list.size()];
		list.toArray(result);
		return result;
	}

	public void beginLocal() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Begin local [{0}] for application [{1}]",
					getTransactionType(), getModuleID());
		}
	}

	public void commitLocal() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Commit local [{0}] for application [{1}]",
					getTransactionType(), getModuleID());
		}
		commitUniversal();
	}

	public void commit() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Commit [{0}] for application [{1}]",
					getTransactionType(), getModuleID());
		}
		commitUniversal();
	}

	private void commitUniversal() {
		ReferenceObject refs[] = info.getReferences();
		if (libNames != null) {
			for (int i = 0; i < libNames.length; i++) {
				if (!findRef(libNames[i], refs)) {
					String csnComponent = LoggingUtilities.getCsnComponentByDCName(getModuleID());
					if (csnComponent == null || csnComponent.length() == 0) {
						csnComponent = "not available";
					}
					addWarning("Reference from " + getModuleID() + " (CSN component is " + csnComponent + ") to "
							+ libNames[i] + " doesn't exist.");
				}
			}
		}
		ctx.getTxCommunicator().removeReferencesInternally(getModuleID(),
				libNames);
		this.setSuccessfullyFinished(true);
	}

	private boolean findRef(String loaderName, ReferenceObject refs[]) {
		if (refs == null || refs.length == 0) {
			return false;
		}
		if (loaderName != null) {
			String current = null;
			for (int i = 0; i < refs.length; i++) {
				if (refs[i] != null) {
					current = "";
					if (!ReferenceObjectIntf.REF_TARGET_TYPE_APPLICATION
							.equals(refs[i].getReferenceTargetType())) {
						current += refs[i].getReferenceTargetType() + ":";
					}
					current += refs[i].getName();
					if (loaderName.equals(current)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String[] getLibNames() {
		return libNames;
	}

	@Override
	protected Map<String, Object> prepareNotification() {
		final Map<String, Object> cmd = super.prepareNotification();
		cmd.put("ref_objects", getLibNames());
		return cmd;		
	}

}

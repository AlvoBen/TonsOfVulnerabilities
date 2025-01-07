package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UndeployItemInitializer {

	private static final UndeployItemInitializer INSTANCE = new UndeployItemInitializer();

	UnsupportedComponentVisitor unsupportedComponentVisitor = new UnsupportedComponentVisitor();
	CheckSoftwareTypeVisitor checkSoftwareTypeVisitor = new CheckSoftwareTypeVisitor();
	
	private UndeployItemInitializer() {
	}

	public static final UndeployItemInitializer getInstance() {
		return INSTANCE;
	}


	/**
	 * As a first step: The operation loads the corresponding <code>Sda</code>
	 * for each <code>UndeployItem</code> from the specified
	 * <code>UndeploymentBatch</code>.
	 * 
	 * If there is no <code>Sda</code> corresponding to the
	 * <code>UndeployItem</code>, this means that there is no deployment
	 * performed and the <code>UndeployItem</code> could not be undeployed. Its
	 * status is set to <code>UndeployItemStatus.NOT_DEPLOYED</code>.
	 * 
	 * If the <code>Sda</code> is successfully loaded then the status of the
	 * item is set to <code>UndeployItemStatus.ADMITTED</code>.
	 * 
	 * As a second step: The operation filters the <code>UndeployItem</code>s by
	 * using the type <code>UnsupportedSoftwareTypesFilter</code>.
	 * 
	 * As a third step: The operation initializes the <code>UndeployItem</code>s
	 * dependencies between them in both directions. The ones on which an
	 * <code>UndeployItem</code> depends on and the ones which are depending on
	 * the <code>UndeployItem</code>.
	 * 
	 * @param undeploymentBatch
	 *            batch with <code>UndeployItem</code>s which have to be
	 *            initialized.
	 * @throws UndeploymentException
	 */
	public void init(UndeploymentBatch undeploymentBatch,
			ErrorStrategy errorStrategy) throws UndeploymentException {
		final Collection<GenericUndeployItem> batchUndeployItems = undeploymentBatch
				.getUndeployItems();
		final Set<GenericUndeployItem> admittedUndeployItems = new HashSet<GenericUndeployItem>();
		final Set<GenericUndeployItem> erroneousUndeployItems = new HashSet<GenericUndeployItem>();
		final SoftwareTypeService softwareTypeService = (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());

		
		GetUndeployItemVisitor undeployItemVisitor = new GetUndeployItemVisitor(); 
		AuthorizationVisitor authorizationVisitor = new AuthorizationVisitor(softwareTypeService);
		UndeployItemConsistenceVisitor undeployItemConsistenceVisitor = new UndeployItemConsistenceVisitor();
		
		
		//Note: the current logic does not allowed undeployment of non empty SCA
		//      because of that it don't care of the SDAs of the given for undeploy SCAs
		
		for (Iterator<GenericUndeployItem> iter = batchUndeployItems.iterator(); iter.hasNext();) {
			final GenericUndeployItem undeployItem = iter.next();
			
			undeployItem.accept(unsupportedComponentVisitor);

			if (UndeployItemStatus.INITIAL.equals(undeployItem
					.getUndeployItemStatus())) {
				undeployItem.accept(undeployItemVisitor);
			}

			if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				undeployItem.accept(undeployItemConsistenceVisitor);
			}
			
			if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				undeployItem.accept(authorizationVisitor);
			}

			if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				undeployItem.accept(checkSoftwareTypeVisitor);
			}
			
			if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				admittedUndeployItems.add(undeployItem);
			}else{
				erroneousUndeployItems.add(undeployItem);				
			}
						
			
		}
		undeploymentBatch.clear();
		undeploymentBatch.addUndeployItems(admittedUndeployItems);
		undeploymentBatch.addUndeployItems(erroneousUndeployItems);
		try{
			doPostInitCheck(admittedUndeployItems, batchUndeployItems,
				erroneousUndeployItems, errorStrategy);
		}catch(UndeploymentException ue){
			ue.addUndeployItems(
					undeploymentBatch.getOrderedUndeployItems(), 
					undeploymentBatch.getUndeployItems());
			throw ue;
		}
		initDependencies(admittedUndeployItems);
	}

	private void doPostInitCheck(Set<GenericUndeployItem> admittedUndeployItems,
			Collection<GenericUndeployItem> batchUndeployItems, Set<GenericUndeployItem> erroneousUndeployItems,
			ErrorStrategy errorStrategy) throws UndeploymentException {
		if (admittedUndeployItems.isEmpty()) {
			final StringBuffer sbErrText = new StringBuffer(
					"There is no one undeploy item which is admitted "
							+ "for undeployment:" + Constants.EOL);
			int idx = 1;
			for (Iterator<GenericUndeployItem> iter = batchUndeployItems.iterator(); iter.hasNext();) {
				final GenericUndeployItem undeployItem = iter.next();
				sbErrText.append("" + idx++).append(undeployItem).append(
						Constants.EOL);
			}
			UndeploymentException ue = new UndeploymentException(sbErrText.toString());
			ue.setMessageID("ASJ.dpl_dc.003220");
			throw ue;
		}

		if (errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)
				&& !erroneousUndeployItems.isEmpty()) {
			final StringBuffer sbErrText = new StringBuffer(
					"An error occurred while checking the undeployment items "
							+ "selected for undeployment."
							+ Constants.EOL
							+ "The following "
							+ erroneousUndeployItems.size()
							+ " undeployment items are not admitted for "
							+ "undeployment: " + Constants.EOL);
			int idx = 1;
			for (Iterator<GenericUndeployItem> iter = erroneousUndeployItems.iterator(); iter.hasNext();) {
				final GenericUndeployItem errUndeployItem = iter.next();
				sbErrText.append("" + idx++).append(errUndeployItem).append(
						Constants.EOL);
			}
			for (Iterator<GenericUndeployItem> iter = admittedUndeployItems.iterator(); iter.hasNext();) {
				final GenericUndeployItem addmittedUndeployItem = (GenericUndeployItem) iter.next();
				addmittedUndeployItem.setUndeployItemStatus(UndeployItemStatus.SKIPPED);
			}
			UndeploymentException ue = new UndeploymentException(sbErrText.toString());
			ue.setMessageID("ASJ.dpl_dc.003221");
			throw ue;

		}
	}

	void initAdmitted(Collection<GenericUndeployItem> newAdmittedUndeployItems,
			Collection<GenericUndeployItem> allAdmittedUndeployItems)
			throws UndeploymentException {
		final Collection<GenericUndeployItem> admitted = new ArrayList<GenericUndeployItem>();
		admitted.addAll(allAdmittedUndeployItems);

		for (Iterator<GenericUndeployItem> iter = newAdmittedUndeployItems.iterator(); iter
				.hasNext();) {
			final GenericUndeployItem undeployItem = iter.next();

			if (!UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				continue;
			}

			undeployItem.accept(unsupportedComponentVisitor);

			if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				undeployItem.accept(checkSoftwareTypeVisitor);
			}

			if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				admitted.add(undeployItem);
			}
		}

		initDependencies(admitted);
	}

	/**
	 * The operation finds all the <code>UndeployItem</code>s from the specified
	 * <code>UndeploymentBatch</code>, to which the specified
	 * <code>undeployItems</code> have dependencies. The resolving is based on
	 * the SDA's dependencies. The operation sets for each item both the items
	 * on which depends and the items which depend on it.
	 * 
	 * @param <code>Collection</code> with <code>UndeployItem</code>s
	 */
	private void initDependencies(Collection<GenericUndeployItem> admittedUndeployItems) {
		InitDependenciesVisitor dependenciesVisitor = new InitDependenciesVisitor(admittedUndeployItems);
		for (Iterator<GenericUndeployItem> iter = admittedUndeployItems.iterator(); iter.hasNext();) {
			final GenericUndeployItem undeployItem = iter.next();
			undeployItem.accept(dependenciesVisitor);
		}
	}

}

package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.ArrayList;
import java.util.List;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.DependenciesResolvingException;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.sdu_deps_resolver.CyclicDependenciesException;
import com.sap.engine.services.dc.cm.deploy.sdu_deps_resolver.SduDependenciesResolver;
import com.sap.engine.services.dc.cm.deploy.sdu_deps_resolver.SduDependenciesResolverFactory;
import com.sap.engine.services.dc.cm.deploy.sdu_deps_resolver.UnresolvedDependenciesException;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DependencyResolver {

	private static final DependencyResolver INSTANCE = new DependencyResolver();

	private  final Location location = DCLog.getLocation(this.getClass());
	
	static DependencyResolver getInstance() {
		return INSTANCE;
	}

	private DependencyResolver() {
	}

	/**
	 * The operation resolves the batch items and orders them with respect to
	 * dependencies
	 * 
	 * @return a list with the admitted and resolved SDAs, sorted in the order
	 *         of dependency
	 * @see com.sap.engine.services.dc.cm.deploy.sdu_deps_resolver.SduDependenciesResolver
	 */
	List<DeploymentItem> resolve(ResolverData resolverData)
			throws DependenciesResolvingException {

		final SduDependenciesResolver depsResolver = getDependencyResolver(resolverData);

		// this list should be populated with the resolved items, sorted
		// according to dependencies
		final List<DeploymentItem> sortedDeploymentBatchItems = new ArrayList<DeploymentItem>();
		try {
			depsResolver.resolve(resolverData.getDeploymentBatch(),
					sortedDeploymentBatchItems);
			return sortedDeploymentBatchItems;

		} catch (UnresolvedDependenciesException ude) {
			final String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.006505",
							"An unresolved dependencies error occurred while sorting the deployment batch items regarding the dependencies.");

			if (resolverData.getErrorStrategy().equals(
					ErrorStrategy.ON_ERROR_STOP)) {
				throw new DependenciesResolvingException(errMsg, ude);
			}

			DCLog
					.logErrorThrowable(location, 
							"ASJ.dpl_dc.001026",
							"{0} Because of error while handling strategy [{1}] the deployment process continues.",
							new Object[] { errMsg,
									resolverData.getErrorStrategy() }, ude);
			return sortedDeploymentBatchItems;
		} catch (CyclicDependenciesException cde) {
			DependenciesResolvingException dre = new DependenciesResolvingException(
					"A cyclic dependencies error occurred, while sorting "
							+ "the deployment batch items with respect to dependencies.",
					cde);
			dre.setMessageID("ASJ.dpl_dc.003034");
			throw dre;
		}
	}

	private SduDependenciesResolver getDependencyResolver(
			ResolverData resolverData) {
		if (resolverData == null) {
			return SduDependenciesResolverFactory.getInstance()
					.createSduDependenciesResolver();
		}
		return SduDependenciesResolverFactory.getInstance()
				.createSduDependenciesResolver(
						resolverData.getSoftwareTypeService());
	}

	static final class ResolverData {
		private final ErrorStrategy errorStrategy;
		private final DeploymentBatch deploymentBatch;
		private final SoftwareTypeService softwareTypeService;

		ResolverData(final ErrorStrategy errorStrategy,
				final DeploymentBatch deploymentBatch,
				final SoftwareTypeService softwareTypeService) {
			this.errorStrategy = errorStrategy;
			this.deploymentBatch = deploymentBatch;
			this.softwareTypeService = softwareTypeService;
		}

		ErrorStrategy getErrorStrategy() {
			return this.errorStrategy;
		}

		DeploymentBatch getDeploymentBatch() {
			return this.deploymentBatch;
		}

		SoftwareTypeService getSoftwareTypeService() {
			return this.softwareTypeService;
		}
	}

}

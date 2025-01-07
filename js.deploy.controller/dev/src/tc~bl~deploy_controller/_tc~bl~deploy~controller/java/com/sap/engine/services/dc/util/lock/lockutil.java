package com.sap.engine.services.dc.util.lock;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.deploy.impl.lock.impl.util.ParallelismEvaluator;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.DeploymentsContainer;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.SdaFilterVisitor;
import com.sap.tc.logging.Location;

public class LockUtil {
	
	private static Location location = getLocation(LockUtil.class);

	/**
	 * 
	 * get the repo contents and filter out the SCAs so that just the sdas get
	 * returned
	 * 
	 * TODO move this method to a more appropriate place
	 * 
	 * @return all the sdas currently in the repo
	 */
	static Set<Sda> getRepoSdas() {

		final DeploymentsContainer deploymentsContainer = RepositoryContainer
				.getDeploymentsContainer();
		Sdu[] currentSdus = deploymentsContainer.getAllDeployments();

		// get just the SDAs
		SdaFilterVisitor filter = new SdaFilterVisitor();

		for (Sdu sdu : currentSdus) {
			sdu.accept(filter);
		}

		return filter.getSdas();

	}

	/**
	 * 
	 * Get all the dependencies for this item down to the root
	 * 
	 * TODO move this method to a more appropriate place
	 * 
	 * @param item
	 * @param dependenciesGraph
	 * @return
	 * @throws CyclicReferencesException
	 */
	static List<SduId> getAllDependencies(SduId id,
			Graph<SduId> dependenciesGraph) throws CyclicReferencesException {

		return dependenciesGraph.sort(id);

	}

	/**
	 * 
	 * add the sdas as nodes to the graph if not already present and add any
	 * additional dependencies that might not have already been added yet
	 * 
	 * @param graph
	 *            the graph that should be appended with the new items
	 * @param sdas
	 *            the sdas that have to be added to the graph
	 */
	static void appendGraph(Graph<SduId> graph, Set<Sda> sdas) {

		// add all the items in the graph
		for (Sda sda : sdas) {

			SduId id = sda.getId();
			if (!graph.containsNode(id)) {
				graph.add(id);
			}

		}

		// add an edge for every dependency
		for (Sda sda : sdas) {

			final Set<Dependency> dependencies = sda.getDependencies();

			Iterator<Dependency> it = dependencies.iterator();
			while (it.hasNext()) {

				Dependency dependency = it.next();
				SdaId dependencyId = RepositoryComponentsFactory.getInstance()
						.createSdaId(dependency.getName(),
								dependency.getVendor());

				if (graph.containsNode(dependencyId)) {

					// add the edge if not already added ( might not be needed -
					// have to check with the refgraph docs
					if (!graph.getReferencesToOthersFrom(sda.getId()).contains(
							dependencyId)) {

						Edge<SduId> edge = new Edge<SduId>(sda.getId(),
								dependencyId, Edge.Type.HARD, null);

						graph.add(edge);
					}

				} else {
					// Illegal state - unresolved dependency. Log the error and
					// skip this dependency
					logError(
							location,
							"ASJ.dpl_dc.005909",
							"The item {0} has a dependency {1} which is not present in the graph",
							new Object[] { sda.toString(),
									dependency.toString() });
				}

			}
		}

	}

	/**
	 * 
	 * @param sortedAdmittedDeploymentBatchItems
	 * @return a set with all the sdas of the current sorted items
	 */
	static Set<Sda> getBatchSdas(
			Collection<DeploymentBatchItem> sortedAdmittedDeploymentBatchItems) {

		SdaFilterVisitor filter = new SdaFilterVisitor();

		for (DeploymentBatchItem item : sortedAdmittedDeploymentBatchItems) {

			item.getSdu().accept(filter);

		}
		return filter.getSdas();

	}

	/**
	 * Iterate through the batch items and create an exclusive lock item for
	 * each of
	 * 
	 * @param sortedAdmittedDeploymentBatchItems
	 * @return a set of lock items for each batch item or null if the batch
	 *         can't be executed in parallel
	 */
	public static Set<LockItem> getExclusiveLockItems(
			Collection<DeploymentBatchItem> sortedAdmittedDeploymentBatchItems,
			ParallelismEvaluator parallelistDBIVisitor)
			throws ValidationException {

		Set<LockItem> result = new HashSet<LockItem>();
		ExclusiveSduIdsVisitor exclusiveSduIdsVisitor = new ExclusiveSduIdsVisitor();
		// check for non parallel items and get exclusive locks for each batch
		// items
		Iterator<DeploymentBatchItem> iter = sortedAdmittedDeploymentBatchItems
				.iterator();
		while (iter.hasNext()) {

			DeploymentBatchItem item = iter.next();
			item.accept(parallelistDBIVisitor);

			if (!parallelistDBIVisitor.isSuitable4Parallel()) {

				// there is no point to calculate further
				if (location.beInfo()) {
					traceInfo(location, "Batch {0}",
							new String[] { parallelistDBIVisitor
									.getDescription() });
				}
				return null;

			}

			item.accept(exclusiveSduIdsVisitor);
			Set<SduId> ids = exclusiveSduIdsVisitor.getSduIds();
			
			for(SduId id : ids){
				LockItem lockItem = new LockItem(id, LockType.EXCLUSIVE);
				result.add(lockItem);
			}
			

		}

		// log if the batch is suitable for parallel ( it is known already)
		if (location.bePath()) {
			tracePath(location, "Batch {0}",
					new String[] { parallelistDBIVisitor.getDescription() });
		}

		return result;
	}

	/**
	 * 
	 * Having the exclusive locks, find all the dependencies and add shared
	 * locks for them if there isn't already an exclusive lock for them. We need
	 * to have the old dependencies (currently in the repository ) and the new
	 * ones ( introduced with the batch ) merged in order to handle the
	 * situation when some item fails during deployment and the old dependencies
	 * stay valid.
	 * 
	 * @param lockItems
	 *            - the current collection with lock items to which the shared
	 *            locks should be added
	 * 
	 * @param sortedAdmittedDeploymentBatchItems
	 * @throws CyclicReferencesException
	 * 
	 */
	public static void addSharedLocks(Set<LockItem> lockItems,
			Collection<DeploymentBatchItem> sortedAdmittedDeploymentBatchItems)
			throws CyclicReferencesException {

		Set<Sda> batchSdas = LockUtil
				.getBatchSdas(sortedAdmittedDeploymentBatchItems);
		Set<Sda> repoSdas = LockUtil.getRepoSdas();

		addSharedLocks(lockItems, batchSdas, repoSdas);

	}

	static void addSharedLocks(Set<LockItem> lockItems, Set<Sda> batchSdas,
			Set<Sda> repoSdas) throws CyclicReferencesException {

		// build a graph based on the current state of the repository
		final Graph<SduId> dependenciesGraph = new Graph<SduId>();

		LockUtil.appendGraph(dependenciesGraph, repoSdas);
		LockUtil.appendGraph(dependenciesGraph, batchSdas);

		// dependenciesGraph.cycleCheck();

		// now the graph should contain the merged old and new dependencies

		// map with all sdu ids and their associated lock items
		final Map<SduId, LockItem> sduId2LockItem = new HashMap<SduId, LockItem>(
				repoSdas.size() + batchSdas.size());

		for (LockItem lockItem : lockItems) {
			sduId2LockItem.put(lockItem.getSduId(), lockItem);
		}

		// for every item in the batch find all the dependencies and create lock
		// items for them
		Iterator<Sda> iter = batchSdas.iterator();
		while (iter.hasNext()) {

			Sda item = iter.next();
			List<SduId> allItemDependencies = getAllDependencies(item.getId(),
					dependenciesGraph);

			// for each dependency check if there is a lock item already and if
			// there isn't
			// create a new lock item with a shared lock type
			for (SduId id : allItemDependencies) {

				// ATTENTION be careful when changing this in order not to
				// replace exclusive locks with SHARED
				if (!sduId2LockItem.containsKey(id)) {
					LockItem lockItem = new LockItem(id, LockType.SHARED);
					sduId2LockItem.put(id, lockItem);
					lockItems.add(lockItem);
				}
			}

		}

	}

	// public static Boolean canBeExecutedInParallel(final DeploymentBatchItem
	// dbItem) throws ValidationException {
	//		
	// final Boolean isSuitableForParallel =
	// (Boolean) dbItem.getProperties().get(
	// DBILockEvaluator.CAN_BE_EXECUTED_IN_PARALLEL );
	//	  	
	// if ( isSuitableForParallel == null) {
	//	  		
	// // TODO optimize the creation of the parallelist visitor ( maybe with a
	// clear method )
	// final ParallelismEvaluator parallelistDBIVisitor =
	// ParallelistDBIVisitor.createInstance();
	// dbItem.accept( parallelistDBIVisitor );
	//	  		
	// dbItem.getProperties().put(
	// DBILockEvaluator.CAN_BE_EXECUTED_IN_PARALLEL,
	// parallelistDBIVisitor.isSuitable4Parallel() );
	//	  		
	// return parallelistDBIVisitor.isSuitable4Parallel();
	//	  		
	// } else {
	// return isSuitableForParallel;
	// }
	//	  	
	// }

	
	private static class ExclusiveSduIdsVisitor implements
		DeploymentBatchItemVisitor {

		private final Set<SduId> sduIds = new HashSet<SduId>();
		
		
		Set<SduId> getSduIds() {
			return sduIds;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(DeploymentItem deplItem) {
			sduIds.clear();
			Sda sda = deplItem.getSda();
			sduIds.add(sda.getId());
			if(sda.getScaId() != null){
				sduIds.add(sda.getScaId());
			}
			Sda oldSda = deplItem.getOldSda();
			if(oldSda != null && oldSda.getScaId() != null){
				sduIds.add(oldSda.getScaId());
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(CompositeDeploymentItem deploymentItem) {
			sduIds.clear();
			sduIds.add(deploymentItem.getSca().getId());
			Sca oldSca = deploymentItem.getOldSca();
			if(oldSca != null){
				sduIds.addAll(oldSca.getSdaIds());
			}
		}
	}
		
		
}

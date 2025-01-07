package com.sap.engine.services.dc.repo.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.impl.FastReferenceCycleCheckerHandler;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.lib.refgraph.impl.ReferenceCycleCheckerHandler;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.DependencyCycle;
import com.sap.engine.services.dc.repo.DependencyDeploymentGraph;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduVisitor;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @author Radoslav Ivanov
 * 
 * @version 1.0
 * @since 6.40
 * 
 */
class DependencyDeploymentGraphImpl implements DependencyDeploymentGraph {

	private final Graph<SduId> graph;
	// private final Map<SduId, Sda> sduId2SduMap;
	private final Map<SduId, DeploymentItem> sduId2DeplItemsMap;
	private final NodeAdder nodeAdder;
	
	DependencyDeploymentGraphImpl(Collection<DeploymentItem> deploymentItems) {
		this.graph = new Graph<SduId>();
		this.nodeAdder = new NodeAdder(this.graph);		
		initGraph(RepositoryContainer.getDeploymentsContainer()
				.getAllDeployments());
		this.sduId2DeplItemsMap = new HashMap<SduId, DeploymentItem>(
				deploymentItems.size());

		// add nodes in graph
		for (DeploymentItem deploymentItem : deploymentItems) {
			this.sduId2DeplItemsMap.put(deploymentItem.getSdu().getId(),
					deploymentItem);			
			this.graph.add(deploymentItem.getSdu().getId());
		}

		// add references in graph
		for (DeploymentItem deploymentItem : deploymentItems) {
			deploymentItem.getSdu().accept(this.nodeAdder);			
		}
	}
	
	private void initGraph(final Sdu[] allDeployments) {
		if (allDeployments == null) {
			return;
		}

		for (final Sdu sdu : allDeployments) {
			this.graph.add(sdu.getId());
		}

		for (final Sdu sdu : allDeployments) {
			sdu.accept(nodeAdder);
		}
	}

	public boolean isEmpty() {
		return this.sduId2DeplItemsMap.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.DependencyGraph#getItems()
	 */
	public Collection<DeploymentItem> getItems() {
		return this.sduId2DeplItemsMap.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repository.DependencyGraph#getIndependentItems
	 * ()
	 */
	public Collection<DeploymentItem> getIndependentItems() {
		final Set<DeploymentItem> result = new HashSet<DeploymentItem>();

		while (result.isEmpty() && (this.graph.size() > 0)) {
			final List<SduId> sduIdsWithNoReferencesTo = this.graph
					.getNodesWithNoReferencesTo();
			for (final SduId sduIdWithNoReferencesTo : sduIdsWithNoReferencesTo) {
				final DeploymentItem dbItem = this.sduId2DeplItemsMap
						.get(sduIdWithNoReferencesTo);
				if (dbItem == null) {
					for (Edge<SduId> edges : this.graph
						.getReferencesFromOthersTo(sduIdWithNoReferencesTo)) {
						if (edges != null) {
							graph.remove(edges);
						}
					}

					try {
						this.graph
								.remove(sduIdWithNoReferencesTo);
					} catch (NodeRemoveException nre) {
						throw new RuntimeException(
								"Problem while removing node ", nre);
					}
				} else {					
					result.add(dbItem);					
				}
			}
		}
		return result;
	}

	public Collection<DeploymentItem> getSourceItems() {
		final Set<DeploymentItem> result = new HashSet<DeploymentItem>();

		while (result.isEmpty() && (this.graph.size() > 0)) {
			final List<SduId> sduIdsWithNoReferencesTo = this.graph
					.getNodesWithNoReferencesFrom();
			for (final SduId sduIdWithNoReferencesTo : sduIdsWithNoReferencesTo) {
				final DeploymentItem dbItem = this.sduId2DeplItemsMap
						.get(sduIdWithNoReferencesTo);
				if (dbItem == null) {
					for (Edge<SduId> edges : this.graph
						.getReferencesFromOthersTo(sduIdWithNoReferencesTo)) {
						if (edges != null) {
							graph.remove(edges);
						}
					}

					try {
						this.graph
								.remove(sduIdWithNoReferencesTo);
					} catch (NodeRemoveException nre) {
						throw new RuntimeException(
								"Problem while removing node ", nre);
					}
				} else {					
					result.add(dbItem);					
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repository.DependencyGraph#containsCycles()
	 */
	public boolean containsCycles() {
		try {
			FastCycleChecker fastChecker = new FastCycleChecker();

			for (Iterator<SduId> nodes = this.sduId2DeplItemsMap.keySet()
					.iterator(); nodes.hasNext();) {
				SduId node = nodes.next();
				this.graph.traverseForward(node, fastChecker);
			}

			if (fastChecker.hasSelfCycle()) {
				return true;
			}
		} catch (CyclicReferencesException e) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.DependencyGraph#getCycles()
	 */
	public DependencyCycle[] getCycles() {
		if (!this.containsCycles()) {
			return new DependencyCycle[0];
		}

		AllCyclesCheckerHandler checker = new AllCyclesCheckerHandler(
				this.sduId2DeplItemsMap);
		try {
			for (Iterator<SduId> nodes = this.sduId2DeplItemsMap.keySet()
					.iterator(); nodes.hasNext();) {
				SduId node = nodes.next();
				this.graph.traverseForward(node, checker);
			}
		} catch (CyclicReferencesException e) {// $JL-EXC$
			// cycles should be handled but not thrown
		}

		return checker.getDependencyCycles();
	}

	public void remove(DeploymentItem withoutDeploymentItem,
			boolean withoutDepending) {
		if (withoutDepending) {
			this.removeWithDepending(withoutDeploymentItem.getSda());
		} else {
			this.remove(withoutDeploymentItem.getSda());
		}
	}

	private void remove(Sda sda) {
		this.remove(sda.getId());
	}

	private void removeWithDepending(Sda sda) {
		this.removeWithDepending(sda.getId());
	}

	private void remove(SduId sduId) {
		try {
			removeFromEdges(sduId);
			this.graph.remove(sduId);
			this.sduId2DeplItemsMap.remove(sduId);
		} catch (NodeRemoveException e) {
			throw new RuntimeException(e);
		}
	}

	private void removeFromEdges(SduId sduId) {
		Iterator<Edge<SduId>> edges = 
			this.graph.getReferencesFromOthersTo(sduId).iterator();
		while (edges.hasNext()) {
			this.graph.remove(edges.next());
		}
	}

	private void removeWithDepending(SduId sduId) {
		try {
			List<SduId> sortedIdsForRemove = graph.sortBackward(sduId);
			int i = 0;
			for (SduId sduIdToRemove : sortedIdsForRemove) {

				if (i < sortedIdsForRemove.size() - 1) {// last one has no
					// unresolved dependency
					// among ones in graph
					this.setBatchItemSkipped(sduIdToRemove, sortedIdsForRemove
							.get(i + 1));
				}

				this.sduId2DeplItemsMap.remove(sduIdToRemove);
				try {
					this.graph.remove(sduIdToRemove);
				} catch (NodeRemoveException e) {
					throw new RuntimeException(e);
				}

				i++;
			}
		} catch (CyclicReferencesException cre) {
			throw new RuntimeException(cre);
		}
	}

	private void setBatchItemSkipped(final SduId sduId,
			final SduId unresolvedDependingItem) {
		final DeploymentItem deploymentItem = this.sduId2DeplItemsMap
				.get(sduId);
		if (deploymentItem == null) {//this item is not new one but from the deployed components
			return;
		}
		deploymentItem.setDeploymentStatus(DeploymentStatus.SKIPPED);
		deploymentItem
				.addDescription("Unresolved dependency problem in chain of dependent item '"
						+ unresolvedDependingItem + "'.");

	}

	private static final class FastCycleChecker extends
			FastReferenceCycleCheckerHandler<SduId> {
		private boolean hasSelfCycle = false;

		public void selfCycle(SduId node, Edge<SduId> formEdge,
				boolean isLastCybling) {
			this.hasSelfCycle = true;
		}

		public boolean hasSelfCycle() {
			return this.hasSelfCycle;
		}
	}

	private static final class AllCyclesCheckerHandler extends
			ReferenceCycleCheckerHandler<SduId> {

		private Set<SduId> path = new LinkedHashSet<SduId>();
		private List<DependencyCycle> cycles = new LinkedList<DependencyCycle>();
		private final Map<SduId, DeploymentItem> sduId2DeplItemMap;

		public AllCyclesCheckerHandler(
				Map<SduId, DeploymentItem> sduId2DeplItemMap) {
			super();
			this.sduId2DeplItemMap = sduId2DeplItemMap;
			setOnCycleStop(false);
		}

		public boolean startNode(SduId node, Edge<SduId> edge,
				boolean aLastSybling) {
			path.add(node);
			return super.startNode(node, edge, aLastSybling);
		}

		public void endNode(SduId node) {
			path.remove(node);
		}

		public void cycle(SduId node, Edge<SduId> edge, boolean aLastSybling)
				throws CyclicReferencesException {
			super.cycle(node, edge, aLastSybling);
			addCycle(node, edge, aLastSybling);
		}

		private void addCycle(SduId node, Edge<SduId> edge, boolean aLastSybling) {
			final ArrayList<DeploymentItem> deploymentItems = new ArrayList<DeploymentItem>(
					path.size());
			for (SduId id : path) {
				final DeploymentItem cycleItem  = this.sduId2DeplItemMap.get(id);
				if (cycleItem == null) {
					continue;
				}
				deploymentItems.add(cycleItem);
			}

			cycles.add(RepositoryComponentsFactory.getInstance()
					.createDependencyCycle(deploymentItems));

			path.clear();
		}

		public void selfCycle(SduId aNode, Edge<SduId> aFormEdge,
				boolean isLastCybling) {
			try {
				super.cycle(aNode, aFormEdge, isLastCybling);
			} catch (CyclicReferencesException e) {// $JL-EXC$
				// cycle should be added but not exc thrown
			}
			addCycle(aNode, aFormEdge, isLastCybling);
		}

		public DependencyCycle[] getDependencyCycles() {
			return cycles.toArray(new DependencyCycle[0]);
		}
	}
	
	private static final class NodeAdder implements SduVisitor {

		private final Graph<SduId> graph;
		
		public NodeAdder(final Graph<SduId> graph) {
			this.graph = graph;
		}

		public void visit(final Sda sda) {
			if (this.graph.containsNode(sda.getId())) {
				//remove references
				for (final Edge<SduId> edges : this.graph
					.getReferencesToOthersFrom(sda.getId())) {
					if (edges != null) {
						this.graph.remove(edges);
					}
				}
			} else {
				this.graph.add(sda.getId());
			}

			final Set dependencies = sda.getDependencies();
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
				Dependency dependency = (Dependency) it.next();
				SdaId referenceSdaId = RepositoryComponentsFactory
						.getInstance().createSdaId(dependency.getName(),
								dependency.getVendor());
				// is resolvable
				if (this.graph.containsNode(referenceSdaId)) {
					this.graph.add(new Edge<SduId>(sda.getId(), referenceSdaId,
							Edge.Type.HARD, null));
				}
			}
		}

		public void visit(final Sca sca) {			
		}
	}	
}

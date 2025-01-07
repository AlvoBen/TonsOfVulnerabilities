package com.sap.engine.services.dc.repo.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.DeploymentsContainer;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.repo.SduVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-15
 * 
 * @author Dimitar Dimitrov
 * @author Radoslav Ivanov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeploymentsContainerImpl implements DeploymentsContainer {

	private final InitialSduLoaderVisitor initialSduLoaderVisitor;

	private final ReferenceAdder referenceAdder;
	private final ReferenceRemover referenceRemover;

	private final Map<SduId, Sdu> sduId2SduMap;
	private final Map<SduRepoLocation, Sdu> sduRepoLocation2SduMap;

	private final Graph<SduId> graph;

	DeploymentsContainerImpl() {
		this.graph = new Graph<SduId>();

		this.sduId2SduMap = new ConcurrentHashMap<SduId, Sdu>();
		this.sduRepoLocation2SduMap = new ConcurrentHashMap<SduRepoLocation, Sdu>();

		this.referenceAdder = new ReferenceAdder(this.graph, sduId2SduMap);
		this.referenceRemover = new ReferenceRemover(this.graph);

		this.initialSduLoaderVisitor = new InitialSduLoaderVisitor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#getAllDeployments()
	 */
	public Sdu[] getAllDeployments() {
		return this.sduId2SduMap.values().toArray(new Sdu[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#getDeployment(com
	 * .sap.engine.services.dc.repo.SduId)
	 */
	public Sdu getDeployment(final SduId sduId) {
		doArgCheck(sduId, "sdu id");

		return this.sduId2SduMap.get(sduId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#getDeployment(com
	 * .sap.engine.services.dc.repo.SduRepoLocation)
	 */
	public Sdu getDeployment(final SduRepoLocation sduRepoLocation) {
		doArgCheck(sduRepoLocation, "repository location");

		return this.sduRepoLocation2SduMap.get(sduRepoLocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#addDeployment(com
	 * .sap.engine.services.dc.repo.Sdu)
	 */
	public void addDeployment(final Sdu newSdu) {
		doArgCheck(newSdu, "newSdu");

		final Sdu oldSdu = this.sduId2SduMap.get(newSdu.getId());

		if (oldSdu != null) {
			initialAddSdu(newSdu);
			modifySduReferences(newSdu, oldSdu);
		} else {
			initialAddSdu(newSdu);
			initialAddSduReferences(newSdu);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#removeDeployment
	 * (com.sap.engine.services.dc.repo.Sdu)
	 */
	public void removeDeployment(Sdu sdu) {
		doArgCheck(sdu, "sdu");

		sdu = this.sduId2SduMap.remove(sdu.getId());

		this.sduRepoLocation2SduMap.remove(RepositoryComponentsFactory
				.getInstance().createSduRepoLocation(sdu));

		try {
			this.graph.remove(sdu.getId());// removes references to others nodes
			// and node
		} catch (com.sap.engine.lib.refgraph.NodeRemoveException e) {
			throw new RuntimeException(
					"Exception occurred while removing node.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#modifyDeployment
	 * (com.sap.engine.services.dc.repo.Sdu)
	 */
	public void modifyDeployment(final Sdu sdu) {
		addDeployment(sdu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#getDependingFrom
	 * (com.sap.engine.services.dc.repo.Sdu)
	 */
	public Set getDependingFrom(final Sdu sdu) {
		doArgCheck(sdu, "sdu");

		return this.getDependingFrom(sdu.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.DeploymentsContainer#getDependingFrom
	 * (com.sap.engine.services.dc.repo.SduId)
	 */
	public Set getDependingFrom(final SduId sduId) {
		doArgCheck(sduId, "sdu id");

		Set<Sdu> result = new HashSet<Sdu>();

		for (Iterator<Edge<SduId>> iter = this.graph.getReferencesFromOthersTo(sduId)
				.iterator(); iter.hasNext();) {
			result.add(sduId2SduMap.get(iter.next().getFirst()));
		}

		return result;
	}

	public void clear() {
		this.sduId2SduMap.clear();
		this.sduRepoLocation2SduMap.clear();
		this.graph.clear();
	}

	public void init(Set allDeployments) {
		if (allDeployments == null) {
			return;
		}

		for (Iterator iter = allDeployments.iterator(); iter.hasNext();) {
			final Sdu sdu = (Sdu) iter.next();
			initialAddSdu(sdu);
		}

		for (Iterator iter = allDeployments.iterator(); iter.hasNext();) {
			final Sdu sdu = (Sdu) iter.next();
			initialAddSduReferences(sdu);
		}
	}

	public SduVisitor getInitialSduLoaderVisitor() {
		return this.initialSduLoaderVisitor;
	}

	private void initialAddSdu(Sdu sdu) {
		if (sdu == null) {
			return;
		}

		this.sduId2SduMap.put(sdu.getId(), sdu);

		this.sduRepoLocation2SduMap.put(RepositoryComponentsFactory
				.getInstance().createSduRepoLocation(sdu), sdu);

		this.graph.add(sdu.getId());
	}

	private void initialAddSduReferences(Sdu sdu) {
		if (sdu == null) {
			return;
		}

		sdu.accept(referenceAdder);
	}

	private void modifySduReferences(final Sdu newSdu, final Sdu oldSdu) {
		if (newSdu == null) {
			return;
		}

		oldSdu.accept(referenceRemover);
		newSdu.accept(referenceAdder);
	}

	private void doArgCheck(Object arg, String argName)
			throws NullPointerException {
		if (arg == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003346 The argument " + argName
							+ " is null.");
		}
	}

	public Set<Sdu> getRecursiveAllDependingFrom(final Sdu sdu) {
		doArgCheck(sdu, "sdu");

		return this.getRecursiveAllDependingFrom(sdu.getId());
	}

	public Set getDeploymentsWithNoReferencesTo() {
		final Set<Sdu> result = new HashSet<Sdu>();

		for (Iterator<SduId> iter = this.graph.getNodesWithNoReferencesTo()
				.iterator(); iter.hasNext();) {
			result.add(sduId2SduMap.get(iter.next()));
		}

		return result;
	}

	public Set<Sdu> getRecursiveAllDependingFrom(final SduId sduId) {
		doArgCheck(sduId, "sdu id");
		try {
			Set<Sdu> result = new HashSet<Sdu>();
			for (Iterator<SduId> iter = this.graph.sortBackward(sduId)
					.iterator(); iter.hasNext();) {
				result.add(sduId2SduMap.get(iter.next()));
			}
			// remove this item among sorted ones
			result.remove(this.sduId2SduMap.get(sduId));

			return result;
		} catch (CyclicReferencesException cre) {
			throw new RuntimeException(cre);
		}
	}

	private static final class ReferenceRemover implements SduVisitor {

		private final Graph<SduId> graph;

		public ReferenceRemover(Graph<SduId> graph) {
			this.graph = graph;
		}

		public void visit(Sda sda) {
			final Set dependencies = sda.getDependencies();
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
				Dependency dependency = (Dependency) it.next();
				this.graph.remove(new Edge<SduId>(sda.getId(),
						RepositoryComponentsFactory.getInstance().createSdaId(
								dependency.getName(), dependency.getVendor()),
						Edge.Type.HARD, null));

			}
		}

		public void visit(Sca sca) {
		}

	}

	private static final class ReferenceAdder implements SduVisitor {

		private final Graph<SduId> graph;
		private final Map<SduId, Sdu> sduId2SduMap;

		public ReferenceAdder(Graph<SduId> graph, Map<SduId, Sdu> sduId2SduMap) {
			this.graph = graph;
			this.sduId2SduMap = sduId2SduMap;
		}

		public void visit(Sda sda) {
			final Set dependencies = sda.getDependencies();
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
				Dependency dependency = (Dependency) it.next();
				SdaId referenceSdaId = RepositoryComponentsFactory
						.getInstance().createSdaId(dependency.getName(),
								dependency.getVendor());
				// is resolvable
				if (this.sduId2SduMap.containsKey(referenceSdaId)) {
					this.graph.add(new Edge<SduId>(sda.getId(), referenceSdaId,
							Edge.Type.HARD, null));
				}
			}
		}

		public void visit(Sca sca) {
		}

	}

	class InitialSduLoaderVisitor implements SduVisitor {
		public void visit(Sda sda) {
			initialAddSdu(sda);
		}

		public void visit(Sca sca) {
			initialAddSdu(sca);
		}
	}
}

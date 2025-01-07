package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduIdVisitor;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.repo.SduVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-2
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SduRepoLocationBuilder {

	private static SduRepoLocationBuilder INSTANCE = new SduRepoLocationBuilder();

	static SduRepoLocationBuilder getInstance() {
		return INSTANCE;
	}

	private SduRepoLocationBuilder() {
	}

	SduRepoLocation build(Sdu sdu) {
		BuildBySduHelper buildBySduHelper = new BuildBySduHelper();

		sdu.accept(buildBySduHelper);

		return buildBySduHelper.getSduRepoLocation();
	}

	SduRepoLocation build(Dependency dependency) {
		final SdaId sdaId = RepositoryComponentsFactory.getInstance()
				.createSdaId(dependency.getName(), dependency.getVendor());

		final String location = LocationConstants.ROOT_REPO_DC
				+ LocationConstants.PATH_SEPARATOR + sdaId.toString();

		return new SdaRepoLocationImpl(location);
	}

	SduRepoLocation build(SduId sduId) {
		BuildBySduIdHelper buildSduIdHelper = new BuildBySduIdHelper();

		sduId.accept(buildSduIdHelper);

		return buildSduIdHelper.getSduRepoLocation();
	}

	SduRepoLocation build(String root, String sduId) {
		final String location = root + LocationConstants.PATH_SEPARATOR + sduId;

		if (root.endsWith(LocationConstants.DC)) {
			return new SdaRepoLocationImpl(location);
		} else if (root.endsWith(LocationConstants.SC)) {
			return new ScaRepoLocationImpl(location);
		} else {
			throw new IllegalArgumentException("ASJ.dpl_dc.003355 The "
					+ root + " is not valid repository root.");
		}
	}

	SduRepoLocation build(Configuration cfg) {
		final String root = cfg.getPath();

		if (root.indexOf(LocationConstants.PATH_DC_PATH) > -1) {
			return new SdaRepoLocationImpl(cfg);
		} else if (root.indexOf(LocationConstants.PATH_SC_PATH) > -1) {
			return new ScaRepoLocationImpl(cfg);
		} else {
			throw new IllegalArgumentException("ASJ.dpl_dc.003356 The "
					+ root + " is not valid repository root.");
		}
	}

	SduRepoLocation build(Configuration cfg, Sdu sdu) {
		BuildSduRepoLocation builer = new BuildSduRepoLocation(cfg);
		sdu.accept(builer);

		return builer.getSduRepoLocation();
	}

	private static final class BuildSduRepoLocation implements SduVisitor {

		private SduRepoLocation sduRepoLocation;
		private Configuration cfg;

		SduRepoLocation getSduRepoLocation() {
			return sduRepoLocation;
		}

		BuildSduRepoLocation(Configuration cfg) {
			this.cfg = cfg;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
		 * .dc.repo.Sda)
		 */
		public void visit(Sda sda) {
			sduRepoLocation = new SdaRepoLocationImpl(cfg, sda);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
		 * .dc.repo.Sca)
		 */
		public void visit(Sca sca) {
			sduRepoLocation = new ScaRepoLocationImpl(cfg, sca);
		}

	}

	private static final class BuildBySduIdHelper implements SduIdVisitor {

		private SduRepoLocation sduRepoLocation;
		private String location;

		private BuildBySduIdHelper() {
		}

		private SduRepoLocation getSduRepoLocation() {
			return sduRepoLocation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduIdVisitor#visit(com.sap.engine
		 * .services.dc.repo.SdaId)
		 */
		public void visit(SdaId sdaId) {
			location = LocationConstants.ROOT_REPO_DC
					+ LocationConstants.PATH_SEPARATOR + sdaId.toString();

			sduRepoLocation = new SdaRepoLocationImpl(location);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduIdVisitor#visit(com.sap.engine
		 * .services.dc.repo.ScaId)
		 */
		public void visit(ScaId scaId) {
			location = LocationConstants.ROOT_REPO_SC
					+ LocationConstants.PATH_SEPARATOR + scaId.toString();

			sduRepoLocation = new ScaRepoLocationImpl(location);
		}

	}

	private static final class BuildBySduHelper implements SduVisitor {

		private SduRepoLocation sduRepoLocation;
		private String location;

		private BuildBySduHelper() {
		}

		private SduRepoLocation getSduRepoLocation() {
			return sduRepoLocation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
		 * .dc.repo.Sda)
		 */
		public void visit(Sda sda) {
			location = LocationConstants.ROOT_REPO_DC
					+ LocationConstants.PATH_SEPARATOR + sda.getId().toString();

			sduRepoLocation = new SdaRepoLocationImpl(location, sda);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
		 * .dc.repo.Sca)
		 */
		public void visit(Sca sca) {
			location = LocationConstants.ROOT_REPO_SC
					+ LocationConstants.PATH_SEPARATOR + sca.getId().toString();

			sduRepoLocation = new ScaRepoLocationImpl(location, sca);
		}

	}

}

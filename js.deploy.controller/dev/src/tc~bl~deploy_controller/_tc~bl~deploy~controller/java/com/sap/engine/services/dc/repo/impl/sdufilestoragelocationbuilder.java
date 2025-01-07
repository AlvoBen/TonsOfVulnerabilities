package com.sap.engine.services.dc.repo.impl;

import java.io.File;

import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.SduFileStorageLocation;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduIdVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SduFileStorageLocationBuilder {

	private static SduFileStorageLocationBuilder INSTANCE = new SduFileStorageLocationBuilder();

	static SduFileStorageLocationBuilder getInstance() {
		return INSTANCE;
	}

	private SduFileStorageLocationBuilder() {
	}

	SduFileStorageLocation build(SduId sduId) {
		return build(sduId, null);
	}

	SduFileStorageLocation build(SduId sduId, File sduFile) {
		final BuildHelper buildHelper = new BuildHelper(sduFile);
		sduId.accept(buildHelper);

		return buildHelper.getSduStorageLocation();
	}

	private static final class BuildHelper implements SduIdVisitor {

		private SduFileStorageLocation sduStorageLocation;
		private File sduFile;
		private String location;

		private BuildHelper(File _sduFile) {
			sduFile = _sduFile;
		}

		SduFileStorageLocation getSduStorageLocation() {
			return sduStorageLocation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduIdVisitor#visit(com.sap.engine
		 * .services.dc.repo.SdaId)
		 */
		public void visit(SdaId sdaId) {
			location = LocationConstants.ROOT_STORAGE_DC
					+ LocationConstants.PATH_SEPARATOR + sdaId.toString();

			sduStorageLocation = new SdaFileStorageLocationImpl(location,
					sduFile);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.repo.SduIdVisitor#visit(com.sap.engine
		 * .services.dc.repo.ScaId)
		 */
		public void visit(ScaId scaId) {
			location = LocationConstants.ROOT_STORAGE_SC
					+ LocationConstants.PATH_SEPARATOR + scaId.toString();

			sduStorageLocation = new ScaFileStorageLocationImpl(location,
					sduFile);
		}

	}

}

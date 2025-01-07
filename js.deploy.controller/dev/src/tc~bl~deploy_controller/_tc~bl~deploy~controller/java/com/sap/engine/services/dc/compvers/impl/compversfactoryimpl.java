package com.sap.engine.services.dc.compvers.impl;

import javax.sql.DataSource;

import com.sap.engine.services.dc.cm.utils.db.DBPoolSystemDataSourceBuilder;
import com.sap.engine.services.dc.cm.utils.db.SystemDataSourceBuildingException;
import com.sap.engine.services.dc.compvers.CompVersException;
import com.sap.engine.services.dc.compvers.CompVersManager;
import com.sap.engine.services.dc.compvers.CompVersFactory;
import com.sap.engine.services.dc.compvers.CompVersSyncher;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.sl.util.cvers.api.CVersAccessException;
import com.sap.sl.util.cvers.api.CVersFactoryIF;
import com.sap.sl.util.cvers.api.CVersManagerIF;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class CompVersFactoryImpl extends CompVersFactory {

	private CVersFactoryIF cversFactory;

	public CompVersFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersFactory#createCompVersManager
	 * ()
	 */
	public CompVersManager createCompVersManager() throws CompVersException {
		// init DataSource
		final DataSource ds;
		try {
			ds = DBPoolSystemDataSourceBuilder.getInstance()
					.buildSystemDataSource();
		} catch (SystemDataSourceBuildingException sdsbe) {
			throw new CompVersException(DCExceptionConstants.ERROR_CREATING,
					new String[] { "system data source" }, sdsbe);
		}

		final CVersManagerIF cversManagerIF;
		try {
			cversManagerIF = this.getCVersFactory().createCVersManager(ds);
		} catch (CVersAccessException cvae) {
			throw new CompVersException(DCExceptionConstants.ERROR_CREATING,
					new String[] { "CVers Manager" }, cvae);
		}

		return new CompVersManagerImpl(cversManagerIF);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersFactory#createCompVersSyncher
	 * ()
	 */
	public CompVersSyncher createCompVersSyncher() {
		return new CompVersSyncherImpl();
	}

	private synchronized CVersFactoryIF getCVersFactory() {
		if (this.cversFactory == null) {
			this.cversFactory = CVersFactoryIF.getInstance();
		}

		return this.cversFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersFactory#createCompVersManager
	 * (javax.sql.DataSource)
	 */
	public CompVersManager createCompVersManager(DataSource ds)
			throws CompVersException {
		final CVersManagerIF cversManagerIF;
		try {
			cversManagerIF = this.getCVersFactory().createCVersManager(ds);
		} catch (CVersAccessException cvae) {
			throw new CompVersException(DCExceptionConstants.ERROR_CREATING,
					new String[] { "CVers Manager" }, cvae);
		}

		return new CompVersManagerImpl(cversManagerIF);
	}

}

package com.sap.engine.services.dc.util.readers.sdu_reader;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-26
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class SduReaderFactory {

	private static SduReaderFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.util.readers.sdu_reader.impl.SduReaderFactoryImpl";

	protected SduReaderFactory() {
	}

	public static synchronized SduReaderFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static SduReaderFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (SduReaderFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003390 An error occurred while creating an instance of "
					+ "class SduReaderFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract SduReader createSduReader();

}

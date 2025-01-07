package com.sap.engine.services.dc.compvers.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.compvers.CompVersException;
import com.sap.engine.services.dc.compvers.CompVersManager;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.sl.util.components.api.ComponentElementIF;
import com.sap.sl.util.components.xml.api.ComponentElementXMLizerException;
import com.sap.sl.util.components.xml.api.ComponentElementXMLizerFactoryIF;
import com.sap.sl.util.components.xml.api.ComponentElementXMLizerIF;
import com.sap.sl.util.cvers.api.CVersAccessException;
import com.sap.sl.util.cvers.api.CVersManagerIF;
import com.sap.tc.logging.Location;

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
final class CompVersManagerImpl implements CompVersManager {

	private Location location = DCLog.getLocation(this.getClass());
	
	private final CVersManagerIF cversManager;

	CompVersManagerImpl(CVersManagerIF cversManager) {
		this.cversManager = cversManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersManager#sduChanged(com.sap
	 * .engine.services.dc.repo.Sdu)
	 */
	public void sduChanged(Sdu sdu) throws CompVersException {
		final ComponentElementIF compElem = getComponentElement(sdu
				.getComponentElementXML());
		
		final String tagName = "CVers:writeCVers:" + sdu.getId();
	    Accounting.beginMeasure( tagName, cversManager.getClass());    
		try {
			this.cversManager.writeCVers(new ComponentElementIF[] { compElem });
			if (location.bePath()) {
				tracePath(location, 
						"SDU [{0}] was stored into the [{1}]", new Object[] {
								sdu.getId(), CompVersConstants.COMPVERS_NAME });
			}
		} catch (CVersAccessException cvae) {
			DCLog.logErrorThrowable(location, null,
					"An error occurred while storing the Sdu '" + sdu.getId()
							+ "' into the " + CompVersConstants.COMPVERS_NAME,
					cvae);

			throw new CompVersException(
					DCExceptionConstants.CVERS_ERROR_UPDATE, new String[] {
							CompVersConstants.COMPVERS_NAME,
							compElem.toString() }, cvae);
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersManager#sduUndeployed(com
	 * .sap.engine.services.dc.repo.Sdu)
	 */
	public void sduUndeployed(Sdu sdu) throws CompVersException {
		final ComponentElementIF compElem = getComponentElement(sdu
				.getComponentElementXML());
		
		final String tagName = "CVers:removeCVers:" + sdu.getId();
	    Accounting.beginMeasure(tagName, cversManager.getClass());
		try {
			this.cversManager
					.removeCVers(new ComponentElementIF[] { compElem });
			if (location.beDebug()) {
				traceDebug(
						location,
						"SDU [{0}] was removed from the [{1}]", new Object[] {
								sdu.getId(), CompVersConstants.COMPVERS_NAME });
			}
		} catch (CVersAccessException cvae) {
			DCLog.logErrorThrowable(location, null,
					"An error occurred while removing the Sdu '" + sdu.getId()
							+ "' from the " + CompVersConstants.COMPVERS_NAME,
					cvae);

			throw new CompVersException(
					DCExceptionConstants.CVERS_ERROR_REMOVE, new String[] {
							CompVersConstants.COMPVERS_NAME,
							compElem.toString() }, cvae);
		} finally {
			Accounting.endMeasure(tagName);
		}		
	}

	private ComponentElementIF getComponentElement(String componentElementXML)
			throws CompVersException {
		final ComponentElementXMLizerIF compElemXMLizer;
		final String tagName = "CVers:getComponentElement";
	    Accounting.beginMeasure(tagName, ComponentElementXMLizerFactoryIF.getInstance().getClass());
		try {
			compElemXMLizer = ComponentElementXMLizerFactoryIF.getInstance()
					.createComponentXMLizerElementFromXML(componentElementXML);
			return compElemXMLizer.getComponentElement();
		} catch (ComponentElementXMLizerException cexmle) {
			throw new CompVersException(DCExceptionConstants.CVERS_ERROR_INIT,
					new String[] { componentElementXML }, cexmle);
		} finally {
			Accounting.endMeasure(tagName);
		}		
	}

}

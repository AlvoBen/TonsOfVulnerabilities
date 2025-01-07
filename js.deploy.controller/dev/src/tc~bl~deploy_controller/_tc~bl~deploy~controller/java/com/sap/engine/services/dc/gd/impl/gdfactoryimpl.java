package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.DeliveryType;
import com.sap.engine.services.dc.gd.GenericDelivery;
import com.sap.engine.services.dc.gd.GDFactory;
import com.sap.engine.services.dc.gd.SyncDelivery;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-18
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class GDFactoryImpl extends GDFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.gd.GDFactory#createGD()
	 */
	public GenericDelivery createGenericDelivery(DeliveryType deliveryType)
			throws DeliveryException {
		GenericDelivery genericDelivery;
		if (deliveryType.equals(DeliveryType.ROLLING)) {
			genericDelivery = new RollingGenericDeliveryImpl();
		} else if (deliveryType.equals(DeliveryType.NORMAL)) {
			genericDelivery = new GenericDeliveryImpl();
		} else {
			throw new DeliveryException(
					DCExceptionConstants.WRONG_DELIVERY_TYPE,
					new String[] { deliveryType.toString() });
		}
		return genericDelivery;
	}

	@Override
	public SyncDelivery createSyncDelivery() {
		return new SyncDeliveryImpl();
	}

}

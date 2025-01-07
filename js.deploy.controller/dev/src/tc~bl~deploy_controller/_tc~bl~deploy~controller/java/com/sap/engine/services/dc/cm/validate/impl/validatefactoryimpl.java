/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.validate.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.cm.validate.ValidateFactory;
import com.sap.engine.services.dc.cm.validate.Validator;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class ValidateFactoryImpl extends ValidateFactory {

	public Validator createValidator(final String performerUserUniqueId)
			throws RemoteException {
		return new ValidatorImpl(performerUserUniqueId);
	}
}

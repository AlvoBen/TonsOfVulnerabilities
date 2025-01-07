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
package com.sap.engine.services.dc.api.validate;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.Batch;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationException;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The class consists of operations for validating <code>DeployItems</code>
 * (SDA/SCA) and <code>UndeployItems</code> passed as ordered list of batches.</DD>
 * <DT><B>Usage: </B></DT>
 * <DD>ComponentManager componentManager = client.getComponentManager();//gets
 * component manager</DD>
 * <DD>ValidateProcessor validateProcessor =
 * componentManager.getValidateProcessor();//creates new validate processor</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2007-23-10</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.ComponentManager#getValidateProcessor()
 */
public interface ValidateProcessor {
	/**
	 * The operation validates the specified list of batches with
	 * <code>DeployItems</code> or <code>UndeployItems</code> and returns a
	 * <code>ValidateResult</code> which specifies generally whether the items
	 * are successfully validate or not. The <code>ValidationStatus</code> of
	 * the returned <code>ValidateResult</code> is <code>SUCCESS</code> if all
	 * the items are admitted for deployment or undeployment and
	 * <code>ERROR</code> in other cases.
	 * 
	 * @param List
	 *            batchList with the <code>DeployItems []</code> or
	 *            <code>UndeployItems []</code> which have to be validated.
	 * @return <code>ValidateResult</code>
	 * @throws ValidationException
	 *             in case the validation could not be performed or there are
	 *             invalid archives and the <code>Validator</code>'s error
	 *             handling strategy is <code>ErrorStrategy.ON_ERROR_STOP</code>
	 *             . Additionally, the exception will be trown in case all the
	 *             archives are not admitted for deployment/undeployment,
	 *             regardless of the error handling strategy. Therefore, when
	 *             the error handling strategy is
	 *             <code>ErrorStrategy.ON_ERROR_STOP</code> throwing a
	 *             <code>ValidateException</code> could mean that the specified
	 *             archives are not correct;
	 * @throws TransportException
	 *             in case there are archives to validate that can not exists,
	 *             can not be read or can not be upload to the server.
	 * @throws ConnectionException
	 *             in case of connection error
	 * @throws APIException
	 *             in case there are problems with creating or getting the
	 *             validator from the server.
	 */
	public ValidationResultSet validate(Batch[] batchList)
			throws ConnectionException, ValidateException, TransportException,
			APIException;

}

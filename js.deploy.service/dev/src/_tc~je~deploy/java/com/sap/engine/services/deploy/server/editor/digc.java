/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.editor;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public interface DIGC extends VersionInterface {

	public abstract void delete(Configuration appsCfg, Configuration deployCfg)
			throws ConfigurationException;

	public abstract void deleteAppStatus(Configuration deployCfg,
			Configuration globalCfg) throws ConfigurationException;

}

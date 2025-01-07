﻿/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime.ejb.api;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;


public interface JXBPLocalHome extends EJBLocalHome {

    JXBPLocalBeanIntf create() throws CreateException;
}

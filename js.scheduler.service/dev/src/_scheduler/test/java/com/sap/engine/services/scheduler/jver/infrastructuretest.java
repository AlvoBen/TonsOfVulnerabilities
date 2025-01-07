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
package com.sap.engine.services.scheduler.jver;

import com.sap.tc.jtools.jver.framework.Test;
import com.sap.engine.services.scheduler.impl.SingletonScheduler;

public class InfrastructureTest extends Test {

    public void test_JVerIntegration() {
		verify(true, "JVer is not Integrated properly"); 
    }
}
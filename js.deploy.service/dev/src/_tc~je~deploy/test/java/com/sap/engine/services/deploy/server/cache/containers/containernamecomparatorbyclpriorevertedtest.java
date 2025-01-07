/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.cache.containers;

import java.util.Arrays;

import com.sap.engine.services.deploy.ear.jar.ContainerWrapperFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.ear.jar.ContainerInterfaceAdaptor;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;


/**
 *@author Luchesar Cekov
 */
public class ContainerNameComparatorByCLPrioRevertedTest extends TestCase {
  public void testCompare() throws Exception {
    Containers.getInstance().clear();
    
    for (int i = 0; i < 100; i++) {
    	int prio = (int)Math.round(Math.random() * 100);
    	ContainerWrapper ci = ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), 
    			Integer.toString(prio) + Math.random(), prio);
      Containers.getInstance().addContainer(ci.getContainerInfo().getName(), ci);
    }
    
    
    
    Assert.assertEquals(100, Containers.getInstance().size());
    
    String[] containerNames = new String[100];
    Containers.getInstance().getNames().toArray(containerNames);
    Arrays.sort(containerNames, ContainerNameComparatorByCLPrioReverted.instance);
    
    ContainerInterface previous = null;
    for (int i = 0; i < containerNames.length; i++) {
      ContainerInterface container = Containers.getInstance().getContainer(containerNames[i]);
      if (previous != null) {
        Assert.assertTrue(previous.getContainerInfo().getClassLoadPriority() >= container.getContainerInfo().getClassLoadPriority());
      }
      
      previous = container;
    }
  }
    
  
}

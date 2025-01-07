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

import com.sap.engine.services.deploy.ear.jar.ContainerWrapperFactory;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.ear.jar.ContainerInterfaceAdaptor;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;

import junit.framework.TestCase;


/**
 *@author Luchesar Cekov
 */
public class ContainersTest extends TestCase {  
  public void test() throws Exception {
    Containers containers = Containers.getInstance();
    containers.clear();
    ContainerWrapper ci0 = ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "container0", 1);
    containers.addContainer(ci0.getContainerInfo().getName(), ci0);
    ContainerWrapper ci1 = ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "container1", 1);
    containers.addContainer(ci1.getContainerInfo().getName(), ci1);
    ContainerWrapper ci2 = ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "container2", 1);
    containers.addContainer(ci2.getContainerInfo().getName(), ci2);
    ContainerWrapper ci3 = ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "container3", 1);
    containers.addContainer(ci3.getContainerInfo().getName(), ci3);
    ContainerWrapper ci4 = ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "container4", 1);
    containers.addContainer(ci4.getContainerInfo().getName(), ci4);        
    
    assertEquals(5, containers.size());
    assertSame(ci0, containers.getContainer(ci0.getContainerInfo().getName()));
    
    assertSame(ci1, containers.getContainer(ci1.getContainerInfo().getName()));
    assertSame(ci2, containers.getContainer(ci2.getContainerInfo().getName()));
    assertSame(ci3, containers.getContainer(ci3.getContainerInfo().getName()));
    assertSame(ci4, containers.getContainer(ci4.getContainerInfo().getName()));
    
    containers.remove(ci0.getContainerInfo().getName());
    assertNull(containers.getContainer(ci0.getContainerInfo().getName()));
    containers.remove(ci1.getContainerInfo().getName());
    assertNull(containers.getContainer(ci1.getContainerInfo().getName()));
    containers.remove(ci2.getContainerInfo().getName());
    assertNull(containers.getContainer(ci2.getContainerInfo().getName()));
    containers.remove(ci3.getContainerInfo().getName());
    assertNull(containers.getContainer(ci3.getContainerInfo().getName()));
    containers.remove(ci4.getContainerInfo().getName());
    assertNull(containers.getContainer(ci4.getContainerInfo().getName()));
  }
  
}

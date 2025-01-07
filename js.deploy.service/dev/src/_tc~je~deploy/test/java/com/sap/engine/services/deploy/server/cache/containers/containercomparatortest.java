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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

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
public class ContainerComparatorTest  extends TestCase {
  public void testCompare() throws Exception {
    ArrayList list = new ArrayList(100);
    for (int i = 0; i < 100; i++) {
      list.add(ContainerWrapperFactory.buildContainerWrapper( new ContainerInfo(), "", (int)Math.round(Math.random() * 100)));
    }
    
    Collections.sort(list, ContainerComparator.instance);
    
    Assert.assertEquals(100, list.size());
    ContainerInterface previous = null;
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      ContainerInterface container = (ContainerInterface) iter.next();
      if (previous != null) {
        Assert.assertTrue(previous.getContainerInfo().getPriority() <= container.getContainerInfo().getPriority());
      }
      
      previous = container;
    }
  }
  
  public void testCompareReverted() throws Exception {
    ArrayList list = new ArrayList();
    for (int i = 0; i < 100; i++) {
      list.add(ContainerWrapperFactory.buildContainerWrapper( new ContainerInfo(), "", (int)Math.round(Math.random() * 100)));
    }
    
    Collections.sort(list, ContainerComparatorReverted.instance);
    
    ContainerInterface previous = null;
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      ContainerInterface container = (ContainerInterface) iter.next();
      if (previous != null) {
        Assert.assertTrue(previous.getContainerInfo().getPriority() >= container.getContainerInfo().getPriority());
      }
      
      previous = container;
    }
  }
  
  public void testCompareToNull() throws Exception {    
    ContainerWrapper ci = ContainerWrapperFactory.buildContainerWrapper( new ContainerInfo(), "", 20);
    TreeSet treeset = new TreeSet(ContainerComparator.instance);
    treeset.add(ci);
    treeset.add(null);
    Iterator iter = treeset.iterator();
    Assert.assertSame(ci, iter.next());
    Assert.assertNull(iter.next());
  }
  
}

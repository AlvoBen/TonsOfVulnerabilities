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
package com.sap.engine.lib.refgraph.impl.util;

import com.sap.engine.lib.refgraph.impl.util.BooleanList;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Elena Yaneva
 */
public class BooleanListTest extends TestCase {

  private BooleanList list = new BooleanList();

  private void init(int size) {
    for (int i = 0; i < size; i++) {
      list.add(true);
    }
  }

  public void testSize() {
    init(100);
    Assert.assertEquals(100, list.size());
  }

  public void testGet() {
    init(5);
    try {
    list.get(5);
    Assert.fail("There should be illegalArgumentException, because the get metod is invoked with index, bigger or equal with the size of the list");
    }catch (IllegalArgumentException e) {
   Assert.assertTrue(e.getMessage().indexOf("index should be at most size-1") >0);
   System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$
    }
  }
  
}

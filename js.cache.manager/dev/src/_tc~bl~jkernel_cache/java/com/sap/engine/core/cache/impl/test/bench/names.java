/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class Names {
  
  public static String[] key = new String[1000];
  
  static {
    for (int i = 0; i < 1000; i++) {
      key[i] = "" + (i * 1000000 + (int)(Math.random() * 1000000));
    }
  }

}

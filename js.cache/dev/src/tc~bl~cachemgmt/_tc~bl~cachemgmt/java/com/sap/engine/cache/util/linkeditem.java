/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.util;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LinkedItem {
  
  Object value = null;
  LinkedItem prev = null;
  LinkedItem next = null;
  
  LinkedItem(Object value) {
    this.value = value;
  }

}

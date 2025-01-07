/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ContextList;

import java.util.Vector;

/**
 * @author Georgy Stanev
 * @version 4.0
 */
public class ContextListImpl extends ContextList {

  private Vector data;

  public ContextListImpl() {
    data = new Vector();
  }

  public int count() {
    return data.size();
  }

  public void add(String ctx) {
    data.addElement(ctx);
  }

  public String item(int index) throws Bounds {
    try {
      return (String) data.elementAt(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      String messageWithId = "ID019029: ContextList.item(" + index + "): index out of range";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ContextListImpl.item(int)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new Bounds(messageWithId);
    }
  }

  public void remove(int index) throws Bounds {
    try {
      data.removeElementAt(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      String messageWithId = "ID019030: ContextList.remove(" + index + "): index out of range";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ContextListImpl.remove(int)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new Bounds(messageWithId);
    }
  }

}


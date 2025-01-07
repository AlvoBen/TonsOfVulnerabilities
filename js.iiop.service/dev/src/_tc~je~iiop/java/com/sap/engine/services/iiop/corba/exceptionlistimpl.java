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
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;

import java.util.Vector;

/**
 * An object used in <code>RequestImpl</code> operations to
 * describe the exceptions that can be thrown by a method.  It maintains a
 * modifiable list of <code>TypeCode</code>s of the exceptions.
 * <P>
 * The following code fragment demonstrates creating
 * an <code>ExceptionList</code> object:
 * <PRE>
 *    ORB orb = ORB.init(args, null);
 *    org.omg.CORBA.ExceptionList excList = orb.create_exception_list();
 * </PRE>
 * The variable <code>excList</code> represents an <code>ExceptionList</code>
 * object with no <code>TypeCode</code> objects in it.
 * <P>
 * To add items to the list, you first create a <code>TypeCode</code> object
 * for the exception you want to include, using the <code>ORB</code> method
 * <code>create_exception_tc</code>.  Then you use the <code>ExceptionList</code>
 * method <code>add</code> to add it to the list.
 * The class <code>ExceptionListImpl</code> has a method for getting
 * the number of <code>TypeCode</code> objects in the list, and  after
 * items have been added, it is possible to call methods for accessing
 * or deleting an item at a designated index.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class ExceptionListImpl extends ExceptionList {

  private Vector data;

  public ExceptionListImpl() {
    data = new Vector();
  }

  public int count() {
    return data.size();
  }

  public void add(TypeCode tc) {
    data.addElement(tc);
  }

  public TypeCode item(int index) throws Bounds {
    try {
      return (TypeCode) data.elementAt(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      String messageWithId = "ID019031: ExceptionList.item(): index out of range";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ExceptionListImpl.item(int)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new Bounds(messageWithId);
    }
  }

  public void remove(int index) throws Bounds {
    try {
      data.removeElementAt(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      String messageWithId = "ID019032: ExceptionList.remove(): index out of range";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ExceptionListImpl.remove(int)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new Bounds(messageWithId);
    }
  }

}


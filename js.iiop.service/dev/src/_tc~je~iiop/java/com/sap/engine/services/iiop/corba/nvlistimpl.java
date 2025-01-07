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
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;

import java.util.Vector;

/**
 * A modifiable list containing <code>NamedValue</code> objects.
 * <P>
 * The class <code>NVList</code> is used as follows:
 * <UL>
 * <LI>to describe arguments for a <code>Request</code> object
 * in the Dynamic Invocation Interface and the Dynamic Skeleton Interface
 * <LI>to describe context values in a <code>Context</code> object
 * </UL>
 * <P>
 * Each <code>NamedValue</code> object consists of the following:
 * <UL>
 * <LI>a name, which is a <code>String</code> object
 * <LI>a value, as an <code>Any</code> object
 * <LI>an argument mode flag
 * </UL>
 * <P>
 * An <code>NVList</code> object may be created using one of the following
 * <code>ORB</code> methods:
 * <OL>
 * <LI><code>org.omg.CORBA.ORB.create_list</code>
 * <PRE>
 *    org.omg.CORBA.NVList nv = orb.create_list(3);
 * </PRE>
 * The variable <code>nv</code> represents a newly-created
 * <code>NVList</code> object.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class NVListImpl extends NVList {

  private Vector _list = null;
  private org.omg.CORBA.ORB orb;

  public NVListImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
    _list = new Vector();
  }

  public NamedValue add_item(String name, int flags) {
    NamedValue nv = new NamedValueImpl(orb, name, flags);
    _list.add(nv);
    return nv;
  }

  public NamedValue add_value(String name, Any value, int flags) {
    NamedValue nv = new NamedValueImpl(orb, name, value, flags);
    _list.add(nv);
    return nv;
  }

  public NamedValue add(int flags) {
    NamedValue nv = new NamedValueImpl(orb, flags);
    _list.add(nv);
    return nv;
  }

  public int count() {
    return _list.size();
  }

  public NamedValue item(int index) throws Bounds {
    try {
      NamedValue nv = (NamedValueImpl) _list.elementAt(index);
      return nv;
    } catch (ArrayIndexOutOfBoundsException e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("NVListImpl.item(int)", "Cannot access item with " + index + " index from name value list." +  LoggerConfigurator.exceptionTrace(e));
      }
      throw new Bounds("ID019033: " + e.getMessage());
    }
  }

  public void remove(int index) throws Bounds {
    try {
      _list.removeElementAt(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("NVListImpl.remove(int)", "Cannot remove item with " + index + " index from name value list." +  LoggerConfigurator.exceptionTrace(e));
      }
      throw new Bounds("ID019034: " + e.getMessage());
    }
  }

}// NVListImpl


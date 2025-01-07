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

import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;

/**
 * An object used in the DII and DSI to describe
 * arguments and return values. <code>NamedValue</code> objects
 * are also used in the <code>Context</code>
 * object routines to pass lists of property names and values.
 * <P>
 * A <code>NamedValue</code> object contains:
 * <UL>
 * <LI>a name -- If the <code>NamedValue</code> object is used to
 * describe arguments to a request, the name will be an argument
 * identifier specified in the OMG IDL interface definition
 * for the operation being described.
 * <LI>a value -- an <code>Any</code> object
 * <LI>an argument mode flag -- one of the following:
 *   <UL>
 *    <LI><code>ARG_IN.value</code>
 *    <LI><code>ARG_OUT.value</code>
 *    <LI><code>ARG_INOUT.value</code>
 *    <LI>zero -- if this <code>NamedValue</code> object represents a property
 *                in a <code>Context</code> object rather than a parameter or
 *                return value
 *   </UL>
 * </UL>
 * <P>
 * The class <code>NamedValue</code> has three methods, which
 * access its fields.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class NamedValueImpl extends NamedValue {

  private int flags;
  private String name;
  private Any value;
  private org.omg.CORBA.ORB orb;

  public NamedValueImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
    flags = 0;
    name = "";
    value = new AnyImpl(orb);
  }

  public NamedValueImpl(org.omg.CORBA.ORB orb0, int flags0) {
    this(orb0);
    flags = flags0;
    name = "";
    value = new AnyImpl(orb);
  }

  public NamedValueImpl(org.omg.CORBA.ORB orb0, String name0, int flags0) {
    this(orb0, flags0);
    name = name0;
    value = new AnyImpl(orb);
  }

  public NamedValueImpl(org.omg.CORBA.ORB orb0, String name0, Any value0, int flags0) {
    this(orb0, name0, flags0);
    value = value0;
  }

  public int flags() {
    return flags;
  }

  public String name() {
    return name;
  }

  public Any value() {
    return value;
  }

}// NamedValueImpl


/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.SendingContext;

import org.omg.CORBA.portable.ObjectImpl;
import org.omg.SendingContext.RunTime;

/**
 * @author Ivan Atanassov
 */
public class RunTimeHepler {
//  private static String _id = "IDL:omg.org/SendingContext/RunTime:1.0";


  public RunTimeHepler() {
  }

//  public static String id() {
//    return _id;
//  }

  public static RunTime narrow(org.omg.CORBA.Object obj) {
    if(obj == null) {
      return null;
    } else if(obj instanceof RunTime) {
      return (RunTime)obj;
//    } else if(!obj._is_a(id())) {
//      throw new BAD_PARAM();
    } else {
      org.omg.CORBA.portable.Delegate delegate1 = ((ObjectImpl)obj)._get_delegate();
      return new _RunTime_Stub(delegate1);
    }
  }
}

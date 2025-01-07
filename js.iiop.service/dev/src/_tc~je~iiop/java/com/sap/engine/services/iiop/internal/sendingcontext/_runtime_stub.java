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

/**
 * This class implements GIOP message header.
 * It is the base class of all GIOP messages available in CORBA 2.2 specification.
 * It also contains most of the constants used in GIOP protocol. This class is used
 * for parsing/creating GIOP message headers and reading/writing GIOP messages.
 *
 * @author Ivan Atanassov
 */
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.SendingContext.RunTime;

public class _RunTime_Stub extends ObjectImpl implements RunTime {
    private static String __ids[] = {"IDL:omg.org/SendingContext/RunTime:1.0", "IDL:omg.org/SendingContext/CodeBase:1.0"};

    public _RunTime_Stub() {
    }

    public String[] _ids() {
      return (String[])__ids.clone();
    }

    public _RunTime_Stub(Delegate delegate1) {
        _set_delegate(delegate1);
    }
}

/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime;

import com.sap.engine.session.callback.CallbackHandler;
import com.sap.engine.session.callback.Callback;
import com.sap.engine.session.callback.CallbackException;
import com.sap.engine.session.AbstractSessionHolder;
import com.sap.engine.session.SessionHolder;
import com.sap.engine.session.SessionNotFoundException;
import com.sap.engine.session.SessionException;
import com.sap.engine.session.trace.Trace;

/**
 * Author: Georgi-S
 * Date: 2005-4-15
 */
public class SessionInvalidationCallback implements CallbackHandler {

  public void handle(Callback callback) throws CallbackException {
    AbstractSessionHolder holder;
    try {
      holder = (AbstractSessionHolder) callback;
    } catch (ClassCastException cast) {
      throw new CallbackException("Unsupported callback object:" + callback);
    }

    try {
      holder.expire(true);
      holder.remove();
    } catch (SessionNotFoundException e) {
      Trace.logException(e);
    } catch (SessionException e) {
      Trace.logException(e);
    }

  }

  public String handlerName() {
    return SessionHolder.CALLBACK_HANDLER;
  }
}

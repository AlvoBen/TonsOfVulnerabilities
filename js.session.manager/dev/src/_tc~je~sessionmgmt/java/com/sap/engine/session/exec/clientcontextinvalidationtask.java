/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.exec;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class ClientContextInvalidationTask extends TimerTask {

  private ConcurrentHashMap<ClientContextImpl, Object> scheduledContexts = new ConcurrentHashMap<ClientContextImpl, Object>(5);
  
  void scheduleForInactivityCheck(ClientContextImpl context) {
    scheduledContexts.put(context, context.getClientId());
  }
  
  void removeScheduledContext(ClientContextImpl context) {
    scheduledContexts.remove(context);
  }

  public void run() {
    try {
      for(ClientContextImpl ctx : scheduledContexts.keySet()) {
      	if(ctx.getClientId() == null){
      		scheduledContexts.remove(ctx);
      	} else if (ctx.isContextExpired()) {
          scheduledContexts.remove(ctx);
          ctx.removeAllClientContextData();
        }
      }
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable t) {
      Location.getLocation(this).traceThrowableT(Severity.ERROR, "Error during client context cleanup.", t);
    }
  }
    
}

/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.trace;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class TraceManager {

  public static final String DOMAIN_TRACE = "session.trace.DomainTrace";
  public static final String SESSION_TRACE = "session.trace.SessionTrace";
  public static final String SESSION_INVALIDATION_TRACE = "session.trace.SessionInvalidationTrace";

  private static final Object mutex = new Object(); // Object on which to synchronize
  private static Map<String, Tracer> tracers = Collections.synchronizedMap(new HashMap<String, Tracer>());


  static {
    tracers.put(DOMAIN_TRACE, new Tracer(DOMAIN_TRACE));
    tracers.put(SESSION_TRACE, new Tracer(SESSION_TRACE));
    tracers.put(SESSION_INVALIDATION_TRACE, new Tracer(SESSION_INVALIDATION_TRACE));
  }

  public static Tracer getTracer(String name) {
    Tracer tracer = tracers.get(name);
    if (tracer == null) {
      synchronized (mutex) {
        if (!tracers.containsKey(name)) {
          tracer = new Tracer(name);
          tracers.put(name, tracer);
        } else {
          tracer = tracers.get(name);
        }
      }
    }
    return tracer;
  }

  public static boolean changeTrace(String tracer) {
    Tracer tr = tracers.get(tracer);
    if (tr != null) {
      tr.enable(!tr.enable);
      return tr.enable;
    } else {
      throw new IllegalArgumentException("No such tracer:" + tracer);
    }
  }

  public static void print() {
    for (Tracer trc : tracers.values()) {
      Trace.trace(trc.toString(new StringBuffer()).toString());
    }
  }



}

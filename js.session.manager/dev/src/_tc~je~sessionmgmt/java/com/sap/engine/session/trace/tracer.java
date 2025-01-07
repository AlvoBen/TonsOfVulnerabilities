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

import java.util.*;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class Tracer {

  private final Map<String, TraceRecorder> recorders = new HashMap<String, TraceRecorder>();
  private String name;
  public boolean enable;

  public Tracer(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  protected boolean enable(boolean flag) {
    return enable = flag;
  }

  public TraceRecorder getTraceRecorder(String name) {
    TraceRecorder recorder = recorders.get(name);
    if (recorder == null) {
      synchronized (recorders) {
        recorder = recorders.get(name);
        if (recorder == null) {
          recorder = new TraceRecorder(name);
          recorders.put(name, recorder);
        }
      }
    }
    return recorder;
  }

  public void removeRecorder(TraceRecorder recorder) {
    recorders.remove(recorder);
  }

  public String toString() {
    StringBuffer temp = new StringBuffer();
    return temp.toString();
  }

  public StringBuffer toString(StringBuffer buffer) {
    buffer.append(name);
    buffer.append("\n");
    for (TraceRecorder traceRecorder : recorders.values()) {
      (traceRecorder).toString(buffer);
    }
    buffer.append("\n");
    return buffer;
  }

}
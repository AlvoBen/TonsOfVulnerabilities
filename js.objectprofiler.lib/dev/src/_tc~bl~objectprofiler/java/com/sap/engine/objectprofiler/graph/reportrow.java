package com.sap.engine.objectprofiler.graph;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2005-12-14
 * Time: 12:58:45
 * To change this template use File | Settings | File Templates.
 */
public class ReportRow implements Comparable, Serializable {
  public ShareabilityDescription desc = null;
  public int weight = 0;
  public int objectCounter = 0;
  public int refCounter = 0;

  static final long serialVersionUID =  2098556424710910869L;

  public int compareTo(Object o) {
    ReportRow row = (ReportRow)o;

    return weight - row.weight;
  }
}

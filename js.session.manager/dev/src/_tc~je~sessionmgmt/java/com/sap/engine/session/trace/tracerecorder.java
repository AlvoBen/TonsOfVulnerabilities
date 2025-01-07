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

import java.util.List;
import java.util.AbstractList;
import java.util.Date;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class TraceRecorder  {

  private static final String st = "Trace recorder list is created";

  private final Object mutex = new Object(); // Object on which to synchronize
  private Record firstRecord;
  private Record lastRecord;

  private String name;
   private Date lastUpdate;

  protected TraceRecorder(String name) {
    this.name = name;
    new Record();
  }




  public final void addRecord(String rec) {
    Record record = new Record(rec);
    lastUpdate = record.date;
  }

  public final void addRecord(String rec, Throwable tr) {
    Record record = new Record(rec);
    lastUpdate = record.date;
  }
  public String getName() {
    return name;
  }

  public String toString() {
    StringBuffer temp = new StringBuffer();
    return toString(temp).toString();
  }



  public StringBuffer toString(StringBuffer buffer) {
    buffer.append(name);
    buffer.append(Record.nl);
    Record current = firstRecord;
    while (current != null) {
      buffer.append("  ");
      current.toString(buffer);
      current = current.next;
    }
    buffer.append("\n");
    return buffer;
  }

  private final class Record {

    public static final String nl = "\n";
    public static final String s = "|";
    public static final String tab = "\tat ";
    // linked list implementation
    Record next;

    String record;
    Throwable tr;
    Date date;

    protected Record() {
      StringBuffer temp  = new StringBuffer (st);
      temp.append(name);
      this.record = temp.toString();
      this.date = new Date(System.currentTimeMillis());
      lastRecord = this;
      firstRecord = this;
    }

    public Record(String record) {
      this(record, null);

    }

    public Record(String record, Throwable tr) {
      this.record = record;
      this.tr = tr;
      this.date = new Date(System.currentTimeMillis());
      synchronized (mutex) {
        lastRecord.next = this;
        lastRecord = this;
      }
    }

    //Record format: "date|record msg"
    public String toString() {
     return toString(new StringBuffer()).toString();
    }

    public StringBuffer toString(StringBuffer buffer) {
      buffer.append(date);
      buffer.append(Record.s);
      buffer.append(record);
      if (tr != null) {
        buffer.append(Record.nl);
        buffer.append(tr.toString());
        StackTraceElement stack[] = tr.getStackTrace();
        for (int i = 0; i < stack.length; i++) {
          buffer.append(Record.nl);
          buffer.append(Record.tab);
          buffer.append(stack[i]);
        }
      }
      return buffer.append("\n");
    }

  }


}

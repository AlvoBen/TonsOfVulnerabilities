package com.sap.engine.cache.util.dump;

import java.io.*;
import java.util.Date;

public class DumpEntity {

  private PrintWriter writer = null;
  static DumpEntity stdEntity = null;

  DumpEntity(String fileName) {
    try {
      fileName += " (" + (new Date()).toString() + ")";
      fileName = fileName.replace(':', '_');
      File file = (new File(fileName)).getCanonicalFile();
      file.getParentFile().mkdirs();
      writer = new PrintWriter(new FileOutputStream(file), false);
    } catch (IOException e) {
      LogUtil.logT(e);
      writer = null;
    }
  }

  void write(String s) {
    if (writer != null) {
      synchronized (this) {
        writer.print("[");
        writer.print(new Date().toString());
        writer.print("] : ");
        writer.println(s);
        writer.flush();
      }
    }
  }

  private static PrintStream standardOut = System.out; //$JL-SYS_OUT_ERR$
  
  static void writeStd(String s) {
    if (stdEntity == null) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[");
      buffer.append(new Date().toString());
      buffer.append("] : ");
      buffer.append(s);
      standardOut.println(buffer);
    } else {
      stdEntity.write(s);
    }
  }

}

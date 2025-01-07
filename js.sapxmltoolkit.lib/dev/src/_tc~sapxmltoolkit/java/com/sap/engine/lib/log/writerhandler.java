package com.sap.engine.lib.log;

import java.io.Writer;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.sap.engine.lib.xml.util.NestedRuntimeException;

public class WriterHandler extends Handler {
  
  private Writer writer;
  
  WriterHandler(Writer writer, Formatter formatter) {
    this.writer = writer;
    setFormatter(formatter);
  }
  
  public void publish(LogRecord record) {
    String message = getFormatter().format(record);
    try {
      writer.write(message);
    } catch(Exception exc) {
      throw new NestedRuntimeException(exc);
    }
  }

  public void flush() {
    try {
      writer.flush();
    } catch(Exception exc) {
      throw new NestedRuntimeException(exc);
    }
  }

  public void close() throws SecurityException {
    try {
      writer.close();
    } catch(Exception exc) {
      throw new NestedRuntimeException(exc);
    }
  }
}

package com.sap.engine.lib.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
  
  LogFormatter() {
    super();
  }
  
  public String format(LogRecord record) {
    return(formatMessage(record));
  }
}

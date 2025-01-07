package com.sap.engine.lib.log;

import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.xml.sax.SAXParseException;

public class LogWriter {
  
  private static final LogWriter systemLogWriter = new LogWriter();
  
  private Logger logger;
  
  private LogWriter() {
    ConsoleHandler consoleHandler = new ConsoleHandler(); 
    consoleHandler.setFormatter(new LogFormatter());
    initLogger(consoleHandler);
  }
  
  private LogWriter(OutputStream outputStream) {
    initLogger(new StreamHandler(outputStream, new LogFormatter()));
  }
  
  private LogWriter(Writer writer) {
    initLogger(new WriterHandler(writer, new LogFormatter()));
  }
  
  private void initLogger(Handler handler) {
    logger = Logger.getAnonymousLogger();
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
  }
  
  public static LogWriter getSystemLogWriter() {
    return(systemLogWriter);
  }
  
  public static LogWriter newLogWriter(OutputStream outputStream) {
    return(new LogWriter(outputStream));
  }
  
  public static LogWriter newLogWriter(Writer writer) {
    return(new LogWriter(writer));
  }
  
  public void print(String message) {
    print(message, false);
  }
  
  public void println(String message) {
    print(message, true);
  }
  
  private void print(String message, boolean appendNewLine) {
    logger.severe(message);
    if(appendNewLine) {
      logger.severe("\n");
    }
    Handler handler = logger.getHandlers()[0]; 
    handler.flush();
  }
  
  public static String createExceptionRepresentation(Throwable thr) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(thr.getClass().getName());
    String message = thr.getMessage();
    if(message != null) {
      buffer.append(": ");
      buffer.append(message);
    }
    
    if(thr instanceof SAXParseException) {
      SAXParseException saxParseException = (SAXParseException)thr;
      int line = saxParseException.getLineNumber();
      if(line >= 0) {
        String systemId = saxParseException.getSystemId();
        if(systemId == null) {
          systemId = ":main:";
        }
        buffer.append("(");
        buffer.append(systemId);
        buffer.append(", ");
        buffer.append("row=");
        buffer.append(line);
        buffer.append(", ");
        buffer.append("col=");
        buffer.append(saxParseException.getColumnNumber());
        buffer.append(")");
      }
    }
    
    Throwable cause = thr.getCause();
    if(cause != null) {
      buffer.append(" -> ");
      buffer.append(cause.toString());
    }
    return(buffer.toString());
  }
}

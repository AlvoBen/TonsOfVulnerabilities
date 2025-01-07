package com.sap.engine.lib.xml.parser;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * Class description - thrown on parser error or bad xml
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 */
public class ParserException extends Exception {
  
  private int row;
  private int col;
  private String sourceID;
  private Throwable cause;

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public String getSourceID() {
    return sourceID;
  }

  protected String msg;

  public ParserException(Throwable cause) {
    super(cause.toString());
    this.cause = cause;
    this.row = -1;
    this.col = -1;
    this.msg = null;
  }

  public ParserException(String msg, Throwable cause) {
    super(msg);
    this.cause = cause;
    this.row = -1;
    this.col = -1;
    this.msg = msg;
  }

  public ParserException(String s, int row, int col) {
    super(s);
    this.row = row;
    this.col = col - 1;
    msg = s;
  }

  public ParserException(String msg, CharArray sourceID, int row, int col) {
    super(msg);
    this.row = row;
    this.col = col - 1;
    this.msg = msg;
    this.sourceID = sourceID.toString();
  }
  
  public Throwable getCause() {
    return cause; 
  }

  public String getMessage() {
    if (sourceID != null) {
      return msg + " (" + sourceID + ", row:" + row + ", col:" + col + ")";
    }
    return msg + " (row:" + row + ", col:" + col + ")";
  }
  
  public String toString() {
    return(LogWriter.createExceptionRepresentation(this));
  }
}


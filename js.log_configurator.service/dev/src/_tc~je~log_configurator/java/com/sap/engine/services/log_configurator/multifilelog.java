package com.sap.engine.services.log_configurator;

import java.io.IOException;
import java.util.Enumeration;

import com.sap.engine.lib.util.HashMapIntObject;
import com.sap.engine.lib.util.ArrayInt;
import com.sap.tc.logging.*;

/**
 * This class provide fast logging in many files, each one of them replied to a one severity level.  
 * 
 * @author Nikola Marchev
 * @version 6.30
 * @deprecated
 */ 
public class MultiFileLog extends Log {

  /** This hashmap conains the inner filelogs */
  private HashMapIntObject logs;
  /** The directory for archiving */
  private String zipDirectory = null;

  /**
   * @param zipDirectory the directory for archiving
   */ 
  public MultiFileLog(String zipDirectory) {
    this.zipDirectory = zipDirectory;
    removeFilters();
    setEffectiveSeverity( Severity.ALL );
    logs = new HashMapIntObject();
  }

  // from Log
  /**
   * Do nothing in this implementation
   * 
   * @return null
   */ 
  public String getEncoding() {
    return null;
  }
  /**
   * Do nothing in this implementation
   */ 
  public void setEncoding( String encoding ) {
  }
  /**
   * Do nothing in this implementation
   */ 
  protected void closeInt() throws IOException {
  }
  /**
   * Do nothing in this implementation
   */ 
  protected void flushInt() throws IOException {
  }
  /**
   * Do nothing in this implementation
   */ 
  protected void writeInt(String s) throws IOException {
  }
  /**
   * Do nothing in this implementation
   */ 
  public void close() {
  }
  
  /**
   * Flushs all inner logs
   */ 
  public void flush() {
    Enumeration e = logs.elements();
    while ( e.hasMoreElements() ) {
      ((Log) e.nextElement()).flush();
    }
  }

  /**
   * Write a log into one of log files in dependence of severity
   * 
   * @param record the log record that must be logged
   * @return The log record that been logged
   */ 
  public LogRecord write( LogRecord record ) {
    Log log = (Log) logs.get( record.getSeverity() % 100);

    if ( log == null ) {
      return null;
    }

    return log.write( record );
  }

  /**
   * Adds a log. If the log with this level exist, it will be replaced with the new log.
   * 
   * @param level level for this log
   * @param log the log that must be added
   */ 
  public void addLog( int level, Log log ) {
    if ( log != null ) {
      logs.put( level, log );
    }
  }

  /**
   * Returns the log that correspond with specific level
   * 
   * @param level the correspond level for the log
   * @return The log that correspond with specific level
   */ 
  public Log getLog( int level ) {
    return (Log) logs.get( level );
  }

  /**
   * Returns the log that correspond with specific level, catsted to FileLog 
   * 
   * @param level the correspond level for the log
   * @return The log that correspond with specific level, catsed to FileLog
   */ 
  public FileLog getFileLog( int level ) {
    return (FileLog) logs.get( level );
  }

  /**
   * Remove the log that corresponds with specific level
   * 
   * @param level the correspond level for the log
   */ 
  public void removeLog( int level ) {
    logs.remove( level );
  }

  /**
   * Remove all inner logs
   */ 
  public void removeAllLogs() {
    logs.clear();
  }

  /**
   * Returns the dirctory for archiving
   * 
   * @return The dirctory for archiving
   */ 
  public String getZipDirectory() {
    return zipDirectory;
  }
  
  /**
   * Remove specific FileLog. 
   * 
   * @param fileLog the FileLog
   * @return all severities corresponding with this FileLog 
   */ 
  public int[] removeFileLogLevels( FileLog fileLog ) {
    ArrayInt result = new ArrayInt();
    int[] levels = logs.getAllKeys();
    for ( int i = 0; i < levels.length; i ++ ) {
      if ( ((FileLog) logs.get( levels[ i ] )).equals( fileLog ) ) {
        result.addElement( levels[ i ] );
        removeLog( levels[ i ] );
      }
    }
    if ( result.size() == 0 ) {
      return null;
    }
    return result.toArray();
  }

  public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException(); 
  }  
  
}
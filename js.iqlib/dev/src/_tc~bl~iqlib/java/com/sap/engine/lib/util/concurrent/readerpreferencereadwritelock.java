package com.sap.engine.lib.util.concurrent;

public class ReaderPreferenceReadWriteLock extends WriterPreferenceReadWriteLock {

  protected boolean allowReader() {
    return activeWriterThread == null;
  }

}


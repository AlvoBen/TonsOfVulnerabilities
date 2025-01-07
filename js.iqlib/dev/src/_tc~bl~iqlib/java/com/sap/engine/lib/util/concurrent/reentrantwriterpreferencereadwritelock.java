package com.sap.engine.lib.util.concurrent;

import com.sap.engine.lib.util.HashMapObjectIntPositive;

public class ReentrantWriterPreferenceReadWriteLock extends WriterPreferenceReadWriteLock {

  /**
   * Number of acquires on write lock by activeWriter_ thread
   */
  protected long writeHoldsCount = 0;
  /**
   * Number of acquires on read lock by any reader thread
   */
  protected HashMapObjectIntPositive readersHash = new HashMapObjectIntPositive();

  protected boolean allowReader() {
    return (activeWriterThread == null && waitingWritersCount == 0) || activeWriterThread == Thread.currentThread();
  }

  protected synchronized boolean startRead() {
    Thread t = Thread.currentThread();
    //    Object c = readersHash.get(t);
    int c = readersHash.get(t);

    if (c != -1) { // already held -- just increment hold count
      readersHash.put(t, c + 1);
      ++activeReadersCount;
      return true;
    } else if (allowReader()) {
      readersHash.put(t, 1);
      ++activeReadersCount;
      return true;
    } else {
      return false;
    }
  }

  protected synchronized boolean startWrite() {
    if (activeWriterThread == Thread.currentThread()) { // already held; re-acquire
      ++writeHoldsCount;
      return true;
    } else if (writeHoldsCount == 0) {
      if (activeReadersCount == 0 || (readersHash.size() == 1 && readersHash.get(Thread.currentThread()) != -1)) {
        activeWriterThread = Thread.currentThread();
        writeHoldsCount = 1;
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  protected synchronized Signaller endRead() {
    --activeReadersCount;
    Thread t = Thread.currentThread();
    int c = readersHash.get(t);

    if (c != 1) { // more than one hold; decrement count
      readersHash.put(t, c - 1);
      return null;
    } else {
      readersHash.remove(t);
      if (writeHoldsCount > 0) // a write lock is still held by current thread
      {
        return null;
      } else if (activeReadersCount == 0 && waitingWritersCount > 0) {
        return writerLock;
      } else {
        return null;
      }
    }
  }

  protected synchronized Signaller endWrite() {
    --writeHoldsCount;

    if (writeHoldsCount > 0) // still being held
    {
      return null;
    } else {
      activeWriterThread = null;
      if (waitingReadersCount > 0 && allowReader()) {
        return readerLock;
      } else if (waitingWritersCount > 0) {
        return writerLock;
      } else {
        return null;
      }
    }
  }

}


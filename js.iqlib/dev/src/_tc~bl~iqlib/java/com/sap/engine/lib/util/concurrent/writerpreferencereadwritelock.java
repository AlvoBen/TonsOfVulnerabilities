package com.sap.engine.lib.util.concurrent;

public class WriterPreferenceReadWriteLock implements ReadWriteLock {

  protected long activeReadersCount = 0;
  protected Thread activeWriterThread = null;
  protected long waitingReadersCount = 0;
  protected long waitingWritersCount = 0;
  protected final ReaderLock readerLock = new ReaderLock();
  protected final WriterLock writerLock = new WriterLock();

  public Sync writeLock() {
    return writerLock;
  }

  public Sync readLock() {
    return readerLock;
  }

  /*
   A bunch of small synchronized methods are needed
   to allow communication from the Lock objects
   back to this object, that serves as controller
   */
  protected synchronized void cancelledWaitingReader() {
    --waitingReadersCount;
  }

  protected synchronized void cancelledWaitingWriter() {
    --waitingWritersCount;
  }

  /**
   * Override this method to change to reader preference
   */
  protected boolean allowReader() {
    return activeWriterThread == null && waitingWritersCount == 0;
  }

  protected synchronized boolean startRead() {
    boolean allowRead = allowReader();
    if (allowRead) {
      ++activeReadersCount;
    }
    return allowRead;
  }

  protected synchronized boolean startWrite() {
    // The allowWrite expression cannot be modified without
    // also changing startWrite, so is hard-wired
    boolean allowWrite = (activeWriterThread == null && activeReadersCount == 0);
    if (allowWrite) {
      activeWriterThread = Thread.currentThread();
    }
    return allowWrite;
  }

  /*
   Each of these variants is needed to maintain atomicity
   of wait counts during wait loops. They could be
   made faster by manually inlining each other. We hope that
   compilers do this for us though.
   */
  protected synchronized boolean startReadFromNewReader() {
    boolean pass = startRead();
    if (!pass) {
      ++waitingReadersCount;
    }
    return pass;
  }

  protected synchronized boolean startWriteFromNewWriter() {
    boolean pass = startWrite();
    if (!pass) {
      ++waitingWritersCount;
    }
    return pass;
  }

  protected synchronized boolean startReadFromWaitingReader() {
    boolean pass = startRead();
    if (pass) {
      --waitingReadersCount;
    }
    return pass;
  }

  protected synchronized boolean startWriteFromWaitingWriter() {
    boolean pass = startWrite();
    if (pass) {
      --waitingWritersCount;
    }
    return pass;
  }

  /**
   * Called upon termination of a read.
   * Returns the object to signal to wake up a waiter, or null if no such
   */
  protected synchronized Signaller endRead() {
    if (--activeReadersCount == 0 && waitingWritersCount > 0) {
      return writerLock;
    } else {
      return null;
    }
  }

  /**
   * Called upon termination of a write.
   * Returns the object to signal to wake up a waiter, or null if no such
   */
  protected synchronized Signaller endWrite() {
    activeWriterThread = null;
    if (waitingReadersCount > 0 && allowReader()) {
      return readerLock;
    } else if (waitingWritersCount > 0) {
      return writerLock;
    } else {
      return null;
    }
  }

  /**
   * Reader and Writer requests are maintained in two different
   * wait sets, by two different objects. These objects do not
   * know whether the wait sets need notification since they
   * don't know preference rules. So, each supports a
   * method that can be selected by main controlling object
   * to perform the notifications.  This base class simplifies mechanics.
   */
  protected abstract class Signaller {

    // base for ReaderLock and WriterLock
    abstract void signalWaiters();

  }

  protected class ReaderLock extends Signaller implements Sync {

    public void acquire() throws InterruptedException {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      InterruptedException ie = null;
      synchronized (this) {
        if (!startReadFromNewReader()) {
          for (;;) {
            try {
              ReaderLock.this.wait();
              if (startReadFromWaitingReader()) {
                return;
              }
            } catch (InterruptedException ex) {
              cancelledWaitingReader();
              ie = ex;
              break;
            }
          } 
        }
      }

      if (ie != null) {
        // fall through outside synch on interrupt.
        // This notification is not really needed here, 
        //   but may be in plausible subclasses
        writerLock.signalWaiters();
        throw ie;
      }
    }

    public void release() {
      Signaller s = endRead();
      if (s != null) {
        s.signalWaiters();
      }
    }

    synchronized void signalWaiters() {
      ReaderLock.this.notifyAll();
    }

    public boolean attempt(long msecs) throws InterruptedException {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      InterruptedException ie = null;
      synchronized (this) {
        if (msecs <= 0) {
          return startRead();
        } else if (startReadFromNewReader()) {
          return true;
        } else {
          long waitTime = msecs;
          long start = System.currentTimeMillis();

          for (;;) {
            try {
              ReaderLock.this.wait(waitTime);
            } catch (InterruptedException ex) {
              cancelledWaitingReader();
              ie = ex;
              break;
            }

            if (startReadFromWaitingReader()) {
              return true;
            } else {
              waitTime = msecs - (System.currentTimeMillis() - start);

              if (waitTime <= 0) {
                cancelledWaitingReader();
                break;
              }
            }
          } 
        }
      }
      // safeguard on interrupt or timeout:
      writerLock.signalWaiters();
      if (ie != null) {
        throw ie;
      } else {
        return false; // timed out

      }
    }

  }

  protected class WriterLock extends Signaller implements Sync {

    public void acquire() throws InterruptedException {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      InterruptedException ie = null;
      synchronized (this) {
        if (!startWriteFromNewWriter()) {
          for (;;) {
            try {
              WriterLock.this.wait();
              if (startWriteFromWaitingWriter()) {
                return;
              }
            } catch (InterruptedException ex) {
              cancelledWaitingWriter();
              WriterLock.this.notify();
              ie = ex;
              break;
            }
          } 
        }
      }

      if (ie != null) {
        // Fall through outside synch on interrupt.
        //  On exception, we may need to signal readers.
        //  It is not worth checking here whether it is strictly necessary.
        readerLock.signalWaiters();
        throw ie;
      }
    }

    public void release() {
      Signaller s = endWrite();
      if (s != null) {
        s.signalWaiters();
      }
    }

    synchronized void signalWaiters() {
      WriterLock.this.notify();
    }

    public boolean attempt(long msecs) throws InterruptedException {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      InterruptedException ie = null;
      synchronized (this) {
        if (msecs <= 0) {
          return startWrite();
        } else if (startWriteFromNewWriter()) {
          return true;
        } else {
          long waitTime = msecs;
          long start = System.currentTimeMillis();

          for (;;) {
            try {
              WriterLock.this.wait(waitTime);
            } catch (InterruptedException ex) {
              cancelledWaitingWriter();
              WriterLock.this.notify();
              ie = ex;
              break;
            }

            if (startWriteFromWaitingWriter()) {
              return true;
            } else {
              waitTime = msecs - (System.currentTimeMillis() - start);

              if (waitTime <= 0) {
                cancelledWaitingWriter();
                WriterLock.this.notify();
                break;
              }
            }
          } 
        }
      }
      readerLock.signalWaiters();
      if (ie != null) {
        throw ie;
      } else {
        return false; // timed out

      }
    }

  }

}


package com.sap.engine.lib.util.concurrent;

public class CyclicBarrier implements Barrier {

  protected final int initialParties;
  protected boolean isBroken = false;
  protected Runnable barrierRunnableCommand = null;
  protected int partiesCount; // number of parties still waiting
  protected int resetsCount = 0; // incremented on each release

  /**
   * Create a CyclicBarrier for the indicated number of parties,
   * and no command to run at each barrier.
   * @exception IllegalArgumentException if parties less than or equal to zero.
   */
  public CyclicBarrier(int parties) {
    this(parties, null);
  }

  /**
   * Create a CyclicBarrier for the indicated number of parties.
   * and the given command to run at each barrier point.
   * @exception IllegalArgumentException if parties less than or equal to zero.
   */
  public CyclicBarrier(int parties, Runnable command) {
    if (parties <= 0) {
      throw new IllegalArgumentException();
    }
    initialParties = parties;
    partiesCount = parties;
    barrierRunnableCommand = command;
  }

  /**
   * Set the command to run at the point at which all threads reach the
   * barrier. This command is run exactly once, by the thread
   * that trips the barrier. The command is not run if the barrier is
   * broken.
   * @param command the command to run. If null, no command is run.
   * @return the previous command
   */
  public synchronized Runnable setBarrierCommand(Runnable command) {
    Runnable old = barrierRunnableCommand;
    barrierRunnableCommand = command;
    return old;
  }

  public synchronized boolean broken() {
    return isBroken;
  }

  /**
   * Reset to initial state. Clears both the broken status
   * and any record of waiting threads, and releases all
   * currently waiting threads with indeterminate return status.
   * This method is intended only for use in recovery actions
   * in which it is somehow known
   * that no thread could possibly be relying on the
   * the synchronization properties of this barrier.
   */
  public synchronized void restart() {
    isBroken = false;
    ++resetsCount;
    partiesCount = initialParties;
    notifyAll();
  }

  public int parties() {
    return initialParties;
  }

  /**
   * Enter barrier and wait for the other parties()-1 threads.
   * @return the arrival index: the number of other parties
   * that were still waiting
   * upon entry. This is a unique value from zero to parties()-1.
   * If it is zero, then the current
   * thread was the last party to hit barrier point
   * and so was responsible for releasing the others.
   * @exception BrokenBarrierException if any other thread
   * in any previous or current barrier
   * since either creation or the last restart
   * operation left the barrier
   * prematurely due to interruption or time-out. (If so,
   * the broken status is also set.)
   * Threads that are noticied to have been
   * interrupted <em>after</em> being released are not considered
   * to have broken the barrier.
   * In all cases, the interruption
   * status of the current thread is preserved, so can be tested
   * by checking Thread.interrupted.
   * @exception InterruptedException if this thread was interrupted
   * during the barrier, and was the one causing breakage.
   * If so, broken status is also set.
   */
  public int barrier() throws InterruptedException, BrokenBarrierException {
    return doBarrier(false, 0);
  }

  /**
   * Enter barrier and wait at most msecs for the other parties()-1 threads.
   * @return if not timed out, the arrival index: the number of other parties
   * that were still waiting
   * upon entry. This is a unique value from zero to parties()-1.
   * If it is zero, then the current
   * thread was the last party to hit barrier point
   * and so was responsible for releasing the others.
   * @exception BrokenBarrierException
   * if any other thread
   * in any previous or current barrier
   * since either creation or the last restart
   * operation left the barrier
   * prematurely due to interruption or time-out. (If so,
   * the broken status is also set.)
   * Threads that are noticed to have been
   * interrupted <em>after</em> being released are not considered
   * to have broken the barrier.
   * In all cases, the interruption
   * status of the current thread is preserved, so can be tested
   * by checking Thread.interrupted.
   * @exception InterruptedException if this thread was interrupted
   * during the barrier. If so, broken status is also set.
   * @exception TimeoutException if this thread timed out waiting for
   *  the barrier. If the timeout occured while already in the
   * barrier, broken status is also set.
   */
  public int attemptBarrier(long msecs) throws InterruptedException, TimeoutException, BrokenBarrierException {
    return doBarrier(true, msecs);
  }

  protected synchronized int doBarrier(boolean timed, long msecs) throws InterruptedException, com.sap.engine.lib.util.concurrent.TimeoutException, BrokenBarrierException {
    int index = --partiesCount;

    if (isBroken) {
      throw new BrokenBarrierException(index);
    } else if (Thread.interrupted()) {
      isBroken = true;
      notifyAll();
      throw new InterruptedException();
    } else if (index == 0) { // tripped
      partiesCount = initialParties;
      ++resetsCount;
      notifyAll();
      try {
        if (barrierRunnableCommand != null) {
          barrierRunnableCommand.run();
        }
        return 0;
      } catch (RuntimeException ex) {
        isBroken = true;
        return 0;
      }
    } else if (timed && msecs <= 0) {
      isBroken = true;
      notifyAll();
      throw new com.sap.engine.lib.util.concurrent.TimeoutException(msecs);
    } else { // wait until next reset
      int r = resetsCount;
      long startTime = (timed) ? System.currentTimeMillis() : 0;
      long waitTime = msecs;

      for (;;) {
        try {
          wait(waitTime);
        } catch (InterruptedException ex) {
          // Only claim that broken if interrupted before reset
          if (resetsCount == r) {
            isBroken = true;
            notifyAll();
            throw ex;
          } else {
            Thread.currentThread().interrupt(); // propagate
          }
        }

        if (isBroken) {
          throw new BrokenBarrierException(index);
        } else if (r != resetsCount) {
          return index;
        } else if (timed) {
          waitTime = msecs - (System.currentTimeMillis() - startTime);

          if (waitTime <= 0) {
            isBroken = true;
            notifyAll();
            throw new com.sap.engine.lib.util.concurrent.TimeoutException(msecs);
          }
        }
      } 
    }
  }

}


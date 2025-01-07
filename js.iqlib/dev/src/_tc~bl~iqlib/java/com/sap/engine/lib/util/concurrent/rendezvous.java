package com.sap.engine.lib.util.concurrent;

public class Rendezvous implements Barrier {

  /**
   * Interface for functions run at rendezvous points
   */
  public interface RendezvousFunction {

    /**
     * Perform some function on the objects presented at
     * a rendezvous. The objects array holds all presented
     * items; one per thread. Its length is the number of parties.
     * The array is ordered by arrival into the rendezvous.
     * So, the last element (at objects[objects.length-1])
     * is guaranteed to have been presented by the thread performing
     * this function. No identifying information is
     * otherwise kept about which thread presented which item.
     * If you need to
     * trace origins, you will need to use an item type for rendezvous
     * that includes identifying information. After return of this
     * function, other threads are released, and each returns with
     * the item with the same index as the one it presented.
     */
    public void rendezvousFunction(Object[] objects);

  }

  /**
   * The default rendezvous function. Rotates the array
   * so that each thread returns an item presented by some
   * other thread (or itself, if parties is 1).
   */
  public static class Rotator implements RendezvousFunction {

    /** Rotate the array  */
    public void rendezvousFunction(Object[] objects) {
      int lastIdx = objects.length - 1;
      Object first = objects[0];
      for (int i = 0; i < lastIdx; ++i) {
        objects[i] = objects[i + 1]; 
      }
      objects[lastIdx] = first;
    }

  }

  protected final int partiesCount;
  protected boolean isBroken = false;
  /**
   * Number of threads that have entered rendezvous
   */
  protected int entriesCount = 0;
  /**
   * Number of threads that are permitted to depart rendezvous
   */
  protected long departuresCount = 0;
  /**
   * Incoming threads pile up on entry until last set done.
   */
  protected final Semaphore entryGateSync;
  /**
   * Temporary holder for items in exchange
   */
  protected final Object[] slotsArray;
  /**
   * The function to run at rendezvous point
   */
  protected RendezvousFunction rendezvousFunction;

  /**
   * Create a Barrier for the indicated number of parties,
   * and the default Rotator function to run at each barrier point.
   * @exception IllegalArgumentException if parties less than or equal to zero.
   */
  public Rendezvous(int parties) {
    this(parties, new Rotator());
  }

  /**
   * Create a Barrier for the indicated number of parties.
   * and the given function to run at each barrier point.
   * @exception IllegalArgumentException if parties less than or equal to zero.
   */
  public Rendezvous(int parties, RendezvousFunction function) {
    if (parties <= 0) {
      throw new IllegalArgumentException();
    }
    partiesCount = parties;
    rendezvousFunction = function;
    entryGateSync = new WaiterPreferenceSemaphore(parties);
    slotsArray = new Object[parties];
  }

  /**
   * Set the function to call at the point at which all threads reach the
   * rendezvous. This function is run exactly once, by the thread
   * that trips the barrier. The function is not run if the barrier is
   * broken.
   * @param function the function to run. If null, no function is run.
   * @return the previous function
   */
  public synchronized RendezvousFunction setRendezvousFunction(RendezvousFunction function) {
    RendezvousFunction old = rendezvousFunction;
    rendezvousFunction = function;
    return old;
  }

  public int parties() {
    return partiesCount;
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
  public void restart() {
    // This is not very good, but probably the best that can be done
    for (;;) {
      synchronized (this) {
        if (entriesCount != 0) {
          notifyAll();
        } else {
          isBroken = false;
          return;
        }
      }
      Thread.yield();
    } 
  }

  /**
   * Enter a rendezvous; returning after all other parties arrive.
   * @param x the item to present at rendezvous point.
   * By default, this item is exchanged with another.
   * @return an item x given by some thread, and/or processed
   * by the rendezvousFunction.
   * @exception BrokenBarrierException
   * if any other thread
   * in any previous or current barrier
   * since either creation or the last restart
   * operation left the barrier
   * prematurely due to interruption or time-out. (If so,
   * the broken status is also set.)
   * Also returns as
   * broken if the RendezvousFunction encountered a run-time exception.
   * Threads that are noticed to have been
   * interrupted <em>after</em> being released are not considered
   * to have broken the barrier.
   * In all cases, the interruption
   * status of the current thread is preserved, so can be tested
   * by checking Thread.interrupted.
   * @exception InterruptedException if this thread was interrupted
   * during the exchange. If so, broken status is also set.
   */
  public Object rendezvous(Object x) throws InterruptedException, BrokenBarrierException {
    return doRendezvous(x, false, 0);
  }

  /**
   * Wait msecs to complete a rendezvous.
   * @param x the item to present at rendezvous point.
   * By default, this item is exchanged with another.
   * @param msecs The maximum time to wait.
   * @return an item x given by some thread, and/or processed
   * by the rendezvousFunction.
   * @exception BrokenBarrierException
   * if any other thread
   * in any previous or current barrier
   * since either creation or the last restart
   * operation left the barrier
   * prematurely due to interruption or time-out. (If so,
   * the broken status is also set.)
   * Also returns as
   * broken if the RendezvousFunction encountered a run-time exception.
   * Threads that are noticed to have been
   * interrupted <em>after</em> being released are not considered
   * to have broken the barrier.
   * In all cases, the interruption
   * status of the current thread is preserved, so can be tested
   * by checking Thread.interrupted.
   * @exception InterruptedException if this thread was interrupted
   * during the exchange. If so, broken status is also set.
   * @exception TimeoutException if this thread timed out waiting for
   * the exchange. If the timeout occured while already in the
   * exchange, broken status is also set.
   */
  public Object attemptRendezvous(Object x, long msecs) throws InterruptedException, TimeoutException, BrokenBarrierException {
    return doRendezvous(x, true, msecs);
  }

  protected Object doRendezvous(Object x, boolean timed, long msecs) throws InterruptedException, com.sap.engine.lib.util.concurrent.TimeoutException, BrokenBarrierException {
    // rely on semaphore to throw interrupt on entry
    long startTime;

    if (timed) {
      startTime = System.currentTimeMillis();

      if (!entryGateSync.attempt(msecs)) {
        throw new com.sap.engine.lib.util.concurrent.TimeoutException(msecs);
      }
    } else {
      startTime = 0;
      entryGateSync.acquire();
    }

    synchronized (this) {
      Object y = null;
      int index = entriesCount++;
      slotsArray[index] = x;
      try {
        // last one in runs function and releases
        if (entriesCount == partiesCount) {
          departuresCount = entriesCount;
          notifyAll();
          try {
            if (!isBroken && rendezvousFunction != null) {
              rendezvousFunction.rendezvousFunction(slotsArray);
            }
          } catch (RuntimeException ex) {
            isBroken = true;
          }
        } else {
          while (!isBroken && departuresCount < 1) {
            long timeLeft = 0;

            if (timed) {
              timeLeft = msecs - (System.currentTimeMillis() - startTime);

              if (timeLeft <= 0) {
                isBroken = true;
                departuresCount = entriesCount;
                notifyAll();
                throw new com.sap.engine.lib.util.concurrent.TimeoutException(msecs);
              }
            }

            try {
              wait(timeLeft);
            } catch (InterruptedException ex) {
              if (isBroken || departuresCount > 0) { // interrupted after release
                Thread.currentThread().interrupt();
                break;
              } else {
                isBroken = true;
                departuresCount = entriesCount;
                notifyAll();
                throw ex;
              }
            }
          }
        }
      } finally {
        y = slotsArray[index];

        // Last one out cleans up and allows next set of threads in
        if (--departuresCount <= 0) {
          for (int i = 0; i < slotsArray.length; ++i) {
            slotsArray[i] = null; 
          }
          entryGateSync.release(entriesCount);
          entriesCount = 0;
        }
      }
      // continue if no IE/TO throw
      if (isBroken) {
        throw new BrokenBarrierException(index);
      } else {
        return y;
      }
    }
  }

}


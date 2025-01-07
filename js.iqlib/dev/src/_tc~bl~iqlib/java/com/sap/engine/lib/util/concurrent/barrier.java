package com.sap.engine.lib.util.concurrent;

/**
 * Barriers serve as synchronization points for groups of threads that
 * must occasionally wait for each other.
 */
public interface Barrier {

  /**
   * Return the number of parties that must meet per barrier
   * point. The number of parties is always at least 1.
   */
  public int parties();


  /**
   * Returns true if the barrier has been compromised
   * by threads leaving the barrier before a synchronization
   * point (normally due to interruption or timeout).
   * Barrier methods in implementation classes throw
   * throw BrokenBarrierException upon detection of breakage.
   * Implementations may also support some means
   * to clear this status.
   */
  public boolean broken();

}


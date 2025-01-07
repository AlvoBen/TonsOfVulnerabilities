package com.sap.engine.lib.util.concurrent;

/**
 * Main interface for locks, gates, and conditions.
 */
public interface Sync {

  /**
   *  Wait (possibly forever) until successful passage.
   *  Fail only upon interuption. Interruptions always result in
   *  `clean' failures. On failure,  you can be sure that it has not
   *  been acquired, and that no
   *  corresponding release should be performed. Conversely,
   *  a normal return guarantees that the acquire was successful.
   */
  public void acquire() throws InterruptedException;


  /**
   * Wait at most msecs to pass; report whether passed.
   *
   * The method has best-effort semantics:
   * The msecs bound cannot
   * be guaranteed to be a precise upper bound on wait time in Java.
   * Implementations generally can only attempt to return as soon as possible
   * after the specified bound. Also, timers in Java do not stop during garbage
   * collection, so timeouts can occur just because a GC intervened.
   * So, msecs arguments should be used in
   * a coarse-grained manner. Further,
   * implementations cannot always guarantee that this method
   * will return at all without blocking indefinitely when used in
   * unintended ways. For example, deadlocks may be encountered
   * when called in an unintended context.
   *
   * @param msecs the number of milleseconds to wait.
   * An argument less than or equal to zero means not to wait at all.
   * However, this may still require
   * access to a synchronization lock, which can impose unbounded
   * delay if there is a lot of contention among threads.
   * @return true if acquired
   */
  public boolean attempt(long msecs) throws InterruptedException;


  /**
   * Potentially enable others to pass.
   * <p>
   * Because release does not raise exceptions,
   * it can be used in `finally' clauses without requiring extra
   * embedded try/catch blocks. But keep in mind that
   * as with any java method, implementations may
   * still throw unchecked exceptions such as Error or NullPointerException
   * when faced with uncontinuable errors. However, these should normally
   * only be caught by higher-level error handlers.
   */
  public void release();


  /**  One second, in milliseconds; convenient as a time-out value  */
  public static final long ONE_SECOND = 1000;
  /**  One minute, in milliseconds; convenient as a time-out value  */
  public static final long ONE_MINUTE = 60 * ONE_SECOND;
  /**  One hour, in milliseconds; convenient as a time-out value  */
  public static final long ONE_HOUR = 60 * ONE_MINUTE;
  /**  One day, in milliseconds; convenient as a time-out value  */
  public static final long ONE_DAY = 24 * ONE_HOUR;
  /**  One week, in milliseconds; convenient as a time-out value  */
  public static final long ONE_WEEK = 7 * ONE_DAY;

}


package com.sap.engine.lib.util.concurrent;

/**
 * This interface exists to enable stricter type checking
 * for channels. A method argument or instance variable
 * in a consumer object can be declared as only a Takable
 * rather than a Channel, in which case a Java compiler
 * will disallow put operations.
 */
public interface Takable {

  /**
   * Return and remove an item from channel,
   * possibly waiting indefinitely until
   * such an item exists.
   * @return  some item from the channel. Different implementations
   *  may guarantee various properties (such as FIFO) about that item
   * @exception InterruptedException if the current thread has
   * been interrupted at a point at which interruption
   * is detected, in which case state of the channel is unchanged.
   *
   */
  public Object take() throws InterruptedException;


  /**
   * Return and remove an item from channel only if one is available within
   * msecs milliseconds. The time bound is interpreted in a coarse
   * grained, best-effort fashion.
   * @param msecs the number of milliseconds to wait. If less than
   *  or equal to zero, the operation does not perform any timed waits,
   * but might still require
   * access to a synchronization lock, which can impose unbounded
   * delay if there is a lot of contention for the channel.
   * @return some item, or null if the channel is empty.
   * @exception InterruptedException if the current thread has
   * been interrupted at a point at which interruption
   * is detected, in which case state of the channel is unchanged
   * (i.e., equivalent to a false return).
   */
  public Object poll(long msecs) throws InterruptedException;

}


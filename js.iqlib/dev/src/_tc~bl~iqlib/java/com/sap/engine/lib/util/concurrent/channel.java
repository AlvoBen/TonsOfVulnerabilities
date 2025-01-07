package com.sap.engine.lib.util.concurrent;

/**
 * Channel interface.
 */
public interface Channel
  extends Puttable, Takable {

  /**
   * Place item in the channel, possibly waiting indefinitely until
   * it can be accepted. Channels implementing the BoundedChannel
   * subinterface are generally guaranteed to block on puts upon
   * reaching capacity, but other implementations may or may not block.
   * @param item the element to be inserted. Should be non-null.
   * @exception InterruptedException if the current thread has
   * been interrupted at a point at which interruption
   * is detected, in which case the element is guaranteed not
   * to be inserted. Otherwise, on normal return, the element is guaranteed
   * to have been inserted.
   */
  public void put(Object item) throws InterruptedException;


  /**
   * Place item in channel only if it can be accepted within
   * msecs milliseconds. The time bound is interpreted in
   * a coarse-grained, best-effort fashion.
   * @param item the element to be inserted. Should be non-null.
   * @param msecs the number of milliseconds to wait. If less than
   * or equal to zero, the method does not perform any timed waits,
   * but might still require
   * access to a synchronization lock, which can impose unbounded
   * delay if there is a lot of contention for the channel.
   * @return true if accepted, else false
   * @exception InterruptedException if the current thread has
   * been interrupted at a point at which interruption
   * is detected, in which case the element is guaranteed not
   * to be inserted (i.e., is equivalent to a false return).
   */
  public boolean offer(Object item, long msecs) throws InterruptedException;


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
   * (i.e., equivalent to a null return).
   */
  public Object poll(long msecs) throws InterruptedException;


  /**
   * Return, but do not remove object at head of Channel,
   * or null if it is empty.
   */
  public Object peek();

}


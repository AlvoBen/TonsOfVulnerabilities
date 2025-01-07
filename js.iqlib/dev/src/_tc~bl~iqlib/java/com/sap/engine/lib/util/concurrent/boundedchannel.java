package com.sap.engine.lib.util.concurrent;

/**
 * Channel implementation, which has capacity limit.
 */
public interface BoundedChannel
  extends Channel {

  /**
   * Return the maximum number of elements that can be held.
   * @return the capacity of this channel.
   */
  public int capacity();

}


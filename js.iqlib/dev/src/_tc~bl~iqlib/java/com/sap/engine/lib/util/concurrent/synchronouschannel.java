package com.sap.engine.lib.util.concurrent;

public class SynchronousChannel implements BoundedChannel {

  /*
   This implementation divides actions into two cases for puts:
   * An arriving putter that does not already have a waiting taker
   creates a node holding item, and then waits for a taker to take it.
   * An arriving putter that does already have a waiting taker fills
   the slot node created by the taker, and notifies it to continue.
   And symmetrically, two for takes:
   * An arriving taker that does not already have a waiting putter
   creates an empty slot node, and then waits for a putter to fill it.
   * An arriving taker that does already have a waiting putter takes
   item from the node created by the putter, and notifies it to continue.
   This requires keeping two simple queues: waitingPuts and waitingTakes.
   
   When a put or take waiting for the actions of its counterpart
   aborts due to interruption or timeout, it marks the node
   it created as "CANCELLED", which causes its counterpart to retry
   the entire put or take sequence.
   */
  /**
   * Special marker used in queue nodes to indicate that
   * the thread waiting for a change in the node has timed out
   * or been interrupted.
   */
  protected static final Object CANCELLED = new Object();

  /**
   * Simple FIFO queue class to hold waiting puts/takes.
   */
  protected static class Queue {

    protected LinkedNode headNode;
    protected LinkedNode lastNode;

    protected void enq(LinkedNode p) {
      if (lastNode == null) {
        lastNode = headNode = p;
      } else {
        lastNode = lastNode.next = p;
      }
    }

    protected LinkedNode deq() {
      LinkedNode p = headNode;
      if (p != null && (headNode = p.next) == null) {
        lastNode = null;
      }
      return p;
    }

  }

  protected final Queue waitingPuts = new Queue();
  protected final Queue waitingTakes = new Queue();

  /**
   * @return zero --
   * Synchronous channels have no internal capacity.
   */
  public int capacity() {
    return 0;
  }

  /**
   * @return null --
   * Synchronous channels do not hold contents unless actively taken
   */
  public Object peek() {
    return null;
  }

  public void put(Object x) throws InterruptedException {
    if (x == null) {
      throw new IllegalArgumentException();
    }

    // This code is conceptually straightforward, but messy
    // because we need to intertwine handling of put-arrives first
    // vs take-arrives first cases.
    // Outer loop is to handle retry due to cancelled waiting taker
    for (;;) {
      // Get out now if we are interrupted
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      // Exactly one of item or slot will be nonnull at end of
      // synchronized block, depending on whether a put or a take
      // arrived first. 
      LinkedNode slot;
      LinkedNode item = null;
      synchronized (this) {
        // Try to match up with a waiting taker; fill and signal it below
        slot = waitingTakes.deq();
        // If no takers yet, create a node and wait below
        if (slot == null) {
          waitingPuts.enq(item = new LinkedNode(x));
        }
      }

      if (slot != null) { // There is a waiting taker.
        // Fill in the slot created by the taker and signal taker to
        // continue.
        synchronized (slot) {
          if (slot.value != CANCELLED) {
            slot.value = x;
            slot.notify();
            return;
          }

          // else the taker has cancelled, so retry outer loop
        }
      } else {
        // Wait for a taker to arrive and take the item.
        synchronized (item) {
          try {
            while (item.value != null) {
              item.wait();
            }
            return;
          } catch (InterruptedException ie) {
            // If item was taken, return normally but set interrupt status
            if (item.value == null) {
              Thread.currentThread().interrupt();
              return;
            } else {
              item.value = CANCELLED;
              throw ie;
            }
          }
        }
      }
    } 
  }

  public Object take() throws InterruptedException {
    // Entirely symmetric to put()
    for (;;) {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      LinkedNode item;
      LinkedNode slot = null;
      synchronized (this) {
        item = waitingPuts.deq();
        if (item == null) {
          waitingTakes.enq(slot = new LinkedNode());
        }
      }

      if (item != null) {
        synchronized (item) {
          Object x = item.value;

          if (x != CANCELLED) {
            item.value = null;
            item.next = null;
            item.notify();
            return x;
          }
        }
      } else {
        synchronized (slot) {
          try {
            for (;;) {
              Object x = slot.value;

              if (x != null) {
                slot.value = null;
                slot.next = null;
                return x;
              } else {
                slot.wait();
              }
            } 
          } catch (InterruptedException ie) {
            Object x = slot.value;

            if (x != null) {
              slot.value = null;
              slot.next = null;
              Thread.currentThread().interrupt();
              return x;
            } else {
              slot.value = CANCELLED;
              throw ie;
            }
          }
        }
      }
    } 
  }

  /*
   Offer and poll are just like put and take, except even messier.
   */
  public boolean offer(Object x, long msecs) throws InterruptedException {
    if (x == null) {
      throw new IllegalArgumentException();
    }
    long waitTime = msecs;
    long startTime = 0; // lazily initialize below if needed

    for (;;) {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      LinkedNode slot;
      LinkedNode item = null;
      synchronized (this) {
        slot = waitingTakes.deq();

        if (slot == null) {
          if (waitTime <= 0) {
            return false;
          } else {
            waitingPuts.enq(item = new LinkedNode(x));
          }
        }
      }

      if (slot != null) {
        synchronized (slot) {
          if (slot.value != CANCELLED) {
            slot.value = x;
            slot.notify();
            return true;
          }
        }
      }

      long now = System.currentTimeMillis();
      if (startTime == 0) {
        startTime = now;
      } else {
        waitTime = msecs - (now - startTime);
      }

      if (item != null) {
        synchronized (item) {
          try {
            for (;;) {
              if (item.value == null) {
                return true;
              }

              if (waitTime <= 0) {
                item.value = CANCELLED;
                return false;
              }

              item.wait(waitTime);
              waitTime = msecs - (System.currentTimeMillis() - startTime);
            } 
          } catch (InterruptedException ie) {
            if (item.value == null) {
              Thread.currentThread().interrupt();
              return true;
            } else {
              item.value = CANCELLED;
              throw ie;
            }
          }
        }
      }
    } 
  }

  public Object poll(long msecs) throws InterruptedException {
    long waitTime = msecs;
    long startTime = 0;

    for (;;) {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      LinkedNode item;
      LinkedNode slot = null;
      synchronized (this) {
        item = waitingPuts.deq();

        if (item == null) {
          if (waitTime <= 0) {
            return null;
          } else {
            waitingTakes.enq(slot = new LinkedNode());
          }
        }
      }

      if (item != null) {
        synchronized (item) {
          Object x = item.value;

          if (x != CANCELLED) {
            item.value = null;
            item.next = null;
            item.notify();
            return x;
          }
        }
      }

      long now = System.currentTimeMillis();
      if (startTime == 0) {
        startTime = now;
      } else {
        waitTime = msecs - (now - startTime);
      }

      if (slot != null) {
        synchronized (slot) {
          try {
            for (;;) {
              Object x = slot.value;

              if (x != null) {
                slot.value = null;
                slot.next = null;
                return x;
              }

              if (waitTime <= 0) {
                slot.value = CANCELLED;
                return null;
              }

              slot.wait(waitTime);
              waitTime = msecs - (System.currentTimeMillis() - startTime);
            } 
          } catch (InterruptedException ie) {
            Object x = slot.value;

            if (x != null) {
              slot.value = null;
              slot.next = null;
              Thread.currentThread().interrupt();
              return x;
            } else {
              slot.value = CANCELLED;
              throw ie;
            }
          }
        }
      }
    } 
  }

}


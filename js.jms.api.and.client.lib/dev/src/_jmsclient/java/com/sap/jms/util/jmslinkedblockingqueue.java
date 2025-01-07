package com.sap.jms.util;

import java.util.concurrent.ConcurrentLinkedQueue;

public class JMSLinkedBlockingQueue<E> {
	private ConcurrentLinkedQueue<E> queue;

	public JMSLinkedBlockingQueue() {
		queue = new ConcurrentLinkedQueue<E>();
	}

	/**
	 * Adds the specified element to the tail of this queue, waiting if
	 * necessary for space to become available.
	 * @param o the element to add
	 * @throws InterruptedException if interrupted while waiting.
	 * @throws NullPointerException if the specified element is <tt>null</tt>.
	 */
	public void put(E o) throws InterruptedException {
		synchronized (this) {
			queue.add(o);
			notifyAll();
		}
	}

	public void push(E o) throws InterruptedException {

		ConcurrentLinkedQueue<E> tmp = new ConcurrentLinkedQueue<E>();
		tmp.add(o);
		synchronized (this) {
			tmp.addAll(queue);

			queue = tmp;

			notifyAll();
		}
	}

	/**
	 * Retrieves and removes the head of this queue, waiting
	 * if necessary up to the specified wait time if no elements are
	 * present on this queue.
	 * @param timeout how long to wait before giving up, in miliseconds
	 * @return the head of this queue, or <tt>null</tt> if the
	 * specified waiting time elapses before an element is present.
	 * @throws InterruptedException 
	 * @throws InterruptedException if interrupted while waiting.
	 */
	public E poll(long timeout) throws InterruptedException {
		E x = null;

		x = queue.poll();
		if (x == null) {
			synchronized (this) {
				long start = System.currentTimeMillis();
				long delta = 0;
				while (queue.isEmpty() && (timeout - delta) > 0) {
					wait(timeout - delta);
					delta = System.currentTimeMillis() - start;
				}
			}
			x = queue.poll();
		}

		return x;
	}

	/**
	 * Removes all of the elements from this collection (optional operation). 
	 * This collection will be empty after this method returns unless it 
	 * throws an exception.
	 * @throws UnsupportedOperationException 
	 * UnsupportedOperationException - if the clear method is not supported by this collection.
	 */
	public void clear() {
		queue.clear();
	}

	/**
	 * Retrieves, but does not remove, the head of this queue, waiting
	 * if necessary up to the specified wait time if no elements are
	 * present on this queue.
	 * @return the head of this queue, or <tt>null</tt> if the
	 * specified waiting time elapses before an element is present.
	 * @throws InterruptedException 
	 * @throws InterruptedException if interrupted while waiting.
	 */
	public E peek() {
		return queue.peek();
	}

	public E peek(long timeout) throws InterruptedException {
		E x = null;

		x = queue.peek();
		if (x == null) {
			synchronized (this) {
				long start = System.currentTimeMillis();
				long delta = 0;
				while (queue.isEmpty() && (timeout - delta) > 0) {
					wait(timeout - delta);
					delta = System.currentTimeMillis() - start;
				}
			}
			x = queue.peek();
		}

		return x;
	}

	public int size() {
		return queue.size();
	}

	public String toString() {
		return queue.toString();
	}
}

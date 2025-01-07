package com.sap.jms.util.compat.concurrent;

import java.util.LinkedList;

public class ConcurrentLinkedQueue {
	
	private LinkedList queue = null;
	
	public ConcurrentLinkedQueue() {
		queue = new LinkedList(); 
	}
	
	public synchronized Object element() {
		Object result = queue.getFirst();
		return result;
	}
	
	public synchronized boolean offer(Object o) {
		return queue.add(o);
	}

	public synchronized Object peek() {
		Object result = queue.isEmpty() ? null : queue.get(0);		
		return result;	
	}
	
	public synchronized Object poll() {
		Object result = queue.isEmpty() ? null : queue.remove(0);
		return result;		
	}
	
	public synchronized boolean add(Object o) {
		return queue.add(o);
	}

	public synchronized Object remove() {
		return queue.removeFirst();
	}
	
	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public synchronized void clear() {
		queue.clear();
	}
}

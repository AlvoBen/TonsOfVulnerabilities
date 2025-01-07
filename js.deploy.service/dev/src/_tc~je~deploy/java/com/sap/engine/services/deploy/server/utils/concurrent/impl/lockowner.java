package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import static com.sap.engine.services.deploy.container.util.CAConstants.EOL;
import static com.sap.engine.services.deploy.server.utils.DSConstants.EOL_TAB;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.Retainable;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class holds information about the threads which are trying to acquire
 * the locks. Here we are tracking the relations between the threads which 
 * allows us to give the child threads to obtain the locks of the parent 
 * thread. The client is responsible for the synchronization when using this 
 * class.
 * 
 * @author Emil Dinchev
 */
public final class LockOwner<N> 
	implements ContextObject, Retainable {
	private static final Location location = 
		Location.getLocation(LockOwner.class);

	/**
	 * Custom impl of stack, because java.util.Stack extends Vector,
	 * which is synchronized. We suppose that our stack is synchronized 
	 * outside.
	 */
	private final class Stack<T> {
		private final List<T> lst;

		public Stack() {
			lst = new ArrayList<T>();
		}
				
		/**
		 * @return the last element, without removing it from the stack.
		 */
		public T peek() {
			return lst.get(length() - 1); 
		}

		/**
		 * @return the last element, removing it from the stack.
		 */
		public T pop() {
			return lst.remove(length() - 1);
		}
			
		/**
		 * Pushes a new element in the stack.
		 * @param item
		 */
		public void push(final T element) {
			lst.add(element);
		}
			
		public boolean isEmpty() {
			return lst.isEmpty();
		}
		
		private int length() {
			final int len = lst.size();
			if(len == 0) {
				throw new EmptyStackException(); 
			}
			return len;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(T element: lst) {
				sb.append(DSConstants.EOL_TAB).append(element);
			}
			return sb.toString();
		}
	}

	// The stack of operations for which this context object is used.
	private Stack<String> opStack;
	private ThreadDescriptor creator;
	private ThreadDescriptor current;
	
	private ThreadLocal<LockOwner<N>> threadLocal;

	// The node for which the first operation is invoked.
	private N root;
	private boolean failFast;

	// LockOwner must not be created from other packages.
	LockOwner() {
		current = ThreadDescriptor.create();
		creator = current;
	}
	
	/*	
	 * This method is called in the current thread by ThreadSystem to provide 
	 * an initial &quot;clean&quot; value of the current ContextObject when
	 * there is no information about the parent. 
	 * @see com.sap.engine.frame.core.thread.ContextObject#getInitialValue()
	 */
	public ContextObject getInitialValue() {
		final LockOwner<N> lockOwner = new LockOwner<N>();
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"getInitialValue() returns {0}", lockOwner);
		}
		return lockOwner;
	}

	/* 
	 * This method is called in the parent thread by ThreadSystem to inherit
	 * the current context object. It is guaranteed that the context object
	 * already exists - i.e. the parent is not null.
	 * @see com.sap.engine.frame.core.thread.ContextObject#childValue(
	 * 	com.sap.engine.frame.core.thread.ContextObject, 
	 * 	com.sap.engine.frame.core.thread.ContextObject)
	 */
	@SuppressWarnings("unchecked")
	public ContextObject childValue(final ContextObject parent, 
		final ContextObject child) {
		final LockOwner lockOwner = new LockOwner<N>();
		lockOwner.inherit((LockOwner<N>)parent);
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"childValue({0}, {1}) called.", parent, lockOwner);
		}
		return lockOwner;
	}

	/* 
	 * This method is called by ThreadSystem to release the thread context 
	 * object.
	 * @see com.sap.engine.frame.core.thread.ContextObject#empty()
	 */
	public void empty() {
		// Do not trace any logs here to avoid ConcurrentModificationException
		// in com.sap.engine.core.thread.ThreadContextImpl.
		assert opStack == null;
		assert root == null;
		assert threadLocal == null;
		creator = null;
	}

	void setThreadLocal(final ThreadLocal<LockOwner<N>> threadLocal) {
		this.threadLocal = threadLocal;
	}

	/**
	 * Package-private method to inherit the parent if it is available. Our 
	 * goal is to inherit the creator.
	 * @param parent the parent context object. Cannot be null.
	 */
	void inherit(final LockOwner<N> parent) {
		assert parent != null;
		creator = parent.creator;
	}

	/**
	 * This method is called when owner enters an operation. The call has to
	 * be done after a call to <tt>init()</tt> method.
	 * @param operation the operation.
	 * @param node the node for which the current operation is invoked.
	 */
	void enterOperation(final String op, final N node) {
		assert isInitialized();
		if(opStack == null) {
			// This is the first operation for the current lock owner.
			opStack = new Stack<String>();
			root = node;
		}
		opStack.push(op + " : " + node);
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"{0} just entered operation[{1}]", this, op);
		}
	}

	/**
	 * Package-private method to set the fail fast flag.
	 * @param failFast fail fast flag.
	 */
	void setFailFastOnLockAttempt(final boolean failFast) {
		this.failFast = failFast;
	}
	
	/**
	 * Package private method to test the fail fast flag.
	 * @return value of the fail fast flag. By default it is <tt>false</tt>.
	 */
	boolean failFastOnLockAttempt() {
		return failFast;
	}

	/**
	 * This has to be called after obtaining of the thread context object, 
	 * before any call to other methods, because we are not sure when this object will be created. Called from the same package.
	 */
	void init(final boolean failFast) {
		this.failFast = failFast;
		current = ThreadDescriptor.create();
		if(current.equals(creator)) {
			creator = current;
		}
		assert isInitialized();
	}

	private boolean isInitialized() {
		return creator != null && current != null;
	}
	
	/**
	 * Leave an operation. Has to be called for every  call to 
	 * <tt>enterOperation()</tt> in order to signal the lock owner that the 
	 * current operation is finished.
	 */
	void leaveOperation() {
		assert isInitialized();
		final String op = opStack.pop();
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"{0} just left operation[{1}]", this, op);
		}
		if(opStack.isEmpty()) {
			free();
		}
	}

	
	/**
	 * This method is used to free the lock owner, when there are no more
	 * operations in the stack and we are back to the root node. However the 
	 * creator field has to remain untouched.
	 */
	private void free() {
		if(location.beDebug()) {
		SimpleLogger.trace(Severity.PATH, location, null, 
			"Will free {0}", this);
		}
		
		opStack = null;
		root = null;
		// Current must not be reset here, to avoid NPE in hashCode()
		if(threadLocal != null) {
			threadLocal.remove();
			threadLocal = null;
		}
	}

	ThreadDescriptor getCreator() {
		return creator;
	}
	
	ThreadDescriptor getCurrent() {
		return current;
	}

	String getOperation() {
		return opStack.peek();
	}
	
	N getRoot() {
		return root;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(current).append(" {").append(EOL_TAB)
			.append("creator:").append(creator).append(EOL_TAB);
		if(opStack != null) {
			sb.append("Active operations:")
				.append(EOL_TAB).append(opStack.toString());
		}
		sb.append(EOL).append("}").append(EOL);
		return sb.toString();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj) {
		if(obj == null) {
			return false;
		}
		LockOwner<N> other = (LockOwner<N>)obj;
		return this.current.equals(other.current);
	}
	
	@Override
	public int hashCode() {
		return current.hashCode();
	}
}
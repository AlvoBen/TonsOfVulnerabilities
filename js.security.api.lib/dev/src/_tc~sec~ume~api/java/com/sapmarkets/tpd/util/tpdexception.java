package com.sapmarkets.tpd.util;

import java.io.PrintStream; 
import java.io.PrintWriter; 

/**
 * Trading Partner exception class. Uses chainable exceptions.
 * Creation date: (8/9/00 3:41:41 PM)
 * @author Venkataramiah Annapragada
 * @author Martin Stein
 * @author Jimmy Wong
 */
public class TpdException extends Exception {

	private static final long serialVersionUID = 3195601832936693074L;
	
	private Throwable _previousThrowable = null;

	/**
	 * TpdException constructor comment.
	 */
	public TpdException() {
		super();
	}

	/**
	 * TpdException constructor comment.
	 * @param s java.lang.String
	 */
	public TpdException(String s) {
		super(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/9/00 5:05:49 PM)
	 * @param s java.lang.String
	 * @param pEx java.lang.Throwable
	 */
	public TpdException(String s, Throwable pEx) { 
		super(s);
		_previousThrowable = pEx;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/9/00 5:07:27 PM)
	 * @param pEx java.lang.Throwable
	 */
	public TpdException(Throwable pEx) { 
		_previousThrowable = pEx;
	}

	public void printStackTrace() { 
		super.printStackTrace(); 
		if (_previousThrowable != null) { 
			_previousThrowable.printStackTrace(); 
		} 
	}

	public void printStackTrace(PrintStream pPS) { 
		super.printStackTrace(pPS); 
		if (_previousThrowable != null) { 
			_previousThrowable.printStackTrace(pPS); 
		} 
	}

	public void printStackTrace(PrintWriter pPW) { 
		super.printStackTrace(pPW); 
		if (_previousThrowable != null) { 
			_previousThrowable.printStackTrace(pPW); 
		} 
	}
}

/*
 * Created on Jan 26, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.library_container.deploy;

/**
 * Action type for application hooks - can be invoked before the start of the
 * application or after its stop.
 * 
 * @author I024067
 * 
 */
public class Action {
	public static final String START = "beforeStart";
	public static final String STOP = "afterStop";
	String type = null;
	String className = null;
	String methodName = null;
	boolean isFatal = true;
}

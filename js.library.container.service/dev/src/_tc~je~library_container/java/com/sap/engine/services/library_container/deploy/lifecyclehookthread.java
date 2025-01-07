/*
 * Created on Jan 17, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.library_container.deploy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.sap.engine.interfaces.resourcecontext.ResourceContext;
import com.sap.engine.interfaces.resourcecontext.ResourceContextException;
import com.sap.engine.lib.util.concurrent.CountDown;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.library_container.LCResourceAccessor;

/**
 * Runnable for the execution of the so called <i>applicaiton hooks</i> -
 * methods that are part of the classes packed in the application libraries.
 * Depending on the hook type, the methods are executed either before the
 * application start or after the application stop. The method and class names
 * are defined in META-INF\application-service.xml in the SDA package. The
 * method shall have no parameters and shall not return any result. Further
 * details of the specification for application hooks can be found in section
 * 4.5.2 in Library Container SRS document.
 * 
 * @author I024067
 */
public class LifeCycleHookThread implements Runnable {

	private CountDown countDown = null;
	private String className = null;
	private ClassLoader classLoader = null;
	private String methodName = null;
	private String applicationName = null;
	private Throwable throwable = null;
	private ResourceContext resourceContext = null;
	private final String threadName;

	private final static String EOL = System.getProperty("line.separator");

	/**
	 * 
	 * @param cd
	 *            object which blocks the thread that will start the application
	 *            hook thread; at the end of execution the lock is automatically
	 *            removed which notifies the blocked thread about the operation
	 *            finish
	 * @param className
	 *            class name of application hook
	 * @param cl
	 *            application classloader
	 * @param methodName
	 *            method name
	 * @param applicationName
	 *            name of the application for which the hook is invoked
	 * @param resourceCtx
	 *            application resource context
	 */
	public LifeCycleHookThread(CountDown cd, String className, ClassLoader cl,
			String methodName, String applicationName,
			ResourceContext resourceCtx) {
		this.countDown = cd;
		this.className = className;
		this.classLoader = cl;
		this.methodName = methodName;
		this.applicationName = applicationName;
		this.resourceContext = resourceCtx;
		// "application hook [<class_name>.<method_name>()]"
		this.threadName = "application hook [" + className + "." + methodName
				+ "()]";
		if (LCResourceAccessor.isDebugTraceable()) {
			LCResourceAccessor.traceDebug("{0}", toString());
		}
	}

	public void run() {
		if (LCResourceAccessor.isDebugTraceable()) {
			LCResourceAccessor.traceDebug(
					"Invoking the run() method for [{0}] application.",
					getApplicationName());
		}
		try {
			getResourceContext().enterMethod(getMethodName());
			Class classObject = Class.forName(getClassName(), true,
					getClassLoader());
			Constructor constructor = classObject
					.getConstructor(new Class[] {});
			Object obj = constructor.newInstance(new Object[] {});
			Method methodObject = classObject.getDeclaredMethod(
					getMethodName(), new Class[] {});
			if (LCResourceAccessor.isDebugTraceable()) {
				LCResourceAccessor
						.traceDebug(
								"The method [{0}] is going to be invoked for [{1}] application.",
								methodObject, getApplicationName());
			}
			final String applicationHookExecutionTag = "executing application hook "
					+ className
					+ "."
					+ methodName
					+ " for application "
					+ applicationName;
			Accounting.beginMeasure(applicationHookExecutionTag, classObject);
			try {
				methodObject.invoke(obj, new Object[] {});
			} finally {
				Accounting.endMeasure(applicationHookExecutionTag);
			}
			if (LCResourceAccessor.isDebugTraceable()) {
				LCResourceAccessor
						.traceDebug(
								"  >>>  successfully invoked [{0}] for [{1}] application.",
								methodObject, getApplicationName());
			}
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (ThreadDeath e) {
			throw e;
		} catch (Throwable th) {
			throwable = th;
		} finally {
			try {
				resourceContext.exitMethod(getMethodName(), true);
			} catch (ResourceContextException rcex) {
				LCResourceAccessor
						.traceException(
								"ASJ.dpl_lbc.000010",
								"[ERROR CODE DPL.LIB.105] Error occurred during exiting method [{0}] of resource context for application [{1}].",
								rcex, getMethodName(), getApplicationName());
			}
			countDown.release();
		}
	}

	/**
	 * 
	 * @return the <code>Throwable</code> which has caused failure in the
	 *         application hook's execution.
	 */
	public Throwable getThrowable() {
		if (LCResourceAccessor.isDebugTraceable()) {
			LCResourceAccessor.traceDebug(
					"Invoking the getThrowable() method for [{0}].",
					getApplicationName());
		}
		return throwable;
	}

	private String getApplicationName() {
		return applicationName;
	}

	private ClassLoader getClassLoader() {
		return classLoader;
	}

	private String getClassName() {
		return className;
	}

	private CountDown getCountDown() {
		return countDown;
	}

	private String getMethodName() {
		return methodName;
	}

	private ResourceContext getResourceContext() {
		return resourceContext;
	}

	public final String getThreadName() {
		return this.threadName;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().toString());
		sb.append(EOL);
		sb.append("CountDown = ");
		sb.append(getCountDown());
		sb.append(EOL);
		sb.append("ClassName = ");
		sb.append(getClassName());
		sb.append(EOL);
		sb.append("MethodName = ");
		sb.append(getMethodName());
		sb.append(EOL);
		sb.append("ApplicationName = ");
		sb.append(getApplicationName());
		sb.append(EOL);
		sb.append("Throwable = ");
		sb.append(getThrowable());
		sb.append(EOL);
		sb.append("ResourceContext = ");
		sb.append(getResourceContext());
		sb.append(EOL);
		return sb.toString();
	}

}

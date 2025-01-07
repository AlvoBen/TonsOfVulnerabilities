package com.sap.engine.services.dc.cm.utils;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import com.sap.engine.lib.util.concurrent.CountDown;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.ValidatorUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

public class EngineThreadUtil {
	
	private static Location location = DCLog.getLocation(EngineThreadUtil.class);

	/**
	 * Executes the given runnable in an applicatoin thread with the credentials
	 * of the given userUniqueId. The execution is performed synchronously - the
	 * method blocks until the run method of the runnable returns.
	 * 
	 * @param runnable
	 * @param userUniqueId
	 */
	public static void executeInAuthorizedApplicationThreadSync(
			final Runnable runnable, final String taskName, String threadName,
			final String userUniqueId) {

		final Subject userSubject = SecurityUtil.getUserSubject(userUniqueId);
		final CountDown _countd = new CountDown(1);

		final Runnable applicationRunnable = new Runnable() {
			public void run() {
				try {
					PrivilegedAction privilegedAction = new PrivilegedAction() {
						public Object run() {
							runnable.run();
							return null;
						}
					};
					Subject.doAs(userSubject, privilegedAction);
				} finally {
					_countd.release();
				}
			}
		};

		final ServiceConfigurer serviceConfigurer = ServiceConfigurer
				.getInstance();
		ValidatorUtils.validateNull(serviceConfigurer, "ServiceConfigurer");

		// start deploy process in application thread
		serviceConfigurer.getApplicationServiceContext().getCoreContext()
				.getThreadSystem().startThread(applicationRunnable, taskName,
						threadName, Boolean.FALSE);

		// wait for the deploy to finish
		try {
			_countd.acquire();
		} catch (InterruptedException e) {
			DCLog.logErrorThrowable(location, e);
		}
	}

	/**
	 * Executes the given runnable in an applicatoin thread with the credentials
	 * of the given userUniqueId. The execution is performed asynchronously -
	 * the method does not block until the run method of the runnable returns.
	 * 
	 * @param runnable
	 * @param userUniqueId
	 */
	public static void executeInAuthorizedApplicationThreadAsync(
			final Runnable runnable, final String taskName, String threadName,
			final String userUniqueId) {

		final Subject userSubject = SecurityUtil.getUserSubject(userUniqueId);

		final Runnable applicationRunnable = new Runnable() {
			public void run() {
				PrivilegedAction privilegedAction = new PrivilegedAction() {
					public Object run() {
						runnable.run();
						return null;
					}
				};
				Subject.doAs(userSubject, privilegedAction);
			}
		};

		executeThreadAsync(applicationRunnable, taskName, threadName,
				Boolean.FALSE);
	}

	public static void executeThreadAsync(final Runnable runnable,
			final String taskName, String threadName, final Boolean system) {

		final ServiceConfigurer serviceConfigurer = ServiceConfigurer
				.getInstance();
		ValidatorUtils.validateNull(serviceConfigurer, "ServiceConfigurer");

		// start process in system thread
		serviceConfigurer.getApplicationServiceContext().getCoreContext()
				.getThreadSystem().startThread(runnable, taskName, threadName,
						system);
	}
}

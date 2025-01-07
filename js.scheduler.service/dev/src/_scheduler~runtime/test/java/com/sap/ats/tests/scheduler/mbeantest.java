package com.sap.ats.tests.scheduler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.sap.ats.ApplicationTest;
import com.sap.ats.env.LogEnvironment;
import com.sap.ats.env.TestEnvironment;
import com.sap.ats.env.system.EnvironmentFactory;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaSchedulerFireTimeEvent;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaSchedulerJobParameterDefinition;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaSchedulerTask;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaSchedulerTime;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaSchedulerWrapper;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAM_Helper;
import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.fields.CronDOMField;
import com.sap.scheduler.api.fields.CronDOWField;
import com.sap.scheduler.api.fields.CronHourField;
import com.sap.scheduler.api.fields.CronMinuteField;
import com.sap.scheduler.api.fields.CronMonthField;
import com.sap.scheduler.api.fields.CronYearField;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;

/**
 * ATS Application tests
 * 
 * @author d047889
 */

public class MBeanTest implements ApplicationTest {

	/**
	 * ats Log environment
	 */
	private LogEnvironment logEnv;

	/**
	 * ats TEST environment
	 */
	private TestEnvironment testEnv;

	private String server_password;

	private String server_user;

	private final String LINE_WRAP = System.getProperty("line.separator");

	public static final String INITIAL_CONTEXT_FACTORY = "com.sap.engine.services.jndi.InitialContextFactoryImpl";

	public static final int PREPARE = 1;

	public static final int TEST_START_JOB1 = 2;

	public static final int TEST_START_JOB2 = 3;

	public static final int TEST_START_JOB3 = 4;

	public static final int TEST_START_JOB4 = 5;

	public static final int TEST_START_JOB5 = 6;

	public static final int TEST_START_JOB6 = 7;

	public static final int TEST_START_JOB7 = 8;

	public static final int TEST_START_JOB8 = 9;

	public static final int TEST_START_JOB9 = 10;

	public static final int TEST_START_JOB10 = 11;

	public static final int TEST_START_JOB11 = 12;

	public static final int TEST_START_JOB12 = 13;

	public static final int TEST_START_JOB13 = 14;

	public static final int TEST_START_JOB14 = 15;

	public static final int TEST_START_JOB15 = 16;

	public static final int TEST_START_JOB16 = 17;

	public static final int TEST_START_JOB17 = 18;

	public static final int TEST_START_JOB18 = 19;

	public static final int TEST_START_JOB19 = 20;

	public static final int TEST_START_JOB20 = 21;

	public static final int TEST_START_JOB21 = 22;

	public static final int TEST_START_JOB22 = 23;

	public static final int TEST_START_JOB23 = 24;

	public static final int TEST_START_JOB24 = 25;

	public static final int TEST_START_JOB25 = 26;

	public static final int TEST_START_JOB26 = 27;

	public static final int TEST_START_JOB27 = 28;
	
	public static final int TEST_START_JOB28 = 29;
	
	public static final int TEST_START_JOB29 = 30;
	
	public static final int TEST_START_JOB30 = 31;

	public static final int CLEAN_UP = 99;

	public static final int END_THREAD = 100;

	MbobThread otherUserAccessThread;

	private MBeanServerConnection mbsc;

	private Set<ObjectName> all;

	private ObjectName schedulerMBean;

	public MBeanTest() {
	}

	public class MbobThread extends Thread {
		LinkedBlockingQueue<Integer> inqueue = new LinkedBlockingQueue<Integer>();

		LinkedBlockingQueue<Object> outqueue = new LinkedBlockingQueue<Object>();

		TestDataType tdt = null;

		public MbobThread(String name) {
			super(name);
		}

		public int cleanUp_t() {
			try {
				// process cleanup
				if (logEnv != null) {
					logEnv.close();
				}
				if (testEnv != null) {
					testEnv.close();
				}
			} catch (Exception e) {
				logEnv.log(e);
				return FAILED;
			}

			try {

				testEnv.close();
				logEnv.close();
			} catch (Exception e) {

			}
			return PASSED;
		}

		public int action(int a) throws Exception {

			inqueue.add(a);
			Object E = outqueue.poll(600000, TimeUnit.SECONDS);

			if (E instanceof Integer) {
				return ((Integer) E).intValue();
			} else {
				throw (Exception) E;
			}
		}

		// init for MBean
		@SuppressWarnings("unchecked")
		public int prepare_t() throws Exception {

			logEnv = (LogEnvironment) EnvironmentFactory.getEnvironment(EnvironmentFactory.LOG);
			testEnv = (TestEnvironment) EnvironmentFactory.getEnvironment(EnvironmentFactory.TEST);

			server_password = testEnv.getProperties().getProperty("server_password");
			server_user = testEnv.getProperties().getProperty("server_user");

			Properties props = new Properties();
			props.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
			props.put(Context.SECURITY_PRINCIPAL, server_user);
			props.put(Context.SECURITY_CREDENTIALS, server_password);

			InitialContext initCtx = new InitialContext(props);
			mbsc = (MBeanServer) initCtx.lookup("jmx");

			all = mbsc.queryNames(new ObjectName("*:type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJavaScheduler,*"), null);

			if (all.size() < 1) {
				log("Scheduler MBean not registered.");
				return FAILED;
			}
			schedulerMBean = (ObjectName) all.toArray()[0];
			log("prepare_t() successfully passed!");

			return PASSED; // PASSED | FAILED
		}

		/**
		 * Tested Cron (*)/10:11:2:2:12:12
		 * 
		 * @return PASSED if the result is the same as expected
		 *  
		 */
		public int test_01_t() {
			log("starting test_01_t");

			tdt = testCase(getCronEntry(cron1), null, startTimeForGetFireTimes1, endTimeForGetFireTimes1, datesCron1, cron1);
			try {
				log("Tested Cron:" + "\t" + cron1);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron1);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron1);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
			finally {
				
			}

		}

		/**
		 * Tested Cron "*:*:*:*:1-19/3:7-57/18"
		 * 
		 * @return PASSED if the result is as expected
		 *  
		 */
		public int test_02_t() {
			log("starting test_02_t");

			tdt = testCase(getCronEntry(cron2), null, startTimeForGetFireTimes2, endTimeForGetFireTimes2, datesCron2, cron2);
			try {
				log("Tested Cron:" + "\t" + cron2);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron2);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron2);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron:  "2007:1:29:*:10:20"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse 29 februar 2007 does not exist
		 * 
		 *
		 */
		public int test_03_t() {
			log("starting test_03_t");

			try {
				log("Tested Cron:" + "\t" + cron3);
				if (testCronEntry(cron3)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**"2007-2012:1:29:*:10:20"
		 * 
		 * @return PASSED if the result is as expected
		 * 
		 */
		public int test_04_t() {

			log("starting test_4_t");
			CronEntry entry4 = new CronEntry(cron4);
			tdt = testCase(entry4, recurringEntryForCron4, startTimeForGetFireTimes4, endTimeForGetFireTimes4, datesCron4, cron4);

			try {
				log("TEST_4");
				log("Tested Cron:" + "\t" + cron4);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron4);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron4);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**Tested Cron: "2007/2:1:29:*:10:20"
		 * 
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse on the left side of the year field no period is specified
		 *   
		 * 
		 */
		public int test_05_t() {

			log("starting test_5_t");
			try {
				log("TEST_5");
				log("Tested Cron:" + "\t" + cron5);
				if (testCronEntry(cron5)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/** Tested Cron : "(*)/2:1:29:*:10:20"
		 *
		 * @return PASSED if the result is as expected
		 * 
		 */
		public int test_06_t() {

			log("starting test_6_t");
			CronEntry entry6 = new CronEntry(cron6);
			tdt = testCase(entry6, null, startTimeForGetFireTimes6, endTimeForGetFireTimes6, datesCron6, cron6);
			try {
				log("TEST_6");
				log("Tested Cron:" + "\t" + cron6);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron6);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron6);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "2007:11:12:2/2:1-3:10-40/15"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse on the left side of the DayOfWeek field no period is specified
		 */
		public int test_07_t() {

			log("starting test_7_t");
			try {
				log("TEST_7");
				log("Tested Cron:" + "\t" + cron7);
				if (testCronEntry(cron7)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "(*:*)/2:10:*:12:12"
		 * 
		 * @return PASSED if the result is as expected
		 * 
		 */
		public int test_08_t() {

			log("starting test_8_t");
			CronEntry entry8 = new CronEntry(cron8);
			tdt = testCase(entry8, null, startTimeForGetFireTimes8, endTimeForGetFireTimes8, datesCron8, cron8);
			try {
				log("TEST_8");
				log("Tested Cron:" + "\t" + cron8);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron8);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron8);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "2008:(*)/2:31:*:10:10"
		 * 
		 * @return PASSED if the result is as expected
		 * 
		 */
		public int test_09_t() {

			log("starting test_9_t");
			CronEntry entry9 = new CronEntry(cron9);
			tdt = testCase(entry9, recurringEntryNextYear, startTimeForGetFireTimes9, endTimeForGetFireTimes9, datesCron9, cron9);
			try {
				log("TEST_9");
				log("Tested Cron:" + "\t" + cron9);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron9);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron9);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 *	Tested Cron "2007:11:12:*:2-12/3:10-40/4"
		 * 
		 * 	@return PASSED if the result is as expected
		 */
		public int test_10_t() {

			log("starting test_10_t");
			CronEntry entry10 = new CronEntry(cron10);
			tdt = testCase(entry10, null, startTimeForGetFireTimes10, endTimeForGetFireTimes10, datesCron10, cron10);
			try {
				log("TEST_10");
				log("Tested Cron:" + "\t" + cron10);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron10);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron10);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "2007:11:*:2/2:1-4:10-40/15 "
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse on the left side of the DOW field no period is specified
		 */
		public int test_11_t() {

			log("starting test_11_t");
			try {
				log("TEST_11");
				log("Tested Cron:" + "\t" + cron11);
				if (testCronEntry(cron11)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "2007:11:*:2/8:1-4:10-40/15"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse on the left side of the DOW field no period is specified
		 */
		public int test_12_t() {

			log("starting test_12_t");
			try {
				log("TEST_12");
				log("Tested Cron:" + "\t" + cron12);
				if (testCronEntry(cron12)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "2007:12:*:2/8:1-4:10-40/15"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse the month does not exists
		 */
		public int test_13_t() {

			log("starting test_13_t");
			try {
				log("TEST_13");
				log("Tested Cron:" + "\t" + cron13);
				if (testCronEntry(cron13)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "(*:*)/12:*:*:12:12"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse there is no month such as 12/12 athe month field values are between 0 and 11
		 * 
		 */
		public int test_14_t() {

			log("starting test_14_t");
			try {
				log("TEST_14");
				log("Tested Cron:" + "\t" + cron14);
				if (testCronEntry(cron14)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "1965-1789:*:*:*:1-19/3:7-57/18"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse there is no such year period(first it is before 1970 and second its not correct defined)
		 * 
		 */
		public int test_15_t() {

			log("starting test_15_t");
			try {
				log("TEST_15");
				log("Tested Cron:" + "\t" + cron15);
				if (testCronEntry(cron15)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/** Tested Cron "1965-1972:*:*:*:*:*"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse the  year period(first it is before 1970 and second it is not correct defined)
		 */
		public int test_16_t() {

			log("starting test_16_t");
			try {
				log("TEST_16");
				log("Tested Cron:" + "\t" + cron16);
				if (testCronEntry(cron16)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**Tested Cron "-2020:*:*:*:*:*"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse the  year period is invalid
		 *	 
		 */
		public int test_17_t() {

			log("starting test_17_t");
			try {
				log("TEST_17");
				log("Tested Cron:" + "\t" + cron17);
				if (testCronEntry(cron17)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**Tested Cron "2008:1:31:*:*:*"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse that day does not exists
		 */
		public int test_18_t() {

			log("starting test_18_t");
			try {
				log("TEST_18");
				log("Tested Cron:" + "\t" + cron18);
				if (testCronEntry(cron18)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "2008:3:31:*:*:*"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse that day does not exists
		 * 
		 */
		public int test_19_t() {

			log("starting test_19_t");
			try {
				log("TEST_19");
				log("Tested Cron:" + "\t" + cron19);
				if (testCronEntry(cron19)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Cron "0:*:*:*:*:*"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse that year is before 1970 
		 */
		public int test_20_t() {

			log("starting test_20_t");
			try {
				log("TEST_20");
				log("Tested Cron:" + "\t" + cron20);
				if (testCronEntry(cron20)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		* Tested Cron "*:*:*:*:1-4:10-40/70"
		 * 
		 * @return PASSED if IllegalArgumentException is thrown becouse the right side ot the minute field is over 60
		 */
		public int test_21_t() {

			log("starting test_21_t");
			try {
				log("TEST_21");
				log("Tested Cron:" + "\t" + cron21);
				if (testCronEntry(cron21)) {
					log("STATUS" + "\t" + " PASSED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: IllegalArgumentException");
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					log("Expected: IllegalArgumentException" + "\t" + "Returned: No Exceptions returned");
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested cron : "2007:2:25:*:1-4:10-40/10"
		 * 
		 * @return PASSED if the result is as expected
		 * 
		 */
		public int test_22_t() {

			log("starting test_22_t");
			tdt = testCase(cronEntry22, null, startTimeForGetFireTimes22, endTimeForGetFireTimes22, datesCron22, cron22);
			try {
				log("TEST_22");
				log("Tested Cron:" + "\t" + cron22);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron22);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron22);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 *  Simulating client and server in DIFFERENT TIME ZONES with recurring 
		 *  
		 * @return
		 */
		public int test_23_t() {

			log("starting test_23_t");
			tdt = testCase(null, recEntry23, startTimeForGetFireTimes23, endTimeForGetFireTimes23, datesRec23, recurring23);
			try {
				log("TEST_23");
				log("Tested RecurringEntry:" + "\t" + recurring23);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesRec23);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesRec23);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Simulating client and server in DIFFERENT TIME ZONES with recurring
		 *  
		 * @return
		 * 
		 */
		public int test_24_t() {

			log("starting test_24_t");
			tdt = testCase(null, recEntry24, startTimeForGetFireTimes24, endTimeForGetFireTimes24, datesRec24, recurring24);
			try {
				log("TEST_24");
				log("Tested RecurringEntry:" + "\t" + recurring24);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesRec24);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesRec24);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 *Tested cron:  "2008:10:28:*:10:10"
		 *
		 *Simulating client and server in DIFFERENT TIME ZONES with cron 
		 *
		 * @return
		 */
		public int test_25_t() {
			log("starting test_25_t");
			tdt = testCase(cronEntry25, null, startTimeForGetFireTimes25, endTimeForGetFireTimes25, datesCron25, cron25);
			try {
				log("Tested Cron:" + "\t" + cron25);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron25);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron25);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**Tested cron entry "2008:10:28:*:14:30"
		 * 
		 * Simulating client and server in DIFFERENT TIME ZONES with cron
		 * 
		 * @return
		 * 
		 */
		public int test_26_t() {
			log("starting test_26_t");
			tdt = testCase(cronEntry26, null, startTimeForGetFireTimes26, endTimeForGetFireTimes26, datesCron26, cron26);
			try {
				log("Tested Cron:" + "\t" + cron26);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron26);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron26);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested cron entry "2007:2:25:*:1-4:10-40/10"
		 * DST in march
		 * @return
		 */
		public int test_27_t() {

			log("starting test_27_t");
			tdt = testCase(cronEntry27, null, startTimeForGetFireTimes27, endTimeForGetFireTimes27, datesCron27, cron27);
			try {
				log("TEST_27");
				log("Tested Cron:" + "\t" + cron27);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron27);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron27);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}
		
		/**
		 * Tested cron entry "2007:9:28:,:1-4:10-50/10"
		 * DST in october
		 * @return 
		 * 
		 */
		public int test_28_t() {

			log("starting test_28_t");
			tdt = testCase(cronEntry28, null, startTimeForGetFireTimes28, endTimeForGetFireTimes28, datesCron28, cron28);
			try {
				log("TEST_28");
				log("Tested Cron:" + "\t" + cron28);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesCron28);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesCron28);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested recurring entry "28.10.2007 1:00 every 10 minutes until 4:50"
		 * DST in october
		 * @return
		 * 
		 */
		public int test_29_t() {

			log("starting test_29_t");
			tdt = testCase(null, recEntry29, startTimeForGetFireTimes29, endTimeForGetFireTimes29, datesRec29, recurring29);
			try {
				log("TEST_29");
				log("Tested RecurringEntry:" + "\t" + recurring29);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesRec29);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesRec29);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}

		/**
		 * Tested Recurring entry "25.03.2007 1:00 every 10 minutes until 4:50" 
		 * DST in march
		 * @return
		 * 
		 */
		public int test_30_t() {

			log("starting test_30_t");
			tdt = testCase(null, recEntry30, startTimeForGetFireTimes30, endTimeForGetFireTimes30, datesRec30, recurring30);
			try {
				log("TEST_30");
				log("Tested RecurringEntry:" + "\t" + recurring30);
				if (tdt.getBoolean()) {
					log("STATUS" + "\t" + " PASSED");
					createTable(tdt.getDates(), datesRec30);
					cancelSchedulerTask(tdt.getTaskId());
					return PASSED;
				} else {
					log("STATUS" + "\t" + " FAILED");
					createTable(tdt.getDates(), datesRec30);
					cancelSchedulerTask(tdt.getTaskId());
					return FAILED;
				}

			} catch (Exception jx) {
				logEnv.log(jx);
				return FAILED;
			}
		}
		
		

		public void run() {

			while (true) {
				Integer action = null;
				try {
					action = inqueue.poll(600000, TimeUnit.SECONDS);
				} catch (InterruptedException i) {
				}

				if (action == null) {
					return;
				}
				int task = action.intValue();

				Integer val = null;
				Exception ex = null;

				switch (task) {
				case PREPARE:
					try {
						val = prepare_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}

					break;
				case TEST_START_JOB1:
					try {
						val = test_01_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB2:
					try {
						val = test_02_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB3:
					try {
						val = test_03_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB4:
					try {
						val = test_04_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB5:
					try {
						val = test_05_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB6:
					try {
						val = test_06_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB7:
					try {
						val = test_07_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB8:
					try {
						val = test_08_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB9:
					try {
						val = test_09_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB10:
					try {
						val = test_10_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB11:
					try {
						val = test_11_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB12:
					try {
						val = test_12_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB13:
					try {
						val = test_13_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB14:
					try {
						val = test_14_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB15:
					try {
						val = test_15_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB16:
					try {
						val = test_16_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB17:
					try {
						val = test_17_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB18:
					try {
						val = test_18_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB19:
					try {
						val = test_19_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB20:
					try {
						val = test_20_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB21:
					try {
						val = test_21_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB22:
					try {
						val = test_22_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB23:
					try {
						val = test_23_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;
				case TEST_START_JOB24:
					try {
						val = test_24_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case TEST_START_JOB25:
					try {
						val = test_25_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case TEST_START_JOB26:
					try {
						val = test_26_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case TEST_START_JOB27:
					try {
						val = test_27_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case TEST_START_JOB28:
					try {
						val = test_28_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case TEST_START_JOB29:
					try {
						val = test_29_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case TEST_START_JOB30:
					try {
						val = test_30_t();
					} catch (Exception e) {
						logEnv.log(e);
						ex = e;
					}
					break;

				case CLEAN_UP:
					try {
						val = cleanUp_t();
					} catch (Exception e) {
						ex = e;
						logEnv.log(e);
					}
					break;
				case END_THREAD:
					outqueue.add(PASSED);
					return;
				}
				if (ex != null) {
					outqueue.add(ex);
				} else {
					outqueue.add(val);
				}

			}
		}
	}

	public int test_01() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB1);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_02() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB2);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_03() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB3);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_04() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB4);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_05() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB5);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_06() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB6);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_07() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB7);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_08() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB8);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_09() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB9);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_10() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB10);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_11() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB11);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_12() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB12);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_13() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB13);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_14() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB14);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_15() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB15);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_16() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB16);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_17() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB17);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_18() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB18);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_19() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB19);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_20() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB20);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_21() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB21);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_22() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB22);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_23() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB23);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_24() {

		try {
			return otherUserAccessThread.action(TEST_START_JOB24);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_25() {
		try {
			return otherUserAccessThread.action(TEST_START_JOB25);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_26() {
		try {
			return otherUserAccessThread.action(TEST_START_JOB26);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_27() {
		try {
			return otherUserAccessThread.action(TEST_START_JOB27);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}
	
	public int test_28() {
		try {
			return otherUserAccessThread.action(TEST_START_JOB28);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_29() {
		try {
			return otherUserAccessThread.action(TEST_START_JOB29);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int test_30() {
		try {
			return otherUserAccessThread.action(TEST_START_JOB30);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}
	}

	public int prepare() throws Exception {
		logEnv = (LogEnvironment) EnvironmentFactory.getEnvironment(EnvironmentFactory.LOG);
		testEnv = (TestEnvironment) EnvironmentFactory.getEnvironment(EnvironmentFactory.TEST);

		otherUserAccessThread = new MbobThread("MBeanTestThread");
		otherUserAccessThread.start();
		return otherUserAccessThread.action(PREPARE);
	}

	/**
	 * logs a message via the log environment
	 * 
	 * @param msg
	 *            the log message
	 */
	private void log(String msg) {
		if (logEnv != null) {
			logEnv.log(msg);
			logEnv.log(LINE_WRAP);
		}
	}

	/**
	 * Cleans up the environments
	 * 
	 * @return PASSED always
	 */
	public int cleanUp() {
		try {
			otherUserAccessThread.action(CLEAN_UP);
			otherUserAccessThread.action(END_THREAD);
		} catch (Exception e) {
			logEnv.log(e);
			return FAILED;
		}

		try {
			testEnv.close();
			logEnv.close();
		} catch (Exception e) {

		}
		return PASSED;
	}

	public String toString() {
		return "MBeanTest (NWScheduler)";
	}

	public void createTable(Date[] result, Date[] expected) {

		log("RESULT" + "\t\t\t\t" + "EXPECTED");
		if ((result.length == 0) && (expected.length == 0)) {
			log("no result" + "\t\t" + "no result");
			return;
		}
		if ((result.length == 0) && (expected.length != 0)) {
			for (int i = 0; i < expected.length; i++) {
				log("   " + "\t\t" + expected[i]);
			}

		}
		if ((result.length != 0) && (expected.length == 0)) {
			for (int i = 0; i < result.length; i++) {

				log(result[i] + "\t\t" + "   ");
			}

		}
		if ((result.length != 0) && (expected.length != 0)) {
			if (result.length == expected.length) {
				for (int i = 0; i < expected.length; i++) {
					log(result[i] + "\t\t\t" + expected[i]);
				}

			}
			if (result.length > expected.length) {
				for (int i = 0; i < expected.length; i++) {
					log(result[i] + "\t\t" + expected[i]);
				}
				for (int i = expected.length; i < result.length; i++) {
					log(result[i] + "\t\t" + "  ");
				}

			}
			if (expected.length > result.length) {
				for (int i = 0; i < result.length; i++) {
					log(result[i] + "\t\t\t" + expected[i]);
				}
				for (int i = result.length; i < expected.length; i++) {
					log("  " + "\t\t\t" + expected[i]);
				}

			}
		}

	}
	
	/**
	 * Canceles the task with the given task ID
	 * 
	 * @param taskId
	 */
	public void cancelSchedulerTask(String taskId) throws ReflectionException, MBeanException, InstanceNotFoundException, IOException 
			 {

		String[] scheduleSignature = new String[] {  "String" };
		mbsc.invoke(schedulerMBean, "CancelTask", new Object[] {taskId}, scheduleSignature);
		System.out.println("Task with id: " + taskId + "was successfully canceled");
	}
	
	/**
	 * Creates a Calender and returns a Date(needed becouse of the deprication
	 * of some Date constructors)
	 * 
	 * @param y =
	 *            year
	 * @param m =
	 *            month
	 * @param d =
	 *            day
	 * @param h =
	 *            hour
	 * @param min =
	 *            minute
	 * @param s =
	 *            sec
	 * @return
	 */
	public static Date createDate(int y, int m, int d, int h, int min, int s) {
		return createDate(y, m, d, h, min, s, TimeZone.getDefault());

		/*Calendar cal = Calendar.getInstance();
		 cal.set(y, m, d, h, min, s);
		 // normalize the date --> cut millis away
		 cal.setTime( new Date ( (long)( (cal.getTimeInMillis()/1000) * 1000 ) ) );        
		 Date date = cal.getTime();
		 
		 return date;*/
	}

	public static Date createDate(int y, int m, int d, int h, int min, int s, TimeZone tz) {
		Calendar cal = Calendar.getInstance(tz);
		cal.set(y, m, d, h, min, s);
		// normalize the date --> cut millis away
		cal.setTime(new Date((long) ((cal.getTimeInMillis() / 1000) * 1000)));
		Date date = cal.getTime();

		return date;
	}

	public Date[] getFireTimes(String ScheTaskID, SAP_ITSAMJavaSchedulerTime startTime, SAP_ITSAMJavaSchedulerTime endTime) throws ReflectionException, InstanceNotFoundException,
			MBeanException, OpenDataException, IOException {
		Date[] fireTimesArr = null;

		String[] scheduleSignature = new String[] { "java.lang.String", "javax.management.openmbean.CompositeData", "javax.management.openmbean.CompositeData" };

		CompositeData[] fireTime = (CompositeData[]) mbsc.invoke(schedulerMBean, "GetFireTimes", new Object[] { ScheTaskID,
				SAP_ITSAMJavaSchedulerWrapper.getCDataForSAP_ITSAMJavaSchedulerTime(startTime), SAP_ITSAMJavaSchedulerWrapper.getCDataForSAP_ITSAMJavaSchedulerTime(endTime) },
				scheduleSignature);

		SAP_ITSAMJavaSchedulerFireTimeEvent[] fireTimes = SAP_ITSAMJavaSchedulerWrapper.getSAP_ITSAMJavaSchedulerFireTimeEventArrForCData(fireTime);

		SAP_ITSAMJavaSchedulerTime scheTimes[] = new SAP_ITSAMJavaSchedulerTime[fireTimes.length];
		Date innerDateArr[] = new Date[fireTimes.length];
		int i = 0;
		while (i < fireTimes.length) {

			scheTimes[i] = fireTimes[i].getTime();
			i++;
		}
		for (int d = 0; d < fireTimes.length; d++) {
			innerDateArr[d] = scheTimes[d].getTime();
		}
		fireTimesArr = innerDateArr;

		return fireTimesArr;
	}

	public TestDataType testCase(CronEntry croEntry, RecurringEntry recEntry, SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes,
			SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes, Date[] datesExpected, String testEntry) {
		boolean basicOPareOK = false;
		boolean testOK = false;
		TestDataType testDatatype = new TestDataType();
		String result = null;
		Date[] dt = null;
		String taskId = null;

		try {
			if ((croEntry != null) && (recEntry == null)) {
				taskId = createSchedulerTask(croEntry);
			}
			if ((croEntry == null) && (recEntry != null)) {
				taskId = createSchedulerTask(recEntry);
			}
			if ((croEntry != null) && (recEntry != null)) {
				taskId = createSchedulerTask(recEntry, croEntry);
			}

			Date[] date = getFireTimes(taskId, startTimeForGetFireTimes, endTimeForGetFireTimes);
			dt = date;
			basicOPareOK = true;
			testDatatype.setBoolean(basicOPareOK);
			testDatatype.setDates(date);
			testDatatype.setTaskId(taskId);

		} catch (Exception e) {
			basicOPareOK = false;
			result = "Unexpected Exception! Test will fail";
			log(result);
			logEnv.log(e);
			testDatatype.setBoolean(basicOPareOK);
			return testDatatype;

		}

		if (dt.length != datesExpected.length) {
			result = "The CronEntry: " + testEntry + " does not match the expected result! Different amount or results";
			log(result);
			testOK = false;
			testDatatype.setBoolean(testOK);
			return testDatatype;

		}
		if ((dt.length == 0) && (datesExpected.length == 0)) {
			result = "The CronEntry:" + testEntry + " match the expected result!";
			log(result);
			testOK = true;
			testDatatype.setBoolean(testOK);
			return testDatatype;

		} else {

			for (int i = 0; i < dt.length; i++) {

				//if (dt[i].toString().equals(datesExpected[i].toString()) == false) {
				if ( getNormalizedDate(dt[i]).getTime() != getNormalizedDate(datesExpected[i]).getTime() ) {
					result = "The CronEntry:" + testEntry + " does NOT MATCH the expected result!";
					log(result);
					testOK = false;
					testDatatype.setBoolean(testOK);
					return testDatatype;
				} else {
					result = "The CronEntry:" + testEntry + " match the expected result!";
					testOK = true;
					testDatatype.setBoolean(testOK);
				}

			}
		}
		log(result);
		return testDatatype;
	}

	public String createSchedulerTask(RecurringEntry recEntry) throws ReflectionException, MBeanException, InstanceNotFoundException, IOException, AttributeNotFoundException,
			OpenDataException {

		// first we build a normal scheduler task and than we convert it to
		// SAP_ITSAMJavaSchedulerTask becouse of the too complicated constructor
		// of the SAP_ITSAMJavaSchedulerTask

		SchedulerTask st = new SchedulerTask(SchedulerTaskID.newID(), getJobParameters().getJobDefinitionId(), getJobParameters().getJobParameterArr(),
				new RecurringEntry[] { recEntry }, null);

		SAP_ITSAMJavaSchedulerTask iTsamST = SAP_ITSAM_Helper.convertSchedulerTaskToSAP_ITSAM(st, null, null);

		// Note, we cannot take the signature from the operation info is it is
		// different from what is needed , so we build it ourselves.

		String[] scheduleSignature = new String[] { "javax.management.openmbean.CompositeData", "String" };

		String result = (String) mbsc.invoke(schedulerMBean, "Schedule", new Object[] { SAP_ITSAMJavaSchedulerWrapper.getCDataForSAP_ITSAMJavaSchedulerTask(iTsamST),
				"Administrator" }, scheduleSignature);

		System.out.println("TaskID of the new task is: " + result);
		return result;
	}

	public String createSchedulerTask(CronEntry cronEntry) throws ReflectionException, MBeanException, InstanceNotFoundException, IOException, AttributeNotFoundException,
			OpenDataException {

		SchedulerTask st = new SchedulerTask(SchedulerTaskID.newID(), getJobParameters().getJobDefinitionId(), getJobParameters().getJobParameterArr(), null,
				new CronEntry[] { cronEntry });

		SAP_ITSAMJavaSchedulerTask iTsamST = SAP_ITSAM_Helper.convertSchedulerTaskToSAP_ITSAM(st, null, null);

		String[] scheduleSignature = new String[] { "javax.management.openmbean.CompositeData", "String" };

		String result = (String) mbsc.invoke(schedulerMBean, "Schedule", new Object[] { SAP_ITSAMJavaSchedulerWrapper.getCDataForSAP_ITSAMJavaSchedulerTask(iTsamST),
				"Administrator" }, scheduleSignature);

		log("TaskID of the new task is: " + result);
		return result;
	}

	public String createSchedulerTask(RecurringEntry recEntry, CronEntry cronEntry) throws ReflectionException, MBeanException, InstanceNotFoundException, IOException,
			AttributeNotFoundException, OpenDataException {

		SchedulerTask st = new SchedulerTask(SchedulerTaskID.newID(), getJobParameters().getJobDefinitionId(), getJobParameters().getJobParameterArr(),
				new RecurringEntry[] { recEntry }, new CronEntry[] { cronEntry });

		SAP_ITSAMJavaSchedulerTask iTsamST = SAP_ITSAM_Helper.convertSchedulerTaskToSAP_ITSAM(st, null, null);

		String[] scheduleSignature = new String[] { "javax.management.openmbean.CompositeData", "String" };

		String result = (String) mbsc.invoke(schedulerMBean, "Schedule", new Object[] { SAP_ITSAMJavaSchedulerWrapper.getCDataForSAP_ITSAMJavaSchedulerTask(iTsamST),
				"Administrator" }, scheduleSignature);

		System.out.println("TaskID of the new task is: " + result);
		return result;
	}

	public JobParameterType getJobParameters() throws ReflectionException, MBeanException, InstanceNotFoundException, IOException, AttributeNotFoundException {
		String[] scheduleSignature = new String[] { "java.lang.String" };
		CompositeData jobDefinition = (CompositeData) mbsc.invoke(schedulerMBean, "GetJobDefinitionByName", new Object[] { "SleepJob" }, scheduleSignature);

		String jobDId = null;
		CompositeData[] params = null;

		jobDId = (String) jobDefinition.get("JobDefinitionID");
		params = (CompositeData[]) jobDefinition.get("Parameters");

		if (jobDId == null) {
			log("jobDefId is null ");

		} else {
			log("jobDefId is OK");

		}
		JobParameter p = null;
		SAP_ITSAMJavaSchedulerJobParameterDefinition[] sap_itsam_jparams = SAP_ITSAMJavaSchedulerWrapper.getSAP_ITSAMJavaSchedulerJobParameterDefinitionArrForCData(params);
		JobParameterDefinition[] jparams = new JobParameterDefinition[sap_itsam_jparams.length];
		for (int i = 0; i < sap_itsam_jparams.length; i++) {
			jparams[i] = SAP_ITSAM_Helper.convertSAP_ITSAMToJobParameterDefinition(sap_itsam_jparams[i]);

		}
		for (int i = 0; i < jparams.length; i++) {
			if (jparams[i].getName().equalsIgnoreCase("SleepTime")) {
				p = new JobParameter(jparams[i], "1000");
			}
		}
		JobParameter[] jp = new JobParameter[] { p };
		JobDefinitionID jobDefId = JobDefinitionID.parseID(jobDId);
		JobParameterType jpt = new JobParameterType(jp, jobDefId);
		return jpt;
	}

	public boolean testCronEntry(String cronEntry) {
		try {
			getCronEntry(cronEntry);
			return false;
		} catch (IllegalArgumentException e) {
			logEnv.log(e);
			return true;

		}

	}

	// Data used by more than one test
	public static Date dateNow = new Date();
	
	// TestCase1
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes1 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));

	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes1 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));

	public static String cron1 = "*/10:11:2:2:12:12";

	public static Date[] datesCron1 = new Date[] { createDate(2000, 11, 2, 12, 12, 0), createDate(2000, 11, 4, 12, 12, 0), createDate(2000, 11, 11, 12, 12, 0),
			createDate(2000, 11, 18, 12, 12, 0), createDate(2000, 11, 25, 12, 12, 0),

			createDate(2010, 11, 2, 12, 12, 0), createDate(2010, 11, 6, 12, 12, 0), createDate(2010, 11, 13, 12, 12, 0), createDate(2010, 11, 20, 12, 12, 0),
			createDate(2010, 11, 27, 12, 12, 0),

			createDate(2020, 11, 2, 12, 12, 0), createDate(2020, 11, 7, 12, 12, 0), createDate(2020, 11, 14, 12, 12, 0), createDate(2020, 11, 21, 12, 12, 0),
			createDate(2020, 11, 28, 12, 12, 0), };

	// TestCase2
	public static String cron2 = "*:*:*:*:1-19/3:7-57/18";

	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes2 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2012, 1, 1, 0, 0, 0));

	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes2 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2012, 2, 31, 23, 0, 0));

	public static Date[] datesCron2 = new Date[] {

	createDate(2012, 1, 1, 1, 7, 0), createDate(2012, 1, 1, 1, 25, 0), createDate(2012, 1, 1, 1, 43, 0), createDate(2012, 1, 1, 4, 7, 0), createDate(2012, 1, 1, 4, 25, 0),
			createDate(2012, 1, 1, 4, 43, 0), createDate(2012, 1, 1, 7, 7, 0), createDate(2012, 1, 1, 7, 25, 0), createDate(2012, 1, 1, 7, 43, 0),
			createDate(2012, 1, 1, 10, 7, 0), createDate(2012, 1, 1, 10, 25, 0), createDate(2012, 1, 1, 10, 43, 0), createDate(2012, 1, 1, 13, 7, 0),
			createDate(2012, 1, 1, 13, 25, 0), createDate(2012, 1, 1, 13, 43, 0), createDate(2012, 1, 1, 16, 7, 0), createDate(2012, 1, 1, 16, 25, 0),
			createDate(2012, 1, 1, 16, 43, 0), createDate(2012, 1, 1, 19, 7, 0), createDate(2012, 1, 1, 19, 25, 0), createDate(2012, 1, 1, 19, 43, 0),
			createDate(2012, 1, 2, 1, 7, 0), createDate(2012, 1, 2, 1, 25, 0), createDate(2012, 1, 2, 1, 43, 0), createDate(2012, 1, 2, 4, 7, 0), createDate(2012, 1, 2, 4, 25, 0),
			createDate(2012, 1, 2, 4, 43, 0), createDate(2012, 1, 2, 7, 7, 0), createDate(2012, 1, 2, 7, 25, 0), createDate(2012, 1, 2, 7, 43, 0),
			createDate(2012, 1, 2, 10, 7, 0), createDate(2012, 1, 2, 10, 25, 0), createDate(2012, 1, 2, 10, 43, 0), createDate(2012, 1, 2, 13, 7, 0),
			createDate(2012, 1, 2, 13, 25, 0), createDate(2012, 1, 2, 13, 43, 0), createDate(2012, 1, 2, 16, 7, 0), createDate(2012, 1, 2, 16, 25, 0),
			createDate(2012, 1, 2, 16, 43, 0), createDate(2012, 1, 2, 19, 7, 0), createDate(2012, 1, 2, 19, 25, 0), createDate(2012, 1, 2, 19, 43, 0),
			createDate(2012, 1, 3, 1, 7, 0), createDate(2012, 1, 3, 1, 25, 0), createDate(2012, 1, 3, 1, 43, 0), createDate(2012, 1, 3, 4, 7, 0), createDate(2012, 1, 3, 4, 25, 0),
			createDate(2012, 1, 3, 4, 43, 0), createDate(2012, 1, 3, 7, 7, 0), createDate(2012, 1, 3, 7, 25, 0), createDate(2012, 1, 3, 7, 43, 0),
			createDate(2012, 1, 3, 10, 7, 0), createDate(2012, 1, 3, 10, 25, 0), createDate(2012, 1, 3, 10, 43, 0), createDate(2012, 1, 3, 13, 7, 0),
			createDate(2012, 1, 3, 13, 25, 0), createDate(2012, 1, 3, 13, 43, 0), createDate(2012, 1, 3, 16, 7, 0), createDate(2012, 1, 3, 16, 25, 0),
			createDate(2012, 1, 3, 16, 43, 0), createDate(2012, 1, 3, 19, 7, 0), createDate(2012, 1, 3, 19, 25, 0), createDate(2012, 1, 3, 19, 43, 0),
			createDate(2012, 1, 4, 1, 7, 0), createDate(2012, 1, 4, 1, 25, 0), createDate(2012, 1, 4, 1, 43, 0), createDate(2012, 1, 4, 4, 7, 0), createDate(2012, 1, 4, 4, 25, 0),
			createDate(2012, 1, 4, 4, 43, 0), createDate(2012, 1, 4, 7, 7, 0), createDate(2012, 1, 4, 7, 25, 0), createDate(2012, 1, 4, 7, 43, 0),
			createDate(2012, 1, 4, 10, 7, 0), createDate(2012, 1, 4, 10, 25, 0), createDate(2012, 1, 4, 10, 43, 0), createDate(2012, 1, 4, 13, 7, 0),
			createDate(2012, 1, 4, 13, 25, 0), createDate(2012, 1, 4, 13, 43, 0), createDate(2012, 1, 4, 16, 7, 0), createDate(2012, 1, 4, 16, 25, 0),
			createDate(2012, 1, 4, 16, 43, 0), createDate(2012, 1, 4, 19, 7, 0), createDate(2012, 1, 4, 19, 25, 0), createDate(2012, 1, 4, 19, 43, 0),
			createDate(2012, 1, 5, 1, 7, 0), createDate(2012, 1, 5, 1, 25, 0), createDate(2012, 1, 5, 1, 43, 0), createDate(2012, 1, 5, 4, 7, 0), createDate(2012, 1, 5, 4, 25, 0),
			createDate(2012, 1, 5, 4, 43, 0), createDate(2012, 1, 5, 7, 7, 0), createDate(2012, 1, 5, 7, 25, 0), createDate(2012, 1, 5, 7, 43, 0),
			createDate(2012, 1, 5, 10, 7, 0), createDate(2012, 1, 5, 10, 25, 0), createDate(2012, 1, 5, 10, 43, 0), createDate(2012, 1, 5, 13, 7, 0),
			createDate(2012, 1, 5, 13, 25, 0), createDate(2012, 1, 5, 13, 43, 0), createDate(2012, 1, 5, 16, 7, 0), createDate(2012, 1, 5, 16, 25, 0),
			createDate(2012, 1, 5, 16, 43, 0), createDate(2012, 1, 5, 19, 7, 0), createDate(2012, 1, 5, 19, 25, 0), createDate(2012, 1, 5, 19, 43, 0),
			createDate(2012, 1, 6, 1, 7, 0), createDate(2012, 1, 6, 1, 25, 0), createDate(2012, 1, 6, 1, 43, 0), createDate(2012, 1, 6, 4, 7, 0), createDate(2012, 1, 6, 4, 25, 0),
			createDate(2012, 1, 6, 4, 43, 0), createDate(2012, 1, 6, 7, 7, 0), createDate(2012, 1, 6, 7, 25, 0), createDate(2012, 1, 6, 7, 43, 0),
			createDate(2012, 1, 6, 10, 7, 0), createDate(2012, 1, 6, 10, 25, 0), createDate(2012, 1, 6, 10, 43, 0), createDate(2012, 1, 6, 13, 7, 0),
			createDate(2012, 1, 6, 13, 25, 0), createDate(2012, 1, 6, 13, 43, 0), createDate(2012, 1, 6, 16, 7, 0), createDate(2012, 1, 6, 16, 25, 0),
			createDate(2012, 1, 6, 16, 43, 0), createDate(2012, 1, 6, 19, 7, 0), createDate(2012, 1, 6, 19, 25, 0), createDate(2012, 1, 6, 19, 43, 0),
			createDate(2012, 1, 7, 1, 7, 0), createDate(2012, 1, 7, 1, 25, 0), createDate(2012, 1, 7, 1, 43, 0), createDate(2012, 1, 7, 4, 7, 0), createDate(2012, 1, 7, 4, 25, 0),
			createDate(2012, 1, 7, 4, 43, 0), createDate(2012, 1, 7, 7, 7, 0), createDate(2012, 1, 7, 7, 25, 0), createDate(2012, 1, 7, 7, 43, 0),
			createDate(2012, 1, 7, 10, 7, 0), createDate(2012, 1, 7, 10, 25, 0), createDate(2012, 1, 7, 10, 43, 0), createDate(2012, 1, 7, 13, 7, 0),
			createDate(2012, 1, 7, 13, 25, 0), createDate(2012, 1, 7, 13, 43, 0), createDate(2012, 1, 7, 16, 7, 0), createDate(2012, 1, 7, 16, 25, 0),
			createDate(2012, 1, 7, 16, 43, 0), createDate(2012, 1, 7, 19, 7, 0), createDate(2012, 1, 7, 19, 25, 0), createDate(2012, 1, 7, 19, 43, 0),
			createDate(2012, 1, 8, 1, 7, 0), createDate(2012, 1, 8, 1, 25, 0), createDate(2012, 1, 8, 1, 43, 0), createDate(2012, 1, 8, 4, 7, 0), createDate(2012, 1, 8, 4, 25, 0),
			createDate(2012, 1, 8, 4, 43, 0), createDate(2012, 1, 8, 7, 7, 0), createDate(2012, 1, 8, 7, 25, 0), createDate(2012, 1, 8, 7, 43, 0),
			createDate(2012, 1, 8, 10, 7, 0), createDate(2012, 1, 8, 10, 25, 0), createDate(2012, 1, 8, 10, 43, 0), createDate(2012, 1, 8, 13, 7, 0),
			createDate(2012, 1, 8, 13, 25, 0), createDate(2012, 1, 8, 13, 43, 0), createDate(2012, 1, 8, 16, 7, 0), createDate(2012, 1, 8, 16, 25, 0),
			createDate(2012, 1, 8, 16, 43, 0), createDate(2012, 1, 8, 19, 7, 0), createDate(2012, 1, 8, 19, 25, 0), createDate(2012, 1, 8, 19, 43, 0),
			createDate(2012, 1, 9, 1, 7, 0), createDate(2012, 1, 9, 1, 25, 0), createDate(2012, 1, 9, 1, 43, 0), createDate(2012, 1, 9, 4, 7, 0), createDate(2012, 1, 9, 4, 25, 0),
			createDate(2012, 1, 9, 4, 43, 0), createDate(2012, 1, 9, 7, 7, 0), createDate(2012, 1, 9, 7, 25, 0), createDate(2012, 1, 9, 7, 43, 0),
			createDate(2012, 1, 9, 10, 7, 0), createDate(2012, 1, 9, 10, 25, 0), createDate(2012, 1, 9, 10, 43, 0), createDate(2012, 1, 9, 13, 7, 0),
			createDate(2012, 1, 9, 13, 25, 0), createDate(2012, 1, 9, 13, 43, 0), createDate(2012, 1, 9, 16, 7, 0), createDate(2012, 1, 9, 16, 25, 0),
			createDate(2012, 1, 9, 16, 43, 0), createDate(2012, 1, 9, 19, 7, 0), createDate(2012, 1, 9, 19, 25, 0), createDate(2012, 1, 9, 19, 43, 0),
			createDate(2012, 1, 10, 1, 7, 0), createDate(2012, 1, 10, 1, 25, 0), createDate(2012, 1, 10, 1, 43, 0), createDate(2012, 1, 10, 4, 7, 0),
			createDate(2012, 1, 10, 4, 25, 0), createDate(2012, 1, 10, 4, 43, 0), createDate(2012, 1, 10, 7, 7, 0), createDate(2012, 1, 10, 7, 25, 0),
			createDate(2012, 1, 10, 7, 43, 0), createDate(2012, 1, 10, 10, 7, 0), createDate(2012, 1, 10, 10, 25, 0), createDate(2012, 1, 10, 10, 43, 0),
			createDate(2012, 1, 10, 13, 7, 0), createDate(2012, 1, 10, 13, 25, 0), createDate(2012, 1, 10, 13, 43, 0), createDate(2012, 1, 10, 16, 7, 0),
			createDate(2012, 1, 10, 16, 25, 0), createDate(2012, 1, 10, 16, 43, 0), createDate(2012, 1, 10, 19, 7, 0), createDate(2012, 1, 10, 19, 25, 0),
			createDate(2012, 1, 10, 19, 43, 0), createDate(2012, 1, 11, 1, 7, 0), createDate(2012, 1, 11, 1, 25, 0), createDate(2012, 1, 11, 1, 43, 0),
			createDate(2012, 1, 11, 4, 7, 0), createDate(2012, 1, 11, 4, 25, 0), createDate(2012, 1, 11, 4, 43, 0), createDate(2012, 1, 11, 7, 7, 0),
			createDate(2012, 1, 11, 7, 25, 0), createDate(2012, 1, 11, 7, 43, 0), createDate(2012, 1, 11, 10, 7, 0), createDate(2012, 1, 11, 10, 25, 0),
			createDate(2012, 1, 11, 10, 43, 0), createDate(2012, 1, 11, 13, 7, 0), createDate(2012, 1, 11, 13, 25, 0), createDate(2012, 1, 11, 13, 43, 0),
			createDate(2012, 1, 11, 16, 7, 0), createDate(2012, 1, 11, 16, 25, 0), createDate(2012, 1, 11, 16, 43, 0), createDate(2012, 1, 11, 19, 7, 0),
			createDate(2012, 1, 11, 19, 25, 0), createDate(2012, 1, 11, 19, 43, 0), createDate(2012, 1, 12, 1, 7, 0), createDate(2012, 1, 12, 1, 25, 0),
			createDate(2012, 1, 12, 1, 43, 0), createDate(2012, 1, 12, 4, 7, 0), createDate(2012, 1, 12, 4, 25, 0), createDate(2012, 1, 12, 4, 43, 0),
			createDate(2012, 1, 12, 7, 7, 0), createDate(2012, 1, 12, 7, 25, 0), createDate(2012, 1, 12, 7, 43, 0), createDate(2012, 1, 12, 10, 7, 0),
			createDate(2012, 1, 12, 10, 25, 0), createDate(2012, 1, 12, 10, 43, 0), createDate(2012, 1, 12, 13, 7, 0), createDate(2012, 1, 12, 13, 25, 0),
			createDate(2012, 1, 12, 13, 43, 0), createDate(2012, 1, 12, 16, 7, 0), createDate(2012, 1, 12, 16, 25, 0), createDate(2012, 1, 12, 16, 43, 0),
			createDate(2012, 1, 12, 19, 7, 0), createDate(2012, 1, 12, 19, 25, 0), createDate(2012, 1, 12, 19, 43, 0), createDate(2012, 1, 13, 1, 7, 0),
			createDate(2012, 1, 13, 1, 25, 0), createDate(2012, 1, 13, 1, 43, 0), createDate(2012, 1, 13, 4, 7, 0), createDate(2012, 1, 13, 4, 25, 0),
			createDate(2012, 1, 13, 4, 43, 0), createDate(2012, 1, 13, 7, 7, 0), createDate(2012, 1, 13, 7, 25, 0), createDate(2012, 1, 13, 7, 43, 0),
			createDate(2012, 1, 13, 10, 7, 0), createDate(2012, 1, 13, 10, 25, 0), createDate(2012, 1, 13, 10, 43, 0), createDate(2012, 1, 13, 13, 7, 0),
			createDate(2012, 1, 13, 13, 25, 0), createDate(2012, 1, 13, 13, 43, 0), createDate(2012, 1, 13, 16, 7, 0), createDate(2012, 1, 13, 16, 25, 0),
			createDate(2012, 1, 13, 16, 43, 0), createDate(2012, 1, 13, 19, 7, 0), createDate(2012, 1, 13, 19, 25, 0), createDate(2012, 1, 13, 19, 43, 0),
			createDate(2012, 1, 14, 1, 7, 0), createDate(2012, 1, 14, 1, 25, 0), createDate(2012, 1, 14, 1, 43, 0), createDate(2012, 1, 14, 4, 7, 0),
			createDate(2012, 1, 14, 4, 25, 0), createDate(2012, 1, 14, 4, 43, 0), createDate(2012, 1, 14, 7, 7, 0), createDate(2012, 1, 14, 7, 25, 0),
			createDate(2012, 1, 14, 7, 43, 0), createDate(2012, 1, 14, 10, 7, 0), createDate(2012, 1, 14, 10, 25, 0), createDate(2012, 1, 14, 10, 43, 0),
			createDate(2012, 1, 14, 13, 7, 0), createDate(2012, 1, 14, 13, 25, 0), createDate(2012, 1, 14, 13, 43, 0), createDate(2012, 1, 14, 16, 7, 0),
			createDate(2012, 1, 14, 16, 25, 0), createDate(2012, 1, 14, 16, 43, 0), createDate(2012, 1, 14, 19, 7, 0), createDate(2012, 1, 14, 19, 25, 0),
			createDate(2012, 1, 14, 19, 43, 0), createDate(2012, 1, 15, 1, 7, 0), createDate(2012, 1, 15, 1, 25, 0), createDate(2012, 1, 15, 1, 43, 0),
			createDate(2012, 1, 15, 4, 7, 0), createDate(2012, 1, 15, 4, 25, 0), createDate(2012, 1, 15, 4, 43, 0), createDate(2012, 1, 15, 7, 7, 0),
			createDate(2012, 1, 15, 7, 25, 0), createDate(2012, 1, 15, 7, 43, 0), createDate(2012, 1, 15, 10, 7, 0), createDate(2012, 1, 15, 10, 25, 0),
			createDate(2012, 1, 15, 10, 43, 0), createDate(2012, 1, 15, 13, 7, 0), createDate(2012, 1, 15, 13, 25, 0), createDate(2012, 1, 15, 13, 43, 0),
			createDate(2012, 1, 15, 16, 7, 0), createDate(2012, 1, 15, 16, 25, 0), createDate(2012, 1, 15, 16, 43, 0), createDate(2012, 1, 15, 19, 7, 0),
			createDate(2012, 1, 15, 19, 25, 0), createDate(2012, 1, 15, 19, 43, 0), createDate(2012, 1, 16, 1, 7, 0), createDate(2012, 1, 16, 1, 25, 0),
			createDate(2012, 1, 16, 1, 43, 0), createDate(2012, 1, 16, 4, 7, 0), createDate(2012, 1, 16, 4, 25, 0), createDate(2012, 1, 16, 4, 43, 0),
			createDate(2012, 1, 16, 7, 7, 0), createDate(2012, 1, 16, 7, 25, 0), createDate(2012, 1, 16, 7, 43, 0), createDate(2012, 1, 16, 10, 7, 0),
			createDate(2012, 1, 16, 10, 25, 0), createDate(2012, 1, 16, 10, 43, 0), createDate(2012, 1, 16, 13, 7, 0), createDate(2012, 1, 16, 13, 25, 0),
			createDate(2012, 1, 16, 13, 43, 0), createDate(2012, 1, 16, 16, 7, 0), createDate(2012, 1, 16, 16, 25, 0), createDate(2012, 1, 16, 16, 43, 0),
			createDate(2012, 1, 16, 19, 7, 0), createDate(2012, 1, 16, 19, 25, 0), createDate(2012, 1, 16, 19, 43, 0), createDate(2012, 1, 17, 1, 7, 0),
			createDate(2012, 1, 17, 1, 25, 0), createDate(2012, 1, 17, 1, 43, 0), createDate(2012, 1, 17, 4, 7, 0), createDate(2012, 1, 17, 4, 25, 0),
			createDate(2012, 1, 17, 4, 43, 0), createDate(2012, 1, 17, 7, 7, 0), createDate(2012, 1, 17, 7, 25, 0), createDate(2012, 1, 17, 7, 43, 0),
			createDate(2012, 1, 17, 10, 7, 0), createDate(2012, 1, 17, 10, 25, 0), createDate(2012, 1, 17, 10, 43, 0), createDate(2012, 1, 17, 13, 7, 0),
			createDate(2012, 1, 17, 13, 25, 0), createDate(2012, 1, 17, 13, 43, 0), createDate(2012, 1, 17, 16, 7, 0), createDate(2012, 1, 17, 16, 25, 0),
			createDate(2012, 1, 17, 16, 43, 0), createDate(2012, 1, 17, 19, 7, 0), createDate(2012, 1, 17, 19, 25, 0), createDate(2012, 1, 17, 19, 43, 0),
			createDate(2012, 1, 18, 1, 7, 0), createDate(2012, 1, 18, 1, 25, 0), createDate(2012, 1, 18, 1, 43, 0), createDate(2012, 1, 18, 4, 7, 0),
			createDate(2012, 1, 18, 4, 25, 0), createDate(2012, 1, 18, 4, 43, 0), createDate(2012, 1, 18, 7, 7, 0), createDate(2012, 1, 18, 7, 25, 0),
			createDate(2012, 1, 18, 7, 43, 0), createDate(2012, 1, 18, 10, 7, 0), createDate(2012, 1, 18, 10, 25, 0), createDate(2012, 1, 18, 10, 43, 0),
			createDate(2012, 1, 18, 13, 7, 0), createDate(2012, 1, 18, 13, 25, 0), createDate(2012, 1, 18, 13, 43, 0), createDate(2012, 1, 18, 16, 7, 0),
			createDate(2012, 1, 18, 16, 25, 0), createDate(2012, 1, 18, 16, 43, 0), createDate(2012, 1, 18, 19, 7, 0), createDate(2012, 1, 18, 19, 25, 0),
			createDate(2012, 1, 18, 19, 43, 0), createDate(2012, 1, 19, 1, 7, 0), createDate(2012, 1, 19, 1, 25, 0), createDate(2012, 1, 19, 1, 43, 0),
			createDate(2012, 1, 19, 4, 7, 0), createDate(2012, 1, 19, 4, 25, 0), createDate(2012, 1, 19, 4, 43, 0), createDate(2012, 1, 19, 7, 7, 0),
			createDate(2012, 1, 19, 7, 25, 0), createDate(2012, 1, 19, 7, 43, 0), createDate(2012, 1, 19, 10, 7, 0), createDate(2012, 1, 19, 10, 25, 0),
			createDate(2012, 1, 19, 10, 43, 0), createDate(2012, 1, 19, 13, 7, 0), createDate(2012, 1, 19, 13, 25, 0), createDate(2012, 1, 19, 13, 43, 0),
			createDate(2012, 1, 19, 16, 7, 0), createDate(2012, 1, 19, 16, 25, 0), createDate(2012, 1, 19, 16, 43, 0), createDate(2012, 1, 19, 19, 7, 0),
			createDate(2012, 1, 19, 19, 25, 0), createDate(2012, 1, 19, 19, 43, 0), createDate(2012, 1, 20, 1, 7, 0), createDate(2012, 1, 20, 1, 25, 0),
			createDate(2012, 1, 20, 1, 43, 0), createDate(2012, 1, 20, 4, 7, 0), createDate(2012, 1, 20, 4, 25, 0), createDate(2012, 1, 20, 4, 43, 0),
			createDate(2012, 1, 20, 7, 7, 0), createDate(2012, 1, 20, 7, 25, 0), createDate(2012, 1, 20, 7, 43, 0), createDate(2012, 1, 20, 10, 7, 0),
			createDate(2012, 1, 20, 10, 25, 0), createDate(2012, 1, 20, 10, 43, 0), createDate(2012, 1, 20, 13, 7, 0), createDate(2012, 1, 20, 13, 25, 0),
			createDate(2012, 1, 20, 13, 43, 0), createDate(2012, 1, 20, 16, 7, 0), createDate(2012, 1, 20, 16, 25, 0), createDate(2012, 1, 20, 16, 43, 0),
			createDate(2012, 1, 20, 19, 7, 0), createDate(2012, 1, 20, 19, 25, 0), createDate(2012, 1, 20, 19, 43, 0), createDate(2012, 1, 21, 1, 7, 0),
			createDate(2012, 1, 21, 1, 25, 0), createDate(2012, 1, 21, 1, 43, 0), createDate(2012, 1, 21, 4, 7, 0), createDate(2012, 1, 21, 4, 25, 0),
			createDate(2012, 1, 21, 4, 43, 0), createDate(2012, 1, 21, 7, 7, 0), createDate(2012, 1, 21, 7, 25, 0), createDate(2012, 1, 21, 7, 43, 0),
			createDate(2012, 1, 21, 10, 7, 0), createDate(2012, 1, 21, 10, 25, 0), createDate(2012, 1, 21, 10, 43, 0), createDate(2012, 1, 21, 13, 7, 0),
			createDate(2012, 1, 21, 13, 25, 0), createDate(2012, 1, 21, 13, 43, 0), createDate(2012, 1, 21, 16, 7, 0), createDate(2012, 1, 21, 16, 25, 0),
			createDate(2012, 1, 21, 16, 43, 0), createDate(2012, 1, 21, 19, 7, 0), createDate(2012, 1, 21, 19, 25, 0), createDate(2012, 1, 21, 19, 43, 0),
			createDate(2012, 1, 22, 1, 7, 0), createDate(2012, 1, 22, 1, 25, 0), createDate(2012, 1, 22, 1, 43, 0), createDate(2012, 1, 22, 4, 7, 0),
			createDate(2012, 1, 22, 4, 25, 0), createDate(2012, 1, 22, 4, 43, 0), createDate(2012, 1, 22, 7, 7, 0), createDate(2012, 1, 22, 7, 25, 0),
			createDate(2012, 1, 22, 7, 43, 0), createDate(2012, 1, 22, 10, 7, 0), createDate(2012, 1, 22, 10, 25, 0), createDate(2012, 1, 22, 10, 43, 0),
			createDate(2012, 1, 22, 13, 7, 0), createDate(2012, 1, 22, 13, 25, 0), createDate(2012, 1, 22, 13, 43, 0), createDate(2012, 1, 22, 16, 7, 0),
			createDate(2012, 1, 22, 16, 25, 0), createDate(2012, 1, 22, 16, 43, 0), createDate(2012, 1, 22, 19, 7, 0), createDate(2012, 1, 22, 19, 25, 0),
			createDate(2012, 1, 22, 19, 43, 0), createDate(2012, 1, 23, 1, 7, 0), createDate(2012, 1, 23, 1, 25, 0), createDate(2012, 1, 23, 1, 43, 0),
			createDate(2012, 1, 23, 4, 7, 0), createDate(2012, 1, 23, 4, 25, 0), createDate(2012, 1, 23, 4, 43, 0), createDate(2012, 1, 23, 7, 7, 0),
			createDate(2012, 1, 23, 7, 25, 0), createDate(2012, 1, 23, 7, 43, 0), createDate(2012, 1, 23, 10, 7, 0), createDate(2012, 1, 23, 10, 25, 0),
			createDate(2012, 1, 23, 10, 43, 0), createDate(2012, 1, 23, 13, 7, 0), createDate(2012, 1, 23, 13, 25, 0), createDate(2012, 1, 23, 13, 43, 0),
			createDate(2012, 1, 23, 16, 7, 0), createDate(2012, 1, 23, 16, 25, 0), createDate(2012, 1, 23, 16, 43, 0), createDate(2012, 1, 23, 19, 7, 0),
			createDate(2012, 1, 23, 19, 25, 0), createDate(2012, 1, 23, 19, 43, 0), createDate(2012, 1, 24, 1, 7, 0), createDate(2012, 1, 24, 1, 25, 0),
			createDate(2012, 1, 24, 1, 43, 0), createDate(2012, 1, 24, 4, 7, 0), createDate(2012, 1, 24, 4, 25, 0), createDate(2012, 1, 24, 4, 43, 0),
			createDate(2012, 1, 24, 7, 7, 0), createDate(2012, 1, 24, 7, 25, 0), createDate(2012, 1, 24, 7, 43, 0), createDate(2012, 1, 24, 10, 7, 0),
			createDate(2012, 1, 24, 10, 25, 0), createDate(2012, 1, 24, 10, 43, 0), createDate(2012, 1, 24, 13, 7, 0), createDate(2012, 1, 24, 13, 25, 0),
			createDate(2012, 1, 24, 13, 43, 0), createDate(2012, 1, 24, 16, 7, 0), createDate(2012, 1, 24, 16, 25, 0), createDate(2012, 1, 24, 16, 43, 0),
			createDate(2012, 1, 24, 19, 7, 0), createDate(2012, 1, 24, 19, 25, 0), createDate(2012, 1, 24, 19, 43, 0), createDate(2012, 1, 25, 1, 7, 0),
			createDate(2012, 1, 25, 1, 25, 0), createDate(2012, 1, 25, 1, 43, 0), createDate(2012, 1, 25, 4, 7, 0), createDate(2012, 1, 25, 4, 25, 0),
			createDate(2012, 1, 25, 4, 43, 0), createDate(2012, 1, 25, 7, 7, 0), createDate(2012, 1, 25, 7, 25, 0), createDate(2012, 1, 25, 7, 43, 0),
			createDate(2012, 1, 25, 10, 7, 0), createDate(2012, 1, 25, 10, 25, 0), createDate(2012, 1, 25, 10, 43, 0), createDate(2012, 1, 25, 13, 7, 0),
			createDate(2012, 1, 25, 13, 25, 0), createDate(2012, 1, 25, 13, 43, 0), createDate(2012, 1, 25, 16, 7, 0), createDate(2012, 1, 25, 16, 25, 0),
			createDate(2012, 1, 25, 16, 43, 0), createDate(2012, 1, 25, 19, 7, 0), createDate(2012, 1, 25, 19, 25, 0), createDate(2012, 1, 25, 19, 43, 0),
			createDate(2012, 1, 26, 1, 7, 0), createDate(2012, 1, 26, 1, 25, 0), createDate(2012, 1, 26, 1, 43, 0), createDate(2012, 1, 26, 4, 7, 0),
			createDate(2012, 1, 26, 4, 25, 0), createDate(2012, 1, 26, 4, 43, 0), createDate(2012, 1, 26, 7, 7, 0), createDate(2012, 1, 26, 7, 25, 0),
			createDate(2012, 1, 26, 7, 43, 0), createDate(2012, 1, 26, 10, 7, 0), createDate(2012, 1, 26, 10, 25, 0), createDate(2012, 1, 26, 10, 43, 0),
			createDate(2012, 1, 26, 13, 7, 0), createDate(2012, 1, 26, 13, 25, 0), createDate(2012, 1, 26, 13, 43, 0), createDate(2012, 1, 26, 16, 7, 0),
			createDate(2012, 1, 26, 16, 25, 0), createDate(2012, 1, 26, 16, 43, 0), createDate(2012, 1, 26, 19, 7, 0), createDate(2012, 1, 26, 19, 25, 0),
			createDate(2012, 1, 26, 19, 43, 0), createDate(2012, 1, 27, 1, 7, 0), createDate(2012, 1, 27, 1, 25, 0), createDate(2012, 1, 27, 1, 43, 0),
			createDate(2012, 1, 27, 4, 7, 0), createDate(2012, 1, 27, 4, 25, 0), createDate(2012, 1, 27, 4, 43, 0), createDate(2012, 1, 27, 7, 7, 0),
			createDate(2012, 1, 27, 7, 25, 0), createDate(2012, 1, 27, 7, 43, 0), createDate(2012, 1, 27, 10, 7, 0), createDate(2012, 1, 27, 10, 25, 0),
			createDate(2012, 1, 27, 10, 43, 0), createDate(2012, 1, 27, 13, 7, 0), createDate(2012, 1, 27, 13, 25, 0), createDate(2012, 1, 27, 13, 43, 0),
			createDate(2012, 1, 27, 16, 7, 0), createDate(2012, 1, 27, 16, 25, 0), createDate(2012, 1, 27, 16, 43, 0), createDate(2012, 1, 27, 19, 7, 0),
			createDate(2012, 1, 27, 19, 25, 0), createDate(2012, 1, 27, 19, 43, 0), createDate(2012, 1, 28, 1, 7, 0), createDate(2012, 1, 28, 1, 25, 0),
			createDate(2012, 1, 28, 1, 43, 0), createDate(2012, 1, 28, 4, 7, 0), createDate(2012, 1, 28, 4, 25, 0), createDate(2012, 1, 28, 4, 43, 0),
			createDate(2012, 1, 28, 7, 7, 0), createDate(2012, 1, 28, 7, 25, 0), createDate(2012, 1, 28, 7, 43, 0), createDate(2012, 1, 28, 10, 7, 0),
			createDate(2012, 1, 28, 10, 25, 0), createDate(2012, 1, 28, 10, 43, 0), createDate(2012, 1, 28, 13, 7, 0), createDate(2012, 1, 28, 13, 25, 0),
			createDate(2012, 1, 28, 13, 43, 0), createDate(2012, 1, 28, 16, 7, 0), createDate(2012, 1, 28, 16, 25, 0), createDate(2012, 1, 28, 16, 43, 0),
			createDate(2012, 1, 28, 19, 7, 0), createDate(2012, 1, 28, 19, 25, 0), createDate(2012, 1, 28, 19, 43, 0), createDate(2012, 1, 29, 1, 7, 0),
			createDate(2012, 1, 29, 1, 25, 0), createDate(2012, 1, 29, 1, 43, 0), createDate(2012, 1, 29, 4, 7, 0), createDate(2012, 1, 29, 4, 25, 0),
			createDate(2012, 1, 29, 4, 43, 0), createDate(2012, 1, 29, 7, 7, 0), createDate(2012, 1, 29, 7, 25, 0), createDate(2012, 1, 29, 7, 43, 0),
			createDate(2012, 1, 29, 10, 7, 0), createDate(2012, 1, 29, 10, 25, 0), createDate(2012, 1, 29, 10, 43, 0), createDate(2012, 1, 29, 13, 7, 0),
			createDate(2012, 1, 29, 13, 25, 0), createDate(2012, 1, 29, 13, 43, 0), createDate(2012, 1, 29, 16, 7, 0), createDate(2012, 1, 29, 16, 25, 0),
			createDate(2012, 1, 29, 16, 43, 0), createDate(2012, 1, 29, 19, 7, 0), createDate(2012, 1, 29, 19, 25, 0), createDate(2012, 1, 29, 19, 43, 0),
			createDate(2012, 2, 1, 1, 7, 0), createDate(2012, 2, 1, 1, 25, 0), createDate(2012, 2, 1, 1, 43, 0), createDate(2012, 2, 1, 4, 7, 0), createDate(2012, 2, 1, 4, 25, 0),
			createDate(2012, 2, 1, 4, 43, 0), createDate(2012, 2, 1, 7, 7, 0), createDate(2012, 2, 1, 7, 25, 0), createDate(2012, 2, 1, 7, 43, 0),
			createDate(2012, 2, 1, 10, 7, 0), createDate(2012, 2, 1, 10, 25, 0), createDate(2012, 2, 1, 10, 43, 0), createDate(2012, 2, 1, 13, 7, 0),
			createDate(2012, 2, 1, 13, 25, 0), createDate(2012, 2, 1, 13, 43, 0), createDate(2012, 2, 1, 16, 7, 0), createDate(2012, 2, 1, 16, 25, 0),
			createDate(2012, 2, 1, 16, 43, 0), createDate(2012, 2, 1, 19, 7, 0), createDate(2012, 2, 1, 19, 25, 0), createDate(2012, 2, 1, 19, 43, 0),
			createDate(2012, 2, 2, 1, 7, 0), createDate(2012, 2, 2, 1, 25, 0), createDate(2012, 2, 2, 1, 43, 0), createDate(2012, 2, 2, 4, 7, 0), createDate(2012, 2, 2, 4, 25, 0),
			createDate(2012, 2, 2, 4, 43, 0), createDate(2012, 2, 2, 7, 7, 0), createDate(2012, 2, 2, 7, 25, 0), createDate(2012, 2, 2, 7, 43, 0),
			createDate(2012, 2, 2, 10, 7, 0), createDate(2012, 2, 2, 10, 25, 0), createDate(2012, 2, 2, 10, 43, 0), createDate(2012, 2, 2, 13, 7, 0),
			createDate(2012, 2, 2, 13, 25, 0), createDate(2012, 2, 2, 13, 43, 0), createDate(2012, 2, 2, 16, 7, 0), createDate(2012, 2, 2, 16, 25, 0),
			createDate(2012, 2, 2, 16, 43, 0), createDate(2012, 2, 2, 19, 7, 0), createDate(2012, 2, 2, 19, 25, 0), createDate(2012, 2, 2, 19, 43, 0),
			createDate(2012, 2, 3, 1, 7, 0), createDate(2012, 2, 3, 1, 25, 0), createDate(2012, 2, 3, 1, 43, 0), createDate(2012, 2, 3, 4, 7, 0), createDate(2012, 2, 3, 4, 25, 0),
			createDate(2012, 2, 3, 4, 43, 0), createDate(2012, 2, 3, 7, 7, 0), createDate(2012, 2, 3, 7, 25, 0), createDate(2012, 2, 3, 7, 43, 0),
			createDate(2012, 2, 3, 10, 7, 0), createDate(2012, 2, 3, 10, 25, 0), createDate(2012, 2, 3, 10, 43, 0), createDate(2012, 2, 3, 13, 7, 0),
			createDate(2012, 2, 3, 13, 25, 0), createDate(2012, 2, 3, 13, 43, 0), createDate(2012, 2, 3, 16, 7, 0), createDate(2012, 2, 3, 16, 25, 0),
			createDate(2012, 2, 3, 16, 43, 0), createDate(2012, 2, 3, 19, 7, 0), createDate(2012, 2, 3, 19, 25, 0), createDate(2012, 2, 3, 19, 43, 0),
			createDate(2012, 2, 4, 1, 7, 0), createDate(2012, 2, 4, 1, 25, 0), createDate(2012, 2, 4, 1, 43, 0), createDate(2012, 2, 4, 4, 7, 0), createDate(2012, 2, 4, 4, 25, 0),
			createDate(2012, 2, 4, 4, 43, 0), createDate(2012, 2, 4, 7, 7, 0), createDate(2012, 2, 4, 7, 25, 0), createDate(2012, 2, 4, 7, 43, 0),
			createDate(2012, 2, 4, 10, 7, 0), createDate(2012, 2, 4, 10, 25, 0), createDate(2012, 2, 4, 10, 43, 0), createDate(2012, 2, 4, 13, 7, 0),
			createDate(2012, 2, 4, 13, 25, 0), createDate(2012, 2, 4, 13, 43, 0), createDate(2012, 2, 4, 16, 7, 0), createDate(2012, 2, 4, 16, 25, 0),
			createDate(2012, 2, 4, 16, 43, 0), createDate(2012, 2, 4, 19, 7, 0), createDate(2012, 2, 4, 19, 25, 0), createDate(2012, 2, 4, 19, 43, 0),
			createDate(2012, 2, 5, 1, 7, 0), createDate(2012, 2, 5, 1, 25, 0), createDate(2012, 2, 5, 1, 43, 0), createDate(2012, 2, 5, 4, 7, 0), createDate(2012, 2, 5, 4, 25, 0),
			createDate(2012, 2, 5, 4, 43, 0), createDate(2012, 2, 5, 7, 7, 0), createDate(2012, 2, 5, 7, 25, 0), createDate(2012, 2, 5, 7, 43, 0),
			createDate(2012, 2, 5, 10, 7, 0), createDate(2012, 2, 5, 10, 25, 0), createDate(2012, 2, 5, 10, 43, 0), createDate(2012, 2, 5, 13, 7, 0),
			createDate(2012, 2, 5, 13, 25, 0), createDate(2012, 2, 5, 13, 43, 0), createDate(2012, 2, 5, 16, 7, 0), createDate(2012, 2, 5, 16, 25, 0),
			createDate(2012, 2, 5, 16, 43, 0), createDate(2012, 2, 5, 19, 7, 0), createDate(2012, 2, 5, 19, 25, 0), createDate(2012, 2, 5, 19, 43, 0),
			createDate(2012, 2, 6, 1, 7, 0), createDate(2012, 2, 6, 1, 25, 0), createDate(2012, 2, 6, 1, 43, 0), createDate(2012, 2, 6, 4, 7, 0), createDate(2012, 2, 6, 4, 25, 0),
			createDate(2012, 2, 6, 4, 43, 0), createDate(2012, 2, 6, 7, 7, 0), createDate(2012, 2, 6, 7, 25, 0), createDate(2012, 2, 6, 7, 43, 0),
			createDate(2012, 2, 6, 10, 7, 0), createDate(2012, 2, 6, 10, 25, 0), createDate(2012, 2, 6, 10, 43, 0), createDate(2012, 2, 6, 13, 7, 0),
			createDate(2012, 2, 6, 13, 25, 0), createDate(2012, 2, 6, 13, 43, 0), createDate(2012, 2, 6, 16, 7, 0), createDate(2012, 2, 6, 16, 25, 0),
			createDate(2012, 2, 6, 16, 43, 0), createDate(2012, 2, 6, 19, 7, 0), createDate(2012, 2, 6, 19, 25, 0), createDate(2012, 2, 6, 19, 43, 0),
			createDate(2012, 2, 7, 1, 7, 0), createDate(2012, 2, 7, 1, 25, 0), createDate(2012, 2, 7, 1, 43, 0), createDate(2012, 2, 7, 4, 7, 0), createDate(2012, 2, 7, 4, 25, 0),
			createDate(2012, 2, 7, 4, 43, 0), createDate(2012, 2, 7, 7, 7, 0), createDate(2012, 2, 7, 7, 25, 0), createDate(2012, 2, 7, 7, 43, 0),
			createDate(2012, 2, 7, 10, 7, 0), createDate(2012, 2, 7, 10, 25, 0), createDate(2012, 2, 7, 10, 43, 0), createDate(2012, 2, 7, 13, 7, 0),
			createDate(2012, 2, 7, 13, 25, 0), createDate(2012, 2, 7, 13, 43, 0), createDate(2012, 2, 7, 16, 7, 0), createDate(2012, 2, 7, 16, 25, 0),
			createDate(2012, 2, 7, 16, 43, 0), createDate(2012, 2, 7, 19, 7, 0), createDate(2012, 2, 7, 19, 25, 0), createDate(2012, 2, 7, 19, 43, 0),
			createDate(2012, 2, 8, 1, 7, 0), createDate(2012, 2, 8, 1, 25, 0), createDate(2012, 2, 8, 1, 43, 0), createDate(2012, 2, 8, 4, 7, 0), createDate(2012, 2, 8, 4, 25, 0),
			createDate(2012, 2, 8, 4, 43, 0), createDate(2012, 2, 8, 7, 7, 0), createDate(2012, 2, 8, 7, 25, 0), createDate(2012, 2, 8, 7, 43, 0),
			createDate(2012, 2, 8, 10, 7, 0), createDate(2012, 2, 8, 10, 25, 0), createDate(2012, 2, 8, 10, 43, 0), createDate(2012, 2, 8, 13, 7, 0),
			createDate(2012, 2, 8, 13, 25, 0), createDate(2012, 2, 8, 13, 43, 0), createDate(2012, 2, 8, 16, 7, 0), createDate(2012, 2, 8, 16, 25, 0),
			createDate(2012, 2, 8, 16, 43, 0), createDate(2012, 2, 8, 19, 7, 0), createDate(2012, 2, 8, 19, 25, 0), createDate(2012, 2, 8, 19, 43, 0),
			createDate(2012, 2, 9, 1, 7, 0), createDate(2012, 2, 9, 1, 25, 0), createDate(2012, 2, 9, 1, 43, 0), createDate(2012, 2, 9, 4, 7, 0), createDate(2012, 2, 9, 4, 25, 0),
			createDate(2012, 2, 9, 4, 43, 0), createDate(2012, 2, 9, 7, 7, 0), createDate(2012, 2, 9, 7, 25, 0), createDate(2012, 2, 9, 7, 43, 0),
			createDate(2012, 2, 9, 10, 7, 0), createDate(2012, 2, 9, 10, 25, 0), createDate(2012, 2, 9, 10, 43, 0), createDate(2012, 2, 9, 13, 7, 0),
			createDate(2012, 2, 9, 13, 25, 0), createDate(2012, 2, 9, 13, 43, 0), createDate(2012, 2, 9, 16, 7, 0), createDate(2012, 2, 9, 16, 25, 0),
			createDate(2012, 2, 9, 16, 43, 0), createDate(2012, 2, 9, 19, 7, 0), createDate(2012, 2, 9, 19, 25, 0), createDate(2012, 2, 9, 19, 43, 0),
			createDate(2012, 2, 10, 1, 7, 0), createDate(2012, 2, 10, 1, 25, 0), createDate(2012, 2, 10, 1, 43, 0), createDate(2012, 2, 10, 4, 7, 0),
			createDate(2012, 2, 10, 4, 25, 0), createDate(2012, 2, 10, 4, 43, 0), createDate(2012, 2, 10, 7, 7, 0), createDate(2012, 2, 10, 7, 25, 0),
			createDate(2012, 2, 10, 7, 43, 0), createDate(2012, 2, 10, 10, 7, 0), createDate(2012, 2, 10, 10, 25, 0), createDate(2012, 2, 10, 10, 43, 0),
			createDate(2012, 2, 10, 13, 7, 0), createDate(2012, 2, 10, 13, 25, 0), createDate(2012, 2, 10, 13, 43, 0), createDate(2012, 2, 10, 16, 7, 0),
			createDate(2012, 2, 10, 16, 25, 0), createDate(2012, 2, 10, 16, 43, 0), createDate(2012, 2, 10, 19, 7, 0), createDate(2012, 2, 10, 19, 25, 0),
			createDate(2012, 2, 10, 19, 43, 0), createDate(2012, 2, 11, 1, 7, 0), createDate(2012, 2, 11, 1, 25, 0), createDate(2012, 2, 11, 1, 43, 0),
			createDate(2012, 2, 11, 4, 7, 0), createDate(2012, 2, 11, 4, 25, 0), createDate(2012, 2, 11, 4, 43, 0), createDate(2012, 2, 11, 7, 7, 0),
			createDate(2012, 2, 11, 7, 25, 0), createDate(2012, 2, 11, 7, 43, 0), createDate(2012, 2, 11, 10, 7, 0), createDate(2012, 2, 11, 10, 25, 0),
			createDate(2012, 2, 11, 10, 43, 0), createDate(2012, 2, 11, 13, 7, 0), createDate(2012, 2, 11, 13, 25, 0), createDate(2012, 2, 11, 13, 43, 0),
			createDate(2012, 2, 11, 16, 7, 0), createDate(2012, 2, 11, 16, 25, 0), createDate(2012, 2, 11, 16, 43, 0), createDate(2012, 2, 11, 19, 7, 0),
			createDate(2012, 2, 11, 19, 25, 0), createDate(2012, 2, 11, 19, 43, 0), createDate(2012, 2, 12, 1, 7, 0), createDate(2012, 2, 12, 1, 25, 0),
			createDate(2012, 2, 12, 1, 43, 0), createDate(2012, 2, 12, 4, 7, 0), createDate(2012, 2, 12, 4, 25, 0), createDate(2012, 2, 12, 4, 43, 0),
			createDate(2012, 2, 12, 7, 7, 0), createDate(2012, 2, 12, 7, 25, 0), createDate(2012, 2, 12, 7, 43, 0), createDate(2012, 2, 12, 10, 7, 0),
			createDate(2012, 2, 12, 10, 25, 0), createDate(2012, 2, 12, 10, 43, 0), createDate(2012, 2, 12, 13, 7, 0), createDate(2012, 2, 12, 13, 25, 0),
			createDate(2012, 2, 12, 13, 43, 0), createDate(2012, 2, 12, 16, 7, 0), createDate(2012, 2, 12, 16, 25, 0), createDate(2012, 2, 12, 16, 43, 0),
			createDate(2012, 2, 12, 19, 7, 0), createDate(2012, 2, 12, 19, 25, 0), createDate(2012, 2, 12, 19, 43, 0), createDate(2012, 2, 13, 1, 7, 0),
			createDate(2012, 2, 13, 1, 25, 0), createDate(2012, 2, 13, 1, 43, 0), createDate(2012, 2, 13, 4, 7, 0), createDate(2012, 2, 13, 4, 25, 0),
			createDate(2012, 2, 13, 4, 43, 0), createDate(2012, 2, 13, 7, 7, 0), createDate(2012, 2, 13, 7, 25, 0), createDate(2012, 2, 13, 7, 43, 0),
			createDate(2012, 2, 13, 10, 7, 0), createDate(2012, 2, 13, 10, 25, 0), createDate(2012, 2, 13, 10, 43, 0), createDate(2012, 2, 13, 13, 7, 0),
			createDate(2012, 2, 13, 13, 25, 0), createDate(2012, 2, 13, 13, 43, 0), createDate(2012, 2, 13, 16, 7, 0), createDate(2012, 2, 13, 16, 25, 0),
			createDate(2012, 2, 13, 16, 43, 0), createDate(2012, 2, 13, 19, 7, 0), createDate(2012, 2, 13, 19, 25, 0), createDate(2012, 2, 13, 19, 43, 0),
			createDate(2012, 2, 14, 1, 7, 0), createDate(2012, 2, 14, 1, 25, 0), createDate(2012, 2, 14, 1, 43, 0), createDate(2012, 2, 14, 4, 7, 0),
			createDate(2012, 2, 14, 4, 25, 0), createDate(2012, 2, 14, 4, 43, 0), createDate(2012, 2, 14, 7, 7, 0), createDate(2012, 2, 14, 7, 25, 0),
			createDate(2012, 2, 14, 7, 43, 0), createDate(2012, 2, 14, 10, 7, 0), createDate(2012, 2, 14, 10, 25, 0), createDate(2012, 2, 14, 10, 43, 0),
			createDate(2012, 2, 14, 13, 7, 0), createDate(2012, 2, 14, 13, 25, 0), createDate(2012, 2, 14, 13, 43, 0), createDate(2012, 2, 14, 16, 7, 0),
			createDate(2012, 2, 14, 16, 25, 0), createDate(2012, 2, 14, 16, 43, 0), createDate(2012, 2, 14, 19, 7, 0), createDate(2012, 2, 14, 19, 25, 0),
			createDate(2012, 2, 14, 19, 43, 0), createDate(2012, 2, 15, 1, 7, 0), createDate(2012, 2, 15, 1, 25, 0), createDate(2012, 2, 15, 1, 43, 0),
			createDate(2012, 2, 15, 4, 7, 0), createDate(2012, 2, 15, 4, 25, 0), createDate(2012, 2, 15, 4, 43, 0), createDate(2012, 2, 15, 7, 7, 0),
			createDate(2012, 2, 15, 7, 25, 0), createDate(2012, 2, 15, 7, 43, 0), createDate(2012, 2, 15, 10, 7, 0), createDate(2012, 2, 15, 10, 25, 0),
			createDate(2012, 2, 15, 10, 43, 0), createDate(2012, 2, 15, 13, 7, 0), createDate(2012, 2, 15, 13, 25, 0), createDate(2012, 2, 15, 13, 43, 0),
			createDate(2012, 2, 15, 16, 7, 0), createDate(2012, 2, 15, 16, 25, 0), createDate(2012, 2, 15, 16, 43, 0), createDate(2012, 2, 15, 19, 7, 0),
			createDate(2012, 2, 15, 19, 25, 0), createDate(2012, 2, 15, 19, 43, 0), createDate(2012, 2, 16, 1, 7, 0), createDate(2012, 2, 16, 1, 25, 0),
			createDate(2012, 2, 16, 1, 43, 0), createDate(2012, 2, 16, 4, 7, 0), createDate(2012, 2, 16, 4, 25, 0), createDate(2012, 2, 16, 4, 43, 0),
			createDate(2012, 2, 16, 7, 7, 0), createDate(2012, 2, 16, 7, 25, 0), createDate(2012, 2, 16, 7, 43, 0), createDate(2012, 2, 16, 10, 7, 0),
			createDate(2012, 2, 16, 10, 25, 0), createDate(2012, 2, 16, 10, 43, 0), createDate(2012, 2, 16, 13, 7, 0), createDate(2012, 2, 16, 13, 25, 0),
			createDate(2012, 2, 16, 13, 43, 0), createDate(2012, 2, 16, 16, 7, 0), createDate(2012, 2, 16, 16, 25, 0), createDate(2012, 2, 16, 16, 43, 0),
			createDate(2012, 2, 16, 19, 7, 0), createDate(2012, 2, 16, 19, 25, 0), createDate(2012, 2, 16, 19, 43, 0), createDate(2012, 2, 17, 1, 7, 0),
			createDate(2012, 2, 17, 1, 25, 0), createDate(2012, 2, 17, 1, 43, 0), createDate(2012, 2, 17, 4, 7, 0), createDate(2012, 2, 17, 4, 25, 0),
			createDate(2012, 2, 17, 4, 43, 0), createDate(2012, 2, 17, 7, 7, 0), createDate(2012, 2, 17, 7, 25, 0), createDate(2012, 2, 17, 7, 43, 0),
			createDate(2012, 2, 17, 10, 7, 0), createDate(2012, 2, 17, 10, 25, 0), createDate(2012, 2, 17, 10, 43, 0), createDate(2012, 2, 17, 13, 7, 0),
			createDate(2012, 2, 17, 13, 25, 0), createDate(2012, 2, 17, 13, 43, 0), createDate(2012, 2, 17, 16, 7, 0), createDate(2012, 2, 17, 16, 25, 0),
			createDate(2012, 2, 17, 16, 43, 0), createDate(2012, 2, 17, 19, 7, 0), createDate(2012, 2, 17, 19, 25, 0), createDate(2012, 2, 17, 19, 43, 0),
			createDate(2012, 2, 18, 1, 7, 0), createDate(2012, 2, 18, 1, 25, 0), createDate(2012, 2, 18, 1, 43, 0), createDate(2012, 2, 18, 4, 7, 0),
			createDate(2012, 2, 18, 4, 25, 0), createDate(2012, 2, 18, 4, 43, 0), createDate(2012, 2, 18, 7, 7, 0), createDate(2012, 2, 18, 7, 25, 0),
			createDate(2012, 2, 18, 7, 43, 0), createDate(2012, 2, 18, 10, 7, 0), createDate(2012, 2, 18, 10, 25, 0), createDate(2012, 2, 18, 10, 43, 0),
			createDate(2012, 2, 18, 13, 7, 0), createDate(2012, 2, 18, 13, 25, 0), createDate(2012, 2, 18, 13, 43, 0), createDate(2012, 2, 18, 16, 7, 0),
			createDate(2012, 2, 18, 16, 25, 0), createDate(2012, 2, 18, 16, 43, 0), createDate(2012, 2, 18, 19, 7, 0), createDate(2012, 2, 18, 19, 25, 0),
			createDate(2012, 2, 18, 19, 43, 0), createDate(2012, 2, 19, 1, 7, 0), createDate(2012, 2, 19, 1, 25, 0), createDate(2012, 2, 19, 1, 43, 0),
			createDate(2012, 2, 19, 4, 7, 0), createDate(2012, 2, 19, 4, 25, 0), createDate(2012, 2, 19, 4, 43, 0), createDate(2012, 2, 19, 7, 7, 0),
			createDate(2012, 2, 19, 7, 25, 0), createDate(2012, 2, 19, 7, 43, 0), createDate(2012, 2, 19, 10, 7, 0), createDate(2012, 2, 19, 10, 25, 0),
			createDate(2012, 2, 19, 10, 43, 0), createDate(2012, 2, 19, 13, 7, 0), createDate(2012, 2, 19, 13, 25, 0), createDate(2012, 2, 19, 13, 43, 0),
			createDate(2012, 2, 19, 16, 7, 0), createDate(2012, 2, 19, 16, 25, 0), createDate(2012, 2, 19, 16, 43, 0), createDate(2012, 2, 19, 19, 7, 0),
			createDate(2012, 2, 19, 19, 25, 0), createDate(2012, 2, 19, 19, 43, 0), createDate(2012, 2, 20, 1, 7, 0), createDate(2012, 2, 20, 1, 25, 0),
			createDate(2012, 2, 20, 1, 43, 0), createDate(2012, 2, 20, 4, 7, 0), createDate(2012, 2, 20, 4, 25, 0), createDate(2012, 2, 20, 4, 43, 0),
			createDate(2012, 2, 20, 7, 7, 0), createDate(2012, 2, 20, 7, 25, 0), createDate(2012, 2, 20, 7, 43, 0), createDate(2012, 2, 20, 10, 7, 0),
			createDate(2012, 2, 20, 10, 25, 0), createDate(2012, 2, 20, 10, 43, 0), createDate(2012, 2, 20, 13, 7, 0), createDate(2012, 2, 20, 13, 25, 0),
			createDate(2012, 2, 20, 13, 43, 0), createDate(2012, 2, 20, 16, 7, 0), createDate(2012, 2, 20, 16, 25, 0), createDate(2012, 2, 20, 16, 43, 0),
			createDate(2012, 2, 20, 19, 7, 0), createDate(2012, 2, 20, 19, 25, 0), createDate(2012, 2, 20, 19, 43, 0), createDate(2012, 2, 21, 1, 7, 0),
			createDate(2012, 2, 21, 1, 25, 0), createDate(2012, 2, 21, 1, 43, 0), createDate(2012, 2, 21, 4, 7, 0), createDate(2012, 2, 21, 4, 25, 0),
			createDate(2012, 2, 21, 4, 43, 0), createDate(2012, 2, 21, 7, 7, 0), createDate(2012, 2, 21, 7, 25, 0), createDate(2012, 2, 21, 7, 43, 0),
			createDate(2012, 2, 21, 10, 7, 0), createDate(2012, 2, 21, 10, 25, 0), createDate(2012, 2, 21, 10, 43, 0), createDate(2012, 2, 21, 13, 7, 0),
			createDate(2012, 2, 21, 13, 25, 0), createDate(2012, 2, 21, 13, 43, 0), createDate(2012, 2, 21, 16, 7, 0), createDate(2012, 2, 21, 16, 25, 0),
			createDate(2012, 2, 21, 16, 43, 0), createDate(2012, 2, 21, 19, 7, 0), createDate(2012, 2, 21, 19, 25, 0), createDate(2012, 2, 21, 19, 43, 0),
			createDate(2012, 2, 22, 1, 7, 0), createDate(2012, 2, 22, 1, 25, 0), createDate(2012, 2, 22, 1, 43, 0), createDate(2012, 2, 22, 4, 7, 0),
			createDate(2012, 2, 22, 4, 25, 0), createDate(2012, 2, 22, 4, 43, 0), createDate(2012, 2, 22, 7, 7, 0), createDate(2012, 2, 22, 7, 25, 0),
			createDate(2012, 2, 22, 7, 43, 0), createDate(2012, 2, 22, 10, 7, 0), createDate(2012, 2, 22, 10, 25, 0), createDate(2012, 2, 22, 10, 43, 0),
			createDate(2012, 2, 22, 13, 7, 0), createDate(2012, 2, 22, 13, 25, 0), createDate(2012, 2, 22, 13, 43, 0), createDate(2012, 2, 22, 16, 7, 0),
			createDate(2012, 2, 22, 16, 25, 0), createDate(2012, 2, 22, 16, 43, 0), createDate(2012, 2, 22, 19, 7, 0), createDate(2012, 2, 22, 19, 25, 0),
			createDate(2012, 2, 22, 19, 43, 0), createDate(2012, 2, 23, 1, 7, 0), createDate(2012, 2, 23, 1, 25, 0), createDate(2012, 2, 23, 1, 43, 0),
			createDate(2012, 2, 23, 4, 7, 0), createDate(2012, 2, 23, 4, 25, 0), createDate(2012, 2, 23, 4, 43, 0), createDate(2012, 2, 23, 7, 7, 0),
			createDate(2012, 2, 23, 7, 25, 0), createDate(2012, 2, 23, 7, 43, 0), createDate(2012, 2, 23, 10, 7, 0), createDate(2012, 2, 23, 10, 25, 0),
			createDate(2012, 2, 23, 10, 43, 0), createDate(2012, 2, 23, 13, 7, 0), createDate(2012, 2, 23, 13, 25, 0), createDate(2012, 2, 23, 13, 43, 0),
			createDate(2012, 2, 23, 16, 7, 0), createDate(2012, 2, 23, 16, 25, 0), createDate(2012, 2, 23, 16, 43, 0), createDate(2012, 2, 23, 19, 7, 0),
			createDate(2012, 2, 23, 19, 25, 0), createDate(2012, 2, 23, 19, 43, 0), createDate(2012, 2, 24, 1, 7, 0), createDate(2012, 2, 24, 1, 25, 0),
			createDate(2012, 2, 24, 1, 43, 0), createDate(2012, 2, 24, 4, 7, 0), createDate(2012, 2, 24, 4, 25, 0), createDate(2012, 2, 24, 4, 43, 0),
			createDate(2012, 2, 24, 7, 7, 0), createDate(2012, 2, 24, 7, 25, 0), createDate(2012, 2, 24, 7, 43, 0), createDate(2012, 2, 24, 10, 7, 0),
			createDate(2012, 2, 24, 10, 25, 0), createDate(2012, 2, 24, 10, 43, 0), createDate(2012, 2, 24, 13, 7, 0), createDate(2012, 2, 24, 13, 25, 0),
			createDate(2012, 2, 24, 13, 43, 0), createDate(2012, 2, 24, 16, 7, 0), createDate(2012, 2, 24, 16, 25, 0), createDate(2012, 2, 24, 16, 43, 0),
			createDate(2012, 2, 24, 19, 7, 0), createDate(2012, 2, 24, 19, 25, 0), createDate(2012, 2, 24, 19, 43, 0), createDate(2012, 2, 25, 1, 7, 0),
			createDate(2012, 2, 25, 1, 25, 0), createDate(2012, 2, 25, 1, 43, 0), createDate(2012, 2, 25, 4, 7, 0), createDate(2012, 2, 25, 4, 25, 0),
			createDate(2012, 2, 25, 4, 43, 0), createDate(2012, 2, 25, 7, 7, 0), createDate(2012, 2, 25, 7, 25, 0), createDate(2012, 2, 25, 7, 43, 0),
			createDate(2012, 2, 25, 10, 7, 0), createDate(2012, 2, 25, 10, 25, 0), createDate(2012, 2, 25, 10, 43, 0), createDate(2012, 2, 25, 13, 7, 0),
			createDate(2012, 2, 25, 13, 25, 0), createDate(2012, 2, 25, 13, 43, 0), createDate(2012, 2, 25, 16, 7, 0), createDate(2012, 2, 25, 16, 25, 0),
			createDate(2012, 2, 25, 16, 43, 0), createDate(2012, 2, 25, 19, 7, 0), createDate(2012, 2, 25, 19, 25, 0), createDate(2012, 2, 25, 19, 43, 0),
			createDate(2012, 2, 26, 1, 7, 0), createDate(2012, 2, 26, 1, 25, 0), createDate(2012, 2, 26, 1, 43, 0), createDate(2012, 2, 26, 4, 7, 0),
			createDate(2012, 2, 26, 4, 25, 0), createDate(2012, 2, 26, 4, 43, 0), createDate(2012, 2, 26, 7, 7, 0), createDate(2012, 2, 26, 7, 25, 0),
			createDate(2012, 2, 26, 7, 43, 0), createDate(2012, 2, 26, 10, 7, 0), createDate(2012, 2, 26, 10, 25, 0), createDate(2012, 2, 26, 10, 43, 0),
			createDate(2012, 2, 26, 13, 7, 0), createDate(2012, 2, 26, 13, 25, 0), createDate(2012, 2, 26, 13, 43, 0), createDate(2012, 2, 26, 16, 7, 0),
			createDate(2012, 2, 26, 16, 25, 0), createDate(2012, 2, 26, 16, 43, 0), createDate(2012, 2, 26, 19, 7, 0), createDate(2012, 2, 26, 19, 25, 0),
			createDate(2012, 2, 26, 19, 43, 0), createDate(2012, 2, 27, 1, 7, 0), createDate(2012, 2, 27, 1, 25, 0), createDate(2012, 2, 27, 1, 43, 0),
			createDate(2012, 2, 27, 4, 7, 0), createDate(2012, 2, 27, 4, 25, 0), createDate(2012, 2, 27, 4, 43, 0), createDate(2012, 2, 27, 7, 7, 0),
			createDate(2012, 2, 27, 7, 25, 0), createDate(2012, 2, 27, 7, 43, 0), createDate(2012, 2, 27, 10, 7, 0), createDate(2012, 2, 27, 10, 25, 0),
			createDate(2012, 2, 27, 10, 43, 0), createDate(2012, 2, 27, 13, 7, 0), createDate(2012, 2, 27, 13, 25, 0), createDate(2012, 2, 27, 13, 43, 0),
			createDate(2012, 2, 27, 16, 7, 0), createDate(2012, 2, 27, 16, 25, 0), createDate(2012, 2, 27, 16, 43, 0), createDate(2012, 2, 27, 19, 7, 0),
			createDate(2012, 2, 27, 19, 25, 0), createDate(2012, 2, 27, 19, 43, 0), createDate(2012, 2, 28, 1, 7, 0), createDate(2012, 2, 28, 1, 25, 0),
			createDate(2012, 2, 28, 1, 43, 0), createDate(2012, 2, 28, 4, 7, 0), createDate(2012, 2, 28, 4, 25, 0), createDate(2012, 2, 28, 4, 43, 0),
			createDate(2012, 2, 28, 7, 7, 0), createDate(2012, 2, 28, 7, 25, 0), createDate(2012, 2, 28, 7, 43, 0), createDate(2012, 2, 28, 10, 7, 0),
			createDate(2012, 2, 28, 10, 25, 0), createDate(2012, 2, 28, 10, 43, 0), createDate(2012, 2, 28, 13, 7, 0), createDate(2012, 2, 28, 13, 25, 0),
			createDate(2012, 2, 28, 13, 43, 0), createDate(2012, 2, 28, 16, 7, 0), createDate(2012, 2, 28, 16, 25, 0), createDate(2012, 2, 28, 16, 43, 0),
			createDate(2012, 2, 28, 19, 7, 0), createDate(2012, 2, 28, 19, 25, 0), createDate(2012, 2, 28, 19, 43, 0), createDate(2012, 2, 29, 1, 7, 0),
			createDate(2012, 2, 29, 1, 25, 0), createDate(2012, 2, 29, 1, 43, 0), createDate(2012, 2, 29, 4, 7, 0), createDate(2012, 2, 29, 4, 25, 0),
			createDate(2012, 2, 29, 4, 43, 0), createDate(2012, 2, 29, 7, 7, 0), createDate(2012, 2, 29, 7, 25, 0), createDate(2012, 2, 29, 7, 43, 0),
			createDate(2012, 2, 29, 10, 7, 0), createDate(2012, 2, 29, 10, 25, 0), createDate(2012, 2, 29, 10, 43, 0), createDate(2012, 2, 29, 13, 7, 0),
			createDate(2012, 2, 29, 13, 25, 0), createDate(2012, 2, 29, 13, 43, 0), createDate(2012, 2, 29, 16, 7, 0), createDate(2012, 2, 29, 16, 25, 0),
			createDate(2012, 2, 29, 16, 43, 0), createDate(2012, 2, 29, 19, 7, 0), createDate(2012, 2, 29, 19, 25, 0), createDate(2012, 2, 29, 19, 43, 0),
			createDate(2012, 2, 30, 1, 7, 0), createDate(2012, 2, 30, 1, 25, 0), createDate(2012, 2, 30, 1, 43, 0), createDate(2012, 2, 30, 4, 7, 0),
			createDate(2012, 2, 30, 4, 25, 0), createDate(2012, 2, 30, 4, 43, 0), createDate(2012, 2, 30, 7, 7, 0), createDate(2012, 2, 30, 7, 25, 0),
			createDate(2012, 2, 30, 7, 43, 0), createDate(2012, 2, 30, 10, 7, 0), createDate(2012, 2, 30, 10, 25, 0), createDate(2012, 2, 30, 10, 43, 0),
			createDate(2012, 2, 30, 13, 7, 0), createDate(2012, 2, 30, 13, 25, 0), createDate(2012, 2, 30, 13, 43, 0), createDate(2012, 2, 30, 16, 7, 0),
			createDate(2012, 2, 30, 16, 25, 0), createDate(2012, 2, 30, 16, 43, 0), createDate(2012, 2, 30, 19, 7, 0), createDate(2012, 2, 30, 19, 25, 0),
			createDate(2012, 2, 30, 19, 43, 0), createDate(2012, 2, 31, 1, 7, 0), createDate(2012, 2, 31, 1, 25, 0), createDate(2012, 2, 31, 1, 43, 0),
			createDate(2012, 2, 31, 4, 7, 0), createDate(2012, 2, 31, 4, 25, 0), createDate(2012, 2, 31, 4, 43, 0), createDate(2012, 2, 31, 7, 7, 0),
			createDate(2012, 2, 31, 7, 25, 0), createDate(2012, 2, 31, 7, 43, 0), createDate(2012, 2, 31, 10, 7, 0), createDate(2012, 2, 31, 10, 25, 0),
			createDate(2012, 2, 31, 10, 43, 0), createDate(2012, 2, 31, 13, 7, 0), createDate(2012, 2, 31, 13, 25, 0), createDate(2012, 2, 31, 13, 43, 0),
			createDate(2012, 2, 31, 16, 7, 0), createDate(2012, 2, 31, 16, 25, 0), createDate(2012, 2, 31, 16, 43, 0), createDate(2012, 2, 31, 19, 7, 0),
			createDate(2012, 2, 31, 19, 25, 0), createDate(2012, 2, 31, 19, 43, 0),

	};

	// TestCase3 --> Exception in cause of 29th Feb 2007
	public static String cron3 = "2007:1:29:*:10:20";

	//SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes3 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	//SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes3 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	// Exception too = case 2.
	//Date[] datesCron3 = new Date[0];

	// TestCase4
	public static String cron4 = "2007-2012:1:29:*:10:20";
	public static Date cron4DummyDate = 
		new Date(Math.max(createDate(2013, 11, 31, 23, 0, 0).getTime(), dateNow.getTime() + (366L * 86400000L)));
	public static Date cron4EndDate = 
		new Date(Math.max(createDate(2028, 11, 31, 23, 0, 0).getTime(), dateNow.getTime() + (2L * 366L * 86400000L)));
	public static RecurringEntry recurringEntryForCron4 = 
		new RecurringEntry(new SchedulerTime(cron4DummyDate, TimeZone.getDefault()), 0, 0); 
	
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes4 = 
		new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes4 = 
		new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), cron4EndDate);

	public static Date[] datesCron4 = new Date[] { createDate(2008, 1, 29, 10, 20, 0), 
		createDate(2012, 1, 29, 10, 20, 0), cron4DummyDate };

	// TestCase5 - Exception!
	public static String cron5 = "2007/2:1:29:*:10:20";

	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes5 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));

	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes5 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));

	Date[] datesCron5 = new Date[0];

	// TestCase6
	public static String cron6 = "*/2:1:29:*:10:20";

	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes6 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));

	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes6 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));

	public static Date[] datesCron6 = new Date[] { createDate(2000, 1, 29, 10, 20, 0), createDate(2004, 1, 29, 10, 20, 0), createDate(2008, 1, 29, 10, 20, 0),
			createDate(2012, 1, 29, 10, 20, 0), createDate(2016, 1, 29, 10, 20, 0), createDate(2020, 1, 29, 10, 20, 0), createDate(2024, 1, 29, 10, 20, 0),
			createDate(2028, 1, 29, 10, 20, 0) };

	// TestCase7 - Exception!!!
	public static String cron7 = "2007:11:12:2/2:1-3:10-40/15";

	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes7 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));

	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes7 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));

	Date[] datesCron7 = new Date[0];

	// TestCase8
	public static String cron8 = "*:*/2:10:*:12:12";

	// - only for YEAR 2007
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes8 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2006, 11, 31, 23, 59, 0));

	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes8 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 11, 31, 23, 59, 0));

	public static Date[] datesCron8 = new Date[] { createDate(2007, 0, 10, 12, 12, 0), createDate(2007, 2, 10, 12, 12, 0), createDate(2007, 4, 10, 12, 12, 0),
			createDate(2007, 6, 10, 12, 12, 0), createDate(2007, 8, 10, 12, 12, 0), createDate(2007, 10, 10, 12, 12, 0) };

	// TestCase9
	public static String cron9 = "2008:*/2:31:*:10:10";  //last firetime 2008.07.31. 10h10
	
	public static Date dateNextYear = new Date(dateNow.getTime() + (366L * 86400000L) );
	public static Date cron9EndDate = 
		new Date(Math.max(createDate(2028, 11, 31, 23, 0, 0).getTime(), dateNextYear.getTime() + (366L * 86400000L)));
	// cron9EndDate is at least 2 years in the future
	// test does not work prior to 2006, as the firetimes are not in the expected order at that time
	
	public static RecurringEntry recurringEntryNextYear = 
		new RecurringEntry(new SchedulerTime(dateNextYear, TimeZone.getDefault()), 0, 0); 
	
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes9 = 
		new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));

	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes9 = 
		new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), cron9EndDate);

	public static Date[] datesCron9 = new Date[] { createDate(2008, 0, 31, 10, 10, 0), createDate(2008, 2, 31, 10, 10, 0), 
		createDate(2008, 4, 31, 10, 10, 0), createDate(2008, 6, 31, 10, 10, 0), dateNextYear };

	// TestCase10
	//public static String cron10 = "2007:11:12:*:2-12/3:10-40/4";
	public static String cron10 = "*:11:12:*:2-12/3:10-40/4";
	//public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes10 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	//public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes10 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
  public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes10 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 11, 12, 2, 9, 0));
  public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes10 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 11, 12, 11, 39, 0));

	public static Date[] datesCron10 = new Date[] { createDate(2007, 11, 12, 2, 10, 0), createDate(2007, 11, 12, 2, 14, 0), createDate(2007, 11, 12, 2, 18, 0),
			createDate(2007, 11, 12, 2, 22, 0), createDate(2007, 11, 12, 2, 26, 0), createDate(2007, 11, 12, 2, 30, 0), createDate(2007, 11, 12, 2, 34, 0),
			createDate(2007, 11, 12, 2, 38, 0),

			createDate(2007, 11, 12, 5, 10, 0), createDate(2007, 11, 12, 5, 14, 0), createDate(2007, 11, 12, 5, 18, 0), createDate(2007, 11, 12, 5, 22, 0),
			createDate(2007, 11, 12, 5, 26, 0), createDate(2007, 11, 12, 5, 30, 0), createDate(2007, 11, 12, 5, 34, 0), createDate(2007, 11, 12, 5, 38, 0),

			createDate(2007, 11, 12, 8, 10, 0), createDate(2007, 11, 12, 8, 14, 0), createDate(2007, 11, 12, 8, 18, 0), createDate(2007, 11, 12, 8, 22, 0),
			createDate(2007, 11, 12, 8, 26, 0), createDate(2007, 11, 12, 8, 30, 0), createDate(2007, 11, 12, 8, 34, 0), createDate(2007, 11, 12, 8, 38, 0),

			createDate(2007, 11, 12, 11, 10, 0), createDate(2007, 11, 12, 11, 14, 0), createDate(2007, 11, 12, 11, 18, 0), createDate(2007, 11, 12, 11, 22, 0),
			createDate(2007, 11, 12, 11, 26, 0), createDate(2007, 11, 12, 11, 30, 0), createDate(2007, 11, 12, 11, 34, 0), createDate(2007, 11, 12, 11, 38, 0) };

	// TestCase11 - Exception expected (Invalid day of week field value)
	public static String cron11 = "2007:11:*:2/2:1-4:10-40/15 ";
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes11 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes11 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	public static Date[] datesCron11 = new Date[0];
	
	// TestCase12 - Exception expected (Invalid day of week field value)
	public static String cron12 = "2007:11:*:2/8:1-4:10-40/15";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes12 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes12 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron12 = new Date[0];

	// TestCase13 - Exception expected (Invalid day of week field value)
	public static String cron13 = "2007:12:*:2/8:1-4:10-40/15";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes13 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes13 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron13 = new Date[0];

	// TestCase14 - Exception expected (Invalid month value)
	public static String cron14 = "*:*/12:*:*:12:12";
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes14 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes14 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	public static Date[] datesCron14 = new Date[0];

	// TestCase15 - Exception expected (invalid year range)
	public static String cron15 = "1965-1789:*:*:*:1-19/3:7-57/18";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes15 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes15 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron15 = new Date[0];

	// TestCase16 - Exception expected (invalid year range)
	public static String cron16 = "1965-1972:*:*:*:*:*";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes16 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes16 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron16 = new Date[0];

	// TestCase17 - Exception expected (invalid year)
	public static String cron17 = "-2020:*:*:*:*:*";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes17 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes17 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron17 = new Date[0];

	// TestCase18 - Exception expected (invalid day of month)
	public static String cron18 = "2008:1:31:*:*:*";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes18 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes18 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron18 = new Date[0];

	// TestCase19 - Exception expected (invalid day of month)
	public static String cron19 = "2008:3:31:*:*:*";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes19 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes19 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron19 = new Date[0];

	// TestCase20 - Exception expected (invalid year)
	public static String cron20 = "0:*:*:*:*:*";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes20 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes20 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron20 = new Date[0];

	// TestCase21 - Exception expected (invalid step length)
	public static String cron21 = "*:*:*:*:1-4:10-40/70";
	SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes21 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2000, 0, 1, 23, 0, 0));
	SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes21 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2028, 11, 31, 23, 0, 0));
	Date[] datesCron21 = new Date[0];

	// TestCase22
	public static String cron22 = "*:2:25:*:1-4:10-40/10";
	public static TimeZone cron22tz = TimeZone.getTimeZone("Europe/Berlin");
	public static CronEntry cronEntry22 = new CronEntry(cron22, cron22tz);
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes22 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 0, 1, 23, 0, 0));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes22 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 11, 31, 23, 0, 0));
	public static Date[] datesCron22 = new Date[] { 
			createDate(2007, 2, 25, 1, 10, 0, cron22tz), createDate(2007, 2, 25, 1, 20, 0, cron22tz), 
			createDate(2007, 2, 25, 1, 30, 0, cron22tz), createDate(2007, 2, 25, 1, 40, 0, cron22tz),

			createDate(2007, 2, 25, 3, 10, 0, cron22tz), createDate(2007, 2, 25, 3, 20, 0, cron22tz),
			createDate(2007, 2, 25, 3, 30, 0, cron22tz), createDate(2007, 2, 25, 3, 40, 0, cron22tz),

			createDate(2007, 2, 25, 4, 10, 0, cron22tz), createDate(2007, 2, 25, 4, 20, 0, cron22tz),
			createDate(2007, 2, 25, 4, 30, 0, cron22tz), createDate(2007, 2, 25, 4, 40, 0, cron22tz) };

	// Test Case 23
	// RecurringEntry
	String recurring23 = "28.11.2008 10:10:00 TimeZone = PST"; // scheduled every day at this time
	public static RecurringEntry recEntry23 = new RecurringEntry(new SchedulerTime((createDate(2008, 10, 28, 10, 10, 00, TimeZone.getTimeZone("PST"))), TimeZone.getTimeZone("PST")), 86400000); // 86400000 = 1 day
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes23 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 28, 0, 0, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes23 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 29, 0, 0, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static Date[] datesRec23 = new Date[] { createDate(2008, 10, 28, 19, 10, 0, TimeZone.getTimeZone("Europe/Berlin")) };

	// Test Case 24
	// RecurringEntry - simulating Client in India and Server in Germany
	String recurring24 = "28.11.2008 14:30 TimeZone = Asia/Calcutta"; // scheduled every day at this time
	public static RecurringEntry recEntry24 = new RecurringEntry(new SchedulerTime((createDate(2008, 10, 28, 14, 30, 00, TimeZone.getTimeZone("Asia/Calcutta"))), TimeZone.getTimeZone("Asia/Calcutta")), 86400000); // 86400000 = 1 day
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes24 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 28, 0, 00, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes24 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 29, 0, 00, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static Date[] datesRec24 = new Date[] { createDate(2008, 10, 28, 10, 00, 0, TimeZone.getTimeZone("Europe/Berlin")) };

	// Test Case 25
	public static String cron25 = "*:10:28:*:10:10";
	public static CronEntry cronEntry25 = new CronEntry(cron25, TimeZone.getTimeZone("PST"));
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes25 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 28, 0, 0, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes25 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 29, 0, 0, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static Date[] datesCron25 = new Date[] { createDate(2008, 10, 28, 19, 10, 0, TimeZone.getTimeZone("Europe/Berlin")) };


	// Test Case 26
	public static String cron26 = "*:10:28:*:14:30";
	public static CronEntry cronEntry26 = new CronEntry(cron26, TimeZone.getTimeZone("Asia/Calcutta"));
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes26 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 28, 0, 0, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes26 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getTimeZone("Europe/Berlin").toString(), createDate(2008, 10, 29, 0, 0, 0, TimeZone.getTimeZone("Europe/Berlin")));
	public static Date[] datesCron26 = new Date[] { createDate(2008, 10, 28, 10, 00, 0, TimeZone.getTimeZone("Europe/Berlin")) };

	//  TestCase27 
	public static String cron27 = "*:2:25:*:1-4:10-40/10";
  public static TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
	public static CronEntry cronEntry27 = new CronEntry(cron27, tz);
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes27 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 0, 1, 23, 0, 0));
	public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes27 = new SAP_ITSAMJavaSchedulerTime(TimeZone.getDefault().toString(), createDate(2007, 11, 31, 23, 0, 0));
	public static Date[] datesCron27 = new Date[]  { createDate(2007, 2, 25, 1, 10, 0, tz), createDate(2007, 2, 25, 1, 20, 0, tz), 
                                                     createDate(2007, 2, 25, 1, 30, 0, tz), createDate(2007, 2, 25, 1, 40, 0, tz),
                                                     // no results between 2 and 3
                                                     createDate(2007, 2, 25, 3, 10, 0, tz), createDate(2007, 2, 25, 3, 20, 0, tz), 
                                                     createDate(2007, 2, 25, 3, 30, 0, tz), createDate(2007, 2, 25, 3, 40, 0, tz),
                                                     createDate(2007, 2, 25, 4, 10, 0, tz), createDate(2007, 2, 25, 4, 20, 0, tz), 
                                                     createDate(2007, 2, 25, 4, 30, 0, tz), createDate(2007, 2, 25, 4, 40, 0, tz) 
                                                    };		
  // TestCase28 
	public static String cron28 = "*:9:28:,:1-4:10-50/10";
  public static TimeZone cron28tz = TimeZone.getTimeZone("Europe/Berlin");
	public static CronEntry cronEntry28 = new CronEntry(cron28, cron28tz);
	public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes28 = new SAP_ITSAMJavaSchedulerTime(cron28tz.toString(), createDate(2007, 9, 28, 0, 0, 0, cron28tz));
  public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes28 = new SAP_ITSAMJavaSchedulerTime(cron28tz.toString(), createDate(2007, 9, 29, 0, 0, 0, cron28tz));
	public static Date[] datesCron28 = new Date[] { createDate(2007, 9, 28, 1, 10, 0, cron28tz), createDate(2007, 9, 28, 1, 20, 0, cron28tz),
                                                    createDate(2007, 9, 28, 1, 30, 0, cron28tz), createDate(2007, 9, 28, 1, 40, 0, cron28tz),
                                                    createDate(2007, 9, 28, 1, 50, 0, cron28tz), createDate(2007, 9, 28, 2, 10, 0, cron28tz),
                                                    createDate(2007, 9, 28, 2, 20, 0, cron28tz), createDate(2007, 9, 28, 2, 30, 0, cron28tz), 
                                                    createDate(2007, 9, 28, 2, 40, 0, cron28tz), createDate(2007, 9, 28, 2, 50, 0, cron28tz), 
                                                    createDate(2007, 9, 28, 3, 10, 0, cron28tz), createDate(2007, 9, 28, 3, 20, 0, cron28tz),
                                                    createDate(2007, 9, 28, 3, 30, 0, cron28tz), createDate(2007, 9, 28, 3, 40, 0, cron28tz),
                                                    createDate(2007, 9, 28, 3, 50, 0, cron28tz), createDate(2007, 9, 28, 4, 10, 0, cron28tz),
                                                    createDate(2007, 9, 28, 4, 20, 0, cron28tz), createDate(2007, 9, 28, 4, 30, 0, cron28tz),
                                                    createDate(2007, 9, 28, 4, 40, 0, cron28tz), createDate(2007, 9, 28, 4, 50, 0, cron28tz), 
                                                  };

	// Test Case 29
	// RecurringEntry - planing jobs in the DST period.
	String recurring29 = "28.10.* 1:00 every 10 minutes until 4:50";
  public static TimeZone rec29tz = TimeZone.getTimeZone("Europe/Berlin");
  public static TimeZone rec29GMTtz = TimeZone.getTimeZone("GMT");
  public static RecurringEntry recEntry29 = new RecurringEntry(new SchedulerTime((createDate(2007, 9, 28, 1, 0, 0, rec29tz)), rec29tz), (long) 600000); // 10 min
  public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes29 = new SAP_ITSAMJavaSchedulerTime(rec29tz.toString(), createDate(2007, 9, 28, 0, 59, 0, rec29tz));
  public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes29 = new SAP_ITSAMJavaSchedulerTime(rec29tz.toString(), createDate(2007, 9, 28, 4, 51, 0, rec29tz));
  public static Date[] datesRec29 = new Date[] { createDate(2007, 9, 28, 1, 0, 0, rec29tz), createDate(2007, 9, 28, 1, 10, 0, rec29tz), 
                                     createDate(2007, 9, 28, 1, 20, 0, rec29tz), createDate(2007, 9, 28, 1, 30, 0, rec29tz),
                                     createDate(2007, 9, 28, 1, 40, 0, rec29tz), createDate(2007, 9, 28, 1, 50, 0, rec29tz),
                                     // the following block of dates is added twice, because this hour exists twice between 2 and 3 am
                                     // I set here the time for GMT which is 2 hours back in comparison to CEST
                                     createDate(2007, 9, 28, 0, 0, 0, rec29GMTtz), createDate(2007, 9, 28, 0, 10, 0, rec29GMTtz), 
                                     createDate(2007, 9, 28, 0, 20, 0, rec29GMTtz), createDate(2007, 9, 28, 0, 30, 0, rec29GMTtz),
                                     createDate(2007, 9, 28, 0, 40, 0, rec29GMTtz), createDate(2007, 9, 28, 0, 50, 0, rec29GMTtz),
                                     
                                     createDate(2007, 9, 28, 2, 0, 0, rec29tz), createDate(2007, 9, 28, 2, 10, 0, rec29tz), 
                                     createDate(2007, 9, 28, 2, 20, 0, rec29tz), createDate(2007, 9, 28, 2, 30, 0, rec29tz),
                                     createDate(2007, 9, 28, 2, 40, 0, rec29tz), createDate(2007, 9, 28, 2, 50, 0, rec29tz),

                                     createDate(2007, 9, 28, 3, 0, 0, rec29tz), createDate(2007, 9, 28, 3, 10, 0, rec29tz), 
                                     createDate(2007, 9, 28, 3, 20, 0, rec29tz), createDate(2007, 9, 28, 3, 30, 0, rec29tz),
                                     createDate(2007, 9, 28, 3, 40, 0, rec29tz), createDate(2007, 9, 28, 3, 50, 0, rec29tz),
                                     createDate(2007, 9, 28, 4, 0, 0, rec29tz), createDate(2007, 9, 28, 4, 10, 0, rec29tz), 
                                     createDate(2007, 9, 28, 4, 20, 0, rec29tz), createDate(2007, 9, 28, 4, 30, 0, rec29tz),
                                     createDate(2007, 9, 28, 4, 40, 0, rec29tz), createDate(2007, 9, 28, 4, 50, 0, rec29tz) 
                                   };
 
	// TestCase30
	String recurring30 = "25.03.2007 1:00 every 10 minutes until 4:50";
  public static TimeZone rec30tz = TimeZone.getTimeZone("Europe/Berlin");
  public static RecurringEntry recEntry30 = new RecurringEntry(new SchedulerTime((createDate(2007, 2, 25, 1, 0, 0, rec30tz)), rec30tz), null, (long) 600000);	
  public static SAP_ITSAMJavaSchedulerTime startTimeForGetFireTimes30 = new SAP_ITSAMJavaSchedulerTime(rec30tz.toString(), createDate(2007, 2, 25, 0, 59, 0, rec30tz));
  public static SAP_ITSAMJavaSchedulerTime endTimeForGetFireTimes30 = new SAP_ITSAMJavaSchedulerTime(rec30tz.toString(), createDate(2007, 2, 25, 4, 51, 0, rec30tz));
	
  public static Date[] datesRec30 = new Date[] { createDate(2007, 2, 25, 1, 0, 0, rec30tz), createDate(2007, 2, 25, 1, 10, 0, rec30tz), 
                                     createDate(2007, 2, 25, 1, 20, 0, rec30tz), createDate(2007, 2, 25, 1, 30, 0, rec30tz),
                                     createDate(2007, 2, 25, 1, 40, 0, rec30tz), createDate(2007, 2, 25, 1, 50, 0, rec30tz),
                                     // no results between 2 and 3
                                     createDate(2007, 2, 25, 3, 0, 0, rec30tz), createDate(2007, 2, 25, 3, 10, 0, rec30tz), 
                                     createDate(2007, 2, 25, 3, 20, 0, rec30tz), createDate(2007, 2, 25, 3, 30, 0, rec30tz),
                                     createDate(2007, 2, 25, 3, 40, 0, rec30tz), createDate(2007, 2, 25, 3, 50, 0, rec30tz),
                                     createDate(2007, 2, 25, 4, 0, 0, rec30tz), createDate(2007, 2, 25, 4, 10, 0, rec30tz), 
                                     createDate(2007, 2, 25, 4, 20, 0, rec30tz), createDate(2007, 2, 25, 4, 30, 0, rec30tz),
                                     createDate(2007, 2, 25, 4, 40, 0, rec30tz), createDate(2007, 2, 25, 4, 50, 0, rec30tz) 
                                   };
	
	
	private CronEntry getCronEntry(String cronStr) {
		String[] s = cronStr.split(":");
		return new CronEntry(new CronYearField(s[0]), new CronMonthField(s[1]), new CronDOMField(s[2]), new CronDOWField(s[3]), new CronHourField(s[4]), new CronMinuteField(s[5]));
	}
	
    private static Date getNormalizedDate(Date date) {
        Calendar cal = Calendar.getInstance();        
        cal.setTime( new Date ( (long)( (date.getTime()/1000) * 1000 ) ) );
        return cal.getTime();
    }


	public class JobParameterType {

		private JobParameter[] jobParameterArr;

		private JobDefinitionID jobDefinitionId;

		public JobParameterType() {

		}

		public JobParameterType(JobParameter[] jobParameterArr, JobDefinitionID jobDefinitionId) {
			this.jobParameterArr = jobParameterArr;
			this.jobDefinitionId = jobDefinitionId;
		}

		public JobParameter[] getJobParameterArr() {
			return jobParameterArr;
		}

		public JobDefinitionID getJobDefinitionId() {

			return jobDefinitionId;
		}

		public void setJobParameterArr(JobParameter[] jobParameterArr) {
			this.jobParameterArr = jobParameterArr;
		}

		public void setJobDefinitionId(JobDefinitionID jobDefinitionId) {

			this.jobDefinitionId = jobDefinitionId;
		}

	}

	public class TestDataType {

		private boolean result;

		private Date[] dates;

		private String taskId;

		public TestDataType() {
			result = false;
			dates = null;
			taskId = null;
		}

		public TestDataType(boolean result, Date[] dates, String taskId) {
			this.result = result;
			this.dates = dates;
			this.taskId = taskId;
		}

		public boolean getBoolean() {
			return result;
		}

		public Date[] getDates() {
			return dates;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setBoolean(boolean res) {
			this.result = res;
		}

		public void setDates(Date[] date) {
			this.dates = date;
		}

		public void setTaskId(String IdOfTheTask) {
			this.taskId = IdOfTheTask;
		}
	}

}

/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.api.jver;

import java.util.TimeZone;

import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.tc.jtools.jver.framework.Test;

/**
 * @author Hristo Sabev (i027642)
 *
 * This class contains unit tests for the RecurringEntry class
 * @see RecurringEntry.
 */
public class RecurringEntryTest extends Test {
	
	public static void main(String[] args) {
		RecurringEntry re1 = new RecurringEntry(new SchedulerTime(), 0);
	}
	
	/**
	 * Tests the RecurringEntry(long) constructor. This method checks whether
	 * the appropriate exceptions are thrown by the tested constructors. It also
	 * tests whether the appropriate values are returned by the get* methods of
	 * RecurringEntry after the object is initialized with the tested constructor
	 * @see RecurringEntry#RecurringEntry(long)
	 */
	public void test_Constructor1() {
		/*
		 * Pass 0 as wait period. This should throw IllegalArgumentException 
		 */
		try {
			RecurringEntry re1 = new RecurringEntry(0);
			flop("RecurringEntry(long) didn't throw IllegalArgumentException when 0 was passed for wait period");
		} catch (IllegalArgumentException iae) {
			this.logStackTrace(iae);
			this.log("IllegalArgumentException cought as expected");
			verify(true, "IllegalArgumentException cought as expected");
		}

		try {
			RecurringEntry re2 = new RecurringEntry(-1);
			flop("RecurringEntry(long) didn't throw IllegalArgumentException when negative period was passed");
		} catch (IllegalArgumentException iae) {
			this.logStackTrace(iae);
			this.log("IllegalArgumentException cought as expected");
			verify(true, "IllegalArgument cought as expected");
		}
		
		RecurringEntry re3 = new RecurringEntry(500);
		final SchedulerTime startTime = re3.getStartTime();
		verify(startTime.getTimeZone().equals(TimeZone.getDefault()), "time zone of start time is not the same as the default time zone when the entry was created with constructor RecurringEntry(long)");
		verify(re3.getEndTime() == null, "RecurringEntry.getEndTime() returned a value different than null, when created with constructor RecurringEntry(long)");
		verify(re3.getPeriod() == 500, "RecurringEntry.getPeriod() returned value different than 500 when 500 was passed for period");
	}

	/**
	 * Tests the RecurringEntry(SchedulerTime) constructor. This method checks
	 * whether the appriate exceptions are thrown by the tested constructors.
	 * It also tests whether the appropriate values are returned by the get*
	 * methods of RecurringEntry the object is nitialized with the tested 
	 * constructor.
	 * @see RecurringEntry#RecurringEntry(com.sap.scheduler.api.SchedulerTime)
	 */
	public void test_Constructor2() {
		try {
			RecurringEntry re1 = new RecurringEntry(null);
			flop("RecurringEntry(SchedulerTime) dind't throw NullPointerException when null was passed for expirationTime");
		} catch (NullPointerException npe) {
			verify(true, "NullPointerException thrown as expected");
		}
		
		SchedulerTime expirationTime = new SchedulerTime();
		RecurringEntry re2 = new RecurringEntry(expirationTime);
		verify(re2.getPeriod() == 0, "getPeriod() returned value different than 0");
		verify(re2.getStartTime().equals(expirationTime), "getStartTime() returned value different than the expiration time");
		verify(re2.getEndTime().equals(expirationTime), "getEntDime() returned value different than expirationTime");
	}
	
	/**
	 * Tests the RecurringEntry(SchedulerTime, long) constructor. This method checks whether
	 * the appropriate exceptions are thrown by the tested constructors. It also
	 * tests whether the appropriate values are returned by the get* methods of
	 * the RecurringEntry after the object is initialized with the tested constructor
	 * @see RecurringEntry#RecurringEntry(com.sap.scheduler.api.SchedulerTime, long)
	 */
	public void test_Constructor3() {
		try {
			RecurringEntry re = new RecurringEntry(null, 50);
			flop("NullPointerException was not thrown by constructor RecurringEntry(SchedulerTime, long) when startTime was null");
		} catch (NullPointerException npe) {
			verify(true, "NullPointerException cought as expected");
		}
		
		final SchedulerTime startTime = new SchedulerTime();
		try {
			RecurringEntry re1 = new RecurringEntry(startTime, 0);
			flop("RecurringEntry(long) didn't throw IllegalArgumentException when 0 was passed for wait period");
		} catch (IllegalArgumentException iae) {
			this.logStackTrace(iae);
			this.log("IllegalArgumentException cought as expected");
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		try {
			RecurringEntry re2 = new RecurringEntry(startTime, -1);
			flop("RecurringEntry(long) didn't throw IllegalArgumentException when negative period was passed");
		} catch (IllegalArgumentException iae) {
			this.logStackTrace(iae);
			this.log("IllegalArgumentException cought as expected");
			verify(true, "IllegalArgument cought as expected");
		}
		
		RecurringEntry re3 = new RecurringEntry(startTime, 500);
		verify(re3.getStartTime().getTimeZone().equals(TimeZone.getDefault()), "time zone of start time is not the same as the default time zone when the entry was created with constructor RecurringEntry(long)");
		verify(re3.getEndTime() == null, "RecurringEntry.getEndTime() returned a value different than null, when created with constructor RecurringEntry(long)");
		verify(re3.getPeriod() == 500, "RecurringEntry.getPeriod() returned value different than 500 when 500 was passed for period");
	}
	
	/**
	 * Tests the RecurringEntry(SchedulerTime, long, int) constructor. This method checks
	 * whether the appropriate exceptions are thrown by the tested constructors. It also
	 * tests whether the appropriate values are returned by the get* methods of the
	 * RecurringEntry after the object is initialized with the tested constructor
	 * @see RecurringEntry#RecurringEntry(com.sap.scheduler.api.SchedulerTime, com.sap.scheduler.api.SchedulerTime, int)
	 */
	public void test_Constructor4() {
		final SchedulerTime startTime = new SchedulerTime();
		try {
			RecurringEntry re1 = new RecurringEntry(null, 10, 10);
			flop("NullPointerException was not thrown when startTime was null");
		} catch (NullPointerException npe) {
			verify(true, "NullPointerEception cought as expected");
		}
		try {
			RecurringEntry re2 = new RecurringEntry(startTime, 10, 1);
			flop("IllegalArgumentException was not thrown when iterations were 1 and period was > 0");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		try {
			RecurringEntry re3 = new RecurringEntry(startTime, -50, 23 );
			flop("IllegalArgumentException was not thrown when period was negative");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		//create ordinary entry and test getters for right values
		RecurringEntry re4 = new RecurringEntry(startTime, 23, 50);
		verify(re4.getStartTime().equals(startTime), "Returned startTime was different than passed startTime");
		verify(re4.getPeriod() == 23, "Returned period does not equal the passed period");
		final SchedulerTime expectedEnd = new SchedulerTime(startTime.timeMillis() + 23 * 49, TimeZone.getDefault());
		verify(re4.getEndTime().equals(expectedEnd), "end time was not calucalted properly from startTime, iterations and period");
		
		//create single expiration entry and test getter for right values
		RecurringEntry re5 = new RecurringEntry(startTime, 0, 0);
		verify(re5.getEndTime().equals(startTime), "Period was 0 but startTime was not equal to endTime.");
		
	}
	
	/**
	 * Tests the RecurringEntry(SchedulerTime, SchedulerTime, int) constructor. This method checks
	 * whether the appropriate exceptions are thrown by the tested constructors. It also
	 * tests whether the appropriate values are returned by the get* methods of the
	 * RecurringEntry after the object is initialized with the tested constructor
	 * @see RecurringEntry#RecurringEntry(com.sap.scheduler.api.SchedulerTime, com.sap.scheduler.api.SchedulerTime, int)
	 */
	public void test_Constructor5() {
		SchedulerTime startTime = new SchedulerTime();
		SchedulerTime endTime = new SchedulerTime(startTime.timeMillis() + 1000 * 60 * 10, TimeZone.getDefault()); //10 min later
		
		try {
			RecurringEntry re1 = new RecurringEntry(null, endTime, 10);
			flop("NullPointerException was not thrown when startTime was null");
		} catch (NullPointerException npe) {
			verify(true, "NullPointerEception cought as expected");
		}
		
		try {
			RecurringEntry re2 = new RecurringEntry(startTime, null, 10);
			flop("NullPointerException was not thrown when endTime was null");
		} catch (NullPointerException npe) {
			verify(true, "NullPointerException gought as expected");
		}
		
		try {
			RecurringEntry re3 = new RecurringEntry(startTime, endTime, 0);
			flop("IllegalArgumentException was not thrown when iterations were less than 1");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		try {
			RecurringEntry re4 = new RecurringEntry(startTime, endTime, 1);
			flop("IllegalArgumentException was not thrown when iterations were 1 but startTime was different than endTime");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		RecurringEntry re5 = new RecurringEntry(startTime, startTime, 1);
		verify(re5.getPeriod() == 0, "Retruned period was not 0 but iterations were 1");
		verify(re5.getStartTime().equals(startTime), "Returned startTime was different than passed startTime");
		verify(re5.getEndTime().equals(startTime), "Returned endTime was different than passed endTime");
	}
	
	/**
	 * Tests the RecurringEntry(SchedulerTime, SchedulerTime, long) constructor  This
	 * method checks whether  the appropriate exceptions are thrown by the tested constructors.
	 * It also tests whether the appropriate values are returned by the get* methods of
	 * the RecurringEntry after the object is initialized with the tested constructor
	 * @see RecurringEntry#RecurringEntry(com.sap.scheduler.api.SchedulerTime, com.sap.scheduler.api.SchedulerTime, long)
	 */
	public void test_Constructor6() {
		SchedulerTime startTime = new SchedulerTime();
		SchedulerTime endTime = new SchedulerTime(System.currentTimeMillis() + 10 * 1000 * 60 * 10, TimeZone.getDefault()); //10 mins since current time
		try {
			RecurringEntry re1 = new RecurringEntry(null, endTime, 10L * 1000 * 60 * 5); // 5 mins periods
			flop("NullPointerException was not thrown when startTime was null in call to RecurringEntry(SchedulerTime, SchedulerTime, long)");
		} catch (NullPointerException npe) {
			verify(true, "NullPointerException cought as expected");
		}
		
		try {
			RecurringEntry re2 = new RecurringEntry(startTime, endTime, 0);
			flop("IllegalArgumentException was not thrown by RecurringEntry(SchedulerTime, SchedulerTime, long) when startTime and endTime were different by period was 0");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		try {
			RecurringEntry re3 = new RecurringEntry(startTime, endTime, -1);
			flop("IllegalArgumentException was not thrown by RecurringEntry(SchedulerTime, SchedulerTime, long) when negative period was passed");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		try {
			RecurringEntry re4 = new RecurringEntry(startTime, new SchedulerTime(startTime.timeMillis() - 100000, TimeZone.getDefault()), 1000L);
			flop("IllegalArgumentException was not thrown when startTime was later than endTime");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegaArgumentException cought as expected");
		}
		
		try {
			SchedulerTime startTime1 = new SchedulerTime (System.currentTimeMillis(), TimeZone.getTimeZone("GMT+2"));
			SchedulerTime endTime1 = new SchedulerTime(startTime.timeMillis(), TimeZone.getTimeZone("GMT+3"));
			RecurringEntry re4 = new RecurringEntry(startTime1, endTime1, 5000L);
			flop("IllegalArgumentException was not thrown when startTime and endTime were in different time zones");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");
		}
		
		try {
			SchedulerTime endTime1 = new SchedulerTime(startTime.timeMillis() + 10000, startTime.getTimeZone());
			RecurringEntry re5 = new RecurringEntry(startTime, endTime1, 10001L);
			flop("IllgalArgumentException was not thrown when the period was longer than the period between startTime and endTime");
		} catch (IllegalArgumentException iae) {
			verify(true, "IllegalArgumentException cought as expected");			
		}
		
		//create an ok entry and check that all passed values are properly returned by the get methods
		RecurringEntry re6 = new RecurringEntry(startTime, endTime, 500L);
		verify(re6.getPeriod() == 500, "Passed period does not equal to returned period. Passed was 500, returned was " + re6.getPeriod());
		verify(re6.getStartTime().equals(startTime), "Passed start time does not equal to returned start time");
		verify(re6.getEndTime().equals(endTime), "Passed end time does not equal to returned end time");
	}
	
}

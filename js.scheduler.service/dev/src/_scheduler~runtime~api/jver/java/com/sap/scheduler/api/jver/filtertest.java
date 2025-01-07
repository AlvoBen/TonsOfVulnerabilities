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

import com.sap.tc.jtools.jver.framework.Test;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.api.Filter;

import java.util.Date;
import java.util.TimeZone;

public class FilterTest extends Test {

    public void test_Constructor() {
        Date date1 = new Date();
        SchedulerTime startTime = new SchedulerTime(date1, TimeZone.getDefault());
        SchedulerTime endTime = new SchedulerTime(date1.getTime() - 1, TimeZone.getDefault());

        try {
            new Filter(null, endTime);
            verify(false, "Filter(SchedulerTime, SchedulerTime) didn't throw NullPointerException when startTime was null");
        } catch (NullPointerException npe) {
            verify(true, "");
        }

        try {
            new Filter(startTime, null);
            verify(false, "Filter(SchedulerTime, SchedulerTime) didn't throw NullPointerException when endTime was null");
        } catch (NullPointerException npe) {
            verify(true, "");
        }

        try {
            new Filter(startTime, endTime);
            verify(false, "IllegalArgumentException was not thrown by Filter(SchedulerTime, SchedulerTime) though endTime" +
                    " was earlier than startTime");
        } catch (IllegalArgumentException iae) {
            verify(true, "IllegalArgumentException cought as expected");
        }

        Filter testObject = new Filter(endTime, startTime); //call with endTime in place of startTime as endTime is earlier than startTime.
        //This call should pass as values are not null and are correct.
        verify(testObject.getStartTime().equals(endTime), "Returned start time didn't test equal to the past start time");
        verify(testObject.getEndTime().equals(startTime), "Returned end time didn't test equal to the past end time");
    }
}

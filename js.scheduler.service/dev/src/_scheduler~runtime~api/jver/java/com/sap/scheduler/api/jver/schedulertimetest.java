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

import com.sap.scheduler.api.SchedulerTime;
import com.sap.tc.jtools.jver.framework.Test;

import java.util.Date;
import java.util.TimeZone;
import java.util.Random;

/**
 * This class tests methods of the SchedulerTime class.
 */
public class SchedulerTimeTest extends Test {

    /**
     * Tests the constrcutor <c>SchedulerTime(Date, TimeZone)</c>. It tests whether <c>NullPointerException</c>
     * is thrown when a null parameter is passed.
     */
    public void test_Constructor() {
        Date time = new Date();
        TimeZone tz = TimeZone.getDefault();
        try {
            new SchedulerTime(null, tz);
            verify(false, "SchedulerTime constructor didn't throw NullPointerException when null was passed for time parameter");
        } catch (NullPointerException npe) {
            verify(true, "");
        }

        try {
            new SchedulerTime(time, null);
            verify(false, "SchedulerTime constructor didn't throw NullPointerExeption when null was passed for time zone parameter");
        } catch (NullPointerException npe) {
            verify(true, "");
        }
    }

    /**
     * Tests <c>equals()</c> and <c>hashCode</c> methods. his test performs 500 iterations. At each iteration it creates
     * gets a random long. Then this long is used to create two <c>SchedulerTime</c> instances with randomly chosen
     * time zones from the interval GMT+0 to GMT+12. Then these instances are compared for equality and <c>hashCode</c>
     */
    public void test_equals_hashCode() {
        Random random = new Random();
        for (int absoluteTimeMillis = 0; absoluteTimeMillis < 500 ; absoluteTimeMillis ++) {
            Date randomTime1 = getRandomTime(random);
            TimeZone randomTimeZone1 = getRandomTimeZone(random);
            //test reflexivity
            SchedulerTime sct1 = new SchedulerTime(randomTime1, randomTimeZone1);
            //verify equals on the same instance
            verify(sct1.equals(sct1), "SchedulerTime.equals returned false when compared to the same instance");
            //verify equals with another instance initialized with the same timezone and time
            SchedulerTime sct1equal = new SchedulerTime(randomTime1, randomTimeZone1);
            verify(sct1.equals(sct1equal) && sct1equal.equals(sct1), "SchedulerTime instance does not test equal to an instance" +
                    "created with the same parameters");
            verify(sct1.hashCode() == sct1equal.hashCode(), "Scheduler time isntance returned a hashcode different than the hashcode" +
                "returned from a instance created with the same parameters");
            //symetry
            SchedulerTime sct2 = new SchedulerTime(randomTime1, getRandomTimeZone(random));
            verify(sct1.equals(sct2) && sct2.equals(sct1), "SchedulerTime instances differ when created with one and the same absolute time but" +
                   "different time zones");
            verify(sct1.hashCode() == sct2.hashCode(), "SchedulerTime instance " + sct1 + " tested equal to instance " + sct2 + " but returned different haschodes");

            //no need to test transitivity as sct1 and sct2 were created in a random manner. So creating a sct3 and testing
            //for sct3.equals(sct1) && sct3.equals(sct2) => sct2.equals(sct3) simply makes no sense

            SchedulerTime sct3 = new SchedulerTime(new Date(absoluteTimeMillis + 1), TimeZone.getTimeZone("GMT+0"));
            verify(!sct3.equals(sct1), "equals returned true when tested to a non equal object");
        }
    }

    /**
     * Tests the compareTo method. This test performs 500 iterations. At each iteration it creates three random longs,
     * used to create three <c>SchedulerTime</c> instances. Each scheduler time is initialized by a random time zone chosen
     * betwen GMT+0 to GMT + 12. then all 9 posibilities for a <= b && b <= c ==> a <= c are tested. There are 9 posibilities
     * as each of the variables a, b, c in the above implication could be any of the 3 random scheduler time instances.
     * Reflexivity is tested by the <c>test_equals()</c> method as <c>equals()</c> is implemented through <c>compareTo(Object)</c>
     */
    public void test_compareTo() {
        //reflexivity is tested through equals
        Random random = new Random();
        SchedulerTime[] sct = new SchedulerTime[3];
        for (int counter = 0; counter < 500; counter++) {
            for (int i = 0 ; i < sct.length; i++) {
                Date time = getRandomTime(random);
                sct[i] = new SchedulerTime(time, getRandomTimeZone(random));
            }

            for (int i = 0; i < sct.length; i++)
                for (int j = 0; j < sct.length; j++)
                    for (int k = 0; k < sct.length; k++) {
                        int res_a_b = sct[i].compareTo(sct[j]);
                        int res_b_c = sct[j].compareTo(sct[k]);
                        if ((res_a_b == -1 || res_a_b == 0)
                            && (res_b_c == -1 || res_b_c == 0)) {
                            int res_a_c = sct[i].compareTo(sct[k]);
                            verify(res_a_c == -1 || res_a_c == 0, "CompareTo was not transitive:"
                                    + sct[i] + " <= " + sct[j] + " & " + sct[j] + "<= " + sct[k] + " but " + sct[i] + " >  " + sct[k]);
                        }
                    }
        }

    }

    private TimeZone getRandomTimeZone(Random random) {
        return TimeZone.getTimeZone("GMT+" + random.nextInt(13));
    }

    private Date getRandomTime(Random random) {
        return new Date(random.nextLong());
    }
}

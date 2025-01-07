/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.external.simpletypes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    BooleanTest.class,
    ByteTest.class,
    BytesTest.class,
    CharTest.class,
    DateTest.class,
    DateTimeTest.class,
    YearMonthDayTest.class,
    DurationTest.class,
    MonthTest.class,
    StringsTest.class,
    DateConversionsTest.class
})
public class AllSimpleTypeTests {
}

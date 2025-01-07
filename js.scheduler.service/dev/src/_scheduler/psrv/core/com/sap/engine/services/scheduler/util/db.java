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
package com.sap.engine.services.scheduler.util;

import java.sql.Statement;

import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public final class DB {

	/**
	 * Counts the overal number of inserted rows by a batch statement. This method assumes that each
	 * statement in the batch inserts a single role. It works by summing all the elements of the
	 * batchInserts array. If any element of the array has a value <c>Statement.SUCCESS_NO_INFO</c>
	 * this method assumes that the corresponding batch statement has succeeded and has inserted a single
	 * role. Thus it adds 1 to the number of all rows count. If any element has a value of
	 * <c>Statement.EXECUTE_FAILED</c> this method throws IllegalArgumentException as this method is
	 * intended to be applied only on the array returned by Statement.executeBatch(). If some of the batched
	 * statements failed this method would throw <c>BatchUpdateException</c> and thus would not return a result.
	 * If this method is applied on the result of <c>BatchUpdateException.getUpdateCounts</c> it will most probably
	 * throw <c>IllegalArgumentException</c> as this array would most probably contain an element with value
	 * <c>Statement.EXECUTE_FAILED</c>. 
	 * @param batchInserts - an array returned by <c>Statement.executeBatch()</c>
	 * @return - the accumalitive number of rows coded by the batchInserts array.
	 */
	public static int countSingleRowBatchInserts(int[] batchInserts) throws IllegalArgumentException {
		int insertCount = 0;
		for (int inserts = 0; inserts < batchInserts.length; inserts++) {
			if (batchInserts[inserts] >= 0) {
				insertCount += batchInserts[inserts];
			} else if (Statement.SUCCESS_NO_INFO == batchInserts[inserts]) {
				insertCount++;
			} else if (Statement.EXECUTE_FAILED == batchInserts[inserts]) {
				throw new IllegalArgumentException(
						"the "+ inserts + "th. insert count contains a value equal to Statement.EXECUTE_FAILED." +
						" Thus the whole batch update has failed and no accumulative insert count could be calculated");
			} else {
				throw new UnreachableCodeException(
						"This code must has never been reached. This is a sever error as the jdbc driver" +
						" for the currently used database does not work properly. It has returned a number of "
						+ batchInserts[inserts]	+ " inserted rows for the " + inserts
						+ " batch statement. However it is allowed to return only " + Statement.EXECUTE_FAILED
						+ " in case of fauler, " + Statement.SUCCESS_NO_INFO
						+ " in case of successfull execution but the number of affected rows is unknown, a number" 
						+ " greater or equal to zero which is the number of affected rows");
			}
		}
		return insertCount;
	}
	
	/**
	 * This method is used to write a log record in server's log in case that the expected number of inserted
	 * rows does not match the number of actually inserted rows. It is typically used in conjunction with
	 * <c>countSingleRowBatchInserts</c>. The caller of <c>countSingleRowBatchInserts</c> may check whether
	 * the expected number of inserted rows is the same as the returned by <c>countSingleRowBatchInserts</c>
	 * and if not it may use this method to create a <c>SchedulerRuntimeException</c> and log the error
	 * condition in the server's log. This method uses the category Category.SYS_SERVER in order to write the log.
	 * @param expected - the expected number of inserted rows
	 * @param counted - the real number of inserted rows
	 * @param location - location used to write the the log.
	 * @return an instance of SchedulerRuntimeExceptoin containing the same text as the written log record.
	 */
	public static SchedulerRuntimeException createAndLogBadInsertCountExcetpion(int expected, int counted, Location location) {
		String errMsg = expected + " batched statements were executed. Every statement inserts a single row, however the number" +
				" of reported inserts was:" + counted + ". Please analyze the database as it may be in incosistent state." +
				" Or this may be a jdbc driver error.";
		Category.SYS_SERVER.logT(Severity.ERROR, location, errMsg);
		return new SchedulerRuntimeException(errMsg);
	}
}

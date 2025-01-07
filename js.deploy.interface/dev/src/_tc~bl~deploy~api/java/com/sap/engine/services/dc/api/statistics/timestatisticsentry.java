package com.sap.engine.services.dc.api.statistics;

import java.io.Serializable;

/**
 * This interface presents time statistics information for a deployment
 * operation.
 * 
 * @version 1.0
 * 
 */

public interface TimeStatisticsEntry extends Serializable {
	/** Time statistics of the whole deployment process. */
	public static final int ENTRY_TYPE_GLOBAL = 0;// 0
	/** Time statistics of the common steps different from other described. */
	public static final int ENTRY_TYPE_OTHER = 1 << 0;// 1
	/** Time statistics of the archive preparation step. */
	public static final int ENTRY_TYPE_PREPARE = 1 << 1;// 2
	/** Time statistics of the real deployment step. */
	public static final int ENTRY_TYPE_DEPLOY = 1 << 2;// 3
	/**
	 * Time statistics of the post process step. This step is a part of the real
	 * deployment.
	 */
	public static final int ENTRY_TYPE_POSTPROCESS = 1 << 3;// 8
	/**
	 * Time statistics of the component stop step. This step is a part of the
	 * real deployment.
	 */
	public static final int ENTRY_TYPE_STOP = 1 << 4;// 16
	/**
	 * Time statistics of the component delivery step. This step is a part of
	 * the real deployment.
	 */
	public static final int ENTRY_TYPE_DELIVERY = 1 << 5;// 32
	/**
	 * Time statistics of the component start step. This step is a part of the
	 * real deployment.
	 */
	public static final int ENTRY_TYPE_START = 1 << 6;// 64
	/** Time statistics of the validation step. */
	public static final int ENTRY_TYPE_VALIDATE = 1 << 7;// 128

	/**
	 * Returns the name of the operation which is being measured.
	 * 
	 * @return name of the operation
	 */
	public String getName();

	/**
	 * Returns <code>System.<b>currentTimeMillis</b>()</code> when the step is
	 * created.
	 * 
	 * @return starting time in milliseconds.
	 */
	public long getStartTime();

	/**
	 * Returns <code>System.<b>currentTimeMillis</b>()</code> when the step was
	 * finished.
	 * 
	 * @return finish time in milliseconds
	 */
	public long getFinishTime();

	/**
	 * Returns the duration of the step in millis
	 * 
	 * @return duration in millis
	 */
	public long getDuration();

	/**
	 * Returns array with all children statistics if there are otherwise 'null'.
	 * 
	 * @return array with all children statistics
	 */
	public TimeStatisticsEntry[] getTimeStatisticEntries();

	/**
	 * Returns the entry type( pre step, deployment(delivery) or post step )
	 * 
	 * @return entry type
	 */
	public int getEntryType();
}

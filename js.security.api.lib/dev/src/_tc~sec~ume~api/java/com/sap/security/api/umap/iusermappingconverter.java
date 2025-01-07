package com.sap.security.api.umap;

import java.util.Map;

import com.sap.security.api.UMException;

/**
 * <p>INTERNAL: Main interface for conversion of user mapping data between
 *   different encryption/persistence mechanisms.
 * </p>
 */
public interface IUserMappingConverter {

	/**
	 * <p>Current status of user mapping data conversion process.
     * </p>
     * 
	 * <p>Key for the <code>Map</code> returned by {@link #getConversionStatus}. 
     *   The corresponding <code>Map</code> value is of type <code>java.lang.Integer</code>.
     * </p>
     * 
	 * <p>Possible values:
     * </p>
     * <ul>
	 *   <li>{@link #CONVERSION_STATUS_NONE}</li>
	 *   <li>{@link #CONVERSION_STATUS_PREPARING}</li>
	 *   <li>{@link #CONVERSION_STATUS_SEARCHING}</li>
	 *   <li>{@link #CONVERSION_STATUS_CONVERTING}</li>
	 *   <li>{@link #CONVERSION_STATUS_COMPLETED}</li>
	 *   <li>{@link #CONVERSION_STATUS_ABORTED}</li>
     * </ul>
	 */
	public static final String CONVERSION_STATUS = "status";
    
	/**
	 * Possible status of user mapping data conversion: Conversion has not been
     * started (since the last startup of the server).
	 */
	public static final int CONVERSION_STATUS_NONE = 0;
	
	/**
	 * Possible status of user mapping data conversion: Preparing conversion
     * process.
	 */
	public static final int CONVERSION_STATUS_PREPARING = 10;
    
	/**
	 * Possible status of user mapping data conversion: Searching for relevant
     * user mapping data.
	 */
	public static final int CONVERSION_STATUS_SEARCHING = 20;
    
	/**
	 * Possible status of user mapping data conversion: Converting relevant user
     * mapping data.
	 */
	public static final int CONVERSION_STATUS_CONVERTING = 30;
    
	/**
	 * Possible status of user mapping data conversion: Conversion completed.
	 */
	public static final int CONVERSION_STATUS_COMPLETED = 40;
    
	/**
	 * Possible status of user mapping data conversion: Conversion aborted
     * because of some error.
	 */
	public static final int CONVERSION_STATUS_ABORTED = 99;
    
	/**
	 * <p>Total number of user mapping entries to convert.
     * </p>
     * 
	 * <p>Key for the <code>Map</code> returned by {@link #getConversionStatus}.
	 *   The corresponding <code>Map</code> value is of type
     *   <code>java.lang.Integer</code>.
     * </p>
	 */
	public static final String CONVERSION_ENTRIES_TOTAL = "entries.total";
    
	/**
	 * <p>Number of user mapping entries successfully converted.
     * </p>
     * 
	 * <p>Key for the <code>Map</code> returned by {@link #getConversionStatus}.
	 *   The corresponding <code>Map</code> value is of type
     *   <code>java.lang.Integer</code>.
     * <p>
	 */
	public static final String CONVERSION_ENTRIES_SUCCEEDED = "entries.succeeded";
	
	/**
	 * <p>Number of user mapping entries that couldn't be converted due to
     *   errors.
     * </p>
     * 
	 * <p>Key for the <code>Map</code> returned by {@link #getConversionStatus}.
	 *   The corresponding <code>Map</code> value is of type
     *   <code>java.lang.Integer</code>.
     * </p>
	 */
	public static final String CONVERSION_ENTRIES_FAILED = "entries.failed";
	
	/**
	 * <p>Time passed until start of conversion (in ms).
     * </p>
     * 
	 * <p>Key for the <code>Map</code> returned by {@link #getConversionStatus}. 
	 *   The corresponding <code>Map</code> value is of type
     *   <code>java.lang.Long</code>.
     * </p>
	 */
	public static final String CONVERSION_TIME_PASSED = "time.passed";
	
	/**
	 * Get the type identifier of this user mapping converter.
     * 
	 * @return String describing what the converter does.
	 */
	public String getType();
	
	/**
	 * Check whether conversion of user mapping data is currently possible.
     * 
	 * @return <code>true</code> if conversion is possible; <code>false</code>
     *         if this type of conversion can not be run
	 */
	public boolean isConversionPossible();

	/**
	 * <p>Convert user mapping data from one type / encryption level to another
	 *   (e.g. from "weak encryption" to "strong encryption").
     * </p>
     * 
     * <p>Please make sure this converter can be run by calling
     *   {@link #isConversionPossible()} before calling this method.
     * </p>
	 *
	 * <p>Note: The conversion process will be run in background threads (to
     *   avoid e.g. timeout of the UI component starting the conversion). You
     *   can specify the number of threads to be used by calling
     *   {@link #startConversion(int)} instead. Use
     *   {@link #getConversionStatus()} to retrieve information about the
     *   current status of the conversion process.
     * </p>
	 *
	 * <p>The conversion includes:
     * </p>
     * 
     * <ul>
	 *   <li>Change UME configuration accordingly (e.g. property
	 *     <code>ume.usermapping.unsecure</code> from <code>TRUE</code> to
	 *     <code>FALSE</code> to switch from weak to strong encryption)</li>
	 *   <li>Convert each single occurence of user mapping data (e.g. decrypt
     *     weak data and encrypt using strong cryptography)</li>
     * </ul>
     *
	 * <p>During conversion, both original and new type of user mapping data 
	 *   (e.g. both "weakly" and "strongly encrypted" data) will be processed
	 *   (unlike "normal" state: only user mapping data with the currently set
     *   encryption level is processed).
     * </p>
	 *
	 * <p>The configuration and state change is performed in a cluster wide way.
     * </p>
     *
	 * @throws UMException If the conversion can't be performed for some reason.
	 */
	public void startConversion() throws UMException;

	/**
	 * <p>Run the conversion in a background thread to avoid e.g. timeout of the
     *   UI component starting the conversion.
     * </p>
     * 
     * <p>Please make sure this converter can be run by calling
     *   {@link #isConversionPossible()} before calling this method.
     * </p>
     * 
     * <p>For further details, see {@link #startConversion()}.
     * </p>
     *
	 * <p>The current status of the conversion can be retrieved from
	 *   {@link #getConversionStatus()}.
     * </p>
     * 
	 * @param numberOfThreads Number of worker threads to use for actual
     *        conversion of user mapping data (searching for relevant entries is
     *        done in a single thread because there's no relevant potential for
     *        speed-up using multiple threads performing database queries at the
     *        same time - the database still has to serialize the queries). Must
     *        be &gt; 0.
     *
     * @throws UMException If the conversion can't be performed for some reason.
	 */
	public void startConversion(int numberOfThreads) throws UMException;

    /**
     * <p>Determine current status of a running conversion.
     * </p>
     * 
     * <p>Possible keys for <code>Map</code> entries are:
     * </p>
     * <ul>
     *   <li>{@link #CONVERSION_STATUS}</li>
     *   <li>{@link #CONVERSION_ENTRIES_TOTAL}</li>
     *   <li>{@link #CONVERSION_ENTRIES_SUCCEEDED}</li>
     *   <li>{@link #CONVERSION_ENTRIES_FAILED}</li>
     *   <li>{@link #CONVERSION_TIME_PASSED}</li>
     * </ul>
     * 
     * @return <code>Map</code> object containing status information
     */
	public Map getConversionStatus();
	
	/**
	 * <p>Reset the status information about the last conversion process.
     * </p>
     * 
	 * <p>To be used after the conversion has finished and the administrator
	 *   accepted the conversion results.
     * </p>
	 */
	public void resetStatus();

}

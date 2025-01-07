/**
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.appmigration.api.util;

import com.sap.engine.services.appmigration.api.exception.ConfigException;

/**
 * This interface is used to provide the migration module
 * with the functionality to track the status of its transactions
 * and to help with the module restartability.
 * Example: The application migration module has to perform
 * transaction1, transaction2, transaction3, etc. it should 
 * perform at the beginning of transaction1 the
 * check if this transaction has status OK, in this case the
 * transaction has been already executed and it should not be
 * executed once more time. At the end of each successfully executed
 * transaction the setTransactionStatus method should be called 
 * to set the status to STATUS_OK, if at the end the transaction3 
 * fails and the whole migration module should be restarted 
 * the first two transactions won't be executed again.
 *
 * @author Svetla Tsvetkova
 * @version 1.00
 */
public interface StatusIF
{

    /**
     * This constant indicates that the migration transaction 
     * has passed with no errors
     */
    public static final byte STATUS_OK = 1;

    /**
     * This constant indicates that there were errors during
     * this migration transaction
     */
    public static final byte STATUS_ERROR = 2;

    
    /**
     * This constant indicates that there is no information
     * stored about this transaction in the config 
     */
    public static final byte STATUS_NOT_DEFINED = 0;
 
    /**
     * Sets the status of the result of a transaction module execution. 
     * It should be called directly after the execution of each transaction
     * of the migration module.
     * @param name The name of the transaction that was called.
     * @param status The status after the transaction execution. 
     * Use one of the constants.
     */
    public void setTransactionStatus(String name, byte status) throws ConfigException;

    /**
     * Returns the status of a transation execution. 
     * This method should be invoked before every transaction execution.
     * @param name The name of the transaction which status should be checked.
     * @return The status of the execution of the module. 
     * If it is STATUS_ERROR, the transaction should be re-executed.
     */
    public byte getTransactionStatus(String name) throws ConfigException;

}

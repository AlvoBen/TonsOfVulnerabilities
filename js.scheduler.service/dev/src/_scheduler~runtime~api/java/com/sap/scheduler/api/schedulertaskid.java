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
package com.sap.scheduler.api;

import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.guid.GUID;

public class SchedulerTaskID extends AbstractIdentifier {
    private static final ConcreteInstanceFactory schedulerTaskIdFactory;
    static{
        schedulerTaskIdFactory = new ConcreteInstanceFactory() {
            public AbstractIdentifier createNotInitialized() {
                return new SchedulerTaskID();
            }
        };
    }

    private SchedulerTaskID() {
        
    }

    /**
     * Creates a new <code>SchedulerTaskID</code> instance that represent a new guid.
     * @return a new <code>SchedulerTaskID</code> instance
     * @see com.sap.guid.GUID
     */
    public static SchedulerTaskID newID() {
        SchedulerTaskID jobDefinitionID = new SchedulerTaskID();
        jobDefinitionID.setBytesNoCloneAndCheck(new GUID().toBytes());
        return jobDefinitionID;
    }

    /**
     * Reconstructs a <code>SchedulerTaskID</code> instance from a string representation. The supplied a
     * <code>String</code> must be in a valid <code>GUID</code> hex-string format.
     * @param stringGUID - a hex representation of a <code>GUID</code>. If this paramter is null then this
     * method returns null.
     * @return a <code>SchedulerTaskID</code> holding the identity specified by the given string representation
     * of <code>GUID</code>. Null if <code>stringGUID<code> is null.
     * @throws IllegalArgumentException - if the string is not a valid hex-string representation of a
     * <code>GUID</code>.
     * @see com.sap.guid.GUID#toHexString()
     */
    public static SchedulerTaskID parseID(String stringGUID) throws IllegalArgumentException {
        return (SchedulerTaskID)parseID(stringGUID, schedulerTaskIdFactory);
    }

    /**
     * Reconstructs a <code>SchedulerTaskID</code> instance from a byte[] representation of a guid. The
     * supplied <code>byte[]</code> must be in a valid <code>GUID</code> format.
     * @param byteGUID - a <code>byte[]</code> representation of a guid. If this parameter is null then this
     * method returns null.
     * @return a <code>SchedulerTaskID</code> holding the identity specified by the given byte array representation
     * of a <code>GUID</code>. Null if <code>byteGUID</code> is null.
     * @throws IllegalArgumentException - thrown if the <code>byteGUID</code> argument is not a valid
     * <code>GUID</code> byte representation
     * @see com.sap.guid.GUID#toBytes()
     */
    public static SchedulerTaskID parseID(byte[] byteGUID) throws IllegalArgumentException {
        return (SchedulerTaskID)parseID(byteGUID, schedulerTaskIdFactory);
    }
}

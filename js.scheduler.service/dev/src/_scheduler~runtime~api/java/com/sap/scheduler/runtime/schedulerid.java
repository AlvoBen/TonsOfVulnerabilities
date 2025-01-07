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

package com.sap.scheduler.runtime;

import com.sap.guid.GUID;
import com.sap.scheduler.runtime.AbstractIdentifier;

/**
 * Uniquely identifies a Job instance. This class is XML Serializable and can
 * be used for a web service parameter. The public constructor of this class is
 * intended to be used only by serialization deserialization framework. This is basically
 * a XML-serializable equivalent of the <code>GUID</code> class, however it has the
 * semantics of identifying a given job instance.
 * @see GUID
 * @see com.sap.scheduler.runtime.AbstractIdentifier
 */
public final class SchedulerID extends AbstractIdentifier  {

    private static final ConcreteInstanceFactory schedulerIdFactory;
    static{
        schedulerIdFactory = new ConcreteInstanceFactory() {
            public AbstractIdentifier createNotInitialized() {
                return new SchedulerID();
            }
        };
    }

    /**
     * This constructor is intended only for XML serialization/deserialization. Do not use it
     * for creating instances of SchedulerID use one of the <code>newID</code> static methods. When
     * created with this constructor this identity object identifies no job. In order to give it
     * identity the method <code>setBytes()</code> must be called.
     * @deprecated This constructor is intended to be used only by XML serialization/deserializtion.
     * In order to create new instances of this class use one of the static <newID> methods
     */
    public SchedulerID() {

    }

    /**
     * Creates a new <code>SchedulerID</code> instance that represent a new guid.
     * @return a new <code>SchedulerID</code> instance
     * @see GUID
     */
    public static SchedulerID newID() {
        SchedulerID schedulerID = new SchedulerID();
        schedulerID.setBytesNoCloneAndCheck(new GUID().toBytes());
        return schedulerID;
    }

    /**
     * Reconstructs a <code>SchedulerID</code> instance from a string representation. The supplied a
     * <code>String</code> must be in a valid <code>GUID</code> hex-string format.
     * @param stringGUID - a hex representation of a <code>GUID</code>. If this parameter is null then this
     * method returns null.
     * @return a <code>SchedulerID</code> holding the identity specified by the given string representation
     * of <code>GUID</code>. Null if the passed <code>stringGUID</code> is null.
     * @throws IllegalArgumentException - if the string is not a valid hex-string representation of a
     * <code>GUID</code>.
     * @see com.sap.guid.GUID#toHexString()
     */
    public static SchedulerID parseID(String stringGUID) throws IllegalArgumentException {
        return (SchedulerID)parseID(stringGUID, schedulerIdFactory);
    }

    /**
     * Reconstructs a <code>SchedulerID</code> instance from a byte[] representation of a guid. The
     * supplied <code>byte[]</code> must be in a valid <code>GUID</code> format.
     * @param byteGUID - a <code>byte[]</code> representation of a guid. If this parameter is null then this
     * method return null
     * @return a <code>SchedulerID</code> holding the identity specified by the given byte array representation
     * of a <code>GUID</code> . Null if the passed <code>byteGUID</code> is null.
     * @throws IllegalArgumentException - thrown if the <code>byteGUID</code> argument is not a valid
     * <code>GUID</code> byte representation
     * @see com.sap.guid.GUID#toBytes()
     */
    public static SchedulerID parseID(byte[] byteGUID) throws IllegalArgumentException {
        return (SchedulerID)parseID(byteGUID, schedulerIdFactory);
    }
}

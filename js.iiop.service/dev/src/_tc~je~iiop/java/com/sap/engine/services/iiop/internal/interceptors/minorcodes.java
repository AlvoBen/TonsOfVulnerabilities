/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.interceptors;

import org.omg.CORBA.OMGVMCID;

/**
 * This class implements GIOP message header.
 * It is the base class of all GIOP messages available in CORBA 2.2 specification.
 * It also contains most of the constants used in GIOP protocol. This class is used
 * for parsing/creating GIOP message headers and reading/writing GIOP messages.
 *
 * @author Ivan Atanassov
 */
public final class MinorCodes {

    public static final int INVALID_PI_CALL = OMGVMCID.value + 14;
    public static final int SERVICE_CONTEXT_ADD_FAILED = OMGVMCID.value + 15;
    public static final int POLICY_FACTORY_REG_FAILED  = OMGVMCID.value + 16;

    public static final int INVALID_SERVICE_CONTEXT_ID = OMGVMCID.value + 26;
    public static final int RIR_WITH_NULL_OBJECT    = OMGVMCID.value + 27;
    public static final int INVALID_COMPONENT_ID    = OMGVMCID.value + 28;
    public static final int INVALID_PROFILE_ID      = OMGVMCID.value + 29;

    public static final int POLICY_UNKNOWN = OMGVMCID.value + 2;

    public static final int PI_OPERATION_NOT_SUPPORTED = OMGVMCID.value + 1;

    public static final int REQUEST_CANCELLED = OMGVMCID.value + 3;

    public static final int UNKNOWN_USER_EXCEPTION  = OMGVMCID.value + 1;
}

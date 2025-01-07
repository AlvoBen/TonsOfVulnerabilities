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

import java.lang.reflect.Field;

public abstract class JobDefinitionType {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0;
    public static final int MDB_JOB_DEFINITION = 0;
    public static final int INVALID_JOB_DEFINITION = -1;

    public static boolean isValidJobDefintionType(int value) {
        return value == 0;
    }

    public static String toString(int type) {
        Field[] constants = JobDefinitionType.class.getFields();
        try {
            String name = "";
            for (int i = 0; i < constants.length; i++) {
                name = constants[i].getName();
                if(constants[i].getInt(null) == type
                    && !name.equals("MAX_VALUE")
                    && !name.equals("MIN_VALUE")) {
                    return name;
                }
            }
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException should never happen as all the constants in JobDefintionType are public",
                    iae);
        }
        return "IVALID_JOB_DEFINITION";
    }
}

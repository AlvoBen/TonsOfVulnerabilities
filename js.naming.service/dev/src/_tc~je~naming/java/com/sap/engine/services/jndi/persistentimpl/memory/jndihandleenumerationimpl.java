/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistentimpl.memory;

import com.sap.engine.lib.util.ConcurrentArrayObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;

/**
 * Enumeration containing JNDI Handles
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class JNDIHandleEnumerationImpl implements JNDIHandleEnumeration, java.io.Serializable {

    /**
     * ConcurrentArrayObject storing the data
     */
    ConcurrentArrayObject v;
    /**
     * Stpres the container ID
     */
    String cName;
    /**
     * Counter
     */
    int counter;
    /**
     * serial version UID
     */
    static final long serialVersionUID = 8035061227086210168L;

    /**
     * Constructor
     *
     * @param v     Vector to use
     * @param cName Container ID to work with
     */
    public JNDIHandleEnumerationImpl(ConcurrentArrayObject v, String cName) {
        //    System.out.println("&&&& NEW HANDLE ENUMERATION &&&& " + v.size());
        this.v = v;
        this.cName = cName;
        this.counter = 0;
        //    System.out.println("&&&& end constructor ");
    }

    /**
     * Retrives the next object
     *
     * @return Next handle requested
     */
    public JNDIHandle nextObject() {
        if (counter >= v.size()) {
            return null;
        }

        String next = (String) v.elementAt(counter);
        counter++;
        return new JNDIHandleImpl(this.cName, next);
    }

    /**
     * Deretmines if there are more elements
     *
     * @return "true" if there are more elements
     */
    public boolean hasMoreElements() {
        return (counter < v.size());
    }

    /**
     * Closes the enumeration
     */
    public void closeEnumeration() {

	}

}

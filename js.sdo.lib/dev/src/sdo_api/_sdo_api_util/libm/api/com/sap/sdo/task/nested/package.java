/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.task.nested;

/**
 * @author D042774
 *
 */
public class Package {
    private String _namespace;
    private String _name;

    /**
     * 
     */
    public Package() {
    }

    /**
     * @return Returns the namespace.
     */
    public String getNamespace() {
        return _namespace;
    }

    /**
     * @param pNamespace The namespace to set.
     */
    public void setNamespace(String pNamespace) {
        _namespace = pNamespace;
    }

    /**
     * @return Returns the package name.
     */
    public String getName() {
        return _name;
    }

    /**
     * @param pUri The package name to set.
     */
    public void setName(String pName) {
        _name = pName;
    }

}

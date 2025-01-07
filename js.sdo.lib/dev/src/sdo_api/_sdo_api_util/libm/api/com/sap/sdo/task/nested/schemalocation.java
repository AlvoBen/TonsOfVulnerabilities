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
public class SchemaLocation {
    private String _location;
    private String _uri;

    /**
     * 
     */
    public SchemaLocation() {
    }

    /**
     * @return Returns the schemaLocation.
     */
    public String getLocation() {
        return _location;
    }

    /**
     * @param pLocation The schemaLocation to set.
     */
    public void setLocation(String pLocation) {
        _location = pLocation;
    }

    /**
     * @return Returns the namespace or the uri#name pair of the type.
     */
    public String getUri() {
        return _uri;
    }

    /**
     * @param pUri The namespace or the uri#name pair of the type to set.
     */
    public void setUri(String pUri) {
        _uri = pUri;
    }

}

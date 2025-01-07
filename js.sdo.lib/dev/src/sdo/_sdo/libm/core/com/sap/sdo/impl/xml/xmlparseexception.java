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
package com.sap.sdo.impl.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

/**
 * @author D042774
 *
 */
public class XmlParseException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 3652476023818669541L;

    /**
     * 
     */
    public XmlParseException() {
    }

    /**
     * @param pMessage
     */
    public XmlParseException(String pMessage) {
        super(pMessage);
    }

    /**
     * @param pCause
     */
    public XmlParseException(Throwable pCause) {
        super(pCause.getMessage());
        initCause(getRealCause(pCause));
    }

    /**
     * @param pMessage
     * @param pCause
     */
    public XmlParseException(String pMessage, Throwable pCause) {
        super(pMessage);
        initCause(getRealCause(pCause));
    }

    /**
     * @param pCause
     * @return
     */
    private Throwable getRealCause(Throwable pCause) {
        if (pCause instanceof XMLStreamException) {
            Throwable nested = ((XMLStreamException)pCause).getNestedException();
            if (nested != null) {
                return nested;
            }
        }
        return pCause;
    }

}

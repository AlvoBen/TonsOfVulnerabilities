/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.xml.stream.adapters;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * @author D042774
 *
 */
public interface ElementAdapter extends PropertyAdapter {
    int getAttributeCount();
    /**
     * @param ns
     * @param local
     * @return
     */
    String getAttributeValue(String ns, String local);
    /**
     * @param index
     * @return
     */
    QName getAttributeName(int index);
    /**
     * @param index
     * @return
     */
    String getAttributeNamespace(int index);
    /**
     * @param index
     * @return
     */
    String getAttributeLocalName(int index);
    /**
     * @param index
     * @return
     */
    String getAttributePrefix(int index);
    /**
     * @param index
     * @return
     */
    String getAttributeValue(int index);
    /**
     * @param index
     * @return
     */
    String getAttributeType(int index);
    boolean hasChild();
    ElementAdapter nextChild();
    String getNamespaceURI(int index);
    String getNamespaceURI(String prefix);
    String getNamespacePrefix(int index);
    int getNamespaceDeclarationCount();
    boolean isStarted();
    boolean isEnded();
    void start();
    void end();
    NamespaceContext getNamespaceContext();
    String generateReference();
}

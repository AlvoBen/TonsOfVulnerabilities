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
package com.sap.sdo.api.helper;

import commonj.sdo.DataObject;

/**
 * Handler to affect naming of open properties while parsing XML.
 * 
 * @author D042774
 *
 */
public interface NamingHandler {

    /**
     * Enables reverse renaming of open content properties.
     * The mangled names could be defined on property object directly either as alias or sdo name.
     * 
     * @param pPropertyObj unnamed property object.
     * @param pUri uri of element or attribute as read from document.
     * @param pLocalName local name of element or attribute  as read from document.
     * @param pIsElement indicates if open content property is an element or attribute.
     * @param pParent container where the open content property has to be defined.
     */
    void nameOpenContentProperty(DataObject pPropertyObj, String pUri, String pLocalName, boolean pIsElement, DataObject pParent);

}

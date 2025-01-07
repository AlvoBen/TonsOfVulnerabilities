/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.objects.path;


import com.sap.sdo.impl.objects.GenericDataObject;

/**
 * implementation to resolve xpath definitions (w3c).
 * 
 * @author D042774
 *
 */
public class XPathResolverImpl extends AbstractPathResolver {

    /**
     * Constructor of path resolver instance of XPaths.
     * @param pRoot data object to resolve path at.
     * @param path path to resolve including namespace and root declaration.
     * @param p index of path string without namespace and root declaration.
     */
    public XPathResolverImpl(GenericDataObject pRoot, String path, int p) {
        super(pRoot, path, p);
    }

    protected PropertyData extractPropertyData(String step, GenericDataObject dataObject) {
        final PropertyData propData;
        int bracket = getLastIndexOfNonEscapedChar(step, '[');
        
        if (bracket > 0) {
            propData = matchPathWithBrackets(step, dataObject, bracket);
        } else {
            propData = new PropertyData(step, _path, dataObject);
        }
        return propData;
    }
}

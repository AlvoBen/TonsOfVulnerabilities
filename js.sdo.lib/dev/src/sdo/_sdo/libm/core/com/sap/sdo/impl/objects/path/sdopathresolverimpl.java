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

import commonj.sdo.Property;

/**
 * implementation to resolve sdo-specific xpath definitions.
 * 
 * @author D042774
 *
 */
public class SdoPathResolverImpl extends AbstractPathResolver {

    /**
     * Constructor of path resolver instance of SDOPaths.
     * @param pRoot data object to resolve path at.
     * @param path path to resolve including namespace and root declaration.
     * @param p index of path string without namespace and root declaration.
     */
    public SdoPathResolverImpl(GenericDataObject pRoot, String path, int p) {
        super(pRoot, path, p);
    }

    protected PropertyData extractPropertyData(String step, GenericDataObject dataObject) {
        // quick exit
        Property prop = dataObject.getInstanceProperty(step);
        if (prop != null) {
            return new PropertyData(step, _path, dataObject);
        }
        
        final PropertyData propData;
        int point = getLastIndexOfNonEscapedChar(step, '.');
        int bracket = getLastIndexOfNonEscapedChar(step, '[');
        
        if (bracket > 0) {
            propData = matchPathWithBrackets(step, dataObject, bracket);
        } else if (point > 0) {
            String ixRef = step.substring(point + 1);
            try {
                propData = new PropertyData(
                    step.substring(0, point),
                    Integer.parseInt(ixRef),
                    _path,
                    dataObject);
            } catch (NumberFormatException nfe) { //$JL-EXC$
                throw new IllegalArgumentException(
                    "bad index \""+ixRef+"\" at "+(_currentpathIndex+point+1)+" in \""+_path+"\"");
            }
        } else {
            propData = new PropertyData(step, _path, dataObject);
        }
        return propData;
    }
}

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
package com.sap.sdo.impl.objects;

import java.util.Locale;

import com.sap.sdo.impl.objects.path.IPathResolver;
import com.sap.sdo.impl.objects.path.SdoPathResolverImpl;
import com.sap.sdo.impl.objects.path.XPathResolverImpl;

/**
 * helper to iterate over the data graph along an xpath 
 * expression. 
 * 
 * @author hb
 *
 */
public class XPathHelper
{
    /**
     * Enumeration of all supported namespaces.
     * @author D042774
     *
     */
    private enum NameSpace {
        /**
         * Support namespace without special syntax. Parsed as default.
         */
        SDO {
            IPathResolver getPathResolver(GenericDataObject pRoot, String path, int p) {
                return new SdoPathResolverImpl(pRoot,path,p);
            }
        },
        /**
         * Special syntax for sdo.
         * Supports index for multi-valued properties as &lt;poperty&gt;.&lt;index&gt;
         */
        SDOPATH {
            IPathResolver getPathResolver(GenericDataObject pRoot, String path, int p) {
                return new SdoPathResolverImpl(pRoot,path,p);
            }
        },
        /**
         * Supports xpath syntax.
         * This is the default namespace.
         */
        XPATH {
            IPathResolver getPathResolver(GenericDataObject pRoot, String path, int p) {
                return new XPathResolverImpl(pRoot,path,p);
            }
        };
        
        /**
         * Returns specialized path resolver for namespace.
         * 
         * @param pRoot root element to use as starting point of path navigation.
         * @param path a path to resolve.
         * @param p points to path after namespace and root declaration.
         * @return instance of path resolver.
         */
        abstract IPathResolver getPathResolver(GenericDataObject pRoot, String path, int p);
    }

    /**
     * This class only provides a static method and instantiation should be avoided.
     * Hidden constructor, never called.
     */
    private XPathHelper() {
        super();
    }

    /**
     * Instantiate a path resolver for given path.
     * This method could be called from this package only.
     * 
     * @param d dataobject that is used as starting point of path navigation.
     * @param path path to dataobject or property.
     * @return path resolver for given path and data object.
     */
    static IPathResolver pathResolver(GenericDataObject d, String path) {
        NameSpace ns = NameSpace.SDO;
        GenericDataObject dataObject = d;
        // check for scheme
        int s = path.indexOf(':');
        if (s > 0) {
            try {
                ns = NameSpace.valueOf(path.substring(0, s).toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("illegal scheme \""+path.substring(0,s)+"\"", ex);
            }
        }
        // increase index because it's either -1 or points to ':'
        if (path.charAt(++s)=='/') {
            dataObject = ((DataObjectDecorator)d.getRootObject()).getInstance();
            ++s;
        }
        return ns.getPathResolver(dataObject, path, s);
        
    }
}


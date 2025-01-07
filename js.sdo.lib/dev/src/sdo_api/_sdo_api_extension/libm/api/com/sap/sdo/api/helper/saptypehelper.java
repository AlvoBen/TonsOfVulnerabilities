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
package com.sap.sdo.api.helper;

import java.util.Set;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;

public interface SapTypeHelper extends TypeHelper {
    
    InterfaceGenerator createInterfaceGenerator(String rootPath);
    
    /**
     * Removes types and global properties from this HelperContext.
     * All types and global properties in the parameters will be removed. The
     * types and global properties that reference these types directly or 
     * indirectly will be also removed. So the HelperContext will
     * stay consistent after removing the metadata.
     * After the call of this method the parameters will contain all removed
     * types and global properties. The number of the entries could have been
     * increased. The parameters must be mutable Sets e.g. a HashSet.
     * WARNING! Existing DataObjects that use removed types or properties might
     * behave erroneous.
     * @param pTypes The set of types to remove.
     * @param pProperties The set of properties to remove.
     */
    void removeTypesAndProperties(Set<Type> pTypes, Set<Property> pProperties);

}

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
package com.sap.sdo.impl.types;

import java.io.Serializable;
import java.util.Map;

import com.sap.sdo.api.types.SapProperty;
import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public interface SdoProperty extends Serializable, SapProperty {
	URINamePair getRef();
    void setIndex(int index);
    int getIndex(Type pType);
    int getRequestedIndex();
    void setRequestedIndex(int pRequestedIndex);
    boolean defined();

    /**
     * Replaces the value by an equal cached value to save space by reducing the
     * number of instances. If the value is null, the return value is also null.
     * @param value The value.
     * @return The value or a equal cached value;
     */
    Object getCachedValue(Object value);

    boolean isOppositeContainment();

    /**
     * Returns the namespace if the property has to berendered with a prefix in
     * XML.
     * @return The namespace uri.
     */
    String getUri();

    /**
     * Creates an index map array for a many valued property of the containing
     * DataObject. The type of this DataObject must have this property.
     * The cached data are soft and weak.
     * @param pContainingObject The containing DataObject.
     * @return The index map array.
     */
    Map<Object, Integer>[] createIndexMaps(DataObject pContainingObject);

    /**
     * Returns the index map array for a many valued property of the containing
     * DataObject. The type of this DataObject must have this property.
     * @param pContainingObject The containing DataObject.
     * @return The index map array or null if never created.
     */
    Map<Object, Integer>[] getIndexMaps(DataObject pContainingObject);

    /**
     * Removes the index map array for a many valued property of the containing
     * DataObject. The type of this DataObject must have this property.
     * @param pContainingObject The containing DataObject.
     */
    void removeIndexMaps(DataObject pContainingObject);

    boolean isOrphanHolder();
}

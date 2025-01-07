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

import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.Property;

/**
 * A PropValue encapsulates a {@link Property} and its value. It also has a link
 * to the container data object.
 * @author D042807
 *
 * @param <T> Object for single valued properties and List&lt;Object&gt; for multi-valued properties.
 */
public interface PropValue<T> {
 
    /**
     * Returns the Poperty.
     * @return The Poperty.
     */
    SdoProperty getProperty();
    
    /**
     * Returns the value of the {@link Property}. A single value is from the
     * class that is defined in the properties Type by 
     * {@link commonj.sdo.Type#getInstanceClass()}.
     * @return A single value or a List.
     */
    T getValue();

    /**
     * Sets the value of the {@link Property}. The single values will be 
     * convereted to the class that is defined in the properties Type by 
     * {@link commonj.sdo.Type#getInstanceClass()}. For multi-valued properties
     * a Collection is expected.
     * @param pValue The new value(s).
     */
    void setValue(Object pValue);
    
    /**
     * Returns the old value at the time when logging began. If the value was
     * not changed, the current value is returned.
     * @return The value when logging began.
     */
    T getOldValue();
    
    /**
     * Checks, if the value was modified.
     * @return true, if the value was modified.
     */
    boolean isModified();
    
    /**
     * Saves the current value to the old values, if logging is activated and
     * it is not already modified.
     */
    void checkSaveOldValue();
    
    /**
     * Sets a single value of a multi-valued {@link Property} at the defined
     * index. For single-valued properties only index 0 is accepted.
     * @param pIndex The index in the Property.
     * @param pValue The new value.
     * @return No relevance for single valued Properties, just to be compatible with {@link java.util.List#set(int, Object)}.
     * @see #setValue(T)
     * @see java.util.List#set(int, Object)
     */
    Object set(int pIndex, Object pValue);
    
    /**
     * Removes the single value from the {@link Property}. If it was the last
     * value or the single value. The Property is unset.
     * @param pValue The value to remove.
     * @return True if the value was part of the property.
     */
    boolean remove(Object pValue);
    
    /**
     * Clears the {@link Property}.
     * @see commonj.sdo.DataObject#unset(Property)
     */
    void unset();
    
    /**
     * Checks, if the {@link Property} is set.
     * @return True if the Property has a non-default value.
     * @see commonj.sdo.DataObject#isSet(Property)
     */
    boolean isSet();
    
    /**
     * Determines, if it is a multi-valued property.
     * @return true, if the property is multi-valued.
     * @see Property#isMany()
     */
    boolean isMany();
    
    /**
     * Returns the owner DataObject.
     * @return The container DataObject.
     */
    GenericDataObject getDataObject();
    
    /**
     * Converts the value to the specific class.
     * @param <C> The expected result class.
     * @param pClass The expected result class.
     * @return The value converted to the result class.
     */
    <C> C getConvertedValue(Class<C> pClass);

    /**
     * Converts a single value at the index to the specific class.
     * For single-valued properties only index 0 is accepted.
     * @param <C> The expected result class.
     * @param pIndex The index in the Property.
     * @param pClass The expected result class.
     * @return The value converted to the result class.
     */
    <C> C getConvertedValue(int pIndex, Class<C> pClass);
    
    /**
     * Returns true if the {@link Property} is read only and the read-only mode
     * is activated.
     * @return True, if the value is unchangeable.
     */
    boolean isReadOnly();
    
    /**
     * This internal method is called, if the relationship to the opposite
     * property was disconnected by changing the opposite property.
     * @param pDataObject The DataObject that contains the opposite Property.
     */
    void internDetachOpposite(DataObjectDecorator pDataObject);

    /**
     * This internal method is called, if the relationship to the opposite
     * property was connected by changing the opposite property.
     * @param pDataObject The DataObject that contains the opposite Property.
     */
    void internAttachOpposite(DataObjectDecorator pDataObject);
    
    /**
     * If this PropValue represents a many valued property filled with
     * DataObjects, this method returns the index of that DataObject with a
     * property called <code>propName</code> and the value <code>value</code>.
     * If it is a single valued property, it returns 0 if the contained
     * DataObject matches the condition.
     * If no DataObject matches, the result is -1.
     * For performance reasons index maps are created in most cases.
     * So the first access has linar complexity and the following constant
     * complexity.
     * @param propName The name of the property that have to be compared.
     * @param value The value to compare.
     * @return The index of the DataObject in this PropValue or -1;
     * @see #invalidateIndexMaps()
     * @see #invalidateIndexMap(SdoProperty)
     */
    int getIndexByPropertyValue(String propName, Object value);
        
    void trimMemory();

}

package com.sap.sdo.impl.objects;

import java.util.List;

import com.sap.sdo.impl.objects.DataStrategy.State;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.ChangeSummary.Setting;

public interface InternalDataObjectModifier {

    /**
     * Sets the value of a {@link Property} without any containment or opposite 
     * checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#setPropertyWithoutCheck(Property, Object)
     */
    public void setPropertyWithoutCheck(Property pProperty, Object pValue);
    
    /**
     * Adds a value to a multi-valued {@link Property} without any containment
     * or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced multi-valued properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#addToPropertyWithoutCheck(Property, Object)
     */
    public void addToPropertyWithoutCheck(Property pProperty, Object pValue);

    /**
     * Sets the {@link Sequence} without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pSettings The new List of Property-value-pairs.
     * @see DataStrategy#setSequenceWithoutCheck(List)
     */
    public void setSequenceWithoutCheck(List<Setting> pSettings);
    
    /**
     * Adds a new value to the {@link Sequence} without any containment or 
     * opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#addToSequenceWithoutCheck(Property, Object)
     */
    public void addToSequenceWithoutCheck(Property pProperty, Object pValue);

    /**
     * Sets the new container and containment property without containment or
     * opposite checks.
     * @param pContainer The new container.
     * @param pProperty The new containment property.
     */
    public void setContainerWithoutCheck(DataObject pContainer, Property pProperty);
    
    /**
     * Sets the {@link State change state} directly without any checks.
     * @param pState The new change state.
     * @see DataStrategy#setChangeStateWithoutCheck(State)
     */
    public void setChangeStateWithoutCheck(State pState);

    
    public void addPropertyValueWithoutCheck(Property pProperty, Object pValue);
    
    public void unsetPropertyWithoutCheck(Property pProperty);

    public Property findOpenProperty(String pUri, String pXsdName, boolean pIsElement);
    
    public void trimMemory();
}

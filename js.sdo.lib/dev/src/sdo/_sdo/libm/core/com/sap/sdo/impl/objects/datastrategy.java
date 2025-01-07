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

import java.util.List;

import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;

/**
 * The DataStrategy hides the implementation of the sequence and the propeties
 * from the {@link com.sap.sdo.impl.objects.GenericDataObject GenericDataObject}.
 * So the sequenced und the non-sequenced case can have specialized implementations.
 * @author D042807
 */
public interface DataStrategy {

    GenericDataObject getDataObject();
    /**
     * Enumeration to repersent the change state of the DataObject.
     */
    public static enum State {MODIFIED, CREATED, DELETED, UNCHANGED};

    public DataObject createDataObject(Property property, Type type);

    void setDataObject(GenericDataObject pDataObject);

    /**
     * @see DataObject#getSequence()
     */
    Sequence getSequence();

    /**
     * Finds the {@link PropValue} for a property index.
     * If the parameter pCreate is false the method can return null if the
     * Property was never used. This can be faster and less memory consuming
     * in some cases. If the pCreate parameter is true, the {@link PropValue}
     * will be created in these cases.
     * @param pIndex The indes of the Property.
     * @param pProperty The property, if known, null otherwise.
     * @param pCreate Create or nor a PropValue if not created yet.
     * @return The PropValue or null.
     * @see GenericDataObject#getPropValue(Property, boolean)
     */
    PropValue<?> getPropValue(int pIndex, Property pProperty, boolean pCreate);

    /**
     * Returns the instance Properties at the point when logging began.
     * @return The old instance Properties.
     * @see GenericDataObject#getOldInstanceProperties()
     * @see DataObject#getInstanceProperties()
     */
    List<Property> getOldInstanceProperties();

    /**
     * Returns the value of a Property at the point when logging began or null
     * if it was not changed.
     * @param pProperty The Property.
     * @return The old value as Setting.
     * @see GenericDataObject#getOldValue(Property)
     * @see commonj.sdo.ChangeSummary#getOldValue(DataObject, Property)
     */
    Setting getOldValue(Property pProperty);

    /**
     * Returns the old Properties that has changed since the logging has begun.
     * @return The list of changed Properties.
     * @see GenericDataObject#getOldChangedProperties()
     */
    List<Property> getOldChangedProperties();

    /**
     * Indicates if the DataObject was in scope of a ChangeSummary when beginLogging() was called.
     * @return true if changes to the DataObject will be logged.
     */
    boolean isInitialScope();

    /**
     * Sets the logging state of a DataObject.
     * @param pLogging The new logging state.
     */
    void setInitalScope(boolean pLogging);

    /**
     * Sets the change {@link State} of the DataObject.
     * Note that {@link State#MODIFIED} can not overwrite {@link State#CREATED}
     * or {@link State#DELETED}. In that cases the state stills {@link State#CREATED}
     * or {@link State#DELETED}.
     * @param pState The new state.
     * @param pCheckLogging If true, the new state has only effect if the DataObject
     * is in scope of a logging {@link commonj.sdo.ChangeSummary}. If false, it is
     * assumed that it is already checked that it is logging.
     * @see setChangeStateWithoutCheck(State)
     */
    void setChangeState(State pState, boolean pCheckLogging);

    /**
     * Returns the current change {@link State} of the DataObject.
     * @return The state.
     * @see GenericDataObject#isModified()
     * @see GenericDataObject#isCreated()
     * @see GenericDataObject#isDeleted()
     */
    State getChangeState();

    public DataObjectDecorator getOldStateFacade();

    /**
     * Returns the containment {@link PropValue} at the point when logging began.
     * @return The old containment PropValue.
     * @see GenericDataObject#getContainmentPropValue()
     * @see GenericDataObject#getOldContainmentPropValue()
     */
    PropValue<?> getOldContainmentPropValue();

    /**
     * Returns the containment {@link PropValue} at the point when logging began.
     * @return The old containment PropValue.
     * @see GenericDataObject#getContainmentPropValue()
     * @see GenericDataObject#getOldContainmentPropValue()
     */
    Sequence getOldSequence();

    /**
     * Clears the old values.
     * @see GenericDataObject#resetChangeSummary()
     */
    void resetChangeSummary();

    /**
     * Determines the current index of the Property. If the {@link Type} is open
     * and the {@link Property} is not present, it will assigned to the
     * {@link DataObject} as open Property if the parameter pCreate is true.
     * @param pProperty The property.
     * @param pCreate If true, assign open Property if not present.
     * @return The index of the Property.
     */
    int indexOfProperty(Property pProperty, boolean pCreate);

    /**
     * Determines a {@link Property} by its index. The order is define in
     * {@link #getInstanceProperties()}.
     * @param pIndex The index to lookup.
     * @return The Property.
     */
    Property getPropertyByIndex(int pIndex);

    /**
     * Determines a {@link Property} by its name or one of its aliases.
     * @param pNameOrAlias The name or an alias.
     * @return The Property.
     * @see GenericDataObject#getProperty(String)
     */
    Property getPropertyByNameOrAlias(String pNameOrAlias);

    Property getProperty(String pUri, String xsdName, boolean isElement);

    Property findOpenProperty(String pUri, String pXsdName, boolean pIsElement);

    /**
     * This is an internal method, that is part of the unset or remove
     * implementation. The only behavior is, if the property is an open property,
     * it will be removed from the instance properties.
     * @param pProperty The Property.
     * @param pSaveChanges true, if the open properties list should be saved for
     * the ChangeSummary.
     * @see GenericDataObject#internUnset(Property)
     * @see #getInstanceProperties()
     */
    void internUnset(Property pProperty, boolean pSaveChanges);

    /**
     * Returns the concatination of the type properties and the data objects
     * open properties.
     * @return The List of instance propeties.
     * @see DataObject#getInstanceProperties()
     */
    List<Property> getInstanceProperties();

    /**
     * Deletes the data object.
     * @see DataObject#delete()
     */
    void delete();

    /**
     * To support {@link GenericDataObject#toString() with a better performance.
     * @param pBuffer The StringBuilder.
     */
    void asString(StringBuilder pBuffer);

    /**
     * Sets the {@link Sequence} without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pSettings The new List of Property-value-pairs.
     * @see GenericDataObject#setSequenceWithoutCheck(List)
     */
    void setSequenceWithoutCheck(List<Setting> pSettings);

    /**
     * Adds a new value to the {@link Sequence} without any containment or
     * opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see GenericDataObject#addToSequenceWithoutCheck(Property, Object)
     */
    PropValue<?> addToSequenceWithoutCheck(Property pProperty, Object pValue);

    /**
     * Sets the value of a {@link Property} without any containment or opposite
     * checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see GenericDataObject#setPropertyWithoutCheck(Property, Object)
     */
    PropValue<?> setPropertyWithoutCheck(Property pProperty, Object pValue);

    /**
     * Adds a value to a multi-valued {@link Property} without any containment
     * or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced multi-valued properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see GenericDataObject#addToPropertyWithoutCheck(Property, Object)
     */
    PropValue<List<Object>> addToPropertyWithoutCheck(Property pProperty, Object pValue);

    /**
     * Sets the {@link State change state} directly without any checks.
     * @param pState The new change state.
     * @see GenericDataObject#setChangeStateWithoutCheck(State)
     */
    void setChangeStateWithoutCheck(State pState);

    /**
     * Sets the old value of a {@link Property} for the {@link commonj.sdo.ChangeSummary}
     * without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see GenericDataObject#setOldPropertyWithoutCheck(Property, Object)
     */
    void setOldPropertyWithoutCheck(Property pProperty, Object pValue);

    /**
     * Adds an old value to a multi-valued {@link Property} for the
     * {@link commonj.sdo.ChangeSummary} without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced multi-valued properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see GenericDataObject#addToOldPropertyWithoutCheck(Property, Object)
     */
    void addToOldPropertyWithoutCheck(Property pProperty, Object pValue);

    /**
     * Sets the old {@link Sequence} for the {@link commonj.sdo.ChangeSummary}
     * without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pSettings The List of Property-value-pairs.
     * @see GenericDataObject#setSequenceWithoutCheck(List)
     */
    void setOldSequenceWithoutCheck(List<Setting> pSettings);

    /**
     * Adds a value to the old {@link Sequence} for the {@link commonj.sdo.ChangeSummary}
     * without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The value.
     * @see GenericDataObject#addToOldSequenceWithoutCheck(Property, Object)
     */
    void addToOldSequenceWithoutCheck(Property pProperty, Object pValue);

    /**
     * Sets the old container and containment property for the
     * {@link commonj.sdo.ChangeSummary} without maintainig the containment
     * property.
     * @param pContainer The old container.
     * @param pProperty The old containment property.
     * @see GenericDataObject#setOldContainerWithoutCheck(DataObject, Property)
     */
    void setOldContainerWithoutCheck(DataObject pContainer, Property pProperty);

    /**
     * Refines the type to a more concrete type (with more properties).
     * The new type has to extend the old type.
     * Replaces open content properties with properties of the same name of the
     * new type.
     * Precondition of this method is that the GenericDataObject has already the
     * new type.
     * @param pOldType The old type.
     * @param pNewType The more concrete type.
     * @return the corrected DataStrategy
     * @see GenericDataObject#refineType(Type)
     */
    DataStrategy refineType(Type pOldType, Type pNewType);

    public DataStrategy simplifyOpenContent();

    /**
     * Returns true if the read-only mode is enabled.
     * @return true if the read-only mode is enabled.
     * @see GenericDataObject#isReadOnlyMode()
     */
    boolean isReadOnlyMode();

    /**
     * Set it to false to disable the read-only checks.
     * @param pActivated true, to enable and false to disable.
     * @see GenericDataObject#setReadOnlyMode(boolean)
     */
    public void setReadOnlyMode(boolean pActivated);

	List<PropValue> getPropValuesForEachProjection(PropValue<?> value);

    public boolean getXsiNil();

    /**
     * Defines if the DataObject is nil.
     * @param pXsiNil true/false.
     */
    public void setXsiNil(boolean pXsiNil);

    /**
     * Defines if the DataObject is nil. In case of null, for {@link #getXsiNil()}
     * the default behavior is applied.
     * @param pXsiNil true/false or null for unknown.
     */
    public void setXsiNilWithoutCheck(Boolean pXsiNil);

    void stealChangeState(DataStrategy dataStrategy);

    void trimMemory();

}

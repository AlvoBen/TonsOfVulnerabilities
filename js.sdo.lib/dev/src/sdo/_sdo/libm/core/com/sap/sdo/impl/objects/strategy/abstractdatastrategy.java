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
package com.sap.sdo.impl.objects.strategy;

import static com.sap.sdo.impl.objects.GenericDataObject.UNSET;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.OldStateDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.SettingImpl;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.HelperContext;

/**
 * The AbstractDataStrategy provides a template to store the data of the
 * DataObject in memory. The behavior that is common for all data strategy
 * implementations like ChangeSummery is implemented in this abstract class.
 * For a concrete data strategy the abstract template methods have to be
 * implemented.
 * To implement a data strategy it is also necessary implement the abstract
 * template classes {@link com.sap.sdo.impl.objects.strategy.AbstractPropSingleValue}
 * and {@link com.sap.sdo.impl.objects.strategy.AbstractPropMultiValue}. In the
 * non-sequenced case the more conrete classes
 * {@link com.sap.sdo.impl.objects.strategy.AbstractNonSequencedPropSingleValue}
 * and {@link com.sap.sdo.impl.objects.strategy.AbstractNonSequencedPropMultiValue}
 * will fit in most cases to be implemented.
 * @author D042807
 *
 */
public abstract class AbstractDataStrategy implements DataStrategy, Serializable {

    private static final long serialVersionUID = -92679364266290821L;
    private GenericDataObject _gdo;
    protected ChangeState _changeState;
    private boolean _isReadOnlyActivated = true;
    private boolean _xsiNil;
    private boolean _xsiNilInitialized;
    private boolean _initialScope = false;

    public void setDataObject(GenericDataObject pDataObject) {
        _gdo = pDataObject;
    }

    /**
     * Returns the GenericDataObject, the strategy is created for.
     * @return The GenericDataObject.
     */
    public GenericDataObject getDataObject() {
        return _gdo;
    }

    public int indexOfProperty(final Property pProperty, final boolean pCreate) {
        SdoProperty property = (SdoProperty)pProperty;
        Type type = getDataObject().getType();
        int index = property.getIndex(type);
        if (index >= 0) {
            return index;
        }
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            index = openProperties.indexOf(property);
        }
        if (index < 0) {
            if (pCreate) {
                index = createOpenProperty(property);
            } else {
                return -1;
            }
        }
        return typePropertiesSize() + index;
    }

    public HelperContext getHelperContext() {
        return _gdo.getHelperContext();
    }

    int indexOfOpenProperty(Property pProperty) {
        return typePropertiesSize() + getOpenProperties().indexOf(pProperty);
    }

    public Sequence getSequence() {
        return null;
    }

    protected List<Setting> calculateOldSequence() {
        return null;
    }

    public List<Property> getOldChangedProperties() {
        List<Property> changedProperties = getOldChangedNonSequencedProperties();
        if (!changedProperties.isEmpty()) {
            Collections.sort(changedProperties, new PropertyComperator(getOpenProperties(), getOldOpenProperties()));
        }
        return changedProperties;
    }

    public void asString(StringBuilder b) {
        b.append('{');
        List<Property> properties = getDataObject().getInstanceProperties();
        for (Iterator<Property> propIt = properties.iterator(); propIt.hasNext();) {
            Property property = propIt.next();
            b.append(property.getName());
            b.append(':');
            PropValue<?> propValue = getPropValue(property, true);
            if (propValue.isMany()) {
                if (propValue.getValue() instanceof Collection) {
                b.append('(');
                    for (Iterator<Object> valIt= ((Collection<Object>)propValue.getValue()).iterator(); valIt.hasNext();) {
                    Object value = valIt.next();
                    valueAsString(b, property, value);
                    if (valIt.hasNext()) {
                        b.append(", ");
                    }
                }
                b.append(')');
                }
            } else {
                if (!propValue.isSet()) {
                    b.append("[UNSET] default=");
                }
                valueAsString(b, property, propValue.getValue());
            }
            if (propIt.hasNext()) {
                b.append("; ");
            }
        }
        b.append('}');
    }

    public PropValue<List<Object>> addToPropertyWithoutCheck(Property pProperty, Object pValue) {
        if (!pProperty.isMany()) {
            throw new IllegalArgumentException("Only available for many valued properties");
        }
        AbstractNonSequencedPropMultiValue nonSequencedPropMultiValue = (AbstractNonSequencedPropMultiValue)getPropValue(pProperty, true);
        nonSequencedPropMultiValue.addWithoutCheck(pValue);
        return nonSequencedPropMultiValue;
    }

    public void setSequenceWithoutCheck(List<Setting> pSettings) {
        throw new UnsupportedOperationException("Only available for sequenced DataObjects");
    }

    public PropValue<?> addToSequenceWithoutCheck(Property pProperty, Object pValue) {
        throw new UnsupportedOperationException("Not supported for non-sequenced DataObjects");
    }

    public void addToOldSequenceWithoutCheck(Property pProperty, Object pValue) {
        throw new UnsupportedOperationException("Not supported for non-sequenced DataObjects");
    }

    public void setOldSequenceWithoutCheck(List<Setting> pSettings) {
        throw new UnsupportedOperationException("Not supported for non-sequenced DataObjects");
    }

    /**
     * Determines if the Property is read-only in this moment. The result
     * depends on the propety and if the read-only mode is activated.
     * @param pProperty The property.
     * @return True if the property is read-only, false if it can be modified.
     * @see Property#isReadOnly()
     * @see #isReadOnlyMode()
     */
    public boolean isReadOnly(Property pProperty) {
        return _isReadOnlyActivated && pProperty.isReadOnly();
    }

    /**
     * Adds the new property to the open properties.
     * @param pProperty The new open property.
     * @return The index of the new open property.
     * @throws IllegalArgumentException If the property is assigend to a type or
     * the DataObject has already a property with this name.
     */
    int createOpenProperty(Property pProperty) {
        String name = pProperty.getName();
        final Type type = getDataObject().getType();
        if (!type.isOpen()) {
            throw new IllegalArgumentException("Can not create property "
                + name + " because type " + ((SdoType)type).getQName().toStandardSdoFormat() + " is not open");
        }
        checkSaveOpenProperties();
        List<Property> openProperties = getOpenProperties();
        if (openProperties == null) {
            openProperties = new ArrayList<Property>(1);
            setOpenProperties((ArrayList)openProperties);
        }
        int index = openProperties.size();
        openProperties.add(pProperty);
        addOpenProperty(pProperty);
        return index;
    }

    public abstract void addOpenProperty(Property pProperty);

    /**
     * Checks and reactivates a property if it is a deleted open property. An
     * open property will be removed if it was set to unset. If the user has
     * still the property value (the list) in hand, he can reactivate the
     * property by adding values to the list.
     * @param pPropValue The PropValue to check.
     */
    void reactivatePropValue(PropValue<?> pPropValue) {
        Property prop = pPropValue.getProperty();
        if (prop.isOpenContent()) {
            List<Property> openProperties = getOpenProperties();
            if (!openProperties.contains(prop)) {
                openProperties.add(prop);
                reactivateOpenPropValue(pPropValue);
            }
        }
    }

    /**
     * Reactivates an open property.
     * An open property will be removed if it was set to unset. If the user has
     * still the property value (the list) in hand, he can reactivate the
     * property by adding values to the list.
     * @param pPropValue The PropValue to reactivate.
     */
    public abstract void reactivateOpenPropValue(PropValue<?> pPropValue);

    protected void valueAsString(StringBuilder pBuffer, Property pProperty, Object pValue) {
        if (pValue == null) {
            pBuffer.append("null");
        } else if (pValue instanceof DataObjectDecorator) {
            if (pProperty.isContainment()) {
                ((DataObjectDecorator)pValue).getInstance().asString(pBuffer);
            } else {
                pBuffer.append("ref");
            }
        } else if (pValue instanceof String) {
            pBuffer.append('"');
            pBuffer.append((String)pValue);
            pBuffer.append('"');
        } else {
            pBuffer.append(convertValue(pValue, pProperty, String.class));
        }
    }

    //
    // translate ix to Property instance
    public Property getPropertyByIndex(int pIndex) {
        List<Property> typeProperties = getDataObject().getType().getProperties();
        if ((pIndex >= 0) && (pIndex < typeProperties.size())) {
            return typeProperties.get(pIndex);
        }
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            int reducedIx = pIndex - typeProperties.size();
            if ((reducedIx >= 0) && (reducedIx < openProperties.size())) {
                return openProperties.get(reducedIx);
            }
        }
        throw new IndexOutOfBoundsException("No property with index " + pIndex);
    }

    public int getPropertiesSize() {
        int size = typePropertiesSize();
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            size = size + openProperties.size();
        }
        return size;
    }


    // translate nameOrAlias to Property instance
    public Property getPropertyByNameOrAlias(String pNameOrAlias) {
        Property property = getDataObject().getType().getProperty(pNameOrAlias);
        if (property == null) {
            property = getOpenProperty(pNameOrAlias);
        }
        return property;
    }

    public Property getProperty(String pUri, String xsdName, boolean isElement) {
        String uri = pUri;
        if (uri == null) {
            uri = "";
        }
        Property property = ((SdoType)getDataObject().getType()).getPropertyFromXmlName(uri, xsdName, isElement);
        if (property == null) {
            property = getOpenProperty(uri, xsdName, isElement);
        }
        return property;
    }

    /**
     * Lazy creation of the PropValue object. The index of the Property and so
     * the Property must be well known.
     * @param pPropIndex The index of the property.
     * @param pProperty The property, if known, null otherwise.
     * @return The created PropValue.
     */
    public abstract PropValue<?> createPropValue(int pPropIndex, Property pProperty);

    public void internUnset(Property pProperty, boolean pSaveChanges) {
        List<Property> openProperties = getOpenProperties();
        if (getDataObject().getType().isOpen() && openProperties != null)  {
            int index = openProperties.indexOf(pProperty);
            if (index >= 0) {
                if (pSaveChanges) {
                    checkSaveOpenProperties();
                }
                openProperties.remove(index);
                removeOpenPropValue(typePropertiesSize() + index);
            }
        }
    }

    /**
     * Removes an open property if it was set to unset.
     * @param pPropertyIndex The property index.
     */
    public abstract void removeOpenPropValue(int pPropertyIndex);

    /**
     * Finds an open property by its name or alias.
     * @param pName
     * @return The found property or null.
     */
    private Property getOpenProperty(String pName) {
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            String attributePropName = null;
            if (pName.charAt(0) == '@') {
                attributePropName = pName.substring(1);
            }
            for (Property property: openProperties) {
                if (pName.equals(property.getName())) {
                    return property;
                }
                if (attributePropName != null && !((SdoProperty)property).isXmlElement()
                        && attributePropName.equals(property.getName())) {
                    return property;
                }
                if ((property.getAliasNames()!=null) && property.getAliasNames().contains(pName)) {
                    return property;
                }
            }
        }
        return null;
    }

    public Property getOpenProperty(String pUri, String pXsdName, boolean pIsElement) {
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            for (Property property: openProperties) {
                if (isMatch(property, pUri, pXsdName, pIsElement)) {
                    return property;
                }
            }
        }
        return null;
    }

    public Property findOpenProperty(String pUri, String pXsdName, boolean pIsElement) {
        Property result = getOpenProperty(pUri, pXsdName, pIsElement);
        if ((result == null) && (_changeState != null)) {
            List<Property> oldOpenProps = _changeState.getOldOpenProperties();
            if (oldOpenProps != null) {
                for (Property property: oldOpenProps) {
                    if (isMatch(property, pUri, pXsdName, pIsElement)) {
                        return property;
                    }
                }
            }
        }
        return result;
    }

    private boolean isMatch(Property pProperty, String pUri, String pXsdName, boolean pIsElement) {
        SdoProperty sdoProp = (SdoProperty)pProperty;
        if (pXsdName.equals(sdoProp.getXmlName()) && pIsElement == sdoProp.isXmlElement()) {
            String uri = sdoProp.getUri();
            if (uri == null) {
                uri = "";
            }
            if (pUri.equals(uri)) {
                return true;
            }
        }
        return false;
    }

    public List<Property> getInstanceProperties() {
        Type type = _gdo.getType();
        if (type.isOpen()) {
            return new InstancePropList();
        }
        return type.getProperties();
    }

    public List<Property> getOldInstanceProperties() {
        List<Property> properties = new ArrayList<Property>(getDataObject().getType().getProperties());
        if ((_changeState != null) && (_changeState.getOldOpenProperties() != null)) {
            properties.addAll(_changeState.getOldOpenProperties());
        } else {
            List<Property> openProperties = getOpenProperties();
            if (openProperties != null) {
                properties.addAll(openProperties);
            }
        }
        return properties;
    }

    public void delete() {
        setChangeState(State.DELETED, true);
        List<PropValue<?>> propValues = getPropValues(false);
        for (PropValue<?> propValue: propValues) {
            if (!propValue.getProperty().isContainment()) {
                //don't unset read-only property             remove from container later
                if (!propValue.getProperty().isReadOnly() && !propValue.getProperty().isOppositeContainment()) {
                    propValue.unset();
                }
            } else {
                if (propValue.isMany()) {
                    List<DataObjectDecorator> list = new ArrayList<DataObjectDecorator>((List<DataObjectDecorator>)propValue.getValue());
                    for (DataObjectDecorator dataObject: list) {
                        dataObject.getInstance().delete();
                    }
                } else {
                    DataObjectDecorator dataObject = (DataObjectDecorator)propValue.getValue();
                    if (dataObject != null) {
                        dataObject.getInstance().delete();
                    }
                }
            }
        }
        //don't detach read-only property
        Property containmentProp = getDataObject().getContainmentProperty();
        if (containmentProp == null || !containmentProp.isReadOnly()) {
            getDataObject().detach();
        }
    }

    /**
     * Returns the number of non-open properties.
     * @return The number of properties of the type.
     */
    public int typePropertiesSize() {
        return getDataObject().getType().getProperties().size();
    }

    public DataObjectDecorator getOldStateFacade() {
        if (_changeState == null) {
            return getDataObject().getFacade();
        }
        return _changeState.getOldStateFacade();
    }

    /**
     * Returns the changed properties tha are not in a {@link Sequence}.
     * The changed sequenced properties will be managed by the old
     * {@link Sequence}.
     * @return The list of the old changed nonsequenced properties.
     */
    protected List<Property> getOldChangedNonSequencedProperties() {
        if ((_changeState != null) && (_changeState.getOldNonSequencedPropertyValues() != null)) {
            return new ArrayList<Property>(_changeState.getOldNonSequencedPropertyValues().keySet());
        }
        return Collections.emptyList();
    }

    /**
     * Returns the list of open properties at the time, wenn logging has begun.
     * Since the logging began the list could have changed by adding or unsetting
     * open properties.
     * @return The old list of open Properties, never null.
     */
    protected List<Property> getOldOpenProperties() {
        List<Property> oldOpenProperties = null;
        if (_changeState != null) {
            oldOpenProperties = _changeState.getOldOpenProperties();
        }
        if (oldOpenProperties == null) {
            oldOpenProperties = Collections.emptyList();
        }
        return oldOpenProperties;
    }

    /**
     * The method is called when an open property will be added or removed.
     * Sets the {@link #setChangeState(State, boolean) change state} to
     * {@link State#MODIFIED} and saves the current list of the open propetries
     * if the {@link commonj.sdo.ChangeSummary} is logging.
     */
    void checkSaveOpenProperties() {
        //set change state to modified if logging is on
        setChangeState(State.MODIFIED, true);
        if ((_changeState != null) && (_changeState.getOldOpenProperties() == null)) {
            // copies the open properties if logging is on
            _changeState.initOldOpenProperties();
        }
    }

    public boolean isInitialScope() {
        return _initialScope;
    }

    public void setInitalScope(boolean pInitialScope) {
        _initialScope = pInitialScope;
    }

    public State getChangeState() {
        if (_changeState == null) {
            return State.UNCHANGED;
        }
        return _changeState.getState();
    }

    public void setChangeState(State pState, boolean pCheckLogging) {
        if (getChangeState() == pState) {
            return;
        }
        if (pCheckLogging && !isInitialScope()) {
            return;
        }
        setChangeStateWithoutCheck(pState);
        if ((getSequence() != null) && (_changeState._oldSequence == null) && (pState != State.UNCHANGED)) {
            _changeState._oldSequence = new ReadOnlySequence(calculateOldSequence());
        }

    }

    public void setChangeStateWithoutCheck(State pState) {
        if (_changeState == null) {
            _changeState = new ChangeState();
        }
        if (_changeState.getState()!=State.DELETED || pState!=State.MODIFIED) {
            _changeState.setState(pState);
        }
    }

    public PropValue<?> getOldContainmentPropValue() {
        if (_changeState == null) {
            return getDataObject().getContainmentPropValue();
        }
        return _changeState.getOldContainmentPropValue();
    }

    public Sequence getOldSequence() {
        Sequence oldSequence = getOldSavedSequence();
        if (oldSequence != null) {
            return oldSequence;
        }
        oldSequence = getSequence();
        if (oldSequence != null) {
            oldSequence = new ReadOnlySequence(oldSequence);
        }
        return oldSequence;
    }

    /**
     * Returns the {@link Sequence} with the values, wenn logging has begun.
     * If the {@link commonj.sdo.ChangeSummary} is not logging or the Sequence
     * was not changed, the return value is null.
     * The returned Sequence is read-only.
     * @return The old Sequence or null.
     */
    public ReadOnlySequence getOldSavedSequence() {
        if ((_changeState == null) || (_changeState.getOldSequence() == null)) {
            return null;
        }
        return _changeState.getOldSequence();
    }

    public void resetChangeSummary() {
        _changeState = null;
    }

    public PropValue<?> setPropertyWithoutCheck(final Property pProperty, final Object pValue) {
        PropValue<?> propValue = getPropValue(pProperty, true);
        if (propValue instanceof AbstractNonSequencedPropSingleValue) {
            ((AbstractNonSequencedPropSingleValue)propValue).setWithoutCheck(pValue);
        } else if (propValue instanceof AbstractNonSequencedPropMultiValue) {
            List<Object> value;
            if (pValue == UNSET) {
                value = Collections.emptyList();
            } else {
                value = (List<Object>)pValue;
            }
            ((AbstractNonSequencedPropMultiValue)propValue).setWithoutCheck(value);
        } else {
            throw new UnsupportedOperationException("Only available for non-sequenced properties, but was "
                + propValue + ' ' + pProperty.getName());
        }
        return propValue;
    }

    /**
     * Returns the list of the current PropValues. Because of the lazy creation
     * of the PropValue it is possible to skip the not already created PropValues
     * by setting the parameter pCreate to false.
     * @param pCreate true to return all PropValues, false to skip not already created.
     * @return The list of PropValues.
     */
    public List<PropValue<?>> getPropValues(boolean pCreate) {
        int size = getPropertiesSize();
        List<PropValue<?>> propValues = new ArrayList<PropValue<?>>(size);
        for (int i = 0; i < size; i++) {
            PropValue<?> propValue = getPropValue(i, null, pCreate);
            if (propValue != null) {
                propValues.add(propValue);
            }
        }
        return propValues;
    }

    public PropValue<?> getPropValue(Property pProperty, boolean pCreate) {
        int propIndex = indexOfProperty(pProperty, pCreate);
        if (propIndex < 0) {
            if (pCreate) {
                throw new IllegalArgumentException("Can not find property "
                    + pProperty.getName());
            } else {
                return null;
            }
        }
        return getPropValue(propIndex, pProperty, pCreate);
    }


    public void setOldContainerWithoutCheck(DataObject pContainer, Property pProperty) {
        PropValue<?> containerPropValue = null;
        if ((pContainer != null) && (pProperty != null)) {
            GenericDataObject dataObject = ((DataObjectDecorator)pContainer).getInstance();
            containerPropValue = dataObject.getPropValue(pProperty, true, true);
            getOrCreateChangeState().setOldContainerWithoutCheck(containerPropValue);
            if (!containerPropValue.isSet() && ((SdoProperty)pProperty).isOpenContent()) {
                dataObject.internUnset(pProperty);
            }
        }
    }

    public void setOldPropertyWithoutCheck(Property pProperty, Object pValue) {
        SdoProperty property = (SdoProperty)pProperty;
        if ((getSequence() != null) && property.isXmlElement()){
            throw new IllegalArgumentException("Property is sequenced: " + pProperty.getName());
        }
        getOrCreateChangeState().setOldPropertyWithoutCheck(pProperty, pValue);
    }

    public void addToOldPropertyWithoutCheck(Property pProperty, Object pValue) {
        if (!pProperty.isMany()) {
            throw new IllegalArgumentException("Property is not multi-valued: " + pProperty.getName());
        }
        if (getSequence() != null) {
            throw new IllegalArgumentException("Property is sequenced: " + pProperty.getName());
        }
        getOrCreateChangeState().addToOldPropertyWithoutCheck(pProperty, pValue);
    }

    /**
     * Returns the {@link ChangeState} and initializes it before, if it is
     * necessary.
     * @return The ChangeState.
     */
    protected ChangeState getOrCreateChangeState() {
         if(_changeState == null) {
             _changeState = new ChangeState();
         }
         return _changeState;
    }

    public boolean isReadOnlyMode() {
        return _isReadOnlyActivated;
    }

    public void setReadOnlyMode(boolean pActivated) {
        _isReadOnlyActivated = pActivated;
    }

    public void saveValue(Property pProperty, Object pValue) {
        setChangeState(State.MODIFIED, false);
        _changeState.saveValue(pProperty, pValue);
    }

    public Setting getOldValue(Property pProperty) {
        if (_changeState == null) {
            return null;
        }
        PropValue<?> currentPropValue;
        try {
            currentPropValue =
                getPropValue(pProperty, !pProperty.isOpenContent());
        } catch (IllegalArgumentException e) { //$JL-EXC$
            currentPropValue = null;
        }
        if ((currentPropValue != null) && !currentPropValue.isModified()) {
            return null;
        }
        Object oldValue = getOldSavedValue(pProperty);
        oldValue = OldStateDataObject.wrapValue(pProperty, oldValue);
        return new SettingImpl(pProperty, oldValue);
    }

    public Object getOldSavedValue(Property pProperty) {
        if (_changeState == null) {
            return null;
        }
        return _changeState.getOldValue(pProperty);
    }

    public static <C,S> C convertValue(S value, Property pProperty, Class<C> pClass) {
        if (value == null) {
            if (List.class == pClass) {
                Type type = pProperty.getType();
                if (type.getInstanceClass() == List.class) {
                    return (C)Collections.emptyList();
                } else {
                    throw new ClassCastException("Single value property does not support access as List");
                }
            } else {
                return null;
            }
        }
        // TODO increased performance for JDK 1.5.0.7 but last check should be enough
        if ((value.getClass() == pClass) || (pClass == Object.class) || (pClass.isInstance(value))) {
            return (C)value;
        }
        SdoType<S> type = (SdoType<S>)pProperty.getType();
        return type.convertToJavaClass(value, pClass);
    }

    public static Object convert(SdoProperty pProperty, Object pValue) {
        Object value = ((SdoType)pProperty.getType()).convertFromJavaClass(pValue);
        return pProperty.getCachedValue(value);
    }

    //
    // helper
    //
    protected class InstancePropList extends AbstractList<Property> implements RandomAccess {

        @Override
        public Property get(int index) {
            return getPropertyByIndex(index);
        }

        @Override
        public int size() {
            return getPropertiesSize();
        }

    }

    /**
     * The ChangeState covers the old data for the {@link commonj.sdo.ChangeSummary}.
     * @author D042807
     *
     */
    class ChangeState implements Serializable {

        private static final long serialVersionUID = 8892656595398998994L;

        private List<Property> _oldOpenProperties;
        private Map<Property, Object> _oldNonSequencedPropertyValues;
        private ReadOnlySequence _oldSequence;
        private PropValue<?> _oldContainmentPropValue;
        private State _state = State.UNCHANGED;

        private DataObjectDecorator _oldStateFacade;

        public ChangeState() {
            _oldContainmentPropValue = getDataObject().getContainmentPropValue();
        }

        public void initOldOpenProperties() {
            if (_oldOpenProperties != null) {
                return;
            }
            List<Property> openProperties = getOpenProperties();
            if (openProperties != null) {
                _oldOpenProperties = new ArrayList<Property>(openProperties);
            } else {
                _oldOpenProperties = new ArrayList<Property>();
            }
        }

        public PropValue<?> getOldContainmentPropValue() {
            return _oldContainmentPropValue;
        }

        public State getState() {
            return _state;
        }

        public void setState(State pState) {
            if (pState == State.MODIFIED) {
                if ((_state == State.CREATED) || (_state == State.DELETED)) {
                    return;
                }
            }
            // TODO check if handling is correct - create after delete results in modified
            if (pState == State.CREATED && _state == State.DELETED) {
                _state = State.MODIFIED;
            } else {
                _state = pState;
            }
        }

        public ReadOnlySequence getOldSequence() {
            return _oldSequence;
        }

         public void saveValue(Property pProperty, Object pValue) {
            if (_oldNonSequencedPropertyValues == null) {
                _oldNonSequencedPropertyValues = new HashMap<Property, Object>();
            }
            if (pValue == null) {
                throw new NullPointerException("Value must not be null");
            }
            _oldNonSequencedPropertyValues.put(pProperty, pValue);
        }

        public Object getOldValue(Property pProperty) {
            if (_oldNonSequencedPropertyValues == null) {
                return null;
            }
            return _oldNonSequencedPropertyValues.get(pProperty);
        }

        public void setOldContainerWithoutCheck(PropValue<?> pPropValue) {
            _oldContainmentPropValue = pPropValue;
        }

        public void addToOldSequenceWithoutCheck(Property pProperty, Object pValue) {
            if (_oldSequence == null) {
                _oldSequence = new ReadOnlySequence(new ArrayList<Setting>());
            }
            Object value = pValue;
            if ((pProperty != null) && (UNSET != pValue)) {
                value = ((SdoType)pProperty.getType()).convertFromJavaClass(pValue);
            }
            _oldSequence.getSequenceAsList().add(new SettingImpl(pProperty,value));
        }

        public void setOldSequenceWithoutCheck(List<Setting> pSettings) {
            _oldSequence = new ReadOnlySequence(new ArrayList<Setting>());
            for(Setting setting: pSettings) {
                addToOldSequenceWithoutCheck(setting.getProperty(), setting.getValue());
            }
        }

        public void setOldPropertyWithoutCheck(Property pProperty, Object pValue) {
            boolean deleteOpenProperty = (UNSET == pValue);
            checkOldProperty(pProperty, deleteOpenProperty);
            if (deleteOpenProperty) {
                _oldNonSequencedPropertyValues.put(pProperty, pValue);
            } else if (!pProperty.isMany()){
                Object value = ((SdoType)pProperty.getType()).convertFromJavaClass(pValue);
                _oldNonSequencedPropertyValues.put(pProperty, value);
            } else {
                _oldNonSequencedPropertyValues.put(pProperty, new ArrayList<Object>());
                List values = (List)pValue;
                for (Object value: values) {
                    addToOldPropertyWithoutCheck(pProperty, value);
                }
            }
        }

        public void addToOldPropertyWithoutCheck(Property pProperty, Object pValue) {
            checkOldProperty(pProperty, false);
            List<Object> multiValue = (List<Object>)_oldNonSequencedPropertyValues.get(pProperty);
            if (multiValue == null) {
                multiValue = new ArrayList<Object>();
                _oldNonSequencedPropertyValues.put(pProperty, multiValue);
            }
            Object value = ((SdoType)pProperty.getType()).convertFromJavaClass(pValue);
            multiValue.add(value);
        }

        private void checkOldProperty(Property pProperty, boolean pDeleteOpenProperty) {
            if (_oldNonSequencedPropertyValues == null) {
                _oldNonSequencedPropertyValues = new HashMap<Property, Object>();
            }
            if (getDataObject().getType().isOpen()) {
                initOldOpenProperties();
                if (pDeleteOpenProperty) {
                    _oldOpenProperties.remove(pProperty);
                } else if (!_oldOpenProperties.contains(pProperty)) {
                    _oldOpenProperties.add(pProperty);
                }
            }
        }

        public Map<Property, Object> getOldNonSequencedPropertyValues() {
            return _oldNonSequencedPropertyValues;
        }

        public List<Property> getOldOpenProperties() {
            return _oldOpenProperties;
        }

        public DataObjectDecorator getOldStateFacade() {
            if (_oldStateFacade == null) {
                _oldStateFacade = OldStateDataObject.getOldStateFacade(getDataObject());
            }
            return _oldStateFacade;
        }

    }

    public DataStrategy simplifyOpenContent() {
        return this;
    }

    /**
     * Comperator for properties. The sorting of open properties is also paid
     * attention.
     * The sorting of the {@link #_oldOpenProperties} has a higher priority
     * than the sorting of the {@link #_newOpenProperties}.
     * It is assumed that the compared properties are valid properties at the
     * time when logging began or in the current state.
     * @author D042807
     */
    static class PropertyComperator implements Comparator<Property> {

        private final List<Property> _newOpenProperties;
        private final List<Property> _oldOpenProperties;

        /**
         * Creates a PropertyComperator.
         * @param pNewOpenProperties List of the current open properties (null save).
         * @param pOldOpenProperties List of the old open properties (null save).
         */
        public PropertyComperator(List<Property> pNewOpenProperties, List<Property> pOldOpenProperties) {
            if (pNewOpenProperties == null) {
                _newOpenProperties = Collections.emptyList();
            } else {
                _newOpenProperties = pNewOpenProperties;
            }
            if (pOldOpenProperties == null) {
                _oldOpenProperties = Collections.emptyList();
            } else {
                _oldOpenProperties = pOldOpenProperties;
            }
        }

        public int compare(Property pProperty1, Property pProperty2) {
            if (pProperty1 == pProperty2) {
                return 0;
            }
            int index1 = ((SdoProperty)pProperty1).getIndex();
            int index2 = ((SdoProperty)pProperty2).getIndex();
            if (index1 >= 0) {
                if (index2 >= 0) {
                    return index1 - index2;
                } else {
                    return -1;
                }

            } else {
                if (index2 >= 0) {
                    return 1;
                } else {
                    return compareOpenProperties(pProperty1, pProperty2);
                }
            }
        }

        /**
         * The sorting of the {@link #_oldOpenProperties} has a higher priority
         * than the sorting of the {@link #_newOpenProperties}. The property
         * that is found first in the {@link #_oldOpenProperties} or
         * {@link #_newOpenProperties} is the lower one.
         * @param pProperty1
         * @param pProperty2
         * @return
         */
        private int compareOpenProperties(Property pProperty1, Property pProperty2) {
            for (Property property: _oldOpenProperties) {
                if (property == pProperty1) {
                    return -1;
                }
                if (property == pProperty2) {
                    return 1;
                }
            }
            for (Property property: _newOpenProperties) {
                if (property == pProperty1) {
                    return -1;
                }
                if (property == pProperty2) {
                    return 1;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    public List<PropValue> getPropValuesForEachProjection(PropValue value) {
        return Collections.singletonList(value);
    }

    protected abstract void setOpenProperties(ArrayList<Property> pOpenProperties);
    public abstract List<Property> getOpenProperties();

    public boolean getXsiNil() {
        if (!_xsiNilInitialized) {
            GenericDataObject dataObject = getDataObject();
            SdoProperty valueProp = TypeHelperImpl.getSimpleContentProperty(dataObject);
            if (valueProp != null) {
                setXsiNilWithoutCheck(dataObject.get(valueProp) == null);
            } else {
                boolean typeHasElements = false;
                int size = getPropertiesSize();
                for (int i = 0; i < size; i++) {
                    SdoProperty property = (SdoProperty)getPropertyByIndex(i);
                    if (property.isXmlElement()) {
                        typeHasElements = true;
                        if (property.isOpenContent() || dataObject.isSet(property)) {
                            setXsiNilWithoutCheck(false);
                            return false;
                        }
                    }
                }
                setXsiNilWithoutCheck(typeHasElements);
            }
        }
        return _xsiNil;
    }

    public void setXsiNil(boolean pXsiNil) {
        if (pXsiNil && !Boolean.TRUE.equals(getXsiNilIntern())) {
            GenericDataObject dataObject = getDataObject();
            SdoProperty valueProp = TypeHelperImpl.getSimpleContentProperty(dataObject);
            if (valueProp != null) {
                dataObject.set(valueProp, null);
            } else {
                List<Property> instanceProperties = new ArrayList<Property>(getInstanceProperties());
                for (Property property: instanceProperties) {
                    if (((SdoProperty)property).isXmlElement()) {
                        dataObject.unset(property);
                    }
                }
            }
        }
        setXsiNilWithoutCheck(pXsiNil);
    }

    public Boolean getXsiNilIntern() {
        return _xsiNilInitialized?_xsiNil:null;
    }

    public void setXsiNilWithoutCheck(Boolean pXsiNil) {
        if (pXsiNil == null) {
            _xsiNilInitialized = false;
        } else {
            _xsiNil = pXsiNil;
            _xsiNilInitialized = true;
        }
    }

    public void stealChangeState(DataStrategy pDataStrategy) {
        if (pDataStrategy instanceof AbstractDataStrategy) {
            State state = getChangeState();
            _changeState = ((AbstractDataStrategy)pDataStrategy)._changeState;
            if (state != State.UNCHANGED) {
                setChangeStateWithoutCheck(state);
            }
        }
    }

    public void trimMemory() {
    }

}

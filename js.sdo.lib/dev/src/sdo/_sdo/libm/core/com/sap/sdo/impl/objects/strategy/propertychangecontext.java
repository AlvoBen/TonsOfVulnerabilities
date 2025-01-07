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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public class PropertyChangeContext {

    private final PropValue<?> _propValue;
    private final boolean _isCalledByOpposite;
    private boolean _isOldValueSaved;

    public PropertyChangeContext(PropValue pPropValue, boolean pIsCalledByOpposite) {
        this(pPropValue, pIsCalledByOpposite, false);
    }

    public PropertyChangeContext(PropValue pPropValue, boolean pIsCalledByOpposite, boolean pIsOldValueSaved) {
        _propValue = pPropValue;
        _isCalledByOpposite = pIsCalledByOpposite;
        _isOldValueSaved = pIsOldValueSaved;
        if (!pIsCalledByOpposite){
            if (pPropValue.isReadOnly()) {
                throw new UnsupportedOperationException("Property " +
                    _propValue.getProperty().getName() + " is read-only");
            }
            // if this PropValue is not in read-only mode, the opposite
            // is per definition also not in read-only mode, so no further
            // checks are necessary.
        }
    }

    /**
     *
     * @param pOldValue
     * @param pNewValue
     * @param pPropValue
     * @return The new normalized value.
     */
    public Object replaceAndNormalizeValue(Object pOldValue, Object pNewValue) {
        if ((pOldValue == pNewValue) || ((pOldValue != null) && pOldValue.equals(pNewValue))){
            return pOldValue;
        }
        checkSaveOldValue();
        Object newReturnValue;
        SdoProperty property = _propValue.getProperty();
        newReturnValue = pNewValue;
        if (UNSET == newReturnValue) {
            newReturnValue = property.getDefault();
        }

        _propValue.getDataObject().setXsiNilWithoutCheck(null);
        if (newReturnValue instanceof DataObjectDecorator) {
            GenericDataObject newDataObject = ((DataObjectDecorator)newReturnValue).getInstance();

            HelperContext ctx = _propValue.getDataObject().getHelperContext();
            if (ctx != null) {
            	newDataObject = ((DataObjectDecorator)newDataObject.project(ctx)).getInstance();
            }
            if (!property.isContainment() &&  !property.isOppositeContainment()
                    && _propValue.getDataObject().getDataStrategy().isInitialScope()) {
                newDataObject.setChangeStateWithoutCheck(State.CREATED);
            }
            newReturnValue = newDataObject;
    		for (PropValue projectionPropValue: _propValue.getDataObject().getDataStrategy().getPropValuesForEachProjection(_propValue)) {
    			if (projectionPropValue.getProperty().isContainment()) {
					final SapDataFactory dataFactory = (SapDataFactory)projectionPropValue.getDataObject().getHelperContext().getDataFactory();
                    DataObjectDecorator projectedDataObject = (DataObjectDecorator)dataFactory.project(newDataObject);
                    PropValue<?> containmentPropValue = projectedDataObject.getInstance().getContainmentPropValue();
                    if (projectionPropValue == containmentPropValue) {
                        throw new IllegalArgumentException("DataObject contains already the new value: property: "
                            + property.getName() + " new value: " + newDataObject);
                    }
                    boolean containerIsOpposite = (containmentPropValue != null) && (containmentPropValue.getProperty() == projectionPropValue.getProperty().getOpposite());
    				projectedDataObject.getInstance().internAttach(projectionPropValue, containerIsOpposite);
    			}
    		}
            if (!_isCalledByOpposite) {
                PropValue<?> oppositePropValue = getOppositePropValue(property, newDataObject);
                if (oppositePropValue != null) {
                    if (_propValue == oppositePropValue) {
                        throw new IllegalArgumentException("Opposite property contains already the new value: property: "
                            + property.getName() + " new value: " + newDataObject);
                    }
                    oppositePropValue.internAttachOpposite(_propValue.getDataObject());
                }
            }
        }
        if (pOldValue instanceof DataObjectDecorator) {
            GenericDataObject oldDataObject = ((DataObjectDecorator)pOldValue).getInstance();
            if (property.isContainment()) {
                oldDataObject.internDetach();
            }
            if (!_isCalledByOpposite) {
                PropValue<?> oppositePropValue = getOppositePropValue(property, oldDataObject);
                if (oppositePropValue != null) {
                    oppositePropValue.internDetachOpposite(_propValue.getDataObject());
                }
            }
        }
        return newReturnValue;
    }

    public void checkSaveOldValue() {
        if (!_isOldValueSaved) {
            _propValue.checkSaveOldValue();
            clearIndexMaps();
            _isOldValueSaved = true;
        }
    }

    /**
     * Invalidates the index maps that was built by
     * {@link AbstractPropMultiValue#getIndexByPropertyValue(String, Object)}.
     * It deletes the connected index map in the containment property of the
     * DataObject and also the index map in the DataObjects that can be found by
     * opposite properties (because of performance reasons only by single value
     * opposite properties that have a many valued property at the other side).
     */
    private void clearIndexMaps() {
        final SdoProperty property = _propValue.getProperty();
        if (_propValue.isMany()) {
            property.removeIndexMaps(_propValue.getDataObject());
        } else if (!property.isOpenContent() && property.getType().isDataType()) {
            // invalidate the indexMaps
            final GenericDataObject dataObject = _propValue.getDataObject();
            PropValue containmentPropValue = dataObject.getContainmentPropValue();
            if (containmentPropValue != null) {
                invalidateIndexMap(property, containmentPropValue.getDataObject(), containmentPropValue.getProperty());
            }
            SdoType<?> type = (SdoType)dataObject.getType();
            List<SdoProperty> oppositeProps = type.getSingleOppositeProperties();
            for (int i = 0; i < oppositeProps.size(); i++) {
                SdoProperty oppositeProperty = oppositeProps.get(i);
                DataObject oppositeDo = (DataObject)dataObject.get(oppositeProperty);
                if (oppositeDo != null) {
                    SdoProperty propToMyDataObject = (SdoProperty)oppositeProperty.getOpposite();
                    invalidateIndexMap(property, oppositeDo, propToMyDataObject);
                }
            }
        }
    }

    private void invalidateIndexMap(final SdoProperty pProperty, DataObject pOtherDataObject, SdoProperty pPropToMyDataObject) {
        Map<Object, Integer>[] indexMaps = pPropToMyDataObject.getIndexMaps(pOtherDataObject);
        if (indexMaps != null) {
            indexMaps[pProperty.getIndex(pPropToMyDataObject.getType())] = null;
        }
    }

    private PropValue<?> getOppositePropValue(Property pProperty, DataObjectDecorator pDataObject) {
        Property oppositeProperty = pProperty.getOpposite();
        if (oppositeProperty == null) {
            return null;
        }
        GenericDataObject dataObject = pDataObject.getInstance();
        return dataObject.getPropValue(oppositeProperty, true, true);
    }

    public boolean isCalledByOpposite() {
        return _isCalledByOpposite;
    }

    public boolean isOldValueSaved() {
        return _isOldValueSaved;
    }

    public PropValue<?> getPropValue() {
        return _propValue;
    }

    public List<Object> checkAndNormalizeValues(Collection<? extends Object> pValues) {
        Property property = getPropValue().getProperty();
        List<Object> normList;
        if (property.getType().isDataType()) {
            normList = new ArrayList<Object>(pValues.size());
            for (Object value: pValues) {
                normList.add(checkAndNormalizeDataTypeObject(value));
            }
        } else {
            normList = checkAndNormalizeDataObjects(pValues);
        }
        return normList;
    }

    public Object checkAndNormalizeValue(Object pValue) {
        Property property = getPropValue().getProperty();
        if ((pValue == null) && property.isMany() && !property.isNullable()) {
            throw new IllegalArgumentException("Value of many-valued property must not be null");
        }
        Object normValue;
        if (property.getType().isDataType()) {
            normValue = checkAndNormalizeDataTypeObject(pValue);
        } else if (pValue instanceof DataObjectDecorator){
            normValue = checkAndNormalizeDataObject((DataObjectDecorator)pValue);
        } else {
            normValue = convertObject(pValue);
        }
        return normValue;
    }

    private List checkAndNormalizeDataObjects(Collection pValues) {
        List<DataObject> normList = new ArrayList<DataObject>(pValues.size());
        for (Object dataObject: pValues) {
            if (dataObject instanceof DataObjectDecorator) {
                GenericDataObject normDataObject = ((DataObjectDecorator)dataObject).getInstance();
                normList.add(normDataObject);
            } else {
                if (dataObject == null) {
                    throw new IllegalArgumentException("Value of many-valued property must not be null");
                }
                normList.add((DataObject)dataObject);
            }
        }
        Property property = getPropValue().getProperty();
        if (property.isContainment()) {
            DataObject parent = getPropValue().getDataObject();
            Set<DataObject> parents = new HashSet<DataObject>();
            while (parent != null) {
                parents.add(parent);
                parent = parent.getContainer();
            }
            for (DataObject value: normList) {
                if (parents.contains(value)) {
                    throw new IllegalArgumentException("Containment violation"); // TODO error message
                }
            }
        } else {
            Property opposite = property.getOpposite();
            if ((opposite != null) && opposite.isContainment()) {
                DataObject child = getPropValue().getDataObject();
                for (DataObject parent: normList) {
                    checkContainmentViolation(parent, child);
                }
            }
        }

        return normList;
    }

    private DataObject checkAndNormalizeDataObject(DataObjectDecorator pValue) {
        GenericDataObject normDataObject = pValue.getInstance();

        PropValue<?> propValue = getPropValue();
        Property property = propValue.getProperty();
        HelperContext ctx = propValue.getDataObject().getHelperContext();
        normDataObject = ((DataObjectDecorator)normDataObject.project(ctx)).getInstance();
        if (!isCalledByOpposite()) {
            if (property.isContainment()) {
                DataObject parent = propValue.getDataObject();
                checkContainmentViolation(parent, normDataObject);
            } else {
                Property opposite = property.getOpposite();
                if ((opposite != null) && opposite.isContainment()) {
                    DataObject child = propValue.getDataObject();
                    checkContainmentViolation(normDataObject, child);
                }
            }
        }
        return normDataObject;
    }

    private void checkContainmentViolation(DataObject pParent, DataObject pChild) {
        DataObject parent = pParent;
        while (parent != null) {
            if (parent.equals(pChild)) {
                throw new IllegalArgumentException("Containment violation"); // TODO error message
            }
            parent = parent.getContainer();
        }
    }

    private Object checkAndNormalizeDataTypeObject(Object pValue) {
        Object normObject = pValue;
        if (normObject == null) {
            if (getPropValue().getProperty().getType().getInstanceClass().isPrimitive()) {
                throw new IllegalArgumentException("Property " +
                    getPropValue().getProperty().getName() + " is not nullable");
            }
        } else {
            normObject = convertObject(normObject);
        }
        return normObject;
    }

    private Object convertObject(Object pValue) {
        return AbstractDataStrategy.convert(_propValue.getProperty(), pValue);
    }

}
